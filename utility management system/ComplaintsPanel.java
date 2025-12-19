package com.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;
import com.util.DB;

public class ComplaintsPanel extends JPanel implements ActionListener {
    private String role;
    private int userId;

    JTextField complaintIdTxt = new JTextField();
    JComboBox<String> utilityCombo = new JComboBox<>();
    JTextField subjectTxt = new JTextField();
    JTextArea descriptionTxt = new JTextArea();
    JTextField statusTxt = new JTextField();

    JButton addBtn = new JButton("SUBMIT COMPLAINT");
    JButton updateBtn = new JButton("UPDATE STATUS");
    JButton deleteBtn = new JButton("DELETE COMPLAINT");
    JButton loadBtn = new JButton("LOAD COMPLAINTS");
    JButton selectBtn = new JButton("SEARCH BY ID");

    JTable table;
    DefaultTableModel model;

    public ComplaintsPanel(String role, int userId) {
        this.role = role;
        this.userId = userId;

        setLayout(null);
        JLabel title = new JLabel("UTILITY COMPLAINTS MANAGEMENT");
        title.setBounds(320, 10, 400, 25);
        title.setFont(title.getFont().deriveFont(18f));
        add(title);

        int y = 40;
        addField("Complaint ID:", complaintIdTxt, y); y += 35;
        addField("Utility Type:", utilityCombo, y); y += 35;
        addField("Subject:", subjectTxt, y); y += 35;

        JLabel descLabel = new JLabel("Description:");
        descLabel.setBounds(20, y, 100, 25);
        add(descLabel);
        descriptionTxt.setBounds(120, y, 180, 60);
        add(descriptionTxt);
        y += 65;

        addField("Status:", statusTxt, y);

        addBtn.setBounds(350, 40, 180, 30);
        updateBtn.setBounds(350, 80, 180, 30);
        deleteBtn.setBounds(350, 120, 180, 30);
        loadBtn.setBounds(350, 160, 180, 30);
        selectBtn.setBounds(20, 260, 510, 30);

        add(addBtn); add(updateBtn); add(deleteBtn); add(loadBtn); add(selectBtn);

        String[] cols = {"Complaint ID", "Subscriber ID", "Utility", "Subject", "Description", "Status", "Submitted Date"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 310, 900, 250);
        add(sp);

        addBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        loadBtn.addActionListener(this);
        selectBtn.addActionListener(this);

        loadUtilities();
        applyRoleRestrictions();
    }

    private void addField(String label, JComponent field, int y) {
        JLabel l = new JLabel(label);
        l.setBounds(20, y, 100, 25);
        field.setBounds(120, y, 180, 25);
        add(l);
        add(field);
    }

    private void loadUtilities() {
    	
    	    try (Connection con = DB.getConnection();
    	         Statement st = con.createStatement();
    	         ResultSet rs = st.executeQuery("SELECT utility_id, utility_name FROM utility_types")) {

    	        while (rs.next()) {
    	            utilityCombo.addItem(rs.getInt("utility_id") + " - " + rs.getString("utility_name"));
    	        }

    	    } catch (Exception ex) { 
    	        ex.printStackTrace();
    	        JOptionPane.showMessageDialog(this, "Failed to load utilities: " + ex.getMessage());
    	    }
    	}

    

    private void applyRoleRestrictions() {
        switch (role.toLowerCase()) {
            case "staff":
                addBtn.setEnabled(false);
                deleteBtn.setEnabled(false);
                break;
            case "subscriber":
                updateBtn.setEnabled(false);
                deleteBtn.setEnabled(false);
                break;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try (Connection con = DB.getConnection()) {

            if (e.getSource() == loadBtn) {
                model.setRowCount(0);
                PreparedStatement ps;
                if (role.equalsIgnoreCase("subscriber")) {
                    ps = con.prepareStatement("SELECT * FROM complaints WHERE subscriber_id = ?");
                    ps.setInt(1, userId);
                } else {
                    ps = con.prepareStatement("SELECT * FROM complaints");
                }
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("complaint_id"),
                        rs.getInt("subscriber_id"),
                        rs.getInt("utility_id"),
                        rs.getString("subject"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getTimestamp("submitted_date")
                    });
                }
            }

            if (e.getSource() == selectBtn) {
                model.setRowCount(0);
                PreparedStatement ps = con.prepareStatement("SELECT * FROM complaints WHERE complaint_id=?");
                ps.setInt(1, Integer.parseInt(complaintIdTxt.getText()));
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("complaint_id"),
                        rs.getInt("subscriber_id"),
                        rs.getInt("utility_id"),
                        rs.getString("subject"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getTimestamp("submitted_date")
                    });
                }
            }

            if (e.getSource() == addBtn && role.equalsIgnoreCase("subscriber")) {
                String selectedUtility = (String) utilityCombo.getSelectedItem();
                if (selectedUtility == null) {
                    JOptionPane.showMessageDialog(this, "Please select a utility type!");
                    return;
                }
                int utilityId = Integer.parseInt(selectedUtility.split(" - ")[0]);

                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO complaints (subscriber_id, utility_id, subject, description, status) VALUES (?, ?, ?, ?, ?)"
                );
                ps.setInt(1, userId);
                ps.setInt(2, utilityId);
                ps.setString(3, subjectTxt.getText());
                ps.setString(4, descriptionTxt.getText());
                ps.setString(5, "Pending");
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Complaint submitted successfully!");
            }

            if (e.getSource() == updateBtn && !role.equalsIgnoreCase("subscriber")) {
                PreparedStatement ps = con.prepareStatement(
                    "UPDATE complaints SET status=? WHERE complaint_id=?"
                );
                ps.setString(1, statusTxt.getText());
                ps.setInt(2, Integer.parseInt(complaintIdTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Complaint status updated!");
            }

            if (e.getSource() == deleteBtn && role.equalsIgnoreCase("admin")) {
                PreparedStatement ps = con.prepareStatement("DELETE FROM complaints WHERE complaint_id=?");
                ps.setInt(1, Integer.parseInt(complaintIdTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Complaint deleted!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Complaints Panel Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 650);
        frame.add(new ComplaintsPanel("Subscriber", 1));
        frame.setVisible(true);
    }
}
