/* 
    TOAD Shuffler    v 1.1
    Copyright (C) 2001-2002  Hal Canary

    This program is free software; you can redistribute it and/or
    modify it under the terms of version 2 of the GNU General Public
    License as published by the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
/*   (r,c)     (0,0)                
        rows     N   columns  
                N N	   
      <--r     N N N      c-->
              N N N E  
             S S S W E  (0,n-1)
     (n,0)  W N S E N  
             S W N S   
              S S S    
               S S 	   
                S      
              (n,n-1)
*/

import java.awt.Graphics;
import java.awt.Color;

/**
 * Tiling Class:
 * this class represents a tiling of n(n+1) dominos 
 * arranged in an aztec diamond .
 *
 * <p>We use (r,c) instead of (x,y).
 * <br>  r = n-x;
 * <br>  c = n-1-y;
 **/
public class Tiling extends Object {
    //private static final int MAXSIZE = 20;
    //fields
    public int n;             	//current size 
    int arraySize;		//size of arrays.
    public char [] [] domArray;  // the array of dominos
    public char [] [] newArray;  // a temporary array used for moves
    public char [] [] tmpArray;  // a temporary array used for moves

    //Methods:

    /**
     * The Constructor
     * we have to go through and initilize each domino.
     **/ 
    public Tiling() {  
	int r, c;
	n = 1;
	arraySize=8;  // 8 x 8 x 1char =  64 bytes to represent it intially.
	//init domArray
	domArray = new char [arraySize+1] [arraySize];  
	for(r = 0 ; r < n + 1 ; r++) {
	    for(c = 0 ; c < n ; c++) {
		domArray[r][c] = ' ' ;
	    }
	}
	newArray = new char [arraySize+1] [arraySize];  
	isHF = false ;
    }

    /**
     * How big is my AD?
     **/
    public int getSize() {
	return n;
    }

    /**
     * annihilateAll() moves through the array and searches 
     * for something to destroy!!!!!
     **/
    public int annihilateAll() {
	int r, c;
	for(r = 1 ; r < n+1 ; r++) {
	    for(c = 0 ; c < n-1 ; c++) {
		if ( (domArray[r][c] == 'E' && 
		      domArray[r][c+1] == 'W' ) ||
		     (domArray[r][c] == 'S' && 
		      domArray[r][c+1] == 'N' ) ) {
		    domArray[r][c] = ' ';
		    domArray[r][c+1] = ' ';
		}
	    }
	}
	isHF = false ;
	return 0;
    }

    /**
     * moveAll() moves NESorW-going dominos to their new positions
     * in the newArray.
     * it then copies the newArray to domArray.
     **/
    public int moveAll() {
	int r, c;
	if ((n+1) > (arraySize)) {
	    arraySize = 2 * arraySize;   //increase by 4 the total storage.
	    newArray=null;  //throw away old newArray.
	    newArray = new char [arraySize+1] [arraySize];  
	    tmpArray= new char [arraySize+1] [arraySize];  
	    for(r = 0 ; r < n + 1 ; r++) {
		for(c = 0 ; c < n ; c++) {
		    tmpArray[r][c] = domArray[r][c] ;  //make a copy
		}
	    }
	    domArray=tmpArray;
	    tmpArray=null;
	    System.gc();
	}

	//init newArray  it's bigger by 1 in r and c direction!!
	for(r = 0 ; r < n + 2 ; r++) {
	    for(c = 0 ; c < n+1 ; c++) {
		newArray[r][c] = ' ' ;
	    }
	}
	for(r = 0 ; r < n+1 ; r++) {
	    for(c = 0 ; c < n ; c++) {
		if (domArray[r][c] == 'S' ) {
		    newArray[r+1][c+1] = 'S' ;
		}
		else if (domArray[r][c] == 'N' ) {
		    newArray[r][c] = 'N' ;
		}
		else if (domArray[r][c] == 'W' ) {
		    newArray[r+1][c] = 'W' ;
		}
		else if (domArray[r][c] == 'E' ) {
		    newArray[r][c+1] = 'E' ;
		}
	    }
	}
	n++;
	tmpArray=domArray;
	domArray=newArray;
	newArray=tmpArray;
	isHF = false ;
	return 0;
    }

