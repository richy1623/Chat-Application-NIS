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
import javax.crypto.SecretKey;

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

public class GUIClient implements Runnable {

    private final String hostname = "localhost";
    private final int port = 5000;
    private int messageID = 0;

    private ArrayList<Chat> chats = new ArrayList<Chat>();

    private PublicKey serverKey;
    private PrivateKey privateKey;
    private PublicKey publicKey; // ?

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

                    try {
                        this.serverResponse = this.createNewUser(newUsername, newPassword);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println("-new user creation");

                    break;

                case 2: // Logging in (need to have username and password set)

                    this.serverResponse = this.requestLogin(username, password);

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

                    //this.testRuner();

                    break;

            }

        } catch (NoSuchAlgorithmException e) {
            System.out.print("Catastrophic error.");
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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

    // Request to create a new user
    public boolean createNewUser(String username, String password) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        // TODO - Add correct return value based on server

        NetworkMessage createRequest = null;
        try {
            createRequest = new CreateUserRequest(username, Integer.toString(password.hashCode()),
                    Encryption.passEncrypt(privateKey.getEncoded(), password), publicKey);
        } catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        toServer(createRequest);

        return true;
    }

    // Request to login
    private boolean requestLogin(String username, String password) {
        // TODO - Add correct return value based on server
        NetworkMessage loginRequest = new LoginRequest(username, Integer.toString(password.hashCode()));
        ServerResponse passed = toServer(loginRequest);

        return passed.getSuccess();
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
    private boolean chatRequest(String username, String[] otherUsers) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

        byte[][] chatKeys = new byte[otherUsers.length][];
        SecretKey chatKey = Encryption.sessionKey();

        for (int i = 0; i < otherUsers.length; ++i) {
            int userIndex = availableUsers.indexOf(otherUsers[i]);

            System.out.println(otherUsers[i]);
            System.out.println(availableUsers.get(userIndex));
            System.out.println(keys.get(userIndex));

            chatKeys[i] = Encryption.encryptionRSA(chatKey.getEncoded(), keys.get(userIndex));
        }

        NetworkMessage chatReq = new CreateChatRequest(messageID, username, otherUsers, chatKeys);
        System.out.println(toServer(chatReq));

        return true;
    }

    private boolean sendMessage(String from, String[] to, String message) {
        // TODO, return true if successful, false otherwise.

        NetworkMessage msg = new SendMessage(messageID, from, to, message);

        toServer(msg);

        return true;
    }

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
    private ServerResponse toServer(NetworkMessage message) {
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
                SecureMessage secmessage = (SecureMessage) objectInputStream.readObject();
                serverResponse = (ServerResponse) secmessage.decrypt(privateKey);
                messagerec = ">" + serverResponse.getMessage();
                System.out.println(messagerec);
                if (secmessage.validate(serverKey)) {
                    if (serverResponse.getSuccess()) {
                        if (serverResponse instanceof ServerResponseChats) {
                            this.chatBuffer = ((ServerResponseChats) serverResponse).getChats();
                            for (Chat i : chats) {
                                i.printm();
                                System.out.println(i.getKey());
                            }
                        } else if (serverResponse instanceof ServerResponseKeys) {
                            this.keys = ((ServerResponseKeys) serverResponse).getKeys();
                            this.availableUsers = ((ServerResponseKeys) serverResponse).getUsers();
                            for (String k : availableUsers) {
                                System.out.println("available user: " + k);
                            }
                        }
                    }
                }

            } catch (Exception e) {
                messagerec = "Error: " + e;
                serverResponse = new ServerResponse(-1, -1, false, "error: " + e);
            }
            socket.close();
            return serverResponse;

        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());
            return new ServerResponse(-1, -1, false, "error: Server can't be found");

        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return new ServerResponse(-1, -1, false, "error: Failed to send message");
    }

    public void loadServerCertificate() {
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

    public void loadRSAKeys() {
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