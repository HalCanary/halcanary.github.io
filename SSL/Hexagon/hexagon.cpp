/* 
   How do I compile this?
   >  g++ -o hexagon hexagon.cpp
   
   What do the terminal matchings mean?
        1   2            
        |   |                             
   12   |   |   3              1   2		  
     \ / \ / \ /  	     o---o---o    
      |   |   |   	 12 / \ / \ / \ 3  
 11   |   |   |   4	   o---o---o---o  
   \ / \ / \ / \ /	11/ \ / \ / \ / \ 4
    |   |   |   | 	 o---o---o---o---o
    |   |   |   |      10 \ / \ / \ / \ / 5
   / \ / \ / \ / \	   o---o---o---o  
 10   |   |   |   5	 9  \ / \ / \ / 6  
      |   |   |   	     o---o---o    
     / \ / \ / \  	       8   7	  
    9   |   |   6  	 		  
        |   |     	 		  
        8   7         	 		                        
*/

#include <iostream>
#include<stdlib.h>  

using namespace std;

//#include<iostream>
//#include<stdlib>  

/**
 * hexagon class.  
 * can print out all lozenge tilings of a particular size regular hexagon.
 * can print all of the dpfl terminal matchings.
 * Hal Canary, April 2001.
 **/

const int MAXSIZE = 50;

/**
 * class for a hexagon/GelfandSquare
 **/
class Hexagon {
public:
    /** 
     * constructor: makes the smallest gelfand square.
     **/
    Hexagon(int sizein);

    /**
     * default constructor:  sets size to 1.
     **/
    Hexagon();

    /**
     * changes the gelfand square.
     **/
    int iterate();

    /**
     * constructs two matrices that represent the matching/tiling.
     **/
    int makeBW();
    
    /**
     * prints out the tiling (assumes you've run makeBW).
     **/
    int printBW(ostream& o);

    /**
     * prints out the gelfand square.
     **/
    int printGS(ostream& o);

    /**
     * prints out all of the dpfl terminal matchings.
     * Assumes you've run makeBW.
     **/
    int printMatchings(ostream& o);

private:
    //fields:
    int size;                             // size of the regular hexagon.
    int gelfand [MAXSIZE+1] [MAXSIZE+1] ; // GelfandSquare
    char black [2*MAXSIZE][2*MAXSIZE] ;   // black triangles
    char white [2*MAXSIZE][2*MAXSIZE] ;   // white triangles

    //functions:

    /**
     * Recursive Iteration function.
     * parameters: current position in GS.
     * returns 1 if we are done iterating, else 0
     **/
    int iterateR(int row, int col);       

    /**
     * matching function.
     * returns the terminal that matches the starting Terminal.
     **/
    int match(int startTerminal);       

    /**
     * Recursive flux Move function.
     * returns termianl on which the dpf line ends.
     **/
    int rmove(char type, int row, int col, char dir);

    /**
     * returns 0 if not a terminal.  
     * otherwise returns terminal number.
     **/
    int amterm(char type, int row, int col);
};

Hexagon::Hexagon(int sizein) { 
    size = sizein;
    if (size > MAXSIZE) {
	size = MAXSIZE;
    }
    gelfand[0][0] = 0;
    for (int j=1; j < size+1; j++) { 
	gelfand[0][j] = (2 * size) + 1;
    }
    for (int i=1; i < size+1; i++) {
	for (int j=0; j < size+1; j++) {
	    gelfand[i][j] = size-i+1;
	}
    }
}
Hexagon::Hexagon() { 
    size = 1;
    gelfand[0][0] = 0;
    for (int j=1; j < size+1; j++) { 
	gelfand[0][j] = (2 * size) + 1;
    }
    for (int i=1; i < size+1; i++) {
	for (int j=0; j < size+1; j++) {
	    gelfand[i][j] = size-i+1;
	}
    }
}

int Hexagon::iterate() { 
    return iterateR(size, size);
}

int Hexagon::iterateR(int row, int col) {
    if (gelfand[row][col] < gelfand[row-1][col]-1 ) {
	gelfand[row][col]++;
	return 0;
    } 
    else {
	if (col > 1) {
	    if (iterateR(row, col-1) == 1) 
		return 1 ;
	    else { 
		gelfand[row][col] = gelfand[row][col-1];
		return 0;
	    }
	}
	else if (row > 1) {
	    if (iterateR(row-1, size) == 1) 
		return 1 ;
	    else { 
		gelfand[row][col] = gelfand[row][col-1];
		return 0;
	    }
	}
	else if (row == 1 && col == 1) 
	    return 1;
	else
	    return 40;
    }
}

int Hexagon::printGS(ostream& o) {
    for (int i=0; i < size+1; i++) {
	for (int j=0; j < size+1; j++) {
	    o << gelfand[i][j] << '\t' ;
	}
	o << endl;
    }
}

