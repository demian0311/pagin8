#!/usr/bin/env groovy

import com.petebevin.markdown.MarkdownProcessor

@Grab('com.madgag:markdownj-core:0.4.1')

class Pagin8{
   def config = new ConfigSlurper().parse(new File('config.groovy').toURL())
   def markDown = new MarkdownProcessor()

   def createSiteDirectory(){
      println "creating the site directory"
      def d1 = new File(config.dir.site)
      d1.mkdir()
   }

   def copyRawHtml(){
      println "copying raw html and CSS"
      new File(config.dir.rawHtml).eachFile{ fromFile->
         if(fromFile.isFile()) {
            if(fromFile.name.endsWith(".html") || fromFile.name.endsWith(".css")){
               println "\t- $fromFile.name"
               new File(config.dir.site + "/" + fromFile.name).withWriter{ destinationFile ->
                  fromFile.eachLine{ currLine ->
                     destinationFile.writeLine(processLine(currLine))
                  }
               }
            }
         }
      }
   }

   // http://markdownj.org/quickstart.html
   def processMarkdown(){
      println("processing markdown files")
      def m = new MarkdownProcessor(); 
      new File(config.dir.markdown).eachFile{ markdownFile ->
         if(markdownFile.name.endsWith(".md")){ 

            def newFileName = config.dir.site + "/" + markdownFile.name[0..-4] + ".html" 
            println("\t- $markdownFile --> $newFileName")

            // TODO: process aliases here too
            // TODO: process includes here too
            def newFile = new File(newFileName)
            newFile << (new File(config.markdownHeader)).readLines().join('\n')
            newFile << m.markdown(markdownFile.readLines().join('\n'))
            newFile << (new File(config.markdownFooter)).readLines().join('\n')
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
         def includeFileName = config.dir.include + "/" + lineIn[12..-5]
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
}

def pagin8 = new Pagin8()
pagin8.createSiteDirectory()
pagin8.copyRawHtml()
pagin8.processMarkdown()
