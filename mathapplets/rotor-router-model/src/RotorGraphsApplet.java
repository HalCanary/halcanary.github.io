/*
    Rotor Graphs Applet
    Copyright (C) 2001-2004 Hal Canary, Univerity of Wisconsin-Madison
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
import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.Choice;
import java.awt.Label;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.Canvas;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

/**
 * This is the granddaddy of great Applets.  The idea is that the 
 * actual heavy lifting will be performed by classes that subclass
 * the MathSystem class.  RotorGraphsApplet can switch between 
 * several MathStytems.
 **/
public class RotorGraphsApplet extends Applet implements ActionListener,
							 ItemListener,
							 Runnable {
    static String APPLET_INFO 
	= "Rotor Graphs Applet 1.0rc. \n"
	+ "Copyright 2001-2004 Hal Canary. \n"
	+ "License = GNU GPL 2.";
    //static int BG_COLOR [] = {230,230,230};	// background color for applet
    static int BG_COLOR [] = {255,255,255};	// background color for applet
    // Static strings
    static String whichSystemLabel = "Graph/Mode:";	// label text
    static String skipEachLabel = "Skip Each:";		// label text
    // descritions of the mathSystems:
    static String systemAdesc = "Walk on finite graph A";
    static String systemBdesc = "Walk on finite graph B";
    static String systemCdesc = "Walk on finite graph C";
    static String systemDdesc = "1-D Walk";
    static String systemEdesc = "1-D Aggregation";
    static String systemFdesc = "2-D Walk";
    static String systemGdesc = "2-D Aggregation";

    static int SLEEP = 200;	// How long to sleep between moves
    				// when unpaused.
    static int SHORT_SLEEP = 1;	// How log to sleep between animation steps.

    static String PAD		= "  ";		// padding space.
    static String RESET		= "Reset";	// reset the applet.
    static String STEP		= "Step";	// A button to subiterate
    static String STAGE		= "Stage";	// A button to iterate
    static String S_1_STEP	= "1 Step";	// drop down choice
    static String S_1_STAGE	= "1 Stage";	// drop down choice
    static String S_10_STAGES	= "10 Stages" ;	// drop down choice
    static String S_100_STAGES	= "100 Stages";	// drop down choice
    static String INFO		= "Info";	// info button
    static String RESUME 	= "Paused (Press to Resume)";	// pause label
    static String PAUSE		= "Running (Press to Pause)";	// pause label

    FiniteGraphA systemA;	// a MathSystem object
    FiniteGraphB systemB;	// a MathSystem object
    FiniteGraphC systemC;	// a MathSystem object
    OneDeeWalk systemD;		// a MathSystem object
    OneDeeAgg systemE;		// a MathSystem object
    TwoDeeWalk systemF;		// a MathSystem object
    TwoDeeAggregation systemG;	// a MathSystem object

    MathSystem system;		// Pointer to the particular MathSystem
				// we are playing with.
    ICanvas canvas;		// custom class that contains a canvas+image
    PCanvas canvas2;		// a canvas
    InfoFrame infoFrame;	// custom class for Information.

    Choice whichSystem;	// which system (drop down menu)
    Choice skipEachC;	// Choose how many steps to skip each time. (drop down)
    Button button;	// generic button.  I don't need to keep track
    			// of pointers to the individual buttons.
    Button pauseB;	// Because I want to change the label of this button,
    			// I need a pointer to it.

    Thread t;		// Thread will this runnable class to run.
    boolean threadDead;	// should the thread stop running?
    boolean pause;	// Are we paused right now?
    boolean doubleBuffer; // Are we going to double buffer?

    /*
      extends Applet
        public void init()
        public void destroy()
      implements ActionListener
        public void actionPerformed(ActionEvent e)
      implements ItemListener
        public void itemStateChanged(ItemEvent e)
      implements Runnable
        public void run()
      other
        void drawCanvas()	--> Update from image buffer.
        void pause()		--> Toggle pause state.
        void step(int i)	--> Perform steps.
        void stage(int i)	--> Perform stages.
        void changeSystem()	--> Reset Button.
    */

    /** Override Applet init() **/
    public void init() {
	String paramDoubleBuffer = this.getParameter("double_buffer");
	if (paramDoubleBuffer != null &&
	    paramDoubleBuffer.equalsIgnoreCase("on")) {
	    doubleBuffer = true;
	} else { doubleBuffer = false; }

	this.setFont(new Font(this.getFont().getName(),
			      this.getFont().getStyle(), 12));
	this.removeAll();	// Just incase init() gets called twice.
	this.setBackground(new Color(BG_COLOR[0],BG_COLOR[1],BG_COLOR[2]));
	this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Set layout.

	// Start adding components (wigits) to the applet.

	// label for choice.
	this.add(new Label(whichSystemLabel)); // Graph/Mode
	// choice.  which MathSystem?
	whichSystem = new Choice();
	whichSystem.addItemListener(this);
	whichSystem.addItem(systemDdesc);
	whichSystem.addItem(systemEdesc);
	whichSystem.addItem(systemAdesc);
	whichSystem.addItem(systemBdesc);
	whichSystem.addItem(systemCdesc);
	whichSystem.addItem(systemFdesc);
	whichSystem.addItem(systemGdesc);
	whichSystem.select(systemEdesc);
	this.add(whichSystem);

	button = new Button( RESET);	// A button to reset the applet.
	button.addActionListener(this);
	this.add(button);

	this.add(new Label(PAD));	// padding space.

	button = new Button( STEP);	// A button to subiterate
	button.addActionListener(this);
	this.add(button);

	button = new Button( STAGE);	// A button to iterate
	button.addActionListener(this);
	this.add(button);

	this.add(new Label(PAD));	// padding space.

	this.add(new Label(skipEachLabel));	// Label for choice
	skipEachC = new Choice();	// A choice.
	skipEachC.addItem(S_1_STEP    );
	skipEachC.addItem(S_1_STAGE   );
	skipEachC.addItem(S_10_STAGES );
	skipEachC.addItem(S_100_STAGES);
	this.add(skipEachC);

	pauseB = new Button(RESUME);
	pauseB.addActionListener(this);
	this.add(pauseB);

	this.add(new Label(PAD));	// padding space.

	button = new Button(INFO);
	button.addActionListener(this);
	this.add(button);

	button = null;	//we are done with the button pointer.

	this.validate();
	this.setVisible(true);

	/*
	   At this point I need to figure out how big to make the
	   canvas.  I can make the width equal to the width of this
	   applet; To calculate the height, I must make it fit
	   underneath all of the other wigits. Thereofore I need to
	   find out which wigit is the farthest down.
	 */
	Rectangle b;
	int heightUsed = 0;
	for (int i =0;i<this.getComponents().length;i++) {
	    b = this.getComponents()[i].getBounds();
	    if (b.height + b.y > heightUsed)
		heightUsed = b.height + b.y;
	}
	int canvasWidth;	// total width of the canvas.
	int canvasHeight;	// total height.
	canvasWidth = this.getSize().width;
	canvasHeight = this.getSize().height - heightUsed;

	if (doubleBuffer) {
	    canvas = new ICanvas(canvasWidth,canvasHeight,this);
	    this.add(canvas);
	} else {
	    canvas2 = new PCanvas(canvasWidth,canvasHeight, system);
	    this.add(canvas2);
	}
	this.validate(); 

	systemA = new FiniteGraphA() ;
	systemB = new FiniteGraphB() ;
	systemC = new FiniteGraphC() ;
	systemD = new OneDeeWalk() ;
	systemE = new OneDeeAgg() ;
	systemF = new TwoDeeWalk() ;
	systemG = new TwoDeeAggregation() ;
	/*
	   At this point, you can set the default system.  Right
	   now it is OneDeeAgg.
	*/
	system = systemE;

	drawCanvas();
	this.repaint();

	infoFrame = new InfoFrame(INFO);

	pause = true; // Start out paused.
	threadDead = false; // when threadDead==true, stop the thread.
	Thread t = new Thread(this); // since this implements Runnable...
	t.start();
    }

    //     /* Override Applet start() */
    //     public void start() { }
    //     /* Override Applet stop() */
    //     public void stop() { }

    /** Override Applet destroy() **/
    public void destroy() {
	threadDead = true; //stops the thread.
	System.gc();
    }

    public String getAppletInfo() { return APPLET_INFO; }

    //     /* Overridden to eliminate flicker */
    //     public void paint(Graphics g) {  }
    /* Implement actionPerformed(e) for ActionListener, */
    public void actionPerformed(ActionEvent e) {
	if (e.getActionCommand() == STEP ) {
	    step(1);
	} else if (e.getActionCommand() == STAGE ) {
	    stage(1);
	} else if (e.getActionCommand() == RESET ) {
	    changeSystem();
	} else if (e.getActionCommand() == RESUME ) {
	    pause();
	} else if (e.getActionCommand() == PAUSE ) {
	    pause();
	} else if (e.getActionCommand() == INFO ){
	    infoFrame.alert(system.getInfo());
	} else {
	    System.out.print("ACTION: ");
	    System.out.println(e.getActionCommand());
	}
    }
    /* Implement itemStateChanged(e) for ItemListener, */
    public void itemStateChanged(ItemEvent e) {
	 if (e.getItemSelectable() == whichSystem) {
	     changeSystem();
	 }
    }
    /**
     * Implement run() for Runnable.
     * When you make a thread, this can be run.
     * Will run until threadDead.
    **/
    public void run() {
 	while (!threadDead) {
	    if (!pause){
		if (skipEachC.getSelectedItem() == S_1_STEP)
		    step(1);
		else if (skipEachC.getSelectedItem() == S_1_STAGE)
		    stage(1);
		else if (skipEachC.getSelectedItem() == S_10_STAGES)
		    stage(10);
		else if (skipEachC.getSelectedItem() == S_100_STAGES)
		    stage(100);
	    }
	    try {
		t.sleep(SLEEP); //miliseconds;
	    } catch (InterruptedException e) { }
	}
    }

    /** Update from image buffer. **/
    void drawCanvas() {
	if (doubleBuffer) {
	    system.draw(canvas.xSize,canvas.ySize,canvas.g);
	    canvas.draw();
	} else {
	    canvas2.system = this.system;
	    canvas2.repaint();
	}

    }
    void drawAnimateCanvas(int a) {
	if (doubleBuffer) {
	    system.drawAnimate(canvas.xSize,canvas.ySize,canvas.g,a);
	    canvas.draw();
	} else {
	    canvas2.system = this.system;
	    canvas2.repaint();
	}
    }

    /** Toggle pause state. **/
    void pause() {
	pause = !pause;
	if (pause)
	    pauseB.setLabel(RESUME);
	else
	    pauseB.setLabel(PAUSE);
	return;
    }
    /** Perform steps. **/
    void step(int i) {
	system.subiterate(i);
	int animations = system.animateNum();
	if (animations > 0) {
	    for (int animate = 1; animate <= animations; animate++) {
		drawAnimateCanvas(animate);
		//system.drawAnimate(canvas.xSize,canvas.ySize,
				   //canvas.g,animate);
		//canvas.draw();
		try {
		    Thread.sleep(SHORT_SLEEP); //miliseconds;
		} catch (InterruptedException e) { }
	    }
	}
	infoFrame.changeText(system.getInfo());
	drawCanvas();
    }
    /** Perform stages. **/
    void stage(int i) {
	system.iterate(i);
	infoFrame.changeText(system.getInfo());
	drawCanvas();
	
    }

    /** called from the reset button, or from the dropdownstate change.**/
    void changeSystem() {
	if (whichSystem.getSelectedItem() == systemAdesc)
	    system = systemA;
	else if (whichSystem.getSelectedItem() == systemBdesc)
	    system = systemB;
	else if (whichSystem.getSelectedItem() == systemCdesc)
	    system = systemC;
	else if (whichSystem.getSelectedItem() == systemDdesc)
	    system = systemD;
	else if (whichSystem.getSelectedItem() == systemEdesc)
	    system = systemE;
	else if (whichSystem.getSelectedItem() == systemFdesc)
	    system = systemF;
	else if (whichSystem.getSelectedItem() == systemGdesc)
	    system = systemG;
	system.reset();
	if (!pause) { pause(); }
	drawCanvas();
	// The next three lines are an attempt to fix a display bug.
	try { Thread.sleep(100); } //100 miliseconds.
	catch (InterruptedException e) { }
	drawCanvas();
	infoFrame.changeText(system.getInfo());
    }
}