int Hexagon::makeBW() { 
    //black is either r, l, d, or ' '
    //white is either r, l, u, or ' '
    //start the empty bleg.
    for (int i=0; i < 2*size; i++) {
	for (int j=0; j < 2* size; j++) {
	    black[i][j] = '_' ;
	    white[i][j] = '_' ;
	}
    }   

    for (int i=0; i < size; i++) {
	for (int j=0; j < size-1-i; j++) {
	    black[i][j] = ' ' ;
	    white[2*size-i-1][2*size-j-1] = ' ' ;
	}
    }
    for (int i=size; i < 2*size; i++) {
	for (int j=3*size-i-1 ; j < 2*size; j++) {
	    black[i][j] = ' ' ;
	    white[2*size-i-1][2*size-j-1] = ' ' ;
	}
    }
    //insert the up-down tiles...
    for (int i=1; i < size+1; i++) {
	for (int j=1; j < size+1; j++) {
	    int rowblack = i+j-2;
	    int rowwhite = i+j-1;
	    int col = gelfand[i][j] -1 ;
	    white[rowwhite][col] = 'V';
	    black[rowblack][col] = 'A';
	}
    }

    //insert the left-right tiles...
    for (int i=0; i < size; i++) {
	if (black[i][size-i-1] != 'A') {
	    black[i][size-i-1] = '<';
	}
    }
    for (int i=0; i < size; i++) {
	for (int j=size-i; j< 2*size; j++) {
	    if (white[i][j] == 'V' ) {
		if (black[i][j] != 'A' ) {
		    black[i][j] = '<' ;
		}
	    }
	    else if (black[i][j-1] == '<') {
		white[i][j] = '>' ;
		if (black[i][j] != 'A') {
		    black[i][j] = '<';
		}
	    }
	    else {
		white[i][j] = '<';
		black[i][j] = '>';
	    }
	}
    }

    for (int i=size; i < 2*size; i++) {
	if (white[i][0] != 'V') {
	    white[i][0] = '<';
	}
    }
    for (int i=size; i < 2*size; i++) {
	for (int j=0; j< 3*size-i-1; j++) {
	    if (black[i][j]== 'A') {
		if (white[i][j+1] != 'V') {
		    white[i][j+1] = '<' ;
		}
	    }
	    else if (white[i][j]== '<') {
		black[i][j] = '>';
		if (white[i][j+1] != 'V') {
		    white[i][j+1] = '<' ;
		}
	    }
	    else {
		black[i][j] = '<';
		white[i][j+1] = '>';
	    }
	}
    }    
    return 0;
}    

int Hexagon::printBW(ostream& o) { 
    for (int i=0; i < 2*size; i++) {
	for (int j=0; j < i; j++) {
	    o << ' ' ;
	}
	for (int j=0; j < 2* size; j++) {
	    if (i>=size && white[i][j]==' '){
		j = 2*size;
	    }
	    else {
		o << white[i][j] ;
		o << black[i][j] ;
	    }
	}
	o << endl;
    }   
    return 0;
}

int Hexagon::printMatchings(ostream& o) {
    for (int i=1 ; i <= 6 * size ; i=i+2) {
	o << i << ':' << match(i) <<  ' ' ;
    }
    o << endl;
    return 0;
}

int Hexagon::match(int go) {
    if      (go > 0      && go <=   size) { 
	return rmove('w', 0, go+size-1, 'd');
    }
    else if (go > size   && go <= 2*size) { 
	return rmove('b', go-size-1, 2*size-1, 'l');
    }
    else if (go > 2*size && go <= 3*size) {  
	return rmove('w', go-1-size, 4*size-go, 'l');
    }
    else if (go > 3*size && go <= 4*size) {  
	return rmove('b', 2*size-1, 4*size-go, 'u');
    }
    else if (go > 4*size && go <= 5*size) {  
	return rmove('w', 6*size - go, 0, 'r');
    }
    else if (go > 5*size && go <= 6*size) {  
	return rmove('b', 6*size-go, go-5*size-1, 'r');
    }
    else {
	return 7000;
    }
}

