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
import java.awt.image.BufferedImage;
import java.awt.Image;

import java.awt.*;
import javax.swing.*;

public class GUI extends JFrame implements MouseListener {

    // GLOBALS 
    // sizes
    private int WINDOW_W = 900;
    private int WINDOW_H = 500;

    // color schemes
    private Color orange;
    private Color white;
    private Color light_grey;
    private Color grey;
    private Color dark_grey;

    // volatile objects
    JPanel loginPanel;
    JPanel signUpPanel;
    JLabel loginLabel;
    JLabel signUpLabel;
    JPanel rightMainPanel;


    public static void main(String args[]) {
        System.out.println("Starting");
        GUI frontend = new GUI();
    }

    public GUI() {

        // Color setup
        orange = new Color(255, 160, 0);
        white = new Color(255, 255, 255);
        light_grey = new Color(142, 142, 142);
        grey = new Color(97, 97, 97);
        dark_grey = new Color(55, 55, 55);


        // Basic window setup
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(WINDOW_W, WINDOW_H);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        // Login View called
        createUI();

        // Set visible last (Java)
        this.pack();
        this.setVisible(true);

    }

    public void createUI() {

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

        //rightMainPanel.setLayout(new BoxLayout(rightMainPanel, BoxLayout.Y_AXIS));
        rightMainPanel.setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(new Color(255,255,255));
        header.setPreferredSize(new Dimension(1, 80));
        header.setLayout(new GridBagLayout());

            // Adding label to header
            JLabel headerLabel = new JLabel("Login");
            headerLabel.setForeground(orange);
            headerLabel.setFont(new Font("Ubuntu", Font.BOLD, 30));

            header.add(headerLabel);

        JPanel inputArea = new JPanel();
        inputArea.setBackground(orange);
        inputArea.setPreferredSize(new Dimension(1, 20));
        inputArea.setLayout(new BoxLayout(inputArea, BoxLayout.Y_AXIS));

            // Adding to inputArea
            JPanel usernamePanel = new JPanel();
            usernamePanel.setBackground(dark_grey);
            usernamePanel.setLayout(new GridLayout(4, 1));

            JPanel filler = new JPanel();
            filler.setBackground(white);

            JPanel filler2 = new JPanel();
            filler2.setBackground(white);

            JPanel passwordPanel = new JPanel();
            passwordPanel.setBackground(orange);
            passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.X_AXIS));

                // Adding to password panel
                
                JPanel pFiller1 = new JPanel();
                pFiller1.setBackground(white);

                JPanel pFiller2 = new JPanel();
                pFiller2.setBackground(white);

                JPanel passwordPanelCentered = new JPanel();
                passwordPanelCentered.setBackground(new Color(255, 10, 80));

                    // Adding to password panel centered
                    JLabel passLabel = new JLabel("Password: ");
                    passLabel.setFont(new Font("Ubuntu", Font.PLAIN, 15));
                    JTextField passTextField = new JTextField();
                    passTextField.setColumns(15);
                    passTextField.setFont(new Font("Ubuntu", Font.PLAIN, 20));

                    passwordPanelCentered.add(passLabel);
                    passwordPanelCentered.add(passTextField);

                passwordPanel.add(pFiller1);
                passwordPanel.add(passwordPanelCentered);
                passwordPanel.add(pFiller2);



            JPanel vertCenterUsername = new JPanel();
            vertCenterUsername.setBackground(new Color(255,0,0));
            vertCenterUsername.setLayout(new BoxLayout(vertCenterUsername, BoxLayout.X_AXIS));

                // Adding to vertically centered username panel
                JPanel filler3 = new JPanel();
                filler3.setBackground(white);

                JPanel filler4 = new JPanel();
                filler4.setBackground(white);

                JPanel horCenterUsername = new JPanel();
                //horCenterUsername.setPreferredSize(new Dimension());
                horCenterUsername.setBackground(new Color(0, 255, 50));

                vertCenterUsername.add(filler3);
                vertCenterUsername.add(horCenterUsername);
                vertCenterUsername.add(filler4);

                    // Adding to horizontally centered username
                    JLabel usernameLabel = new JLabel("Username: ");
                    usernameLabel.setFont(new Font("Ubuntu", Font.PLAIN, 15));
                    JTextField usernameTextField = new JTextField();
                    usernameTextField.setColumns(15);
                    usernameTextField.setFont(new Font("Ubuntu", Font.PLAIN, 20));

                    horCenterUsername.add(usernameLabel);
                    horCenterUsername.add(usernameTextField);
                

            
            usernamePanel.add(vertCenterUsername);
            usernamePanel.add(filler);
            usernamePanel.add(passwordPanel);
            usernamePanel.add(filler2);
            

            JPanel submitPanel = new JPanel();
            submitPanel.setBackground(grey);


            JPanel errorMessagePanel = new JPanel();
            errorMessagePanel.setBackground(orange);


            inputArea.add(usernamePanel);
            inputArea.add(submitPanel);
            inputArea.add(errorMessagePanel);




        JPanel submitArea = new JPanel();
        submitArea.setBackground(new Color(255,255,10));
        submitArea.setPreferredSize(new Dimension(1, 100));



        // Adding to right panel
        rightMainPanel.add(header, BorderLayout.NORTH);
        rightMainPanel.add(inputArea, BorderLayout.CENTER);
        rightMainPanel.add(submitArea, BorderLayout.SOUTH);

    }



    public void createSignUpWindow() {
        //rightMainPanel.setLayout(new BoxLayout(rightMainPanel, BoxLayout.Y_AXIS));
        rightMainPanel.setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(new Color(255,255,255));
        header.setPreferredSize(new Dimension(1, 80));
        header.setLayout(new GridBagLayout());

            // Adding label to header
            JLabel headerLabel = new JLabel("Login");
            headerLabel.setForeground(orange);
            headerLabel.setFont(new Font("Ubuntu", Font.BOLD, 30));

            header.add(headerLabel);

        JPanel inputArea = new JPanel();
        inputArea.setBackground(orange);
        inputArea.setPreferredSize(new Dimension(1, 20));
        inputArea.setLayout(new BoxLayout(inputArea, BoxLayout.Y_AXIS));

            // Adding to inputArea
            JPanel usernamePanel = new JPanel();
            usernamePanel.setBackground(dark_grey);
            usernamePanel.setLayout(new GridLayout(3, 1));

            JPanel filler = new JPanel();
            filler.setBackground(white);

            JPanel filler2 = new JPanel();
            filler2.setBackground(white);

            JPanel vertCenterUsername = new JPanel();
            vertCenterUsername.setBackground(new Color(255,0,0));
            vertCenterUsername.setLayout(new BoxLayout(vertCenterUsername, BoxLayout.X_AXIS));

                // Adding to vertically centered username panel
                JPanel filler3 = new JPanel();
                filler3.setBackground(white);

                JPanel filler4 = new JPanel();
                filler4.setBackground(white);

                JPanel horCenterUsername = new JPanel();
                //horCenterUsername.setPreferredSize(new Dimension());
                horCenterUsername.setBackground(new Color(0, 255, 50));

                vertCenterUsername.add(filler3);
                vertCenterUsername.add(horCenterUsername);
                vertCenterUsername.add(filler4);

                    // Adding to horizontally centered username
                    JLabel usernameLabel = new JLabel("username");
                    JTextField usernameTextField = new JTextField();
                    usernameTextField.setColumns(15);

                    horCenterUsername.add(usernameLabel);
                    horCenterUsername.add(usernameTextField);
                

            usernamePanel.add(filler);
            usernamePanel.add(vertCenterUsername);
            usernamePanel.add(filler2);

            



            JPanel passwordPanel = new JPanel();
            passwordPanel.setBackground(grey);


            JPanel errorMessagePanel = new JPanel();
            errorMessagePanel.setBackground(orange);


            inputArea.add(usernamePanel);
            inputArea.add(passwordPanel);
            inputArea.add(errorMessagePanel);




        JPanel submitArea = new JPanel();
        submitArea.setBackground(new Color(255,255,10));
        submitArea.setPreferredSize(new Dimension(1, 100));



        // Adding to right panel
        rightMainPanel.add(header, BorderLayout.NORTH);
        rightMainPanel.add(inputArea, BorderLayout.CENTER);
        rightMainPanel.add(submitArea, BorderLayout.SOUTH);
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


        } else if(e.getSource() == signUpPanel) {

            signUpPanel.setBackground(dark_grey);
            signUpLabel.setForeground(orange);
            signUpLabel.setFont(new Font("Ubuntu", Font.BOLD, 20));

            loginPanel.setBackground(light_grey);
            loginLabel.setForeground(white);
            loginLabel.setFont(new Font("Ubuntu", Font.PLAIN, 20));

            // Call to populate right with sign up

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
