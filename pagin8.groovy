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

      // look for an include
      if(lineIn.startsWith("<!-->>")){
         def includeFileName = config.dir.include + "/" + lineIn[6..-4]
         println("\t\t- $includeFileName <-- including") 
         def linesOut = ""
         new File(includeFileName).eachLine{ currLine ->
            linesOut += currLine + "\n" 
         }
         return linesOut
      }

      lineIn
   }
}

def pagin8 = new Pagin8()
pagin8.createSiteDirectory()
pagin8.copyRawHtml()
