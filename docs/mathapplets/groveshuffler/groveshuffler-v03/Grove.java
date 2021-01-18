/*
    Grove Shuffler Applet
    Copyright (C) 2001-2003  
      Hal Canary, Univerity of Wisconsin-Madison (hal@ups.physics.wisc.edu)
      Kyle Petersen, Brandeis University (tkpeters@brandeis.edu)

    A description of the algorithm can be found in the file index.html

    Licence Information:

	This program is free software; you can redistribute it and/or
	modify it under the terms of version 2 of the GNU General
	Public License as published by the Free Software Foundation.

	A copy of the liscence was distributed in the file LICENCE.txt

	This program is distributed in the hope that it will be
	useful, but WITHOUT ANY WARRANTY; without even the implied
	warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
	PURPOSE.  See the GNU General Public License for more details.

    See the README.txt file for version information,
*/
/* A description
      (r,c)                  (0,0)
       T  T  T  T             D  A  C  C
         S  S  S                d  c  c
          T  T  T                D  G  A
            S  S       e.g,        g  f
             T  T                   A  B
               S                      b
                T                      B
                                      (2n-2,2n-2)
                
     Downward-pointing (T)          Upward-pointing (S)

                                      e        f       g
     A =  *   *            --->       *        *       *
                           --->        \      /       / \
            *              --->     *   *    *   *   *   *


        B =  *---*                   b =   *
                           --->              
               *                          *---*


     C =  *   *                      c =   *
           \               --->              \
            *                             *   *


        D =  *   *                   d =   *
                /          --->            /
               *                          *   *


    E       F       G
  *---*   *---*   *   *              a =   *
   \         /     \ /     --->      
    *       *       *                     *   *
  
 */
import java.awt.Graphics;
import java.awt.Color;
/**
 * A Grove. A collection of trees on the size n triangular lattice graph.
 **/
public class Grove extends MathSystem {
    /** Current Iteration Number  **/
    public int n; 

    private static int INITIAL_SIZE = 40; //initial array size
    //40^2 = 1600 B ~= 1.6 kB  char = 1 Byte = 1 B
    private int arraySize; //Current array size.
    private char [] [] array;  // the array of triangles
    private char [] [] tArray;  // the array of triangles
    private int subit; //subiteratyion 0=>just moved/filled 1=>just anililated
    Color c1;
    Color c2;
    Color c3;

    /**
     * The Constructor
     **/ 
    public Grove() {  
	c1 = new Color(255,63,63); 
	c2 = Color.green;
	c3 = new Color(127,127,255); 
	this.reset();
    }
    /**
     * print()  prints a representaion of the system 
     * to cout.  Can be used for debugging.
     **/ 
    public void print() {
	for(int r = 0 ; r < 2*n+1 ; r++) {
	    for(int c = 0 ; c < 2*n+1 ; c++) {
		System.out.print(array[r][c]);
		System.out.print(' ');
	    }
	    System.out.print('\n');
	}
	System.out.print('\n');
    }

