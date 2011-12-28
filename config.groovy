// aliases will be replaced in all files and includes
alias{
   h1BackgroundColor= "#f8f8f8"
   textColor        = "#666666"
   DLN              = "Demian L. Neidetcher"
}

// you probably don't need to mess with any of these values
dir{
   site = "./site"               // the destination directory
   rawHtml = "./input"       // files will just be copied over, with aliases and includes applied
   include = "./input"   // snippest to pull from <!--include:footer.html-->
   markdown = "./input" // where markdown files are
}

markdownHeader = "./input/header.htm"
markdownFooter = "./input/footer.htm"
aliasBegin = "@["
aliasEnd = "]"

