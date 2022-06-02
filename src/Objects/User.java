package Objects;

import java.security.PublicKey;
import java.util.ArrayList;

import Objects.NetworkMessages.ServerResponse;

public class User {
    private String username;
    private String password;
    private ArrayList<ServerResponse> responses;
    private ArrayList<Message> messages;
    private ArrayList<String> chats;
    private ArrayList<byte[]> chatKeys; 
    private byte[] privateKey;
    private PublicKey publicKey;

    public User(String username, String password, byte[] privateKey, PublicKey publicKey){
        this.username = username;
        this.password = password;
        responses = new ArrayList<ServerResponse>();
        messages = new ArrayList<Message>();
        chats = new ArrayList<String>();
        chatKeys = new ArrayList<byte[]>();
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public boolean is(String u){
        return username.equals(u);
    }

    public boolean authenticate(String u, String p){
        return username.equals(u) && password.equals(p);
    }

    public void resetRequests(){
        responses = new ArrayList<ServerResponse>();
    }

    public ServerResponse getPriorRequest(int x){
        for (ServerResponse i:responses){
            if (i.getID()==x) return i;
        }
        return null;
    }

    public void addRequest(ServerResponse x){
        responses.add(x);
    }

    public void addMessage(Message message){
        messages.add(message);
    }

    public byte[] getPrivateKey(){
        return privateKey;
    }
    public PublicKey getPublicKey(){
        return publicKey;
    }
    public void addChat(String chat, byte[] key){
        chats.add(chat);
        chatKeys.add(key);
    }
    public String getUsername(){
        return username;
    }
    public byte[] getKey(int n){
        return chatKeys.get(n);
    }

}
