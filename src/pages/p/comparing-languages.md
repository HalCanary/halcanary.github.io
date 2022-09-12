Comparing Compiled Languages
============================

    /* hello.c */
    int main() {
        printf("hello world\n");
        return 0;
    }

that was c. and here's cpp.

    
    /* hello.cpp */
    #include <iostream>
    using namespace std;
    int main() {
      cout << "hello world\n";
      return 0;
    }

And here's java:

    /* hello.java */
    public class hello {
        public static void main(String[] args) {
            System.out.println("hello world");
        }
    }

And compile, run, list, and time:

    
    [hal@ups(pts/3) ~/code/c]$ PS1OLD=$PS1;PS1="\$ "
    $ gcc -o hello.c.out hello.c
    $ g++ -o hello.cpp.out hello.cpp
    $ gcj --main=hello -o hello.java.out hello.java
    $ ./hello.c.out
    hello world
    $ ./hello.cpp.out
    hello world
    $ ./hello.java.out
    hello world
    $ ls *.out
    -rwxrwxr-x  1 hal hal 4631 Apr 12 14:19 hello.c.out
    -rwxrwxr-x  1 hal hal 6382 Apr 12 14:20 hello.cpp.out
    -rwxrwxr-x  1 hal hal 9093 Apr 12 14:19 hello.java.out
    $ time ./hello.c.out
    hello world
     
    real    0m0.014s
    user    0m0.000s
    sys     0m0.000s
    $ time ./hello.cpp.out
    hello world
     
    real    0m0.034s
    user    0m0.010s
    sys     0m0.000s
    $ time ./hello.java.out
    hello world
     
    real    0m0.153s
    user    0m0.090s
    sys     0m0.010s
    
    $ cat > hello.sh
    #!/bin/sh
    echo "hello world"
    $ chmod +x ./hello.sh
    $ time ./hello.sh
    hello world
     
    real    0m0.023s
    user    0m0.000s
    sys     0m0.010s
    
    $ PS1=$PS1OLD
    [hal@ups(pts/3) ~/code/c]$
    

* * *

file modification time: 2006-09-14 18:50:27

* * *
