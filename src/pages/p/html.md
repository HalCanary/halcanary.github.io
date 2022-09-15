# Web Design Philosophy

## Basic Goals

*   Keep content and markup seperate when possible. Therefore, use css, as well as dynamic templates.
*   Make it easy to administer

## Three types of content

1.  Journal Content. This can be abstracts that point to new content elsewhere on the site, or a personal journal, or links to outside content, or a combination of the above. Ususally only the most recent _n_ entries are displayed.
2.  Static Content. For instance _https://halcanary.org/p/photos_ is a static page that contains links to each of my photo albums. Paradoxicaly, a static page is more likely to change than a journal type page.
3.  Form based CGI's. Usually only used if the reader needs to send information back to the site.

additionally, dynamically generated search results, [like this example](https://www.google.com/search?&q=site%3Ahalcanary.org+devil%27s+lake). I'm not including this as one of the three, because I've never written a search engine.

## Getting Started

Under most circumstances, a web page is an HTML Document. Here is an example of a valid HTML document:

<div class="code"><div class="codeheader">a html document</div>

    <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN">
    <html>
      <head>
        <title>Title</title>
        <meta http-equiv="Content-type" 
          content='text/html; charset="iso-8859-1"'>
        <link rel="stylesheet" type="text/css" href="style.css">
      </head>
      <body>
        <h1>Heading</h1>
        <p>content</p>
      </body>
    </html>

</div>

Under most circumstances, you request a document, and the server feeds you back a stream of bytes that did not exist before that request. The server calls another script or program to output the HTML document on the fly. This is called a _dynamically generated web page_. Here is an example of a perl script that might genetate such a page:

<div class="code"><div class="codeheader">index.pl</div>

    #/usr/bin/perl

    print <<"HERE";
    <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN">
    <html>
      <head>
        <title>Title</title>
        <meta http-equiv="Content-type" 
          content='text/html; charset="iso-8859-1"'>
        <link rel="stylesheet" type="text/css" href="style.css">
      </head>
      <body>
        <h1>Heading</h1>
    HERE

    print '    <p>Current date and time: '.localtime().'.</p>';

    print <<"HERE";
      </body>
    </html>
    HERE

</div>

## Look And Layout

The next thing to do is design the look and layout of your page. I'm not going to tell you how to do it, but I will give suggestions:

*   First decide on layout. Try and keep it as simple as possible. Don't use frames, only a single large table.
*   Don't mess with the browser's default font. I hate that. Fonts are largely a matter of personal preference.
*   Put all your color and paragraph formatting information in a [css](http://www.w3.org/Style/CSS/) file.
*   Use subtle color shades to differentiate between logical parts of a document.

<div class="rightside"><em>file modification time: 2003-07-16 05:18:44</em></div>
