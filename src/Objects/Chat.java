package Objects;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import Objects.NetworkMessages.Encryption;

public class Chat implements Serializable {
    private String[] users;
    private boolean initialized;
    private ArrayList<Message> messages;
    private byte[] key;

    public Chat(String user, String[] users) {
        this.users = new String[users.length + 1];
        this.users[0] = user;
        for (int i = 0; i < users.length; i++) {
            this.users[i + 1] = users[i];
        }
        this.messages = new ArrayList<Message>();
        key = null;
        initialized = false;
    }

    public boolean is(String user, String[] users) {
        if (users.length + 1 != this.users.length)
            return false;
        boolean found = false;
        for (String i : this.users) {
            if (i.equals(user))
                found = true;
        }
        if (!found)
            return false;
        for (String i : users) {
            found = false;
            for (String j : this.users) {
                if (i.equals(j))
                    found = true;
            }
            if (!found)
                return false;
        }
        return true;
    }

    public boolean is(String chatName) {
        return chatName.equals(getChatName());
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public boolean userIn(String user) {
        for (String i : users) {
            if (i.equals(user))
                return true;
        }
        return false;
    }

    public void printu() {
        System.out.println("Printing Chat Users");
        for (String i : users) {
            System.out.println(i);
        }
    }

    public void printm() {
        System.out.println("Printing Chat Messages in chat: " + String.join("_", users));
        for (Message i : messages) {
            System.out.println("-" + i.getFrom() + ": " + i.getContent());
        }
    }

    public String[] getUsers() {
        return users;
    }

    public String getReceivers(String currentUser) {
        ArrayList<String> users = new ArrayList<String>(Arrays.asList(this.users));
        users.remove(currentUser);
        return users.toString();
    }

    public boolean isGroupChat() {
        if (users.length > 2) {
            return true;
        }

        return false;
    }

    public String getChatName() {
        return String.join("_", users);
    }

    public Message[] getMessagesFrom(int n) {
        if (n > messages.size())
            return null;
        Message[] out = new Message[messages.size() - n];
        for (int i = n; i < messages.size(); i++) {
            out[i - n] = messages.get(i);
        }
        return out;
    }

    public void initialize() {
        initialized = true;
    }

    public boolean initialized() {
        return initialized;
    }

    public void addChatToUsers(ArrayList<User> users, byte[][] keys) {
        for (int i = 0; i < users.size(); i++) {
            if (userIn(users.get(i).getUsername())) {
                for (int j = 0; j < this.users.length; j++) {
                    if (this.users[j].equals(users.get(i).getUsername())) {
                        users.get(i).addChat(getChatName(), keys[j]);
                    }
                }
            }
        }
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public byte[] getKey() {
        return key;
    }

    public void decrypt(SecretKey key) {
        for (int i = 0; i < messages.size(); i++) {
            try {
                messages.get(i).setContent(new String(
                        Encryption.decryptionAES(Base64.getDecoder().decode(messages.get(i).getContent()), key)));
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                    | BadPaddingException e) {
                e.printStackTrace();
            }
        }
    }
}
