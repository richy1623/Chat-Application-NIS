package Objects;

public class User {
    private String username;
    private String password;


    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public boolean authenticate(String u, String p){
        return username.equals(u) && password.equals(p);
    }
}
