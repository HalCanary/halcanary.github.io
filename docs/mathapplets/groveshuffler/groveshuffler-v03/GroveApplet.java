/*
    Grove Shuffler Applet
    Copyright (C) 2001-2003  
      Hal Canary, Univerity of Wisconsin-Madison (hal@ups.physics.wisc.edu)
      Kyle Petersen, Brandeis University (tkpeters@brandeis.edu)

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
import java.io.*;
import java.net.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.applet.Applet;

/**
 * Note:  I need to put in better comments.
 **/
public class GroveApplet extends Applet implements ActionListener {
    static final int canvasWidth = 813;
    static final int canvasHeight = 562;
    static final int panelHeight = 38;
    static final String title = "Grove Shuffler";

    Image buffer;               // The graphics buffer
    Graphics gb;                // The graphicvs that hold onto the buffer.
    //ImageCanvas canvas;
    Graphics agraph;             // applet graphics.

    Button button;              // a button
    Grove grove;            // The AD that we are playing with
    TextField howmany;
    Panel bPanel;
    Choice choice;
 

    public void init() { 
	System.gc();
	//this.setBackground(Color.white);
	this.setBackground(new Color(230,230,230));
	setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0)); 

        // Set up our Buttons.
	bPanel = new Panel();
	bPanel.setBackground(new Color(230,230,230));
	bPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0)); 

        button = new Button( "ITERATE");
	button.addActionListener(this);
        bPanel.add(button);
        button = new Button( "NEW");
	button.addActionListener(this);
        bPanel.add(button);

	bPanel.add(new Label(" Speed:"));

        choice = new Choice();
	choice.addItem("single step");
	choice.addItem("two steps");
	choice.select("single step");
        bPanel.add(choice);

	bPanel.add(new Label(" Initial Size:"));
        howmany = new TextField("2", 3);
        bPanel.add(howmany);

	add(bPanel);

        //Set up the graphics buffer.  Double Buffering!
	setVisible(true);
	buffer = createImage(canvasWidth, canvasHeight); 
	gb = buffer.getGraphics();
	//canvas = new ImageCanvas(buffer);
	//canvas.setSize( new Dimension(canvasWidth, canvasHeight));
	//canvas.setBackground(Color.white);
	//add(canvas); 
	agraph = this.getGraphics();

        grove = new Grove() ;
	drawCanvas();
    }

    public void start() { }
    public void stop() { }

    public void destroy() { 
	System.gc();
    }

    
    public void paint(Graphics g) { 
	g.drawImage(buffer, 0, panelHeight, null);
    }

    public void drawCanvas() {
	grove.draw(canvasWidth, canvasHeight,gb);
	//canvas.repaint();
	agraph.drawImage(buffer, 0, panelHeight, null);
	//canvas.paint(this.getGraphics());
	//this.paint();
    }

    public void actionPerformed(ActionEvent e) {
	//if (e.getActionCommand() == "Exit" ) { killMe(); }
	//else 
	if (e.getActionCommand() == "ITERATE" ) { 
	    if (choice.getSelectedItem() == "single step") {
		grove.subiterate(1);
	    } else {
		grove.iterate(1);
	    }
	    drawCanvas();
	}
	else if (e.getActionCommand() == "NEW" ) { 
	    grove.reset();
	    try{ 
		int x = Integer.parseInt(howmany.getText());
		if (x > 2) { 
		    grove.iterate(x-2); 
		} else {
		    howmany.setText("2"); 
		}
	    }
	    catch (Exception exc) {
		howmany.setText("2");
	    }
	    drawCanvas();
	}
    }
}

// class ImageCanvas extends Canvas {
//     Image canvasImage ;
//     public ImageCanvas(Image image) { super(); canvasImage = image; }
//     public void paint(Graphics g) { g.drawImage(canvasImage, 0, 0, null); }
// }
