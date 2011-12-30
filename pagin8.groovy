#!/usr/bin/env groovy

import com.petebevin.markdown.MarkdownProcessor

@Grab('com.madgag:markdownj-core:0.4.1')

class Pagin8{
   def static config = new ConfigSlurper().parse(new File('config.groovy').toURL())
   def static markDown = new MarkdownProcessor() // http://markdownj.org/quickstart.html

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
      //println("parentPath      : $parentPath")
      def m = new MarkdownProcessor(); 
      currentDirectory.eachFile{ currentFile ->
         if(currentFile.name.endsWith(".md")){ 
            def newFileName = createDestinationPath(currentDirectory.getPath()) + "/" + currentFile.name[0..-4] + ".html" 
            println("\t-MARKDOWN: $currentFile --> $newFileName")
            def newFile = new File(newFileName)
            newFile << (new File(config.markdownHeader)).readLines().join('\n')
            newFile << m.markdown(currentFile.readLines().join('\n'))
            newFile << (new File(config.markdownFooter)).readLines().join('\n')
         }
         else if(currentFile.name.endsWith(".html") || currentFile.name.endsWith(".css")){
            println "\t-RAW     : $currentFile.name"

            def newFileName = createDestinationPath(currentDirectory.getPath()) + "/" + currentFile.name

            new File(newFileName).withWriter{ destinationFile ->
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
            lineIn += currLine + "\n" 
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
      // TODO: index the blog entries pagin8.handleBlogEntries()
   }
}
