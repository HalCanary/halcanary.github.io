Firewall Notes
==============

### Quickstart:

Instructions for configuring a linux firewall for a simple non-routing workstaion.

Save this file as `/usr/local/sbin/iptables-script.sh` :

    
    #!/bin/sh
    PATH=/sbin:$PATH
    iptables --flush
    iptables --delete-chain
    iptables --policy INPUT DROP
    iptables --policy FORWARD DROP
    iptables --policy OUTPUT ACCEPT
    iptables -A INPUT -i lo -j ACCEPT
    iptables -A INPUT -d 255.255.255.255 -j DROP
    iptables -A INPUT -p icmp -m icmp --icmp-type 255 -j ACCEPT
    iptables -A INPUT -m state --state RELATED,ESTABLISHED -j ACCEPT
    iptables -A INPUT -p tcp -m state --state NEW -m tcp --dport 22 -j ACCEPT
    iptables -A INPUT -j REJECT --reject-with icmp-host-prohibited
    

Then, as root, execute this script. If everything works as planned, set up your system to implement these rules at every boot. If you use a redhat-type system, run:

    
    /sbin/service iptables save
    /sbin/chkconfig iptables on
    

If that doesn't work, you could try calling the script from `/etc/rc.local` .

* * *

### Dirty! Dirty boy!

The internet is a dirty place. I'm not talking about porn, I'm talking about worms, trojans, and viruses.

By the way, what is the difference between worms, trojans, and viruses? They all are forms of _malware_, distinguished by how they reproduce. _Worms_ can infect other computers directly over the internet. _Viruses_ propagate by hitching a ride with otherwise good information (Word documents, legitimate email). _Trojans_ pretend to be good information, but are not.

