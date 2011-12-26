#!/usr/bin/env groovy

class Pagin8{

   def public RAW_HTML_DIR = "./input/raw"
   def public SITE_DIR = "./site"

   def createSiteDirectory(){
      println "creating the site directory"
      def d1 = new File(SITE_DIR)
      d1.mkdir()
   }

   def copyRawHtml(){
      println "copying raw html and CSS"
      new File(RAW_HTML_DIR).eachFile{ fromFile->
         if(fromFile.isFile()) {
            println "\t- $fromFile.name"
            new File(SITE_DIR + "/" + fromFile.name).withWriter{ destinationFile ->
               fromFile.eachLine{ currLine ->
                  destinationFile.writeLine(currLine)
               }
            }
         }
      }
   }
}

def pagin8 = new Pagin8()
pagin8.createSiteDirectory()
pagin8.copyRawHtml()
