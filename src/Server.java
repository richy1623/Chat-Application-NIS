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
                    case 2:
                        checkLogin(message);
                        break;
                    case 3:
                        findChats(message);
                        break;
                    case 4:
                        //merged with 3
                        break;
                    case 5:
                        createChat(message);
                        break;
                    case 6:
                        recieveMessage(message);
                        break;
                    /*
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
                response = new ServerResponse(message.getType(), message.getID(), true, "User:"+data.getUsername()+" Created Successfully");
            }else {
                response = new ServerResponse(message.getType(), message.getID(), false, "User Already Exists");
            }
        }catch (Exception e){
            System.out.println("Invalid Object");
        }
        //Send message to Client
        sendResponse(response);
    }
    private void checkLogin(NetworkMessage message){
        ServerResponse response = new ServerResponse(message.getType(), message.getID(), false, "Invalid Object Sent");
        try {
            LoginRequest data = (LoginRequest) message;
            boolean found = false;
            for (User i : users){
                if (i.is(data.getUsername())){
                    if (i.authenticate(data.getUsername(), data.getPassword())){
                        i.resetRequests();
                        response = new ServerResponse(message.getType(), message.getID(), true, "Login Successfull");
                    }else{
                        response = new ServerResponse(message.getType(), message.getID(), false, "Incorrect Password");
                    }
                    found=true;
                    break;
                }
            }
            if (!found){
                response = new ServerResponse(message.getType(), message.getID(), false, "User Does Not Exist");
            }
        }catch (Exception e){
            System.out.println("Invalid Object");
        }
        //Send message to Client
        sendResponse(response);
    }

    private void createChat(NetworkMessage message){
        ServerResponse response = new ServerResponse(message.getType(), message.getID(), false, "Invalid Object Sent");
        if (message instanceof CreateChatRequest){
            CreateChatRequest data = (CreateChatRequest) message;
            int userIndex = getUserIndex(data.from());
            if (userIndex>=0){
                response = users.get(userIndex).madeRequest(data.getID());
                
                if (response==null){
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
                    //Save response in case user repeats the request
                    users.get(userIndex).madeRequest(response);
                }
            }else{
                response = new ServerResponse(message.getType(), message.getID(), false, "Invlaid User");
            }
            
        }else{
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

    private void recieveMessage(NetworkMessage message){
        ServerResponse response = new ServerResponse(message.getType(), message.getID(), false, "Invalid Object Sent");
        if (message instanceof SendMessage){
            SendMessage data = (SendMessage) message;
            int userIndex = getUserIndex(data.from());
            if (userIndex>=0){
                response = users.get(userIndex).madeRequest(data.getID());
                
                if (response==null){
                    boolean recipients = false;
                    
                    for (Chat i: chats){
                        if (i.is(data.from(), data.to())) {
                            recipients=true;
                            i.addMessage(data.getMessage());
                            i.printm();
                            break;
                        }
                    }
                    
                    if (recipients){
                        /*int receiver;
                        for (String i: recipients){
                            receiver = getUserIndex(i);
                            users.get(receiver).addMessage(data.getMessage());
                        }*/
                        response = new ServerResponse(message.getType(), message.getID(), true, "Message Sent");
                    }else {
                        response = new ServerResponse(message.getType(), message.getID(), false, "Chat Does Not Exist");
                    }
                    users.get(userIndex).madeRequest(response);
                }
            }else{
                response = new ServerResponse(message.getType(), message.getID(), false, "Invlaid User Sending Message");
            }
            
        }else{
            System.out.println("Invalid Object Type");
        }
        //Send message to Client
        sendResponse(response);
    }

    private void findChats(NetworkMessage message){
        ServerResponseChats response = new ServerResponseChats(message.getType(), message.getID(), false, "Invalid Object Sent");
        if (message instanceof QueryChatsRequest){
            QueryChatsRequest data = (QueryChatsRequest) message;
            int userIndex = getUserIndex(data.getUser());
            if (userIndex>=0){
                response = new ServerResponseChats(message.getType(), message.getID(), true, "Sending Chats");
                //boolean found = false;
                
                for (Chat i: chats){
                    if (i.userIn(data.getUser())) {
                        //recipients=true;
                        response.addChat(i);
                    }
                }
                
            }else{
                response = new ServerResponseChats(message.getType(), message.getID(), false, "Invlaid User Requesting Chats");
            }
            
        }else{
            System.out.println("Invalid Object Type");
        }
        //Send message to Client
        sendResponse(response);
    }

    private int getUserIndex(String u){
        for (int i=0;i<users.size();i++){
            if (users.get(i).is(u)) return i;
        }
        return -1;
    }

    public void close(){

    }

	public static void main(String args[])
	{
		Server server = new Server(5000);
        server.close();
	}
}
