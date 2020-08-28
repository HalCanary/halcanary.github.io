/*
    One Dimentional Walk
    Copyright (C) 2003-2004 Hal Canary, University of Wisconsin-Madison
    hal@ups.physics.wisc.edu

    License Information:

	This program is free software; you can redistribute it and/or
	modify it under the terms of version 2 of the GNU General
	Public License as published by the Free Software Foundation.

	A copy of the license was distributed in the file LICENSE.txt

	This program is distributed in the hope that it will be
	useful, but WITHOUT ANY WARRANTY; without even the implied
	warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
	PURPOSE.  See the GNU General Public License for more details.

    See the README.txt file for version information,
*/

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

/**
 * this class represents a mathematical system.
 **/
public class OneDeeWalk extends MathSystem {
    /** The Constructor **/ 
    public OneDeeWalk() { reset(); }
    /** Current Iteration Number  **/
    public int subit; 
    public int total1; //not sure why should be public, but what the hell?
    public int total2;
    boolean going;
    boolean moved;
    boolean isshort;
    int nodes [];
    int pos;
    int numnodes;
    static int rotorsize = 2;
    Color right;
    Color left;
    /** Current score **/
    double score;
    double maxScore;
    double minScore;
    Font normalFont;
    Font boldFont; 

    /** Restarts the system **/
    public void reset() {
	score = 0;
	maxScore = 0;
	minScore = 0;
	numnodes = 10;
	this.n = 0;
	this.subit = 0;
	nodes = new int [numnodes];
	for (int i = 0; i<numnodes; i++) {
	    nodes[i] = 1;
	}
	nodes[0] = -1;
	nodes[1] = -1;
	going = false;
	isshort = false;
	pos = -1;
	total1 = 0;
	total2 = 0;
	right  = new Color(127,127,255); 
	left   = Color.yellow;
	normalFont = new Font("SansSerif", Font.PLAIN, 20);
	boldFont   = new Font("SansSerif", Font.BOLD,  20);
    }

