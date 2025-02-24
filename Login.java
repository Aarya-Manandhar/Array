import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Login {

    private JFrame frame;
    private JPanel formPanel;
    private JComboBox<String> accountTypeComboBox;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel usernameLabel;
    private final Connection conn;
    JButton loginButton;
    JButton registerButton;

    public Login(Connection conn) {
        this.conn = conn;
        Frame();
    }

    private void Frame() {
        frame = new JFrame("Login Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout(10, 10));

        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        JLabel accountTypeLabel = new JLabel("Account Type:");
        String[] loginTypes = { "Contractor", "Admin" };
        accountTypeComboBox = new JComboBox<>(loginTypes);
        accountTypeComboBox.setSelectedIndex(0);

        // Username and password fields
        usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(15);

        // Layout setup
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(accountTypeLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(accountTypeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(loginButton, gbc);

        gbc.gridx = 1;
        formPanel.add(registerButton, gbc);

        // Add action listeners
        loginButton.addActionListener(e -> handleLogin());
        accountTypeComboBox.addActionListener(e -> updateFormForAccountType());
        registerButton.addActionListener(e -> handleRegister());

        frame.add(formPanel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Initially update form for the selected account type
        updateFormForAccountType();
    }

    private void updateFormForAccountType() {
        String selectedType = (String) accountTypeComboBox.getSelectedItem();

        if (selectedType.equals("Admin")) {
            usernameLabel.setVisible(false);
            usernameField.setVisible(false);
            registerButton.setVisible(false);

        } else {
            usernameLabel.setVisible(true);
            usernameField.setVisible(true);
            registerButton.setVisible(true);
        }

        formPanel.revalidate();
        formPanel.repaint();
    }

    private void handleLogin() {
        String selectedType = (String) accountTypeComboBox.getSelectedItem();
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter the password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selectedType.equals("Admin")) {
            // Admin login logic
            if (password.equals("admin123")) { // Example password
                JOptionPane.showMessageDialog(frame, "Admin Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                new Admin();
                // new AdminPanel(); // Navigate to Admin panel (example)
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Admin password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (selectedType.equals("Contractor")) {
            // Contractor login logic
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter the username", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                String query = "SELECT * FROM contractors WHERE username = ? AND password = ?";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, username);
                pst.setString(2, password);

                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    int contractorId = rs.getInt("constructor_id");  // Retrieve the contractor ID

                    JOptionPane.showMessageDialog(frame, "Contractor Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                    new Contractor(contractorId);  // Pass contractorId to the Contractor constructor
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid Contractor credentials", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleRegister() {
        String selectedType = (String) accountTypeComboBox.getSelectedItem();

        if (selectedType.equals("Contractor")) {
            // Open Register window for Contractor
            new Register(conn);
            frame.dispose();  // Close the login window
        } else {
            JOptionPane.showMessageDialog(frame, "Admin cannot register. Please contact support.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            // Connect to the database
            String DB_URL = "jdbc:mysql://localhost:3306/smart_tender";
            String DB_USER = "root";
            String DB_PASSWORD = "root";
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Launch the login window
            SwingUtilities.invokeLater(() -> new Login(conn));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + ex.getMessage());
        }
    }
}
