package Objects;

import java.util.ArrayList;

public class Chat {
    private String[] users;
    private ArrayList<Message> messages;

    public Chat(String user, String[] users){
        this.users = new String[users.length+1];
        for (int i=0;i<users.length;i++){
            this.users[i]=users[i];
        }
        this.users[users.length]=user;
        this.messages = new ArrayList<Message>();
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
}
