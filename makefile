all: 
	cd src; javac -d ../classfiles *.java

server:
	java -classpath classfiles Server

client:
	java -classpath classfiles Client

GUI:
	java -classpath classfiles GUI

Populate:
	java -classpath classfiles GUI Populate

clean:
	find . -name \*.class -type f -delete