package com.form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.util.DB;

public class LogInForm extends JFrame {

    JTextField username = new JTextField(20);
    JPasswordField password = new JPasswordField(20);
    JButton loginBtn = new JButton("LOGIN");
    JButton registerBtn = new JButton("REGISTER");

    public LogInForm() {

        setTitle("Utility Management System - Login");
        setSize(800, 450);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel container = new JPanel(new BorderLayout());
        add(container);

        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 120, 150),
                                                     getWidth(), getHeight(), new Color(255, 180, 200));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        leftPanel.setPreferredSize(new Dimension(350, 0));
        leftPanel.setLayout(new BorderLayout());

        JLabel welcome = new JLabel("WELCOME TO UTMS ", SwingConstants.CENTER);
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("SansSerif", Font.BOLD, 16));

        leftPanel.add(welcome, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel loginTitle = new JLabel("LOGIN", SwingConstants.CENTER);
        loginTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        loginTitle.setForeground(new Color(255, 100, 120));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        rightPanel.add(loginTitle, gbc);

        gbc.gridwidth = 1;

        gbc.gridy++;
        rightPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        rightPanel.add(username, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        rightPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        rightPanel.add(password, gbc);

        loginBtn.setBackground(new Color(255, 110, 130));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);

        registerBtn.setBackground(new Color(100, 100, 100));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);

        gbc.gridx = 0;
        gbc.gridy++;
        rightPanel.add(loginBtn, gbc);

        gbc.gridx = 1;
        rightPanel.add(registerBtn, gbc);

        container.add(leftPanel, BorderLayout.WEST);
        container.add(rightPanel, BorderLayout.CENTER);

        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginUser();
            }
        });

        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new RegistrationForm();
            }
        });


        setVisible(true);
    }

    private void loginUser() {

        String usern = username.getText().trim();
        String passw = new String(password.getPassword()).trim();

        if (usern.isEmpty() || passw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.");
            return;
        }

        try (Connection conn = DB.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, usern);
            stmt.setString(2, passw);

            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                int userId = result.getInt("user_id");
                String fullName = result.getString("full_name");
                String userRole = result.getString("role");

                JOptionPane.showMessageDialog(this,
                        "Welcome " + fullName + "! (" + userRole + ")",
                        "Login Successful", JOptionPane.INFORMATION_MESSAGE);

                dispose();
                new UTMS(userRole, userId);

            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password.",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
public static void main(String[] args) 
{ 
	new LogInForm();
	} 
}
