package Objects;

public class Group {
    private String name;
    private int groupId;
    private User[] usersList;
    
    public Group(String name, int groupId, User[] usersList )
    {   this.name = name;
        this.groupId = groupId;
        this.usersList = usersList; 

    }

    public String getName(){
        return name; 
    }

    public int getGroupId(){
        return groupId;
    }

    public User[] getUsersList(){
        return usersList;
    }


    
}
