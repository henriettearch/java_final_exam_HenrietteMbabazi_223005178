package com.form;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

import com.util.DB;

public class RegistrationForm extends JFrame {

    JLabel titleLabel = new JLabel("WELCOME TO UTILITY MANAGEMENT SYSTEM", SwingConstants.CENTER);

    JLabel ul = new JLabel("Username:");
    JTextField username = new JTextField(20);

    JLabel fl = new JLabel("Full Name:");
    JTextField fullname = new JTextField(20);

    JLabel el = new JLabel("Email:");
    JTextField email = new JTextField(20);

    JLabel phl = new JLabel("Phone:");
    JTextField phone = new JTextField(20);

    JLabel al = new JLabel("Address:");
    JTextField address = new JTextField(20);

    JLabel pl = new JLabel("Password:");
    JPasswordField password = new JPasswordField(20);

    JLabel rl = new JLabel("Role:");
    String[] roles = {"Admin", "Staff", "Subscriber"};
    JComboBox<String> role = new JComboBox<>(roles);

    JButton registerBtn = new JButton("Register");
    JButton loginBtn = new JButton("Login");

    public RegistrationForm() {

        setTitle("UTMS - Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0)); 


        JPanel mainPanel = new JPanel(new BorderLayout());


        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 12));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        formPanel.add(ul);
        formPanel.add(username);

        formPanel.add(fl);
        formPanel.add(fullname);

        formPanel.add(el);
        formPanel.add(email);

        formPanel.add(phl);
        formPanel.add(phone);

        formPanel.add(al);
        formPanel.add(address);

        formPanel.add(pl);
        formPanel.add(password);

        formPanel.add(rl);
        formPanel.add(role);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        btnPanel.add(registerBtn);
        btnPanel.add(loginBtn);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);

        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LogInForm();
            }
        });

        setVisible(true);
    }

    
    private void registerUser() {

        String usern = username.getText().trim();
        String fulln = fullname.getText().trim();
        String mail = email.getText().trim();
        String ph = phone.getText().trim();
        String addr = address.getText().trim();
        String rol = role.getSelectedItem().toString();
        String passw = new String(password.getPassword()).trim();

        if (usern.isEmpty() || fulln.isEmpty() || mail.isEmpty() || passw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields!");
            return;
        }

        try (Connection conn = DB.getConnection()) {

            PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT * FROM users WHERE username = ? OR email = ?"
            );
            checkStmt.setString(1, usern);
            checkStmt.setString(2, mail);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Username or Email already exists!");
                return;
            }

            PreparedStatement insertStmt = conn.prepareStatement(
                "INSERT INTO users (username, full_name, email, password, role, phone, address, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())"
            );

            insertStmt.setString(1, usern);
            insertStmt.setString(2, fulln);
            insertStmt.setString(3, mail);
            insertStmt.setString(4, passw);
            insertStmt.setString(5, rol);
            insertStmt.setString(6, ph);
            insertStmt.setString(7, addr);

            int result = insertStmt.executeUpdate();

            if (result == 1) {
                JOptionPane.showMessageDialog(this,
                    "Registration successful! Please log in.");
                dispose();
                new LogInForm();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. Try again.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
