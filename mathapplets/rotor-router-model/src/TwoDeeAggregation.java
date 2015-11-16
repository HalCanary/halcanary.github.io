/* 
    Rotor Router Model Applet
    TwoDee Aggregation Class
    Copyright (C) 2003-2004 Hal Canary, Univerity of Wisconsin-Madison
    hal@ups.physics.wisc.edu

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
import java.awt.*;

/** Mathematical Model of the Rotor Router Object **/
public class TwoDeeAggregation extends MathSystem {
    static int defaultMaxSize = 2048;
    //public boolean allArrows;
    public int maxSize ;
    public int aSize;

    int maxRadius2;  //maximum radius squared
    int minRadius2;  //minimum radius squared
    /** Current minimum radius of the circle containing the blob. **/
    public double minRadius;
    /** Current maximum radius of a circle inside the blob. **/
    public double maxRadius;

    /** Minumum of all differences between minRadius-maxRadius **/
    public double minDifference;
    /** Maximum of all differences between minRadius-maxRadius **/
    public double maxDifference;

    char [] [] sandpile ;
    int middle;
    int posR;
    int posC;
    
    //public int [] recentR ;
    //public int [] recentC ;

    public int n; //iteration
    public int subiteration;
    public double animate;

    boolean moved;

    boolean done;
    boolean filled;

    int lastR;
    int lastC;

    char lastDir;

    Color up;
    Color down;
    Color right;
    Color left;
	
    /** Creates a new empty RotorRouter **/
    public TwoDeeAggregation() {
	maxSize = defaultMaxSize;
	reset();
    } 
    public TwoDeeAggregation(int n) {
	maxSize = n;
	reset();
    } 

    public void reset() {
	maxRadius2 = 0;  //maximum radius squared
	minRadius2 = 0;  //minimum radius squared
	minRadius = 0;
	maxRadius = 0;
	minDifference = 0;
	maxDifference = 0;
	//moved = true;  //CHANGED 2004-04-28
	moved = true;
	animate = 1.0;
	//filled = false; //CHANGED 2004-04-28
	filled = false;
	//n = 1; //CHANGED 2004-04-28
	n = 1;
	done = false;
	subiteration = 0;
	aSize = 7;
	sandpile = new char [aSize][aSize];
	for(int r = 0 ; r < aSize ; r++) {
	    for(int c = 0 ; c < aSize ; c++) {
		sandpile[r][c] = ' ' ;
	    }
	}
	middle = 3;
	lastR=-1;
	lastC=-1; //just in case.
	up     = new Color(255,63,63); 
	down   = Color.green;
	right  = new Color(127,127,255); 
	left   = Color.yellow;
	posR = middle;
	posC = middle;
    }

    public void subiterate(int numsub) {
	for(int bleg = 0; bleg < numsub; bleg++) {
	    if (filled) return;
	    if (done) { // new iteration
		n++;
		moved = true;
		subiteration = 0;
		posR = middle;
		posC = middle;
		done = false;
	    } else if (moved) {
		subiteration++;
		char dir = sandpile[posR][posC];
		//rotate (or fill) the cell
		if (dir == 'u') {
		    sandpile[posR][posC] = 'r';
		    lastDir = sandpile[posR][posC];
		} else if (dir == 'r') {
		    sandpile[posR][posC] = 'd';
		    lastDir = sandpile[posR][posC];
		} else if (dir == 'd') {
		    sandpile[posR][posC] = 'l';
		    lastDir = sandpile[posR][posC];
		} else if (dir == 'l') {
		    sandpile[posR][posC] = 'u';
		    lastDir = sandpile[posR][posC];
		} else if (dir == ' ') {
		    sandpile[posR][posC] = 'l';
		    calcMinMax(posR, posC);
		    lastR = posR;
		    lastC = posC;
		    done = true;
		} else {
		    System.out.println("shouldn't happen! ");	
		}		
		moved = false;
	    } else {
		//System.out.println("moving");	
		char dir = sandpile[posR][posC];
		//move the cell.
		if (dir == 'r') {
		    posC++; //move right
		    if (posC == aSize) { filled = !resizeArray(); }
		} else if (dir == 'd') {
		    posR++; //move down
		    if (posR == aSize) { filled = !resizeArray(); }
		} else if (dir == 'l') {
		    posC--; // move left
		    if (posC < 0) { filled = !resizeArray(); }
		} else if (dir == 'u') {
		    posR--;  //move up
		    if (posR < 0) { filled = !resizeArray(); }
		} else {
		    System.out.println("shouldn't happen! ");	
		}		
		moved = true;
	    }
	}
	return;
    }
    public void iterate(int number) {
	int num;
	for(num = 0; (num < number) && (!filled); num++) {
	    if (done) {
		n++;
		subiteration = 0;
		posR = middle;
		posC = middle;
		moved = true;
		done = false;
	    }
	    while ((! done) && (! filled)) {
		subiterate(1); 
	    }
	} 
	return;
    }
    /** return true if successfull. **/
    private boolean resizeArray() {
	//System.out.println("Resizing! \n");	
        int newArraySize = aSize * 2 + 1;
	if (newArraySize > maxSize) {
	    //do something here to stop this!!!!
	    return false;
	}
	int newMiddle = aSize; //(newArraySize - 1) / 2;  
	int shift = newMiddle - middle;
	char [] [] oldPile = sandpile;	
        sandpile = new char[newArraySize][newArraySize];
        for(int r = 0 ; r < newArraySize ; r++) {
            for(int c = 0 ; c < newArraySize ; c++) {
                sandpile[r][c] = ' ';
            }
        }
	//25% wasteful!  What to do? Worry about smething else!
        for(int r = 0 ; r < aSize-1 ; r++) {
            for(int c = 0 ; c < aSize-1 ; c++) {
                sandpile[r+shift][c+shift] = oldPile[r][c] ;
            }
        }
	oldPile = null;
	//garbage collect???
        middle = newMiddle;
	posR = posR + shift;
	posC = posC + shift;
	aSize = newArraySize;
	return true;
    }


