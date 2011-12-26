#!/usr/bin/env groovy

class Pagin8{

   //def public RAW_HTML_DIR = "./input/raw"
   //def public SITE_DIR = "./site"
   def config = new ConfigSlurper().parse(new File('config.groovy').toURL())

   def createSiteDirectory(){
      println "creating the site directory"
      def d1 = new File(config.dir.site)
      d1.mkdir()
   }

   def copyRawHtml(){
      println "copying raw html and CSS"
      new File(config.dir.rawHtml).eachFile{ fromFile->
         if(fromFile.isFile()) {
            println "\t- $fromFile.name"
            new File(config.dir.site + "/" + fromFile.name).withWriter{ destinationFile ->
               fromFile.eachLine{ currLine ->
                  destinationFile.writeLine(processLine(currLine))
               }
            }
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
      //def map = config.alias.flatten
      config.alias.keySet().each{ currKey ->
         def fromString = config.aliasBegin + currKey + config.aliasEnd

         if(lineIn.contains(fromString)){
            def toString = config.alias.getProperty(currKey)
            println("\t\t- alias  : $fromString -> $toString ") 
            lineIn = ((java.lang.String)lineIn).replace(fromString, toString)
         }


         //lineIn = lineIn.replaceAll(fromString, toString) 
      }

      lineIn
   }
}

def pagin8 = new Pagin8()
pagin8.createSiteDirectory()
pagin8.copyRawHtml()
