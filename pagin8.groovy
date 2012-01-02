#!/usr/bin/env groovy

import com.petebevin.markdown.MarkdownProcessor

@Grab('com.madgag:markdownj-core:0.4.1')

class Pagin8{
   def static config = new ConfigSlurper().parse(new File('config.groovy').toURL())
   def static markDown = new MarkdownProcessor() // http://markdownj.org/quickstart.html
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
         if(currentFile.name.endsWith(".md")){ 
            def newFileName = createDestinationPath(currentDirectory.getPath()) + "/" + currentFile.name[0..-4] + ".html" 
            println("\t-MARKDOWN: $currentFile --> $newFileName")
            def newFile = new File(newFileName)
            indexBlogEntry(newFile)

            newFile << processLine((new File(config.markdownHeader)).readLines().join('\n'))
            newFile << processLine(m.markdown(currentFile.readLines().join('\n')))
            newFile << processLine((new File(config.markdownFooter)).readLines().join('\n'))
         }
         else if(currentFile.name.endsWith(".html") || currentFile.name.endsWith(".css")){
            println "\t-RAW     : $currentFile.name"

            def newFileName = createDestinationPath(currentDirectory.getPath()) + "/" + currentFile.name
            def newFile = new File(newFileName)
            indexBlogEntry(newFile) // 

            newFile.withWriter{ destinationFile ->
               currentFile.eachLine{ currLine ->
                  destinationFile.writeLine(processLine(currLine))
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
   def indexBlogEntry(File blogEntryFile){
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
            for( i in blogEntries.sort{it.yeah + it.month + it.date}.reverse()){
               newFile << "<li>" + i.year + "." + i.month + "." + i.date + ": <a href='$i.entryPath'>$i.title</a></li>"
               // year, month, date, title, entryPath
            }
         }

         newFile << processLine(currLine)
      }
   }

   def processLine(String lineIn){
      if(lineIn == null){
         return
      }

      // look for includes
      if(lineIn.startsWith("<!--include:")){
         lineIn += "\n"
         def includeFileName = config.dir.input+ "/" + lineIn[12..-5]
         println("\t\t- include: $includeFileName") 
         new File(includeFileName).eachLine{ currLine ->
            lineIn += processLine(currLine) + "\n" 
         }
      }

      // look for aliases
      config.alias.keySet().each{ currKey ->
         def fromString = config.aliasBegin + currKey + config.aliasEnd

         if(lineIn.contains(fromString)){
            def toString = config.alias.getProperty(currKey)
            println("\t\t- alias  : $fromString -> $toString ") 
            lineIn = ((java.lang.String)lineIn).replace(fromString, toString)
         }
      }

      lineIn
   }

   static void main(String[] args) {
      def pagin8 = new Pagin8()
      pagin8.createSiteDirectory()
      pagin8.processInput(new File(config.dir.input), "")

      for( i in pagin8.blogEntries){
         println("blog entry: " + i)
      }

      pagin8.createBlogIndex() // creates a /blog/index.html file for you
   }
}
