Two programs for ftping...
==========================

I would sometime like to change these so that it is **one** program, with rcp/scp syntax.

[Go look at fcp](/p/fcp/).

<table class="code"><tbody><tr><th>ftpput.pl</th></tr><tr><td><pre><code>
#!/usr/bin/perl

## ftpput
##   Simple script to put files on a
##   remote server with ftp.
##
## (c) 2000-2003 Hal Canary
##
## License:
##   This is free software, see
##   http://www.gnu.org/licenses/gpl.txt
##   This product is distributed
##   WITHOUT ANY WARRANTY of any kind.

use Net::FTP;

if (($#ARGV eq 0) or ($ARGV[0] eq "")) {
    print "\nusage:\n"
    print "  $0 USER HOSTNAME DIRECTORY FILE [MORE FILES]\n\n";
    exit 1;
}

my ($login, $server, $directory, @files) = @ARGV;
my $pass = "";

system "stty -echo";
print $login."@".$server."'s password: ";
chomp($pass = &lt;STDIN&gt;);
print "\n";
system "stty echo";

my $ftp = Net::FTP-&gt;new($server, Debug =&gt; 0) 
    or die "\n    Unable to connect to $server!    \n\n";

print "Connected to $server. \n";

$ftp-&gt;login($login,$pass) 
    or die "\n    Incorect password!    \n\n";
print "Logged in as $login. \n";

$ftp-&gt;cwd($directory);
print "Directory is $directory. \n\n";

foreach $x (@files) {
    $ftp-&gt;put($x);
    print "Put $directory/$x \n";
}
$ftp-&gt;quit;   
print "\n"

</code></pre></td></tr></tbody></table>

<table class="code"><tbody><tr><th>ftpget.pl</th></tr><tr><td><pre><code>
#!/usr/bin/perl

## ftpget
##   Simple script to get files off
##   of a remote server with ftp.
##
## (c) 2000-2003 Hal Canary
##
## License:
##   This is free software, see
##   http://www.gnu.org/licenses/gpl.txt
##   This product is distributed
##   WITHOUT ANY WARRANTY of any kind.

use Net::FTP;

if (($#ARGV eq 0) or ($ARGV[0] eq "")) {
    print "\nusage:\n";
    print "  $0 USER HOSTNAME DIRECTORY FILE [MORE FILES]\n\n";
    exit 1;
}

my ($login, $server, $directory, @files) = @ARGV;

my $pass = "";

if (! $pass) {
    system "stty -echo";
    print $login."@".$server."'s password: ";
    chomp($pass = &lt;STDIN&gt;);
    print "\n";
    system "stty echo";
}

my $ftp = Net::FTP-&gt;new($server, Debug =&gt; 0) 
    or die "\n    Unable to connect to $server!    \n\n";

print "Connected to $server. \n";

$ftp-&gt;login($login,$pass) 
    or die "\n    Incorect password!    \n\n";
print "Logged in as $login. \n";

$ftp-&gt;cwd($directory);
print "Directory is $directory. \n\n";

for $x (@files) {
    print "  Getting $directory/$x ..." ;
    $ftp-&gt;get($x);
    print "done\n";
    system( "/bin/ls -l $x");
}
print "Quitting...";
$ftp-&gt;quit;  
print "\n";
</code></pre></td></tr></tbody></table>

* * *

file modification time: 2003-12-16 17:12:28

* * *
