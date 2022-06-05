import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import Objects.Chat;
import Objects.User;
import Objects.NetworkMessages.CreateChatRequest;
import Objects.NetworkMessages.CreateUserRequest;
import Objects.NetworkMessages.Encryption;
import Objects.NetworkMessages.KeysRequest;
import Objects.NetworkMessages.LoginRequest;
import Objects.NetworkMessages.NetworkMessage;
import Objects.NetworkMessages.QueryChatsRequest;
import Objects.NetworkMessages.SecureMessage;
import Objects.NetworkMessages.SendMessage;
import Objects.NetworkMessages.ServerResponse;
import Objects.NetworkMessages.ServerResponseChats;
import Objects.NetworkMessages.ServerResponseKeys;

public class GUIClient implements Runnable { // TODO - remove all static keywords, since this class needs to have
                                             // separate values for separate concurrent users
    private static final String hostname = "localhost";
    private static final int port = 5000;
    private static int messageID = 0;
    private static Scanner input = new Scanner(System.in);

    private static ArrayList<Chat> chats = new ArrayList<Chat>();

    private static PublicKey serverKey;
    private static PrivateKey privateKey;
    private static PublicKey publicKey;
    private static byte[][] currentChatKeys;

    // GUI values
    private int mode;
    private String newUsername;
    private String newPassword;
    private boolean serverResponse;
    private String username, password;
    private ArrayList<Chat> chatBuffer;
    private ArrayList<PublicKey> keys;
    private ArrayList<String> availableUsers;
    private String[] otherUsers;
    private String message;

    public GUIClient() {

        this.chatBuffer = new ArrayList<Chat>();
        this.keys = new ArrayList<PublicKey>();
        this.availableUsers = new ArrayList<String>();

    }

    public void run() {

        this.incrementMessageID();

        try {

            this.setup();

            switch (mode) {
                case 1: // Creating a new user (need to have newUsername and newPassword set)

                    this.serverResponse = this.createNewUser(newUsername, newPassword);
                    System.out.println("-new user creation");

                    break;

                case 2: // Logging in (need to have username and password set)

                    this.serverResponse = this.requestLogin(username, password);
                    System.out.println("-client login");

                    break;

                case 3: // Grabbing chats (username must be specified)

                    this.serverResponse = this.queryChats(username);

                    break;

                case 4: // Create a new personal chat (need to have username and otherUser set)

                    this.serverResponse = this.chatRequest(username, otherUsers);

                    break;

                case 5: // Fetch all available users from the server

                    this.dumpContacts();

                    this.serverResponse = this.queryUsers(username);

                    break;

                case 6: // Send a message to a chat (need to have username, otherUsers and message set)

                    this.serverResponse = this.sendMessage(username, otherUsers, message);

                    break;

                case 99:

                    this.testRuner();

                    break;

            }

        } catch (NoSuchAlgorithmException e) {
            System.out.print("Catastrophic error.");
        }

    }

    public void setup() throws NoSuchAlgorithmException {

        System.out.println("Client is setting up..");

        loadServerCertificate();
        loadRSAKeys();

    }

    public void dumpChatBuffer() {
        this.chatBuffer.clear();
    }

    public void dumpContacts() {
        this.availableUsers.clear();
        this.keys.clear();
    }

