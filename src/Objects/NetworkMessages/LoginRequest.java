package Objects.NetworkMessages;

public class LoginRequest extends NetworkMessage{
    private String username;
    private String password;
    public LoginRequest(String username, String password){
        super(2, 0);
        this.username = username;
        this.password = password;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }
}
