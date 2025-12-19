package com.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;
import com.util.DB;

public class UtilityTypesPanel extends JPanel implements ActionListener {
    private String role;
    private int userId;

    JTextField idTxt = new JTextField();
    JTextField nameTxt = new JTextField();
    JTextField rateTxt = new JTextField(); 
    JTextField descTxt = new JTextField();

    JButton addBtn = new JButton("ADD UTILITY");
    JButton updateBtn = new JButton("UPDATE UTILITY");
    JButton deleteBtn = new JButton("REMOVE UTILITY");
    JButton loadBtn = new JButton("LOAD UTILITIES");
    JButton selectBtn = new JButton("SELECT BY ID");

    JTable table;
    DefaultTableModel model;

    public UtilityTypesPanel(String role, int userId) {
        this.role = role;
        this.userId = userId;

        setLayout(null);
        JLabel title = new JLabel("UTILITY TYPES MANAGEMENT");
        title.setBounds(320, 10, 400, 25);
        title.setFont(title.getFont().deriveFont(18f));
        add(title);

        String[] columns = {"Utility ID", "Name", "Rate per Unit", "Description"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 310, 900, 300);

        int y = 50;
        addField("Utility ID", idTxt, y); y += 30;
        addField("Utility Name", nameTxt, y); y += 30;
        addField("Rate per Unit", rateTxt, y); y += 30; 
        addField("Description", descTxt, y);

        addButtons();
        add(sp);

        applyRoleRestrictions();
    }

    private void addButtons() {
        addBtn.setBounds(350, 50, 180, 30);
        updateBtn.setBounds(350, 90, 180, 30);
        deleteBtn.setBounds(350, 130, 180, 30);
        loadBtn.setBounds(350, 170, 180, 30);
        selectBtn.setBounds(20, 260, 510, 30);

        add(addBtn); add(updateBtn); add(deleteBtn); add(loadBtn); add(selectBtn);

        addBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        loadBtn.addActionListener(this);
        selectBtn.addActionListener(this);
    }

    private void addField(String lbl, JComponent txt, int y) {
        JLabel l = new JLabel(lbl);
        l.setBounds(20, y, 100, 25);
        txt.setBounds(120, y, 180, 25);
        add(l);
        add(txt);
    }

    private void applyRoleRestrictions() {
        if (role.equalsIgnoreCase("subscriber")) {
            addBtn.setEnabled(false);
            updateBtn.setEnabled(false);
            deleteBtn.setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try (Connection con = DB.getConnection()) {
            String sql = null;

            if (e.getSource() == addBtn && !role.equalsIgnoreCase("subscriber")) {
                sql = "INSERT INTO utility_types (utility_name, rate_per_unit, description) VALUES (?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, nameTxt.getText());
                ps.setBigDecimal(2, new java.math.BigDecimal(rateTxt.getText()));
                ps.setString(3, descTxt.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Utility type added!");
            } 
            
            else if (e.getSource() == updateBtn && !role.equalsIgnoreCase("subscriber")) {
                sql = "UPDATE utility_types SET utility_name=?, rate_per_unit=?, description=? WHERE utility_id=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, nameTxt.getText());
                ps.setBigDecimal(2, new java.math.BigDecimal(rateTxt.getText()));
                ps.setString(3, descTxt.getText());
                ps.setInt(4, Integer.parseInt(idTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Utility updated!");
            } 
            
            else if (e.getSource() == deleteBtn && role.equalsIgnoreCase("admin")) {
                sql = "DELETE FROM utility_types WHERE utility_id=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(idTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Utility deleted!");
            } 
            
            else if (e.getSource() == loadBtn) {
                model.setRowCount(0);
                sql = "SELECT * FROM utility_types";
                ResultSet rs = con.createStatement().executeQuery(sql);
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("utility_id"),
                        rs.getString("utility_name"),
                        rs.getBigDecimal("rate_per_unit"),
                        rs.getString("description")
                    });
                }
            } 
           
            else if (e.getSource() == selectBtn) {
                model.setRowCount(0);
                sql = "SELECT * FROM utility_types WHERE utility_id=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(idTxt.getText()));
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("utility_id"),
                        rs.getString("utility_name"),
                        rs.getBigDecimal("rate_per_unit"),
                        rs.getString("description")
                    });
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Utility Types Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(960, 620);
        frame.add(new UtilityTypesPanel("Admin", 1));
        frame.setVisible(true);
    }
}
