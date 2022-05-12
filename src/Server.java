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
                System.out.println("\nNew client connected");

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
                System.out.println(">Client requests service: "+message.getType());
                switch(message.getType()){
                    case 1:
                        createUser(message);
                        break;
                    /*case 2:
                        createUser(message);
                        break;
                    case 3:
                        createUser(message);
                        break;
                    case 4:
                        createUser(message);
                        break;
                    */
                    case 5:
                        createChat(message);
                        break;
                    /*
                    case 6:
                        createUser(message);
                        break;
                    case 7:
                        createUser(message);
                        break;
                    case 8:
                        createUser(message);
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
                System.out.println("Creating User: "+data.getUsername());
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

    private void createChat(NetworkMessage message){
        ServerResponse response = new ServerResponse(message.getType(), message.getID(), false, "Invalid Object Sent");
        try {
            CreateChatRequest data = (CreateChatRequest) message;
            boolean found = false;
            
            for (Chat i: chats){
                if (i.is(data.from(), data.with())) {
                    found=true;
                    break;
                }
            }
            
            if (!found){
                chats.add(new Chat(data.from(), data.with()));
                response = new ServerResponse(message.getType(), message.getID(), true, "Chat Created Successfully");
            }else {
                response = new ServerResponse(message.getType(), message.getID(), false, "Chat Already Exists");
            }
            
        }catch (Exception e){
            System.out.println("Invalid Object Type");
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