    void calcMinMax(int r, int c) {
	int d2 = ((r-middle) * (r-middle)) + ((c-middle) * (c-middle));
	if (d2 > maxRadius2) {
	    maxRadius2 = d2;
	    maxRadius = java.lang.Math.sqrt( (double) maxRadius2 );
	}
	int min2 = (middle + 1) * (middle + 1);
	int l;
	for( int row = 0; row < aSize ; row++) {
	    for( int col = 0; col < aSize ; col++) {
		if (sandpile[row][col] == ' '){
		    l = ((row-middle) * (row-middle)) + 
			((col-middle) * (col-middle));
		    if (l < min2) { min2 = l; }
		}
	    }
	}
	minRadius2 = min2;
	minRadius = java.lang.Math.sqrt( (double) minRadius2 );

	double diff = maxRadius - minRadius;
	if ( diff < minDifference ) {
	    minDifference = diff; 
	}
	if ( diff > maxDifference ) {
	    maxDifference = diff; 
	}
	return;
    }
    public String toString() {
	int minR = aSize - 1;
	int minC = aSize - 1;
	int maxR = 0;
	int maxC = 0;
	StringBuffer s = new StringBuffer();
	for(int r = 0 ; r < aSize ; r++) {
            for(int c = 0 ; c < aSize ; c++) {
		if (sandpile[r][c] != ' ') {
		    if (r < minR) minR = r;
		    if (r > maxR) maxR = r;
		    if (c < minC) minC = c;
		    if (c > maxC) maxC = c;
		}
	    }
	}
	for(int r = minR ; r <= maxR ; r++) {
            for(int c = minC ; c <= maxC ; c++) {
		s.append(sandpile[r][c]);
	    }
	    s.append('\n');
	}
	return s.toString();
    }
    public void printArray() {
	System.out.print("Grain number ");
	System.out.println(n);
	for(int r = 0 ; r < aSize-1 ; r++) {
            for(int c = 0 ; c < aSize-1 ; c++) {
		System.out.print(sandpile[r][c]);
		System.out.print(' ');
	    }
	    System.out.print('\n');
	}
	System.out.print('\n');
    }

