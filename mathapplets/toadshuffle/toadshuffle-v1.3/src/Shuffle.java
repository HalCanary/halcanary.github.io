/* 
    TOAD Shuffler Applet
    Copyright (C) 2001-2003  Hal Canary Univerity of Wisconsin-Madison 
      (hal@ups.physics.wisc.edu)

    A description of the algorithm can be found in the file index.html

    Licence Information:

	This program is free software; you can redistribute it and/or
	modify it under the terms of version 2 of the GNU General
	Public License as published by the Free Software Foundation.

	A copy of the liscence was distributed in the file LICENSE.txt

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
import java.applet.Applet;

public class Shuffle extends Applet implements ActionListener {
    static final int initialDominoSize = 10;
    static final int diamondSize = 532;
    static final String title = "TOAD Shuffler";

    boolean showDPFL = false;
    int iter;                   // the iterator
    Button button;              // a button
    int l;                      // size of domino
    Tiling diamond;            // The AD that we are playing with
    Asm asm1;
    Asm asm2;

    Image buffer;               // The graphics buffer
    Graphics gb;                // The graphics that hold onto the buffer.
    Image asmBuffer1;
    Graphics agb1; 
    Image asmBuffer2;
    Graphics agb2; 
    
    Graphics appletGraphics;

    TextField howmany;
    TextField stage;
    Button buttonDPFLon;
    Button buttonDPFLoff;
    Panel bPanel;

    public void init() { 
	System.gc();
	setBackground(Color.white);
	//setCursor(Frame.CROSSHAIR_CURSOR);
	setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0)); 

        // Set up our Buttons.
	bPanel = new Panel();
	bPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0)); 

        button = new Button( "Iterate Step");
	button.addActionListener(this);
        bPanel.add(button);
        button = new Button( "Iterate Stage");
	button.addActionListener(this);
        bPanel.add(button);

	bPanel.add(new Label("Current Size:"));
        stage = new TextField("0", 3);
	stage.setEditable(false);
        bPanel.add(stage);

        button = new Button( "New AD");
	button.addActionListener(this);
        bPanel.add(button);

	bPanel.add(new Label(" Initial Size:"));
        howmany = new TextField("1", 3);
        bPanel.add(howmany);

	buttonDPFLon = new Button( "Show FPL");
	buttonDPFLon.addActionListener(this);
	buttonDPFLoff = new Button("Hide FPL");
	buttonDPFLoff.addActionListener(this);
	bPanel.add(buttonDPFLon);

	add(bPanel);

        //Set up the graphics buffer.  Double Buffering!
	setVisible(true);
	buffer = createImage(diamondSize, diamondSize); 
	gb = buffer.getGraphics();

	//Graphics for ASMs, if and when.
	asmBuffer1 = createImage(diamondSize/2, diamondSize/2 ); 
	agb1 = asmBuffer1.getGraphics();
	asmBuffer2 = createImage(diamondSize/2, diamondSize/2 ); 
	agb2 = asmBuffer2.getGraphics();

	showDPFL = false;
	l = initialDominoSize  ;
	appletGraphics = this.getGraphics();

        reInit() ;
	iterateShuffle();
	drawShuffle();
    }

    public void start() { }
    public void stop() { }

    public void destroy() { 
	System.gc();
    }
    
    public void paint(Graphics g) {
	g.drawImage(buffer, 0,70, null); 
	if (showDPFL == true) {
	    appletGraphics.drawImage(asmBuffer1, diamondSize, 68, null);
	    appletGraphics.
		drawImage(asmBuffer2, diamondSize, 70 + (diamondSize/2), null);
	}
    }
    //public boolean action(Event evt, Object arg) {   }

    public void reInit() { 
        diamond = new Tiling();
	System.gc();
        iter = 0;
	l = initialDominoSize;
    }

    public void iterateShuffle() {
        if (iter%3 == 0) {
            diamond.fillAll();
	    if (showDPFL == true) {
		asm1 = new Asm();
		asm2 = new Asm();
		diamond.toAsm(asm1, asm2);
	    }
        }
        else if (iter%3 == 1) {
            diamond.annihilateAll();
        }
        else if (iter%3 == 2){
            diamond.moveAll() ;
            if ((diamond.n)*2*l > diamondSize) {
                l = diamondSize / (diamond.n * 2) ;
            }
        }
        iter++;
    }

    void iterateShuffleStage() {
	iterateShuffle();
	while(iter%3 != 1) {
	    iterateShuffle();
	}
    }

    void newShuffle(int howBig) {
	reInit() ;
	iterateShuffle();
	if (howBig > 1)
	    for (int j = 0; j < (3 * howBig) - 3; j++)
		iterateShuffle();
    }

    public void drawShuffle() {
	gb.clearRect(0,0,diamondSize, diamondSize) ;
        diamond.drawAll(l, diamondSize, diamondSize, gb);
	appletGraphics.drawImage(buffer, 0,70, null); 
	if (iter%3 == 1 &&  showDPFL == true) {
	    agb1.clearRect(0,0, diamondSize/2, diamondSize/2) ;	
	    agb2.clearRect(0,0, diamondSize/2, diamondSize/2) ;	
	    agb1.setColor(Color.blue);
	    agb2.setColor(Color.blue);
	    agb1.drawRect(0,0, diamondSize/2-1, diamondSize/2-1) ;	
	    agb2.drawRect(0,0, diamondSize/2-1, diamondSize/2-1) ;	
	    agb1.setColor(Color.black);
	    agb2.setColor(Color.black);
	    
	    asm1.drawDPF(agb1, diamondSize/2);
	    asm2.drawDPF(agb2, diamondSize/2);
	    appletGraphics.drawImage(asmBuffer1, diamondSize, 68, null);
	    appletGraphics.
		drawImage(asmBuffer2, diamondSize, 70 + (diamondSize/2), null);
	}
	stage.setText(Integer.toString(iter/3+1));
    }

    public void changeDPFLstate() {
	showDPFL = !showDPFL;
	if (showDPFL == true) {
	    bPanel.remove(buttonDPFLon);
	    bPanel.add(buttonDPFLoff);
	    bPanel.validate();

	    //////////////////////////////////
	    asm1 = new Asm();
	    asm2 = new Asm();
	    diamond.toAsm(asm1, asm2);
	    drawShuffle();	    
	}
	else if (showDPFL  == false){
	    bPanel.remove(buttonDPFLoff);
	    bPanel.add(buttonDPFLon);
	    bPanel.validate() ;
	    this.repaint();
	}
    }

    public Shuffle() {
    }

    public void actionPerformed(ActionEvent e) {
	if (e.getActionCommand() == "Iterate Step" ) { 
	    iterateShuffle();
	    drawShuffle();
	}
	else if (e.getActionCommand() == "Iterate Stage" ) { 
	    iterateShuffleStage();
	    drawShuffle();
	}
	else if (e.getActionCommand() == "New AD" ) { 
	    int x = 0;
	    try{ x = Integer.parseInt(howmany.getText()); }
	    catch (Exception exc) { }
	    newShuffle(x);
	    drawShuffle();
	}
	else if (e.getActionCommand() == "Show FPL" ) { 
	    changeDPFLstate(); 
	}
	else if (e.getActionCommand() == "Hide FPL" ) { 
	    changeDPFLstate();
	}
    }
}
