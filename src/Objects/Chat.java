package Objects;

import java.io.Serializable;
import java.util.ArrayList;

public class Chat implements Serializable{
    private String[] users;
    private boolean initialized;
    private ArrayList<Message> messages;

    public Chat(String user, String[] users){
        this.users = new String[users.length+1];
        this.users[0]=user;
        for (int i=0;i<users.length;i++){
            this.users[i+1]=users[i];
        }
        this.messages = new ArrayList<Message>();
        initialized = false;
    }

    public boolean is(String user, String[] users){
        if (users.length+1!=this.users.length) return false;
        boolean found = false;
        for (String i: this.users){
            if (i.equals(user)) found=true;
        }
        if (!found) return false;
        for (String i: users){
            found = false;
            for (String j: this.users){
                if (i.equals(j)) found=true;
            }
            if (!found) return false;
        }
        return true;
    }

    public boolean is(String chatName){
        return chatName.equals(getChatName());
    }

    public void addMessage(Message message){
        messages.add(message);
    }

    public boolean userIn(String user){
        for (String i: users){
            if (i.equals(user)) return true;
        }
        return false;
    }

    public void printu(){
        System.out.println("Printing Chat Users");
        for (String i:users){
            System.out.println(i);
        }
    }

    public void printm(){
        System.out.println("Printing Chat Messages in chat: "+String.join("_", users));
        for (Message i:messages){
            System.out.println("-"+i.getFrom()+": "+i.getContent());
        }
    }

    public String[] getUsers(){
        return users;
    }

    public String getChatName(){
        return String.join("_", users);
    }

    public Message[] getMessagesFrom(int n){
        if (n>messages.size()) return null;
        Message[] out = new Message[messages.size()-n];
        for (int i = n;i<messages.size();i++){
            out[i-n]=messages.get(i);
        }
        return out;
    }

    public void initialize(){
        initialized = true;
    }

    public boolean initialized(){
        return initialized;
    }
}
