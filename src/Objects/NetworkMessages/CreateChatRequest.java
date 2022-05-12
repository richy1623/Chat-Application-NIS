package Objects.NetworkMessages;

public class CreateChatRequest extends NetworkMessage{
    private String from;
    private String[] with;


    /**
    * Request to the server to create a chat with another client/group
    *
    * @param  id  the message id number
    * @param  from a String of the user trying to create the chat
    * @param with a string[] of all of the users to be in the chat besides the creator
    */

    public CreateChatRequest(int id, String from, String[] with){
        super(5, id);
        this.from = from;
        this.with = with;
    }
    public String from(){
        return from;
    }

    public String[] with(){
        return with;
    }
}
