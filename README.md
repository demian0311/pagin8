pagin8
==============================================

## What's it do?
Static site generation in Groovy.  I took inspiration from the rizzo project.
I use the same Markdown Java library that they are.  Rizzo has cool features
around blogging which I have none of right now.

#### Alias token replacement
For example you can replace all instances of `@[backgroundColor]` with `#FFF`.
This is easily configured in config.groovy in the alias block.

Here is what my alias section of config.groovy looks like right now.

      alias{
         h1BackgroundColor= "#f8f8f8"
         textColor        = "#666666"
         DLN              = "Demian L. Neidetcher"
      }


#### Includes
Instead of repeating portions of HTML you can just include.
    <!--include:footer.htm-->


#### Markdown
You can also write your pages using [Markdown](http://daringfireball.net/projects/markdown/).
I use the [markdownj](http://markdownj.org/quickstart.html) project, seems good so far.


## Getting started 

Any time you want to change values in one place add values in the alias section of
config.groovy.

