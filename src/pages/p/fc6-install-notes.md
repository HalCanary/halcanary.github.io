Fedora Core 6 Install Notes
===========================

This is mostly for my own reference.

### Before I Install

Get current information from `df` and `fdisk -l` .

    
    ----------------------------------------------------------------------
    Disk /dev/hda: 203.9 GB, 203928109056 bytes
    255 heads, 63 sectors/track, 24792 cylinders
    Units = cylinders of 16065 * 512 = 8225280 bytes
    
       Device Boot      Start         End      Blocks   Id  System
    /dev/hda1   *           1        6374    51199123+   7  HPFS/NTFS
    /dev/hda2            6375       24792   147942585    c  W95 FAT32 LBA
    
    ----------------------------------------------------------------------
    Disk /dev/hdb: 120.0 GB, 120060444672 bytes
    255 heads, 63 sectors/track, 14596 cylinders
    Units = cylinders of 16065 * 512 = 8225280 bytes
    
       Device Boot      Start         End      Blocks   Id  System
    /dev/hdb1   *           1          25      200781   83  Linux
    /dev/hdb2              26        1247     9815715   83  Linux
    /dev/hdb3            1248       14347   105225748   83  Linux
    /dev/hdb4           14348       14596     2000092   82  Linux swap
    
    ----------------------------------------------------------------------
    

    
    Filesystem 1M-blocks Mounted on
    ------------------------------------
    /dev/hda1            NTFS partition
    /dev/hda2     144405 /d
    ------------------------------------
    /dev/hdb1        190 /boot
    /dev/hdb2       9286 /
    /dev/hdb3     101147 /home
    /dev/hdb4            SWAP
    ------------------------------------
    

Tar up /etc to grab specific config files later.

    sudo tar czf /home/etc-$(date +%Y-%m-%d).tgz /etc

As before, hardlink the iso of the install disc\[s\] into /home.

    cd /home/files/Downloads/Zod-dvd-i386/
    ln FC-6-i386-DVD.iso /home/

Then you can use

    linux askmethod

to install from the HDD. This tends to be faster than installing from CD and less error-prone.

### Post-Install

Log in tty1 as root:

Set up sudoers file

    EDITOR=nano /usr/sbin/visudo
    	hal ALL=(ALL) ALL
    	hal ALL=(ALL) NOPASSWD: /usr/bin/cdrecord
    

Set up /home partition.

    mv /home /old.home
    mkdir /home
    nano /etc/fstab
    	/dev/hdb3 /home ext3 defaults 1 2
    mount /home

Login X11 as normal

Remove "`quiet rhgb`" from kernel options.

    sudo nano /etc/grub.conf

Install packages.

    sudo yum -y update
    
    sudo yum -y install gstreamer-ffmpeg gstreamer-plugins-ugly
    sudo yum -y install gconf-editor
    sudo yum -y install mplayer
    sudo yum -y install bittorrent bittorrent-gui
    sudo yum -y install alltray
    sudo yum -y install inkscape emacs
    sudo yum -y install fortune-mod fortune-firefly
    sudo yum -y install fonts-japanese fonts-chinese fonts-korean
    
    sudo yum -y install bash-completion id3lib id3v2

Install Macromedia Flash.

    sudo wget -O /etc/yum.repos.d/macromedia-i386.repo \
    	http://macromedia.mplug.org/macromedia-i386.repo
    sudo mkdir -p /usr/X11R6/lib/X11/fs/
    sudo ln -s /etc/X11/fs/config /usr/X11R6/lib/X11/fs/config
    sudo yum -y install flash-plugin

Why do I need to do this?

    sudo yum remove totem-mozplugin

Config things.

    sudo system-config-printer
    sudo system-config-network
    sudo system-config-display
    sudo system-config-securitylevel
    	Add port tcp/59147

Remove unnecessary services

    sudo /sbin/chkconfig autofs off
    sudo /sbin/chkconfig apmd off
    sudo /sbin/chkconfig bluetooth off
    sudo /sbin/chkconfig hidd off
    sudo /sbin/chkconfig cpuspeed off
    sudo /sbin/chkconfig firstboot off
    sudo /sbin/chkconfig gpm off
    sudo /sbin/chkconfig irqbalance off
    sudo /sbin/chkconfig isdn off
    sudo /sbin/chkconfig mdmonitor off
    sudo /sbin/chkconfig netfs off
    sudo /sbin/chkconfig nfslock off
    sudo /sbin/chkconfig pcscd off
    sudo /sbin/chkconfig portmap off
    sudo /sbin/chkconfig rpcgssd off
    sudo /sbin/chkconfig rpcidmapd off

Add guest account

    sudo system-config-users

Modify SSH Daemon Configuration.

    sudo /sbin/service sshd stop
    sudo nano /etc/ssh/sshd_config
    	PermitRootLogin no
    	DenyUsers guest
    	Banner /etc/issue.ssh

assume we kept a copy of /etc/ssh/ssh\_host\_\* in a tarball somewhere.

    sudo rename ssh_host OLD_ssh_host /etc/ssh/ssh_host_*
    sudo cp --preserve=mode,timestamps ssh_host_* /etc/ssh/

    cd /etc/ssh/
    su -c 'for x in ssh*.pub;do ssh-keygen -l -f $x >>/etc/issue.ssh;done'
    cd -

Edit `/etc/issue.ssh` to look nice. Add line:

    ===== Unauthorized Access Prohibited =====

Restart the daemon

    sudo /sbin/service sshd start

![[]](/images/fc6-screenshot-cube.jpg)

* * *

<div class="rightside"><em>file modification time: 2006-10-31 19:16:23</em></div>

