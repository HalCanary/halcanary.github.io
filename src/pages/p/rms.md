rms
===

"`rm`, safely." I also think it's funny that I've named my reimplementation of the M$ recycling bin after RMS.

$HOME/bin/rms

* * *

    
    #!/bin/sh
    
    ## rms
    ##   "rm, safely." I also think it's funny
    ##   that I've named my reimplementation
    ##   of the MSFT recycling bin after RMS.
    ##
    ## (c) 2000-2003 Hal Canary
    ##
    ## License:
    ##   This is free software, see
    ##   http://www.gnu.org/licenses/gpl.txt
    ##   This product is distributed
    ##   WITHOUT ANY WARRANTY of any kind.
    
    TRASH="/tmp/$USER/trash";
    
    if [ "$#" -lt 1 ]; then
        echo "Usage: $0 FILE...";
        echo "Move FILE(s) to $TRASH.";
        exit 1;
    fi
    
    [ -d "$TRASH" ] || mkdir -p "$TRASH";
    
    for file in "$@"; do
        if [ -f "$file" ]; then
    	mv -f "$file" "$TRASH"
    	echo "$file -> $TRASH"
        else
    	echo "$file doesn't exist!"
        fi
    done
    exit 0;
    
    

* * *

* * *

file modification time: 2003-12-08 21:46:29

* * *
