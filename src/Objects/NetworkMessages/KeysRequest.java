package Objects.NetworkMessages;

public class KeysRequest extends NetworkMessage{
    private String[] users;
    public KeysRequest(String[] users){
        super(7, 0);
        this.users = users;
    }

    public String[] getUsers(){
        return users;
    }
}
