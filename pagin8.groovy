#!/usr/bin/env groovy

import com.petebevin.markdown.MarkdownProcessor
import java.util.Date
import java.text.SimpleDateFormat

@Grab('com.madgag:markdownj-core:0.4.1')

class Pagin8{
   def static config = new ConfigSlurper().parse(new File('config.groovy').toURL())
   def static markDown = new MarkdownProcessor() // http://markdownj.org/quickstart.html
   def static currentDate = (new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss")).format(new Date())

   def blogEntries = []

   def createSiteDirectory(){
      println "creating the site directory"
      def d1 = new File(config.dir.site)
      d1.mkdir()
   }

   /* has side effect of creating the directories if they don't exist */
   def createDestinationPath(String fromPath){
      println ("*** in: $fromPath")
      def inputDirSize = config.dir.input.size() 
      println("*** inputDirSize: $inputDirSize")

      def pathWithoutInputDir = ""
      if(fromPath != config.dir.input){
         pathWithoutInputDir = fromPath[inputDirSize..-1]
      }
      println("pathWithoutInputDir : $pathWithoutInputDir")

      def out = config.dir.site + pathWithoutInputDir

      (new File(out)).mkdirs()

      println ("*** out: $out")
      out
   }

   def processInput(File currentDirectory, String parentPath){
      println("currentDirectory: $currentDirectory")
      def m = new MarkdownProcessor(); 
      currentDirectory.eachFile{ currentFile ->

         //def currentFileName = currentFile.name
         def dotLoc = ((java.lang.String)currentFile.name).lastIndexOf('.')
         def currentFileName = currentFile.name[0..dotLoc - 1]

         if(currentFile.name.endsWith(".md")){ 
            def newFileName = createDestinationPath(currentDirectory.getPath()) + "/" + currentFile.name[0..-4] + ".html" 
            println("\t-MARKDOWN: $currentFile --> $newFileName")
            def newFile = new File(newFileName)
            indexBlogEntry(newFile)

            // first process the md
            def markdownLines = []
            currentFile.readLines().each{ currentLine ->
               markdownLines.add(processLine(currentLine, currentFileName))
            }

            newFile << processLine((new File(config.markdownHeader)).readLines().join('\n'), currentFileName)
            newFile << m.markdown(markdownLines.join('\n'))
            newFile << processLine((new File(config.markdownFooter)).readLines().join('\n'), currentFileName)
         }
         else if(currentFile.name.endsWith(".html") || currentFile.name.endsWith(".css")){
            println "\t-RAW     : $currentFile.name"

            def newFileName = createDestinationPath(currentDirectory.getPath()) + "/" + currentFile.name
            def newFile = new File(newFileName)
            indexBlogEntry(newFile) // 

            newFile.withWriter{ destinationFile ->
               currentFile.eachLine{ currLine ->
                  destinationFile.writeLine(processLine(currLine, currentFileName))
               }
            }
         }
         else if(currentFile.isDirectory()){
            println("\t-DIR     : $currentFile")
            processInput(currentFile, currentFile.name + "/")
         }
         else{
            // what else?
            println "\t-DUNNO   : $currentFile.name"
         }
      }
   }

   /** the blog entry file should have some meta-data */
   def Boolean indexBlogEntry(File blogEntryFile){
      def blogDestinationPath = config.dir.site + "/" + config.dir.blog
      def path = blogEntryFile.getPath()
      if(path[0..blogDestinationPath.size() - 1] == blogDestinationPath){
         def siteDirSize = config.dir.site.size()
         def pathWithoutSiteDir = path[siteDirSize..-1]

         def sections = pathWithoutSiteDir.split("/")
         def blogEntryMap = [
            entryPath: pathWithoutSiteDir,
            year: sections[2],
            month: sections[3],
            date: sections[4]]

         // turn the file name into a title
         java.lang.String niceName = sections[5].replaceAll('_', ' ')
         blogEntryMap.put('title', niceName[0..niceName.lastIndexOf('.') - 1])

         blogEntries.add(blogEntryMap)
      }
   }

   def createBlogIndex(){
      def newFile = new File(config.dir.site + "/" + config.dir.blog + "/index.html")
      def blogIndex= new File(config.blogIndex)

      blogIndex.eachLine{ currLine ->
         if(currLine == '<!--blog-entries-->'){
            for( i in blogEntries.sort{it.year + it.month + it.date}.reverse()){
               newFile << "<li>" + i.year + "." + i.month + "." + i.date + ": <a href='$i.entryPath'>$i.title</a></li>\n"
               // year, month, date, title, entryPath
            }
         }

         newFile << processLine(currLine, "index")
      }
   }

   def createBlogFeed(){
      def newFile = new File(config.dir.site + "/" + config.dir.blog + "/atom.xml")
      def feedTemplate= new File(config.feedTemplate)

      feedTemplate.eachLine{ currLine ->
         if(currLine == '<!--feed-entries-->'){
            for( i in blogEntries.sort{it.year + it.month + it.date}.reverse()){
               // TODO: externalize this 
               newFile << "<entry>"
               newFile << "\t<id>$i.entryPath</id>"
               newFile << "\t<title>$i.title</title>"
               newFile << "\t<link href='${config.alias.siteUrl}$i.entryPath'/>"
               newFile << "\t<updated>$i.year-$i.month-${i.date}T00:00:00</updated>"
               newFile << "\t<summary>$i.title</summary>"
               newFile << "</entry>"
            }
         }

         newFile << processLine(currLine, "index")
      }
   }

   def processLine(String lineIn, String currentFileName){
      if(lineIn == null){
         return
      }

      // look for includes
      // TODO: figure out a way to dynamically create aliases for the templates
      // title = resume
      if(lineIn.startsWith("<!--include:")){
         lineIn += "\n"
         def includeFileName = config.dir.input+ "/" + lineIn[12..-5]
         println("\t\t- include: $includeFileName") 
         new File(includeFileName).eachLine{ currLine ->
            lineIn += processLine(currLine, currentFileName) + "\n" 
         }
      }

      // look for aliases
      config.alias.keySet().each{ currKey ->
         def fromString = config.aliasBegin + currKey + config.aliasEnd

         if(lineIn.contains(fromString)){
            def toString = config.alias.getProperty(currKey)
            println("\t\t- alias  : $fromString -> $toString ") 
            lineIn = lineIn.replace(fromString, toString)
         }
      }

      def currentDateToken = config.aliasBegin + "currentDate" + config.aliasEnd
      lineIn = lineIn.replace(currentDateToken, currentDate)

      def currentFileNamePrettyPrint = ""
      if(!currentFileName.equals('index')){
         currentFileName = currentFileName.replace('_', ' ')
         def headerDelimiter = config.headerDelimiter
         currentFileNamePrettyPrint = "${headerDelimiter} ${currentFileName}"
      }
      def currentFileNamePrettyPrintToken = config.aliasBegin + "currentFileNamePrettyPrint" + config.aliasEnd
      lineIn = lineIn.replace(currentFileNamePrettyPrintToken, currentFileNamePrettyPrint)

      lineIn
   }

   static void main(String[] args) {
      def pagin8 = new Pagin8()
      
      pagin8.createSiteDirectory()
      pagin8.processInput(new File(config.dir.input), "")
      pagin8.createBlogIndex() // creates a /blog/index.html file for you
      pagin8.createBlogFeed() // creates a /blog/index.html file for you

      //println("foo: " + pagin8.generateCurrentDate())
   }
}