    public void setMode(int i) {
        this.mode = i;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setOtherUser(String otherUser) {
        this.otherUsers = new String[] { otherUser };
    }

    public void setOtherUsers(ArrayList<String> otherUsers) {
        this.otherUsers = otherUsers.toArray(new String[otherUsers.size()]);
    }

    public void setSignUpDetails(String username, String password) {
        this.newUsername = username;
        this.newPassword = password;
    }

    public void setLoginDetails(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setMessageToSend(String message) {
        this.message = message;
    }

    public boolean getServerResponse() {
        return serverResponse;
    }

    public ArrayList<Chat> getChats() {
        return this.chatBuffer;
    }

    public ArrayList<String> getAvailableUsers() {
        return this.availableUsers;
    }

    // NB message ID incremenets on every request made by GUI

    private void incrementMessageID() {
        ++messageID;
    }

    // Checks if the user wants to return to the main menu.
    private static boolean menuReturn(String choice) {
        if (choice.equals("M")) {
            System.out.println("Returning to the menu.");
            return true;
        }
        return false;
    }

    // Gets username and password of current user and stores
    // them in a User object.
    private void setUser() {
        System.out.println("Enter a username:");
        username = input.next();
        if (menuReturn(username)) {
            return;
        }
        System.out.println("Enter a password:");
        password = input.next();
    }

    // Request to create a new user
    public boolean createNewUser(String username, String password) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        // TODO - Add correct return value based on server
        NetworkMessage createRequest = new CreateUserRequest(username, Integer.toString(password.hashCode()),
                Encryption.passEncrypt(privateKey.getEncoded(), password), publicKey);
        toServer(createRequest);

        return true;
    }

    // Request to login
    private boolean requestLogin(String username, String password) {
        // TODO - Add correct return value based on server
        NetworkMessage loginRequest = new LoginRequest(username, password);
        toServer(loginRequest);

        return true;
    }

    // Get all chats the current user is involved in
    private Boolean queryChats(String username) {
        // TODO return true or false depending on if the query succeeded or failed.
        NetworkMessage query = new QueryChatsRequest(username);
        toServer(query);

        return true;
    }

    // Get all the available users and their respective keys
    private Boolean queryUsers(String username) {
        NetworkMessage keysReq = new KeysRequest(username);
        toServer(keysReq);

        return true;
    }

    // Create a chat with the users specified TODO: Create correct byte[][] keys
    private boolean chatRequest(String username, String[] otherUsers) {

        byte[][] dummy_keys = { { 10 }, { 10 }, { 10 }, { 10 }, { 10 }, { 10 } };

        NetworkMessage chatReq = new CreateChatRequest(messageID, username, otherUsers, dummy_keys);
        System.out.println(toServer(chatReq));

        return true;
    }

    private boolean sendMessage(String from, String[] to, String message) {
        // TODO, return true if successful, false otherwise.

        NetworkMessage msg = new SendMessage(messageID, from, to, message);

        toServer(msg);

        return true;
    }

    private static void chatSelection() {
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
    }

    // Sends a message
    /*
     * private void sendMsg() {
     * System.out.
     * println("To a (N)ew or (E)xisting chat? Use (M) to return to the main menu."
     * );
     * String chatChoice = input.next();
     * 
     * if (chatChoice.equals("E")) {
     * chatSelection();
     * } else if (chatChoice.equals("N")) {
     * chatRequest(username);
     * chatSelection();
     * } else if (menuReturn(chatChoice)) {
     * return;
     * } else {
     * System.out.println("Invalid choice... Please try again");
     * sendMsg();
     * return;
     * }
     * 
     * int choice = input.nextInt();
     * String[] all = chats.get(choice - 1).getUsers();
     * String[] to = new String[all.length - 1];
     * 
     * for (int i = 0; i < to.length; ++i) {
     * to[i] = all[i + 1];
     * }
     * 
     * System.out.print("Message:");
     * String message = input.nextLine();
     * System.out.println(message);
     * 
     * System.out.println(username + " -> " + Arrays.toString(to));
     * NetworkMessage msg = new SendMessage(messageID, username, to, message);
     * toServer(msg);
     * }
     * 
     */

    // New Test Method to run a serriese of tests to the server
    public void testRuner() {

        toServer(new CreateUserRequest("a", "test"));
        toServer(new CreateUserRequest("b", "test"));
        toServer(new CreateUserRequest("c", "test"));
        toServer(new CreateUserRequest("d", "test"));
        toServer(new CreateUserRequest("e", "test"));
        // toServer(new LoginRequest("a", "test"));
        byte[][] testKey = { { 10 }, { 10 }, { 10 }, { 10 }, { 10 }, { 10 } };
        toServer(new CreateChatRequest(1, "a", new String[] { "b", "c" }, testKey));
        toServer(new CreateChatRequest(1, "b", new String[] { "a" }, testKey));
        toServer(new SendMessage(2, "a", new String[] { "b", "c" }, "Hello There"));
        toServer(new SendMessage(2, "b", new String[] { "a", "c" }, "Hey There"));
        toServer(new SendMessage(3, "a", new String[] { "b", "c" }, "Hi"));
        toServer(new SendMessage(3, "b", new String[] { "a" }, "Personal Message"));
        // toServer(new QueryChatsRequest("a"));
        // toServer(new QueryChatsRequest("c"));

    }

    // Sends and receives a the specified message from the server.
    private String toServer(NetworkMessage message) {
        try (Socket socket = new Socket(hostname, port)) {
            // Increment the messageID for every server interaction.

            OutputStream output = socket.getOutputStream();
            ObjectOutputStream objectOutput = new ObjectOutputStream(output);
            // Added to test new server functions
            System.out.println("Testing service " + message.getType());

            // Encrypt the Network Message in an encrypted SecureMessage Object
            SecureMessage secureMessage = new SecureMessage(message, Encryption.sessionKey(), serverKey, privateKey);

            // Send the secure message to the Server
            objectOutput.writeObject(secureMessage);

            InputStream input = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(input);
            String messagerec;
            ServerResponse serverResponse;
            try {
                serverResponse = (ServerResponse) objectInputStream.readObject();
                messagerec = ">" + serverResponse.getMessage();
                System.out.println(messagerec);

                if (serverResponse.getSuccess()) {
                    if (serverResponse instanceof ServerResponseChats) {
                        this.chatBuffer = ((ServerResponseChats) serverResponse).getChats();
                        for (Chat i : chats) {
                            i.printm();
                        }
                    } else if (serverResponse instanceof ServerResponseKeys) {
                        this.keys = ((ServerResponseKeys) serverResponse).getKeys();
                        this.availableUsers = ((ServerResponseKeys) serverResponse).getUsers();
                        for (String k : availableUsers) {
                            System.out.println("available user: " + k);
                        }
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
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return "Failed to send message";
    }

    public static void loadServerCertificate() {
        // Load Public key on client
        try {
            KeyStore keyStoreClient = KeyStore.getInstance("PKCS12");
            keyStoreClient.load(new FileInputStream("Resources/client_keystore.p12"), "keyring".toCharArray());
            Certificate certificate = keyStoreClient.getCertificate("serverKeyPair");
            serverKey = certificate.getPublicKey();
            // System.out.println("Public
            // Key:\n"+Base64.getEncoder().encodeToString(serverKey.getEncoded()));
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            System.out.println("Unable to load Keys");
            e.printStackTrace();
            System.exit(0);
        }

    }

    public static void loadRSAKeys() {
        try {
            KeyPair pair = Encryption.generate();
            privateKey = pair.getPrivate();
            publicKey = pair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Unable to load RSA key pair");
            e.printStackTrace();
            return;
        }
    }
}