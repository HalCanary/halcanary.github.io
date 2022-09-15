CGI Email Reader
================

This is a cool little mailreader I wrote.

mailmenu.py

* * *

    
    #!/usr/bin/python
    
    # (c) 2001-2003 Hal Canary
    # Not only is this code badly written, 
    # it is probably horribly insecure, so I 
    # provide ABSOLUTELY NO WARRANTY.
    # License:
    # 	http://www.gnu.org/licenses/gpl.txt
    
    import cgi
    import os
    import string
    
    mypassword="badpassword"
    cgilocation="/cgi-bin/cgiwrap/hal/mailmenu.py"
    defaulfile="/var/spool/mail/hal"
    mailfolderslist="/home/hal/mail/.ls"
    style="/~hal/hal.css"
    icon="/~hal/favicon.ico"
    
    def header():
    	print "Content-type: text/html \n"
    	print '<?xml version="1.0" encoding="US-ASCII"?>'
    	print '<!DOCTYPE html '
    	print '     PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" '
    	print '     "DTD/xhtml1-transitional.dtd">'
    	print '<html xmlns="http://www.w3.org/1999/xhtml" ',
    	print 'xml:lang="en" lang="en">'
    	print '  <head>'
    	print '    <title>CGI Mail</title>'
    	print '    <link rel="SHORTCUT ICON" href="'+icon+'" />'
    	print '    <link rel="stylesheet" type="text/css" href="'+style+'" />'
    	print '  </head>'
    	print '  <body>'
    	print '    <div class="small-center">'
    	print '      <p>'
    	print '        <a href="/">MAIN</a> '
    	print '      </p>'
    	print '    </div>'
    	print "    <h1>Hal's Mail</h1>"
    
    def tailer():
    	print "    <hr />\n  </body>\n</html>"
    
    def login():
    	print '    <form method="post" action="'+cgilocation+'">'
    	print '      <p>Check a mail folder on ups. <br />'
    	print '        <input type="hidden" name="N" value="0" />'
    	print '        <input type="hidden" name="file"',
    	print ' value="'+defaulfile+'" />'
    	print '        <b>Password:</b>'
    	print '        <input type="password" name="passwd" size="25" />'
    	print '        <input type="submit" value="Login" />'
    	print '      </p>'
    	print '    </form>'
    
    def getmenu():
    	filename=form["file"].value
    	passwd=form["passwd"].value
    	print '    <form method="post" action="'+cgilocation+'">'
    	print '      <p>'
    	print '        <input type="hidden" name="N" value="0" />'
    	print '      <input type="hidden"  name="file" value="'+filename+'" />'
    	print '       <input type="hidden" name="passwd" value="'+passwd+'" />'
    	print '        <input type="submit" value="Folder Index" />'
    	print "      </p>\n    </form>\n"
    	print '    <form method="post" action="'+cgilocation+'">'
    	print '      <p>'
    	print '        <input type="hidden" name="N" value="0" />'
    	print '        <input type="hidden"  name="file" value=" " />'
    	print '       <input type="hidden" name="passwd" value="'+passwd+'" />'
    	print '        <input type="submit" value="Choose Folder" />'
    	print "      </p>\n    </form>\n"
    
    def choosefolder():
    	filename=form["file"].value
    	passwd=form["passwd"].value
    	file=open(mailfolderslist,'r')
    	print '    <form method="post" action="'+cgilocation+'">'
    	print "      <p>"
    	print '        <input type="hidden" name="N" value="'+str(0)+'" />'
    	print '        <input type="hidden" name="passwd"',
    	print ' value="'+form["passwd"].value+'" />'
    	print "        <select name=\"file\">"
    	print "          <option>/var/spool/mail/hal</option>"
    	line= file.readline()
    	while line != '' :
    		print "          <option>"+line[:-1]+"</option>"
    	        line= file.readline()
    	print '        </select>'
    	print '        <input type="submit" value="Get Folder" />'
    	print "      </p>\n    </form>"
    	file.close()
    	print '    <form method="post" action="'+cgilocation+'">'
    	print "      <p>"
    	print '        <input type="hidden" name="N" value="0" />'
    	print '    <input type="hidden"  name="file" value="'+defaulfile+'" />'
    	print '       <input type="hidden" name="passwd" value="'+passwd+'" />'
    	print '        <input type="submit" value="Default" />'
    	print "      </p>\n    </form>\n"
    
    def writemailn():
    	filename=form["file"].value
    	passwd=form["passwd"].value
    	messagenumber=int(form["N"].value)
    	file = open(filename, 'r')
    	N=0
    	line= file.readline()
    	print '<table><tr><td><pre>'
    	while line != '':
    		if line[0:5]=="From ":
    			N=N+1
    		if messagenumber == N:
    			print string.replace(line,"<","&lt;"),
    			#print line,
    		line= file.readline()
    	print '</pre></td></tr></table>'
    	file.close()
    
    def folderindex():
    	filename=form["file"].value
    	passwd=form["passwd"].value
    	file = open(filename, 'r')
    	formstate="closed"
    	N=0
    	line= file.readline()
    	while line != '':
    		if line[0:5]=="From " and N==0:
    			N=1
    		elif line[0:5]=="From ":
    			N=N+1
    			if formstate=="open" :
    				print "      </p>\n    </form>"
    				print "\n    <hr />"
    			formstate = "open"
    			print ''
    			print '    <form method="post" ',
    			print 'action="'+cgilocation+'">'
    			print '      <!--',line[:-1],'-->'
    			print "      <p>"
    			print '        <input type="hidden" name="N"',
    			print ' value="' + str(N) + '" />'
    			print '        <input type="hidden" name="file"',
    			print ' value="'+filename+'" />'
    			print '        <input type="hidden" name="passwd"',
    			print ' value="'+passwd+'" />'
    			print '       <input type="submit" value="Get Mail" />'
    		if line[0:44]=="Subject: DON'T DELETE THIS MESSAGE -- FOLDER":
    			print '',
    		elif line[0:31]=="From: Mail System Internal Data":
    			print '',
    		elif line[0:9]=="Subject: " or line[0:6]=="From: " :
    			print '        <br />',string.replace(line,"<","&lt;"),
    		elif line[0:4]=="To: " or line[0:6]=="Date: " :
    			if formstate=="open" :
    				print '        <br />',
    				print string.replace(line,"<","&lt;"),
    		line= file.readline()
    	file.close()
    	if formstate=="open" :
    		print "      </p>\n    </form> \n\n    <hr />\n"
    	formstate = "closed"
    
    
    form = cgi.FieldStorage()
    
    header()
    
    if not (form.has_key("passwd") and form.has_key("file") and form.has_key("N")):
    	login()
    elif form["passwd"].value != mypassword :
    	print "<p>Wrong Passwd</p>"
    	login()
    elif form["file"].value == ' ':
    	choosefolder()
    elif form["N"].value == "0" :
    	folderindex()	
    	getmenu()
    else:
    	writemailn()
    	print '    <hr />'
    	getmenu()
    tailer()
    
    

* * *

<div class="rightside"><em>file modification time: 2003-12-07 22:23:26</em></div>

