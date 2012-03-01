#!/bin/sh
exec scala -savecompiled -cp "./lib/markdownj-core-0.4.1.jar" "$0" "$@"
!#

import com.petebevin.markdown.MarkdownProcessor

Console.println("Hello, world!")
argv.toList foreach Console.println

val m = new MarkdownProcessor() // http://markdownj.org/quickstart.html
val result = m.markdown("This is a *simple* test.")
println("result: " + result)

// TODO: show that we can traverse the files


