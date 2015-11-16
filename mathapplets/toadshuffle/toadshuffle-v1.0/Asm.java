/* 
    TOAD Shuffler
    Copyright (C) 2001  Hal Canary

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
import java.awt.Graphics;
import java.awt.Color;

 /**
 * ASM Class
 * 
 * 
 **/
public class Asm extends Object {
    public int size;
    public int entries [] [] ;

    /**
     * The constructor.  Makes a size 0 ASM.  
     **/
    public Asm() {
	size = 0;
    }

    /**
     * initializes the ASM
     * 
     **/
    public int reinit(int sizeIn, int [][]asmArray) {
	size = sizeIn;
	entries = new int [size][size];
	for (int i = 0 ; i < size ; i++) {
	    for (int j = 0 ; j < size ; j++) {
		entries[i][j] = asmArray[i][j];
	    }
	}
	return 0;
    }
    
    /**
     * Checks to see if it's a proper ASM.
     * return 0 --> not ASM. 
     * return 1 --> is ASM.
     * return 2 --> is ASM and is permutation.
     * return 3 --> is ASM and is baxter permutation (2BP). (don'twork)
     **/
    public int checkSelf() {
 	int value = 1;
	//check for ASMness.
	for (int i = 0; i<size; i++) {
	    int sum_row = 0;
	    int sum_col = 0;
	    for (int j = 0; j<size; j++) {
		sum_col = sum_col + entries[i][j];
		sum_row = sum_row + entries[j][i];
		if ( (sum_row !=0 && sum_row != 1) || 
 		     (sum_col !=0 && sum_col != 1)   ) {
		    value = 0;
		}
	    }
	    if ( (sum_row != 1) || (sum_col != 1) ) {
		value = 0;
	    }
	}
	if (value == 0) {
	    return value;
	}
	else 
	    value =2 ;

	//check for Permutationness.
	for (int i = 0; i<size; i++) {
	    for (int j = 0; j<size; j++) {
		if (entries[j][i] == -1) {
		    value = 1 ;
		}
	    }
	}
	return value;
    }

    /**
     * Checks to see if this ASM is compatable with the given smaller ASM 
     * DOESN'T WORK yet.
     **/
    public boolean checkAgainst(Asm asm2) {
	return false;
    }

    /**
     * printAsm() prints the ASM
     **/
    public int printAsm(StringBuffer s) {
	for (int k=0; k < size; k++) {
	    for  (int l=0; l < size; l++) {
		s.append( ' ' );
		if (entries[k][l] == 0 ) {
		    s.append( '0' );
		}
		else if (entries[k][l] == 1 ) {
		    s.append( '+' );
		}
		else if (entries[k][l] == -1 ) {
		    s.append( '-' );
		}
		else {
		    s.append( '#' );
		}
	    }
	    s.append( '\n');
	}
	return 0;
    }

    /**
     * printIntersperced() prints tho ASM
     **/
    public int printInter(Asm asm2,  StringBuffer s) {
	if (asm2.size != size-1) {
	    s.append("Sorry!\n\n");
	    return 1;
	}
	for (int k=0; k < size; k++) {
	    for  (int l=0; l < size; l++) {
		if (entries[k][l] == 0 ) {
		    s.append( '0' );
		}
		else if (entries[k][l] == 1 ) {
		    s.append( '+' );
		}
		else if (entries[k][l] == -1 ) {
		    s.append( '-' );
		}
		else {
		    s.append( '#' );
		}
		s.append( "   " );
	    }
	    s.append( "\n  ");
	    if (k != size-1) {
		for  (int l=0; l < size-1; l++) {
		    if (asm2.entries[k][l] == 0 ) {
			s.append( '0' );
		    }
		    else if (asm2.entries[k][l] == 1 ) {
			s.append( '+' );
		    }
		    else if (asm2.entries[k][l] == -1 ) {
			s.append( '-' );
		    }
		    else {
			s.append( '#' );
		    }
		    s.append( "   " );
		}
	    }
	    s.append("\n");
	}
	return 0;
    }
    

    public boolean equals(Asm otherAsm) {
	if ( otherAsm.size == size ) {
	    for (int k=0; k < size; k++) {
		for  (int l=0; l < size; l++) {
		    if ( otherAsm.entries[k][l] != entries[k][l] ) {
			return false ; 
		    }
		}
	    }
	    return true ;
	}
	else {
	    return false ;
	}
    }

