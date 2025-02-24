import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Contractor extends JFrame implements ActionListener {

    private Connect c;
    private Connection conn;
    private JFrame contractorFrame;
    private JPanel mainPanel;
    int contractor_id;

    public Contractor(int contractor_id) {
        this.contractor_id = contractor_id;
        c = new Connect(); // Establish database connection
        initializeContractorPanel();
    }

    public void initializeContractorPanel()
    {
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        contractorFrame = new JFrame("Contractor Panel");
        contractorFrame.setSize(800, 600);
        contractorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add menu bar
        JMenuBar menuBar = new JMenuBar();

        // Dashboard Menu
        JMenu dashboardMenu = new JMenu("Dashboard");
        JMenuItem viewProfileItem = new JMenuItem("View Profile");
        JMenuItem viewAvailableTendersItem = new JMenuItem("View Available Tenders");

        viewProfileItem.addActionListener(e -> showProfile());
        viewAvailableTendersItem.addActionListener(e -> showAvailableTenders());

        dashboardMenu.add(viewProfileItem);
        dashboardMenu.add(viewAvailableTendersItem);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);

        menuBar.add(dashboardMenu);
        menuBar.add(helpMenu);

        // Logout Menu
        JMenu mlogout = new JMenu("LOGOUT");
        mlogout.setFont(new Font("Tw Cen MT", Font.PLAIN, 12));
        mlogout.setForeground(Color.RED);
        menuBar.add(mlogout);

        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setFont(new Font("Tw Cen MT", Font.PLAIN, 12));
        logoutItem.setBackground(Color.WHITE);
        logoutItem.addActionListener(this);
        mlogout.add(logoutItem);

        contractorFrame.setJMenuBar(menuBar);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        // Main Panel
        mainPanel = new JPanel(new BorderLayout());
        JLabel headingLabel = new JLabel("Contractor Dashboard", SwingConstants.CENTER);
        headingLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(headingLabel);

        contractorFrame.add(mainPanel);
        contractorFrame.setVisible(true);
    }

    private void showAvailableTenders() {
        JFrame tenderFrame = new JFrame("Available Tenders");
        tenderFrame.setSize(800, 600);
        tenderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        DefaultTableModel model = new DefaultTableModel(new Object[]{"Tender ID", "Name", "Budget", "Description", "Action"}, 0);
        JTable tenderTable = new JTable(model);

        try {
            String query = "SELECT * FROM Tender WHERE status = 'Open'";
            ResultSet rs = c.s.executeQuery(query);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("tender_id"),
                        rs.getString("tender_name"),
                        rs.getBigDecimal("budget"),
                        rs.getString("tender_description"),
                        "Apply"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(tenderFrame, "Error retrieving tenders: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        tenderTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        tenderTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), tenderTable));

        JScrollPane scrollPane = new JScrollPane(tenderTable);
        tenderFrame.add(scrollPane);

        tenderFrame.setVisible(true);
    }

    private void showProfile() {
        JFrame profileFrame = new JFrame("Contractor Profile");
        profileFrame.setSize(400, 500);
        profileFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        profileFrame.setLocationRelativeTo(null);

        JPanel profilePanel = new JPanel(new GridLayout(10, 2, 10, 10));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel nameLabel = new JLabel("Company Name:");
        JLabel nameValue = new JLabel();
        JLabel authorizedPersonLabel = new JLabel("Authorized Person:");
        JLabel authorizedPersonValue = new JLabel();
        JLabel contactLabel = new JLabel("Contact:");
        JLabel contactValue = new JLabel();
        JLabel addressLabel = new JLabel("Address:");
        JLabel addressValue = new JLabel();
        JLabel emailLabel = new JLabel("Email:");
        JLabel emailValue = new JLabel();
        JLabel appliedTendersLabel = new JLabel("Applied Tenders:");
        JLabel appliedTendersValue = new JLabel();

        profilePanel.add(nameLabel);
        profilePanel.add(nameValue);
        profilePanel.add(authorizedPersonLabel);
        profilePanel.add(authorizedPersonValue);
        profilePanel.add(contactLabel);
        profilePanel.add(contactValue);
        profilePanel.add(addressLabel);
        profilePanel.add(addressValue);
        profilePanel.add(emailLabel);
        profilePanel.add(emailValue);
        profilePanel.add(appliedTendersLabel);
        profilePanel.add(appliedTendersValue);

        profileFrame.add(profilePanel);

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/smart_tender", "root", "root");
            Statement stmt = conn.createStatement();

            // Query to get contractor's profile details
            String profileQuery = "SELECT company_name, authorized_person_name, contact_number, registered_address, email " +
                    "FROM Contractors WHERE constructor_id = " + this.contractor_id;
            ResultSet rs = stmt.executeQuery(profileQuery);

            if (rs.next()) {
                nameValue.setText(rs.getString("company_name"));
                authorizedPersonValue.setText(rs.getString("authorized_person_name"));
                contactValue.setText(rs.getString("contact_number"));
                addressValue.setText(rs.getString("registered_address"));
                emailValue.setText(rs.getString("email"));
            } else {
                JOptionPane.showMessageDialog(profileFrame, "No data found for the given ID.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            }

            // Query to count how many tenders the contractor has applied for
            String appliedTendersQuery = "SELECT COUNT(*) AS applied_count FROM TenderApplications WHERE constructor_id = " + this.contractor_id;
            ResultSet appliedTendersRs = stmt.executeQuery(appliedTendersQuery);

            if (appliedTendersRs.next()) {
                int appliedCount = appliedTendersRs.getInt("applied_count");
                appliedTendersValue.setText(String.valueOf(appliedCount));
            } else {
                appliedTendersValue.setText("0");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        profileFrame.setVisible(true);
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(contractorFrame, "Contractor Panel\nVersion 1.0\nDeveloped by Aarya Manandhar",
                "About", JOptionPane.INFORMATION_MESSAGE);
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            setText("Apply");
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private JTable table;

        public ButtonEditor(JCheckBox checkBox, JTable table) {
            super(checkBox);
            this.table = table;
            button = new JButton("Apply");
            button.setOpaque(true);

            button.addActionListener(e -> {
                int row = table.getSelectedRow();
                int tenderId = (int) table.getValueAt(row, 0);
                applyForTender(tenderId);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Apply";
        }
    }

    private void applyForTender(int tenderId) {
        JFrame applyFrame = new JFrame("Apply for Tender");
        applyFrame.setSize(400, 300);
        applyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel applyPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        applyPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField workExperienceField = new JTextField();
        JTextField proposedBudgetField = new JTextField();
        JTextField materialGradeField = new JTextField();

        applyPanel.add(new JLabel("Work Experience (in years):"));
        applyPanel.add(workExperienceField);
        applyPanel.add(new JLabel("Proposed Budget:"));
        applyPanel.add(proposedBudgetField);
        applyPanel.add(new JLabel("Material Grade:"));
        applyPanel.add(materialGradeField);

        JButton submitButton = new JButton("Submit");
        applyPanel.add(new JLabel()); // Empty label for alignment
        applyPanel.add(submitButton);

        applyFrame.add(applyPanel);
        applyFrame.setLocationRelativeTo(null);
        applyFrame.setVisible(true);

        submitButton.addActionListener(e -> {
            String workExperience = workExperienceField.getText();
            String proposedBudget = proposedBudgetField.getText();
            String materialGrade = materialGradeField.getText();

            if (workExperience.isEmpty() || proposedBudget.isEmpty() || materialGrade.isEmpty()) {
                JOptionPane.showMessageDialog(applyFrame, "All fields are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int contractorId = 1; // Assuming contractor is logged in with ID 1
                int workExpInt = Integer.parseInt(workExperience);
                double proposedBudgetDouble = Double.parseDouble(proposedBudget);

                // Insert data into the TenderApplications table
                String query = String.format(
                        "INSERT INTO TenderApplications (tender_id, constructor_id, work_experience, proposed_budget, material_grade) " +
                                "VALUES (%d, %d, %d, %f, '%s')",
                        tenderId, this.contractor_id, workExpInt, proposedBudgetDouble, materialGrade
                );
                c.s.executeUpdate(query);

                JOptionPane.showMessageDialog(applyFrame, "Application submitted successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                applyFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(applyFrame, "Work experience and budget must be numeric.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(applyFrame, "Error: " + ex.getMessage(), "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String text = ae.getActionCommand();
        if (text.equals("Logout")) {
            contractorFrame.setVisible(false);
         //
            new Login(conn);
        }
    }

    public static void main(String[] args) {
        new Contractor(1); // assuming the contractor's ID is 1
    }
}