    boolean resize(){
	int [] newnodes = new int [numnodes * 2];
	for (int i = 0; i<numnodes; i++) {
            newnodes[i] = nodes[i];
	}
	for (int i = 0; i<numnodes; i++) {
            newnodes[numnodes+i] = 1;
	}
	numnodes = numnodes * 2;
	nodes = newnodes;
	return true;
    }
    /** Does a subiteration, if applicable. **/
    public void subiterate(int subiterations){
	for (int i=0; i < subiterations; i++) {
	    subit++;
	    if (!going) {
		going = true;
		moved = true;
		pos = 2;
		subit = 1;
		n++;
	    } else {
		if (moved) {
		    // rotate the router
		    if (pos == 0) {
			pos = -1;
			going = false;
			total1++;
		    } else if (pos == 1) {
			pos = -1;
			going = false;
			total2++;
		    }else {
			nodes[pos]++;
			nodes[pos] = nodes[pos] % rotorsize;
		    } 
		    if (!going) { scoreMe(); }
		    moved = false;
		} else {
		    //move now!!!
		    if (pos > 0) {
			if ( nodes[pos] == 0) {
			    pos--; pos--;
			} else if (nodes[pos] == 1 ) {
			    pos++; 
			    if ( pos == numnodes) {
				resize();
			    }
			}
		    }
		    moved = true;
		} 
	    }
	}
    }
    void scoreMe() {
	score = score();
	if (score > maxScore) { maxScore = score; }
	if (score < minScore) { minScore = score; }
    }
    /** Iterates the system. **/
    /** Does a iteration. **/
    public void iterate(int iterations){
	for (int i=0; i < iterations; i++) {
	    subiterate(1);
	    while(going) { subiterate(1); }
	}
    }
    /**
     * draw() 
     *
     * Draws itself on the graphics buffer.
     *
     * @param sizeX is the x size of region on which to draw.
     * @param sizeY is the y size of region on which to draw.
     **/ 
    public void draw(int sizeX, int sizeY, java.awt.Graphics gb) {
	int nodesToDraw = numnodes;
	if (nodesToDraw > 16) { nodesToDraw = 16; }
	int size = (int)(sizeX/(nodesToDraw*2+1));
	int ydist = (int)(sizeY/2);
	gb.setColor(Color.white);
	gb.fillRect(0,0,sizeX,sizeY);
	gb.drawRect(0,0,sizeX,sizeY);
	gb.setColor(Color.black);

	digraph(gb,'L',2,size,ydist,2);
	for (int i = 3 ; i<nodesToDraw-2 ; i++) {
	    digraph(gb,'r',i,size,ydist,0);
	    //digraph(gb,'r',i,size,ydist,1);
	    if (i%2 == 0)
		digraph(gb,'L',i,size,ydist,2);
	    else
		digraph(gb,'L',i,size,ydist,-2);
	}
	digraph(gb,'L',1,size,ydist,-2);
	digraph(gb,'L',nodesToDraw-2,size,ydist,2);
	//digraph(gb,'r',nodesToDraw-1,size,ydist,1);
	//digraph(gb,'l',nodesToDraw-2,size,ydist,-2);
	digraph(gb,'r',nodesToDraw-2,size,ydist,0);
	digraph(gb,'r',nodesToDraw-1,size,ydist,0);
	//digraph(gb,'r',nodesToDraw-1,size,ydist,-2);
	String l;
	for(int i = 0 ; i < nodesToDraw ; i++) {
	    l = "";
	    if (nodes[i] == -1 && (nodesToDraw < 12)) {
		if (i == 0) l = Integer.toString(total1);
		else if (i == 1) l = Integer.toString(total2);
	    }
	    drawBox(gb,i,size,ydist,nodes[i],l);
	}
	currentBox(gb,pos,size,ydist);
	elipsis(gb,nodesToDraw,size,ydist);

	gb.setColor(Color.black);
	if (!going ) {	gb.setFont(boldFont); }
	else {		gb.setFont(normalFont); }


	gb.drawString("Left Count = " + total1, 50, 50);
	gb.drawString("Right Count = " + total2, (int)sizeX/2 + 150, 50);
	gb.drawString(sqrtdesc(), 50, sizeY - 50);
	int fontHeight = gb.getFontMetrics().getHeight();
	gb.drawString("Stage = " + n, 50, sizeY - 50 - fontHeight);
	//gb.drawString("Stage = " + n, (int)sizeX/2 + 150, sizeY - 50);
	return;
    }
    double score() {
	return (total1 - (total2 * (1.0 + java.lang.Math.sqrt(5))/2 ));
    }
    String sqrtdesc() {
	String out = doubleToString(score());
	return "(Left Count - Right Count * (1 + sqrt(5))/2 = "+out;
    }
    /** nice reperentation to 6 digits. **/
    String doubleToString(double x) {
	String s = Double.toString(x);
	if (x >= 0.0) { s = "+"+s; }
	if (s.length() > 6) { s = s.substring(0,6); }
	return s ;
    }
    void drawBox(Graphics gb, int pos, int size, int y, int state, String l) {
	if (state == -1){
	    gb.setColor(Color.black);
	    gb.fillRect(size+(2*pos*size),y-(size/2),size,size);
	} else if (state == 0) {
	    gb.setColor(left);
	    gb.fillRect(size+(2*pos*size),y-(size/2),size,size);
	    //left
	    gb.setColor(Color.black);
	    drawArrow(gb,(2*(pos+1)*size)-7, y,((2*pos+1)*size)+7 ,y );
	} else if (state == 1) {
	    gb.setColor(right);
	    gb.fillRect(size+(2*pos*size),y-(size/2),size,size);
	    //right
	    gb.setColor(Color.black);
	    drawArrow(gb,size+(2*pos*size)+7, y, (2*(pos+1)*size)-7,y );
	} 
	if (! l.equals("")) {
	    gb.setColor(Color.white);
	    gb.drawString(l,size+(2*pos*size)+5,y);
	}

    }
    void currentBox(Graphics gb, int pos, int size, int y) {
	gb.setColor(Color.black);
	gb.drawRect(size+(2*pos*size),y-(size/2),size-1,size-1);
	gb.drawRect(size+(2*pos*size)+1,y-(size/2)+1,size-3,size-3);
	gb.drawRect(size+(2*pos*size)+2,y-(size/2)+2,size-5,size-5);
	gb.setColor(Color.white);
	gb.drawRect(size+(2*pos*size)+3,y-(size/2)+3,size-7,size-7);
	gb.drawRect(size+(2*pos*size)+4,y-(size/2)+4,size-9,size-9);
	gb.drawRect(size+(2*pos*size)+5,y-(size/2)+5,size-11,size-11);
    }
    void elipsis(Graphics gb, int pos, int size, int y) {
	gb.setColor(Color.black);
	int radius = (int) (size / 8);
	fillCircle(gb,radius,(2*pos*size)+radius*2,y);
	fillCircle(gb,radius,(2*pos*size)+radius*5,y);
	fillCircle(gb,radius,(2*pos*size)+radius*8,y);
    }

