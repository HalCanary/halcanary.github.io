Backup Directory Hierarchy To CDR/DVDR.
=======================================

I use this on my /home/music directory, which is 23 GiB.

    
    #!/usr/bin/perl
    
    ##   Backup Hierarchy.
    ##
    ##	Version:  backup-hierarchy.pl v2005-06-24.
    ##
    ##	Description:  backup a directory hierarchy to a set 
    ##	of CDR/DVDRs.
    ##
    ##   Copyright (c) 2005 Hal Canary.
    ##
    ##	hal at ups dot physics dot wisc dot edu.
    ##
    ##   Licence Information:
    ##
    ##	This program is free software; you can redistribute it
    ##	and/or modify it under the terms of version 2 of the
    ##	GNU General Public License as published by the Free
    ##	Software Foundation.
    ##
    ##	A copy of the licence can be found at:
    ##			http://www.gnu.org/licenses/gpl.txt
    ##
    ##	This program is distributed in the hope that it will
    ##	be useful, but WITHOUT ANY WARRANTY; without even the
    ##	implied warranty of MERCHANTABILITY or FITNESS FOR A
    ##	PARTICULAR PURPOSE.  See the GNU General Public
    ##	License for more details.
    ##
    ##   TO DO: Limit symlink filename length to 64 chars.
    ##
    ##   How to use:
    ##	Change the $backupdir, $startdir, and $originaldir
    ##	variables.  Maybe also change the $discsize variable.
    ##
    ##	After running the program, copy or symlink INDEX.txt
    ##	into each directory.  Now you can use a single disc to
    ##	look up files on ALL discs.
    ##
    ##	If you want to preserve permissions and such, tar up
    ##	the files before burning.  "tar --dereference" will
    ##	dereference those symlinks.  For backing up music and
    ##	video this is most likey unnessisary.
    ##
    ##	mkisofs -V "MUSIC 001" -r -J -f -quiet \
    ##		-o MUSIC_001.iso MUSIC_001/
    ##	cdrecord -v -pad -eject MUSIC_001.iso
    
    my $backupdir = "/home/hal/MUSIC_BACKUP"; #abs
    my $backupname = "MUSIC" ;
    my $startdir = "/home"; #abs
    my $originaldir = "music"; #rel
    mkdir $backupdir;
    #$discsize = 700*1024*1024; # Bytes for CD-R
    my $discsize = 4.7*1000*1000*1000; # Bytes for DVD-R
    
    my $num=1;
    my $dirnum = &padnum($num);
    my $disc = $backupdir."/".$backupname."_".$dirnum;
    my $cursize = 0;
    mkdir $disc;
    
    chdir $startdir ;
    &dealwithdir($originaldir);
    
    chdir $backupdir ;
    system ("/bin/ls -shpR . > INDEX.txt");
    
    sub dealwithdir {
        my $dir=shift;
        mkdir $disc."/".$dir ;
        opendir(DH, $dir);
        my @contents = readdir(DH);
        closedir(DH);    #print($dir."\n");
        foreach my $file (@contents) {
    	if (-f $dir . "/" . $file ) {
    	    dealwithfile($dir, $file);
    	} elsif ( -d $dir . "/" . $file ) {
    	    &dealwithdir ($dir . "/" . $file)
    		unless ($file eq "."
    			or $file eq "..");
    	}
        }
    }
    
    sub dealwithfile {
        my $dir = shift;
        my $file = shift;
        my $size = (stat($dir."/".$file))[7] ;
        $cursize += $size;
        if ($cursize > $discsize) {
    	$num++;
     	$dirnum = &padnum($num);
    	$cursize = $size ;
    	$disc = $backupdir."/"
    	    .$backupname."_".$dirnum;
    	mkdir $disc;
    	system( "mkdir -p ".$disc."/".$dir );
        }
        my $bdir = $disc."/".$dir ;
        symlink( $startdir."/".$dir."/".$file ,
    	     $bdir."/".$file )
    	or die "HELP";
    }
    
    sub padnum {
        $num=shift;
        return "00".$num if $num%lt;10;
        return "0".$num if $num%lt;100;
        return "$num";
    }
    

* * *

file modification time: 2005-06-24 16:44:06

* * *
