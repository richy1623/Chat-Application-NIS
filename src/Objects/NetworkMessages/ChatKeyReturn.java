package Objects.NetworkMessages;

public class ChatKeyReturn extends NetworkMessage{
    private String[] users;
    private String[] keys;
    public ChatKeyReturn(int id, String[] users, String[] keys){
        super(4, id);
        this.users = users;
        this.keys = keys;
    }
    public String[] getKeys(){
        return keys;
    }
    public String[] getUsers(){
        return users;
    }
}
