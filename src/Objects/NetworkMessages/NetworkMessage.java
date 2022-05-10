package Objects.NetworkMessages;

import java.io.Serializable;

public class NetworkMessage implements Serializable{
    private int messageType;
    private int id;

    public NetworkMessage(int messageType, int id){
        this.messageType = messageType;
        this.id = id;
    }

    public int getType(){
        return messageType;
    }

    public String getContent(){
        return "Temp";
    }

    public int getID(){
        return id;
    }
}
