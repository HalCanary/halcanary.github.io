/* 
    Two-Dimensional Rotor-Walk System
    Copyright (C) 2003-2004  Hal Canary, Univerity of Wisconsin-Madison
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
import java.lang.System;

/**  Two-Dimensional Rotor-Walk System  **/
public class TwoDeeWalk extends MathSystem{

    // MAX_SIZE should be a power of 2.
    /** Maximum size I want this system to get to.**/
    public static int MAX_SIZE = 2024;

    /** Maximum size that gets displayed.**/
    public static int MAX_DISPLAY_SIZE = 256;

    /** document me! **/
    static int SKIP_PIXELS=5;

    /** Current Iteration Number  **/
    public int n; 
    /** Current Subiteration Number  **/
    public int subit;

    /** Current score **/
    double score;
    double maxScore;
    double minScore;
    String [] scoreSt;
    
    Font normalFont;
    Font boldFont; 

    int lastSizeX;
    int lastSizeY;

    int aSize;
    char [] [] sandpile ;
    int middle;
    int posR;
    int posC;
    int oldPosR;
    int oldPosC;
    char lastDir;
    

    boolean moved;
    boolean lastSource;

    int numSource;
    int numDrain;

    boolean filled;	/* the array is filled to MAX_SIZE */
    boolean done;	/* the iteration is done */

    Color up;
    Color down;
    Color right;
    Color left;

    /** Current animation position  **/
    double animate; 
	

