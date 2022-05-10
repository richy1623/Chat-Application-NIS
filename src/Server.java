// A Java program for a Server
import java.net.*;
import java.io.*;

public class Server
{
	//initialize socket and input stream
	private ServerSocket server = null;
	private InputStream  input = null;

	// constructor with port
	public Server(int port)
	{
		// starts server and waits for a connection
        try {
            server = new ServerSocket(port);

            System.out.println("Server started on port " + port);
            
            //Keep on accepting clients
            while (true) {
                //Accept Client onto socket
                Socket socket = server.accept();
                System.out.println("New client connected");

                //Get message from client
                input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String message = reader.readLine();

                //Do something with message
                System.out.println("Client: "+message);

                //Setup output chanel for client
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                
                //Return message to Client
                writer.println("Server: " + message);
                
                //Close connection with client
                input.close();
                socket.close();
            }
    
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }	
	}

	public static void main(String args[])
	{
		Server server = new Server(5000);
	}
}
