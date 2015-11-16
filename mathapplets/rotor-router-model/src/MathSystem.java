/*
    Java Bits - Useful Java Classes for discrete systems.
    Copyright (C) 2003-2004  Hal Canary, University of Wisconsin-Madison
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

/**
 * MathSystem Class: this class represents a generic mathematical
 * system. Subclass this to make use of its interface.
 **/
public abstract class MathSystem {

    /* //  I'm not going to implement this funtionality just now.
       //  At some later version, MathSystem might implement 
       //  MouseListener.
       implements java.awt.event.MouseListener{ */

    /** Current Iteration Number  **/
    public int n; 
    /** Current Subiteration Number  **/
    public int subit; 
    /**
     * The Constructor.
     **/ 
    public MathSystem() {}
    /**
     * Draws the current state of the sytem on the graphics buffer.
     *
     * @param sizeX is the x size of region on which to draw.
     * @param sizeY is the y size of region on which to draw.
     * @param gb is the graphics buffer on which to draw.  It 
     *           may be from an image or a canvas.
     **/ 
    public abstract void draw(int sizeX, int sizeY, java.awt.Graphics gb);
    /** 
     * Iterates the system. 
     * @param iterations is the number of iterations.  Usually 1.
    **/
    public abstract void iterate(int iterations);
    /** 
     * Does a subiteration, if applicable. 
     * @param subiterations is the number of subiterations.  Usually 1.
     **/
    public abstract void subiterate(int subiterations);
    /** 
     * Restarts the system. You will lose all state information.
     **/
    public abstract void reset();
    /** 
     *  Returns a long string describing some info about current state. 
     *  Override if you want such a function.
     **/
    public String getInfo() { return ""; }
    /** 
     *  Override this function if you want the system to hava an animation.  
     *  Returns number of intermediate steps in the animation.
    **/
    public int animateNum() { return 0; }
    /** 
     *  Override this function if you want the system to hava an animation.  
     *  Draws the Nth intermediate animation.  1 <= aniamte <= animateNum().
     *      for (int animate = 1; animate <= animateNum(); animate++)
    **/
    public void drawAnimate(int sizeX, int sizeY, java.awt.Graphics gb, 
			    int animate) { 
	return ; 
    }


    /*
      If you want your math system to be clickable, extend these five 
      methods.  Then canvas.addMouseListener(system);
     */
    /** Invoked when the mouse has been clicked on a component. **/
    //public void mouseClicked(java.awt.event.MouseEvent e) { return; }
    /** Invoked when the mouse enters a component. **/
    //public void mouseEntered(java.awt.event.MouseEvent e) { return; }
    /** Invoked when the mouse exits a component. **/
    //public void mouseExited(java.awt.event.MouseEvent e) { return; }
    /** Invoked when a mouse button has been pressed on a component. **/
    //public void mousePressed(java.awt.event.MouseEvent e) { return; }
    /** Invoked when a mouse button has been released on a component. **/
    //public void mouseReleased(java.awt.event.MouseEvent e) { return; }

    /* used by drawSquareBug() */
    static final void drawSquare(java.awt.Graphics gb, 
				 int x, int y, int size, int shrink) {
	gb.drawRect(x +shrink, y+shrink, 
		    size - (2*shrink),  size - (2*shrink));
    }
    /** 
	Provides a standard "look" to all square Bugs.
	@param x top left corner x coord
	@param x top left corner y coord
	@param size size.
    **/
    public static final void drawSquareBug(java.awt.Graphics gb, 
					   int x, int y, int size) {
	if (size >= 32) {
	    gb.setColor(java.awt.Color.black);
	    drawSquare(gb,x,y,size,0);
	    drawSquare(gb,x,y,size,1);
	    drawSquare(gb,x,y,size,2);
	    drawSquare(gb,x,y,size,3);
	    gb.setColor(java.awt.Color.white);
	    drawSquare(gb,x,y,size,4);
	    drawSquare(gb,x,y,size,5);
	    drawSquare(gb,x,y,size,6);
	    drawSquare(gb,x,y,size,7);
	} else if (size >= 24) {
	    gb.setColor(java.awt.Color.black);
	    drawSquare(gb,x,y,size,0);
	    drawSquare(gb,x,y,size,1);
	    drawSquare(gb,x,y,size,2);
	    gb.setColor(java.awt.Color.white);
	    drawSquare(gb,x,y,size,3);
	    drawSquare(gb,x,y,size,4);
	    drawSquare(gb,x,y,size,5);
	    drawSquare(gb,x,y,size,6);
	    drawSquare(gb,x,y,size,7);
	} else if (size >= 16) {
	    gb.setColor(java.awt.Color.black);
	    drawSquare(gb,x,y,size,0);
	    drawSquare(gb,x,y,size,1);
	    gb.setColor(java.awt.Color.white);
	    drawSquare(gb,x,y,size,2);
	    drawSquare(gb,x,y,size,3);
	} else if (size >= 8) {
	    gb.setColor(java.awt.Color.black);
	    drawSquare(gb,x,y,size,0);
	    gb.setColor(java.awt.Color.white);
	    drawSquare(gb,x,y,size,1);
	} else if (size >= 4) {
	    gb.setColor(java.awt.Color.black);
	    drawSquare(gb,x,y,size,0);
	}
	return;
    }
}
