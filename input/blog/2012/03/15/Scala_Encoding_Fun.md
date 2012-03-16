I pulled a toy Scala [project](https://github.com/demian0311/scalakata) into Eclipse and saw this 
problem:

    IO error while decoding [file].scala with UTF-8 Please try specifying another one using the -encoding option

[This guy](http://p5wscala.wordpress.com/tag/encoding/) didn't have the exact solution but he got me pointed in the right direction.  The problem is that 
I must have copied in some non-UTF-8 characters into my source code.  At this point the Scala compiler
got confused.

Here's a bunch of shell commands I did to fix the problem:

### Figure out the character set of the file we're having trouble with
    ~/code/scalakata>file -I ./src/test/scala/com/neidetcher/sfti/Chapter1Test.scala
    ./src/test/scala/com/neidetcher/sfti/Chapter1Test.scala: text/x-java; charset=iso-8859-1
Yep, the old iso-8859-1, that's not what we want.  That's what we use as the *from* encoding.

### Make a copy of the offending file UTF-8
    ~/code/scalakata>iconv -f iso-8859-1 -t utf-8 ./src/test/scala/com/neidetcher/sfti/Chapter1Test.scala > ./src/test/scala/com/neidetcher/sfti/Chapter1Test.scala.utf8

### The diff command will show you what the offending line was 
    ~/code/scalakata>diff ./src/test/scala/com/neidetcher/sfti/Chapter1Test.scala ./src/test/scala/com/neidetcher/sfti/Chapter1Test.scala.utf8
    134c134
    < 		// 4) Scala lets you multiply a string with a number?try out "crazy" * 3 in the REPL. What does this operation do? Where can you find it in Scaladoc?
    ---
    > 		// 4) Scala lets you multiply a string with a numberÑtry out "crazy" * 3 in the REPL. What does this operation do? Where can you find it in Scaladoc?


### Now actually replace the old file  
    ~/code/scalakata>mv ./src/test/scala/com/neidetcher/sfti/Chapter1Test.scala.utf8 ./src/test/scala/com/neidetcher/sfti/Chapter1Test.scala

After that I was back in business.

<!--include:disqus.htm-->
