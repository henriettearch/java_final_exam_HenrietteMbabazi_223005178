package com.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;
import com.util.DB;

public class BillsPanel extends JPanel implements ActionListener {
    private String role;
    private int userId;

    JTextField billIdTxt = new JTextField();
    JTextField consumptionIdTxt = new JTextField();
    JTextField utilityTypeTxt = new JTextField();
    JTextField amountTxt = new JTextField();
    JTextField statusTxt = new JTextField();

    JButton addBtn = new JButton("ADD BILL");
    JButton updateBtn = new JButton("UPDATE BILL");
    JButton deleteBtn = new JButton("DELETE BILL");
    JButton loadBtn = new JButton("LOAD BILLS");
    JButton selectBtn = new JButton("SEARCH BY BILL ID");

    DefaultTableModel model;
    JTable table;

    public BillsPanel(String role, int userId) {
        this.role = role;
        this.userId = userId;

        setLayout(null);
        setBounds(0, 0, 1000, 600);

        
        JLabel title = new JLabel("UTILITY BILLS RECORDS");
        title.setBounds(360, 15, 400, 30);
        title.setFont(title.getFont().deriveFont(20f));
        add(title);

        
        JLabel l1 = new JLabel("Bill ID:");
        JLabel l2 = new JLabel("Consumption ID:");
        JLabel l3 = new JLabel("Utility Type:");
        JLabel l4 = new JLabel("Amount:");
        JLabel l5 = new JLabel("Status:");

        l1.setBounds(30, 70, 120, 25);
        billIdTxt.setBounds(160, 70, 180, 25);
        l2.setBounds(30, 105, 120, 25);
        consumptionIdTxt.setBounds(160, 105, 180, 25);
        l3.setBounds(30, 140, 120, 25);
        utilityTypeTxt.setBounds(160, 140, 180, 25);
        l4.setBounds(30, 175, 120, 25);
        amountTxt.setBounds(160, 175, 180, 25);
        l5.setBounds(30, 210, 120, 25);
        statusTxt.setBounds(160, 210, 180, 25);

        add(l1); add(billIdTxt);
        add(l2); add(consumptionIdTxt);
        add(l3); add(utilityTypeTxt);
        add(l4); add(amountTxt);
        add(l5); add(statusTxt);

        
        addBtn.setBounds(380, 70, 160, 35);
        updateBtn.setBounds(380, 115, 160, 35);
        deleteBtn.setBounds(380, 160, 160, 35);
        loadBtn.setBounds(380, 205, 160, 35);
        selectBtn.setBounds(30, 260, 510, 35);

        add(addBtn);
        add(updateBtn);
        add(deleteBtn);
        add(loadBtn);
        add(selectBtn);

        
        String[] cols = {"Bill ID", "Consumption ID", "Total Amount", "Status", "Issue Date", "Due Date"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(30, 310, 920, 250);
        add(sp);

        
        addBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        loadBtn.addActionListener(this);
        selectBtn.addActionListener(this);

        applyRoleRestrictions();
    }

    private void applyRoleRestrictions() {
        switch (role.toLowerCase()) {
            case "staff":
                addBtn.setEnabled(true);
                updateBtn.setEnabled(true);
                deleteBtn.setEnabled(false);
                break;
            case "subscriber":
                addBtn.setEnabled(false);
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
                   
                    ps = con.prepareStatement(
                        "SELECT * FROM bills WHERE consumption_id IN (SELECT consumption_id FROM consumption WHERE subscriber_id = ?)"
                    );
                    ps.setInt(1, userId);
                } else {
                    
                    ps = con.prepareStatement("SELECT * FROM bills");
                }

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("bill_id"),
                        rs.getInt("consumption_id"),
                        rs.getDouble("total_amount"),
                        rs.getString("status"),
                        rs.getDate("issue_date"),
                        rs.getDate("due_date")
                    });
                }
            }

            
            if (e.getSource() == selectBtn) {
                model.setRowCount(0);
                PreparedStatement ps = con.prepareStatement("SELECT * FROM bills WHERE bill_id = ?");
                ps.setInt(1, Integer.parseInt(billIdTxt.getText()));
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("bill_id"),
                        rs.getInt("consumption_id"),
                        rs.getDouble("total_amount"),
                        rs.getString("status"),
                        rs.getDate("issue_date"),
                        rs.getDate("due_date")
                    });
                }
            }

            
            if (e.getSource() == addBtn && !role.equalsIgnoreCase("subscriber")) {
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO bills (consumption_id, total_amount, status, issue_date, due_date) VALUES (?, ?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY))"
                );
                ps.setInt(1, Integer.parseInt(consumptionIdTxt.getText()));
                ps.setDouble(2, Double.parseDouble(amountTxt.getText()));
                ps.setString(3, statusTxt.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Bill added successfully!");
            }

            
            if (e.getSource() == updateBtn && !role.equalsIgnoreCase("subscriber")) {
                PreparedStatement ps = con.prepareStatement(
                    "UPDATE bills SET consumption_id=?, total_amount=?, status=? WHERE bill_id=?"
                );
                ps.setInt(1, Integer.parseInt(consumptionIdTxt.getText()));
                ps.setDouble(2, Double.parseDouble(amountTxt.getText()));
                ps.setString(3, statusTxt.getText());
                ps.setInt(4, Integer.parseInt(billIdTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Bill updated successfully!");
            }

            
            if (e.getSource() == deleteBtn && role.equalsIgnoreCase("admin")) {
                PreparedStatement ps = con.prepareStatement("DELETE FROM bills WHERE bill_id=?");
                ps.setInt(1, Integer.parseInt(billIdTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Bill deleted successfully!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Bills Panel");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(1000, 600);
        f.add(new BillsPanel("Subscriber", 1)); 
        f.setVisible(true);
    }
}
