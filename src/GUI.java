import javax.imageio.ImageIO;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.awt.image.BufferedImage;
import java.awt.Image;

import java.awt.*;
import javax.swing.*;

import Objects.*;

public class GUI extends JFrame implements MouseListener {

    // GLOBALS 
    // client
    GUIClient client;

    // chats
    ArrayList<GUIChat> chats;
    Collection<JPanel> chatIcons;

    // sizes
    private int WINDOW_W = 900;
    private int WINDOW_H = 500;

    private int WINDOW_W_CHAT = 1200;
    private int WINDOW_H_CHAT = 700;

    // color schemes and themes
    private Color orange;
    private Color white;
    private Color light_grey;
    private Color grey;
    private Color dark_grey;
    private Font defFont;
    private Font defFontLarge;

    // volatile/usable objects
    String currentUser;
    JPanel loginPanel;
    JPanel signUpPanel;
    JLabel loginLabel;
    JLabel signUpLabel;
    JPanel rightMainPanel;
    JButton contButtonLogin;
    JButton contButtonSignUp;
    JPasswordField passTextFieldLogin;
    JTextField usernameTextFieldLogin;
    JTextField usernameTextFieldSignUp;
    JPasswordField passTextFieldSignUp1;
    JPasswordField passTextFieldSignUp2;
    JLabel errorLabel;
    JPanel addChatPanel;
    JButton newChatConfirm;
    JPanel addGroupPanel;
    JButton addUser;

    public static void main(String args[]) throws InterruptedException {
        System.out.println("Starting");

        // Create GUI
        GUI frontend = new GUI();


    }

    public GUI() throws InterruptedException {

        // Create Client Thread
        client = new GUIClient();  // call all its methods in a thread (?)
        Thread thread = new Thread(client);

        // Chat ArrayList setup
        this.chats = new ArrayList<GUIChat>();

        // Color and theme setup
        orange = new Color(255, 160, 0);
        white = new Color(255, 255, 255);
        light_grey = new Color(109, 109, 109);
        grey = new Color(66, 66, 66);
        dark_grey = new Color(27, 27, 27);

        defFont = new Font("Ubuntu", Font.PLAIN, 20);
        defFontLarge = new Font("Ubuntu", Font.BOLD, 24);


        // Error message setup
        errorLabel = new JLabel("Error");
        errorLabel.setFont(new Font("Ubuntu", Font.PLAIN, 15));
        errorLabel.setForeground(new Color(255, 30, 0));


        // Basic window setup
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(WINDOW_W, WINDOW_H);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        // Login View called
        createUILogin();
        //createUIChat();

        // Set visible last (Java)
        this.pack();
        this.setVisible(true);

    }

    // UI Methods

    public void setError(int i) {
        switch( i ) {
            case 1:
                errorLabel.setText("");
                break;
            case 2:
                errorLabel.setText("Please fill all input fields above.");
                break;
            case 3:
                errorLabel.setText("Passwords don't match.");
                break;
            case 4:
                errorLabel.setText("Incorrect login details.");
                break;
            case 5:
                errorLabel.setText("Error creating a user, username already exists");
                break;
        }
    }

    public JPanel generateFiller() {
        JPanel fill = new JPanel();
        fill.setOpaque(false);

        return fill;
    }

    public JPanel generateFiller(int preferredW, int preferredH) {
        JPanel fill = new JPanel();
        fill.setOpaque(false);
        fill.setPreferredSize(new Dimension(preferredW, preferredH));

        return fill;
    }

    public JPanel generatePanelHolder(JLabel label) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.add(label);

