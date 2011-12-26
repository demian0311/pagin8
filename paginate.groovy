#!/usr/bin/env groovy

def RAW_HTML_DIR = "./input/raw"
def SITE_DIR = "./site"

println "creating the site directory"
def d1 = new File(SITE_DIR)
d1.mkdir()

println "copying raw html"
new File(RAW_HTML_DIR).eachFile{ fromFile->
   if(fromFile.isFile()) {
      println "copying $fromFile.name"
      new File(SITE_DIR + "/" + fromFile.name).withWriter{ destinationFile ->
         fromFile.eachLine{ currLine ->
            destinationFile.writeLine(currLine)
         }
      }
   }
}
