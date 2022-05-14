package Objects.NetworkMessages;

import java.util.ArrayList;

import Objects.Chat;

public class ServerResponseChats extends ServerResponse{
    private ArrayList<Chat> chats;
    public ServerResponseChats(int type, int id, boolean success, String message){
        super(type, id, success, message);
        chats = new ArrayList<Chat>();
    }

    public ArrayList<Chat> getChats(){
        return chats;
    }

    public void addChat(Chat chat){
        chats.add(chat);
    }
}
