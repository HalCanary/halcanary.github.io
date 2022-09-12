About my Photography
====================

[![[]](https://halcanary.org/photos/thumb/2002-08-25-curtis02.jpg)](/photos/2002-08-25-curtis02.jpg) Since August 2002, I've been using a Cannon Powershot A40. (Other users of this model include [Mark Chapman](http://nuclear.physics.wisc.edu/~chapman/pictures/) and [Wil Wheaton](http://wwdn.textamerica.com/).) From January 2000 to 2002, I used an APS camera, which I was unhappy with. I didn't think the film quality was as good as 35mm, even if the film canisters were convenient.

The A40 takes 1600x1200 photos. I store photos on a 512 MB CF card; I can hold about 800 jpegs on it. I have two adapter lenses for it: a wide angle and a telephoto. If I bought a new camera, I'd want one with improved optics (maybe an SLR) and more megapixels. Small cameras don't interest me.

I do all of my photo editing on my Linux desktop, using gphoto2 to transfer files off of the camera. ([Outdated info on gphoto2](/p/a40/)) I edit photos using the Gimp and ImageMagick.

My usual process it to dump the original 2 megapixel images in a $HOME/Photos/Originals/ directory, [prepend the date](/archives/000179.html), and rotate if necessary.

$HOME/bin/rotate-counter

* * *

    #!/bin/sh
    for x in "$@"; do 
        convert -rotate -90 $x tmp.jpg
        mv $x unrotated_$x
        mv tmp.jpg $x
    done

* * *

and

$HOME/bin/rotate-clock

* * *

    #!/bin/sh
    for x in "$@"; do 
        convert -rotate 90 $x tmp.jpg
        mv $x unrotated_$x
        mv tmp.jpg $x
    done

* * *

Then I copy the files into a working directory and make whatever changes I want to make before publishing to the web. Many photos don't make it; about 30%--50% do. At this point I'll crop and resize. If the photo is poor quality, I'll scale it down to 800x600. If it is okay, I'll only scale it to 1024x768. If it is excellent, I'll leave it at full resolution.

I usually crop and resize with [the Gimp](http://gimp.org). If no cropping is necessary, I'll just let imagemagick to do the work:

$HOME/bin/resize-1024

* * *

    #!/bin/sh
    SIZE="1024x1024"
    for x in "$@"; do 
        convert -size $SIZE $x \
            -resize $SIZE tmp.jpg
        mv $x fullsize_$x
        mv tmp.jpg $x
    done

* * *

Next, I make a thumbnail. Again, imagemagick's convert is your friend:

$HOME/bin/thumb

* * *

    #!/bin/sh
    if [ "$#" -lt 1 ]; then
        echo "Usage: thumb sourcefile[s]";
        exit 1
    fi
    PREFIX="/photos/"
    THUMB_PREFIX="thumb/"
    for file in "$@" ; do 
        OUT=$THUMB_PREFIX$file
        convert -size 120x120 $file \
    	-resize 120x120 +profile "*" $OUT
        GEOM=`identify -format 'width="%w" height="%h"' $OUT`
        echo "<p>"
        echo "  <a href=\"$PREFIX$file\">"
        echo "    <img $GEOM alt=\"[Thumb]\" src=\"$PREFIX$OUT\" />"
        echo "  </a>"
        echo "</p>"
    done

* * *

If I want to print a photo to hang on the wall, I'll go back to the original image and crop out a 8:10 (1200x1500) portion. Once I get a enough to bother, I'll burn those images onto a CD and take it to the grocery store for development. I usually print out 8"x10"s. At 2 megapixels, 8x10 is about as big as you can go. 8x10s cost less than $2 at Woodmans or Walmart; Walgreens will charge you $4.

* * *

file modification time: 2004-07-15 20:30:12

* * *
