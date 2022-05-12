// A Java program for a Server
import java.net.*;
import java.io.*;
import java.util.ArrayList;

import Objects.*;
import Objects.NetworkMessages.*;


public class Server
{
	//initialize socket and input stream
	private ServerSocket server = null;
	private InputStream  input = null;
    private ObjectOutputStream objectOutput = null;

    private ArrayList<User> users;
    private ArrayList<Chat> chats;


	// constructor with port
	public Server(int port)
	{
		// starts server and waits for a connection
        try {
            server = new ServerSocket(port);

            users = new ArrayList<User>();
            chats = new ArrayList<Chat>();

            System.out.println("Server started on port " + port);
            
            //Keep on accepting clients
            while (true) {
                //Accept Client onto socket
                Socket socket = server.accept();
                System.out.println("New client connected");

                //Get message from client
                input = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(input);
                NetworkMessage message;
                try {
                    message = ((NetworkMessage) objectInputStream.readObject());
                }catch (Exception e){
                    System.out.println("Message not Valid"+e);
                    continue;
                }

                //Setup output chanel for client
                OutputStream output = socket.getOutputStream();
                objectOutput = new ObjectOutputStream(output);

                //Do something with message
                System.out.println("Client requests service: "+message.getType());
                switch(message.getType()){
                    case 1:
                        createUser(message);
                        break;
                    /*case 2:
                        createUser();
                        break;
                    case 3:
                        createUser();
                        break;
                    case 4:
                        createUser();
                        break;
                    case 5:
                        createUser();
                        break;
                    case 6:
                        createUser();
                        break;
                    case 7:
                        createUser();
                        break;
                    case 8:
                        createUser();
                        break;*/
                    default:
                        System.out.println("Invalid Message Type: "+message.getType());
                        ServerResponse response = new ServerResponse(message.getType(), message.getID(), false, "Invalid Message Type");
                        sendResponse(response);
                }
                
                //Close connection with client
                input.close();
                socket.close();
            }
    
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }	
	}

    private void createUser(NetworkMessage message){
        ServerResponse response = new ServerResponse(message.getType(), message.getID(), false, "Invalid Object Sent");
        try {
            CreateUserRequest data = (CreateUserRequest) message;
            boolean found = false;
            for (User i : users){
                if (i.is(data.getUsername())){
                    found=true;
                    break;
                }
            }
            if (!found){
                users.add(new User(data.getUsername(), data.getPassword()));
                response = new ServerResponse(message.getType(), message.getID(), true, "User Created Successfully");
            }else {
                response = new ServerResponse(message.getType(), message.getID(), false, "User Already Exists");
            }
        }catch (Exception e){
            System.out.println("Invalid Object");
        }
        //Send message to Client
        sendResponse(response);
    }

    public void sendResponse(ServerResponse response){
        try{
            objectOutput.writeObject(response);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void close(){

    }

	public static void main(String args[])
	{
		Server server = new Server(5000);
        server.close();
	}
}
