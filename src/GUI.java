import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

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

public class GUI extends JFrame implements MouseListener {

    // Globals (things that change)
    private int WINDOW_W = 900;
    private int WINDOW_H = 500;

    JPanel loginPanel;
    JPanel signUpPanel;
    JLabel loginLabel;
    JLabel signUpLabel;


    public static void main(String args[]) {
        System.out.println("Starting");
        GUI frontend = new GUI();
    }

    public GUI() {

        // Basic setup
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(WINDOW_W, WINDOW_H);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        // Login View called
        createLogin();

        // Set visible last (Java)
        this.setVisible(true);

    }

    public void createLogin() {

        // Layout manager for entire frame
        this.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));

        // Left Option Panel
        JPanel leftMainPanel = new JPanel();
        leftMainPanel.setPreferredSize(new Dimension(250, WINDOW_H));
        leftMainPanel.setBackground(new Color(97, 97, 97));

        // Right Action Panel
        JPanel rightMainPanel = new JPanel();
        rightMainPanel.setPreferredSize(new Dimension(WINDOW_W-250, WINDOW_H));
        rightMainPanel.setBackground(new Color(55, 55, 55));

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
                logoPanel.setBackground(new Color(97, 97, 97));
                logoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

                logoPanel.add(logoLabel);

                loginPanel = new JPanel();
                loginPanel.setPreferredSize(new Dimension(250, 60));
                loginPanel.setBackground(new Color(55, 55, 55));
                loginPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                loginPanel.addMouseListener(this);

                signUpPanel = new JPanel();
                signUpPanel.setPreferredSize(new Dimension(250, 60));
                signUpPanel.setBackground(new Color(142, 142, 142));
                signUpPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                signUpPanel.addMouseListener(this);

                // Left Option Panel Panel Populating:

                    // Logo

                    // Login Panel
                    loginPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 19));
                    loginLabel = new JLabel();
                    loginLabel.setText("Login");
                    loginLabel.setFont(new Font("Ubuntu", Font.BOLD, 20));
                    loginLabel.setForeground(new Color(255, 160, 0));

                    loginPanel.add(loginLabel);

                    // Sign Up Panel
                    signUpPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 19));
                    signUpLabel = new JLabel();
                    signUpLabel.setText("Sign Up");
                    signUpLabel.setFont(new Font("Ubuntu", Font.PLAIN, 20));
                    signUpLabel.setForeground(new Color(255, 255, 255));

                    signUpPanel.add(signUpLabel);


                // Adding to left panel
                leftMainPanel.add(logoPanel);
                leftMainPanel.add(loginPanel);
                leftMainPanel.add(signUpPanel);

        // Add both main panels
        this.add(leftMainPanel);
        this.add(rightMainPanel);
    }







    // Events

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        if(e.getSource() == loginPanel) {
            
            loginPanel.setBackground(new Color(55, 55, 55));
            loginLabel.setForeground(new Color(255, 160, 0));
            loginLabel.setFont(new Font("Ubuntu", Font.BOLD, 20));

            signUpPanel.setBackground(new Color(142, 142, 142));
            signUpLabel.setForeground(new Color(255, 255, 255));
            signUpLabel.setFont(new Font("Ubuntu", Font.PLAIN, 20));

            // Call to populate right with login


        } else if(e.getSource() == signUpPanel) {

            signUpPanel.setBackground(new Color(55, 55, 55));
            signUpLabel.setForeground(new Color(255, 160, 0));
            signUpLabel.setFont(new Font("Ubuntu", Font.BOLD, 20));

            loginPanel.setBackground(new Color(142, 142, 142));
            loginLabel.setForeground(new Color(255, 255, 255));
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