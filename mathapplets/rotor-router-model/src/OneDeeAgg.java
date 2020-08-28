/*
    1-D Aggregation Rotor System
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

public class OneDeeAgg extends MathSystem {
    /** The Constructor **/ 
    public OneDeeAgg() { reset(); }
    /** Current Iteration Number  **/
    public int subit; 
    public int numright;
    public int numleft;
    Font normalFont;
    Font boldFont; 
    boolean going;
    boolean moved;
    boolean isshort;
    boolean right;
    int nodes [];
    int numnodes;
    int center;
    int pos;
    static int INITIAL_NUM = 15;
    static int rotorsize = 2;
    Color rightC;
    Color leftC;

    /** Current score **/
    double score;
    double maxScore;
    double minScore;


    /** Restarts the system **/
    public void reset() {
	score = 0;
	maxScore = 0;
	minScore = 0;
	this.n = 1;
	this.subit = 0;
	numnodes = INITIAL_NUM;
	center = (INITIAL_NUM - 1)/2;
	nodes = new int[numnodes];
	for (int i=0;i<numnodes; i++){ nodes[i] = -1; } 
	nodes[center] = 0;
	//going = false;

	going = true;
	moved = true;
	pos = center;
	subit = 1;
	
	//pos = -1;
	numright=0;
	numleft=0;
	normalFont = new Font("SansSerif", Font.PLAIN, 20);
	boldFont   = new Font("SansSerif", Font.BOLD,  20);

	rightC  = new Color(127,127,255); 
	leftC   = Color.yellow;
    }
    /** Does a subiteration, if applicable. **/
    public void subiterate(int subiterations){
	for (int i=0; i < subiterations; i++) {
	    subit++;
	    if (!going) {
		going = true;
		moved = true;
		pos = center;
		subit = 1;
		n++;
	    } else if (pos>=0){
		if (moved) {
		    // rotate the router
		    if (pos == 1 || pos == numnodes)
			resize();
		    if (nodes[pos] == -1) {
			if (!right) {
			    numleft++; numleft++;
			    nodes[pos] = 0;
			    nodes[pos-1] = 0;
			} else
			    numright++;
			    nodes[pos] = 0;
			pos = -1;
			going = false;
			scoreMe();
		    } else {
			nodes[pos]++;
			nodes[pos] = nodes[pos] % rotorsize;
		    } 
		    moved = false;
		} else {
		    //move now!!!
		    if (pos >= 0 && pos <= numnodes-1) {
			if ( nodes[pos] == 0) {
			    right = false;
			    pos--;
			} else if (nodes[pos] == 1 ) {
			    right = true;
			    pos++;
			}
		    }
		    moved = true;
		} 
	    } else {
		System.out.println("Error: evil pos.");
	    }
	}
    }

    void scoreMe() {
	score = score();
	if (score > maxScore) { maxScore = score; }
	if (score < minScore) { minScore = score; }
    }

    void resize() {
	numnodes = (numnodes * 2) + 1;
	int newcenter = (numnodes-1)/2;
	int diff = newcenter - center;
	int newnodes [] = new int [numnodes] ;
	for (int i=0;i<numnodes; i++){ 
	    if (((i-diff)>=0)&&((i-diff)<nodes.length)) {
		newnodes[i] = nodes[i-diff];
	    } else {
		newnodes[i] = -1; 
	    }
	} 
	pos = pos+diff;
	center = newcenter;
	nodes = newnodes;
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
	gb.setFont(normalFont);
	gb.setColor(Color.white);
	gb.fillRect(0,0,sizeX,sizeY);
	gb.drawRect(0,0,sizeX,sizeY);
	gb.setColor(Color.black);
	//gb.drawLine((int)sizeX/2,0,(int)sizeX/2,sizeY);
	int ydist = (int)(sizeY/2);
	int size;
	int xorig;
	int dCenter;
	dCenter = (int)(sizeX/2);
	if (numnodes <= 16) {
	    size = (int)(sizeX/(numnodes*2+1));
	    dCenter = (int) size*(numnodes*2+1)/2;
	    //gb.drawLine(dCenter,0,dCenter,sizeY);
	    gb.drawLine(dCenter,ydist-100,dCenter,ydist+100);
	    for (int i=0;i<numnodes;i++){
		drawBox(gb,i,size,ydist,nodes[i],"");
	    }
	    if (pos >= 0){
		currentBox(gb,pos,size,ydist);
	    }
	} else if (numnodes <= sizeX) {
	    size = (int)(sizeX/(numnodes));
	    if (size < 1) {size = 1;}
	    xorig = (int)(sizeX - size*numnodes)/2;
	    dCenter = (int)(size*numnodes)/2 + xorig;
	    gb.drawLine(dCenter,ydist-100,dCenter,ydist+100);
	    //gb.drawLine(dCenter,0,dCenter,sizeY);
	    for (int i=0;i<numnodes;i++) {
		if (nodes[i] >= 0) 
		    drawSpectrum(gb, i, size, xorig, ydist, nodes[i]);
	    }
	    if (pos >= 0){
		drawSpectrumBug(gb, pos, size, xorig, ydist);
	    }
	} else {
	    /* THIS IS FOR THE SQUEEZED CASE.  FIX ME*/
	    int density = (int)(numnodes/sizeX);
	    int width = (int)(numnodes/density);
	    xorig = (int)(sizeX - width)/2;
	    //dCenter = (int)(numnodes)/2 + xorig;
	    dCenter = (int)(xorig+(width/2));
	    gb.drawLine(dCenter,ydist-100,dCenter,ydist+100);
	    //gb.drawLine(dCenter,0,dCenter,sizeY);
	    for (int i=0;i<width;i++) {
		int state = nodes[i*density];
		if (state >= 0) {
		    if (state == 0)
			gb.setColor(leftC);
		    else if (state == 1)
			gb.setColor(rightC);
		    gb.fillRect(xorig+(i),ydist-16,1,32);
		}
	    }
	}

	gb.setColor(Color.black);

	gb.drawString("Leftmost = -" + numleft, 50, 50);
	gb.drawString("Rightmost = " + numright, dCenter + 50, 50);

	if (going)
	    gb.setFont(normalFont);
	else 
	    gb.setFont(boldFont);

	gb.drawString(sqrtdesc(), 50, sizeY - 50);
	int fontHeight = gb.getFontMetrics().getHeight();
	gb.drawString("Stage = " + n, 50, sizeY - 50 - fontHeight);
	return;
    }
    
    double score() {
	return (0 - numleft + (numright * java.lang.Math.sqrt(2) ));
    }

    String sqrtdesc() {
	String out;
	double x = score();
	if (x >= 0.0) 
	    out = "+"+Double.toString(x);
	else
	    out = Double.toString(x);
	if (out.length() > 6)
	    out = out.substring(0,6);
	return "(Leftmost + Rightmost * sqrt(2)) = "+out;
    }
    void drawSpectrum(Graphics gb, int pos, int size, int x, int y, int state){
	if (state== -1)
	    gb.setColor(Color.black);
	else if (state == 0)
	    gb.setColor(leftC);
	else if (state == 1)
	    gb.setColor(rightC);
	else
	    gb.setColor(Color.blue);
	gb.fillRect(x+size*pos,y-16,size,32);
	if (size > 3){
	    gb.setColor(Color.black);
	    gb.drawRect(x+size*pos,y-16,size,32);
	}
    }
    void drawSpectrumBug(Graphics gb, int pos, int size, int x, int y){
	if (size > 3){
	    gb.setColor(Color.black);
	    gb.drawRect(x+size*pos,  y-32,  size,   32  );
	    gb.drawRect(x+size*pos+1,y-32+1,size-2, 32-2);
	    gb.drawRect(x+size*pos+2,y-32+2,size-4, 32-4);
	    gb.setColor(Color.white);
	    gb.drawRect(x+size*pos+3,y-32+3,size-6,  32-6);
	    gb.drawRect(x+size*pos+4,y-32+4,size-8,  32-8);
	    gb.drawRect(x+size*pos+5,y-32+5,size-10, 32-10);
	}
    }
    void drawBox(Graphics gb, int pos, int size, int y, int state, String l) {
	if (state == -1){
	    gb.setColor(Color.black);
	    gb.fillRect(size+(2*pos*size),y-(size/2),size,size);
	} else if (state == 0) {
	    //gb.setColor(Color.red);
	    gb.setColor(leftC);
	    gb.fillRect(size+(2*pos*size),y-(size/2),size,size);
	    //left
	    gb.setColor(Color.black);
	    drawArrow(gb,(2*(pos+1)*size)-7, y,((2*pos+1)*size)+7 ,y );
	} else if (state == 1) {
	    //gb.setColor(Color.green);
	    gb.setColor(rightC);
	    gb.fillRect(size+(2*pos*size),y-(size/2),size,size);
	    //right
	    gb.setColor(Color.black);
	    drawArrow(gb,size+(2*pos*size)+7, y, (2*(pos+1)*size)-7,y );
	} if (! l.equals("")) {
	    gb.setColor(Color.white);
	    gb.drawString(l,size+(2*pos*size)+5,y);
	}

    }
    void currentBox(Graphics gb, int pos, int size, int y) {
	if (size > 20) {
	    gb.setColor(Color.black);
	    gb.drawRect(size+(2*pos*size),y-(size/2),size-1,size-1);
	    gb.drawRect(size+(2*pos*size)+1,y-(size/2)+1,size-3,size-3);
	    gb.drawRect(size+(2*pos*size)+2,y-(size/2)+2,size-5,size-5);
	    gb.setColor(Color.white);
	    gb.drawRect(size+(2*pos*size)+3,y-(size/2)+3,size-7,size-7);
	    gb.drawRect(size+(2*pos*size)+4,y-(size/2)+4,size-9,size-9);
	    gb.drawRect(size+(2*pos*size)+5,y-(size/2)+5,size-11,size-11);
	} else if (size > 5) {
	    gb.setColor(Color.black);
	    gb.drawRect(size+(2*pos*size)-3,y-(size/2)-3,size+5,size+5);
	    gb.drawRect(size+(2*pos*size)-2,y-(size/2)-2,size+3,size+3);
	    gb.drawRect(size+(2*pos*size)-1,y-(size/2)-1,size+1,size+1);
	    gb.setColor(Color.white);
	    gb.drawRect(size+(2*pos*size)  ,y-(size/2),  size-1,size-1);
	    gb.drawRect(size+(2*pos*size)+1,y-(size/2)+1,size-3,size-3);
	    gb.drawRect(size+(2*pos*size)+2,y-(size/2)+2,size-5,size-5);
	}
    }

    void drawArrow(Graphics gb, int x1, int y1, int x2, int y2, Color c) {
	gb.setColor(c);
	drawArrow(gb, x1, y1, x2, y2);
	return;
    }
    void drawArrow(Graphics gb, int x1, int y1, int x2, int y2) {
	gb.drawLine(x1,y1,x2,y2);
	//gb.drawLine(x1,y1+1,x2,y2+1);
	//gb.drawLine(x1,y1-1,x2,y2-1);
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
	//gb.drawLine(x2,y2,(int)(x2+5*java.lang.Math.cos(a1)),
	//	    (int)(y2+5*java.lang.Math.sin(a1)));
	//gb.drawLine(x2,y2,(int)(x2+5*java.lang.Math.cos(a2)),
	//	    (int)(y2+5*java.lang.Math.sin(a2)));
    }
    void digraph(Graphics gb,char dir, int pos, int size, int y, int h) {
	int right = size + (2*pos*size) - 5;
	int left = (2*pos*size) + 5;
	if (dir == 'r') {
	    drawArrow(gb, left, y + 10*h, right,y + 10*h);
	} else if (dir == 'l') {
	    drawArrow(gb, right, y + 10*h, left,y + 10*h);
	} else if (dir == 'R') {
	    drawArrow(gb, left, y + 10*h, right+(size*2),y + 10*h);
	} else if (dir == 'L') {
	    drawArrow(gb, right+(size*2), y + 10*h, left, y + 10*h);
	}
    }
    public String getInfo() {
	return 
	    "Stage = "+n
	    +"\nleftmost = "+numleft
	    +"\nrightmost = "+numright
	    +"\n\n"
	    +sqrtdesc()+"\n\n"+
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

}


