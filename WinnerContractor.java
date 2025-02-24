import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class WinnerContractor extends JFrame {
    // Declare labels for the application
    JLabel applicationId, tenderID, tenderName, budget, duration, material;
    JLabel constructorId, ConstructorName, workExperience, proposedBudget, proposedMaterial;
    int t_ID; // Tender ID passed to the constructor

    // Constructor for initializing the JFrame and adding components
    WinnerContractor(int t_ID) {
        this.t_ID = t_ID; // Set the tender ID

        // Set up the window layout
        JLabel heading = new JLabel("!!Winner Winner!!");
        heading.setBounds(200, 10, 500, 35);
        heading.setFont(new Font("Tw Cen MT", Font.PLAIN, 32));
        heading.setForeground(Color.BLACK);
        add(heading);

        // Labels for displaying data
        createLabel("Application ID:", 20, 60);
        applicationId = createLabelWithValue(150, 60);

        createLabel("Tender ID:", 20, 90);
        tenderID = createLabelWithValue(150, 90);

        createLabel("Tender Name:", 20, 120);
        tenderName = createLabelWithValue(150, 120);

        createLabel("Budget:", 20, 150);
        budget = createLabelWithValue(150, 150);

        createLabel("Duration:", 20, 180);
        duration = createLabelWithValue(150, 180);

        createLabel("Constructor ID:", 20, 210);
        constructorId = createLabelWithValue(150, 210);

        createLabel("Constructor Name:", 20, 240);
        ConstructorName = createLabelWithValue(150, 240);

        createLabel("Work Experience:", 20, 270);
        workExperience = createLabelWithValue(150, 270);

        createLabel("Proposed Budget:", 20, 300);
        proposedBudget = createLabelWithValue(150, 300);

        createLabel("Proposed Material:", 20, 330);
        proposedMaterial = createLabelWithValue(150, 330);

        createLabel("Material:", 20, 360);
        material = createLabelWithValue(150, 360);

        // Create the database connection and fetch data
        Connect c = new Connect();
        try {
            // Construct the query using the passed t_ID
            String query = "SELECT " +
                    "ta.application_id, ta.work_experience, ta.proposed_budget, ta.material_grade, " +
                    "t.tender_id, t.tender_name, t.budget AS tender_budget, t.project_duration, " +
                    "c.constructor_id, c.company_name, c.contact_number " +
                    "FROM TenderApplications ta " +
                    "JOIN Tender t ON ta.tender_id = t.tender_id " +
                    "JOIN Contractors c ON ta.constructor_id = c.constructor_id " +
                    "WHERE ta.tender_id = " + t_ID + " " + // Use the tender_id directly in the query
                    "ORDER BY ta.proposed_budget ASC, " +
                    "CASE " +
                    "    WHEN ta.material_grade = 'A' THEN 1 " +
                    "    WHEN ta.material_grade = 'B' THEN 2 " +
                    "    WHEN ta.material_grade = 'C' THEN 3 " +
                    "    ELSE 4 " +
                    "END ASC " +
                    "LIMIT 1"; // Limit to 1 contractor

            // Execute the query using Statement (c.s)
            ResultSet rs = c.s.executeQuery(query);

            if (rs.next()) {
                // Set the values to the corresponding labels
                applicationId.setText(String.valueOf(rs.getInt("application_id")));
                tenderID.setText(String.valueOf(rs.getInt("tender_id")));
                tenderName.setText(rs.getString("tender_name"));
                budget.setText(String.valueOf(rs.getDouble("tender_budget")));
                duration.setText(rs.getInt("project_duration") + " months");
                constructorId.setText(String.valueOf(rs.getInt("constructor_id")));
                ConstructorName.setText(rs.getString("company_name"));
                workExperience.setText(String.valueOf(rs.getInt("work_experience")) + " years");
                proposedBudget.setText(String.valueOf(rs.getDouble("proposed_budget")));
                proposedMaterial.setText(rs.getString("material_grade"));
                material.setText(rs.getString("material_grade"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Window settings
        setSize(600, 450);
        setLocation(350, 130);
        setLayout(null); // using absolute layout
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Utility function to create label with text
    private JLabel createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 140, 20);
        label.setFont(new Font("Tw Cen MT", Font.BOLD, 16));
        add(label);
        return label;
    }

    // Utility function to create label without text (to be filled later)
    private JLabel createLabelWithValue(int x, int y) {
        JLabel label = new JLabel();
        label.setBounds(x, y, 150, 20);
        label.setFont(new Font("Tw Cen MT", Font.BOLD, 16));
        add(label);
        return label;
    }

    public static void main(String[] args) {
        // Pass the Tender ID (t_ID) to the constructor
        new WinnerContractor(1);  // Example with tender_id = 1
    }
}