This logwatch report shows 289 dropped packets in a day. I didn't log all dropped packets (`-m limit --limit 1/min`); one in three didn't get logged. And I dropped another 10,000 or so sent to 255.255.255.255, the broadcast address.

    
     ################### LogWatch 4.3.2 (02/18/03) ##################
           Processing Initiated: Fri Apr  9 04:02:02 2004
           Date Range Processed: yesterday
         Detail Level of Output: 0
              Logfiles for Host: lensman.localdomain
     ################################################################
    
     --------------------- Kernel Begin ------------------------
    
    Dropped 289 packets on interface eth0
       From 4.43.201.77 - 2 packets
          To 66.191.XXX.XXX - 2 packets
             Service: asp (tcp/27374) (eth0,none) - 2 packets
       From 24.103.27.182 - 2 packets
          To 66.191.XXX.XXX - 2 packets
             Service: 81 (tcp/81) (eth0,none) - 2 packets
       From 24.118.142.205 - 3 packets
          To 66.191.XXX.XXX - 3 packets
             Service: 1214 (udp/1214) (eth0,none) - 3 packets
       From 24.130.88.220 - 4 packets
          To 66.191.XXX.XXX - 4 packets
             Service: 1026 (udp/1026) (eth0,none) - 2 packets
             Service: 1027 (udp/1027) (eth0,none) - 2 packets
       From 24.176.84.73 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 6129 (tcp/6129) (eth0,none) - 1 packet
       From 24.187.82.135 - 3 packets
          To 66.191.XXX.XXX - 3 packets
             Service: 3127 (tcp/3127) (eth0,none) - 3 packets
       From 61.17.107.11 - 3 packets
          To 66.191.XXX.XXX - 3 packets
             Service: 1026 (udp/1026) (eth0,none) - 1 packet
             Service: 1027 (udp/1027) (eth0,none) - 2 packets
       From 61.133.63.113 - 5 packets
          To 66.191.XXX.XXX - 5 packets
             Service: 1025 (tcp/1025) (eth0,none) - 1 packet
             Service: 2745 (tcp/2745) (eth0,none) - 1 packet
             Service: 3127 (tcp/3127) (eth0,none) - 1 packet
             Service: 5000 (tcp/5000) (eth0,none) - 1 packet
             Service: 6129 (tcp/6129) (eth0,none) - 1 packet
       From 64.45.210.38 - 4 packets
          To 66.191.XXX.XXX - 4 packets
             Service: 1026 (udp/1026) (eth0,none) - 2 packets
             Service: 1027 (udp/1027) (eth0,none) - 2 packets
       From 64.123.109.83 - 2 packets
          To 66.191.XXX.XXX - 2 packets
             Service: ms-sql-s (tcp/1433) (eth0,none) - 2 packets
       From 65.107.53.98 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: ms-sql-s (tcp/1433) (eth0,none) - 1 packet
       From 65.169.154.6 - 2 packets
          To 66.191.XXX.XXX - 2 packets
             Service: 45273 (tcp/45273) (eth0,none) - 1 packet
             Service: 45274 (tcp/45274) (eth0,none) - 1 packet
       From 65.191.117.228 - 2 packets
          To 66.191.XXX.XXX - 2 packets
             Service: 1026 (udp/1026) (eth0,none) - 1 packet
             Service: 1027 (udp/1027) (eth0,none) - 1 packet
       From 66.56.62.142 - 5 packets
          To 66.191.XXX.XXX - 5 packets
             Service: 1025 (tcp/1025) (eth0,none) - 1 packet
             Service: 2745 (tcp/2745) (eth0,none) - 2 packets
             Service: 3127 (tcp/3127) (eth0,none) - 1 packet
             Service: 6129 (tcp/6129) (eth0,none) - 1 packet
       From 66.129.38.47 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 4899 (tcp/4899) (eth0,none) - 1 packet
       From 66.191.94.119 - 8 packets
          To 66.191.XXX.XXX - 8 packets
             Service: 1025 (tcp/1025) (eth0,none) - 1 packet
             Service: 2745 (tcp/2745) (eth0,none) - 5 packets
             Service: 3127 (tcp/3127) (eth0,none) - 1 packet
             Service: 6129 (tcp/6129) (eth0,none) - 1 packet
       From 66.191.106.241 - 38 packets
          To 66.191.XXX.XXX - 38 packets
             Service: 1025 (tcp/1025) (eth0,none) - 10 packets
             Service: 2745 (tcp/2745) (eth0,none) - 23 packets
             Service: 3127 (tcp/3127) (eth0,none) - 3 packets
             Service: 6129 (tcp/6129) (eth0,none) - 2 packets
       From 66.191.XXX.XXX - 1 packet
          To 66.191.XXX.255 - 1 packet
             Service: netbios-ns (udp/137) (eth0,none) - 1 packet
       From 66.191.157.28 - 11 packets
          To 66.191.XXX.XXX - 11 packets
             Service: 1025 (tcp/1025) (eth0,none) - 3 packets
             Service: 2745 (tcp/2745) (eth0,none) - 8 packets
       From 66.191.161.138 - 27 packets
          To 66.191.XXX.XXX - 27 packets
             Service: 1025 (tcp/1025) (eth0,none) - 9 packets
             Service: 2745 (tcp/2745) (eth0,none) - 10 packets
             Service: 3127 (tcp/3127) (eth0,none) - 5 packets
             Service: 6129 (tcp/6129) (eth0,none) - 3 packets
       From 66.191.203.20 - 11 packets
          To 66.191.XXX.XXX - 11 packets
             Service: 2745 (tcp/2745) (eth0,none) - 11 packets
       From 66.191.218.183 - 16 packets
          To 66.191.XXX.XXX - 16 packets
             Service: 1025 (tcp/1025) (eth0,none) - 3 packets
             Service: 2745 (tcp/2745) (eth0,none) - 10 packets
             Service: 3127 (tcp/3127) (eth0,none) - 2 packets
             Service: 6129 (tcp/6129) (eth0,none) - 1 packet
       From 66.191.220.73 - 3 packets
          To 66.191.XXX.XXX - 3 packets
             Service: 2745 (tcp/2745) (eth0,none) - 3 packets
       From 66.197.0.50 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 42366 (tcp/42366) (eth0,none) - 1 packet
       From 66.197.0.63 - 2 packets
          To 66.191.XXX.XXX - 2 packets
             Service: 42358 (tcp/42358) (eth0,none) - 2 packets
       From 66.197.0.171 - 10 packets
          To 66.191.XXX.XXX - 10 packets
             Service: 42355 (tcp/42355) (eth0,none) - 10 packets
       From 67.69.151.99 - 2 packets
          To 66.191.XXX.XXX - 2 packets
             Service: 1026 (udp/1026) (eth0,none) - 1 packet
             Service: 1027 (udp/1027) (eth0,none) - 1 packet
       From 68.61.155.36 - 2 packets
          To 66.191.XXX.XXX - 2 packets
             Service: 34816 (tcp/34816) (eth0,none) - 2 packets
       From 68.116.249.187 - 3 packets
          To 66.191.XXX.XXX - 3 packets
             Service: 1026 (udp/1026) (eth0,none) - 1 packet
             Service: 1027 (udp/1027) (eth0,none) - 2 packets
       From 68.164.227.254 - 2 packets
          To 66.191.XXX.XXX - 2 packets
             Service: 1026 (udp/1026) (eth0,none) - 1 packet
             Service: 1027 (udp/1027) (eth0,none) - 1 packet
       From 69.34.144.210 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1027 (udp/1027) (eth0,none) - 1 packet
       From 69.46.29.189 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1026 (udp/1026) (eth0,none) - 1 packet
       From 69.93.198.113 - 12 packets
          To 66.191.XXX.XXX - 12 packets
             Service: 1026 (udp/1026) (eth0,none) - 12 packets
       From 80.142.179.89 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: ms-sql-s (tcp/1433) (eth0,none) - 1 packet
       From 80.177.198.10 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: ms-sql-s (tcp/1433) (eth0,none) - 1 packet
       From 81.250.171.239 - 2 packets
          To 66.191.XXX.XXX - 2 packets
             Service: ms-sql-s (tcp/1433) (eth0,none) - 2 packets
       From 140.247.29.166 - 5 packets
          To 66.191.XXX.XXX - 5 packets
             Service: 1025 (tcp/1025) (eth0,none) - 1 packet
             Service: 2745 (tcp/2745) (eth0,none) - 1 packet
             Service: 3127 (tcp/3127) (eth0,none) - 1 packet
             Service: 5000 (tcp/5000) (eth0,none) - 1 packet
             Service: 6129 (tcp/6129) (eth0,none) - 1 packet
       From 141.150.107.61 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 17300 (tcp/17300) (eth0,none) - 1 packet
       From 162.105.142.3 - 3 packets
          To 66.191.XXX.XXX - 3 packets
             Service: sunrpc (tcp/111) (eth0,none) - 3 packets
       From 198.17.201.195 - 2 packets
          To 66.191.XXX.XXX - 2 packets
             Service: 6129 (tcp/6129) (eth0,none) - 2 packets
       From 201.128.202.60 - 3 packets
          To 66.191.XXX.XXX - 3 packets
             Service: 3127 (tcp/3127) (eth0,none) - 3 packets
       From 203.236.97.45 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 4899 (tcp/4899) (eth0,none) - 1 packet
       From 204.18.44.149 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1028 (udp/1028) (eth0,none) - 1 packet
       From 204.32.9.174 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1029 (udp/1029) (eth0,none) - 1 packet
       From 204.44.117.117 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1028 (udp/1028) (eth0,none) - 1 packet
       From 204.48.34.209 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1029 (udp/1029) (eth0,none) - 1 packet
       From 204.108.210.64 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1028 (udp/1028) (eth0,none) - 1 packet
       From 204.110.216.153 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1029 (udp/1029) (eth0,none) - 1 packet
       From 204.141.237.116 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1028 (udp/1028) (eth0,none) - 1 packet
       From 204.252.71.13 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1029 (udp/1029) (eth0,none) - 1 packet
       From 205.33.102.53 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1028 (udp/1028) (eth0,none) - 1 packet
       From 207.36.233.89 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1026 (udp/1026) (eth0,none) - 1 packet
       From 208.180.115.209 - 3 packets
          To 66.191.XXX.XXX - 3 packets
             Service: 1214 (udp/1214) (eth0,none) - 3 packets
       From 209.50.235.10 - 3 packets
          To 66.191.XXX.XXX - 3 packets
             Service: auth (tcp/113) (eth0,none) - 3 packets
       From 210.178.106.148 - 5 packets
          To 66.191.XXX.XXX - 5 packets
             Service: 1025 (tcp/1025) (eth0,none) - 3 packets
             Service: 2745 (tcp/2745) (eth0,none) - 2 packets
       From 211.20.71.84 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 4899 (tcp/4899) (eth0,none) - 1 packet
       From 211.99.204.84 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 23410 (tcp/23410) (eth0,none) - 1 packet
       From 211.136.163.240 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1027 (udp/1027) (eth0,none) - 1 packet
       From 211.148.102.210 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1026 (udp/1026) (eth0,none) - 1 packet
       From 211.162.168.216 - 5 packets
          To 66.191.XXX.XXX - 5 packets
             Service: 1025 (tcp/1025) (eth0,none) - 1 packet
             Service: 2745 (tcp/2745) (eth0,none) - 1 packet
             Service: 3127 (tcp/3127) (eth0,none) - 1 packet
             Service: 5000 (tcp/5000) (eth0,none) - 1 packet
             Service: 6129 (tcp/6129) (eth0,none) - 1 packet
       From 211.171.149.114 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1026 (udp/1026) (eth0,none) - 1 packet
       From 211.175.24.90 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1027 (udp/1027) (eth0,none) - 1 packet
       From 211.194.78.167 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1027 (udp/1027) (eth0,none) - 1 packet
       From 211.195.98.150 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1026 (udp/1026) (eth0,none) - 1 packet
       From 211.216.249.13 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1027 (udp/1027) (eth0,none) - 1 packet
       From 211.236.25.108 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1027 (udp/1027) (eth0,none) - 1 packet
       From 211.244.145.174 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1026 (udp/1026) (eth0,none) - 1 packet
       From 212.8.193.255 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1026 (udp/1026) (eth0,none) - 1 packet
       From 212.12.34.231 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1026 (udp/1026) (eth0,none) - 1 packet
       From 212.17.198.131 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1026 (udp/1026) (eth0,none) - 1 packet
       From 212.21.11.155 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1026 (udp/1026) (eth0,none) - 1 packet
       From 212.24.80.49 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1027 (udp/1027) (eth0,none) - 1 packet
       From 212.36.70.78 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1027 (udp/1027) (eth0,none) - 1 packet
       From 212.37.36.224 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 6112 (tcp/6112) (eth0,none) - 1 packet
       From 212.79.144.102 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1027 (udp/1027) (eth0,none) - 1 packet
       From 212.116.1.121 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1026 (udp/1026) (eth0,none) - 1 packet
       From 212.119.151.162 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1027 (udp/1027) (eth0,none) - 1 packet
       From 212.121.104.172 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1027 (udp/1027) (eth0,none) - 1 packet
       From 212.135.185.162 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1026 (udp/1026) (eth0,none) - 1 packet
       From 212.135.231.165 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1026 (udp/1026) (eth0,none) - 1 packet
       From 212.179.149.196 - 3 packets
          To 66.191.XXX.XXX - 3 packets
             Service: ms-sql-s (tcp/1433) (eth0,none) - 3 packets
       From 217.129.202.193 - 4 packets
          To 66.191.XXX.XXX - 4 packets
             Service: 17300 (tcp/17300) (eth0,none) - 4 packets
       From 217.132.125.225 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 6129 (tcp/6129) (eth0,none) - 1 packet
       From 217.160.94.163 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: sunrpc (tcp/111) (eth0,none) - 1 packet
       From 218.90.156.18 - 4 packets
          To 66.191.XXX.XXX - 4 packets
             Service: 1025 (tcp/1025) (eth0,none) - 1 packet
             Service: 2745 (tcp/2745) (eth0,none) - 1 packet
             Service: 3127 (tcp/3127) (eth0,none) - 1 packet
             Service: 6129 (tcp/6129) (eth0,none) - 1 packet
       From 219.117.200.19 - 2 packets
          To 66.191.XXX.XXX - 2 packets
             Service: 3127 (tcp/3127) (eth0,none) - 2 packets
       From 220.170.88.7 - 5 packets
          To 66.191.XXX.XXX - 5 packets
             Service: 2282 (tcp/2282) (eth0,none) - 1 packet
             Service: squid (tcp/3128) (eth0,none) - 1 packet
             Service: 3382 (tcp/3382) (eth0,none) - 1 packet
             Service: 6588 (tcp/6588) (eth0,none) - 1 packet
             Service: 8000 (tcp/8000) (eth0,none) - 1 packet
       From 221.6.178.2 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 5000 (tcp/5000) (eth0,none) - 1 packet
       From 221.140.75.218 - 5 packets
          To 66.191.XXX.XXX - 5 packets
             Service: 1025 (tcp/1025) (eth0,none) - 1 packet
             Service: 2745 (tcp/2745) (eth0,none) - 2 packets
             Service: 5000 (tcp/5000) (eth0,none) - 1 packet
             Service: 6129 (tcp/6129) (eth0,none) - 1 packet
       From 221.232.160.103 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 1026 (udp/1026) (eth0,none) - 1 packet
       From 221.237.76.163 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 3389 (tcp/3389) (eth0,none) - 1 packet
       From 222.110.9.43 - 1 packet
          To 66.191.XXX.XXX - 1 packet
             Service: 4899 (tcp/4899) (eth0,none) - 1 packet
    
     ---------------------- Kernel End -------------------------
    

