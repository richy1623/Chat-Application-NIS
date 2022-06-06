package Objects.NetworkMessages;

import java.security.PublicKey;

public class ServerResponseLogin extends ServerResponse{
    private byte[] privateKey;
    private PublicKey publicKey;

    public ServerResponseLogin(int type,int id, boolean success, String message){
        super(type, id, success, message);
    }

    public void addPrivateKey(byte[] key){
        privateKey = key;
    }
    public void addPublicKey(PublicKey key){
        publicKey = key;
    }

    public byte[] getPrivateKey(){
        return privateKey;
    }
    public PublicKey getPublicKey(){
        return publicKey;
    }
    
}
