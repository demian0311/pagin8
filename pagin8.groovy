#!/usr/bin/env groovy

import com.petebevin.markdown.MarkdownProcessor

@Grab('com.madgag:markdownj-core:0.4.1')

class Pagin8{
   def static config = new ConfigSlurper().parse(new File('config.groovy').toURL())
   def markDown = new MarkdownProcessor()

   def createSiteDirectory(){
      println "creating the site directory"
      def d1 = new File(config.dir.site)
      d1.mkdir()
   }


   /*
   the blog handling should more be about indexing stuff inside of 
   the target blog directory and making that available in reverse
   chronological order.
   */

   /*
   def handleBlogEntries(){
      // go through the years directory
      new File(config.dir.blog).eachFile{ yearDir ->
         //println("yearDir: $yearDir")
         println("year: $yearDir.name")
         if(yearDir.isDirectory()){
            yearDir.eachFile{ monthDir ->
               println("month: $monthDir.name")
               if(monthDir.isDirectory()){
                  monthDir.eachFile{ dayDir ->
                     println("day: $dayDir.name")
                     dayDir.eachFile{ entryFile ->
                        println("entry: $entryFile")
                     }
                  }
               }
            }
         }
      }
   }
   */

   // http://markdownj.org/quickstart.html
   def processInput(File currentDirectory){
      println("processing markdown files in $currentDirectory")
      def m = new MarkdownProcessor(); 
      new File(config.dir.input).eachFile{ currentFile ->

/*
         if(currentFile.isDirectory() && currentFile != currentDirectory){
            // recurse down, how to preserve the directory structure
            processMarkdown(currentFile)
         }
         */

         if(currentFile.name.endsWith(".md")){ 
            def newFileName = config.dir.site + "/" + currentFile.name[0..-4] + ".html" 
            println("\t-MARKDOWN: $currentFile --> $newFileName")
            // TODO: process aliases here too
            // TODO: process includes here too
            def newFile = new File(newFileName)
            newFile << (new File(config.markdownHeader)).readLines().join('\n')
            newFile << m.markdown(currentFile.readLines().join('\n'))
            newFile << (new File(config.markdownFooter)).readLines().join('\n')
         }
         else if(currentFile.name.endsWith(".html") || currentFile.name.endsWith(".css")){
            println "\t-RAW:  $currentFile.name"
            new File(config.dir.site + "/" + currentFile.name).withWriter{ destinationFile ->
               currentFile.eachLine{ currLine ->
                  destinationFile.writeLine(processLine(currLine))
               }
            }
         }
         else{
            // what else?
            println "\t-DUNNO: $currentFile.name"
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
      pagin8.processInput(new File(config.dir.input))
      //pagin8.handleBlogEntries()
   }
}
