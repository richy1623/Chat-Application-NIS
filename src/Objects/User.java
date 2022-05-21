package Objects;

import java.util.ArrayList;

import javax.crypto.SecretKey;

import Objects.NetworkMessages.ServerResponse;

public class User {
    private String username;
    private String password;
    private ArrayList<ServerResponse> responses;
    private ArrayList<Message> messages;
    private ArrayList<String> chats;
    private ArrayList<SecretKey> keys; 

    public User(String username, String password){
        this.username = username;
        this.password = password;
        responses = new ArrayList<ServerResponse>();
        messages = new ArrayList<Message>();
        chats = new ArrayList<String>();
        keys = new ArrayList<SecretKey>();
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

}
