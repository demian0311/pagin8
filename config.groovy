// aliases will be replaced in all files and includes
alias{ // @[theAlias]
   backgroundColor  = "E7ECEB" // muted, blue gray-ish
   h1BackgroundColor= "#f8f8f8"
   textColor        = "#666666"
   DLN              = "Demian L. Neidetcher"
   disqusShortname  = "neidetcher"
   siteUrl          = "http://neidetcher.com" // also for atom feed generation
   // special aliases 
   // currentDate                = 2012-01-07T01:48:31 
   // currentFileNamePrettyPrint = file name, underscores stripped, if index then blank 
}

// you probably don't need to mess with any of these values
dir{
   site  = "site"               // the destination directory
   input = "input" // where markdown files are
   blog  = "blog"
}

headerDelimiter = "|"
blogIndex       = "./input/blog/index.htm"
feedTemplate    = "./input/blog/atom.xml"
markdownHeader  = "./input/header.htm"
markdownFooter  = "./input/footer.htm"
aliasBegin      = "@["
aliasEnd        = "]"
