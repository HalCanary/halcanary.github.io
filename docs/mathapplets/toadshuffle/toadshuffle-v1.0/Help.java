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
import java.util.Vector;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Help  extends Frame implements ActionListener {
    boolean inAnApplet = true;
    static int MAXSIZE = 11;
    String [] questions;
    String [] answers;
    TextArea textArea;

    public Help(BufferedReader in) {
	String title = "Help!";
	String s = null;
	//	resize(50,50);
	setTitle(title);
	setBackground(Color.white);
	setCursor(Frame.CROSSHAIR_CURSOR);
	setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0)); 

	addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) { killMe(); }
        });

	//begin parsing...

	try { s = in.readLine(); }
	catch (IOException e) { e.printStackTrace(); }
	setTitle(s);
	questions = new String[MAXSIZE];
	answers = new String [MAXSIZE];
	answers[0] = new String();
	int pos = 0;

	try { s = in.readLine(); }
	catch (IOException e) { e.printStackTrace(); }
	while (s != null && pos < MAXSIZE) {
	    if (s.length() != 0) {
		if (s.charAt(0) == '?'){
		    String x = new String(s.substring(1,s.length()));
		    questions[pos] = x;
		    pos++;
		    answers[pos] = new String();
		}
		else if (pos > 0)
		    answers[pos-1] = answers[pos-1] + "\n" + s ;
	    }
	    try { s = in.readLine(); }
	    catch (IOException e) { e.printStackTrace(); }
	}

	Button b;

	Panel bPanel;
	bPanel = new Panel();
	bPanel.setLayout(new GridLayout(MAXSIZE, 1, 0, 0)); 

	for (int i = 0 ; i < MAXSIZE ; i++) {
	    if (questions[i] != null){
		//System.out.println(questions[i]);
		//System.out.println(answers[i]);
		b = new Button(  questions[i] );
		b.addActionListener(this);
		bPanel.add(b);
	    }
	}
	b = new Button(  "Exit" );
	b.addActionListener(this);
	bPanel.add(b);
	add(bPanel);

	textArea = new TextArea(" ", 20, 80) ;
	textArea.setEditable(false) ;
	add(textArea) ;
	pack() ;

	try { in.close(); } catch (IOException e) { e.printStackTrace(); }
    }

    public static void main(String[] args) {
	BufferedReader in = null;
	String s = null;
        if (args.length != 2) {
            System.err.println("Usage:  java Help [uf] source");
            System.err.println("\tu = source is a URL.");
            System.err.println("\tf = source is a local file.");
            System.exit(1);
        }
	if (args[0] == "f") {
	    
	    try {
		in = new BufferedReader(new FileReader(args[1]));
	    }
	    catch (FileNotFoundException e){
		System.err.println("That file doesn't exist! ");
		System.exit(1);
	    }
	}
	else if (args[0] == "u"){
	    
	    URL u = null;
	    try {
		u = new URL(args[1]);
		in = new BufferedReader(new 
		    InputStreamReader(u.openStream()));
	    }
	    catch (Exception e) { e.printStackTrace() ;}
	}
	else {
	    System.err.println("The first argument should be a u or a f.");
            System.exit(1);
	}


	try { s = in.readLine(); }
	catch (IOException e) { e.printStackTrace(); }

	if (!s.equals("Help File")) {
	    System.err.println("Is That a Help File?");
            System.exit(1);
        }

	Help window = new Help(in);
	window.inAnApplet = false;
        window.move(0,0);
        window.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
	if (e.getActionCommand() == "Exit" ) { killMe(); }
	else {
	    for (int i = 0; i < MAXSIZE; i++) {
		if (e.getActionCommand().equals(questions[i]) ) { 
		    textArea.setText(answers[i]);
		}
	    }
	}
    }
    public void killMe(){
	if (inAnApplet) {
	    dispose();
	} else {
	    dispose();
	    System.exit(0);
	}
    }
}