/**
 * Icanvas implements double buffering.
**/
class ICanvas extends Canvas {
    /** Constructor.  call with "new ICanvas(?,?,this);" **/
    public ICanvas(int x, int y,Applet owner){
	super();
	xSize = x;
	ySize = y;
	i = owner.createImage(x,y);
	if (i == null) {
	    System.out.println("image is null!");
	} else
	    g = i.getGraphics();
	this.setSize(xSize,ySize);
    }
    public int xSize,ySize;
    public Image i; //image
    public Graphics g; //graphics of image
    public Graphics graph; //graphics of this.
    public Dimension getPreferredSize() {
	return new Dimension(xSize,ySize);
    }
    public void draw() {
	if (i == null) {
	    System.err.println("ERROR: image is null.");
	    return;
	}
	if (graph == null) {
	    graph = this.getGraphics();
	}
	graph.drawImage(i,0,0,null);
    }
    public void paint(Graphics gr) {
	gr.drawImage(i,0,0,null);
    }
}

class PCanvas extends Canvas {
    public MathSystem system;
    public int xSize;
    public int ySize;
    public PCanvas(int x, int y, MathSystem sys){
	super();
	xSize = x;
	ySize = y;
	this.setSize(xSize,ySize);
    }
    public Dimension getPreferredSize() {
	return new Dimension(xSize,ySize);
    }
    public void paint(Graphics gr) {
	if (this.system != null)
	    this.system.draw(this.xSize,this.ySize,gr);
    }
}
