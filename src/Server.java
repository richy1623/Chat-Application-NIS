
// A Java program for a Server
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Base64;

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
import Objects.NetworkMessages.ServerResponseLogin;

public class Server {
    // initialize socket and input stream
    private ServerSocket server = null;
    private InputStream input = null;
    private ObjectOutputStream objectOutput = null;

    // Encryption Objects
    private PrivateKey privateKey;
    private SecureMessage secureMessage;

    private ArrayList<User> users;
    private ArrayList<Chat> chats;

    // constructor with port
    public Server(int port) {
        loadPrivateKey();
        // starts server and waits for a connection
        try {
            server = new ServerSocket(port);
            server.getInetAddress();
            InetAddress serverAdress = InetAddress.getLocalHost();

            users = new ArrayList<User>();
            chats = new ArrayList<Chat>();

            System.out.println("\n$Server started with adress: " + serverAdress.getHostAddress());
            System.out.println("$Server started on port: " + port);

            // Keep on accepting clients
            while (true) {
                // Accept Client onto socket
                Socket socket = server.accept();
                System.out.println("\nNew client connected");

                // Get message from client
                input = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(input);
                NetworkMessage message;
                try {
                    secureMessage = (SecureMessage) objectInputStream.readObject();
                    message = secureMessage.decrypt(privateKey);
                } catch (Exception e) {
                    System.out.println("Message not Valid" + e);
                    input.close();
                    socket.close();
                    continue;
                }

                // Setup output chanel for client
                OutputStream output = socket.getOutputStream();
                objectOutput = new ObjectOutputStream(output);

                // Do something with message
                System.out.println("$Handling Client Request");
                System.out.println(">Client requests service: " + message.getType());
                switch (message.getType()) {
                    case 1:
                        createUser(message);
                        break;
                    case 2:
                        checkLogin(message);
                        break;
                    case 3:
                        findChats(message);
                        break;
                    case 5:
                        createChat(message);
                        break;
                    case 6:
                        recieveMessage(message);
                        break;
                    case 7:
                        getKeys(message);
                        break;
                    /*
                     * case 8:
                     * createUser(message);
                     * break;
                     */
                    default:
                        System.out.println("Invalid Message Type: " + message.getType());
                        ServerResponse response = new ServerResponse(message.getType(), message.getID(), false,
                                "Invalid Message Type");
                        sendResponse(response, null);
                }

                // Close connection with client
                input.close();
                socket.close();
            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void createUser(NetworkMessage message) {
        ServerResponse response = new ServerResponse(message.getType(), message.getID(), false, "Invalid Object Sent");
        try {
            CreateUserRequest data = (CreateUserRequest) message;
            boolean found = false;
            for (User i : users) {
                if (i.is(data.getUsername())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                users.add(new User(data.getUsername(), data.getPassword(), data.getKey(), data.getPublicKey()));
                System.out.println("$Creating User: " + data.getUsername());
                System.out.print("$Listing all users: ");
                for (User u : users) {
                    System.out.print(u.getUsername() + ", ");
                }
                System.out.println();

                response = new ServerResponse(message.getType(), message.getID(), true,
                        "User:" + data.getUsername() + " Created Successfully");
            } else {
                response = new ServerResponse(message.getType(), message.getID(), false, "User Already Exists");
            }
        } catch (Exception e) {
            System.out.println("Invalid Object");
        }
        // Send message to Client
        sendResponse(response, null);
    }

    private void checkLogin(NetworkMessage message) {
        ServerResponseLogin response = new ServerResponseLogin(message.getType(), message.getID(), false,
                "Invalid Object Sent");
        try {
            LoginRequest data = (LoginRequest) message;
            System.out.println(
                    "$User attempting login: username=" + data.getUsername() + "\tpasswordHash=" + data.getPassword());
            boolean found = false;
            for (User i : users) {
                if (i.is(data.getUsername())) {
                    if (i.authenticate(data.getUsername(), data.getPassword())) {
                        i.resetRequests();
                        response = new ServerResponseLogin(message.getType(), message.getID(), true,
                                "Login Successful");
                        response.addPrivateKey(i.getPrivateKey());
                        response.addPublicKey(i.getPublicKey());
                    } else {
                        response = new ServerResponseLogin(message.getType(), message.getID(), false,
                                "Incorrect Password");
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                response = new ServerResponseLogin(message.getType(), message.getID(), false, "User Does Not Exist");
            }
        } catch (Exception e) {
            System.out.println("Invalid Object");
        }
        // Send message to Client
        sendResponse(response, null);
    }

    private void createChat(NetworkMessage message) {
        ServerResponse response = new ServerResponse(message.getType(), message.getID(), false, "Invalid Object Sent");
        PublicKey clientKey = null;
        if (message instanceof CreateChatRequest) {
            CreateChatRequest data = (CreateChatRequest) message;
            int userIndex = getUserIndex(data.from());
            if (userIndex >= 0) {
                response = users.get(userIndex).getPriorRequest(data.getID());

                // Validate Signature
                clientKey = users.get(userIndex).getPublicKey();
                if (!validate(clientKey)) {
                    response = new ServerResponse(message.getType(), message.getID(), false, "Invalid Signature");
                    sendResponse(response, null);
                    return;
                }

                if (response == null) {
                    boolean found = false;

                    for (Chat i : chats) {
                        if (i.is(data.from(), data.with())) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        Chat newChat = new Chat(data.from(), data.with());
                        chats.add(newChat);
                        newChat.addChatToUsers(users, data.getKeys());
                        response = new ServerResponse(message.getType(), message.getID(), true,
                                "Chat Created Successfully");
                        System.out.println("$Created Chat " + newChat.getChatName());
                    } else {
                        response = new ServerResponse(message.getType(), message.getID(), false, "Chat Already Exists");
                    }
                    // Save response in case user repeats the request
                    users.get(userIndex).addRequest(response);
                }
            } else {
                response = new ServerResponse(message.getType(), message.getID(), false, "Invlaid User");
            }

        } else {
            System.out.println("Invalid Object Type");
        }
        // Send message to Client
        sendResponse(response, clientKey);
    }

    public String arrayToString(String[] arr) {
        String out = "";

        for (String s : arr) {
            out = out + s;
        }

        return out;
    }

    private void sendResponse(ServerResponse response, PublicKey clientKey) {
        try {
            System.out.println("\n$Sending Response to Client");
            SecureMessage secure = new SecureMessage(response, Encryption.sessionKey(), clientKey, privateKey);
            objectOutput.writeObject(secure);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void recieveMessage(NetworkMessage message) {
        ServerResponse response = new ServerResponse(message.getType(), message.getID(), false, "Invalid Object Sent");
        PublicKey clientKey = null;
        if (message instanceof SendMessage) {
            SendMessage data = (SendMessage) message;
            int userIndex = getUserIndex(data.from());
            if (userIndex >= 0) {
                response = users.get(userIndex).getPriorRequest(data.getID());

                // Validate Signature
                clientKey = users.get(userIndex).getPublicKey();
                if (!validate(clientKey)) {
                    response = new ServerResponse(message.getType(), message.getID(), false, "Invalid Signature");
                    sendResponse(response, null);
                    return;
                }

                if (response == null) {

                    boolean recipients = false;

                    for (Chat i : chats) {
                        if (i.is(data.from(), data.to())) {
                            recipients = true;
                            i.addMessage(data.getMessage());
                            break;
                        }
                    }

                    if (recipients) {
                        response = new ServerResponse(message.getType(), message.getID(), true, "Message Sent");
                    } else {
                        response = new ServerResponse(message.getType(), message.getID(), false, "Chat Does Not Exist");
                    }
                    users.get(userIndex).addRequest(response);
                }
            } else {
                response = new ServerResponse(message.getType(), message.getID(), false,
                        "Invlaid User Sending Message");
            }

        } else {
            System.out.println("Invalid Object Type");
        }
        // Send message to Client
        sendResponse(response, clientKey);
    }

    private void getKeys(NetworkMessage message) {
        ServerResponseKeys response = new ServerResponseKeys(message.getType(), message.getID(), false,
                "Invalid Object Sent");
        PublicKey clientKey = null;
        if (message instanceof KeysRequest) {
            KeysRequest data = (KeysRequest) message;
            response = new ServerResponseKeys(message.getType(), message.getID(), true,
                    "Successful return of all keys and users");

            // Validate Signature
            int userIndex = getUserIndex(data.getUser());
            if ((userIndex < 0) || (!validate(users.get(userIndex).getPublicKey()))) {
                response = new ServerResponseKeys(message.getType(), message.getID(), false, "Invalid Signature");
                sendResponse(response, null);
                return;
            }

            // Add user and their public keys to response
            for (User i : users) {
                response.addEntity(i.getPublicKey(), i.getUsername());
            }

        } else {
            System.out.println("Invalid Object Type");
        }
        // Send message to Client
        sendResponse(response, clientKey);
    }

    private void findChats(NetworkMessage message) {
        ServerResponseChats response = new ServerResponseChats(message.getType(), message.getID(), false,
                "Invalid Object Sent");
        PublicKey clientKey = null;
        if (message instanceof QueryChatsRequest) {
            QueryChatsRequest data = (QueryChatsRequest) message;
            int userIndex = getUserIndex(data.getUser());
            if (userIndex >= 0) {
                response = new ServerResponseChats(message.getType(), message.getID(), true, "Sending Chats");

                // Validate Signature
                clientKey = users.get(userIndex).getPublicKey();
                if (!validate(clientKey)) {
                    response = new ServerResponseChats(message.getType(), message.getID(), false, "Invalid Signature");
                    sendResponse(response, null);
                    return;
                }

                int n = 0;
                System.out.println("$Sending all chats for user: " + data.getUser());
                for (Chat i : chats) {
                    if (i.userIn(data.getUser())) {
                        i.setKey(users.get(userIndex).getKey(n));
                        response.addChat(i);
                        n++;
                    }
                }

            } else {
                response = new ServerResponseChats(message.getType(), message.getID(), false,
                        "Invlaid User Requesting Chats");
            }

        } else {
            System.out.println("Invalid Object Type");
        }
        // Send message to Client
        sendResponse(response, clientKey);
    }

    private boolean validate(PublicKey clientKey) {
        return secureMessage.validate(clientKey);
    }

    private int getUserIndex(String u) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).is(u))
                return i;
        }
        return -1;
    }

    public void loadPrivateKey() {
        try {
            KeyStore keyStoreServer = KeyStore.getInstance("PKCS12");
            keyStoreServer.load(new FileInputStream("Resources/server_keystore.p12"), "keyring".toCharArray());
            privateKey = (PrivateKey) keyStoreServer.getKey("serverkeypair", "keyring".toCharArray());
            System.out.println("Private Key:\n" + Base64.getEncoder().encodeToString(privateKey.getEncoded()));
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException
                | UnrecoverableKeyException e) {
            System.out.println("Unable to load Keys");
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void close() {
        System.exit(0);
    }

    public static void main(String args[]) {
        Server server = new Server(5000);
        server.close();
    }
}
