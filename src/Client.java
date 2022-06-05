import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyRep;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

public class Client {
    private static final String hostname = "localhost";
    private static final int port = 5000;
    private static int messageID = 0;
    private static Scanner input = new Scanner(System.in);
    private static String username, password;
    private static ArrayList<Chat> chats = new ArrayList<Chat>();

    private static PublicKey serverKey;
    private static PrivateKey privateKey;
    private static PublicKey publicKey;
    private static HashMap<String, PublicKey> userKeys = new HashMap<>();
    private static byte[][] keys;

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {
        loadServerCertificate();
        loadRSAKeys();
        // testRuner();

        while (true) {
            System.out.println("===========================================");
            System.out.println("Enter a number or (Q) to quit:");
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
    private static void setUser() {
        System.out.println("Enter a username:");
        username = input.next();
        if (menuReturn(username)) {
            return;
        }
        System.out.println("Enter a password:");
        password = input.next();
    }

    // Request to create a new user
    private static void createNewUser() {
        NetworkMessage createRequest = new CreateUserRequest(username, password, privateKey.getEncoded(), publicKey);
        toServer(createRequest);
    }

    // Request to login
    private static void requestLogin() {
        NetworkMessage loginRequest = new LoginRequest(username, password);
        toServer(loginRequest);
    }

    // Get all chats the current user is involved in
    private static void queryChats() {
        NetworkMessage query = new QueryChatsRequest(username);
        toServer(query);
    }

    // Create a chat with the users specified
    private static void chatRequest() {
        System.out.println("Enter the number of users you want to include in this chat:");
        String choice = input.next();
        if (menuReturn(choice)) {
            return;
        }
        int amount = Integer.parseInt(choice);
        String[] receivers = new String[amount];

        System.out.println("Enter the usernames, one by one:");
        for (int i = 0; i < amount; ++i) {
            receivers[i] = input.next();
        }

        // Requesting keys
        NetworkMessage keysReq = new KeysRequest(username);
        toServer(keysReq);

        NetworkMessage chatReq = new CreateChatRequest(messageID, username, receivers, keys);
        toServer(chatReq);

        Chat aChat = new Chat(username, receivers);
        chats.add(aChat);
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

    // Still need to finish encryption for pgp.
    private static void encrypt(String message) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        System.out.println("Encrypting message...");
        byte[] encrypted = Encryption.encryptionRSA(message.getBytes(), privateKey);
        System.out.println(new String(encrypted));

    }

    private static void decrypt(String message) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        System.out.println("Decrypting message...");
        byte[] decrypted = Encryption.decryptionRSA(message.getBytes(), publicKey);
        System.out.println(new String(decrypted));
    }

    // Sends a message
    private static void sendMsg() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {
        System.out.println("To a (N)ew or (E)xisting chat? Use (M) to return to the main menu.");
        String chatChoice = input.next();

        if (chatChoice.equals("E")) {
            chatSelection();
        } else if (chatChoice.equals("N")) {
            chatRequest();
            chatSelection();
        } else if (menuReturn(chatChoice)) {
            return;
        } else {
            System.out.println("Invalid choice... Please try again");
            sendMsg();
            return;
        }

        int choice = input.nextInt();
        String[] all = chats.get(choice - 1).getUsers();
        String[] to = new String[all.length - 1];

        for (int i = 0; i < to.length; ++i) {
            to[i] = all[i + 1];
        }

        System.out.print("Message:");
        input.nextLine();
        String message = input.nextLine();

        encrypt(message);

        System.out.println(username + " -> " + Arrays.toString(to));
        NetworkMessage msg = new SendMessage(messageID, username, to, message);
        toServer(msg);
    }

    // New Test Method to run a serriese of tests to the server
    /*
     * public static void testRuner() {
     * 
     * toServer(new CreateUserRequest("testuser1", "Test"));
     * toServer(new CreateUserRequest("testuser2", "Test"));
     * toServer(new CreateUserRequest("testuser3", "Test"));
     * toServer(new LoginRequest("testuser1", "Test"));
     * toServer(new CreateChatRequest(1, "testuser1", new String[] {
     * "testuser2", "testuser3" }));
     * toServer(new CreateChatRequest(1, "testuser2", new String[] {
     * "testuser1" }));
     * toServer(new SendMessage(2, "testuser1", new String[] { "testuser2",
     * "testuser3" }, "Hello There"));
     * toServer(new SendMessage(2, "testuser2", new String[] { "testuser1",
     * "testuser3" }, "Hey There"));
     * toServer(new SendMessage(3, "testuser1", new String[] { "testuser2",
     * "testuser3" }, "Hi"));
     * toServer(new SendMessage(3, "testuser2", new String[] { "testuser1" },
     * "Personal Message"));
     * toServer(new QueryChatsRequest("testuser1"));
     * // toServer(new QueryChatsRequest("testuser3"));
     * 
     * }
     */

    // Helper method to test the individual comonents for the tests
    private static String toServer(NetworkMessage message) {
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
                //TODO: proper secure object handling
                serverResponse = (ServerResponse) objectInputStream.readObject();
                messagerec = ">" + serverResponse.getMessage();
                System.out.println(messagerec);
                if (serverResponse instanceof ServerResponseChats) {
                    ArrayList<Chat> chats = ((ServerResponseChats) serverResponse).getChats();
                    for (Chat i : chats) {
                        i.printm();
                    }
                } else if (serverResponse instanceof ServerResponseKeys) {
                    ArrayList<PublicKey> keys = ((ServerResponseKeys) serverResponse).getKeys();
                    for (PublicKey k : keys) {
                        System.out.println(k.getEncoded());
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