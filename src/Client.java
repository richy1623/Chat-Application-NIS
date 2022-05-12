import java.net.*;
import java.io.*;

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
            PrintWriter writer = new PrintWriter(output, true);

            writer.println(message);

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String serverResponse = reader.readLine();

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
        NetworkMessage msg = new NetworkMessage();
    }

    // Request to login
    public static void requestLogin() {

    }
}