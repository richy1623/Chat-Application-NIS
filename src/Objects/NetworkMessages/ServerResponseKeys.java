package Objects.NetworkMessages;

import java.security.PublicKey;
import java.util.ArrayList;

public class ServerResponseKeys extends ServerResponse{
    private ArrayList<PublicKey> keys;
    public ServerResponseKeys(int type, int id, boolean success, String message){
        super(type, id, success, message);
        keys = new ArrayList<PublicKey>();
    }

    public ArrayList<PublicKey> getChats(){
        return keys;
    }

    public void addKey(PublicKey key){
        keys.add(key);
    }
}