    public int printPotential(StringBuffer s) {
	int potential [][] = new int [size+1] [size+1] ;
	for (int j = 0; j < size+1; j++)
	    potential[0][j] = size - j;
	for (int i = 1 ; i < size+1; i++) {
	    potential[i][0] = size - i ;
	    for (int j = 1; j < size+1; j++) {
		int temp = potential[i-1][j] + potential[i][j-1] 
		    - potential[i-1][j-1];
		if ( entries[i-1][j-1] == 0 )
		    potential[i][j] = temp + 0 ;
		else if ( entries[i-1][j-1] == 1 )
		    potential[i][j] = temp + 2 ;
		else if ( entries[i-1][j-1] == -1 )
		    potential[i][j] = temp - 2 ;
		else 
		    potential[i][j] = -100 ; 
	    }
	}
	for (int k=0; k < size+1 ; k++) {
	    for  (int l=0; l < size+1 ;  l++) {
		s.append( potential[k][l] );
		s.append( '\t' );
	    }
	    s.append( '\n');
	}
	return 0;
    }

    /**
     * print Square Ice model to given stringbuffer
     **/
    public int printSquareIce(StringBuffer s) {
	char udIce[][] = new char [size+1][size] ;
	char lrIce[][] = new char [size][size+1] ;
	
	for (int j = 0 ; j < size ; j++) {
	    udIce[0][j] = 'd';
	}
	for (int i = 1 ; i < size+1; i++) {
	    for (int j = 0; j < size ; j++) {
		if ( entries[i-1][j] == 0 )
		    udIce[i][j] = udIce[i-1][j] ;
		else if ( entries[i-1][j] == 1 )
		    udIce[i][j] = 'u' ;
		else if ( entries[i-1][j] == -1 )
		    udIce[i][j] = 'd' ;
	    }
	}


	for (int i = 0 ; i < size ; i++) {
	    lrIce[i][0] = 'l';
	}
	for (int i = 0 ; i < size ; i++) {
	    for (int j = 1; j < size +1 ; j++) {
		if ( entries[i][j-1] == 0 )
		    lrIce[i][j] = lrIce[i][j-1] ;
		else if ( entries[i][j-1] == 1 )
		    lrIce[i][j] = 'r' ;
		else if ( entries[i][j-1] == -1 )
		    lrIce[i][j] = 'l' ;
	    }
	}


	for (int k=0; k < size ; k++) {
	    for  (int l=0; l < size ;  l++) {
		s.append( ' ' );
		s.append( udIce[k][l] );
	    }
	    s.append( '\n');
	    for  (int l=0; l < size +1 ;  l++) {
		s.append( lrIce[k][l] );
		s.append( ' ' );
	    }
	    s.append( '\n');
	}
	for  (int l=0; l < size ;  l++) {
	    s.append( ' ' );
	    s.append( udIce[size][l] );
	}
	s.append( '\n');

	return 0;
    }

    /**
     * draw DPF
     **/
    public int drawDPF(Graphics g, int imageSize) {

	int l = imageSize / (size+1) ;
	char udIce[][] = new char [size+1][size] ;
	char lrIce[][] = new char [size][size+1] ;
	
	//making Square Ice model...
	for (int j = 0 ; j < size ; j++) {
	    udIce[0][j] = 'd';
	}
	for (int i = 1 ; i < size+1; i++) {
	    for (int j = 0; j < size ; j++) {
		if ( entries[i-1][j] == 0 )
		    udIce[i][j] = udIce[i-1][j] ;
		else if ( entries[i-1][j] == 1 )
		    udIce[i][j] = 'u' ;
		else if ( entries[i-1][j] == -1 )
		    udIce[i][j] = 'd' ;
	    }
	}

	//making Square Ice model...
	for (int i = 0 ; i < size ; i++) {
	    lrIce[i][0] = 'l';
	}
	for (int i = 0 ; i < size ; i++) {
	    for (int j = 1; j < size +1 ; j++) {
		if ( entries[i][j-1] == 0 )
		    lrIce[i][j] = lrIce[i][j-1] ;
		else if ( entries[i][j-1] == 1 )
		    lrIce[i][j] = 'r' ;
		else if ( entries[i][j-1] == -1 )
		    lrIce[i][j] = 'l' ;
	    }
	}

	boolean isRed;
	g.setColor(Color.black);
	for (int i=0; i < size+1 ; i++) {
	    for  (int j=0; j < size ;  j++) {
		isRed = (udIce[i][j] == 'u') ;
		if ((i+j) %2 == 1) 
		    isRed = ! isRed;
		//now draw it
		if (isRed) {
		    g.fillRect(l*(j+1), (i)*l, 1, l );
		}
	    }
	}

	for (int i=0; i < size ; i++) {
	    for  (int j=0; j < size+1 ;  j++) {
		isRed = (lrIce[i][j] == 'l') ;
		if ((i+j) %2 == 1) 
		    isRed = ! isRed;
		//now draw it
		if (isRed) {
		    g.fillRect(l * j, (1+i)*l, l, 1 );
		}
	    }
	}

	return 0 ;
    }

}
