A stupid program...
===================

Luckilly, this program turned out not to be the pinacle of my c++ career. That came later, with [`hexagon`](/p/hexagon/).

shisen.c

* * *

    
    /*  Shisen Sho Top Score Displayer
     *    -- this IS the pinnacle of our careers
     *
     *  reads in 'filenumber' of files and retrieves 'top' scores 
     *  for each geometry
     *
     *  (c) 2000-2003 Hal Canary & Mark Chapman
     *
     *  Licence Information:
     *
     *  	This program is free software; you can redistribute it and/or
     *  	modify it under the terms of version 2 of the GNU General
     *  	Public License as published by the Free Software Foundation.
     *
     *  	A copy of the licence can be found at:
     *  		http://www.gnu.org/licenses/gpl.txt
     *
     *  	This program is distributed in the hope that it will be
     *  	useful, but WITHOUT ANY WARRANTY; without even the implied
     *  	warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
     *    	PURPOSE.  See the GNU General Public License for more details.
     *
     *  Compile with:
     *	g++ -o shisenshow shisen.c
     */
    
    #include <iostream.h>
    #include <fstream.h>
    #include <string>
    
    string NameSpiller(string *str) {
      for(int i = 0; i < str->size(); i++)
        if(str->substr(i,1) == " " && str->substr(i+1,1) > "9")
          return str->substr(i,str->size()-i);
      return *str;
    }
    
    int main(int argc, char *argv) {
      const int filenumber = 7,top = 70;
      string filename[filenumber];
      filename[0] = "/home/jillian/.kde/share/config/kshisenrc";
      filename[1] = "/home/chapman/.kde/share/config/kshisenrc";
      filename[2] = "/home/beth/.kde/share/config/kshisenrc";
      filename[3] = "/home/doksus/.kde/share/config/kshisenrc";
      filename[4] = "/home/hal/.kde/share/config/kshisenrc";
      filename[5] = "/home/lweltzer/.kde/share/config/kshisenrc";
      filename[6] = "/home/lmfinco/.kde/share/config/kshisenrc";
    
      int flag;
      int highnum14 = 0,highnum18 = 0,highnum24 = 0,highnum26 = 0,
          highnum30 = 0;
      int time,time14[top],time18[top],time24[top],time26[top],
          time30[top];
      string highscorer14[top],highscorer18[top],highscorer24[top],
          highscorer26[top],highscorer30[top];
      string buffer,buffer2;
      ifstream fin;
    
      for(int i=0; i<filenumber; i++) { 
        fin.open(filename[i].c_str());
        do {
          getline(fin, buffer, '\n');
          if (buffer.substr(0,3) == "Hig") {
    	buffer = buffer.substr(12,buffer.size()-12);
    	buffer2 = buffer.substr(0,2);
    	if (buffer2 == "14") {
    	  flag = 0;
    	  time = 0;
    	  for(int j = 5; buffer.at(j) != ' '; j++)
    	    time = (time * 10) + atoi(buffer.substr(j,1).c_str());
    	  for(int n = 0; n <= highnum14 && flag == 0; n++) {
    	    if(n == highnum14 && highnum14 < top - 1) {
    	      time14[highnum14] = time;
    	      highscorer14[highnum14] = buffer;
    	      highnum14++;
    	      flag = 1;
    	    } else if(time < time14[n]) {
    	      for(int k = highnum14; k > n; k--) {
    		if(highnum14 == top - 1)
    		  highnum14--;
    		time14[k] = time14[k-1];
    		highscorer14[k] = highscorer14[k-1];
    	      }
     	      time14[n] = time;
    	      highscorer14[n] = buffer;
    	      highnum14++;
    	      flag = 1;
    	    }
    	  }
    	}
    	if (buffer2 == "18") {
    	  flag = 0;
    	  time = 0;
    	  for(int j = 5; buffer.at(j) != ' '; j++)
    	    time = (time * 10) + atoi(buffer.substr(j,1).c_str());
    	  for(int n = 0; n <= highnum18 && flag == 0; n++) {
    	    if(n == highnum18 && highnum18 < top - 1) {
    	      time18[highnum18] = time;
    	      highscorer18[highnum18] = buffer;
    	      highnum18++;
    	      flag = 1;
    	    } else if(time < time18[n]) {
    	      for(int k = highnum18; k > n; k--) {
    		if(highnum18 == top - 1)
    		  highnum18--;
    		time18[k] = time18[k-1];
    		highscorer18[k] = highscorer18[k-1];
    	      }
     	      time18[n] = time;
    	      highscorer18[n] = buffer;
    	      highnum18++;
    	      flag = 1;
    	    }
    	  }
    	}
    	if (buffer2 == "24") {
    	  flag = 0;
    	  time = 0;
    	  for(int j = 6; buffer.at(j) != ' '; j++)
    	    time = (time * 10) + atoi(buffer.substr(j,1).c_str());
    	  for(int n = 0; n <= highnum24 && flag == 0; n++) {
    	    if(n == highnum24 && highnum24 < top - 1) {
    	      time24[highnum24] = time;
    	      highscorer24[highnum24] = buffer;
    	      highnum24++;
    	      flag = 1;
    	    } else if(time < time24[n]) {
    	      for(int k = highnum24; k > n; k--) {
    		if(highnum24 == top - 1)
    		  highnum24--;
    		time24[k] = time24[k-1];
    		highscorer24[k] = highscorer24[k-1];
    	      }
     	      time24[n] = time;
    	      highscorer24[n] = buffer;
    	      highnum24++;
    	      flag = 1;
    	    }
    	  }
    	}
    	if (buffer2 == "26") {
    	  flag = 0;
    	  time = 0;
    	  for(int j = 6; buffer.at(j) != ' '; j++)
    	    time = (time * 10) + atoi(buffer.substr(j,1).c_str());
    	  for(int n = 0; n <= highnum26 && flag == 0; n++) {
    	    if(n == highnum26 && highnum26 < top - 1) {
    	      time26[highnum26] = time;
    	      highscorer26[highnum26] = buffer;
    	      highnum26++;
    	      flag = 1;
    	    } else if(time < time26[n]) {
    	      for(int k = highnum26; k > n; k--) {
    		if(highnum26 == top - 1)
    		  highnum26--;
    		time26[k] = time26[k-1];
    		highscorer26[k] = highscorer26[k-1];
    	      }
     	      time26[n] = time;
    	      highscorer26[n] = buffer;
    	      highnum26++;
    	      flag = 1;
    	    }
    	  }
    	}
    	if (buffer2 == "30") {
    	  flag = 0;
    	  time = 0;
    	  for(int j = 6; buffer.at(j) != ' '; j++)
    	    time = (time * 10) + atoi(buffer.substr(j,1).c_str());
    	  for(int n = 0; n <= highnum30 && flag == 0; n++) {
    	    if(n == highnum30 && highnum30 < top - 1) {
    	      time30[highnum30] = time;
    	      highscorer30[highnum30] = buffer;
    	      highnum30++;
    	      flag = 1;
    	    } else if(time < time30[n]) {
    	      for(int k = highnum30; k > n; k--) {
    		if(highnum30 == top - 1)
    		  highnum30--;
    		time30[k] = time30[k-1];
    		highscorer30[k] = highscorer30[k-1];
    	      }
     	      time30[n] = time;
    	      highscorer30[n] = buffer;
    	      highnum30++;
    	      flag = 1;
    	    }
    	  }
    	}
          }
        } while (buffer != "");
        fin.close();
      }
    
      cout << "\nShisen Sho Top Scores\n  presented by:\n" 
           << "    Hal Canary\n    Mark Chapman\n"
           << "    and users like you!\n";
    
      if(highnum14 > 0)
        cout << "\n14x6 Geometry\n-------------\n";
      for(int i = 0; i<highnum14; i++) {
        cout << '(' << i+1 << ")\t " << time14[i] << " seconds\t" 
    	 << NameSpiller(&highscorer14[i]) << '\n' ;
      }
    
      if(highnum18 > 0)
        cout << "\n18x8 Geometry\n-------------\n";
      for(int i = 0; i<highnum18; i++) {
        cout << '(' << i+1 << ")\t " << time18[i] << " seconds\t" 
    	 << NameSpiller(&highscorer18[i]) << '\n' ;
      }
    
      if(highnum24 > 0)
        cout << "\n24x12 Geometry\n--------------\n";
      for(int i = 0; i<highnum24; i++) {
        cout << '(' << i+1 << ")\t " << time24[i] << " seconds\t" 
    	 << NameSpiller(&highscorer24[i]) << '\n' ;
      }
    
      if(highnum26 > 0)
        cout << "\n26x14 Geometry\n--------------\n";
      for(int i = 0; i<highnum26; i++) {
        cout << '(' << i+1 << ")\t " << time26[i] << " seconds\t" 
    	 << NameSpiller(&highscorer26[i]) << '\n' ;
      }
    
      if(highnum30 > 0)
        cout << "\n30x16 Geometry\n--------------\n";
      for(int i = 0; i<highnum30; i++) {
        cout << '(' << i+1 << ")\t " << time30[i] << " seconds\t" 
    	 << NameSpiller(&highscorer30[i]) << '\n' ;
      }
    
      cout << "\n\n";
    
      return 0;
    }
    

* * *

* * *

file modification time: 2003-12-07 23:45:13

* * *
