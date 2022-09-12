Howto Install IceWM under Fedora.
=================================

\[[background](/archives/000417.html)\]

1) Add the dag repo to yum:

    $ sudo rpm --import 'http://dag.wieers.com/packages/RPM-GPG-KEY.dag.txt'

Edit the file '/etc/yum.conf' and add these lines:

    [dag]
    name=Dag APT Repository
    baseurl=http://dag.freshrpms.net/fedora/$releasever/en/$basearch/dag/
      http://dag.atrpms.net/fedora/$releasever/en/$basearch/dag/
    enabled=0
    gpgcheck=1

2) Install:

    $ sudo yum -y --enablerepo dag install icewm

3) Switch your default:

    $ switchdesk icewm

4) (Optional) Config Files

    $ mkdir $HOME/.icewm
    $ cd /usr/share/icewm
    $ cp menu preferences keys toolbar $HOME/.icewm
    
    $ cd $HOME/.icewm
    $ touch startup 
    $ chmod +x startup
    
    $ echo 'gnome-terminal &' >> startup

Add to toolbar:

    prog "Gnome Terminal" xterm gnome-terminal
    prog "Mozilla Firefox" "/usr/lib/firefox-1.0/icons/mozicon16.xpm"
    firefox

Add to menu:

    prog "Mozilla Firefox"
      "/usr/lib/firefox-1.0/icons/mozicon16.xpm" firefox
    prog "Gnome Terminal" xterm gnome-terminal
    separator
    prog "Music Player" "" rhythmbox
    prog "Instant Message" "" gaim
    prog Gimp gimp gimp
    prog Azureus azureus azureus
    prog Emacs emacs emacs

To load changes:

    $ killall -HUP icewm

I just grabed a new theme. [TrueCurve](http://themes.freshmeat.net/projects/truecurve/).

    
    $ mkdir -p ~/.icewm/themes
    $ cd ~/.icewm/themes
    $ tar xzvf [wherever]/truecurve-default-1.0.4.tar.gz
    $ killall -HUP icewm
    

My preferences:

    
    KeyWinMaximizeVert="Alt+Ctrl+v"
    KeyWinMaximizeHoriz="Alt+Ctrl+h"
    KeyWinFullscreen="Alt+Ctrl+f"
    

My keys

    
    key "Alt+Ctrl+l"	/home/hal/bin/xss-lock
    key "Alt+Ctrl+a"	/home/hal/bin/xss-activate
    key "Alt+Ctrl+e"	/home/hal/bin/xss-exit
    key "Alt+Ctrl+s"	/home/hal/bin/screenshot
    key "Alt+Ctrl+p"	rhythmbox --play-pause
    key "Alt+Ctrl+n"	rhythmbox --next
    key "Alt+Ctrl+m"	emacs
    key "Shift+Ctrl+KP_Add"	aumix -w -10
    key "Shift+Ctrl+KP_Subtract"	aumix -w +10
    

* * *

file modification time: 2005-03-04 20:58:15

* * *
