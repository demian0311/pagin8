pagin8
==============================================
I have a cheap little VPS from [SliceHost](http://www.slicehost.com/) / 
[RackSpace](http://www.rackspace.com/) that doesn't serve up [WordPress](http://wordpress.org/) very well.
I decided to go with a static site generation setup.  I played with some great options like 
[rizzo](https://github.com/fifthposition/rizzo) and [octopress](http://octopress.org/).  Being a programmer I just had to roll my own, ugh, I know, it's bad.

This isn't the most beautiful code I have ever written but it does what I need it to for now.
Feel free to fork it, blow away the `input` directory, add your own HTML and markdown then tweak 
the `config.groovy` file and you should be off to the races.

pagin8 generates my personal website at <http://neidetcher.com>.


## Features 
* authoring pages in HTML and markdown
* blogging in HTML and markdown
* atom feed
* alias substitution in markdown, HTML and CSS files
* include HTML file snippets that you define for headers and footers
* automatic page titles by filename
* integration with the disqus comment service
* central configuration

## Authoring 
Regular pages go in whatever directory structure you want.  As we process files we preserve
the directory structure.  You are responsible for linking to your own pages correctly.
Markdown pages must end in `.md` and will have `header.htm` and `footer.htm` applied to them automatically.  
HTML pages must end in `.html`, they can stand alone or have `<!--include:header.htm-->` and
`<!--include:footer.htm-->`.


## Building
Assuming everything is properly configured and you have files for pages and blog
entries you just go to the root of the project and run `pagin8.groovy`.

## Include files
Put this line in your HTML or markdown 

    <!--include:file_name.htm-->

The contents of the include file will also have aliases applied.

## Configuration
You shouldn't need to change any settings in config.groovy except for
the entries under `alias`.  

### Commenting with Disqus
To use the disqus service you need to:

1. [register your domain with disqus](http://disqus.com)
2. include the disqus.htm in whatever files you want to allow comments on (probably every blog entry)
3. set the `disqusShortname` in `config.groovy`

### Feeds
We use the `siteUrl` from `config.groovy` for the atom feeds.

### Aliases
Use whatever aliases you want, it's a good way to specify and easily change color schemes in CSS
or standard text like copyright information.  Wherever we find something like `@[aliasName]` we'll
look for aliasName under the alis section in the config file and replace whatever value you have specified.

## Blogging
Just put your HTML (.html) files or markdown (.md) into a 
directory conforming to this format

    ./input/blog/YYYY/MM/DD/title_of_your_blog_entry.md

The rest we take care of.

## How I publish
I have the project cloned on both my laptop and the server hosting my static site.  I edit 
files locally, when I'm satisfied I check them all in.  On the remote machine I pull and 
re-generate.  

Point your webserver to the location of your pagin8 output directory.  I have an [nginx](http://nginx.org/) 
configuration that looks like this

    location / {
        alias  /home/demian/code/pagin8/site/;
        index  index.html index.htm;
    }

I have a helpful script to run remote ssh commands, make sure you have your
public key up on the server hosting your site.

    #!/bin/sh
    ssh you@example.com '(cd ~/code/pagin8 && git pull origin master && rm -rf site && ./pagin8.groovy)'

