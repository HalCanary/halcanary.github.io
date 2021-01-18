/* 
    TOAD Shuffler
    Copyright (C) 2001  Hal Canary

    This program is free software; you can redistribute it and/or
    modify it under the terms of version 2 of the GNU General Public
    License as published by the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
import java.io.*;
import java.net.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

public class Shuffle extends Frame implements ActionListener {
    static final int initialDominoSize = 10;
    static final int diamondSize = 532;
    static final String title = "TOAD Shuffler";

    boolean showDPFL = false;
    boolean inAnApplet = true;
    int iter;                   // the iterator
    Image buffer;               // The graphics buffer
    Graphics gb;                // The graphicvs that hold onto the buffer.
    Button button;              // a button
    int l;                      // size of domino
    Tiling diamond;            // The AD that we are playing with
    ImageCanvas canvas;
    ImageCanvas asmCanvas;
    Image asmBuffer1;
    Graphics agb1; 
    Image asmBuffer2;
    Graphics agb2; 
    Image asmBuffer3;
    Graphics agb3; 
    Choice choice;
    TextField howmany;
    Button buttonDPFLon;
    Button buttonDPFLoff;
    Panel bPanel;
    //Panel dpflPanel;
    Asm asm1;
    Asm asm2;

    public void reInit() { 
        diamond = new Tiling();
	System.gc();
        iter = 0;
	l = initialDominoSize;
    }

    public void iterateShuffle() {
        if (iter%3 == 0) {
            diamond.fillAll();
	    asm1 = new Asm();
	    asm2 = new Asm();
	    diamond.toAsm(asm1, asm2);
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
    public void drawShuffle() {
	gb.clearRect(0,0,diamondSize, diamondSize) ;
        diamond.drawAll(l, diamondSize, diamondSize, gb);
	canvas.repaint();
	if (iter%3 == 1 &&  showDPFL == true) {
	    agb1.clearRect(0,0, diamondSize/2, diamondSize/2) ;	
	    agb2.clearRect(0,0, diamondSize/2, diamondSize/2) ;	
	    asm1.drawDPF(agb1, diamondSize/2);
	    asm2.drawDPF(agb2, diamondSize/2);
	    agb3.clearRect(0,0, diamondSize/2, diamondSize) ;
	    agb3.drawImage(asmBuffer1, 0, 0, null);
	    agb3.drawImage(asmBuffer2, 0, diamondSize/2, null);
	    agb3.setColor(Color.blue);
	    agb3.drawRect(0,0,diamondSize/2-1, diamondSize-1) ;
	    agb3.drawLine(0,diamondSize/2,diamondSize/2,diamondSize/2) ;
	    asmCanvas.repaint();

	    StringBuffer s = new StringBuffer();  
	    System.out.println();
	    asm1.printInter(asm2, s);
	    System.out.print(s);
	}
    }

    public void changeDPFLstate() {
	showDPFL = !showDPFL;
	if (showDPFL == true) {
	    resize(diamondSize*3/2+15,diamondSize+70);
	    bPanel.remove(buttonDPFLon);
	    bPanel.add(buttonDPFLoff);
	    bPanel.validate();

	    asmBuffer1 = createImage(diamondSize/2, diamondSize/2 ); 
	    agb1 = asmBuffer1.getGraphics();
	    asmBuffer2 = createImage(diamondSize/2, diamondSize/2 ); 
	    agb2 = asmBuffer2.getGraphics();

	    asmBuffer3 = createImage(diamondSize/2, diamondSize); 
	    agb3 = asmBuffer3.getGraphics();
	    asmCanvas = new ImageCanvas(asmBuffer3);
	    asmCanvas.resize(diamondSize/2, diamondSize);
	    add(asmCanvas ); 

	    agb1.clearRect(0,0, diamondSize/2, diamondSize/2) ;	
	    agb2.clearRect(0,0, diamondSize/2, diamondSize/2) ;	
	    asm1.drawDPF(agb1, diamondSize/2);
	    asm2.drawDPF(agb2, diamondSize/2);
	    agb3.clearRect(0,0, diamondSize/2, diamondSize) ;	
	    agb3.drawImage(asmBuffer1, 0, 0, null);
	    agb3.drawImage(asmBuffer2, 0, diamondSize/2, null);
	    agb3.setColor(Color.blue);
	    agb3.drawRect(0,0,diamondSize/2-1, diamondSize-1) ;
	    agb3.drawLine(0,diamondSize/2,diamondSize/2,diamondSize/2) ;
	    asmCanvas.repaint();

	    StringBuffer s = new StringBuffer();  
	    System.out.println("ASMs:");
	    asm1.printAsm(s);
	    s.append("-----\n");
	    asm2.printAsm(s);
	    s.append("-----\n");
	    System.out.print(s);

	    validate();
	}
	else if (showDPFL  == false){
	    resize(diamondSize+80,diamondSize+70);
	    bPanel.remove(buttonDPFLoff);
	    bPanel.add(buttonDPFLon);
	    bPanel.validate() ;
	    remove(asmCanvas);

	    asmCanvas = null;

	    asmBuffer1 = null;
	    agb1 = null;
	    asmBuffer2 = null;
	    agb2 = null;
	    asmBuffer3 = null;
	    agb3 = null;
	    System.gc();
	    validate();
	}
    }

    public Shuffle() {
	super();
	//Set up the Frame.
	resize(diamondSize+80,diamondSize+70);
	move(0,0);
	setBackground(Color.white);
	setCursor(Frame.CROSSHAIR_CURSOR);
	setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0)); 

        // Set up our Buttons.
	bPanel = new Panel();
	bPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0)); 

        button = new Button( "ITERATE AD");
	button.addActionListener(this);
        bPanel.add(button);
        button = new Button( "NEW AD");
	button.addActionListener(this);
        bPanel.add(button);
        button = new Button( "Help");
	button.addActionListener(this);
        bPanel.add(button);
        button = new Button( "Exit");
	button.addActionListener(this);
        bPanel.add(button);

	bPanel.add(new Label(" Speed:"));

        choice = new Choice();
	choice.addItem("single step");
	choice.addItem("three steps");
	choice.select("single step");
        bPanel.add(choice);

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
	show();
	buffer = createImage(diamondSize, diamondSize ); 
	gb = buffer.getGraphics();
	canvas = new ImageCanvas(buffer);
	canvas.resize(diamondSize, diamondSize);
	canvas.setBackground(Color.white);
	add(canvas); 
	canvas.resize(diamondSize, diamondSize);

	//Listen for close command.
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { killMe(); }
        });

        // Set up our diamond, do the first iteration, and paint it.
	showDPFL = false;
	l = initialDominoSize  ;

        reInit() ;
	iterateShuffle();
	drawShuffle();
    }

    public void actionPerformed(ActionEvent e) {
	if (e.getActionCommand() == "Exit" ) { killMe(); }
	else if (e.getActionCommand() == "ITERATE AD" ) { 
	    if (choice.getSelectedItem() == "single step")
		iterateShuffle();
	    else 
		for (int j=0; j<3; j++)
		    iterateShuffle();
	    drawShuffle();
	}
	else if (e.getActionCommand() == "NEW AD" ) { 
            reInit() ;
	    iterateShuffle();
	    try{ 
		int x = 0;
		x = Integer.parseInt(howmany.getText());
		if (x > 1)
		    for (int j = 0; j < 3*x -3; j++)
			iterateShuffle();
	    }
	    catch (Exception exc) { }
	    drawShuffle();
	}
	else if (e.getActionCommand() == "Show FPL" ) { 
	    changeDPFLstate(); 
	}
	else if (e.getActionCommand() == "Hide FPL" ) { 
	    changeDPFLstate();
	}
	else if (e.getActionCommand() == "Help" ) { 
	    BufferedReader in = null;
	    String s = null;
	    s = "http://ups.physics.wisc.edu/~hal/SSL/shuffle/help.help";
	    URL u = null;
	    try {
		u = new URL(s);
		in = new BufferedReader(new 
		    InputStreamReader(u.openStream()));
	    }
	    catch (Exception ex) { ex.printStackTrace() ;}

	    try { s = in.readLine(); }
	    catch (IOException ex) { ex.printStackTrace(); }
	    
	    Help help = new Help(in);
	    help.inAnApplet = true;
	    help.setVisible(true);
	}
    }

    public static void main(String[] args) {
        Shuffle window = new Shuffle();
        window.inAnApplet = false;
        window.move(0,0);
        window.setTitle(title);
        window.setVisible(true);
    }

    public void killMe(){
	if (inAnApplet) {
	    dispose();
	} else {
	    System.exit(0);
	}
    }
}

class ImageCanvas extends Canvas {
    Image canvasImage ;
    public ImageCanvas(Image image) { super(); canvasImage = image; }
    public void paint(Graphics g) { g.drawImage(canvasImage, 0, 0, null); }
}
