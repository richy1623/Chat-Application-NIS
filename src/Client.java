import java.net.*;
import java.io.*;
 
import Objects.NetworkMessages.CreateUserRequest;
import Objects.NetworkMessages.NetworkMessage;
import Objects.NetworkMessages.ServerResponse;

public class Client {
    private static final String hostname = "localhost";
    private static final int port = 5000;
    public static void main(String[] args) {
        sendNetworkMessage("ping");
    }

    //Test method to ping the server and receive a ping back
    //All messages are converted to a Network Message to send across the network and sent as objects
    //Server will recieve single messages from client, then restart the connection (Call this method again)
    public static String sendNetworkMessage(String message){
        try (Socket socket = new Socket(hostname, port)) {
 
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

            System.out.println(messagerec);
 
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