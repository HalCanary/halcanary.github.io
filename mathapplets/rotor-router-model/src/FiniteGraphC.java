/*
    Finite Graph - System 'C'
    Copyright (C) 2003 Hal Canary, University of Wisconsin-Madison
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

//import java.awt.*;
import java.awt.Graphics;
import java.awt.Color;

/**
 * this class represents a mathematical system.
 **/
public class FiniteGraphC extends MathSystem {
    /** Current Iteration Number  **/
    public int subit; 
    public int total1;
    public int total2;
    boolean going;
    boolean moved;
    int nodes [];
    int pos;
    static int numnodes = 12;
    /**
     * The Constructor
     **/ 
    public FiniteGraphC() {
	reset();
    }
    /** Restarts the system **/
    public void reset() {
	this.n = 0;
	this.subit = 0;
	nodes = new int [numnodes];
	nodes[0] = -1;
	nodes[1] = 0;
	nodes[2] = 0;
	nodes[3] = 0;
	nodes[4] = 0;
	nodes[5] = 0;
	nodes[6] = 0;
	nodes[7] = 0;
	nodes[8] = 0;
	nodes[9] = 0;
	nodes[10] = 0;
	nodes[11] = -1;
	going = false;
	pos = -1;
	total1 = 0;
	total2 = 0;
    }
    /** Does a subiteration, if applicable. **/
    public void subiterate(int subiterations){
	for (int i=0; i < subiterations; i++) {
	    if (!going) {
		going = true;
		moved = true;
		pos = 1;
		subit=1;
		n++;
	    } else {
		subit++;
		if (moved) {
		    // rotate the router
		    if (pos <= 0) {
			pos = -1;
			going = false;
			total1++;
		    } else if ( pos >= numnodes-1) {
			pos = -1;
			going = false;
			total2++;
		    } else if (pos > 0 && pos < numnodes-1) {
			nodes[pos]++;
			nodes[pos] = nodes[pos] % 3;
		    } 
		    moved = false;
		} else {
		    //move now!!!
		    if (pos > 0 && pos < numnodes-1) {
			if ( nodes[pos] == 0) {
			    pos--;
			} else if (nodes[pos] == 1 ) {
			    pos++;
			} else if ( nodes[pos] == 2) {
			    pos++; 
			    if (pos != numnodes-1) pos++;
			}
		    }
		    moved = true;
		} 
	    }
	}
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
	int size = (int)(sizeX/(numnodes*2+1));
	int ydist = (int)(sizeY/2);
	gb.setColor(Color.white);
	gb.fillRect(0,0,sizeX,sizeY);
	gb.drawRect(0,0,sizeX,sizeY);
	gb.setColor(Color.black);
	digraph(gb,'l',1,size,ydist,0);

	for (int i = 2 ; i<numnodes-1 ; i++) {
	    digraph(gb,'l',i,size,ydist,0);
	    digraph(gb,'r',i,size,ydist,1);
	    if (i%2 == 0)
		digraph(gb,'R',i,size,ydist,2);
	    else
		digraph(gb,'R',i,size,ydist,-2);
	}
	digraph(gb,'r',numnodes-1,size,ydist,1);
	digraph(gb,'r',numnodes-1,size,ydist,2);
	digraph(gb,'r',numnodes-1,size,ydist,-2);
	String l;
	for(int i = 0 ; i < numnodes ; i++) {
	    if (nodes[i] == -1) {
		if (i == 0) l = Integer.toString(total1);
		else  l = Integer.toString(total2);
	    } else { l = "";  }
	    drawBox(gb,i,size,ydist,nodes[i],l);
	}
    currentBox(gb,pos,size,ydist);
    }
    void drawBox(Graphics gb, int pos, int size, int y, int state,String l) {
	if (state == -1){
	    gb.setColor(Color.black);
	    gb.fillRect(size+(2*pos*size),y-(size/2),size,size);
	} else if (state == 0) {
	    gb.setColor(Color.red);
	    gb.fillRect(size+(2*pos*size),y-(size/2),size,size);
	    //left
	    gb.setColor(Color.black);
	    drawArrow(gb,(2*(pos+1)*size)-5, y,((2*pos+1)*size)+5 ,y );
	} else if (state == 1) {
	    gb.setColor(Color.green);
	    gb.fillRect(size+(2*pos*size),y-(size/2),size,size);
	    //right
	    gb.setColor(Color.black);
	    drawArrow(gb,size+(2*pos*size)+5, y, (2*(pos+1)*size)-5,y );
	} else if (state == 2) {
	    gb.setColor(Color.yellow);
	    gb.fillRect(size+(2*pos*size),y-(size/2),size,size);
	    //right More
	    gb.setColor(Color.black);
	    drawArrow(gb,size+(2*pos*size)+5, y, (2*(pos+1)*size)-5,y );
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

    void drawArrow(Graphics gb, int x1, int y1, int x2, int y2) {
	gb.drawLine(x1,y1,x2,y2);
	double angle = java.lang.Math.atan2(y2-y1,x2-x1);
	double a1 = angle - (3 * java.lang.Math.PI / 4);
	double a2 = angle + (3 * java.lang.Math.PI / 4);
	gb.drawLine(x2,y2,(int)(x2+5*java.lang.Math.cos(a1)),
		    (int)(y2+5*java.lang.Math.sin(a1)));
	gb.drawLine(x2,y2,(int)(x2+5*java.lang.Math.cos(a2)),
		    (int)(y2+5*java.lang.Math.sin(a2)));
    }
    void digraph(Graphics gb,char dir, int pos, int size, int y, int h) {
	int right = size + (2*pos*size) - 5;
	int left = (2*pos*size) + 5;
	if (dir == 'r') {
	    drawArrow(gb,left, y + 10*h, right,y + 10*h);
	} else if (dir == 'l') {
	    drawArrow(gb,right, y + 10*h, left,y + 10*h);
	} else if (dir == 'R') {
	    drawArrow(gb,left, y + 10*h, right+(size*2),y + 10*h);
	}
    }
    public String getInfo() {
	String ratio1 = "";
	String ratio2 = "";
	try { ratio1 = Double.toString(((double)total2)/total1);
	} catch (java.lang.ArithmeticException evil) {
	    ratio1 = ""+total2+"/"+total1; }
	try { ratio2 = Double.toString(((double)total1)/total2);
	} catch (java.lang.ArithmeticException evil) {
	    ratio2 = ""+total2+"/"+total1; }
	if (ratio1.length() > 6)
	    ratio1 = ratio1.substring(0,6);
	if (ratio2.length() > 6)
	    ratio2 = ratio2.substring(0,6);
	return 
	    "Stage = "+n
	    +"\nStep = "+subit
	    +"\n\nLeft Sink Count = "+total1
	    +"\nRight Sink Count = "+total2
	    +"\n\n(Right Sink Count)/(Left Sink Count) = "+ratio1
	    +"\n\n(Left Sink Count)/(Right Sink Count) = "+ratio2
	    +"\n\n";
    }
}