    /**
     * The Constructor
     **/ 
    public TwoDeeWalk() {
	reset();
    }
    /** Restarts the system **/
    public void reset() {
	score = 0;
	maxScore = 0;
	minScore = 0;
	lastSizeX = 10;
	lastSizeY = 10;
	moved=true;
	lastSource = true;
	n = 1;
	numSource =0;
	numDrain = 0;
	scoreSt = new String[4];
	scoreSt[0] = "(# of captures at sink)";
	scoreSt[1] = "    - (Pi/8) * stage";
	scoreSt[2] = "  = 0 - 0.3927 * 0";
	scoreSt[3] = "  = 0";
	animate=1;
	filled = false;
	done = false;
	subit = 0;
	aSize = 8;
	middle = aSize/2;
	blank(aSize,sandpile);
	posR = aSize/2;
	posC = aSize/2 - 1;
	up     = new Color(255,63,63); 
	down   = Color.green;
	right  = new Color(127,127,255); 
	left   = Color.yellow;
	normalFont = new Font("SansSerif", Font.PLAIN, 20);
	boldFont   = new Font("SansSerif", Font.BOLD,  20);
    }
    void blank(int blanksize, char[][] pile) {
	sandpile = new char [blanksize][blanksize];
	for(int r = 0 ; r < blanksize ; r++) {
	    for(int c = 0 ; c < blanksize ; c++) {
		/*
		if ((r >= c) && (r+c < blanksize-1)) {
		    sandpile[r][c] = 'u' ;
		} else if ((r < c) && (r+c < blanksize)) {
		    sandpile[r][c] = 'r' ;
		} else if ((r <= c) && (r+c >= blanksize)) {
		    sandpile[r][c] = 'd' ;
		} else if ((r > c) && (r+c >= blanksize-1)) {
		    sandpile[r][c] = 'l' ;
		*/
		if ((r > c) && (r+c <= blanksize-1)) {
		    sandpile[r][c] = 'u' ;
		} else if ((r <= c) && (r+c < blanksize-1)) {
		    sandpile[r][c] = 'r' ;
		} else if ((r < c) && (r+c >= blanksize-1)) {
		    sandpile[r][c] = 'd' ;
		} else if ((r >= c) && (r+c > blanksize-1)) {
		    sandpile[r][c] = 'l' ;
		/*
		if ((c+blanksize-r)%4 == blanksize%4+1) {
		    sandpile[r][c] = 'u' ;
		} else if ((c+blanksize-r)%4 == blanksize%4+3) {
		    sandpile[r][c] = 'd' ;
		} else if ((r+c)%4 == 2) {
		    sandpile[r][c] = 'l' ;
		} else if ((r+c)%4 == 0) {
		    sandpile[r][c] = 'r' ;
		*/
		} else {
		    //shouldn't happen;
		    System.err.println("Othercase 1");
		    sandpile[r][c] = 'B' ;
		}
	    }
	}
	sandpile[(blanksize/2)-1][blanksize/2] = 'h';
    }
    /** iterate the system.  
     * @param number how many times to iterate. usually 1.
     **/
    public void iterate(int number) {
	if (filled) { return; }
	for(int num = 0; (num < number) && (!filled); num++) {
	    iterate();
	} 
	return;
    }
    /** 
     * iterate the system once.
     **/
    private void iterate() {
	if (filled) { return; }
	if (done) {
	    n++;
	    moved=true;
	    subit = 0;
	    posR = aSize/2;
	    posC = aSize/2 - 1;
	    done = false;
	}
	while ((! done) && (! filled)) {
	    subiterate(1); 
	}
    }
    /** Does a subiteration, if applicable. **/
    public void subiterate(int subits) { 
	if (filled) { return; }
	if (done) { // new iteration
	    n++;
	    moved = true;
	    subit = 0;
	    posR = aSize/2;
	    posC = aSize/2 - 1;
	    done = false;
	}
	else if (moved) {
	    subit++;
	    char dir = sandpile[posR][posC];
	    // ROTATE the CELL.
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
	    } else {
		System.out.println("shouldn't happen! ");	
	    }
	    moved = false;
	} else {
	    char dir = sandpile[posR][posC];
	    oldPosR = posR;
	    oldPosC = posC;
	    // "filled = !resizeArray();" changed to resizeArray();
	    if (dir == 'u') {
		posR--;	
		if (posR < 0) { resizeArray(); }
	    } else if (dir == 'r') {
		posC++;	
		if (posC == aSize) { resizeArray(); } 
	    } else if (dir == 'd') {
		posR++;	
		if (posR == aSize) { resizeArray(); }
	    } else if (dir == 'l') {
		posC--;	
		if (posC < 0) { resizeArray(); }
	    } else {
		System.out.println("shouldn't happen! ");	
	    }
	    moved = true;
	    if (filled) { return ;}
	    if (sandpile[posR][posC] == 'h') {
		numDrain++;
		lastSource = false;
		done = true;
	    } else if (posR == aSize/2 && posC == (aSize/2)-1 
		       && subit > 1) {
		numSource++;
		lastSource = true;
		done = true;
	    }
	    if (done) { scoreMe(); }
	}
	return ;
    }

    void scoreMe() {
	score = score();
	if (score > maxScore) { maxScore = score; }
	if (score < minScore) { minScore = score; }
	scoreSt[0] = "(# of captures at sink)";
	scoreSt[1] = "    - (Pi/8) * stage";
	scoreSt[2] = "  = "+numDrain+" - 0.3927 * "+n;
	scoreSt[3] = "  = "+getScore();

    }

    /** return true if successfull. **/
    private boolean resizeArray() {
        int newArraySize = aSize * 2;
	if (newArraySize > MAX_SIZE) {
	    //do something here to stop this!!!!
	    filled = true;
	    return false;
	}
	int newMiddle = aSize; //(newArraySize) / 2;  
	int shift = newMiddle - middle;
	char [] [] oldPile = sandpile;	
	blank(newArraySize,sandpile);
	//25% wasteful!  What to do? Worry about smething else!
        for(int r = 0 ; r < aSize ; r++) {
            for(int c = 0 ; c < aSize ; c++) {
                sandpile[r+shift][c+shift] = oldPile[r][c] ;
            }
        }
	oldPile = null;
	//garbage collect???
        middle = newMiddle;
	posR = posR + shift;
	posC = posC + shift;
	oldPosR = oldPosR + shift;
	oldPosC = oldPosC + shift;
	aSize = newArraySize;
	return true;
    }
    /** 
     * print the current system out to System.out.
     * @see <a href="http://ups.physics.wisc.edu/java/java2-api/java/lang/System.html#out">System.out</a>
     **/
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

    int squareSize(int sizeX, int sizeY) {
	int square; //size of thing to draw on.
	if (sizeX < sizeY) { square = sizeX; }
	else { square = sizeY; }
	return square;
    }
    int blockSize(int square) {
	int block = square / this.aSize;
	if (block < 1) { block=1; }
	return block;
    }
    /**
     * draw() 
     *
     * Draws itself on the graphics buffer.
     *
     * @param sizeX is the x size of region on which to draw.
     * @param sizeY is the y size of region on which to draw.
     **/ 
    public void draw(int sizeX, int sizeY, java.awt.Graphics gb){
	lastSizeX = sizeX;
	lastSizeY = sizeY;
	//size of thing to draw on.
	int square = squareSize(sizeX, sizeY); 

	//size of each thing.
	int block = blockSize(square);

	gb.clearRect(0,0,sizeX,sizeY) ;
	gb.setColor(Color.white);
	gb.fillRect(0,0,aSize * block,aSize * block) ;
	gb.setColor(Color.black);

	if (done) {	gb.setFont(boldFont); }
	else {		gb.setFont(normalFont); }

	int fontHeight = gb.getFontMetrics().getHeight();
	gb.drawString(scoreSt[0], square, 20+(fontHeight*1));
	gb.drawString(scoreSt[1], square, 20+(fontHeight*2));
	gb.drawString(scoreSt[2], square, 20+(fontHeight*3));
	gb.drawString(scoreSt[3], square, 20+(fontHeight*4));

	if (this.aSize <= MAX_DISPLAY_SIZE) {
	    //draw black rectange
	    for (int r = 0 ; r < aSize ; r++) {
		for (int c = 0 ; c < aSize ; c++) {
		    char dir = sandpile[r][c];
		    if (dir == 'u' || dir == 'r' || dir == 'd' || dir == 'l') {
			drawBlock(r, c, block, dir, gb, 0, 0);
		    } else if (sandpile[r][c] == 'h') {
			//is the sink.  has no rotor.  is black hole.
			gb.setColor(Color.black);
			gb.fillRect((block * c), (block * r), block, block);
		    }
		}
	    }
	    if (block >= 5) {
		//lable current thing position 
		drawBug(posR, posC, block, gb, 0, 0);
	    }
	} else {
	    /* ZOOM MODE */
	    block = square / MAX_DISPLAY_SIZE;
	    int halfDisplaySize = MAX_DISPLAY_SIZE/2;
	    if (block < 1) { block=1; }
	    //draw black rectange
	    for (int r = middle-halfDisplaySize ; 
		 r < middle+halfDisplaySize ; r++) {
		for (int c = middle-halfDisplaySize ; 
		     c < middle+halfDisplaySize ; c++) {
		    char dir = sandpile[r][c];
		    if (dir == 'u' || dir == 'r' || 
			dir == 'd' || dir == 'l') {
			drawBlock(r, c, block, dir, gb, 
				  -block*(middle-halfDisplaySize), 
				  -block*(middle-halfDisplaySize));
		    } else if (sandpile[r][c] == 'h') {
			//is the sink.  has no rotor.  is black hole.
			gb.setColor(Color.black);
			gb.fillRect(block * (c - middle + halfDisplaySize), 
				    block * (r - middle + halfDisplaySize), 
				    block, block);
		    }
		}
	    }
	    if (block >= 5) {
		//lable current thing position 
		drawBug(posR, posC, block, gb, 
			-block*(middle-halfDisplaySize), 
			-block*(middle-halfDisplaySize));
	    }
	}
	return;
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

    /** draws the Bug at a given position.  **/
    void drawBug(int row, int col, int size, Graphics gb, 
		 int xOffset, int yOffset) {
	int x = (size*col) + xOffset ;
	int y = (size*row) + yOffset;
	int s = size;

	if (s > 9) { 
	    gb.setColor(Color.black);
	    gb.drawRect(x,   y,   s-1,  s-1);
	    gb.drawRect(x+1, y+1, s-3,  s-3);
	    gb.drawRect(x+2, y+2, s-5,  s-5);
	    gb.setColor(Color.white);
	    gb.drawRect(x+3, y+3, s-7,  s-7);
	    gb.drawRect(x+4, y+4, s-9,  s-9);
	    gb.drawRect(x+5, y+5, s-11, s-11);
	} else if (s > 6) { 
	    gb.setColor(Color.black);
	    gb.drawRect(x,   y,   s-1,  s-1);
	    gb.drawRect(x+1, y+1, s-3,  s-3);
	    gb.setColor(Color.white);
	    gb.drawRect(x+2, y+2, s-5,  s-5);
	    gb.drawRect(x+3, y+3, s-7,  s-7);
	}
	return ;
    }

    /** returns (# of captures at sink) - (Pi/8) * stage **/
    double score() {
	return this.numDrain - (java.lang.Math.PI / 8 * this.n);
    }
    String getScore() {
	return doubleToString(score());
    }
    /** **/
    public String getInfo() { 
	String sco = Double.toString(((double)8*numDrain)/n);
	if (sco.length() > 6)
	    sco = sco.substring(0,6);
	return "Number of captures at source = " + numSource +
	    "\nNumber of captures at sink = " + numDrain +
	    "\nStage = "+n+"\n"+
	    //"\n\n(# of captures at sink)/Stage = " + sco+
	    //"\n\n(# of captures at sink) - (Pi/8) * stage = " 
	    //+getScore()+"\n\n"+
	    "\n"+scoreSt[0]+
	    "\n"+scoreSt[1]+
	    "\n"+scoreSt[2]+
	    "\n"+scoreSt[3]+"\n\n"+
	    "Max Score = "+doubleToString(maxScore)+"\n"+
	    "Min Score = "+doubleToString(minScore)+"\n";
    }
    /** nice reperentation to 6 digits. **/
    String doubleToString(double x) {
	String s = Double.toString(x);
	if (x >= 0.0) { s = "+"+s; }
	if (s.length() > 6) { s = s.substring(0,6); }
	return s ;
    }

    /**
     *  Returns number of intermediate steps in the animation.
     *  Notes:  Should return (number of pixels in a tile)/2.
     *          Then the animation will go faster later on.
     **/
    public int animateNum() { 
	//size of thing to draw on.
	int square = squareSize(lastSizeX, lastSizeY); 
	//size of each thing.
	int block = blockSize(square);
	return block/SKIP_PIXELS; 
    }
    /** 
     *  Draws the Nth intermediate animation.  1 <= aniamte <= animateNum().
     *  Notes:  Don't repeat code. if animate==1, call into draw(), then
     *          draw OVER the active region.  Else, just redraw the active
     *          region.  Assume that the graphics is attached to an image
     *          that is not erased between takes.
     *
     * 	    for (int animate = 1; animate <= animateNum(); animate++)
     **/
    public void drawAnimate(int sizeX, int sizeY, java.awt.Graphics gb, 
			    int animate) { 
	if (animate == 1) {
	    draw(sizeX, sizeY, gb); 
	}
	//do something else.
	if (!moved) { return ;}
	
	//size of thing to draw on.
	int square = squareSize(sizeX, sizeY); 
	//size of each thing.
	int block = blockSize(square);

	//	lastDir should be set correctly.  Is it???;
	char dir = sandpile[posR][posC];
	if (dir == 'u' || dir == 'r' || dir == 'd' || dir == 'l') {
	    drawBlock(posR, posC, block, dir, gb, 0, 0);
	} else if (sandpile[posR][posC] == 'h') {
	    //is the sink.  has no rotor.  is black hole.
	    gb.setColor(Color.black);
	    gb.fillRect((block * posC), (block * posR), block, block);
	}
	drawBlock(posR, posC, block, dir, gb, 0, 0);
	if (subit==0) {
	    drawBug(posR, posC, block, gb, 1, 1);
	} else {
	    int offset = block - (animate*SKIP_PIXELS);
	    if (lastDir == 'u') {
		drawBlock(posR+1, posC, block,sandpile[posR+1][posC], gb,0,0);
		drawBug(posR, posC, block, gb, 0, offset);
	    } else if (lastDir == 'r') {
		drawBlock(posR, posC-1, block,sandpile[posR][posC-1], gb,0,0);
		drawBug(posR, posC, block, gb, -1*offset, 0);
	    } else if (lastDir == 'd') {
		drawBlock(posR-1, posC, block,sandpile[posR-1][posC], gb,0,0);
		drawBug(posR, posC, block, gb, 0, -1*offset);
	    } else if (lastDir == 'l') {
		drawBlock(posR, posC+1, block,sandpile[posR][posC+1], gb,0,0);
		drawBug(posR, posC, block, gb, offset, 0);
	    }
	}
	return ; 
    }
}
