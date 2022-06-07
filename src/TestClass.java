public class TestClass {
    public static void main(String[]args){
        try {
            boolean[] tests = new boolean[13];
            //Create 3 Users
            GUIClient Alice = new GUIClient();
            System.out.println("\n************************** Starting test "+1+" **************************");
            tests[0] = Alice.createNewUser("Alice", "Alice#1");
            System.out.println("************************** Test "+1+": "+(tests[0] ? "passed":"failed")+" **************************\n");

            System.out.println("\n************************** Starting test "+2+" **************************");
            tests[1] = Alice.requestLogin("Alice", "Alice#1");
            System.out.println("************************** Test "+2+": "+(tests[1] ? "passed":"failed")+" **************************\n");

            GUIClient Bob = new GUIClient();
            Bob.createNewUser("Bob", "Bob#2");
            Bob.requestLogin("Bob", "Bob#2");
            GUIClient Eve = new GUIClient();
            Eve.createNewUser("Eve", "Eve#3");
            Eve.requestLogin("Eve", "Eve#3");

            //Fail Tests
            GUIClient Sam = new GUIClient();
            //Try create a user that already exists
            System.out.println("\n************************** Starting test "+3+" **************************");
            tests[2] = !Sam.createNewUser("Alice", "Sam#1");
            System.out.println("************************** Test "+3+": "+(tests[2] ? "passed":"failed")+" **************************\n");

            //Try login with the wrong password
            System.out.println("\n************************** Starting test "+4+" **************************");
            tests[3] = !Sam.requestLogin("Alice", "Sam#1");
            System.out.println("************************** Test "+4+": "+(tests[3] ? "passed":"failed")+" **************************\n");

            Sam.createNewUser("Sam", "Sam#4");
            Sam.requestLogin("Sam", "Sam#4");
            //Try Impersonate Alice without her private key
            System.out.println("\n************************** Starting test "+5+" **************************");
            tests[4] = !Sam.queryChats("Alice");
            System.out.println("************************** Test "+5+": "+(tests[4] ? "passed":"failed")+" **************************\n");

            //Alice Creates a chat
            System.out.println("\n************************** Starting test "+6+" **************************");
            tests[5] = Alice.queryUsers("Alice");
            System.out.println("************************** Test "+6+": "+(tests[5] ? "passed":"failed")+" **************************\n");
            
            System.out.println("\n************************** Starting test "+7+" **************************");
            tests[6] = Alice.chatRequest("Alice", new String[]{"Bob","Eve"});
            Alice.incrementMessageID();
            System.out.println("************************** Test "+7+": "+(tests[6] ? "passed":"failed")+" **************************\n");
            
            //Alice Sends a Message to the group
            System.out.println("\n************************** Starting test "+8+" **************************");
            tests[7] = Alice.queryChats("Alice");
            System.out.println("************************** Test "+8+": "+(tests[7] ? "passed":"failed")+" **************************\n");

            System.out.println("\n************************** Starting test "+9+" **************************");
            tests[8] = Alice.sendMessage("Alice", new String[]{"Bob","Eve"}, "Hello Bob and Eve");
            Alice.incrementMessageID();
            System.out.println("************************** Test "+9+": "+(tests[8] ? "passed":"failed")+" **************************\n");

            Alice.sendMessage("Alice", new String[]{"Bob","Eve"}, "How are you doing?");
            Alice.incrementMessageID();
            //Bob Responds
            Bob.queryUsers("Bob");
            Bob.queryChats("Bob");
            System.out.println("\n************************** Starting test "+10+" **************************");
            tests[9] = Bob.sendMessage("Bob", new String[]{"Alice","Eve"}, "Im good thanks");
            Bob.incrementMessageID();
            System.out.println("************************** Test "+10+": "+(tests[9] ? "passed":"failed")+" **************************\n");
            
            //Eve Responds
            Eve.queryUsers("Eve");
            Eve.queryChats("Eve");
            System.out.println("\n************************** Starting test "+11+" **************************");
            tests[10] = Eve.sendMessage("Eve", new String[]{"Alice","Bob"}, "Im good too");
            Eve.incrementMessageID();
            System.out.println("************************** Test "+11+": "+(tests[10] ? "passed":"failed")+" **************************\n");

            //Alice Checks all messages
            System.out.println("\n************************** Starting test "+12+" **************************");
            tests[11] = Alice.queryChats("Alice");
            System.out.println("************************** Test "+12+": "+(tests[11] ? "passed":"failed")+" **************************\n");

            System.out.println("\n************************** Starting test "+13+" **************************");
            tests[12] = Alice.getLastMessage().equals("Im good too");
            System.out.println("************************** Test "+13+": "+(tests[12] ? "passed":"failed")+" **************************\n");

            
            boolean allPass = true;
            for(int i=0;i<tests.length;i++){
                if (!tests[i]) allPass = false;
            }
            System.out.println("^************************** "+(allPass ? "All tests passed":"Not all tests Passed")+" **************************^");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
