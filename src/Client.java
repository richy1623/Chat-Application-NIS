import java.net.*;
import java.io.*;
 
import Objects.NetworkMessages.CreateUserRequest;
import Objects.NetworkMessages.NetworkMessage;
import Objects.NetworkMessages.ServerResponse;

public class Client {
    private static final String hostname = "localhost";
    private static final int port = 5000;
    private static int messageID = 0;

    public static void main(String[] args) {
        sendNetworkMessage("ping");
    }

    // Test method to ping the server and receive a ping back
    // Server will recieve single messages from client, then restart the connection
    // (Call this method again)
    public static String sendNetworkMessage(String message) {
        try (Socket socket = new Socket(hostname, port)) {
            // Increment the messageID for every server interaction.
            ++messageID;

            OutputStream output = socket.getOutputStream();
            ObjectOutputStream objectOutput = new ObjectOutputStream(output);
            
            CreateUserRequest message2 = new CreateUserRequest(1, 0, "Test", "Test");
            objectOutput.writeObject(message2);

            InputStream input = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(input);
            String messagerec;
            try {
                messagerec = ((ServerResponse) objectInputStream.readObject()).getMessage();
            }catch (Exception e){
                messagerec = "Error";
            }

            //String serverResponse = reader.readLine();
            String serverResponse = messagerec;

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
}