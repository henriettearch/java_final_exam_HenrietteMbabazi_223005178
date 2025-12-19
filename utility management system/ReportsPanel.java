package com.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;
import com.util.DB;

public class ReportsPanel extends JPanel implements ActionListener {
    private String role;
    private int userId;

    JButton loadSummaryBtn = new JButton("LOAD SUMMARY REPORT");
    JButton recentPaymentsBtn = new JButton("VIEW RECENT PAYMENTS");
    JButton recentComplaintsBtn = new JButton("VIEW RECENT COMPLAINTS");
    JTable table;
    DefaultTableModel model;
    JLabel totalUsersLbl = new JLabel("Total Users: ");
    JLabel totalBillsLbl = new JLabel("Total Bills: ");
    JLabel totalPaymentsLbl = new JLabel("Total Payments: ");
    JLabel totalComplaintsLbl = new JLabel("Total Complaints: ");

    public ReportsPanel(String role, int userId) {
        this.role = role;
        this.userId = userId;
        setLayout(null);

        JLabel title = new JLabel("UTMS Summary Reports");
        title.setBounds(350, 10, 300, 25);
        title.setFont(title.getFont().deriveFont(18f));
        add(title);

        
        loadSummaryBtn.setBounds(30, 50, 200, 30);
        recentPaymentsBtn.setBounds(250, 50, 200, 30);
        recentComplaintsBtn.setBounds(470, 50, 200, 30);
        add(loadSummaryBtn);
        add(recentPaymentsBtn);
        add(recentComplaintsBtn);

        
        totalUsersLbl.setBounds(30, 100, 200, 25);
        totalBillsLbl.setBounds(250, 100, 200, 25);
        totalPaymentsLbl.setBounds(470, 100, 200, 25);
        totalComplaintsLbl.setBounds(690, 100, 200, 25);
        add(totalUsersLbl);
        add(totalBillsLbl);
        add(totalPaymentsLbl);
        add(totalComplaintsLbl);

        
        model = new DefaultTableModel(new String[]{"Column 1", "Column 2", "Column 3", "Column 4"}, 0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(30, 150, 900, 400);
        add(sp);

        
        loadSummaryBtn.addActionListener(this);
        recentPaymentsBtn.addActionListener(this);
        recentComplaintsBtn.addActionListener(this);

        applyRoleRestrictions();
    }

    private void applyRoleRestrictions() {
        if (role.equalsIgnoreCase("subscriber")) {
            JOptionPane.showMessageDialog(this, "Access denied! Reports are only for Admin and Staff.");
            setVisible(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try (Connection con = DB.getConnection()) {

            if (e.getSource() == loadSummaryBtn) {
                Statement st = con.createStatement();

                
                ResultSet rs1 = st.executeQuery("SELECT COUNT(*) FROM users");
                if (rs1.next()) totalUsersLbl.setText("Total Users: " + rs1.getInt(1));

                
                ResultSet rs2 = st.executeQuery("SELECT COUNT(*) FROM bills");
                if (rs2.next()) totalBillsLbl.setText("Total Bills: " + rs2.getInt(1));

               
                ResultSet rs3 = st.executeQuery("SELECT COUNT(*) FROM payments");
                if (rs3.next()) totalPaymentsLbl.setText("Total Payments: " + rs3.getInt(1));

                
                ResultSet rs4 = st.executeQuery("SELECT COUNT(*) FROM complaints");
                if (rs4.next()) totalComplaintsLbl.setText("Total Complaints: " + rs4.getInt(1));

                JOptionPane.showMessageDialog(this, "Summary loaded successfully!");
            }

            
            if (e.getSource() == recentPaymentsBtn) {
                model.setRowCount(0);
                model.setColumnIdentifiers(new String[]{
                        "Payment ID", "Subscriber ID", "Bill ID", "Amount Paid", "Payment Method", "Payment Date"
                });

                PreparedStatement ps = con.prepareStatement(
                        "SELECT payment_id, subscriber_id, bill_id, amount_paid, payment_method, payment_date " +
                        "FROM payments ORDER BY payment_date DESC LIMIT 10"
                );

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("payment_id"),
                            rs.getInt("subscriber_id"),
                            rs.getInt("bill_id"),
                            rs.getDouble("amount_paid"),
                            rs.getString("payment_method"),
                            rs.getTimestamp("payment_date")
                    });
                }
            }

            
            if (e.getSource() == recentComplaintsBtn) {
                model.setRowCount(0);
                model.setColumnIdentifiers(new String[]{
                        "Complaint ID", "Subscriber ID", "Utility ID", "Subject", "Status", "Submitted Date"
                });

                PreparedStatement ps = con.prepareStatement(
                        "SELECT complaint_id, subscriber_id, utility_id, subject, status, submitted_date " +
                        "FROM complaints ORDER BY submitted_date DESC LIMIT 10"
                );

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("complaint_id"),
                            rs.getInt("subscriber_id"),
                            rs.getInt("utility_id"),
                            rs.getString("subject"),
                            rs.getString("status"),
                            rs.getTimestamp("submitted_date")
                    });
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Reports Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 650);
        frame.add(new ReportsPanel("Admin", 1));
        frame.setVisible(true);
    }
}
