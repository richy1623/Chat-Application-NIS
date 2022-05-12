package Objects;

public class NetworkMessage {
    private int messageID;
    private Message msg;

    public NetworkMessage(int messageID, Message msg) {
        this.messageID = messageID;
        this.msg = msg;
    }
}
