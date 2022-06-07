package Objects.NetworkMessages;

public class CreateChatRequest extends NetworkMessage{
    private String from;
    private String[] with;
    private byte[][] keys;


    /**
    * Request to the server to create a chat with another client/group
    *
    * @param  id  the message id number
    * @param  from a String of the user trying to create the chat
    * @param with a string[] of all of the users to be in the chat besides the creator
    */


    // BELOW CONSTRUCTOR TO BE DEPRECATED. JUST USING FOR TESTING PURPOSES
    public CreateChatRequest(int id, String from, String[] with){
        super(5, id);
        this.from = from;
        this.with = with;
    }
    // *************************************************************************************************************************************


    public CreateChatRequest(int id, String from, String[] with, byte[][] keys){
        super(5, id);
        this.from = from;
        this.with = with;
        this.keys = keys;
    }


    public String from(){
        return from;
    }

    public String[] with(){
        return with;
    }

    public byte[][] getKeys(){
        return keys;
    }
}
