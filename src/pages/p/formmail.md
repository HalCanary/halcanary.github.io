A CGI program for processing HTML Forms
=======================================

You'll want to change the $to, $from, $subject, and $redirect variables.

By the way, you can use this code in accordance with the GNU GPL license.

    
    #!/usr/bin/perl 
    
    ## formmail
    ##   CGI mailer.
    ##
    ## Version 1.0
    ##
    ## (c) 2002-2004 Hal Canary
    ##   Concepts based on other cgi programs.
    ##
    ## License:
    ##   This is free software, see
    ##   http://www.gnu.org/licenses/gpl.txt
    ##   This product is distributed
    ##   WITHOUT ANY WARRANTY of any kind.
    
    use strict;
    use CGI;
    require Mail::Send;
    
    my $to       = 'Hal Canary <insert@address.here>';
    my $from     = 'formmail.cgi <insert@address.here>';
    my $subject  = 'WOHC Web Form';
    my $redirect = 'https://halcanary.org/';
    
    $ENV{PATH} = "/bin:/usr/bin";
    $CGI::DISABLE_UPLOADS = 1;
    $CGI::POST_MAX=1024 * 100; 
    my $cgi = CGI->new();
    my $msg = new Mail::Send;
    $msg->to($to);
    $msg->subject($subject);
    $msg->add("From", $from);
    my $fh = $msg->open;
    foreach ($cgi->param) {
        my $p = $_;
        print $fh ($p." = ".$cgi->param($p)."\n\n"); 
    }
    $fh->close;
    print "Location: $redirect\n\n";
    

How to use it:

    
    <!-- form.html -->
    
    <form method="post" action="/formmail.cgi">
    
    <p>
      What's your name?  
      <br />
      <input size="20" value=""
      name="My_name_is" type="text">
    </p>
    
    <p> 
      Favorite Color?
      <br/>
      <input value="yellow" name="fav_color_is"
      type="radio">yellow
      <br/>
      <input value="red" name="fav_color_is"
      type="radio">blue
      <br/>
      <input value="blue" name="fav_color_is"
      type="radio">blue
    </p>
    
    <p>
      Where do you live?
      <br />
      <input size="20" value=""
      name="I_am_from" type="text">
    </p>
    
    <p>
      Do you want to say anything else?  
      <br />
      <textarea name="comments" cols="50"
      rows="5"></textarea>
    </p>
    
    <p><input value="SUBMIT"
    type="submit"></p>
    
    </form>
    

**Version 2:**

    
    #!/usr/bin/perl 
    
    ## formmail
    ##   CGI mailer.
    ##
    ## Version 2.0
    ##
    ## (c) 2002-2006 Hal Canary
    ##   Concepts based on other cgi programs.
    ##
    ## License:
    ##   This is free software, see
    ##   http://www.gnu.org/licenses/gpl.txt
    ##   This product is distributed
    ##   WITHOUT ANY WARRANTY of any kind.
    
    use strict;
    use CGI;
    require Mail::Send;
    
    ## Modify these variables for your setup:
    
    my $to       = 'Name <address@example.com>';
    my $from     = 'Formmail <noreply@example.com>';
    my $subject  = 'example.com web form';
    my $redirect = 'http://example.com/';
    
    ## Use the cgi stuff.
    
    $ENV{PATH} = "/bin:/usr/bin";
    $CGI::DISABLE_UPLOADS = 1;
    $CGI::POST_MAX = 1024 * 100; 
    my $cgi = CGI->new();
    
    ## Varify that you really came from a html form.
    
    if (!$cgi->param()) {
      if ($cgi->referer()) {
        print "Location: ".$cgi->referer()."\n\n";
      } else {
        print "Location: ".$redirect."\n\n";
      }
      exit 0;
    }
    
    ## Mail the information.
    
    my $msg = new Mail::Send;
    $msg->to($to);
    $msg->subject($subject);
    $msg->add("From", $from);
    my $fh = $msg->open;
    print $fh "remote_host = ".$cgi->remote_host()."\n";
    print $fh "referer = ".$cgi->referer()."\n\n";
    foreach my $p ($cgi->param) {
      print $fh ($p." = ".$cgi->param($p)."\n\n"); 
    }
    $fh->close;
    
    ## Thank the user.
    
    print "Content-type: text/html\n\n";
    print '<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN"
    "http://www.w3.org/TR/html4/strict.dtd">
    <html><head><title></title></head><body>
    <div>Thank you for your submission.<blockquote><div>';
    print "remote_host = ".$cgi->remote_host()."<br />";
    print "referer = ".$cgi->referer()."<br />";
    foreach my $p ($cgi->param) {
      print ($p." = ".$cgi->param($p)."<br />"); 
    }
    print '</div></blockquote>';
    print '<a href="'.$redirect.'">back</a>';
    print '</div></body></html>';
    
    ## That is all.
    

* * *

<div class="rightside"><em>file modification time: 2006-09-16 03:19:45</em></div>

