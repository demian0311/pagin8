// aliases will be replaced in all files and includes
alias{
   h1BackgroundColor= "#f8f8f8"
   textColor        = "#666666"
   DLN              = "Demian L. Neidetcher"
}

// you probably don't need to mess with any of these values
dir{
   rawHtml = "./input/raw"       // files will just be copied over, with aliases and includes applied
   site = "./site"               // the destination directory
   include = "./input/include"   // snippest to pull from <!--include:footer.html-->
   markdown = "./input/markdown" // where markdown files are
}

markdownHeader = "./input/include/header.html"
markdownFooter = "./input/include/footer.html"
aliasBegin = "@["
aliasEnd = "]"

