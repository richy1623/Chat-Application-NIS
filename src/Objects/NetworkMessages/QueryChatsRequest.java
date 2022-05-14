package Objects.NetworkMessages;

public class QueryChatsRequest extends NetworkMessage{
    private String user;

    public QueryChatsRequest(int type, int id, String user){
        super(type, id);
        this.user = user;
    }

    public String getUser(){
        return user;
    }
}
