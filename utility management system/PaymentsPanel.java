package com.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;
import com.util.DB;

public class PaymentsPanel extends JPanel implements ActionListener {
    private String role;
    private int userId;

    JTextField paymentIdTxt = new JTextField();
    JTextField billIdTxt = new JTextField();
    JTextField subscriberIdTxt = new JTextField();
    JTextField amountTxt = new JTextField();
    JComboBox<String> methodCombo = new JComboBox<>(new String[]{"Mobile Money", "Cash", "Bank Transfer"});
    JTextField statusTxt = new JTextField();

    JButton addBtn = new JButton("ADD PAYMENT");
    JButton updateBtn = new JButton("UPDATE PAYMENT");
    JButton deleteBtn = new JButton("DELETE PAYMENT");
    JButton loadBtn = new JButton("LOAD PAYMENTS");
    JButton selectBtn = new JButton("SEARCH BY PAYMENT ID");

    JTable table;
    DefaultTableModel model;

    public PaymentsPanel(String role, int userId) {
        this.role = role;
        this.userId = userId;
        setLayout(null);

        JLabel title = new JLabel("UTILITY PAYMENTS RECORDS");
        title.setBounds(340, 15, 400, 35);
        title.setFont(title.getFont().deriveFont(20f));
        add(title);

        
        JLabel l1 = new JLabel("Payment ID:");
        JLabel l2 = new JLabel("Bill ID:");
        JLabel l3 = new JLabel("Subscriber ID:");
        JLabel l4 = new JLabel("Amount:");
        JLabel l5 = new JLabel("Method:");
        JLabel l6 = new JLabel("Status:");

        int startY = 70;
        int labelWidth = 120;
        int fieldWidth = 220;
        int height = 30;
        int gap = 40;

        l1.setBounds(40, startY, labelWidth, height);
        paymentIdTxt.setBounds(170, startY, fieldWidth, height);

        l2.setBounds(40, startY + gap, labelWidth, height);
        billIdTxt.setBounds(170, startY + gap, fieldWidth, height);

        l3.setBounds(40, startY + gap * 2, labelWidth, height);
        subscriberIdTxt.setBounds(170, startY + gap * 2, fieldWidth, height);

        l4.setBounds(40, startY + gap * 3, labelWidth, height);
        amountTxt.setBounds(170, startY + gap * 3, fieldWidth, height);

        l5.setBounds(40, startY + gap * 4, labelWidth, height);
        methodCombo.setBounds(170, startY + gap * 4, fieldWidth, height);

        l6.setBounds(40, startY + gap * 5, labelWidth, height);
        statusTxt.setBounds(170, startY + gap * 5, fieldWidth, height);

        add(l1); add(paymentIdTxt);
        add(l2); add(billIdTxt);
        add(l3); add(subscriberIdTxt);
        add(l4); add(amountTxt);
        add(l5); add(methodCombo);
        add(l6); add(statusTxt);

        
        int btnX = 450;
        int btnWidth = 180;
        int btnHeight = 35;
        int btnGap = 45;

        addBtn.setBounds(btnX, 70, btnWidth, btnHeight);
        updateBtn.setBounds(btnX, 70 + btnGap, btnWidth, btnHeight);
        deleteBtn.setBounds(btnX, 70 + btnGap * 2, btnWidth, btnHeight);
        loadBtn.setBounds(btnX, 70 + btnGap * 3, btnWidth, btnHeight);
        selectBtn.setBounds(40, 340, 590, 35);

        add(addBtn);
        add(updateBtn);
        add(deleteBtn);
        add(loadBtn);
        add(selectBtn);

        
        String[] cols = {"Payment ID", "Bill ID", "Subscriber ID", "Amount", "Method", "Status", "Payment Date"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(40, 390, 900, 230);
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
                    ps = con.prepareStatement("SELECT * FROM payments WHERE subscriber_id = ?");
                    ps.setInt(1, userId);
                } else {
                    ps = con.prepareStatement("SELECT * FROM payments");
                }
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("payment_id"),
                        rs.getInt("bill_id"),
                        rs.getInt("subscriber_id"),
                        rs.getDouble("amount_paid"),
                        rs.getString("payment_method"),
                        "N/A",
                        rs.getString("payment_date")
                    });
                }
            }

            
            if (e.getSource() == selectBtn) {
                model.setRowCount(0);
                PreparedStatement ps = con.prepareStatement("SELECT * FROM payments WHERE payment_id=?");
                ps.setInt(1, Integer.parseInt(paymentIdTxt.getText()));
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("payment_id"),
                        rs.getInt("bill_id"),
                        rs.getInt("subscriber_id"),
                        rs.getDouble("amount_paid"),
                        rs.getString("payment_method"),
                        "N/A",
                        rs.getString("payment_date")
                    });
                }
            }

            
            if (e.getSource() == addBtn) {
                String method = (String) methodCombo.getSelectedItem();

                if (role.equalsIgnoreCase("subscriber")) {
                    
                    PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO payments (bill_id, subscriber_id, amount_paid, payment_method) VALUES (?, ?, ?, ?)"
                    );
                    ps.setInt(1, Integer.parseInt(billIdTxt.getText()));
                    ps.setInt(2, userId);
                    ps.setDouble(3, Double.parseDouble(amountTxt.getText()));
                    ps.setString(4, method);
                    ps.executeUpdate();

                    
                    PreparedStatement update = con.prepareStatement("UPDATE bills SET status='Paid' WHERE bill_id=?");
                    update.setInt(1, Integer.parseInt(billIdTxt.getText()));
                    update.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Payment recorded and bill marked as Paid!");
                } else {
                    
                    PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO payments (bill_id, subscriber_id, amount_paid, payment_method) VALUES (?, ?, ?, ?)"
                    );
                    ps.setInt(1, Integer.parseInt(billIdTxt.getText()));
                    ps.setInt(2, Integer.parseInt(subscriberIdTxt.getText()));
                    ps.setDouble(3, Double.parseDouble(amountTxt.getText()));
                    ps.setString(4, method);
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Payment added successfully!");
                }
            }

            
            if (e.getSource() == updateBtn && !role.equalsIgnoreCase("subscriber")) {
                String method = (String) methodCombo.getSelectedItem();

                PreparedStatement ps = con.prepareStatement(
                    "UPDATE payments SET bill_id=?, subscriber_id=?, amount_paid=?, payment_method=? WHERE payment_id=?"
                );
                ps.setInt(1, Integer.parseInt(billIdTxt.getText()));
                ps.setInt(2, Integer.parseInt(subscriberIdTxt.getText()));
                ps.setDouble(3, Double.parseDouble(amountTxt.getText()));
                ps.setString(4, method);
                ps.setInt(5, Integer.parseInt(paymentIdTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Payment updated successfully!");
            }

            
            if (e.getSource() == deleteBtn && role.equalsIgnoreCase("admin")) {
                PreparedStatement ps = con.prepareStatement("DELETE FROM payments WHERE payment_id=?");
                ps.setInt(1, Integer.parseInt(paymentIdTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Payment deleted successfully!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Payments Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.add(new PaymentsPanel("Subscriber", 1));
        frame.setVisible(true);
    }
}
