public class TestClass {
    public static void main(String[]args){
        try {
            //Create 3 Users
            GUIClient Alice = new GUIClient();
            Alice.createNewUser("Alice", "Alice#1");
            Alice.requestLogin("Alice", "Alice#1");
            GUIClient Bob = new GUIClient();
            Bob.createNewUser("Bob", "Bob#2");
            Bob.requestLogin("Bob", "Bob#2");
            GUIClient Eve = new GUIClient();
            Eve.createNewUser("Eve", "Eve#3");
            Eve.requestLogin("Eve", "Eve#3");
            //Alice Creates a chat
            Alice.queryUsers("Alice");
            Alice.chatRequest("Alice", new String[]{"Bob","Eve"});
            Alice.incrementMessageID();
            //Alice Sends a Message to the group
            Alice.queryChats("Alice");
            Alice.sendMessage("Alice", new String[]{"Bob","Eve"}, "Hello Bob and Eve");
            Alice.incrementMessageID();
            Alice.sendMessage("Alice", new String[]{"Bob","Eve"}, "How are you doing?");
            Alice.incrementMessageID();
            //Bob Responds
            Bob.queryUsers("Bob");
            Bob.queryChats("Bob");
            Bob.sendMessage("Bob", new String[]{"Alice","Eve"}, "Im good thanks");
            Bob.incrementMessageID();
            //Eve Responds
            Eve.queryUsers("Eve");
            Eve.queryChats("Eve");
            Eve.sendMessage("Eve", new String[]{"Alice","Bob"}, "Im good too");
            Eve.incrementMessageID();
            //Alice Checks all messages
            Alice.queryChats("Alice");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /* 
    toServer(new CreateUserRequest("a", "test"));
    toServer(new CreateUserRequest("b", "test"));
    toServer(new CreateUserRequest("c", "test"));
    toServer(new CreateUserRequest("d", "test"));
    toServer(new CreateUserRequest("e", "test"));
    // toServer(new LoginRequest("a", "test"));
    byte[][] testKey = { { 10 }, { 10 }, { 10 }, { 10 }, { 10 }, { 10 } };
    toServer(new CreateChatRequest(1, "a", new String[] { "b", "c" }, testKey));
    toServer(new CreateChatRequest(1, "b", new String[] { "a" }, testKey));
    toServer(new SendMessage(2, "a", new String[] { "b", "c" }, "Hello There"));
    toServer(new SendMessage(2, "b", new String[] { "a", "c" }, "Hey There"));
    toServer(new SendMessage(3, "a", new String[] { "b", "c" }, "Hi"));
    toServer(new SendMessage(3, "b", new String[] { "a" }, "Personal Message"));
    */
}
