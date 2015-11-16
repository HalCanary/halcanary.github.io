../toad.jar: classes/Asm.class classes/Shuffle.class classes/Tiling.class 
	cd classes; jar -cvf ../../toad.jar *.class

classes/Asm.class: Asm.java
	javac -d classes/ Asm.java

classes/Shuffle.class: Shuffle.java
	javac -d classes/ Shuffle.java

classes/Tiling.class: Tiling.java
	javac -d classes/ Tiling.java

