package Objects.NetworkMessages;

public class KeysRequest extends NetworkMessage{
    private String user;
    public KeysRequest(String user){
        super(7, 0);
        this.user = user;
    }

    public String getUser(){
        return user;
    }
}
