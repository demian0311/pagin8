Here's some opensource software that I have worked on.

=== SiloBase ===
<https://github.com/demian0311/silobase>

At work we wanted to give people within the company ad-hoc access to data.  But we didn't
want to give them complete access with a SQL tool and we also didn't have time to write
custom reports through our entire software stack.

You specify data sources and queries in XML.  The XML contains SQL queries with place holders,
we parse the queries and wherever we find the place holder we create form elements so the
user can specify values.

It's not much, kind of ugly but it worked well for the task.  There isn't much code here.
It highly leverages the capabilities of Spring JDBC template.


=== Pagin8 ===
<https://github.com/demian0311/pagin8>

I installed WordPress on my anemic little SliceHost VPS, it didn't perform as well as I
think it should.  I looked at other static site generators like rizzo and octopress but
for various reasons I didn't like what they had.  I probably should have given them more
   of a chance, octopress in particular has a good community and plugin support.  rizzo
   is implemented in groovy, that's the language I chose for Pagin8.

To create your site you just put HTML, CSS and markdown files in an input directory.  You
can define and use aliases, include other files (for headers and footers) and create
blog posts in both HTML and markdown.

