Address Book Reader
===================

written in [Python](http://www.python.org)

Here's a nice little program (two programs, in fact; does that make it a suite?) that searches an address book and displays entries. There are two halves to the program. `read.script` is meant for use on the command line. `read.cgi` is for the web. `read.cgi` used not-so-secure password protection.

* * *

The address Book
----------------

Here's an example address book:

    
    NAME: 
    Alfred Boxwell
    email: aboxwell@aol.com
    
    NAME: 
    Cal Donaldson
    email: cdonaldson@donaldson.net
    web: http://www.donaldson.net/~cdonaldson/
    phone: 123-456-7890
    phone: 123-237-9478 (cell)
    snail: 1234 Main St. \\ Townshire, IN 12345
    photo: http://www.donaldson.net/~cdonaldson/photo_of_me.jpg
    

I kind of made up the format as I went along. The important things are: (1) it is easy to edit with your favorite text editor. (2) it's flexible. Note that not every entry has to have every field, and any entriy can have two (or more) of the same field filled. Feel free to add new fields to the formatt, or even comments.

* * *

the script: read.script
-----------------------

Here it is:

    
    #!/usr/bin/python 
    #
    # ``Address Book Reader''
    # by Hal Canary
    # August 2000
    #
    # Use: 
    # read.script 
    #	will prompt you for the search pattern.	
    # read.script Hal
    #	will search for "Hal"
    # read.script Hal Canary
    #	will search for "Hal Canary"
    # read.script Hal Winchester Canary 
    #	will search for "Hal Winchester"
    # read.script "Hal Winchester Canary" 
    #	will search for "Hal Winchester Canary"
    #	
    addressbooklocal = '/home/hal/address.book' #where is my book?
    import sys
    import readline
    
    #A function to compare two strings:
    def check_strs(big_string, little_string):
    	if len(little_string) > len(big_string):
    		return 0
    	i = 0
    	while i <= (len(big_string) - len(little_string)) :
    		if big_string[i:i+len(little_string)] ==little_string :
    			return 1
    		i = i+1
    	return 0
    
    #Find out what we're searching for:
    if len(sys.argv) == 2:
    	name = sys.argv[1]
    elif len(sys.argv) >= 3:
    	name = sys.argv[1] + " " + sys.argv[2]
    else:
    	name = raw_input("Pleas enter a name: ")
    print "Searching for ``" + name + "''\n" 
    
    #Main search procedure:
    file = open(addressbooklocal, 'r')
    line = file.readline()	
    while line != '':
    	if line == 'NAME: \012':
    		line = file.readline()	
    		if check_strs(line, name):
    			while line != 'NAME: \012' and line != '': 
    				print line,
    				line =  file.readline()	
    	else:
    		line = file.readline()	
    file.close()

You should get output like this:

    
    [hal@ups ~]>read.script Alfred
    Searching for ``Alfred''
    
    Alfred Boxwell
    email: aboxwell@aol.com
    
    [hal@ups ~]>read.script Don
    Searching for ``Don''
    
    Cal Donaldson
    email: cdonaldson@donaldson.net
    web: http://www.donaldson.net/~cdonaldson/
    phone: 123-456-7890
    phone: 123-237-9478 (cell)
    snail: 1234 Main St. \\ Townshire, IN 12345
    photo: http://www.donaldson.net/~cdonaldson/photo_of_me.jpg
    
    [hal@ups ~]>

Any Questions?

* * *

The CGI Script
--------------

Here it is. Note the password protection!

    
    #!/usr/bin/python
    password = 'BadPassword'
    addressbooklocal = '/home/hal/address.book'
    print "Content-type: text/html \n"
    print "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">"
    print "<HTML>\n<HEAD>\n<TITLE>Address Book</TITLE>\n</HEAD>\n"
    print "<BODY bgcolor=\"#ffff80\" text=\"black\">\n<blockquote>\n"
    print "<h1>Address Book</h1>\n "
    
    import cgi
    def check_strs(big_string, little_string):
    	if len(little_string) > len(big_string):
    		return 0
    	i = 0
    	while i <= (len(big_string) - len(little_string)) :
    		if big_string[i:i+len(little_string)] ==little_string :
    			return 1
    		i = i+1
    	return 0
    
    def formatEntry(string, isname):
    	if isname==1:
    		return "<b><big>" + string[:-1] + "</big></b><br>\n"
    	elif string[0:7] == 'email: ':
    		return '<i>email:</i> <a href="mailto:' + string[7:-1] + '">'+string[7:-1] + '</a>' + "<br>\n"
    	elif string[0:5] == 'web: ':
    		return '<i>web:</i> <a href="' + string[5:-1] + '">'+string[5:-1] + '</a>' + "<br>\n"
    	elif string[0:7] == 'phone: ':
    		return '<i>phone:</i> ' + string[7:-1] + "<br>\n"
    	elif string[0:7] == 'snail: ':
    		return '<i>snail:</i> ' + string[7:-1] + "<br>\n"
    	elif string[0:7] == 'photo: ':
    		return '<img src="' + string[7:-1] + '">' + "<br>\n"
    	#	return '<i>photo: </i> <a href="' + string[7:-1] + '">'+string[7:-1] + '</a>' + "<br>\n"
    	elif string[0:5] == 'ICQ: ':
    		return '<i>ICQ:</i> ' + string[7:-1] + "<br>\n"
    	else:
    		return string[:-1] + "<br>\n"
    
    form = cgi.FieldStorage()
    form_ok = 0
    if form.has_key("data") and form.has_key("passwd"):
    	if form["passwd"].value == password:
    		form_ok=1
    if not form_ok:
    	print "ERROR!"
    	name = "ERROR!"
    else:
    	name = form["data"].value
    print "<h2>Searching for ``" + name + "''</h2>" , 
    print '<hr width="300" align="left">' + "\n" 
    file = open(addressbooklocal, 'r')
    line = file.readline()	
    isname=0
    while line != '' and  form_ok:
    	if line == 'NAME: \012':
    		line = file.readline()	
    		if check_strs(line, name):
    			isname=1
    			while line != '\012' and line != '': 
    				print formatEntry(line, isname), 
    				isname=0
    				line =  file.readline()
    			print '<hr width="300" align="left"> \012'
    	else:
    		line = file.readline()	
    print "\n<h2>Another Search?</h2>"
    print '<FORM METHOD=POST ACTION="/cgi-bin/hal/book/read.cgi">'
    print '<p>Enter Name:'
    print '<br><INPUT TYPE=TEXT NAME="data" SIZE=25 MAXLENGTH=80>'
    print '<!--<p>Enter Password:-->'
    print '<br><INPUT TYPE=hidden NAME="passwd" SIZE=25 MAXLENGTH=80 value="' + password + '">'
    print '<br><INPUT TYPE=SUBMIT VALUE="Sumbit">'
    print '</FORM>'
    print "\n</blockquote>\n</body>\n</html>"
    file.close()

Here's the web page that acceses this cgi-script:

    
    <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
    <html>
    <head>
    <title>Address Book</title>
    </head>
    
    <body bgcolor="#ffff80" text="black">
    <blockquote>
    <h1>Address Book</h1>
    <form method="post" action="/cgi-bin/read.cgi">
    <p>Enter Name:
    <br><input type="text" name="data" size="25" maxlength="80">
    <p>Enter Password:
    <br><input type="password" name="passwd" size="25" maxlength="80">
    <br><input type="submit" value="Sumbit">
    </form>
    </blockquote>
    </body>
    </html>
    
    

And here's some sample output:

    
    <h1>Address Book</h1>
    
    <h2>Searching for ``Cal''</h2> 
    
    <hr width="300" align="left" />
    
    <b><big>Cal Donaldson</big></b><br />
    <i>email:</i> <a
    href="mailto:cdonaldson@donaldson.net">cdonaldson@donaldson.net</a><br />
    <i>web:</i> <a href="http://www.donaldson.net/~cdonaldson/">http://www.donaldson.net/~cdonaldson/</a><br />
    <i>phone:</i> 123-456-7890<br />
    <i>phone:</i> 123-237-9478 (cell)<br />
    
    <i>snail:</i> 1234 Main St. \\ Townshire, IN 12345<br />
    <img src="http://www.donaldson.net/~cdonaldson/photo_of_me.jpg" alt="" /><br />
    <hr width="300" align="left" /> 
    
    <h2>Another Search?</h2>
    <form method="post" action="/cgi-bin/read.cgi">
    <p>Enter Name:
    <br/>
    <input type="text" name="data" size="25" maxlength="80" />
    <!--<p>Enter Password:-->
    <br />
    <input type="hidden" name="passwd" size="25" 
        maxlength="80" value="BadPassword" />
    <br />
    <input type="submit" value="Sumbit" />
    
    </p>
    </form>
    

* * *

* * *

file modification time: 2003-07-16 05:18:49

* * *
