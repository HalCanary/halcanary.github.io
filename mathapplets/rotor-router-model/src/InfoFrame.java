/*
    InfoFrame Class
    Part of JavaBits (Usefull Java Classes)
    Copyright (C) 2001-2004 Hal Canary, Univerity of Wisconsin-Madison 
    hal@ups.physics.wisc.edu

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

import java.awt.Graphics;
import java.awt.Color;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

/**
 * This class reperesents a frame that pops up with information!
 **/
public class InfoFrame extends Frame implements ActionListener,WindowListener {
    /** Constructor.  Sets default title to "InfoFrame"**/
    public InfoFrame() {
	super("InfoFrame");
	create();
    }
    /** Constructor.
     * @param tit Title of the window.
     **/
    public InfoFrame(String tit) {
	super(tit);
	create();
    }
    TextArea ta;
    Button okayButton;
    Panel p;
    void create() {
	this.addWindowListener(this);
	p = new Panel();
	ta = new TextArea(20,80);
	ta.setEditable(false);
	ta.setText("Hello");
	p.add(ta);
	okayButton = new Button("Okay");
	okayButton.addActionListener(this);
	p.add(okayButton);
	this.add(p);
	setVisible(false);
    }
    /**
     * Pops up the InfoFrame with a bit of text in it.
     * @param s String containing info.  Use '\n's for formatting.
     **/
    public void alert(String s) {
	ta.setText(s);
	pack();
	setVisible(true);
	this.show();
    }
    /**
     * Magically changes the text in the InfoFrame Window.
     * @param s String containing info.  Use '\n's for formatting.
     **/
    public void changeText(String s) {
	ta.setText(s);
    }
    /** Implements method from ActionListener interface. **/
    public void actionPerformed(ActionEvent e) {
	if (e.getActionCommand() == "Okay" ) { 
	    setVisible(false);
	}
    }
    /** Implements method from WindowListener interface. **/
    public void windowClosed(WindowEvent e) { setVisible(false); }
    /** Implements method from WindowListener interface. **/
    public void windowClosing(WindowEvent e) { setVisible(false); }
    /** Implements method from WindowListener interface. **/
    public void windowActivated(WindowEvent e) { }
    /** Implements method from WindowListener interface. **/
    public void windowDeactivated(WindowEvent e) { }
    /** Implements method from WindowListener interface. **/
    public void windowDeiconified(WindowEvent e) { }
    /** Implements method from WindowListener interface. **/
    public void windowIconified(WindowEvent e) { }
    /** Implements method from WindowListener interface. **/
    public void windowOpened(WindowEvent e) { }
}
