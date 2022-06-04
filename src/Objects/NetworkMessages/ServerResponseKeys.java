package Objects.NetworkMessages;

import java.security.PublicKey;
import java.util.ArrayList;

public class ServerResponseKeys extends ServerResponse{
    private ArrayList<PublicKey> keys;
    private ArrayList<String> users;
    public ServerResponseKeys(int type, int id, boolean success, String message){
        super(type, id, success, message);
        keys = new ArrayList<PublicKey>();
        users = new ArrayList<String>();
    }

    public ArrayList<PublicKey> getKeys(){
        return keys;
    }
    public ArrayList<String> getUsers(){
        return users;
    }

    public void addEntity(PublicKey key, String user){
        keys.add(key);
        users.add(user);
    }
}
