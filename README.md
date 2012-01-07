pagin8
==============================================

## Features 
* authoring pages in HTML and markdown
* blogging in HTML and markdown
* atom feed
* alias substitution in markdown, HTML and CSS files
* include HTML file snippets that you define for headers and footers
* automatic page titles by filename
* integration with the disqus comment service
* central configuration

## Include files
Put this line in your HTML or markdown 

    <!--include:file_name.htm-->

The contents of the include file will also have aliases applied.

## Configuration
You shouldn't need to change any settings in config.groovy except for
the entries under `alias`.  

### Disqus
To use the disqus service you need to:
1. register your domain with disqus
2. put include the disqus.htm in whatever files you want to allow comments on (probably every blog entry)
3. set the `disqusShortname` in config.groovy

### Feeds
We use the `siteUrl` from `config.groovy` for the atom feeds.

### Aliases
Use whatever aliases you want, it's a good way to specify and easily change color schemes in CSS
or standard text like copyright information.

## Blogging
Just put your HTML (.html) files or markdown (.md) into a 
directory conforming to this format

    ./input/blog/YYYY/MM/DD/title_of_your_blog_entry.md

The rest we take care of.

## How I publish
I have the project cloned on both my laptop and the server hosting my static site.  I edit 
files locally, when I'm satisfied I check them all in.  On the remote machine I pull and 
re-generate.  I have a helpful script to run remote ssh commands, make sure you have your
public key up on the server hosting your site.

    #!/bin/sh
    ssh you@example.com '(cd ~/code/pagin8 && git pull origin master && rm -rf site && ./pagin8.groovy)'

