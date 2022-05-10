package Objects.NetworkMessages;

public class ServerResponse extends NetworkMessage{
    private boolean success;
    private String message;

    public ServerResponse(int type, int id, boolean success, String message){
        super(type, id);
        this.success = success;
        this.message = message;
    }

    public boolean getSuccess(){
        return success;
    }

    public String getMessage(){
        return message;
    }
}
