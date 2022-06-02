package Objects.NetworkMessages;

import java.security.PublicKey;

public class CreateUserRequest extends NetworkMessage{
    private String username;
    private String password;
    private byte[] key;
    private PublicKey publicKey;

    public CreateUserRequest(String username, String password, byte[] key, PublicKey publicKey){
        super(1, 0);
        this.username = username;
        this.password = password;
        this.key = key;
        this.publicKey = publicKey;
    }
    //Temp method
    public CreateUserRequest(String username, String password){
        super(1, 0);
        this.username = username;
        this.password = password;
        this.key = null;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public byte[] getKey(){
        return key;
    }

    public PublicKey getPublicKey(){
        return publicKey;
    }
}
