/*
    Java Bits - Useful Java Classes for discrete systems.
    Copyright (C) 2003  Hal Canary, University of Wisconsin-Madison
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
 * MathSystem Class:
 * this class represents a generic mathematical system.
 **/
public abstract class MathSystem {
    /** Current Iteration Number  **/
    public int n; 
    /**
     * The Constructor
     **/ 
    public MathSystem() {}
    /**
     * draw() 
     *
     * Draws itself on the graphics buffer.
     *
     * @param sizeX is the x size of region on which to draw.
     * @param sizeY is the y size of region on which to draw.
     **/ 
    public abstract void draw(int sizeX, int sizeY, java.awt.Graphics gb);
    /** Iterates the system. **/
    public abstract void iterate(int iterations);
    /** Does a subiteration, if applicable. **/
    public abstract void subiterate(int subiterations);
    /** Restarts the system **/
    public abstract void reset();
}
