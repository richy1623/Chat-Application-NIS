import java.net.*;
import java.util.ArrayList;
import java.io.*;

import Objects.NetworkMessages.*;
import Objects.Chat;

public class Client {
    private static final String hostname = "localhost";
    private static final int port = 5000;
    private static int messageID = 0;

    public static void main(String[] args) {
        testRuner();
    }

    // Test method to ping the server and receive a ping back
    // Server will recieve single messages from client, then restart the connection
    // (Call this method again)
    public static String sendNetworkMessage(int type) {
        try (Socket socket = new Socket(hostname, port)) {
            // Increment the messageID for every server interaction.
            ++messageID;

            OutputStream output = socket.getOutputStream();
            ObjectOutputStream objectOutput = new ObjectOutputStream(output);
            //Added to test new server functions

            //Create Network Message here for your type of request
            CreateUserRequest message2=new CreateUserRequest("testuser"+messageID, "Test");
            
            //Sends Message to server
            objectOutput.writeObject(message2);
            
            //Recieve Message from server
            InputStream input = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(input);

            //Extract Data from the message
            String messagerec;
            try {
                messagerec = ((ServerResponse) objectInputStream.readObject()).getMessage();
            }catch (Exception e){
                messagerec = "Error: "+e;
            }

            //String serverResponse = reader.readLine();
            String serverResponse = ">" + messagerec;

            System.out.println(serverResponse);

            socket.close();
            return serverResponse;

        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }
        return "Failed to send message";
    }

    // Request to create a new user
    public static void requestNewUser() {
        //NetworkMessage msg = new NetworkMessage();
    }

    // Request to login
    public static void requestLogin() {

    }

    //New Test Method to run a serriese of tests to the server
    public static void testRuner(){
        testIndividual(new CreateUserRequest("testuser1", "Test"));
        testIndividual(new CreateUserRequest("testuser2", "Test"));
        testIndividual(new CreateUserRequest("testuser3", "Test"));
        testIndividual(new LoginRequest("testuser1", "Test"));
        testIndividual(new CreateChatRequest(messageID, "testuser1", new String[] {"testuser2","testuser3"}));
        testIndividual(new SendMessage(1, "testuser1", new String[] {"testuser2","testuser3"}, "Hello There"));
        testIndividual(new SendMessage(1, "testuser2", new String[] {"testuser1","testuser3"}, "Hey There"));
        testIndividual(new SendMessage(2, "testuser1", new String[] {"testuser2","testuser3"}, "Hi"));
        testIndividual(new QueryChatsRequest("testuser1"));
        //testIndividual(new QueryChatsRequest("testuser3"));

    }

    //Helper method to test the individual comonents for the tests
    private static String testIndividual(NetworkMessage message){
        try (Socket socket = new Socket(hostname, port)) {
            // Increment the messageID for every server interaction.

            OutputStream output = socket.getOutputStream();
            ObjectOutputStream objectOutput = new ObjectOutputStream(output);
            //Added to test new server functions
            System.out.println("Testing service "+message.getType());
            
            objectOutput.writeObject(message);
            
            InputStream input = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(input);
            String messagerec;
            ServerResponse serverResponse;
            try {
                serverResponse = (ServerResponse) objectInputStream.readObject();
                messagerec= ">" + serverResponse.getMessage();
                System.out.println(messagerec);
                if (serverResponse instanceof ServerResponseChats){
                    ArrayList<Chat> chats = ((ServerResponseChats) serverResponse).getChats();
                    for(Chat i: chats){
                        i.printm();
                    }
                }
            }catch (Exception e){
                messagerec = "Error: "+e;
            }
            socket.close();
            return messagerec;

        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }
        return "Failed to send message";
    }
}