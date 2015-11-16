#!/bin/sh

echo "javac *.java"
javac *.java

echo "jar cf toad.jar *.class"
jar cf toad.jar *.class

echo "DONE."
