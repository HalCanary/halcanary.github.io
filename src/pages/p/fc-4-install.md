FC4 Install Notes (Workstation)
===============================

### Pre-install

**FILESYSTEM**

This is how I set up my hard drive. I planned on formatting every partition except for /home. /home also contained a copy of the four CDs, aquired from a torrent. FC4-i386-disc{1,2,3,4}.iso. I only burnt the first CD. The others could live on disc. The advantage of copying the packages from HDD to HDD is that it is MUCH faster. Use “linux askmethod” at the boot prompt to make this work.

    
    Filesystem	1M-blocks	Mounted on
    /dev/hdb1	      190	/boot
    /dev/hdb2	     9286	/
    /dev/hdb3	   101147	/home
    /dev/hdb4	    ~2048	[SWAP]
    /dev/hda1 (NTFS)		[not mounted]
    /dev/hda2 (VFAT)    19040	/d		
    

**ETC AND VAR**

You may copy /etc and /var to /home

    
    sudp cp -a /etc /home/etc-$(date +%Y-%m-%d)
    sudp cp -a /var /home/var-$(date +%Y-%m-%d)
    

Minimalist method: keep useful files in /home/ETCETERA (hosts dhcpd.conf ssh-\_host\*key\*, sshd\_config, iptables-script).

* * *

### Install

only wipe hdb1, hdb2, and hdb4.

Install packages....

* * *

### Post-install

**/HOME**

As root on the console, set up /home partition

    
    echo '/dev/hdb3 /home ext3 defaults 1 2' >> /etc/fstab
    mv /home /OLD.home
    mkdir /home
    mount /home/
    

**SUDOERS**

Add hal to wheel group (/etc/group). Uncomment wheel group in sudoers file (/etc/sudoers).

**REPOS**

Add these files to /etc/yum.repos.d/. Notice that I enable freshrpms this time. They have the best package selection. And they now complement Fedora Extras just like livna has always done.

    
    ## /etc/yum.repos.d/freshrpms.repo
    [freshrpms]
    name=FreshRPMs Fedora Compatible Packages
    mirrorlist=http://ayo.freshrpms.net/fedora/linux/$releasever/mirrors-freshrpms
    enabled=1
    gpgkey=http://freshrpms.net/RPM-GPG-KEY-freshrpms
    gpgcheck=1
    

**INSTALL PACKAGES**

Start by updating files.

    yum -y update

Find things to install witrh YUM. Check out availible software in each directory listing.

    yum install packages

[Fedora Core](http://download.fedora.redhat.com/pub/fedora/linux/core/4/i386/os/Fedora/RPMS/) - switchdesk-gui dhcp

[Fedora Extras](http://download.fedora.redhat.com/pub/fedora/linux/extras/4/i386/) - bash-completion bittorrent WindowMaker blackbox fluxbox xfce\* lyx gqview id3lib cpan2rpm

[Freshrpms](http://ayo.ie.freshrpms.net/fedora/linux/4/i386/freshrpms/RPMS/) - gstreamer-plugins-extra-audio gstreamer-plugins-extra-dvd gstreamer-plugins-extra-video mplayer unrar gtkpod

**MPLAYER TWEAKS**

Add line “dev.rtc.max-user-freq = 1024” in /etc/sysctl.conf. Run “sysctl -p”.

Edit $HOME/.asoundrc to make sound work nicely for mplayer.

    
    ### user's .asoundrc ###
    pcm.dsp0 {
        type plug
        #slave.pcm "dmix"
        slave.pcm "hw:0"
    }
    ctl.mixer0 {
        type hw
        card 0
    }
    

Edit mplayer config to use fake oss device

    
    ### user's .mplayer/config ###
    ao=oss
    ontop=yes
    

**JAVA JDK AND AZUREUS**

Install jdk-1\_5\_0-linux-i586.rpm, which lives in my packages directory. /usr/bin/java is still used by java-1.4.2-gcj-compat; jdk will only install in /usr/java/jdk1.5.0/

Azureus can be unpackaged to /usr/local/azureus. Then start it with this script:

    
    #!/bin/sh
    ###$HOME/bin/azureus
    export JAVA_HOME="/usr/java/jdk1.5.0"
    export PATH=${JAVA_HOME}/bin:${PATH}
    exec /usr/local/azureus/azureus
    

**SSH**

Grab my old SSHD keys. Tighten sshd security:

    cd /etc/ssh 
    /sbin/service sshd stop
    rename key key.OLD *_key
    rename pub pub.OLD *.pub
    cp -a /home/ETCETERA/ssh_host_*key* .
    echo '## MODIFICATIONS HWC' >> sshd_config
    echo 'PermitRootLogin no' >> sshd_config
    echo 'AllowUsers hal' >> sshd_config
    /sbin/service sshd start
    

**MACROMEDIA FLASH**

Go to [RSLUG's page](http://ruslug.rutgers.edu/macromedia/) and get the appropriate RPM.

**DHCPD**

    
    cp -p /home/ETCETERA/dhcpd.conf /etc/
    /sbin/chkconfig dhcpd on
    /sbin/service dhcpd start
    

I am having problems with dhcpd. More later.

* * *

<div class="rightside"><em>file modification time: 2005-06-18 13:34:08</em></div>
