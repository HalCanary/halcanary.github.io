The Centered <div> Issue
========================

The goal is to put text inside a box of a fixed width that is centered in the middle of the web page. Right now, I use such a layout on [https://halcanary.org/vv/](/vv/) .

I had a problem doing this with non-deprecated HTML. This is how I solved that problem.

* * *

This is a common enough way to layout text on a web page. The HTML 3 way of doing things: <table>s and <center>s:

    
    <!DOCTYPE HTML PUBLIC
    	"-//W3C//DTD HTML 3.2 Final//EN">
    <HTML><HEAD><TITLE></TITLE></HEAD><BODY>
    <center>
    <table width="300px" BORDER="1" CELLSPACING="0">
    <tr><td>
    <p>
    TEST
    </p>
    </td></tr></table>
    </center>
    </BODY></HTML>
    

![[browser rendering]](/images/centered-div-1.png)

Let's replace that <table> with a <div>. <table>s should not be used for layout. Notice I have to use a "text-align:left:" to undo the <center>. This seems like a kludge.

    
    <!DOCTYPE HTML PUBLIC
    	"-//W3C//DTD HTML 4.01 Transitional//EN"
            "http://www.w3.org/TR/html4/loose.dtd">
    <HTML><HEAD><TITLE></TITLE></HEAD><BODY>
    <center>
    <div style="border:1px solid; width:300px; text-align:left">
    <p>
    TEST
    </p>
    </div>
    </center>
    </BODY></HTML>
    

![[browser rendering]](/images/centered-div-2.png)

<center> is [deprecated](http://www.w3.org/TR/REC-html40/present/graphics.html#edef-CENTER), so we can replace it with <div align="center">

    
    <!DOCTYPE HTML PUBLIC
    	"-//W3C//DTD HTML 4.01 Transitional//EN"
            "http://www.w3.org/TR/html4/loose.dtd">
    <HTML><HEAD><TITLE></TITLE></HEAD><BODY>
    <div align="center">
    <div style="border:1px solid; width:300px; text-align:left">
    <p>
    TEST
    </p>
    </div>
    </div>
    </BODY></HTML>
    

![[browser rendering]](/images/centered-div-3.png)

But <div align="???"> is also [deprecated](http://www.w3.org/TR/REC-html40/present/graphics.html#h-15.1.2), so lets use <div style="text-align:center">

    
    <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
       "http://www.w3.org/TR/html4/strict.dtd">
    <HTML><HEAD><TITLE></TITLE></HEAD><BODY>
    <div style="text-align:center">
    <div style="border:1px solid; width:300px; text-align:left">
    <p>
    TEST
    </p>
    </div>
    </div>
    </BODY></HTML>
    

![[browser rendering]](/images/centered-div-4.png)

But that **doesn't work**! It no longer centers the <div>. It looks like the text-align:center and the text-align:left are cancelling out.

* * *

**Here's the solution:**

    
    <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
       "http://www.w3.org/TR/html4/strict.dtd">
    <HTML><HEAD><TITLE></TITLE></HEAD><BODY>
    <div style="margin:auto; border:1px solid; width:300px">
    <p>
    TEST
    </p>
    </div>
    </BODY></HTML>
    

![[browser rendering]](/images/centered-div-5.png)

This is correct CSS and it reneders properly in Mozilla Firefox.

* * *

Now let's try converting that file into a XHTML file.

    
    <?xml version="1.0" encoding="utf-8"?>
    <!DOCTYPE html
            PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
            "http://www.w3.org/TR/xhtml1/DTD/strict.dtd">
    <html xmlns="http://www.w3.org/1999/xhtml"
            xml:lang="en" lang="en">
    <head><title></title></head><body>
    <div style="width:300px; margin:auto; border:1px solid">
    <p>
    TEST
    </p>
    </div>
    </body></html>
    

Here is how it renders in IE6.

![[browser rendering]](/images/centered-div-7-ie.png)

Why does IE6 render Strict XHTML 1.0 differently from the equivilent Strict HTML 4.01?

Try this:

    
    <?xml version="1.0" encoding="utf-8"?>
    <!DOCTYPE html
            PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
            "http://www.w3.org/TR/xhtml1/DTD/strict.dtd">
    <html xmlns="http://www.w3.org/1999/xhtml"
            xml:lang="en" lang="en">
    <head><title></title></head><body>
    <div style="text-align:center;">
    <div style="width:300px; margin:auto;border:1px solid;
            text-align:left;">
    <p>
    TEST
    </p>
    </div>
    </div>
    </body></html>
    

![[browser rendering]](/images/centered-div-8-ie.png)

That's a bit of a kludge, but it's valid html+css and it displays the same in IE6 and Mozilla.

* * *

This is how I go about doing the css for this page:

    
    /* the CSS file */
    body {
    	text-align: center;
    }
    div.column {
    	margin-left: auto;
    	margin-right: auto;
    	width: 640px;
    	text-align: left;
    }
    

and

    
    <!-- the HTML File -->
    <?xml version="1.0" encoding="utf-8"?>
    <!DOCTYPE html
    	PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
    	"http://www.w3.org/TR/xhtml1/DTD/strict.dtd">
    <html xmlns="http://www.w3.org/1999/xhtml"
    	xml:lang="en" lang="en">
     <head>
        <title></title>
        <link rel="stylesheet" type="text/css"
    	href="stylesheet.css" />
      </head>
      <body>
        <div class="column">
          CONTENT
        </div>
      </body>
    </html>
    

### Advanced Topics

Using the max-width property from CSS2. [This is a good description of the problem and a solution](http://www.svendtofte.com/code/max_width_in_ie/).

    
    /* the CSS file */
    body {
    	text-align: center;
    }
    div.column {
    	margin-left: auto;
    	margin-right: auto;
    	max-width: 640px;
    	width:expression(640 + "px");
    	text-align: left;
    }
    

* * *

* * *

file modification time: 2006-09-18 14:08:26

* * *
