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
import java.applet.Applet;
import java.awt.*;

public class ShuffleApplet extends Applet {
    Shuffle window;
    public void init() { }

    public void start() {
	System.gc();
        window = new Shuffle();
        window.inAnApplet = true;
        window.move(0,0);
        window.setTitle(window.title);
        window.setVisible(true);
    }

    public void stop() {
	window.dispose();
	window = null;
	System.gc();
    }
    
    public void paint(Graphics g) { }
    public boolean action(Event evt, Object arg) {
	return false;
    }
}