    /**
     * fillAll() looks for untiled holes in the aztec diamond. 
     * it then flips a coin and tiles with a NS or a EW pair.
     **/ 
    public int fillAll() {
	int r, c;
	int coin;
	for(r = 0 ; r < n+1 ; r++) {
	    for(c = 0 ; c < n ; c++) {
		if (domArray[r][c] == ' ' &&
		    domArray[r+1][c] == ' ') {
		    //flip a coin
		    coin = (int) java.lang.Math.floor(2 
			* java.lang.Math.random()) ;
		    if (coin == 0) {
			domArray[r][c] = 'N';
			domArray[r+1][c] = 'S';
		    }
		    if (coin == 1) {
			domArray[r][c] = 'E';
			domArray[r+1][c] = 'W';
		    }
		}
	    }
	}
	return 0;
    }

    /**
     * printToBuff()  prints to the StringBuffer s
     * 
     **/ 
    public int printToBuff(StringBuffer s) {
	//Top
	int k, l;
	for (k = 0; k < n ; k++) {
	    for (l = 0; l < n-k-1; l++) {
		s.append(' ');
	    }
	    for (l = 0; l <= k; l++) {
		s.append(' ');
		s.append(domArray[k-l][0+l]);
	    }
	    s.append('\n');
	}
	//Bottom half
	for (k = 0; k < n; k++) {
	    for (l = 0; l<k; l++ ) {
		s.append(' ');
	    }
	    for (l = 0; l < n-k ; l++) {
		s.append(domArray[ n-l ][ k+l ]);
		s.append(' ');
	    }
	    s.append('\n');
	}
	return 0;
    }

    /**
     * drawAll() 
     *
     * Tells each domino to draw itself on the graphics buffer.
     *
     * @param l is size of each tile
     * @param sizeX is the x size of region.
     * @param sizeY is the y size of region
     **/ 
    public int drawAll(int l, int sizeX, int sizeY, Graphics gb) {
	if (l < 1) { l = 1 ; }
	int r, c;
	for( r = 0 ; r < n+1 ; r++) {
	    for( c = 0 ; c < n ; c++) {
		if (domArray[r][c] == 'E' ) {
		    gb.setColor(Color.green);
		    gb.fillRect(sizeX/2 + l*(c-r) , sizeY/2 -n*l +(r+c)*l ,
				l, 2*l );
		    if (l>1) {
			gb.setColor(Color.black);
			gb.drawRect(sizeX/2 + l*(c-r), sizeY/2 -n*l +(r+c)*l,
				    l, 2*l );
		    }
		}
		else if (domArray[r][c] == 'W') {
		    gb.setColor(Color.red);
		    gb.fillRect(sizeX/2 + l*(c-r) , sizeY/2 -n*l +(r+c)*l -l,
				l, 2*l );
		    if (l>1) {
			gb.setColor(Color.black);
			gb.drawRect(sizeX/2 + l*(c-r), 
				    sizeY/2 -n*l +(r+c)*l -l, l, 2*l );
		    }
		}
		else if (domArray[r][c] == 'S') {
		    gb.setColor(Color.blue);
		    gb.fillRect(sizeX/2 + l*(c-r) , sizeY/2 -n*l +(r+c)*l ,
				2*l , l);
		    if (l>1) {
			gb.setColor(Color.black);
			gb.drawRect(sizeX/2 + l*(c-r) , sizeY/2 -n*l +(r+c)*l ,
				    2*l , l);
		    }
		}
		else if (domArray[r][c] == 'N') {
		    gb.setColor(Color.yellow);
		    gb.fillRect(sizeX/2 + l*(c-r) -l, sizeY/2 -n*l +(r+c)*l,
				2*l, l);
		    if (l>1) {
			gb.setColor(Color.black);
			gb.drawRect(sizeX/2 + l*(c-r) -l , 
				    sizeY/2 -n*l +(r+c)*l, 2*l, l);
		    }
		}
	    }
	}
	return 0;
    }

    /***************************************************************/
    //here on out is HF and ASM stuff.  Experimental shit.

    private int [][] hfB;		// big HF matrix
    private int [][] hfS;		// small HF matrix
    private boolean isHF;  		// are the HF matrices computed yet?
    