        return panel;
    } 

    public JPanel generatePanelHolder(JTextField field, int width, int height) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(width, height));
        panel.setBackground(dark_grey);
        //panel.setOpaque(false);
        panel.add(field);

        return panel;
    } 

    public void clearRightPanel() {
        rightMainPanel.removeAll();
        rightMainPanel.revalidate();
        rightMainPanel.repaint();
    }

    public void clearJFrame() {
        this.getContentPane().removeAll();
        this.getContentPane().revalidate();
        this.getContentPane().repaint();
    }

    private void loadChats() {
        // TODO
        // testing for now
        GUIChat test = new GUIChat("me", "you", false, null);
        this.chats.add(test);

        // Separate collection full of the GUI chat icons (easier to work with)
        this.chatIcons = chats.stream().map(GUIChat::getGUIcon).collect(Collectors.toCollection(ArrayList::new));
    }


    // UI Creation

    public void createUIChat() {

                loadChats();

                this.setSize(WINDOW_W_CHAT, WINDOW_H_CHAT);
                this.setLocationRelativeTo(null);


                // Layout manager for entire frame
                setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

                // Left Option Panel
                JPanel leftMainPanel = new JPanel();
                leftMainPanel.setPreferredSize(new Dimension(350, WINDOW_H_CHAT));
                leftMainPanel.setBackground(new Color(97, 97, 97));
        
                // Right Action Panel
                rightMainPanel = new JPanel();
                rightMainPanel.setPreferredSize(new Dimension(WINDOW_W_CHAT-350, WINDOW_H_CHAT));
                rightMainPanel.setBackground(dark_grey);
        
                    // Left Option Panel Populating:
        
                        // Left Option Panel Layout
                        leftMainPanel.setLayout(new BoxLayout(leftMainPanel, BoxLayout.Y_AXIS));
                        
                        // Left Option Panel Content
                        BufferedImage img = null;
                        try {
                            img = ImageIO.read(new File("Resources/Logo.png"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
        
                        Image dimg = img.getScaledInstance(350, 112, Image.SCALE_SMOOTH);
                        ImageIcon logo = new ImageIcon(dimg);
        
                        JLabel logoLabel = new JLabel();
                        logoLabel.setIcon(logo);

                        JPanel logoPanel = new JPanel();
                        logoPanel.setBackground(new Color(97, 97, 97));
                        logoPanel.setPreferredSize(new Dimension(350, 122));

                        logoPanel.add(logoLabel);

            
                        

                        JPanel navPanel = new JPanel();
                        navPanel.setBackground(grey);
                        navPanel.setPreferredSize(new Dimension(350, WINDOW_H_CHAT));
                        navPanel.setBackground(orange);

                            // Adding to navPanel
                            navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));

                            JPanel addPanel = new JPanel();
                            addPanel.setPreferredSize(new Dimension(350, 100));
                            addPanel.setBackground(grey);

                                // Adding to addPanel
                                addPanel.setLayout(new GridLayout(1, 2));

                                addChatPanel = new JPanel();
                                addChatPanel.setLayout(new GridBagLayout());
                                addChatPanel.setBackground(grey);
                                addChatPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                                addChatPanel.addMouseListener(this);
                                addChatPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                                JLabel addChatLabel = new JLabel();
                                addChatLabel.setText(" + Chat");
                                addChatLabel.setFont(defFont);
                                addChatLabel.setForeground(white);

                                addChatPanel.add(addChatLabel);

                                addGroupPanel = new JPanel();
                                addGroupPanel.setLayout(new GridBagLayout());
                                addGroupPanel.setBackground(grey);
                                addGroupPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                                addGroupPanel.addMouseListener(this);
                                addGroupPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                                JLabel addGroupLabel = new JLabel();
                                addGroupLabel.setText(" + Group");
                                addGroupLabel.setFont(defFont);
                                addGroupLabel.setForeground(white);

                                addGroupPanel.add(addGroupLabel);



                                addPanel.add(addChatPanel);
                                addPanel.add(addGroupPanel);



                            JPanel chatPanel = new JPanel();
                            chatPanel.setPreferredSize(new Dimension(350, WINDOW_H_CHAT));
                            chatPanel.setBackground(grey);


                            // Adding all chats to the left panel

                            for(int i = 0; i < this.chats.size(); i++) {
                                this.chats.get(i).getGUIcon().addMouseListener(this);
                                this.chats.get(i).getGUIcon().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                                chatPanel.add(this.chats.get(i).getGUIcon());
                            }


                            JScrollPane chatScrollable = new JScrollPane(chatPanel);
                            chatScrollable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  

                            
                            navPanel.add(addPanel);
                            navPanel.add(chatScrollable);
                      


                        // Adding to left
                        leftMainPanel.add(logoPanel);
                        leftMainPanel.add(navPanel);
        
                        
        
                // Add both main panels
                this.add(leftMainPanel);
                this.add(rightMainPanel);
    }



    public void createUILogin() {

        // Layout manager for entire frame
        setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

        // Left Option Panel
        JPanel leftMainPanel = new JPanel();
        leftMainPanel.setPreferredSize(new Dimension(250, WINDOW_H));
        leftMainPanel.setBackground(grey);

        // Right Action Panel
        rightMainPanel = new JPanel();
        rightMainPanel.setPreferredSize(new Dimension(WINDOW_W-250, WINDOW_H));
        rightMainPanel.setBackground(dark_grey);

            // Left Option Panel Populating:

                // Left Option Panel Layout
                leftMainPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 10));
                
                // Left Option Panel Content
                BufferedImage img = null;
                try {
                    img = ImageIO.read(new File("Resources/Logo.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Image dimg = img.getScaledInstance(250, 80, Image.SCALE_SMOOTH);
                ImageIcon logo = new ImageIcon(dimg);

                JLabel logoLabel = new JLabel();
                logoLabel.setIcon(logo);

                JPanel logoPanel = new JPanel();
                logoPanel.setPreferredSize(new Dimension(250, 80));
                logoPanel.setBackground(grey);
                logoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

                logoPanel.add(logoLabel);

                loginPanel = new JPanel();
                loginPanel.setPreferredSize(new Dimension(250, 60));
                loginPanel.setBackground(dark_grey);
                loginPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                loginPanel.addMouseListener(this);

                signUpPanel = new JPanel();
                signUpPanel.setPreferredSize(new Dimension(250, 60));
                signUpPanel.setBackground(light_grey);
                signUpPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                signUpPanel.addMouseListener(this);

                // Left Option Panel Panel Populating:

                    // Logo

                    // Login Panel
                    loginPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 19));
                    loginLabel = new JLabel();
                    loginLabel.setText("Login");
                    loginLabel.setFont(new Font("Ubuntu", Font.BOLD, 20));
                    loginLabel.setForeground(orange);

                    loginPanel.add(loginLabel);

                    // Sign Up Panel
                    signUpPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 19));
                    signUpLabel = new JLabel();
                    signUpLabel.setText("Sign Up");
                    signUpLabel.setFont(new Font("Ubuntu", Font.PLAIN, 20));
                    signUpLabel.setForeground(white);

                    signUpPanel.add(signUpLabel);


                // Adding to left panel
                leftMainPanel.add(logoPanel);
                leftMainPanel.add(loginPanel);
                leftMainPanel.add(signUpPanel);

        // Add both main panels
        this.add(leftMainPanel);
        this.add(rightMainPanel);

        // By default, the login view is shown on startup:
        createLoginWindow();
    }


    /**
     * Populates the right panel with login data.
     */
    public void createLoginWindow() {

        setError(1);

        //rightMainPanel.setLayout(new BoxLayout(rightMainPanel, BoxLayout.Y_AXIS));
        rightMainPanel.setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(dark_grey);
        header.setPreferredSize(new Dimension(1, 80));
        header.setLayout(new GridBagLayout());

            // Adding label to header
            JLabel headerLabel = new JLabel("Login");
            headerLabel.setForeground(orange);
            headerLabel.setFont(new Font("Ubuntu", Font.BOLD, 30));

            header.add(headerLabel);

        JPanel inputArea = new JPanel();
        inputArea.setBackground(dark_grey);
        inputArea.setPreferredSize(new Dimension(1, 20));
        inputArea.setLayout(new BoxLayout(inputArea, BoxLayout.Y_AXIS));

            // Adding to inputArea
            JPanel usernamePanel = new JPanel();
            usernamePanel.setBackground(dark_grey);
            usernamePanel.setLayout(new GridLayout(4, 1));

            JPanel filler = new JPanel();
            filler.setBackground(dark_grey);

            JPanel filler2 = new JPanel();
            filler2.setBackground(dark_grey);

            JPanel passwordPanel = new JPanel();
            passwordPanel.setBackground(dark_grey);
            passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.X_AXIS));

                // Adding to password panel
                
                JPanel pFiller1 = new JPanel();
                pFiller1.setBackground(dark_grey);

                JPanel pFiller2 = new JPanel();
                pFiller2.setBackground(dark_grey);

                JPanel passwordPanelCentered = new JPanel();
                passwordPanelCentered.setBackground(dark_grey);

                    // Adding to password panel centered
                    JLabel passLabel = new JLabel("Password: ");
                    passLabel.setFont(new Font("Ubuntu", Font.PLAIN, 15));
                    passLabel.setForeground(white);
                    passTextFieldLogin = new JPasswordField ();
                    passTextFieldLogin.setColumns(15);
                    passTextFieldLogin.setFont(new Font("Ubuntu", Font.PLAIN, 20));

                    passwordPanelCentered.add(passLabel);
                    passwordPanelCentered.add(passTextFieldLogin);

                passwordPanel.add(pFiller1);
                passwordPanel.add(passwordPanelCentered);
                passwordPanel.add(pFiller2);



            JPanel vertCenterUsername = new JPanel();
            vertCenterUsername.setBackground(dark_grey);
            vertCenterUsername.setLayout(new BoxLayout(vertCenterUsername, BoxLayout.X_AXIS));

                // Adding to vertically centered username panel
                JPanel filler3 = new JPanel();
                filler3.setBackground(dark_grey);

                JPanel filler4 = new JPanel();
                filler4.setBackground(dark_grey);

                JPanel horCenterUsername = new JPanel();
                //horCenterUsername.setPreferredSize(new Dimension());
                horCenterUsername.setBackground(dark_grey);

                vertCenterUsername.add(filler3);
                vertCenterUsername.add(horCenterUsername);
                vertCenterUsername.add(filler4);

                    // Adding to horizontally centered username
                    JLabel usernameLabel = new JLabel("Username: ");
                    usernameLabel.setFont(new Font("Ubuntu", Font.PLAIN, 15));
                    usernameLabel.setForeground(white);
                    usernameTextFieldLogin = new JTextField();
                    usernameTextFieldLogin.setColumns(15);
                    usernameTextFieldLogin.setFont(new Font("Ubuntu", Font.PLAIN, 20));

                    horCenterUsername.add(usernameLabel);
                    horCenterUsername.add(usernameTextFieldLogin);
                

            
            usernamePanel.add(filler2);
            usernamePanel.add(vertCenterUsername);
            usernamePanel.add(filler);
            usernamePanel.add(passwordPanel);
            

            JPanel submitPanel = new JPanel();
            submitPanel.setBackground(dark_grey);
            submitPanel.setLayout(new BoxLayout(submitPanel, BoxLayout.X_AXIS));

            contButtonLogin = new JButton("Continue");
            contButtonLogin.addMouseListener(this);
            contButtonLogin.setBackground(light_grey);
            contButtonLogin.setForeground(white);
            contButtonLogin.setFont(new Font("Ubuntu", Font.PLAIN, 20));
            contButtonLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            submitPanel.add(generateFiller());
            submitPanel.add(contButtonLogin);
            submitPanel.add(generateFiller());


            JPanel errorMessagePanel = new JPanel();
            errorMessagePanel.setBackground(dark_grey);
            errorMessagePanel.setLayout(new BoxLayout(errorMessagePanel, BoxLayout.Y_AXIS));

            errorMessagePanel.add(errorLabel);


            inputArea.add(usernamePanel);
            inputArea.add(errorMessagePanel);
            inputArea.add(submitPanel);




        JPanel bottomArea = new JPanel();
        bottomArea.setBackground(dark_grey);
        bottomArea.setPreferredSize(new Dimension(1, 100));



        // Adding to right panel
        rightMainPanel.add(header, BorderLayout.NORTH);
        rightMainPanel.add(inputArea, BorderLayout.CENTER);
        rightMainPanel.add(bottomArea, BorderLayout.SOUTH);

    }



    public void createSignUpWindow() {
        
        setError(1);

        //rightMainPanel.setLayout(new BoxLayout(rightMainPanel, BoxLayout.Y_AXIS));
        rightMainPanel.setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(dark_grey);
        header.setPreferredSize(new Dimension(1, 80));
        header.setLayout(new GridBagLayout());

            // Adding label to header
            JLabel headerLabel = new JLabel("Sign Up");
            headerLabel.setForeground(orange);
            headerLabel.setFont(new Font("Ubuntu", Font.BOLD, 30));

            header.add(headerLabel);

        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(dark_grey);

        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        // 2 username
        JPanel usernamePanel = new JPanel();
        usernamePanel.setBackground(dark_grey);
        usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.X_AXIS));
        usernamePanel.add(generateFiller());

            JPanel actionUserPanel = new JPanel();
            actionUserPanel.setBackground(dark_grey);

                    JLabel usernameLabel = new JLabel("Username: ");
                    usernameLabel.setFont(new Font("Ubuntu", Font.PLAIN, 15));
                    usernameLabel.setForeground(white);
                    usernameTextFieldSignUp = new JTextField();
                    usernameTextFieldSignUp.setColumns(15);
                    usernameTextFieldSignUp.setFont(new Font("Ubuntu", Font.PLAIN, 20));

            actionUserPanel.add(usernameLabel);
            actionUserPanel.add(usernameTextFieldSignUp);

        usernamePanel.add(actionUserPanel);
        usernamePanel.add(generateFiller());

        // 3 pass 
        JPanel passPanel1 = new JPanel();
        passPanel1.setBackground(dark_grey);
        passPanel1.setLayout(new BoxLayout(passPanel1, BoxLayout.X_AXIS));
        passPanel1.add(generateFiller());

            JPanel actionUserPanel2 = new JPanel();
            actionUserPanel2.setBackground(dark_grey);

                    JLabel passLabel = new JLabel("Password: ");
                    passLabel.setFont(new Font("Ubuntu", Font.PLAIN, 15));
                    passLabel.setForeground(white);
                    passTextFieldSignUp1 = new JPasswordField ();
                    passTextFieldSignUp1.setColumns(15);
                    passTextFieldSignUp1.setFont(new Font("Ubuntu", Font.PLAIN, 20));

                actionUserPanel2.add(passLabel);
                actionUserPanel2.add(passTextFieldSignUp1);

            passPanel1.add(actionUserPanel2);
            passPanel1.add(generateFiller());


        // 4 pass check
        JPanel passPanel2 = new JPanel();
        passPanel2.setBackground(dark_grey);
        passPanel2.setLayout(new BoxLayout(passPanel2, BoxLayout.X_AXIS));
        passPanel2.add(generateFiller());

            JPanel actionUserPanel3 = new JPanel();
            actionUserPanel3.setBackground(dark_grey);

                    JLabel passLabel2 = new JLabel("Password (again): ");
                    passLabel2.setFont(new Font("Ubuntu", Font.PLAIN, 15));
                    passLabel2.setForeground(white);
                    passTextFieldSignUp2 = new JPasswordField ();
                    passTextFieldSignUp2.setColumns(15);
                    passTextFieldSignUp2.setFont(new Font("Ubuntu", Font.PLAIN, 20));

                actionUserPanel3.add(passLabel2);
                actionUserPanel3.add(passTextFieldSignUp2);

            passPanel2.add(actionUserPanel3);
            passPanel2.add(generateFiller());

        // 5 errors
        JPanel errorPanel = new JPanel();
        errorPanel.setBackground(dark_grey);
        errorPanel.setLayout(new BoxLayout(errorPanel, BoxLayout.Y_AXIS));

        errorPanel.add(errorLabel);

        // 6 continue
        JPanel contPanel = new JPanel();
        contPanel.setBackground(dark_grey);
        contPanel.setLayout(new BoxLayout(contPanel, BoxLayout.X_AXIS));

        contButtonSignUp = new JButton("Continue");
        contButtonSignUp.addMouseListener(this);
        contButtonSignUp.setBackground(light_grey);
        contButtonSignUp.setForeground(white);
        contButtonSignUp.setFont(new Font("Ubuntu", Font.PLAIN, 20));
        contButtonSignUp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        contPanel.add(generateFiller());
        contPanel.add(contButtonSignUp);
        contPanel.add(generateFiller());

        // Adding to inputPanel
        inputPanel.add(generateFiller());
        inputPanel.add(usernamePanel);
        inputPanel.add(passPanel1);
        inputPanel.add(passPanel2);
        inputPanel.add(errorPanel);
        inputPanel.add(contPanel);
        inputPanel.add(generateFiller());


        rightMainPanel.add(header, BorderLayout.NORTH);
        rightMainPanel.add(inputPanel, BorderLayout.CENTER);

    }



    public void createNewChatWindow() {

        // Add available users to the usersInnerPanel
        
        rightMainPanel.setLayout(new BoxLayout(rightMainPanel, BoxLayout.Y_AXIS));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(dark_grey);
        JLabel titleLabel = new JLabel("Create a new chat");
        titleLabel.setFont(defFontLarge);
        titleLabel.setForeground(orange);

        titlePanel.add(titleLabel);

        JPanel bodyPanel = new JPanel();
        bodyPanel.setPreferredSize(new Dimension(1, WINDOW_H_CHAT));
        bodyPanel.setBackground(dark_grey);

        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));


            JLabel instructionLabel = new JLabel("with..", SwingConstants.CENTER);
            instructionLabel.setFont(defFont);
            instructionLabel.setForeground(white);


            JLabel instructionLabel2 = new JLabel("(please type in a valid username below)", SwingConstants.CENTER);
            instructionLabel2.setFont(defFont);
            instructionLabel2.setForeground(light_grey);


            JTextField newChatUsername = new JTextField();
            newChatUsername.setColumns(20);
            newChatUsername.setFont(defFont);


            JLabel instructionLabel3 = new JLabel("Available users below:", SwingConstants.CENTER);
            instructionLabel3.setFont(defFont);
            instructionLabel3.setForeground(white);


            JPanel usersPanel = new JPanel();
            usersPanel.setBackground(dark_grey);
            usersPanel.setPreferredSize(new Dimension(WINDOW_W_CHAT, 250));
            //usersPanel.setLayout(new BoxLayout(usersPanel, BoxLayout.Y_AXIS));

            JPanel usersInnerPanel = new JPanel();
            usersInnerPanel.setBackground(light_grey);
            usersInnerPanel.setPreferredSize(new Dimension(400, 250));

            JScrollPane usersScrollPane = new JScrollPane(usersInnerPanel);
            usersScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  


            usersPanel.add(usersScrollPane);

            JPanel buttonHolder = new JPanel();
            buttonHolder.setOpaque(false);


            newChatConfirm = new JButton("Create Chat");
            newChatConfirm.addMouseListener(this);
            newChatConfirm.setBackground(light_grey);
            newChatConfirm.setForeground(white);
            newChatConfirm.setFont(defFont);
            newChatConfirm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


            buttonHolder.add(newChatConfirm);
      



        bodyPanel.add(generateFiller(1, 40));
        bodyPanel.add(generatePanelHolder(instructionLabel));
        bodyPanel.add(generateFiller(1, 5));
        bodyPanel.add(generatePanelHolder(instructionLabel2));
        bodyPanel.add(generateFiller(1, 10));
        bodyPanel.add(generatePanelHolder(newChatUsername, 40, 40));
        bodyPanel.add(generateFiller(1, 10));
        bodyPanel.add(generatePanelHolder(instructionLabel3));
        bodyPanel.add(generateFiller(1, 10));
        bodyPanel.add(usersPanel);
        bodyPanel.add(generateFiller(1, 30));
        bodyPanel.add(buttonHolder);
        //bodyPanel.add(generateFiller(1, WINDOW_H_CHAT));


        rightMainPanel.add(generateFiller(1, 20));
        rightMainPanel.add(titlePanel);
        rightMainPanel.add(bodyPanel);

    }



    public void createNewGroupWindow() {

        // Add available users to the usersInnerPanel
        
        rightMainPanel.setLayout(new BoxLayout(rightMainPanel, BoxLayout.Y_AXIS));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(dark_grey);
        JLabel titleLabel = new JLabel("Create a new group");
        titleLabel.setFont(defFontLarge);
        titleLabel.setForeground(orange);

        titlePanel.add(titleLabel);

        JPanel bodyPanel = new JPanel();
        bodyPanel.setPreferredSize(new Dimension(1, WINDOW_H_CHAT));
        bodyPanel.setBackground(dark_grey);

        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));


        JPanel addPanel = new JPanel();
        addPanel.setBackground(light_grey);
        addPanel.setLayout(new BoxLayout(addPanel, BoxLayout.X_AXIS));

            JPanel leftAddPanel = new JPanel();
            leftAddPanel.setBackground(dark_grey);
            leftAddPanel.setLayout(new GridLayout(3, 1));
            //leftAddPanel.setPreferredSize(new Dimension(200, 100));

                JLabel instructionLabel3 = new JLabel("Add User:", SwingConstants.CENTER);
                instructionLabel3.setFont(defFont);
                instructionLabel3.setForeground(white);

                JTextField userToAdd = new JTextField();
                userToAdd.setColumns(20);
                userToAdd.setFont(defFont);

                addUser = new JButton("Add User");
                addUser.addMouseListener(this);
                addUser.setBackground(light_grey);
                addUser.setForeground(white);
                addUser.setFont(defFont);
                addUser.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                JPanel buttonHolder = new JPanel();
                buttonHolder.setOpaque(false);

                buttonHolder.add(addUser);


            leftAddPanel.add(instructionLabel3);
            leftAddPanel.add(generatePanelHolder(userToAdd, 50, 1));
            leftAddPanel.add(buttonHolder);
            

            JPanel rightAddPanel = new JPanel();
            rightAddPanel.setBackground(dark_grey);
            rightAddPanel.setPreferredSize(new Dimension(200, 150));

            JPanel groupUsersInnerPanel = new JPanel();
            groupUsersInnerPanel.setBackground(light_grey);
            groupUsersInnerPanel.setPreferredSize(new Dimension(400, 150));

            JScrollPane groupUsersScrollPane = new JScrollPane(groupUsersInnerPanel);
            groupUsersScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  


            rightAddPanel.add(groupUsersScrollPane);





            addPanel.add(leftAddPanel);
            addPanel.add(rightAddPanel);
      

        JPanel submitPanel = new JPanel();
        submitPanel.setBackground(dark_grey);
        submitPanel.setLayout(new BoxLayout(submitPanel, BoxLayout.Y_AXIS));

            JPanel usersPanel = new JPanel();
            usersPanel.setBackground(dark_grey);
            usersPanel.setPreferredSize(new Dimension(WINDOW_W_CHAT, 150));
            usersPanel.setLayout(new BoxLayout(usersPanel, BoxLayout.Y_AXIS));

            JPanel usersInnerPanel = new JPanel();
            usersInnerPanel.setBackground(light_grey);
            usersInnerPanel.setPreferredSize(new Dimension(400, 150));

            JScrollPane usersScrollPane = new JScrollPane(usersInnerPanel);
            usersScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
            
            JLabel instructionLabel = new JLabel("Available Users Below:", SwingConstants.CENTER);
            instructionLabel.setFont(defFont);
            instructionLabel.setForeground(white);

            usersPanel.add(generateFiller(10, 20));
            usersPanel.add(generatePanelHolder(instructionLabel));
            usersPanel.add(usersScrollPane);

            JPanel buttonHolder2 = new JPanel();
            buttonHolder2.setOpaque(false);


            newChatConfirm = new JButton("Create Chat");
            newChatConfirm.addMouseListener(this);
            newChatConfirm.setBackground(light_grey);
            newChatConfirm.setForeground(white);
            newChatConfirm.setFont(defFont);
            newChatConfirm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


            buttonHolder2.add(newChatConfirm);






        submitPanel.add(usersPanel);
        submitPanel.add(generateFiller(10, 20));
        submitPanel.add(buttonHolder2);


        bodyPanel.add(addPanel);
        bodyPanel.add(submitPanel);


        rightMainPanel.add(generateFiller(1, 20));
        rightMainPanel.add(titlePanel);
        rightMainPanel.add(generateFiller(1, 20));
        rightMainPanel.add(bodyPanel);

    }



    public void createChatWindow() {

        rightMainPanel.setLayout(new BoxLayout(rightMainPanel, BoxLayout.Y_AXIS));

        JPanel dialoguePanel = new JPanel();
        dialoguePanel.setBackground(dark_grey);
        dialoguePanel.setPreferredSize(new Dimension(WINDOW_W_CHAT, WINDOW_H_CHAT));


        JPanel typingPanel = new JPanel();
        typingPanel.setBackground(light_grey);
        typingPanel.setPreferredSize(new Dimension(WINDOW_W_CHAT, 90));
        typingPanel.setLayout(new BoxLayout(typingPanel, BoxLayout.X_AXIS));

            JPanel textPanel = new JPanel();
            textPanel.setBackground(Color.blue);
            textPanel.setPreferredSize(new Dimension(WINDOW_W_CHAT, 90));
            textPanel.setLayout(new BorderLayout());

                JTextField messageField = new JTextField();
                messageField.setColumns(20);
                messageField.setFont(defFont);

                textPanel.add(messageField, BorderLayout.CENTER);

            JPanel sendPanel = new JPanel();
            sendPanel.setBackground(Color.green);
            sendPanel.setPreferredSize(new Dimension(100, 90));
            sendPanel.setLayout(new BorderLayout());

                JButton sendButton = new JButton(">");
                sendButton.addMouseListener(this);
                sendButton.setBackground(light_grey);
                sendButton.setForeground(white);
                sendButton.setFont(new Font("Ubuntu", Font.PLAIN, 20));
                sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                sendPanel.add(sendButton, BorderLayout.CENTER);


            typingPanel.add(textPanel);
            typingPanel.add(sendPanel);


        rightMainPanel.add(dialoguePanel);
        rightMainPanel.add(typingPanel);

    }



    

    // Events

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        if(e.getSource() == loginPanel) {
            
            loginPanel.setBackground(dark_grey);
            loginLabel.setForeground(orange);
            loginLabel.setFont(new Font("Ubuntu", Font.BOLD, 20));

            signUpPanel.setBackground(light_grey);
            signUpLabel.setForeground(white);
            signUpLabel.setFont(new Font("Ubuntu", Font.PLAIN, 20));

            // Call to populate right with login
            clearRightPanel();
            createLoginWindow();


        } else if(e.getSource() == signUpPanel) {

            signUpPanel.setBackground(dark_grey);
            signUpLabel.setForeground(orange);
            signUpLabel.setFont(new Font("Ubuntu", Font.BOLD, 20));

            loginPanel.setBackground(light_grey);
            loginLabel.setForeground(white);
            loginLabel.setFont(new Font("Ubuntu", Font.PLAIN, 20));

            // Call to populate right with sign up
            clearRightPanel();
            createSignUpWindow();


        } else if (e.getSource() == contButtonSignUp) {

            // **** Handles Signing Up ****
            String username = this.usernameTextFieldSignUp.getText();
            String passString = new String(passTextFieldSignUp1.getPassword());
            String passString2 = new String(passTextFieldSignUp2.getPassword());

            if(username.equals("") || passString.length() == 0 || passString2.length() == 0){
                System.out.println("One of the input fields is empty");

                // Show error message
                setError(2);


            } else {
                

                if (passString.equals(passString2)) {

                    System.out.println("Passed, attempting to call Client class now..");
                    
                    // Calling createNewUser from the Client
                    client.setMode(1);
                    client.setSignUpDetails(this.usernameTextFieldSignUp.getText(), passString);
                 
                    Thread thread = new Thread(client);

                    thread.start();

                    try {
                        thread.join();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                    if (client.getServerResponse()) { 
                        // Change GUI currentUser
                        this.currentUser = username;

                        // Take to new user's chat screen
                        this.clearJFrame();
                        this.createUIChat();


                    } else {
                        setError(5);
                    }


                } else {
                    System.out.println("Passwords don't match");

                    // Show error message
                    setError(3);

                }

                String usernameString = this.usernameTextFieldSignUp.getText();

                System.out.println(usernameString);
                System.out.println(passString);

            }

        } else if (e.getSource() == contButtonLogin) {

            // **** Handles Loggin In ****

            System.out.println("log in");

            String username = this.usernameTextFieldLogin.getText();
            
            if(username.equals("") || this.passTextFieldLogin.getPassword().length == 0){
                System.out.println("One of the input fields is empty");

                // Show error message
                setError(2);

            } else {
            
                System.out.println("Passed");
               
                // Needs hashing down the line
                String password = new String(passTextFieldLogin.getPassword());
              
                client.setMode(2);
                client.setLoginDetails(username, password);

                Thread thread = new Thread(client); //TODO, could use old thread?
                thread.start();

                try {
                    thread.join();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                if (client.getServerResponse()) {
    
                    this.currentUser = username;

                    clearJFrame();
                    createUIChat();

                } else {
                    setError(4);
                }


            }
        
        } else if (chatIcons.contains(e.getSource())){
           // ArrayList<JPanel> chatIcons = chats.stream().map(GUIChat::getGUIcon).collect(Collectors.toCollection(ArrayList::new));
            System.out.print("Chat object");
            // pass chat information into the method below in the future

            clearRightPanel();
            createChatWindow();
           
        } else if (e.getSource() == addChatPanel) {
            System.out.println("add chat");
            clearRightPanel();
            createNewChatWindow();

        } else if (e.getSource() == addGroupPanel) {
            System.out.println("add group");
            clearRightPanel();
            createNewGroupWindow();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

}
