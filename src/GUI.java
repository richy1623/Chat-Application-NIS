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
        light_grey = new Color(109, 109, 109);
        grey = new Color(66, 66, 66);
        dark_grey = new Color(27, 27, 27);


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

    public JPanel generateFiller() {
        JPanel fill = new JPanel();
        fill.setOpaque(false);

        return fill;
    }

    public void clearRightPanel() {
        rightMainPanel.removeAll();
        rightMainPanel.revalidate();
        rightMainPanel.repaint();
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
                    JTextField passTextField = new JTextField();
                    passTextField.setColumns(15);
                    passTextField.setFont(new Font("Ubuntu", Font.PLAIN, 20));

                    passwordPanelCentered.add(passLabel);
                    passwordPanelCentered.add(passTextField);

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
                    JTextField usernameTextField = new JTextField();
                    usernameTextField.setColumns(15);
                    usernameTextField.setFont(new Font("Ubuntu", Font.PLAIN, 20));

                    horCenterUsername.add(usernameLabel);
                    horCenterUsername.add(usernameTextField);
                

            
            usernamePanel.add(filler2);
            usernamePanel.add(vertCenterUsername);
            usernamePanel.add(filler);
            usernamePanel.add(passwordPanel);
            

            JPanel submitPanel = new JPanel();
            submitPanel.setBackground(dark_grey);
            submitPanel.setLayout(new BoxLayout(submitPanel, BoxLayout.X_AXIS));

            JButton contButtonLogin = new JButton("Continue");
            contButtonLogin.setBackground(light_grey);
            contButtonLogin.setForeground(white);
            contButtonLogin.setFont(new Font("Ubuntu", Font.PLAIN, 20));
            contButtonLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            submitPanel.add(generateFiller());
            submitPanel.add(contButtonLogin);
            submitPanel.add(generateFiller());


            JPanel errorMessagePanel = new JPanel();
            errorMessagePanel.setBackground(dark_grey);


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
                    JTextField usernameTextField = new JTextField();
                    usernameTextField.setColumns(15);
                    usernameTextField.setFont(new Font("Ubuntu", Font.PLAIN, 20));

            actionUserPanel.add(usernameLabel);
            actionUserPanel.add(usernameTextField);

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
                    JTextField passTextField = new JTextField();
                    passTextField.setColumns(15);
                    passTextField.setFont(new Font("Ubuntu", Font.PLAIN, 20));

                actionUserPanel2.add(passLabel);
                actionUserPanel2.add(passTextField);

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
                    JTextField passTextField2 = new JTextField();
                    passTextField2.setColumns(15);
                    passTextField2.setFont(new Font("Ubuntu", Font.PLAIN, 20));

                actionUserPanel3.add(passLabel2);
                actionUserPanel3.add(passTextField2);

            passPanel2.add(actionUserPanel3);
            passPanel2.add(generateFiller());

        // 5 errors
        JPanel errorPanel = new JPanel();
        errorPanel.setBackground(dark_grey);

        // 6 continue
        JPanel contPanel = new JPanel();
        contPanel.setBackground(dark_grey);
        contPanel.setLayout(new BoxLayout(contPanel, BoxLayout.X_AXIS));

        JButton contButtonSignUp = new JButton("Continue");
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
