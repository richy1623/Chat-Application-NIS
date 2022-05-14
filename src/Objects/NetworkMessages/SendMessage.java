package Objects.NetworkMessages;

import Objects.Message;

public class SendMessage extends NetworkMessage{
    private String from;
    private String[] to;
    private Message message;
    public SendMessage(int id, String from, String[] to, String message){
        super(6, id);
        this.from = from;
        this.to = to;
        this.message = new Message(from, message);
    }

    public String from(){
        return from;
    }

    public String[] to(){
        return to;
    }

    public Message getMessage(){
        return message;
    }
}
