image-upload.php
================

    
    <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN">
    <html>
      <!-- 
        (c) 2004 Hal Canary, hal at ups dot physics dot wisc dot edu
    
        Licence Information:
    
        This program is free software; you can redistribute it and/or
        modify it under the terms of version 2 of the GNU General
        Public License as published by the Free Software Foundation.
    
        A copy of the licence can be found at:
    	http://www.gnu.org/licenses/gpl.txt
    
        This program is distributed in the hope that it will be
        useful, but WITHOUT ANY WARRANTY; without even the implied
        warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
        PURPOSE.  See the GNU General Public License for more details.
      -->
      <head>
        <title>Image Upload</title>
      </head>
      <body>
        <h1>Image File Upload</h1>
        <hr />
        <div>
          <form action="image-upload.php" method="post" 
    	    enctype="multipart/form-data">
    	<p>
    	  Upload a file: <br />
    	  <input type="hidden" name="MAX_FILE_SIZE" value="1048576" />
    	  <input type="file" name="userfile" value="" />
    	</p>
    	<p>
    	  Alternate Filename (optional) (Dont forget extent.ion!):<br />
    	  <input type="text" name="filename" value="" />
    	</p>
    	<p>
    	  Maximum size to resize to:<br />
    	  <select name="resize">
    	    <option>320</option>
    	    <option>640</option>
    	    <option>800</option>
    	    <option selected="selected">1024</option>
    	    <option>1280</option>
    	    <option>1600</option>
    	    <option>3200</option>
    	  </select>
    	</p>
    	<p>
    	  <input type="submit" value="Submit File" />
    	</p>
          </form>
        </div>
        <hr />
        <?php
    if ($_FILES['userfile']['size'] != 0) {
        $resize=$_POST['resize'];
        $size1="${resize}x${resize}\>"; /* The "\>" says only downsize. */
        $size0="120x120\>";
        $dir_l="/home/hal/public_html/images";  /*Make sure chmod 777*/
        $dir_e="/~hal/images";
    
        $filename = $_FILES['userfile']['name'];
        if ( $_POST['filename'] != "") {
    	$filename = $_POST['filename'];
        }
        $filename = str_replace(" ", "_", $filename);
        
        print "<p>\n";
        $f=escapeshellarg($_FILES['userfile']['tmp_name']);
        system("file -b $f");
        $filetype=trim( `file -bi $f` );
        print "<br /> $filetype";
        print "\n</p>\n";
    
        if (!strstr($filetype, "image" )) {
    	print "<p>Is not an image file, sorry.</p><hr />";
        } else {
    system("convert -size $size1 $f -resize $size1 ${dir_l}/${filename}");
    system("convert -size $size0 $f -resize $size0 ${dir_l}/thumb_${filename}");
    
    $geom=trim(`identify -format 'width="%w" height="%h"' ${dir_l}/thumb_${filename}`);
    ?>
        <p>
          <a href="<?php echo "${dir_e}/${filename}" ?>">
    	<img src="<?php echo "${dir_e}/thumb_${filename}" ?>" alt="[thumb]" 
    	     <?php echo $geom ?> />
          </a>
        </p>
        <p>
          &lt;a href=&quot;<?php echo "${dir_e}/${filename}" ?>&quot;&gt;<br />
          &lt;img src=&quot;<?php echo "${dir_e}/thumb_${filename}" ?>&quot;<br />
          alt=&quot;[thumb]&quot; <?php echo $geom ?> /&gt;<br />
          &lt;/a&gt;
        </p>
        <hr />
        <?php } } ?>
      </body>
    </html>
    

* * *

<div class="rightside"><em>file modification time: 2004-11-29 00:40:55</em></div>

