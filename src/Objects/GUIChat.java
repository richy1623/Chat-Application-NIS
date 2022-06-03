package Objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;

public class GUIChat {
    private String sender;
    private String receiver;
    private Boolean groupChat;
    private ArrayList<Message> messages;

    private Chat chat;
    private String currentUser;

    private JPanel GUIcon;

    private Color light_grey;

    // TO BE DEPRICATED
    public GUIChat(String sender, String receiver, Boolean groupChat, ArrayList<Message> messages) {

        light_grey = new Color(109, 109, 109);

        this.sender = sender;
        this.receiver = receiver;
        this.groupChat = groupChat;
        this.messages = messages;

        this.createGUIcon();

    }

    public GUIChat(Chat chat, String currentUser) {

        light_grey = new Color(109, 109, 109);

        this.chat = chat;
        this.currentUser = currentUser;

        this.groupChat = chat.isGroupChat();
        this.messages = new ArrayList<Message>(Arrays.asList(chat.getMessagesFrom(0)));
        this.receiver = chat.getReceivers(this.currentUser);
       
     

        this.createGUIcon();

    }

    
    private void createGUIcon() {

        GUIcon = new JPanel();
        GUIcon.setBackground(light_grey);
        GUIcon.setPreferredSize(new Dimension(350, 100));
        GUIcon.setLayout(new BoxLayout(GUIcon, BoxLayout.X_AXIS));

        JPanel iconPanel = new JPanel();
        iconPanel.setPreferredSize(new DimensionUIResource(100, 100));
        iconPanel.setOpaque(false);

        JLabel iconLabel = new JLabel();
        iconLabel.setFont(new Font("Ubuntu", Font.PLAIN, 20));

            if(this.groupChat == false){
                iconLabel.setText("(Individual)");
            } else {
                iconLabel.setText("(Group)");
            }

        iconPanel.setLayout(new GridBagLayout());
        iconPanel.add(iconLabel);

        JPanel namePanel = new JPanel();
        namePanel.setPreferredSize(new DimensionUIResource(250, 100));
        namePanel.setOpaque(false);

        JLabel nameLabel = new JLabel();
        nameLabel.setFont(new Font("Ubuntu", Font.PLAIN, 20));
        nameLabel.setText(this.receiver);

        namePanel.setLayout(new GridBagLayout());
        namePanel.add(nameLabel);



        GUIcon.add(iconPanel);
        GUIcon.add(namePanel);

    }

    public JPanel getGUIcon() {
        return this.GUIcon;
    }

}
