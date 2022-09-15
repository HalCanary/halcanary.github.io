“Hello World,” in many languages.
=================================

I use this as a test for installing compilers on a a new machine

### c

hello.c

* * *

    
    /*DTPD*/
    #include <stdio.h>
    #include <stdlib.h>
    
    int main(void) {
      printf("hello world\n");
      return 0;
    }
    

* * *

### c++

hello.cpp

* * *

    
    /*DTPD*/
    #include <iostream.h>
    
    int main(int argc,char *argv[]) {
      cout << "hello world\n";
      return 0;
    }
    

* * *

### java

hello.java

* * *

    
    /*DTPD*/
    /**  
     * Hello World
     **/
    import java.io.PrintStream;
    import java.io.BufferedReader;
    import java.io.InputStreamReader;
    
    public class hello 
    {
        static PrintStream cout = System.out ;
        static PrintStream cerr = System.err ;
        static BufferedReader cin =
            new BufferedReader(new InputStreamReader(System.in));
        public static void main(String[] args) 
        {
            cout.print("hello world\n");
        }
    }
    

* * *

### perl

hello.pl

* * *

    
    #!/cygdrive/d/perl/bin/perl
    ## hello world test script.
    #DTPD#
    print "hello world \n";
    

* * *

### python

hello.py

* * *

    
    #!/usr/bin/python
    #DTPD#
    print "hello world"
    

* * *

### scheme

hello.scm

* * *

    
    ":"; exec /bin/guile.exe -s $0 "$@" 
    ":"; exec /cygdrive/d/scheme/MzScheme.exe -r $0 "$@" 
    ;DTPD;
    ;; hello world testing program
    (begin 
      (display "hello world")
      (newline))
    

* * *

### shell script

hello.sh

* * *

    
    #!/bin/bash
    ## hello world test script.
    #DTPD#
    echo "hello world"
    

* * *

<div class="rightside"><em>file modification time: 2003-12-08 00:50:23</em></div>

