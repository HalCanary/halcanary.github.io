Setting up a ssh tunnel/squid proxy for security.
=================================================

![[network diagram]](/images/wifi-security.png) The problem: **Can I trust WiFi?**

Assumptions:

1.  I may as well trust my own ISP and the Internet itself.
2.  Most public hotspots are the result of kindly gentlemen willing to help out a stranger or not-so-kindly gentlemen who forget to turn on security. \[edit: s/gentlmen/gentlepersons/g \]
3.  Every so often, there are malevolent hotspots who want to use a man-in-the-middle attack on you.
4.  More commonly, people just want to snoop on your HTTP traffic and see where you are going. They can break WEP to do this.

Conclusions.

1.  I want connectivity. I trust ~/.ssh/known\_hosts. Use ssh tunneling for everything.
2.  The question is how to set that up.

### The Plan

Ssh from the laptop (_laptop_) to the trusted workstation (_proxy_). Use ssh port forwarding to forward local port 13128 to port 3128 on proxy. Set up Squid on port 3128 (which happens to be the default) on the proxy machine.

Assume: proxy is a fedora/redhat machine. Any other unix flavor should work, but I use yum and /sbin/service. Your mileage might vary. Assume: your browser (and other internet applications) allows the use of proxies. Assume: you have ssh on your laptop. (Putty or cygwin/openssh will work for win32. I assume openssh.)

### Prepare

**Before you get started**, go to _proxy_, your proxy server, and install squid if it is not installed:

`proxy$ sudo yum install squid`

Set up your squid server. In /etc/squid/squid.conf, add this line AFTER "acl all src 0.0.0.0/0.0.0.0":

`http_access allow all`

This should be secure enough if you have your firewall set up. No need to punch holes in the firewall, as long as the ssh port is open.

### Putting it all together

**When you are on the road**, on the client:

`laptop$ PROXY=nnn.nnn.nnn.nnn laptop$ ssh -L 13128:$PROXY:3128 $PROXY`

Note: $PROXY must be the IP address of your proxy server.

![[]](/images/wifi-security-putty.png) If you use putty/windows instead of openssh, you can do the same with a gui. Here's what is should look like.  

On proxy:

`proxy$ sudo /sbin/service squid start`

![[]](/images/wifi-security-firefox.png) In your proxy settings on your browser, use localhost/13128 as your proxy. Here's what it should look like in firefox/windows.  

Everything should work now. When you are done:

`proxy$ sudo /sbin/service squid stop proxy$ logout laptop$`

Don't forget to fix your browser settings when you are done.

> (C) 2005 Hal W. Canary  
> hal at ups dot physics dot wisc dot edu  
> This document available under the [Creative Commons Attribution-ShareAlike License](http://creativecommons.org/licenses/by-sa/2.0/).

* * *

<div class="rightside"><em>file modification time: 2005-04-20 02:50:08</em></div>

