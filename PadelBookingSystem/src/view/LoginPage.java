package view;

import controller.ControllerLogin;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginPage extends JFrame {

    ControllerLogin controller;

    JPanel panel = new JPanel();

    JLabel title = new JLabel("LOGIN ADMIN");
    JLabel subtitle = new JLabel("Sistem Manajemen Transportasi");

    JLabel labelUsername = new JLabel("Username");
    JLabel labelPassword = new JLabel("Password");

    JTextField inputUsername = new JTextField();
    JPasswordField inputPassword = new JPasswordField();

    JButton tombolLogin = new JButton("Login");
    JButton tombolReset = new JButton("Reset");

    public LoginPage() {

        setTitle("Login");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        panel.setLayout(null);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        add(panel);

        // ===== TITLE =====
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBounds(145, 20, 250, 30);

        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setBounds(125, 50, 260, 20);

        // ===== LABEL =====
        labelUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        labelPassword.setFont(new Font("Arial", Font.PLAIN, 14));

        labelUsername.setBounds(50, 100, 120, 20);
        labelPassword.setBounds(50, 170, 120, 20);

        // ===== INPUT =====
        inputUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPassword.setFont(new Font("Arial", Font.PLAIN, 14));

        inputUsername.setBounds(50, 125, 380, 38);
        inputPassword.setBounds(50, 195, 380, 38);

        // ===== BUTTON =====
        tombolReset.setBounds(50, 255, 170, 40);
        tombolLogin.setBounds(260, 255, 170, 40);

        tombolLogin.setBackground(new Color(0, 102, 204));
        tombolLogin.setForeground(Color.WHITE);

        tombolReset.setBackground(new Color(160, 160, 160));
        tombolReset.setForeground(Color.WHITE);

        // ===== ADD COMPONENT =====
        panel.add(title);
        panel.add(subtitle);

        panel.add(labelUsername);
        panel.add(labelPassword);

        panel.add(inputUsername);
        panel.add(inputPassword);

        panel.add(tombolLogin);
        panel.add(tombolReset);

        // ===== CONTROLLER =====
        controller = new ControllerLogin(this);

        // ===== EVENT LOGIN =====
        tombolLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.login();
            }
        });

        // ===== EVENT RESET =====
        tombolReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputUsername.setText("");
                inputPassword.setText("");
            }
        });

        setVisible(true);
        
        
    }

    // ===== GETTER =====
    public String getUsername() {
        return inputUsername.getText();
    }

    public String getPassword() {
        return String.valueOf(inputPassword.getPassword());
    }
    
    /** Aktifkan / nonaktifkan form saat proses login berlangsung */
    public void setFormEnabled(boolean enabled) {
        inputUsername.setEnabled(enabled);
        inputPassword.setEnabled(enabled);
        tombolLogin  .setEnabled(enabled);
        tombolReset  .setEnabled(enabled);
}

/** Tampilkan teks status di bawah tombol (kosongkan dengan "") */
    public void setStatus(String text) {
        tombolLogin.setText(text.isEmpty() ? "Login" : text);
    }
}