package Objects;

public class User {
    private String username;
    private String password;


    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public boolean is(String u){
        return username.equals(u);
    }

    public boolean authenticate(String u, String p){
        return username.equals(u) && password.equals(p);
    }
}
