import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Register {

    private Connection conn;

    public Register(Connection conn) {
        this.conn = conn;
        createAndShowRegisterFrame();
    }

    private void createAndShowRegisterFrame() {
        JFrame frame = new JFrame("Vendor Registration");
        frame.setSize(600, 800);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Custom panel for background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon bgIcon = new ImageIcon("out/icon/registration.png"); // Replace with your image path
                Image bgImage = bgIcon.getImage();
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        // Main panel with vertical layout
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false); // To make the background visible
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Section: Company Details
        JPanel companyDetailsPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        companyDetailsPanel.setBorder(BorderFactory.createTitledBorder("Company Details"));
        companyDetailsPanel.setOpaque(false); // Transparent panel

        JLabel companyNameLabel = new JLabel("Company Name (Required):");
        JTextField companyNameField = new JTextField();

        JLabel businessTypeLabel = new JLabel("Type of Business (Required):");
        JTextField businessTypeField = new JTextField();

        JLabel registrationNumberLabel = new JLabel("Registration Number (Required):");
        JTextField registrationNumberField = new JTextField();

        JLabel registeredAddressLabel = new JLabel("Registered Office Address (Required):");
        JTextArea registeredAddressArea = new JTextArea(2, 20);
        JScrollPane registeredAddressScroll = new JScrollPane(registeredAddressArea);

        JLabel operationalAddressLabel = new JLabel("Operational Address (Optional):");
        JTextArea operationalAddressArea = new JTextArea(2, 20);
        JScrollPane operationalAddressScroll = new JScrollPane(operationalAddressArea);

        companyDetailsPanel.add(companyNameLabel);
        companyDetailsPanel.add(companyNameField);
        companyDetailsPanel.add(businessTypeLabel);
        companyDetailsPanel.add(businessTypeField);
        companyDetailsPanel.add(registrationNumberLabel);
        companyDetailsPanel.add(registrationNumberField);
        companyDetailsPanel.add(registeredAddressLabel);
        companyDetailsPanel.add(registeredAddressScroll);
        companyDetailsPanel.add(operationalAddressLabel);
        companyDetailsPanel.add(operationalAddressScroll);

        // Section: Personal Details
        JPanel personalDetailsPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        personalDetailsPanel.setBorder(BorderFactory.createTitledBorder("Personal Details"));
        personalDetailsPanel.setOpaque(false);

        JLabel authorizedPersonLabel = new JLabel("Authorized Person Name (Required):");
        JTextField authorizedPersonField = new JTextField();

        JLabel designationLabel = new JLabel("Designation (Optional):");
        JTextField designationField = new JTextField();

        JLabel contactNumberLabel = new JLabel("Contact Number (Required):");
        JTextField contactNumberField = new JTextField();

        JLabel emailLabel = new JLabel("Email Address (Required):");
        JTextField emailField = new JTextField();

        JLabel websiteLabel = new JLabel("Official Website (Optional):");
        JTextField websiteField = new JTextField();

        personalDetailsPanel.add(authorizedPersonLabel);
        personalDetailsPanel.add(authorizedPersonField);
        personalDetailsPanel.add(designationLabel);
        personalDetailsPanel.add(designationField);
        personalDetailsPanel.add(contactNumberLabel);
        personalDetailsPanel.add(contactNumberField);
        personalDetailsPanel.add(emailLabel);
        personalDetailsPanel.add(emailField);
        personalDetailsPanel.add(websiteLabel);
        personalDetailsPanel.add(websiteField);

        // Section: Account Details (Username and Password)
        JPanel accountDetailsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        accountDetailsPanel.setBorder(BorderFactory.createTitledBorder("Account Details"));
        accountDetailsPanel.setOpaque(false);

        JLabel usernameLabel = new JLabel("Username (Required):");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password (Required):");
        JPasswordField passwordField = new JPasswordField();

        accountDetailsPanel.add(usernameLabel);
        accountDetailsPanel.add(usernameField);
        accountDetailsPanel.add(passwordLabel);
        accountDetailsPanel.add(passwordField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        JButton registerButton = new JButton("Register");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        // Add all panels to the main panel
        mainPanel.add(companyDetailsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(personalDetailsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(accountDetailsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(buttonPanel);

        // Add main panel to background panel
        backgroundPanel.add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        // Add background panel to frame
        frame.add(backgroundPanel);

        // Center frame
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Register button action listener
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String companyName = companyNameField.getText().trim();
                String businessType = businessTypeField.getText().trim();
                String registrationNumber = registrationNumberField.getText().trim();
                String registeredAddress = registeredAddressArea.getText().trim();
                String operationalAddress = operationalAddressArea.getText().trim();
                String authorizedPerson = authorizedPersonField.getText().trim();
                String designation = designationField.getText().trim();
                String contactNumber = contactNumberField.getText().trim();
                String email = emailField.getText().trim();
                String website = websiteField.getText().trim();
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if (companyName.isEmpty() || businessType.isEmpty() || registrationNumber.isEmpty() ||
                        registeredAddress.isEmpty() || authorizedPerson.isEmpty() || contactNumber.isEmpty() ||
                        email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please fill in all required fields.",
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    String sql = "INSERT INTO contractors (company_name, business_type, registration_number, " +
                            "registered_address, operational_address, authorized_person_name, designation, contact_number, " +
                            "email, website, username, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, companyName);
                    pstmt.setString(2, businessType);
                    pstmt.setString(3, registrationNumber);
                    pstmt.setString(4, registeredAddress);
                    pstmt.setString(5, operationalAddress.isEmpty() ? null : operationalAddress);
                    pstmt.setString(6, authorizedPerson);
                    pstmt.setString(7, designation.isEmpty() ? null : designation);
                    pstmt.setString(8, contactNumber);
                    pstmt.setString(9, email);
                    pstmt.setString(10, website.isEmpty() ? null : website);
                    pstmt.setString(11, username);
                    pstmt.setString(12, password);

                    pstmt.executeUpdate();

                    JOptionPane.showMessageDialog(frame, "Registration successful! Returning to login panel.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);

                    frame.dispose();

                     new Login(conn);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error during registration: " + ex.getMessage(),
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Cancel button action listener
        cancelButton.addActionListener(e -> frame.dispose());
    }

    public static void main(String[] args) {
        try {
            String DB_URL = "jdbc:mysql://localhost:3306/smart_tender";
            String DB_USER = "root";
            String DB_PASSWORD = "root";
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            new Register(conn);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error connecting to database: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
