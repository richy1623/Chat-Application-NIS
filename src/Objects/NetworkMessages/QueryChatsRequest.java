package Objects.NetworkMessages;

public class QueryChatsRequest extends NetworkMessage{
    private String user;

    public QueryChatsRequest(String user){
        super(3, -1);
        this.user = user;
    }

    public String getUser(){
        return user;
    }
}
