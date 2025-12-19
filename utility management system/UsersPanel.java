package com.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.util.DB;


public class UsersPanel extends JPanel implements ActionListener {

    
    private JTextField usernameTxt = new JTextField();
    private JTextField fullnameTxt = new JTextField();
    private JTextField emailTxt = new JTextField();
    private JTextField phoneTxt = new JTextField();
    private JTextField addressTxt = new JTextField();
    private JTextField roleTxt = new JTextField();
    private JPasswordField passwordTxt = new JPasswordField();

    
    private JButton addBtn = new JButton("ADD USER");
    private JButton loadBtn = new JButton("LOAD USERS");
    private JButton clearBtn = new JButton("CLEAR");
    private JButton logoutBtn = new JButton("LOGOUT");

    
    private DefaultTableModel model;
    private JTable table;

    private String role;
    private int userId;

    public UsersPanel(String role, int userId) {
        this.role = role;
        this.userId = userId;

        setLayout(null);
        setBounds(0, 0, 950, 600);
        setBackground(Color.WHITE);

        
        JLabel title = new JLabel("USER MANAGEMENT PANEL");
        title.setBounds(330, 10, 400, 30);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(title);

        
        String[] labels = {"Username:", "Full name:", "Email:", "Password:",
                "Role:", "Phone:", "Address:"};
        JComponent[] fields = {usernameTxt, fullnameTxt, emailTxt, passwordTxt,
                roleTxt, phoneTxt, addressTxt};

        int y = 60;
        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setBounds(30, y, 100, 25);
            fields[i].setBounds(140, y, 200, 25);
            add(lbl);
            add(fields[i]);
            y += 35;
        }

        
        addBtn.setBounds(380, 80, 150, 35);
        loadBtn.setBounds(550, 80, 150, 35);
        clearBtn.setBounds(380, 130, 150, 35);
        logoutBtn.setBounds(780, 540, 120, 30);

        add(addBtn);
        add(loadBtn);
        add(clearBtn);
        add(logoutBtn);

       
        String[] columns = {"User ID", "Username", "Full Name", "Email", "Role", "Phone", "Address", "Created At"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setFillsViewportHeight(true);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(30, 320, 880, 200);
        add(sp);

        
        addBtn.addActionListener(this);
        loadBtn.addActionListener(this);
        clearBtn.addActionListener(this);
        logoutBtn.addActionListener(this);

        applyRoleRestrictions();
    }

   
    private void applyRoleRestrictions() {
        if (role.equalsIgnoreCase("staff") || role.equalsIgnoreCase("subscriber")) {
            addBtn.setEnabled(false);
        }
    }

    
    private void Clear() {
        usernameTxt.setText("");
        fullnameTxt.setText("");
        emailTxt.setText("");
        passwordTxt.setText("");
        roleTxt.setText("");
        phoneTxt.setText("");
        addressTxt.setText("");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try (Connection con = DB.getConnection()) {

            // ===== LOAD USERS =====
            if (e.getSource() == loadBtn) {
                model.setRowCount(0);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM users");
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("role"),
                            rs.getString("phone"),
                            rs.getString("address"),
                            rs.getTimestamp("created_at")
                    });
                }
                JOptionPane.showMessageDialog(this, "Users loaded successfully!");
            }

            // ===== ADD USER =====
            if (e.getSource() == addBtn) {
                String sql = "INSERT INTO users (username, full_name, email, password, role, phone, address) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, usernameTxt.getText());
                ps.setString(2, fullnameTxt.getText());
                ps.setString(3, emailTxt.getText());
                ps.setString(4, new String(passwordTxt.getPassword()));
                ps.setString(5, roleTxt.getText());
                ps.setString(6, phoneTxt.getText());
                ps.setString(7, addressTxt.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "User added successfully!");
                Clear();
                loadUsers(con);
            }

            // ===== CLEAR FIELDS =====
            if (e.getSource() == clearBtn) {
                Clear();
            }

            // ===== LOGOUT =====
            if (e.getSource() == logoutBtn) {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    parentFrame.dispose();
                    JOptionPane.showMessageDialog(null, "You have been logged out successfully!");
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    
    private void loadUsers(Connection con) throws SQLException {
        model.setRowCount(0);
        ResultSet rs = con.createStatement().executeQuery("SELECT * FROM users");
        while (rs.next()) {
            model.addRow(new Object[]{
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("role"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getTimestamp("created_at")
            });
        }
    }

    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Users Panel ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(960, 620);
        frame.add(new UsersPanel("Admin", 1));
        frame.setVisible(true);
    }
}
