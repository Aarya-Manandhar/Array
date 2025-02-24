import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Admin extends JFrame implements ActionListener {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/smart_tender";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private Connection conn;
    private JTable tenderTable;

    public Admin() {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error connecting to database: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        createAndShowGUI();
    }

    private void createAndShowGUI()
    {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("Admin Panel - Tender Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();

        JMenu tendersMenu = new JMenu("Tenders");

        JMenuItem createTenderItem = new JMenuItem("Create Tender");
        JMenuItem viewTendersItem = new JMenuItem("View All Tenders");
        JMenuItem viewApplicationsItem = new JMenuItem("View Applications");

        createTenderItem.addActionListener(e -> createTender());
        viewTendersItem.addActionListener(e -> viewTenders());
        viewApplicationsItem.addActionListener(e -> viewApplications());

        tendersMenu.add(createTenderItem);
        tendersMenu.add(viewTendersItem);
        tendersMenu.add(viewApplicationsItem);

        menuBar.add(tendersMenu);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        JMenu mlogout = new JMenu("LOGOUT");
        mlogout.setFont(new Font("Tw Cen MT", Font.PLAIN, 12));
        mlogout.setForeground(Color.RED);
        menuBar.add(mlogout);

        JMenuItem logout = new JMenuItem("Logout");
        logout.setFont(new Font("Tw Cen MT", Font.PLAIN, 12));
        logout.setBackground(Color.WHITE);
        logout.addActionListener(this);
        mlogout.add(logout);

        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome to the Admin Panel!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

        add(welcomePanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void createTender() {
        JFrame createFrame = new JFrame("Create Tender");
        createFrame.setSize(400, 400);
        createFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        createFrame.setLocationRelativeTo(null);
        createFrame.setLayout(new GridLayout(8, 2, 5, 5));

        JTextField nameField = new JTextField();
        JTextField experienceField = new JTextField();
        JComboBox<String> gradeDropdown = new JComboBox<>(new String[]{"Grade A", "Grade B", "Grade C"});
        JTextField descriptionField = new JTextField();
        JTextField budgetField = new JTextField();
        JTextField durationField = new JTextField();
        JComboBox<String> durationUnitDropdown = new JComboBox<>(new String[]{"Months", "Years"});

        JButton createButton = new JButton("Create Tender");

        createFrame.add(new JLabel("Tender Name:"));
        createFrame.add(nameField);

        createFrame.add(new JLabel("Description:"));
        createFrame.add(descriptionField);

        createFrame.add(new JLabel("Budget:"));
        createFrame.add(budgetField);

        createFrame.add(new JLabel("Required Experience (Years):"));
        createFrame.add(experienceField);

        createFrame.add(new JLabel("Material Grade:"));
        createFrame.add(gradeDropdown);

        createFrame.add(new JLabel("Duration:"));
        createFrame.add(durationField);

        createFrame.add(new JLabel("Duration Unit:"));
        createFrame.add(durationUnitDropdown);

        createFrame.add(new JLabel());
        createFrame.add(createButton);

        createButton.addActionListener(e -> {
            String name = nameField.getText();
            String description = descriptionField.getText();
            String budget = budgetField.getText();
            String experience = experienceField.getText();
            String grade = (String) gradeDropdown.getSelectedItem();
            String duration = durationField.getText();
            String durationUnit = (String) durationUnitDropdown.getSelectedItem();

            if (name.isEmpty() || description.isEmpty() || budget.isEmpty() || experience.isEmpty() || grade == null
                    || duration.isEmpty() || durationUnit == null) {
                JOptionPane.showMessageDialog(createFrame, "All fields are required.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String query = "INSERT INTO Tender (tender_name, tender_description, budget, required_experience, material_grade, project_duration, duration_unit, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'Open')";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setString(2, description);
                stmt.setDouble(3, Double.parseDouble(budget));
                stmt.setInt(4, Integer.parseInt(experience));
                stmt.setString(5, grade);
                stmt.setInt(6, Integer.parseInt(duration));
                stmt.setString(7, durationUnit);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(createFrame, "Tender created successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                createFrame.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(createFrame, "Error creating tender: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(createFrame, "Please enter valid numeric values for budget and duration.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        createFrame.setVisible(true);
    }

    private void viewTenders() {
        JFrame viewFrame = new JFrame("View Tenders");
        viewFrame.setSize(1000, 600);
        viewFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        viewFrame.setLocationRelativeTo(null);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Tender ID", "Name", "Description", "Budget", "Experience", "Grade", "Duration", "Status", "Action"}, 0);
        tenderTable = new JTable(model);

        try {
            String query = "SELECT * FROM Tender";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("tender_id"),
                        rs.getString("tender_name"),
                        rs.getString("tender_description"),
                        rs.getDouble("budget"),
                        rs.getInt("required_experience"),
                        rs.getString("material_grade"),
                        rs.getInt("project_duration") + " " + rs.getString("duration_unit"),
                        rs.getString("status"),
                        "Close"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(viewFrame, "Error retrieving tenders: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        tenderTable.getColumnModel().getColumn(8).setCellRenderer(new TableCellRenderer() {
            private final JButton button = new JButton("Close");

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                String status = (String) table.getValueAt(row, 7);
                button.setEnabled("Open".equals(status));
                return button;
            }
        });

        tenderTable.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private final JButton button = new JButton("Close");
            private int row;

            {
                button.addActionListener(e -> {
                    int tenderId = (int) tenderTable.getValueAt(row, 0);
                    try {
                        String query = "UPDATE Tender SET status = 'Closed' WHERE tender_id = ?";
                        PreparedStatement stmt = conn.prepareStatement(query);
                        stmt.setInt(1, tenderId);
                        stmt.executeUpdate();
                        tenderTable.setValueAt("Closed", row, 7);
                        JOptionPane.showMessageDialog(viewFrame, "Tender closed successfully!", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    new WinnerContractor(tenderId);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(viewFrame, "Error closing tender: " + ex.getMessage(),
                                "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                this.row = row;
                return button;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tenderTable);
        viewFrame.add(scrollPane, BorderLayout.CENTER);

        viewFrame.setVisible(true);
    }

    private void viewApplications() {
        JFrame viewFrame = new JFrame("View Applications");
        viewFrame.setSize(1000, 600);
        viewFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        viewFrame.setLocationRelativeTo(null);

        // Define table model with detailed columns
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Application ID", "Tender ID", "Company Name", "Applicant Name", "Applicant Contact", "Bid Amount"}, 0);
        JTable applicationTable = new JTable(model);

        try {
            // Updated query to reflect new column names
            String query = "SELECT a.application_id, a.tender_id, c.company_name AS name_of_company, " +
                    "c.authorized_person_name AS applicant_name, c.contact_number AS applicant_contact, " +
                    "a.proposed_budget AS bid_amount " +
                    "FROM TenderApplications a " +
                    "JOIN Contractors c ON a.constructor_id = c.constructor_id";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Add data to the table
                model.addRow(new Object[]{
                        rs.getInt("application_id"),
                        rs.getInt("tender_id"),
                        rs.getString("name_of_company"),
                        rs.getString("applicant_name"),
                        rs.getString("applicant_contact"),
                        rs.getDouble("bid_amount")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(viewFrame, "Error retrieving applications: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        JScrollPane scrollPane = new JScrollPane(applicationTable);
        viewFrame.add(scrollPane, BorderLayout.CENTER);

        viewFrame.setVisible(true);
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this, "Admin Panel\nVersion 1.0\nDeveloped by Aarya Manandhar", "About", JOptionPane.INFORMATION_MESSAGE);
    }
    public void actionPerformed(ActionEvent ae){
        String text = ae.getActionCommand();
 if(text.equals("Logout")){
            setVisible(false);
            new Login(conn);
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Admin::new);
    }
}
