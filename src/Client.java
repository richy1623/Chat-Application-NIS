import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.*;

import Objects.NetworkMessages.*;
import Objects.Chat;
import Objects.User;

public class Client {
    private static final String hostname = "localhost";
    private static final int port = 5000;
    private static int messageID = 0;
    private static Scanner input = new Scanner(System.in);
    private static User current;
    private static String username, password;
    private static ArrayList<Chat> chats = new ArrayList<Chat>();

    public static void main(String[] args) {
        testRuner();

        while (true) {
            System.out.println("===========================================");
            System.out.println("Enter a number:");
            System.out.println("===========================================");
            System.out.println("(1) - Create an account.");
            System.out.println("(2) - Login.");
            System.out.println("(3) - Request all chats open.");
            System.out.println("(5) - Create a chat.");
            System.out.println("(6) - Send a message to someone.");
            System.out.println("===========================================");

            String in = input.next();
            if (in.equals("q") || in.equals("Q")) {
                break;
            }

            ++messageID;
            int choice = Integer.parseInt(in);
            switch (choice) {
                // Create a new user on the server.
                case 1:
                    setUser();
                    createNewUser();
                    break;

                // Make a login request to the server.
                case 2:
                    setUser();
                    requestLogin();
                    break;

                // Query all the chats on the server for a user.
                case 3:
                    queryChats();
                    break;

                // Create a chat with someone.
                case 5:
                    chatRequest();
                    break;

                // Send a message to someone.
                case 6:
                    sendMsg();
                    break;
            }
        }

        input.close();
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
            // Added to test new server functions

            // Create Network Message here for your type of request
            CreateUserRequest message2 = new CreateUserRequest("testuser" + messageID, "Test");

            // Sends Message to server
            objectOutput.writeObject(message2);

            // Recieve Message from server
            InputStream input = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(input);

            // Extract Data from the message
            String messagerec;
            try {
                messagerec = ((ServerResponse) objectInputStream.readObject()).getMessage();
            } catch (Exception e) {
                messagerec = "Error: " + e;
            }

            // String serverResponse = reader.readLine();
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

    // Gets username and password of current user and stores
    // them in a User object.
    private static void setUser() {
        System.out.println("Enter a username:");
        username = input.next();
        System.out.println("Enter a password:");
        password = input.next();
        current = new User(username, password);
    }

    // Request to create a new user
    private static void createNewUser() {
        NetworkMessage createRequest = new CreateUserRequest(username, password);
        testIndividual(createRequest);
    }

    // Request to login
    private static void requestLogin() {
        NetworkMessage loginRequest = new LoginRequest(username, password);
        testIndividual(loginRequest);
    }

    // Get all chats the current user is involved in
    private static void queryChats() {
        NetworkMessage query = new QueryChatsRequest(username);
        testIndividual(query);
    }

    // Create a chat with the users specified
    private static void chatRequest() {
        System.out.println("Enter the number of users you want to include in this chat:");
        int amount = input.nextInt();
        String[] receivers = new String[amount];

        System.out.println("Enter the usernames, one by one:");
        for (int i = 0; i < amount; ++i) {
            receivers[i] = input.next();
        }

        NetworkMessage chatReq = new CreateChatRequest(messageID, username, receivers);
        testIndividual(chatReq);

        Chat aChat = new Chat(username, receivers);
        chats.add(aChat);
    }

    // Sends a message
    private static void sendMsg() {
        System.out.println("Choose a chat to send a message to:");
        for (int i = 0; i < chats.size(); ++i) {
            System.out.print("(" + (i + 1) + ") - To: [");
            String[] to = chats.get(i).getUsers();
            for (int j = 1; j < to.length; ++j) {
                if (j == to.length - 1) {
                    System.out.println(to[j] + "]");
                } else {
                    System.out.print(" " + to[j] + ", ");
                }
            }
        }

        int choice = input.nextInt();
        String[] all = chats.get(choice - 1).getUsers();
        String[] to = new String[all.length - 1];

        for (int i = 0; i < to.length; ++i) {
            to[i] = all[i + 1];
        }

        System.out.print("Message:");
        String message = input.next();
        System.out.println(message);

        System.out.println(username + " -> " + Arrays.toString(to));
        NetworkMessage msg = new SendMessage(messageID, username, to, message);
        testIndividual(msg);
    }

    // New Test Method to run a serriese of tests to the server
    public static void testRuner() {
        testIndividual(new CreateUserRequest("testuser1", "Test"));
        testIndividual(new CreateUserRequest("testuser2", "Test"));
        testIndividual(new CreateUserRequest("testuser3", "Test"));
        testIndividual(new LoginRequest("testuser1", "Test"));
        testIndividual(new CreateChatRequest(1, "testuser1", new String[] { "testuser2", "testuser3" }));
        testIndividual(new CreateChatRequest(1, "testuser2", new String[] { "testuser1" }));
        testIndividual(new SendMessage(2, "testuser1", new String[] { "testuser2", "testuser3" }, "Hello There"));
        testIndividual(new SendMessage(2, "testuser2", new String[] { "testuser1", "testuser3" }, "Hey There"));
        testIndividual(new SendMessage(3, "testuser1", new String[] { "testuser2", "testuser3" }, "Hi"));
        testIndividual(new SendMessage(3, "testuser2", new String[] { "testuser1" }, "Personal Message"));
        testIndividual(new QueryChatsRequest("testuser1"));
        // testIndividual(new QueryChatsRequest("testuser3"));

    }

    // Helper method to test the individual comonents for the tests
    private static String testIndividual(NetworkMessage message) {
        try (Socket socket = new Socket(hostname, port)) {
            // Increment the messageID for every server interaction.

            OutputStream output = socket.getOutputStream();
            ObjectOutputStream objectOutput = new ObjectOutputStream(output);
            // Added to test new server functions
            System.out.println("Testing service " + message.getType());

            objectOutput.writeObject(message);

            InputStream input = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(input);
            String messagerec;
            ServerResponse serverResponse;
            try {
                serverResponse = (ServerResponse) objectInputStream.readObject();
                messagerec = ">" + serverResponse.getMessage();
                System.out.println(messagerec);
                if (serverResponse instanceof ServerResponseChats) {
                    ArrayList<Chat> chats = ((ServerResponseChats) serverResponse).getChats();
                    for (Chat i : chats) {
                        i.printm();
                    }
                }
            } catch (Exception e) {
                messagerec = "Error: " + e;
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