    void drawArrow(Graphics gb, int x1, int y1, int x2, int y2) {
	gb.drawLine(x1,y1,x2,y2);
	double angle = java.lang.Math.atan2(y2-y1,x2-x1);
	double a1 = angle - (3 * java.lang.Math.PI / 4);
	double a2 = angle + (3 * java.lang.Math.PI / 4);
 	int [] xs = {x2,
 		     (int)(x2+5*java.lang.Math.cos(a1)),
 		     (int)(x2+5*java.lang.Math.cos(a2))};
 	int [] ys = {y2,
 		     (int)(y2+5*java.lang.Math.sin(a1)),
 		     (int)(y2+5*java.lang.Math.sin(a2))};
 	gb.fillPolygon(xs,ys,3);
	// 	gb.drawLine(x2,y2,(int)(x2+5*java.lang.Math.cos(a1)),
	// 		    (int)(y2+5*java.lang.Math.sin(a1)));
	// 	gb.drawLine(x2,y2,(int)(x2+5*java.lang.Math.cos(a2)),
	// 		    (int)(y2+5*java.lang.Math.sin(a2)));
    }
    void digraph(Graphics gb,char dir, int pos, int size, int y, int h) {
	int right = size + (2*pos*size) - 5;
	int left = (2*pos*size) + 5;
	if (dir == 'r') {
	    drawArrow(gb, left, y + 12*h, right,y + 12*h);
	} else if (dir == 'l') {
	    drawArrow(gb, right, y + 12*h, left,y + 12*h);
	} else if (dir == 'R') {
	    drawArrow(gb, left, y + 12*h, right+(size*2),y + 12*h);
	} else if (dir == 'L') {
	    drawArrow(gb, right+(size*2), y + 12*h, left, y + 12*h);
	}
    }
    public String getInfo() {
	return 
	    "Stage = "+n
	    +"\nStep = "+subit
	    +"\n\nLeft Sink Count = "+total1
	    +"\nRight Sink Count = "+total2
	    +"\n\n"+sqrtdesc()
	    +"\n\n"+
	    "Max Score = "+doubleToString(maxScore)+"\n"+
	    "Min Score = "+doubleToString(minScore)+"\n";;
    }
    void drawCircle(Graphics gb, int radius, int x, int y) {
	gb.drawOval(x-radius,y-radius,radius*2,radius*2);
	return;
    }
    void fillCircle(Graphics gb, int radius, int x, int y) {
	gb.fillOval(x-radius,y-radius,radius*2,radius*2);
	return;
    }
}
