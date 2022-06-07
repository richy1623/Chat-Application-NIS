import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
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
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import Objects.Chat;
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
import Objects.NetworkMessages.ServerResponseLogin;

public class GUIClient implements Runnable {

    private final String hostname = "0.0.0.0";
    private final int port = 5000;
    private int messageID = 0;

    private PublicKey serverKey;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private SecretKey currentChatKey;

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
    private boolean verbose = true;

    public GUIClient() {

        this.chatBuffer = new ArrayList<Chat>();
        this.keys = new ArrayList<PublicKey>();
        this.availableUsers = new ArrayList<String>();
        try {
            this.setup();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    public void run() {

        this.incrementMessageID();

        try {

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

                    try {
                        this.serverResponse = this.requestLogin(username, password);
                    } catch (InvalidAlgorithmParameterException e3) {
                        
                        e3.printStackTrace();
                    }

                    break;

                case 3: // Grabbing chats (username must be specified)

                    try {
                        this.serverResponse = this.queryChats(username);
                    } catch (InvalidAlgorithmParameterException e2) {
                        
                        e2.printStackTrace();
                    }

                    break;

                case 4: // Create a new personal chat (need to have username and otherUser set)

                    try {
                        this.serverResponse = this.chatRequest(username, otherUsers);
                    } catch (InvalidAlgorithmParameterException e) {
                        e.printStackTrace();
                    }

                    break;

                case 5: // Fetch all available users from the server

                    this.dumpContacts();

                    try {
                        this.serverResponse = this.queryUsers(username);
                    } catch (InvalidAlgorithmParameterException e1) {
                        
                        e1.printStackTrace();
                    }

                    break;

                case 6: // Send a message to a chat (need to have username, otherUsers and message set)

                    try {
                        this.serverResponse = this.sendMessage(username, otherUsers, message);
                    } catch (InvalidAlgorithmParameterException e) {
                        e.printStackTrace();
                    }

                    break;

            }

        } catch (NoSuchAlgorithmException e) {
            System.out.print("Catastrophic error.");
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
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
    public void incrementMessageID() {
        ++messageID;
    }

    public String getLastMessage(){
        return chatBuffer.get(chatBuffer.size()-1).getLastMessage();
    }

    // Request to create a new user
    public boolean createNewUser(String username, String password) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        loadRSAKeys();
        NetworkMessage createRequest = null;
        try {
            createRequest = new CreateUserRequest(username, Integer.toString(password.hashCode()),
                    Encryption.passEncrypt(privateKey.getEncoded(), password), publicKey);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        boolean success = toServer(createRequest).getSuccess();

        return success;
    }

    // Request to login
    public boolean requestLogin(String username, String password) throws InvalidAlgorithmParameterException {
        NetworkMessage loginRequest = new LoginRequest(username, Integer.toString(password.hashCode()));
        ServerResponseLogin passed = (ServerResponseLogin) toServer(loginRequest);
        if (passed.getSuccess()) {
            publicKey = passed.getPublicKey();
            try {
                if (verbose)
                    System.out.println("$Decrypting private key from the server with password: " + password);
                privateKey = Encryption.generatePrivate(Encryption.passcrDecrypt(passed.getPrivateKey(), password));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return passed.getSuccess();
    }

    // Get all chats the current user is involved in
    public boolean queryChats(String username) throws InvalidAlgorithmParameterException {
        NetworkMessage query = new QueryChatsRequest(username);
        boolean success = toServer(query).getSuccess();

        return success;
    }

    // Get all the available users and their respective keys
    public boolean queryUsers(String username) throws InvalidAlgorithmParameterException {

        NetworkMessage keysReq = new KeysRequest(username);
        boolean success = toServer(keysReq).getSuccess();

        return success;
    }

    // Create a chat with the users specified
    public boolean chatRequest(String username, String[] otherUsers) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

        byte[][] chatKeys = new byte[otherUsers.length + 1][];
        currentChatKey = Encryption.sessionKey();

        System.out.println("Chat key is: " + currentChatKey.getEncoded());

        chatKeys[0] = Encryption.encryptionRSA(currentChatKey.getEncoded(), publicKey);
        for (int i = 0; i < otherUsers.length; i++) {
            int userIndex = availableUsers.indexOf(otherUsers[i]);
            System.out.println(otherUsers[i]);
            //System.out.println(availableUsers.get(userIndex));
            //System.out.println(Base64.getEncoder().encodeToString(keys.get(userIndex).getEncoded()));

            chatKeys[i + 1] = Encryption.encryptionRSA(currentChatKey.getEncoded(), keys.get(userIndex));
        }

        NetworkMessage chatReq = new CreateChatRequest(messageID, username, otherUsers, chatKeys);
        boolean success = toServer(chatReq).getSuccess();

        return success;
    }

    public byte[] getCurrentChatKey(String user, String[] to) {
        for (Chat c : chatBuffer) {
            if (c.is(user, to)) {
                return c.getKey();
            }
        }
        return null;
    }

    public boolean sendMessage(String from, String[] to, String message) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

        byte[] decryptedKey = Encryption.decryptionRSA(getCurrentChatKey(from, to), privateKey);
        SecretKey chatKey = Encryption.generateSecretKey(decryptedKey);

        String ciperText = Base64.getEncoder().encodeToString(Encryption.encryptionAES(message.getBytes(), chatKey));
        if (verbose)
            System.out.println("$Message To Encrypt: " + message);
        if (verbose)
            System.out.println("$Encrypted Message: " + ciperText);

        NetworkMessage msg = new SendMessage(messageID, from, to, ciperText);

        boolean success = toServer(msg).getSuccess();

        return success;
    }

    // New Test Method to run a serriese of tests to the server
    public void testRuner() throws InvalidAlgorithmParameterException {

        toServer(new CreateUserRequest("a", "test"));
        toServer(new CreateUserRequest("b", "test"));
        toServer(new CreateUserRequest("c", "test"));
        toServer(new CreateUserRequest("d", "test"));
        toServer(new CreateUserRequest("e", "test"));
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
    private ServerResponse toServer(NetworkMessage message) throws InvalidAlgorithmParameterException {
        try (Socket socket = new Socket(hostname, port)) {
            // Increment the messageID for every server interaction.

            OutputStream output = socket.getOutputStream();
            ObjectOutputStream objectOutput = new ObjectOutputStream(output);
            // Added to test new server functions
            System.out.println("\n$Requesting Server service " + message.getType());
            System.out.println("$Creating a network message");

            // Encrypt the Network Message in an encrypted SecureMessage Object
            SecureMessage secureMessage = new SecureMessage(message, Encryption.sessionKey(), serverKey, privateKey);

            // Send the secure message to the Server
            objectOutput.writeObject(secureMessage);

            InputStream input = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(input);
            String messagerec;
            ServerResponse serverResponse;

            try {
                System.out.println("\n$Reciving Network Message from Server");
                SecureMessage secmessage = (SecureMessage) objectInputStream.readObject();
                serverResponse = (ServerResponse) secmessage.decrypt(privateKey);
                messagerec = ">" + serverResponse.getMessage();
                if (secmessage.validate(serverKey)) {
                    System.out.println(messagerec);
                    if (serverResponse.getSuccess()) {
                        if (serverResponse instanceof ServerResponseChats) {
                            this.chatBuffer = ((ServerResponseChats) serverResponse).getChats();
                            System.out.println("Number of chats: " + chatBuffer.size());
                            for (Chat i : chatBuffer) {
                                if (verbose) {
                                    System.out.println("$Recieving Encrypted Messages: ");
                                    i.printm();
                                }
                                //System.out.println(i.getMessagesFrom(0)[0].getContent());
                                System.out.println("Encrypted Chat Key- " + i.getKey());
                                SecretKey chatKey = Encryption
                                        .generateSecretKey(Encryption.decryptionRSA(i.getKey(), privateKey));
                                System.out.println("$Decrypting Chat Key using own Private Key");
                                System.out.println("Decrypted Chat Key- " + chatKey);
                                i.decrypt(chatKey);
                                if (verbose) {
                                    System.out.println("$Decrypting Messages: ");
                                    i.printm();
                                }
                            }
                        } else if (serverResponse instanceof ServerResponseKeys) {
                            this.keys = ((ServerResponseKeys) serverResponse).getKeys();
                            this.availableUsers = ((ServerResponseKeys) serverResponse).getUsers();
                            for (String k : availableUsers) {
                                System.out.println("\tavailable user: " + k);
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