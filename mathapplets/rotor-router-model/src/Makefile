JAVAC = javac
#JAVAC = gcj -C

.PHONY : all
.PHONY : clean
.PHONY : jar
.PHONY : doc
.PHONY : html

all:
	@echo -en "\nPossible targets are \"jar\" \"doc\" "
	@echo -en "\"html\" and \"clean\".\n"

html: ../index.html ../index-1280x960.html ../index-800x600.html ../ndb.html ../ndb-1280x960.html ../ndb-800x600.html

../index.html: ../templates/head.html ../templates/applet_medium.html ../templates/tail.html
	cd .. ; cat templates/head.html templates/applet_medium.html templates/tail.html > index.html

../index-1280x960.html: ../templates/head.html ../templates/applet_large.html ../templates/tail.html
	cd .. ; cat templates/head.html templates/applet_large.html templates/tail.html > index-1280x960.html

../index-800x600.html: ../templates/head.html ../templates/applet_small.html ../templates/tail.html
	cd .. ; cat templates/head.html templates/applet_small.html templates/tail.html > index-800x600.html

../ndb.html: ../templates/head.html ../templates/applet_medium.html ../templates/tail.html
	cd .. ; cat templates/head.html templates/applet_ndb_medium.html templates/tail.html > ndb.html

../ndb-1280x960.html: ../templates/head.html ../templates/applet_large.html ../templates/tail.html
	cd .. ; cat templates/head.html templates/applet_ndb_large.html templates/tail.html > ndb-1280x960.html

../ndb-800x600.html: ../templates/head.html ../templates/applet_small.html ../templates/tail.html
	cd .. ; cat templates/head.html templates/applet_ndb_small.html templates/tail.html > ndb-800x600.html

jar: ../rotor.jar

../rotor.jar: classes/FiniteGraphA.class classes/FiniteGraphB.class classes/FiniteGraphC.class classes/InfoFrame.class classes/MathSystem.class classes/OneDeeAgg.class classes/OneDeeWalk.class classes/RotorGraphsApplet.class classes/TwoDeeWalk.class classes/TwoDeeAggregation.class
	cd classes; jar -cvf ../../rotor.jar *.class

classes/FiniteGraphA.class: FiniteGraphA.java
	$(JAVAC) -d classes/ FiniteGraphA.java

classes/RotorGraphsApplet.class: RotorGraphsApplet.java
	$(JAVAC) -d classes/ RotorGraphsApplet.java

classes/FiniteGraphB.class: FiniteGraphB.java
	$(JAVAC) -d classes/ FiniteGraphB.java

classes/FiniteGraphC.class: FiniteGraphC.java
	$(JAVAC) -d classes/ FiniteGraphC.java

classes/MathSystem.class: MathSystem.java
	$(JAVAC) -d classes/ MathSystem.java

classes/InfoFrame.class: InfoFrame.java
	$(JAVAC) -d classes/ InfoFrame.java

classes/OneDeeAgg.class: OneDeeAgg.java
	$(JAVAC) -d classes/ OneDeeAgg.java

classes/OneDeeWalk.class: OneDeeWalk.java
	$(JAVAC) -d classes/ OneDeeWalk.java

classes/TwoDeeWalk.class: TwoDeeWalk.java
	$(JAVAC) -d classes/ TwoDeeWalk.java

classes/TwoDeeAggregation.class: TwoDeeAggregation.java
	$(JAVAC) -d classes/ TwoDeeAggregation.java

clean:
	rm *~
	rm ../*~
	rm classes/*.class

doc: ../doc/index.html

../doc/index.html: FiniteGraphA.java FiniteGraphB.java FiniteGraphC.java InfoFrame.java MathSystem.java OneDeeAgg.java OneDeeWalk.java RotorGraphsApplet.java TwoDeeAggregation.java TwoDeeWalk.java
	javadoc -link http://ups.physics.wisc.edu/java/java2-api/ -d ../doc/ *.java