package com.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;
import com.util.DB;

public class ConsumptionPanel extends JPanel implements ActionListener {
    private String role;
    private int userId;

    JTextField idTxt = new JTextField();
    JTextField subscriberTxt = new JTextField();
    JTextField utilityTxt = new JTextField();
    JTextField unitsTxt = new JTextField();
    JTextField monthTxt = new JTextField();

    JButton addBtn = new JButton("ADD CONSUMPTION");
    JButton updateBtn = new JButton("UPDATE CONSUMPTION");
    JButton deleteBtn = new JButton("DELETE CONSUMPTION");
    JButton loadBtn = new JButton("LOAD CONSUMPTIONS");
    JButton selectBtn = new JButton("SELECT BY ID");

    JTable table;
    DefaultTableModel model;

    public ConsumptionPanel(String role, int userId) {
        this.role = role;
        this.userId = userId;

        setLayout(null);
        JLabel title = new JLabel("UTILITY CONSUMPTION RECORDS");
        title.setBounds(320, 10, 400, 25);
        title.setFont(title.getFont().deriveFont(18f));
        add(title);

        String[] columns = {"Consumption ID", "Subscriber ID", "Utility ID", "Month", "Units Used", "Recorded By", "Date Recorded"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 320, 900, 280);

        int y = 50;
        addField("Consumption ID", idTxt, y); y += 35;
        addField("Subscriber ID", subscriberTxt, y); y += 35;
        addField("Utility ID", utilityTxt, y); y += 35;
        addField("Month", monthTxt, y); y += 35;
        addField("Units Used", unitsTxt, y);

        addButtons();
        add(sp);
        applyRoleRestrictions();
    }

    private void addField(String lbl, JComponent txt, int y) {
        JLabel l = new JLabel(lbl);
        l.setBounds(20, y, 100, 25);
        txt.setBounds(120, y, 180, 25);
        add(l);
        add(txt);
    }

    private void addButtons() {
        addBtn.setBounds(350, 50, 180, 30);
        updateBtn.setBounds(350, 90, 180, 30);
        deleteBtn.setBounds(350, 130, 180, 30);
        loadBtn.setBounds(350, 170, 180, 30);
        selectBtn.setBounds(20, 270, 510, 30);

        add(addBtn); add(updateBtn); add(deleteBtn); add(loadBtn); add(selectBtn);

        addBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        loadBtn.addActionListener(this);
        selectBtn.addActionListener(this);
    }

    private void applyRoleRestrictions() {
        if (role.equalsIgnoreCase("subscriber")) {
            updateBtn.setEnabled(false);
            deleteBtn.setEnabled(false);
        }
    }

    private int getLastInsertedConsumptionId(Connection con) throws SQLException {
        ResultSet rs = con.createStatement().executeQuery("SELECT MAX(consumption_id) AS last_id FROM consumption");
        if (rs.next()) return rs.getInt("last_id");
        return 0;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try (Connection con = DB.getConnection()) {

           
            if (e.getSource() == addBtn) {

                
                if (role.equalsIgnoreCase("subscriber")) {
                    
                    PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO consumption (subscriber_id, utility_id, month, units_used, recorded_by) VALUES (?, ?, ?, ?, ?)"
                    );
                    ps.setInt(1, userId);
                    ps.setInt(2, Integer.parseInt(utilityTxt.getText()));
                    ps.setString(3, monthTxt.getText());
                    ps.setDouble(4, Double.parseDouble(unitsTxt.getText()));
                    ps.setInt(5, userId);
                    ps.executeUpdate();

                    
                    PreparedStatement rateStmt = con.prepareStatement(
                        "SELECT rate_per_unit FROM utility_types WHERE utility_id=?"
                    );
                    rateStmt.setInt(1, Integer.parseInt(utilityTxt.getText()));
                    ResultSet rs = rateStmt.executeQuery();
                    double rate = 0;
                    if (rs.next()) rate = rs.getDouble("rate_per_unit");

                    double total = rate * Double.parseDouble(unitsTxt.getText());

                    PreparedStatement billStmt = con.prepareStatement(
                        "INSERT INTO bills (consumption_id, total_amount, status, issue_date, due_date) VALUES (?, ?, 'Pending', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY))"
                    );
                    billStmt.setInt(1, getLastInsertedConsumptionId(con));
                    billStmt.setDouble(2, total);
                    billStmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Consumption recorded and bill generated successfully!");
                }

                
                else {
                    PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO consumption (subscriber_id, utility_id, month, units_used, recorded_by) VALUES (?, ?, ?, ?, ?)"
                    );
                    ps.setInt(1, Integer.parseInt(subscriberTxt.getText()));
                    ps.setInt(2, Integer.parseInt(utilityTxt.getText()));
                    ps.setString(3, monthTxt.getText());
                    ps.setDouble(4, Double.parseDouble(unitsTxt.getText()));
                    ps.setInt(5, userId);
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Consumption record added successfully!");
                }
            }

            
            else if (e.getSource() == updateBtn && !role.equalsIgnoreCase("subscriber")) {
                PreparedStatement ps = con.prepareStatement(
                    "UPDATE consumption SET subscriber_id=?, utility_id=?, month=?, units_used=? WHERE consumption_id=?"
                );
                ps.setInt(1, Integer.parseInt(subscriberTxt.getText()));
                ps.setInt(2, Integer.parseInt(utilityTxt.getText()));
                ps.setString(3, monthTxt.getText());
                ps.setDouble(4, Double.parseDouble(unitsTxt.getText()));
                ps.setInt(5, Integer.parseInt(idTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Consumption record updated!");
            }

            
            else if (e.getSource() == deleteBtn && role.equalsIgnoreCase("admin")) {
                PreparedStatement ps = con.prepareStatement("DELETE FROM consumption WHERE consumption_id=?");
                ps.setInt(1, Integer.parseInt(idTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Consumption deleted successfully!");
            }

            
            else if (e.getSource() == loadBtn) {
                model.setRowCount(0);
                PreparedStatement ps;
                if (role.equalsIgnoreCase("subscriber")) {
                    ps = con.prepareStatement("SELECT * FROM consumption WHERE subscriber_id=?");
                    ps.setInt(1, userId);
                } else {
                    ps = con.prepareStatement("SELECT * FROM consumption");
                }
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("consumption_id"),
                        rs.getInt("subscriber_id"),
                        rs.getInt("utility_id"),
                        rs.getString("month"),
                        rs.getDouble("units_used"),
                        rs.getInt("recorded_by"),
                        rs.getTimestamp("date_recorded")
                    });
                }
            }

            
            else if (e.getSource() == selectBtn) {
                model.setRowCount(0);
                PreparedStatement ps = con.prepareStatement("SELECT * FROM consumption WHERE consumption_id=?");
                ps.setInt(1, Integer.parseInt(idTxt.getText()));
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("consumption_id"),
                        rs.getInt("subscriber_id"),
                        rs.getInt("utility_id"),
                        rs.getString("month"),
                        rs.getDouble("units_used"),
                        rs.getInt("recorded_by"),
                        rs.getTimestamp("date_recorded")
                    });
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Consumption Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(960, 620);
        frame.add(new ConsumptionPanel("Subscriber", 1));
        frame.setVisible(true);
    }
}