    public void draw(int sizeX, int sizeY, Graphics g){
	int square;
	if (sizeX < sizeY) { square = sizeX; }
	else { square = sizeY; }
	int block = square / aSize;
	g.clearRect(0,0,sizeX,sizeY) ;
	g.setColor(Color.white);
	g.fillRect(0,0,aSize * block,aSize * block) ;
	for (int r = 0 ; r < aSize ; r++) {
	    for (int c = 0 ; c < aSize ; c++) {
		char dir = sandpile[r][c];
		if (dir == 'u' || dir == 'r' || dir == 'd' || dir == 'l') {
		    drawBlock(r, c, block, dir, g, 0, 0);
		} 
	    }
	}
	if (block >= 5) {
	    if (this.animate == 1.0 || (!moved)) {
		drawBug(posR, posC, block, g, 0, 0);
	    } else if (subiteration==0) {
		drawBug(posR, posC, block, g, 1, 1);
	    } else {
		char dir = sandpile[posR][posC];
		int offset = (int) ((1.0-animate) * block);
		if (lastDir == 'u')
		    drawBug(posR, posC, block, g, 0, offset);
		if (lastDir == 'r')
		    drawBug(posR, posC, block, g, -1*offset, 0);
		if (lastDir == 'd')
		    drawBug(posR, posC, block, g, 0, -1*offset);
		if (lastDir == 'l')
		    drawBug(posR, posC, block, g, offset, 0);
	    }
	}
	int fontHeight = g.getFontMetrics().getHeight();
	g.setColor(Color.black);
	g.drawString("Stage = "+n, square,20 + (fontHeight*1));
	g.drawString("Maximum Radius = "+doubleToString(maxRadius,6),
		     square,20 + (fontHeight*2));
	g.drawString("Minimum Radius = "+doubleToString(minRadius,6),
		     square,20 + (fontHeight*3));
	g.drawString("Difference = "+doubleToString(maxRadius-minRadius,6),
		     square,20 + (fontHeight*4));
	return;
    }
    /**
       r,c row and column
     **/
    void drawArrow(Graphics g, int r, int c, char dir, int b) {
	int cX = 1 + (b*c) + (b/2);
	int cY = 1 + (b*r) + (b/2);
	g.setColor(Color.black);
	if (dir == 'u' || dir =='d') {
	    g.drawLine(cX, cY-(b/4), cX, cY+(b/4));
	    if (dir == 'u') {
		g.drawLine(cX, cY-(b/4), cX+(b/4), cY);
		g.drawLine(cX, cY-(b/4), cX-(b/4), cY);
	    } else {
		g.drawLine(cX, cY+(b/4), cX+(b/4), cY);
		g.drawLine(cX, cY+(b/4), cX-(b/4), cY);
	    }
	} else if (dir == 'r' || dir =='l') {
	    g.drawLine(cX-(b/4), cY, cX+(b/4), cY);
	    if (dir == 'r') {
		g.drawLine(cX+(b/4), cY, cX, cY+(b/4));
		g.drawLine(cX+(b/4), cY, cX, cY-(b/4));
	    } else {
		g.drawLine(cX-(b/4), cY, cX, cY+(b/4));
		g.drawLine(cX-(b/4), cY, cX, cY-(b/4));
	    }
	} else if (dir == 'X') {
 	    g.drawRect((b*c)+1,(b*r)+1,b-1,b-1);
 	}
	else if (dir == 'C') {
	    g.drawRect((b*c)+1,(b*r)+1,b-1,b-1);
	    if (b > 6) { 
		g.setColor(Color.white);
		g.drawRect((b*c)+2,(b*r)+2,b-3,b-3);
		if (b > 9) { 
		    g.setColor(Color.black);
		    g.drawRect((b*c)+3,(b*r)+3,b-5,b-5);
		}
	    }
	}
    }


    /** **/
    void drawBlock(int row, int col, int size, char dir, 
			  java.awt.Graphics gb, int offsetX, int offsetY) {
	int x = (size * col) + offsetX;
	int y = (size * row) + offsetY;
	int cX = x + (size / 2); //center of square
	int cY = y + (size / 2);
	
	if      (dir == 'u') { gb.setColor(up); 	}
	else if (dir == 'r') { gb.setColor(right);	}
	else if (dir == 'd') { gb.setColor(down);	}
	else if (dir == 'l') { gb.setColor(left);	}
	else if (dir == 'h') { gb.setColor(Color.black); }
	else if (dir == ' ') { gb.setColor(Color.white); }
	gb.fillRect(x,y,size,size);

	if (size >= 5) {
	    gb.setColor(Color.black);
	    if (dir == 'u' || dir =='d') {
		gb.drawLine(cX, cY-(size/4), cX, cY+(size/4));
		if (dir == 'u') {
		    gb.drawLine(cX, cY-(size/4), cX+(size/4), cY);
		    gb.drawLine(cX, cY-(size/4), cX-(size/4), cY);
		} else {
		    gb.drawLine(cX, cY+(size/4), cX+(size/4), cY);
		    gb.drawLine(cX, cY+(size/4), cX-(size/4), cY);
		}
	    } else if (dir == 'r' || dir =='l') {
		gb.drawLine(cX-(size/4), cY, cX+(size/4), cY);
		if (dir == 'r') {
		    gb.drawLine(cX+(size/4), cY, cX, cY+(size/4));
		    gb.drawLine(cX+(size/4), cY, cX, cY-(size/4));
		} else {
		    gb.drawLine(cX-(size/4), cY, cX, cY+(size/4));
		    gb.drawLine(cX-(size/4), cY, cX, cY-(size/4));
		}
	    } 
	}
	return ;
    }

    /** **/
    void drawBug(int row, int col, int size, Graphics gb, 
		 int xOffset, int yOffset) {
	int x = (size * col) + xOffset;
	int y = (size * row) + yOffset;
	drawSquareBug(gb, x, y, size-1);
	return;
    }
    public String getInfo() { 
	return 
	    "Stage = "+n+"\n "+
	    "Step = "+subiteration+"\n\n"+
	    "Minimum Radius = "+doubleToString(minRadius,6)+"\n"+ 
	    "Maximum Radius = "+doubleToString(maxRadius,6)+"\n\n"+
	    "Difference = "+doubleToString(maxRadius-minRadius,6)+
	    "\n\n"+
	    "Minimum Difference = "+doubleToString(minDifference,6)+"\n"+
	    "Maximum Difference = "+doubleToString(maxDifference,6)+"\n";
    }
    
    String doubleToString(double d, int maxLength) {
	String s = Double.toString(d);
	if (s.length() > maxLength)
	    s = s.substring(0,maxLength);
	return s;
    }
    /*
      // I'm not going to implement this just yet.
      // Wait for a later version.
      public void mouseClicked(java.awt.event.MouseEvent e) {
      subiterate(1);
      }
    */
}
