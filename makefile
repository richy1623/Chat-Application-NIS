all: 
	javac -d ../classes *.java

server:
	java -classpath classfiles Server

client:
	java -classpath classfiles Client

GUI:
	java -classpath classfiles GUI

clean:
	find . -name \*.class -type f -delete