    /**
     * constructHF() constructs the HF matrices if they are needed.
     **/
    private int constructHF() {
	hfB = new int [n+2][n+2] ;
	hfS = new int [n+1][n+1] ;
	// let's set the border of B
        for ( int i = 0; i < n+2; i++ ) { 
	    hfB[i][n+1] = 2 * (i);	// right border
	    hfB[i][0]  = 2*(n+1 - i); 	// left border
	    hfB[0][i]  = 2*(n+1 -i); 	// top border
	    hfB[n+1][i] = 2 * (i);	// bottom border
	}

	// let's set the border of S
	for ( int i = 0; i < n+1; i++ ) { 
	    hfS[i][n] = 2*(i) +1 ; 	// right border
	    hfS[i][0] = 2*(n - i) +1;	// left border
	    hfS[0][i] = 2*(n -i) +1;	// top border
	    hfS[n][i] = 2*(i) +1 ; 	// bottom border
	}

	// Now the hard part........
	// going through interior of Aztec Diamond
        
        for ( int i = 1; i <  n+1; i++ ) {
	    for ( int j = 1; j < n+1; j++ ) {
		// Getting height function on row of hfB
		if      ( domArray[i-1][j-1] == 'N' )  
		    hfB[i][j] = hfB[i-1][j] - 2 ;
		
		else if ( domArray[i-1][j-1] == 'S' )  
		    hfB[i][j] = hfB[i-1][j] + 2 ;
		
		else if ( domArray[i-1][j-1] == 'E' )  
		    hfB[i][j] = hfB[i-1][j] + 2 ;
		
		else if ( domArray[i-1][j-1] == 'W' )  
		    hfB[i][j] = hfB[i-1][j] - 2 ;

		else hfB[i][j]=-100;     // error checking
	    }
	}
        for ( int i = 1; i <  n+1; i++ ) {
	    for ( int j = 1; j < n+1; j++ ) {
		// Getting height function on row of hfS
		if      ( domArray[i][j-1] == 'N' )  
		    hfS[i][j] = hfS[i][j-1] - 2 ;
		
		else if ( domArray[i][j-1] == 'S' )  
		    hfS[i][j] = hfS[i][j-1] + 2 ;
		
		else if ( domArray[i][j-1] == 'E' )  
		    hfS[i][j] = hfS[i][j-1] - 2 ;
		
		else if ( domArray[i][j-1] == 'W' )  
		    hfS[i][j] = hfS[i][j-1] + 2 ;

		else hfS[i][j]=-300;     // error checking
	    }
	}
	isHF = true;
	return 0;
    }

    /**
     * printHF() prints the 2 HFs to the stringbuffer. 
     **/
    public int printHF(StringBuffer s) {
	if (! isHF ) {
	    constructHF();
	}

	for (int k=0; k < n+2; k++) {
	    for  (int l=0; l < n+2; l++) {
		s.append( hfB[k][l] );
		s.append( '\t' );
	    }
	    s.append( '\n');
	}
	s.append( "----\n" ) ;
	for (int k=0; k < n+1; k++) {
	    for  (int l=0; l < n+1; l++) {
		s.append( hfS[k][l] );
		s.append( '\t' );
	    }
	    s.append( '\n');
	}
	return 0;
    }

    /**
     * drawHF() writes the HFs onto the gb image.  //DOESN'T WORK
     **/
    public int drawHF(int l, int sizeX, int sizeY, Graphics gb) {
	if (! isHF ) {
	    constructHF();
	}
	return 0;
    } 

    /**
     * toAsm() constructs two ASMs from the height functions.
     * retuens a 1 if both ASMs are permutations.
     * retuens a 0 otherwise.
     **/
    public int toAsm(Asm asm1, Asm asm2) {
	if (! isHF ) {
	    constructHF();
	}
	int asmArray1 [][] = new int [n+1][n+1] ;
	int asmArray2 [][] = new int [n][n] ;
	int temp = 0; 
	int returnValue = 1;
        for ( int i = 0 ; i <  n+1 ; i++ ) {
	    for ( int j = 0 ; j < n+1 ; j++ ) {
		temp = hfB[i][j] + hfB[i+1][j+1] - hfB[i+1][j] - hfB[i][j+1] ;
		if ( temp == 0 ) 
		    asmArray1[i][j] =  0 ;
		else if ( temp == 4 ) 
		    asmArray1[i][j] =  1 ;
		else if ( temp == -4 ) { 
		    asmArray1[i][j] =  -1 ;
		    returnValue = 0;
		}
		else 
		    asmArray1[i][j] =  -100* temp ;
	    }
	}

        for ( int i = 0 ; i <  n ; i++ ) {
	    for ( int j = 0 ; j < n ; j++ ) {
		temp = hfS[i][j] + hfS[i+1][j+1] - hfS[i+1][j] - hfS[i][j+1] ;
		if ( temp == 0 ) 
		    asmArray2[i][j] =  0 ;
		else if ( temp == 4 ) 
		    asmArray2[i][j] = 1 ;
		else if ( temp == -4 ) {
		    asmArray2[i][j] = -1 ;
		    returnValue = 0;
		}
		else
		    asmArray2[i][j] = -200 ;
	    }
	}

	asm1.reinit(n+1, asmArray1);
	asm2.reinit(n,  asmArray2);

	return returnValue;
    }
}
