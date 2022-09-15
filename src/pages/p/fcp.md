A program for ftping...
=======================

<table class="code"><tbody><tr><th>fcp</th></tr><tr><td><pre><code>
#!/usr/bin/perl

## fcp
##   rcp/scp-like functionality but for
##   ftp.  Front-end for Net::FTP.
##
## (c) 2000-2003 Hal Canary
##
## License:
##   This is free software, see
##   http://www.gnu.org/licenses/gpl.txt
##   This product is distributed
##   WITHOUT ANY WARRANTY of any kind.
##
## Bugs:
##   Doesn't use rcp/scp syntax yet.

use Net::FTP;

if (($#ARGV eq 0) or ($ARGV[0] eq "")) {
    print "\nusage:\n  $0 [g|p] USER HOSTNAME DIR";
    print "ECTORY FILE...\n  g = get\n  p = put\n\n";
    exit 1;
}

my ($gp, $login, $server, $directory, @files) = @ARGV;
my $pass = "";

system "stty -echo";
print $login."@".$server."'s password: ";
chomp($pass = &lt;STDIN&gt;);
print "\n";
system "stty echo";

my $ftp = Net::FTP-&gt;new($server, Debug =&gt; 0) 
    or die "\n\tUnable to connect to $server!\n\n";
print "Connected to $server. \n";

$ftp-&gt;login($login,$pass) 
    or die "\n\tIncorect password!\n\n";
print "Logged in as $login. \n";

$ftp-&gt;cwd($directory) 
    or die "\n\tCan't change directory!\n\n";
print "Directory is $directory. \n\n";

foreach $file (@files) {
    if ($gp eq "g") {
	print "  Getting $directory/$file ... " ;
	if (! $ftp-&gt;get($file)) { print "ERROR\n"; }
	else { print "done\n";}
    } elsif ($gp eq "p") {
	$ftp-&gt;put($file);
	print "Put $directory/$file \n";
    }
}
$ftp-&gt;quit;   
print "\n";
</code></pre></td></tr></tbody></table>

* * *

<div class="rightside"><em>file modification time: 2003-12-16 16:59:13</em></div>