If you check those ports at the [Internet Storm Center](http://isc.sans.org/), you'll find that many of them are used by trojans and worms.

So, what do I do about it? At first I ignored the problem, then I turned on the default firewall. Now I've been hacking the firewall.

* * *

### The Default Firewall

On Redhat systems (In this case, Fedora Core 1), the `redhat-config-securitylevel` program configures the firewall. (In FC2, this command has been renamed `system-config-securitylevel`.) That's all well and good, but it doesn't give you much flexibility. Here's a screenshot:

![[redhat-config-securitylevel screenshot]](/images/redhat-config-securitylevel.png)

The firewall can be enabled or disabled. And it can be disabled for certain devices. And you can open any of 5 ports: 80, 21, 22, 23, and 25. Good enough for Aunt Tillie, who only wants to run a subset of those five services. But not good enough for me.

To find out that `redhat-config-securitylevel` really does, you can check out `/etc/sysconfig/iptables`, or you can run the command `/sbin/iptables-save`. This will give you a file in the iptables-save/iptables-restore format. **Do not hand edit these type of files!**. In this example, I'm opening all five ports. Normally, one would not do this. telnet and ftp are especially dangerous.

    
    # Generated by iptables-save v1.2.9 on Fri Apr  9 11:22:57 2004
    *filter
    :INPUT ACCEPT [0:0]
    :FORWARD ACCEPT [0:0]
    :OUTPUT ACCEPT [0:0]
    :RH-Firewall-1-INPUT - [0:0]
    -A INPUT -j RH-Firewall-1-INPUT
    -A FORWARD -j RH-Firewall-1-INPUT
    -A RH-Firewall-1-INPUT -i lo -j ACCEPT
    -A RH-Firewall-1-INPUT -p icmp -m icmp --icmp-type 255 -j ACCEPT
    -A RH-Firewall-1-INPUT -p esp -j ACCEPT
    -A RH-Firewall-1-INPUT -p ah -j ACCEPT
    -A RH-Firewall-1-INPUT -m state --state RELATED,ESTABLISHED -j ACCEPT
    -A RH-Firewall-1-INPUT -p tcp -m state --state NEW -m tcp --dport 80 -j ACCEPT
    -A RH-Firewall-1-INPUT -p tcp -m state --state NEW -m tcp --dport 21 -j ACCEPT
    -A RH-Firewall-1-INPUT -p tcp -m state --state NEW -m tcp --dport 22 -j ACCEPT
    -A RH-Firewall-1-INPUT -p tcp -m state --state NEW -m tcp --dport 23 -j ACCEPT
    -A RH-Firewall-1-INPUT -p tcp -m state --state NEW -m tcp --dport 25 -j ACCEPT
    -A RH-Firewall-1-INPUT -j REJECT --reject-with icmp-host-prohibited
    COMMIT
    # Completed on Fri Apr  9 11:22:57 2004
    

This corresponds to the following script:

    
    #!/bin/sh
    PATH=/sbin:$PATH
    
    iptables --flush
    iptables --delete-chain
    
    iptables --policy INPUT ACCEPT
    iptables --policy FORWARD ACCEPT
    iptables --policy OUTPUT ACCEPT
    
    iptables --new-chain firewall
    iptables -A INPUT -j firewall
    iptables -A FORWARD -j firewall
    
    iptables -A firewall -i lo -j ACCEPT
    iptables -A firewall -p icmp -m icmp --icmp-type 255 -j ACCEPT
    iptables -A firewall -p esp -j ACCEPT
    iptables -A firewall -p ah -j ACCEPT
    iptables -A firewall -m state --state RELATED,ESTABLISHED -j ACCEPT
    iptables -A firewall -p tcp -m state --state NEW -m tcp --dport 80 -j ACCEPT
    iptables -A firewall -p tcp -m state --state NEW -m tcp --dport 21 -j ACCEPT
    iptables -A firewall -p tcp -m state --state NEW -m tcp --dport 22 -j ACCEPT
    iptables -A firewall -p tcp -m state --state NEW -m tcp --dport 23 -j ACCEPT
    iptables -A firewall -p tcp -m state --state NEW -m tcp --dport 25 -j ACCEPT
    iptables -A firewall -j REJECT --reject-with icmp-host-prohibited
    
    ######################################################################
    

Before we go any further, let's understand what this script does.

    
    iptables --flush
    iptables --delete-chain
    

These two lines clear out the filter table, deleting all rules (`--flush`) and deleting all user-defined chains (`--delete-chain`). By default, `iptables` acts on the _filter_ table, which contains the INPUT, FORWARD, and OUTPUT chains.

    
    iptables --policy INPUT ACCEPT
    iptables --policy FORWARD ACCEPT
    iptables --policy OUTPUT ACCEPT
    

These three lines sets the default policy on these three chains. The policy is the like the last rule in the chain. In this case, if a packet reaches the end of the chain, it gets accepted.

    
    iptables --new-chain firewall
    iptables -A INPUT -j firewall
    iptables -A FORWARD -j firewall
    

These lines create a new chain (we can call it anything we like) and appends it to the INPUT and FORWARD chains. the “firewall” chain will have the duty to ACCEPT some good packets and DROP or REJECT all bad packets.

The firewall chain will have rules to ACCEPT all good packets. It will then REJECT everything else.

    
    iptables -A firewall -i lo -j ACCEPT
    

the lo interface is the loopback device. All packets that come from here originate on this computer, and can be assumed to be good.

    
    iptables -A firewall -p icmp -m icmp --icmp-type 255 -j ACCEPT
    

Internet Control Message Protocol (icmp) type packets are used for thins like `ping`. Mostly innocuous, until you get a denial-of-service attack. We let these through.

    
    iptables -A firewall -p esp -j ACCEPT
    iptables -A firewall -p ah -j ACCEPT
    

This is VPN stuff, according to the guys on #fedora. I don't fully understand.

    
    iptables -A firewall -m state --state RELATED,ESTABLISHED -j ACCEPT
    

If a packet is part of an established connection, let it through. This allows clients on this computer to talk to servers on the internet.

    
    iptables -A firewall -p tcp -m state --state NEW -m tcp --dport 80 -j ACCEPT
    iptables -A firewall -p tcp -m state --state NEW -m tcp --dport 21 -j ACCEPT
    iptables -A firewall -p tcp -m state --state NEW -m tcp --dport 22 -j ACCEPT
    iptables -A firewall -p tcp -m state --state NEW -m tcp --dport 23 -j ACCEPT
    iptables -A firewall -p tcp -m state --state NEW -m tcp --dport 25 -j ACCEPT
    

These are the five services we enabled with `redhat-config-securitylevel`.

<table class="p"><tbody><tr><th>name</th><th>port</th><th>aka</th></tr><tr><td>http</td><td>80</td><td>WorldWideWeb HTTP</td></tr><tr><td>ftp</td><td>21</td><td></td></tr><tr><td>ssh</td><td>22</td><td>SSH Remote Login Protocol</td></tr><tr><td>telnet</td><td>23</td><td></td></tr><tr><td>smtp</td><td>25</td><td>mail</td></tr></tbody></table>

    
    iptables -A firewall -j REJECT --reject-with icmp-host-prohibited
    

As we promised, we REJECT everything else.

* * *

### Changing things

Now that you understand what's going on, you can begin to make changes. I keep my firewall scripts in `/usr/local/sbin`. Begin by putting a script there that mimicks your preexisting setup. Compare iptables-save outputs to verify.

If you are happy with the results of a script, run

    /sbin/service iptables save

or

    /sbin/iptables-save > /etc/sysconfig/iptables

to save the current setup to `/etc/sysconfig/iptables`, where it will loaded upon next boot.

The first thing I did when editing my firewall script was to delete the “`-p esp`” and “`-p ah`” stuff that I don't use anyways.

Then I open whatever ports I want open. A CUPS print server needs port 631 (ipp service) open. BitTorrent likes to have tcp ports 6881 through 6889 open.

(ASIDE: You can choose another range of ports for BitTorrent. Use the `--minport` and `--maxport` options to set them. Some suggest that everybody choose a different random integer for their minport for security reasons. `perl -e 'print int(rand(65536-1024)+1024),"\n"'`)

Port 443 is used for https. Maybe a user wants to use port 12345 for a custom server.

Remember that “`-p tcp --dport`” only opens the tcp port. To open a udp port,

    
    iptables -A firewall -p udp -m state --state NEW -m udp --dport <PORT> -j ACCEPT
    

If you want to know how many packets go through your chains, use the

    /sbin/iptables -L -v

command to look at the counters.

Because I get so many packets aimed at 255.255.255.255, I explictly DROP them before I REJECT the rest:

    
    iptables -A firewall -d 255.255.255.255 -j DROP
    iptables -A firewall -j REJECT --reject-with icmp-host-prohibited
    

This gives me two seperate counters. It also allows me to politely reject only those packets which are directed at me, and ignore those that are broadcast to the world.

Another option is logging. This will fill up `/var/log/messages`.

    
    iptables -A firewall -d 255.255.255.255 \
             -m limit --limit 1/minute \
             -j LOG --log-prefix "DROPPED_PACKET " \
             --log-ip-options --log-tcp-options
    iptables -A firewall -d 255.255.255.255 -j DROP
    
    iptables -A firewall -m limit --limit 1/minute \
             -j LOG --log-prefix "REJECTED_PACKET " \
             --log-ip-options --log-tcp-options
    iptables -A firewall -j REJECT --reject-with icmp-host-prohibited
    

Logging will give you a better feel for what is going on with your firewall. Are you dropping packets you want to keep? What type of attacks are being tried? From what servers?

* * *

### Network address translation

Before you can route, you'll have to set the kernel variable `net.ipv4.ip_forward` to `1`. Do this by editing `/etc/sysctl.conf` and then rereading that file with “`/sbin/sysctl -p`”.

    
    ## line from /etc/sysctl.conf
    net.ipv4.ip_forward = 1
    

Here's an example bit of script that sets up the _nat_ table for routing:

    
    ############################################################
    IP_ADDR='XXX.XXX.XXX.XXX'
    WEB_SERVER='192.168.1.12'
    
    iptables -t nat --flush
    iptables -t nat --delete-chain
    
    iptables -t nat --policy PREROUTING ACCEPT
    iptables -t nat --policy POSTROUTING ACCEPT
    iptables -t nat --policy OUTPUT ACCEPT
    
    iptables -t nat -A POSTROUTING -o eth0 -j SNAT --to-source $IP_ADDR
    iptables -t nat -A PREROUTING  -i eth0 -d $IP_ADDR \
    	-p tcp -m tcp --dport 80 \
    	-j DNAT --to-destination $WEB_SERVER:80
    
    iptables -t filter --insert FORWARD -i eth1 -j ACCEPT
    ############################################################
    

This assumes that you have a static IP address. Here's what the network looks like.

![[network diagram for a nat]](/images/network_diagram_nat_2.png "made with DIA!")

eth1 is connected to a 192.168.0.0 network. Anything that's going out on the eth0 iterface needs to pretend its source IP address is $IP\_ADDR. That's done with POSTROUTING/SNAT. Anything trying to connect to port 80 on $IP\_ADDR needs to have it's destination IP changed to $WEB\_SERVER; this is done with PREROUTING/DNAT.

If you do not have a static IP address, you can't SNAT or DNAT, 'cause you don't know the address before hand. Yo will have to MASQUERADE, which is like SNAT, but smarter.

    
    #!/bin/sh
    ############################################################
    ## ${HOME}/bin/iptables-masquerade
    ############################################################
    OUT_IFACE="eth0"
    IN_IFACE="eth1"
    PATH=/sbin:$PATH
    iptables -t nat --flush
    iptables -t nat --delete-chain
    iptables -t nat --policy PREROUTING ACCEPT
    iptables -t nat --policy POSTROUTING ACCEPT
    iptables -t nat --policy OUTPUT ACCEPT
    iptables -t nat -A POSTROUTING -o ${OUT_IFACE} -j MASQUERADE
    iptables -t filter --insert FORWARD -i ${IN_IFACE} -j ACCEPT
    echo "DONE!"
    echo "remember: In '/etc/sysctl.conf', put the line"
    echo "    net.ipv4.ip_forward = 1"
    echo ""
    ############################################################
    

* * *

### Cleaning it up a bit:

Here's a slightly cleaner way of doing things.

    
    #!/bin/sh
    PATH=/sbin:$PATH
    
    iptables --flush
    iptables --delete-chain
    
    iptables --policy INPUT DROP
    iptables --policy FORWARD DROP
    iptables --policy OUTPUT ACCEPT
    
    iptables -A INPUT -i lo -j ACCEPT
    iptables -A INPUT -p icmp -m icmp --icmp-type 255 -j ACCEPT
    iptables -A INPUT -p esp -j ACCEPT
    iptables -A INPUT -p ah -j ACCEPT
    iptables -A INPUT -m state --state RELATED,ESTABLISHED -j ACCEPT
    iptables -A INPUT -p tcp -m state --state NEW -m tcp --dport 80 -j ACCEPT
    iptables -A INPUT -p tcp -m state --state NEW -m tcp --dport 21 -j ACCEPT
    iptables -A INPUT -p tcp -m state --state NEW -m tcp --dport 22 -j ACCEPT
    iptables -A INPUT -p tcp -m state --state NEW -m tcp --dport 23 -j ACCEPT
    iptables -A INPUT -p tcp -m state --state NEW -m tcp --dport 25 -j ACCEPT
    iptables -A INPUT -j REJECT --reject-with icmp-host-prohibited
    
    ######################################################################
    iptables -A FORWARD -m state --state RELATED,ESTABLISHED -j ACCEPT
    iptables -A FORWARD -i eth1 -j ACCEPT
    
    iptables -t nat --flush
    iptables -t nat --delete-chain
    
    iptables -t nat --policy PREROUTING ACCEPT
    iptables -t nat --policy POSTROUTING ACCEPT
    iptables -t nat --policy OUTPUT ACCEPT
    
    iptables -t nat -A POSTROUTING -o eth0 -j MASQUERADE
    ######################################################################
        

* * *

### Advanced topics

More topics I would like to cover:

*   REJECT targets
*   REJECT vs DROP
*   `-m limit` rate limiting

Topics I want to learn about:

*   DNATing ssh and hostkeys.
*   the _mangle_ table
*   statefull vs nonstateful packet filtering
*   Why are they targeting me?
*   Why do I get 255.255.255.255 packets anyways?

* * *

### Notice

This document (c) 2004 Hal Canary. Liscenced under a [Creative Commons Attribution-NoDerivs License](http://creativecommons.org/licenses/by-nd/1.0/).

Let me know if it is useful.

* * *

* * *

file modification time: 2006-07-16 15:38:08

* * *