    /**
     * draw() 
     *
     * Draws itself on the graphics buffer.
     *
     * @param sizeX is the x size of region on which to draw.
     * @param sizeY is the y size of region on which to draw.
     **/
    public void draw(int sizeX, int sizeY, Graphics gb) {
	gb.setColor(Color.white);
	gb.fillRect(0,0,sizeX,sizeY) ;
	//gb.clearRect(0,0,sizeX,sizeY) ;
	gb.setColor(Color.black);
	gb.drawRect(0,0,sizeX-1,sizeY-1);
	int lx = 10;	// lx is length of each edge
	int ly = (int) (lx * java.lang.Math.sqrt(3));
		// ly is roughly sqrt(3)/2*lx
	if (lx*n*2 > sizeX) { 
	    lx = (int) (sizeX / 2 / n); 
	    ly = (int) (lx * java.lang.Math.sqrt(3));
	}
	if (lx < 1) { lx = 1 ; }
        if (ly < 2) { ly = 2 ; }
	int xoffset = (int)((sizeX - (2*n*lx))/2);
	int yoffset = (int)((java.lang.Math.sqrt(3)/6)*(sizeX-2*n*lx))  ;
	//gb.setColor(Color.black);
	//gb.drawLine(xoffset, yoffset, xoffset+2*n*lx, yoffset);
	//gb.drawLine(xoffset, yoffset, xoffset+n*lx, yoffset+n*ly);
	//gb.drawLine(xoffset+2*n*lx, yoffset, xoffset+n*lx, yoffset+n*ly);
        for( int r = 0 ; r < n ; r++) {
	    for( int c = 0 ; c < n ; c++) {
		if (array[2*r][2*c] == 'B' ) {
		    gb.setColor(c1);
		    thickLine(lx,gb, (2*c - r)*lx + xoffset, r*ly + yoffset ,
				(2*c - r+2)*lx + xoffset , r*ly + yoffset );
                }
		else if (array[2*r][2*c] == 'C') {
		    gb.setColor(c2);
		    thickLine(lx,gb, (2*c - r)*lx + xoffset, r*ly + yoffset ,
				(2*c - r+1)*lx + xoffset, (r+1)*ly + yoffset);
                }
		else if (array[2*r][2*c] == 'D') {
		    gb.setColor(c3);
		    thickLine(lx,gb, (2*c - r+2)*lx +xoffset , r*ly + yoffset ,
				(2*c - r+1)*lx +xoffset, (r+1)*ly + yoffset );
		}
		else if (array[2*r][2*c] == 'E') {
		    gb.setColor(c1);
		    thickLine(lx,gb, (2*c - r)*lx +xoffset , r*ly + yoffset,
				(2*c - r+2)*lx +xoffset , r*ly + yoffset);
		    gb.setColor(c2);
                    thickLine(lx,gb, (2*c - r)*lx +xoffset , r*ly +yoffset ,
				(2*c - r+1)*lx +xoffset , (r+1)*ly +yoffset);
		}
                else if (array[2*r][2*c] == 'F') {
		    gb.setColor(c1);
		    thickLine(lx,gb, (2*c - r)*lx +xoffset , r*ly +yoffset,
				(2*c - r+2)*lx +xoffset , r*ly +yoffset);
		    gb.setColor(c3);
                    thickLine(lx,gb, (2*c - r+2)*lx +xoffset, r*ly + yoffset ,
				(2*c - r+1)*lx +xoffset , (r+1)*ly +yoffset);
		}
                else if (array[2*r][2*c] == 'G') {
		    gb.setColor(c3);
		    thickLine(lx,gb, (2*c - r+2)*lx +xoffset , r*ly +yoffset,
				(2*c - r+1)*lx +xoffset, (r+1)*ly +yoffset);
		    gb.setColor(c2);
                    thickLine(lx,gb, (2*c - r)*lx +xoffset , r*ly +yoffset,
				(2*c - r +1)*lx +xoffset , (r+1)*ly +yoffset );
		}
	    }
	}
	return;
    }
    /** Iterates the system. **/
    public void iterate(int iterations) {
	if ( subit == 0 ) { annihilate(); } 
	move();
	for (int i = 1; i<iterations;i++) { //only hit this loop if >1.
	    annihilate();
	    move();
	}
    }

    /** Does a subiteration, if applicable. **/
    public void subiterate(int subiterations) {
	for (int i = 0; i<subiterations;i++) {
	    if ( subit == 0 ) { annihilate(); } 
	    else { move(); }
	}
	return;
    }

    /** Restarts the system **/
    public void reset() {
	n = 1;
	arraySize = INITIAL_SIZE;
	array = new char [arraySize] [arraySize];
	tArray = new char [arraySize] [arraySize];
	for(int r = 0 ; r < 2*n+1 ; r++) {
	    for(int c = 0 ; c < 2*n+1 ; c++) {
		array[r][c] = ' ' ;
	    }
	}
	array[0][0] = 'A';
	iterate(1);
    }

    private void resize(){
	int oldSize = arraySize;
	arraySize = arraySize * 2;
	char [] [] array2 = new char [arraySize] [arraySize];
	for(int r = 0 ; r < oldSize ; r++) {
	    for(int c = 0 ; c < oldSize ; c++) {
		array2[r][c] = array[r][c];
	    }
	}
	array = array2;
	array2 = null;
	tArray = new char [arraySize] [arraySize];
	System.gc();
	return;
    }





    /********************************************************/


    /**     **/
    private void annihilate() {
	for(int r = 0 ; r < 2*n-1 ; r++) {
	    for(int c = 0 ; c < 2*n-1 ; c++) {
		if ( (array[r][c] == 'E') ||
		     (array[r][c] == 'F') ||
                     (array[r][c] == 'G') ) {
		    array[r][c] = 'a';   
                }
	    }
	}
	subit = 1;
	return;
    }

