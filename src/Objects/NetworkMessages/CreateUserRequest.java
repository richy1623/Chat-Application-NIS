package Objects.NetworkMessages;

public class CreateUserRequest extends NetworkMessage{
    private String username;
    private String password;
    public CreateUserRequest(int messageType, int id, String username, String password){
        super(messageType, id);
        this.password = password;
        this.username = username;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }
}