int Hexagon::rmove(char type, int row, int col, char dir) { 
    //decide next tile/node.
    char newdir;
    int newrow;
    int newcol;
    char newtype;
    if (type == 'b') {
	char match = black[row][col];
	newtype = 'w';
	if      (match == 'A' && dir == 'l') {  newdir = 'l'; }
	else if (match == 'A' && dir == 'r') {  newdir = 'r'; }
	else if (match == '<' && dir == 'u') {  newdir = 'l'; }
	else if (match == '<' && dir == 'r') {  newdir = 'd'; }
	else if (match == '>' && dir == 'u') {  newdir = 'r'; }
	else if (match == '>' && dir == 'l') {  newdir = 'd'; }
	else return 666;
	if      (newdir == 'd') { newcol = col;   newrow = row+1; }
	else if (newdir == 'r') { newcol = col+1; newrow = row;   }
	else if (newdir == 'l') { newcol = col;   newrow = row;   }
    }
    else if (type == 'w') {
	char match = white[row][col];
	newtype = 'b';
	if      (match == 'V' && dir == 'l') { newdir = 'l'; }
	else if (match == 'V' && dir == 'r') { newdir = 'r'; }
	else if (match == '<' && dir == 'd') { newdir = 'l'; }
	else if (match == '<' && dir == 'r') { newdir = 'u'; }
	else if (match == '>' && dir == 'd') { newdir = 'r'; }
	else if (match == '>' && dir == 'l') { newdir = 'u'; }
	else { return 20000; }
	if (newdir == 'l') { newcol = col-1; newrow = row;   }
	if (newdir == 'u') { newcol = col;   newrow = row-1; }
	if (newdir == 'r') { newcol = col;   newrow = row;   }
    }
    //if that next tile==terminal, return terminal.
    int x = amterm(newtype, newrow, newcol);
    if (x != 0) {
	return x;
    }
    //else return rmove of next tile...
    else {
	return rmove(newtype, newrow, newcol, newdir) ;
    }
}

int Hexagon::amterm(char type, int row, int col) { 
    if (type == 'b') {
	if (row == 2*size-1) {
	    return (4*size)-col ;
	}
	else if (col == 2*size-1) {
	    return size+1+row;
	} 
	else if (col+row == size-1) {
	    return 5*size+col+1;
	}
	else { 
	    return 0;
	}
    }
    else if (type == 'w') {
	if (row == 0) {
	    return  (col -size +1) ;
	}
	else if (col == 0) {
	    return (6*size-row);
	} 
	else if (col+row == (3*size)-1) {
	    return (row+size+1);
	}
	else { 
	    return 0;
	}
    }
    else {
	return 0;
    }
}
 
int main(int argc, char *argv[]) {
    int size = 0;
    if (argc == 1) {
	cerr << "How big do you want your hexagon?\n"
	     << "Usage: hexagon size [options]\n" 
	     << "``size'' is the size of the hexagon \n"
	     << "\tOptions:\n"
	     << "\tg = print Gelfand Square\n" 
	     << "\th = print Hexagon\n"
	     << "\tc = count tilings\n"
	     << "\tm = do not print matchings\n"   
	     << "example:  ./hexagon 2 hc \n";
	return 1;
    }
    else {
	if (size = abs(atoi(argv[1])) ) {
	    if ( size > MAXSIZE ) {
		size = MAXSIZE ;
	    }
	}
	else {
	    cerr << "Usage: gelfand size [options]" << endl
		 << "and ``size'' is an positive integer less than " 
		 << MAXSIZE << "."<< endl;
	    return 1;
	}
    }
    int printGSon = 0;
    int printBWon = 0;
    int counton = 0;
    int printMatchings = 1;
    if (argc >= 3) {
	for(int i = 0; argv[2][i] != '\0' ; i++) {
	    if (argv[2][i] == 'g') 
		printGSon = 1;
	    if (argv[2][i] == 'h')
		printBWon = 1;
	    if (argv[2][i] == 'c')
		counton = 1;
	    if (argv[2][i] == 'm')
		printMatchings = 0;
	}
    }
    
    Hexagon hex(size);
    long count = 1;
    long count2 = 0;

    if (printGSon ==1) {
	hex.printGS(cout);
	cout << endl;
    }
    if (printBWon == 1 || printMatchings == 1)
	hex.makeBW();
    if (printBWon == 1)
	hex.printBW(cout);
    if (printMatchings == 1)
	hex.printMatchings(cout);
    if (printBWon == 1) 
	cout << endl;

    while (hex.iterate() == 0) {
	if (counton == 1){
	    if (count == 1999999999) {
		count = 0;
		count2++;
	    }
	    else {
		count++;
	    }
	}
	if (printGSon ==1) {
	    hex.printGS(cout);
	    cout << endl;
	}
	if (printBWon == 1 || printMatchings == 1)
	    hex.makeBW();
	if (printBWon == 1) 
	    hex.printBW(cout);
	if (printMatchings == 1)
	    hex.printMatchings(cout);
	if (printBWon == 1) 
	    cout << endl;
    }
    if (counton == 1) {
	if (count2==0) 
	    cout << "Total number of tilings = " << count << endl;
	else 
	    cout << 2*count2 << "000000000 + " << count << endl;
    }
    return 0; 
}