    /**     **/
    private void move() {
	// bigger in r and c direction!!
	if ((2*n)+3 >= arraySize){
	    resize();
	}   
	// clear out temporary array.
	for(int r = 0 ; r < 2*n+1 ; r++) {
	    for(int c = 0 ; c < 2*n+1 ; c++) {
		tArray[r][c] = ' ' ;
	    }
	}
	//copy system from array to tarray.
	for(int r = 0 ; r < n ; r++) {
	    for(int c = r ; c < n ; c++) {
		if (array[2*r][2*c] == 'A' ) {
                    //choose a number 0,1,2
		    int coine = (int) java.lang.Math.floor(3
			* java.lang.Math.random()) ;
		    if (coine == 0) {
			tArray[2*r+1][2*c+1] = 'e' ;
                    }
                    else if (coine == 1) {
			tArray[2*r+1][2*c+1] = 'f';
                    }
                    else if (coine == 2) {
			tArray[2*r+1][2*c+1] = 'g';
                    }
		}
                else if (array[2*r][2*c] == 'B' ) {
		    tArray[2*r+1][2*c+1] = 'b' ;
		}
                else if (array[2*r][2*c] == 'C' ) {
		    tArray[2*r+1][2*c+1] = 'c' ;
		}
                else if (array[2*r][2*c] == 'D' ) {
		    tArray[2*r+1][2*c+1] = 'd' ;
		}
                else if (array[2*r][2*c] == 'a' ) {
		    tArray[2*r+1][2*c+1] = 'a' ;
		}
	    }
	}
	this.n++;
	//then, set array = tArray;
	//use a circular switch-reference so as to conserve allocated memory.
	char [][] temp = tArray;
	tArray = array;
	array = temp;
	temp = null;

        //top left corner
	if ( (array[1][1] == 'd' ) ||
	     (array[1][1] == 'f' ) ||
             (array[1][1] == 'g' ) ) {
	    array[0][0] = 'D';
        }
        else array[0][0] = 'A';
        //top right corner
        if ( (array[1][2*n-3] == 'c' ) || 
             (array[1][2*n-3] == 'e' ) ||
             (array[1][2*n-3] == 'g' ) ) {
            array[0][2*n-2] = 'C' ;
        }
        else array[0][2*n-2] = 'A' ;
        //bottom corner
        if ( (array[2*n-3][2*n-3] == 'b' ) || 
             (array[2*n-3][2*n-3] == 'e' ) ||
             (array[2*n-3][2*n-3] == 'f' ) ) {
            array[2*n-2][2*n-2] = 'B' ;
        }
        else array[2*n-2][2*n-2] = 'A' ;
        //top row minus corners
        for (int c = 0 ; c < n-2 ; c++) {
            if ( ( (array[1][2*c+1] == 'a' ) ||
                   (array[1][2*c+1] == 'b' ) ||
                   (array[1][2*c+1] == 'd' ) ||
                   (array[1][2*c+1] == 'f' ) ) &&
                 ( (array[1][2*c+3] == 'd' ) ||
                   (array[1][2*c+3] == 'f' ) ||
                   (array[1][2*c+3] == 'g' ) ) ) {
		array[0][2*c+2] = 'D' ;
            }
            else if ( ( (array[1][2*c+1] == 'a' ) ||
			(array[1][2*c+1] == 'b' ) ||
			(array[1][2*c+1] == 'd' ) ||
			(array[1][2*c+1] == 'f' ) ) &&
		      ( (array[1][2*c+3] == 'a' ) ||
			(array[1][2*c+3] == 'b' ) ||
			(array[1][2*c+3] == 'c' ) ||
			(array[1][2*c+3] == 'e' ) ) ) {
		array[0][2*c+2] = 'A' ;
            }
            else if ( ( (array[1][2*c+1] == 'c' ) ||
			(array[1][2*c+1] == 'e' ) ||
			(array[1][2*c+1] == 'g' ) ) &&
		      ( (array[1][2*c+3] == 'd' ) ||
			(array[1][2*c+3] == 'f' ) ||
			(array[1][2*c+3] == 'g' ) ) ) {
		array[0][2*c+2] = 'G' ;
            }
            else array[0][2*c+2] = 'C';
        }
        //left side (diagonal) minus corners
        for (int c = 0 ; c < n-2 ; c++) {
            if ( ( (array[2*c+1][2*c+1] == 'a' ) ||
                   (array[2*c+1][2*c+1] == 'c' ) ||
                   (array[2*c+1][2*c+1] == 'd' ) ||
                   (array[2*c+1][2*c+1] == 'g' ) ) &&
                 ( (array[2*c+3][2*c+3] == 'd' ) ||
                   (array[2*c+3][2*c+3] == 'f' ) ||
                   (array[2*c+3][2*c+3] == 'g' ) ) ) {
		array[2*c+2][2*c+2] = 'D' ;
            }
            else if ( ( (array[2*c+1][2*c+1] == 'a' ) ||
			(array[2*c+1][2*c+1] == 'c' ) ||
			(array[2*c+1][2*c+1] == 'd' ) ||
			(array[2*c+1][2*c+1] == 'g' ) ) &&
		      ( (array[2*c+3][2*c+3] == 'a' ) ||
			(array[2*c+3][2*c+3] == 'b' ) ||
			(array[2*c+3][2*c+3] == 'c' ) ||
			(array[2*c+3][2*c+3] == 'e' ) ) ) {
		array[2*c+2][2*c+2] = 'A' ;
            }
            else if ( ( (array[2*c+1][2*c+1] == 'b' ) ||
			(array[2*c+1][2*c+1] == 'e' ) ||
			(array[2*c+1][2*c+1] == 'f' ) ) &&
		      ( (array[2*c+3][2*c+3] == 'd' ) ||
			(array[2*c+3][2*c+3] == 'f' ) ||
			(array[2*c+3][2*c+3] == 'g' ) ) ) {
		array[2*c+2][2*c+2] = 'F' ;
            }
            else array[2*c+2][2*c+2] = 'B' ;
        }
        //right side minus corners
        for (int r = 0 ; r < n-2 ; r++) {
            if ( ( (array[2*r+1][2*n-3] == 'a' ) ||
                   (array[2*r+1][2*n-3] == 'c' ) ||
                   (array[2*r+1][2*n-3] == 'd' ) ||
                   (array[2*r+1][2*n-3] == 'g' ) ) &&
                 ( (array[2*r+3][2*n-3] == 'c' ) ||
                   (array[2*r+3][2*n-3] == 'e' ) ||
                   (array[2*r+3][2*n-3] == 'g' ) ) ) {
		array[2*r+2][2*n-2] = 'C' ;
            }
            else if ( ( (array[2*r+1][2*n-3] == 'a' ) ||
			(array[2*r+1][2*n-3] == 'c' ) ||
			(array[2*r+1][2*n-3] == 'd' ) ||
			(array[2*r+1][2*n-3] == 'g' ) ) &&
		      ( (array[2*r+3][2*n-3] == 'a' ) ||
			(array[2*r+3][2*n-3] == 'b' ) ||
			(array[2*r+3][2*n-3] == 'd' ) ||
			(array[2*r+3][2*n-3] == 'f' ) ) ) {
                 array[2*r+2][2*n-2] = 'A' ;
            }
            else if ( ( (array[2*r+1][2*n-3] == 'b' ) ||
			(array[2*r+1][2*n-3] == 'e' ) ||
			(array[2*r+1][2*n-3] == 'f' ) ) &&
		      ( (array[2*r+3][2*n-3] == 'c' ) ||
			(array[2*r+3][2*n-3] == 'e' ) ||
			(array[2*r+3][2*n-3] == 'g' ) ) ) {
		array[2*r+2][2*n-2] = 'E' ;
            }
            else array[2*r+2][2*n-2] = 'B' ;
        }
        //all of the interior
        for (int r = 0 ; r < n-3 ; r++) {
            for (int c = r+1 ; c < n-2 ; c++) {
               if ( ( (array[2*r+1][2*c+1] == 'a' ) ||
		      (array[2*r+1][2*c+1] == 'c' ) ||
		      (array[2*r+1][2*c+1] == 'd' ) ||
		      (array[2*r+1][2*c+1] == 'g' ) ) &&
		    ( (array[2*r+3][2*c+1] == 'a' ) ||
		      (array[2*r+3][2*c+1] == 'b' ) ||
		      (array[2*r+3][2*c+1] == 'd' ) ||
		      (array[2*r+3][2*c+1] == 'f' ) ) &&
		    ( (array[2*r+3][2*c+3] == 'a' ) ||
		      (array[2*r+3][2*c+3] == 'b' ) ||
		      (array[2*r+3][2*c+3] == 'c' ) ||
		      (array[2*r+3][2*c+3] == 'e' ) ) ) {
		   array[2*r+2][2*c+2] = 'A' ;
               }
               else if ( ( (array[2*r+1][2*c+1] == 'a' ) ||
			   (array[2*r+1][2*c+1] == 'c' ) ||
			   (array[2*r+1][2*c+1] == 'd' ) ||
			   (array[2*r+1][2*c+1] == 'g' ) ) &&
			 ( (array[2*r+3][2*c+1] == 'a' ) ||
			   (array[2*r+3][2*c+1] == 'b' ) ||
			   (array[2*r+3][2*c+1] == 'd' ) ||
			   (array[2*r+3][2*c+1] == 'f' ) ) &&
			 ( (array[2*r+3][2*c+3] == 'd' ) ||
			   (array[2*r+3][2*c+3] == 'f' ) ||
			   (array[2*r+3][2*c+3] == 'g' ) ) ) {
		   array[2*r+2][2*c+2] = 'D' ;
               }
               else if ( ( (array[2*r+1][2*c+1] == 'a' ) ||
			   (array[2*r+1][2*c+1] == 'c' ) ||
			   (array[2*r+1][2*c+1] == 'd' ) ||
			   (array[2*r+1][2*c+1] == 'g' ) ) &&
			 ( (array[2*r+3][2*c+1] == 'c' ) ||
			   (array[2*r+3][2*c+1] == 'e' ) ||
			   (array[2*r+3][2*c+1] == 'g' ) ) &&
			 ( (array[2*r+3][2*c+3] == 'd' ) ||
			   (array[2*r+3][2*c+3] == 'f' ) ||
			   (array[2*r+3][2*c+3] == 'g' ) ) ) {
		   array[2*r+2][2*c+2] = 'G' ;
               }
               else if ( ( (array[2*r+1][2*c+1] == 'a' ) ||
			   (array[2*r+1][2*c+1] == 'c' ) ||
			   (array[2*r+1][2*c+1] == 'd' ) ||
			   (array[2*r+1][2*c+1] == 'g' ) ) &&
			 ( (array[2*r+3][2*c+1] == 'c' ) ||
			   (array[2*r+3][2*c+1] == 'e' ) ||
			   (array[2*r+3][2*c+1] == 'g' ) ) &&
			 ( (array[2*r+3][2*c+3] == 'a' ) ||
			   (array[2*r+3][2*c+3] == 'b' ) ||
			   (array[2*r+3][2*c+3] == 'c' ) ||
			   (array[2*r+3][2*c+3] == 'e' ) ) ) {
		   array[2*r+2][2*c+2] = 'C' ;
               }
               else if ( ( (array[2*r+1][2*c+1] == 'b' ) ||
			   (array[2*r+1][2*c+1] == 'e' ) ||
			   (array[2*r+1][2*c+1] == 'f' ) ) &&
			 ( (array[2*r+3][2*c+1] == 'c' ) ||
			   (array[2*r+3][2*c+1] == 'e' ) ||
			   (array[2*r+3][2*c+1] == 'g' ) ) &&
			 ( (array[2*r+3][2*c+3] == 'a' ) ||
			   (array[2*r+3][2*c+3] == 'b' ) ||
			   (array[2*r+3][2*c+3] == 'c' ) ||
			   (array[2*r+3][2*c+3] == 'e' ) ) ) {
		   array[2*r+2][2*c+2] = 'E' ;
               }
               else if ( ( (array[2*r+1][2*c+1] == 'b' ) ||
			   (array[2*r+1][2*c+1] == 'e' ) ||
			   (array[2*r+1][2*c+1] == 'f' ) ) &&
			 ( (array[2*r+3][2*c+1] == 'a' ) ||
			   (array[2*r+3][2*c+1] == 'b' ) ||
			   (array[2*r+3][2*c+1] == 'd' ) ||
			   (array[2*r+3][2*c+1] == 'f' ) ) &&
			 ( (array[2*r+3][2*c+3] == 'd' ) ||
			   (array[2*r+3][2*c+3] == 'f' ) ||
			   (array[2*r+3][2*c+3] == 'g' ) ) ) {
		   array[2*r+2][2*c+2] = 'F' ;
               }
               else array[2*r+2][2*c+2] = 'B';
            }
        }
	subit = 0;
	return;
    }
    private void thickLine(int lx,Graphics gb, 
			   int x1, int y1, int x2, int y2) {
	gb.drawLine(x1,y1,x2,y2);
	if (lx>2) {gb.drawLine(x1,y1+1,x2,y2+1);}
    }
}
