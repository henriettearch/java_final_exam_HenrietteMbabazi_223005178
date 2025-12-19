package com.form;

import java.awt.*;
import javax.swing.*;
import com.panel.*;  // Import all your panels

public class UTMS extends JFrame {

    private JTabbedPane pane;
    private String role;
    private int user_id;

    public UTMS(String role, int user_id) {
        this.role = role;
        this.user_id = user_id;

        setTitle("UTILITY MANAGEMENT SYSTEM (UTMS)");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        createUI();

        add(pane, BorderLayout.CENTER);
        setVisible(true);
    }

    private void createUI() {
        pane = new JTabbedPane();

        switch (role) {
            case "Admin":
                pane.addTab("Users", new UsersPanel(role, user_id));
                pane.addTab("Utility Types", new UtilityTypesPanel(role, user_id));
                pane.addTab("Consumption", new ConsumptionPanel(role, user_id));
                pane.addTab("Bills", new BillsPanel(role, user_id));
                pane.addTab("Payments", new PaymentsPanel(role, user_id));
                pane.addTab("Complaints", new ComplaintsPanel(role, user_id));
                pane.addTab("Reports", new ReportsPanel(role, user_id));
                break;

            case "Staff":
                pane.addTab("Utility Types", new UtilityTypesPanel(role, user_id));
                pane.addTab("Consumption", new ConsumptionPanel(role, user_id));
                pane.addTab("Bills", new BillsPanel(role, user_id));
                pane.addTab("Payments", new PaymentsPanel(role, user_id));
                pane.addTab("Complaints", new ComplaintsPanel(role, user_id));
                break;

            case "Subscriber":
                pane.addTab("My Bills", new BillsPanel(role,user_id));
                pane.addTab("My Payments", new PaymentsPanel(role,user_id));
                pane.addTab("Submit Complaint", new ComplaintsPanel(role,user_id));
                pane.addTab("View Consumption", new ConsumptionPanel(role,user_id));
                break;

            default:
                JOptionPane.showMessageDialog(this,
                        "Unknown role: " + role,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                dispose();
                break;
        }
    }

    public static void main(String[] args) {
        new UTMS("Admin", 1);
        new UTMS("Staff", 2);
        new UTMS("Subscriber", 3);
    }
}
