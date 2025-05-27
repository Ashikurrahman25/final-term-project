package view;

import model.*;
import service.*;
import util.Validator;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;


public class LoginFrame extends JFrame {
    private UserService userService;
    private FlightService flightService;
    private BookingService bookingService;

    public LoginFrame() {
        setTitle("Flight Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        userService = new UserService();
        flightService = new FlightService();
        bookingService = new BookingService(flightService, userService);

        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Header Panel with project info
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setPreferredSize(new Dimension(0, 120));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Project title
        JLabel titleLabel = new JLabel("FLIGHT MANAGEMENT SYSTEM", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        // Slogan
        JLabel sloganLabel = new JLabel("Your Gateway to Seamless Aviation Management", JLabel.CENTER);
        sloganLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        sloganLabel.setForeground(new Color(220, 235, 255));

        // Header content panel
        JPanel headerContent = new JPanel(new BorderLayout());
        headerContent.setOpaque(false);
        headerContent.add(titleLabel, BorderLayout.NORTH);
        headerContent.add(sloganLabel, BorderLayout.CENTER);

        headerPanel.add(headerContent, BorderLayout.CENTER);

        // Main Panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        // Project summary card
        JPanel summaryCard = createProjectSummaryCard();
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(10, 0, 15, 0);
        mainPanel.add(summaryCard, gbc);

        // Reset insets for buttons
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.NONE;

        // Customer Login Button
        JButton customerBtn = createStyledMainButton("Customer Login", new Color(72, 201, 176));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(customerBtn, gbc);

        // Customer Register Button
        JButton registerBtn = createStyledMainButton("Register as Customer", new Color(130, 88, 159));
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(registerBtn, gbc);

        // Browse Flights Button
        JButton browseBtn = createStyledMainButton("Browse Flights", new Color(52, 152, 219));
        gbc.gridx = 2; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(browseBtn, gbc);

        // Ticket Validation Button
        JButton validateBtn = createStyledMainButton("Validate Ticket", new Color(230, 126, 34));
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(validateBtn, gbc);

        // Admin Login Button
        JButton adminBtn = createStyledMainButton("Admin Login", new Color(255, 87, 87));
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(adminBtn, gbc);

        // Super Admin Login Button
        JButton superAdminBtn = createStyledMainButton("Super Admin Login", new Color(139, 0, 0));
        gbc.gridx = 2; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(superAdminBtn, gbc);

        // Event Listeners
        customerBtn.addActionListener(e -> openCustomerLogin());
        adminBtn.addActionListener(e -> openAdminLogin());
        superAdminBtn.addActionListener(e -> openSuperAdminLogin());
        registerBtn.addActionListener(e -> openCustomerRegistration());
        browseBtn.addActionListener(e -> openFlightBrowser());
        validateBtn.addActionListener(e -> openTicketValidation());

        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JButton createStyledMainButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 60));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true); // Required for Mac compatibility
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private JButton createStyledDialogButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 40));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true); // Required for Mac compatibility
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setPreferredSize(new Dimension(200, 35));
        return field;
    }

    private JPasswordField createStyledPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setPreferredSize(new Dimension(200, 35));
        return field;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Arial", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setForeground(Color.BLACK);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        combo.setPreferredSize(new Dimension(200, 35));
        return combo;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(60, 60, 60));
        return label;
    }

    private void openCustomerLogin() {
        JDialog loginDialog = new JDialog(this, "Customer Login", true);
        loginDialog.setSize(450, 350);
        loginDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);

        JTextField emailField = createStyledTextField(20);
        JPasswordField passwordField = createStyledPasswordField(20);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createStyledLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createStyledLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JButton loginBtn = createStyledDialogButton("Login", new Color(76, 175, 80));
        JButton cancelBtn = createStyledDialogButton("Cancel", new Color(244, 67, 54));

        // Button panel for better alignment
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.add(loginBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);

        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginDialog, "Please fill all fields!");
                return;
            }

            Customer customer = userService.authenticateCustomer(email, password);
            if (customer != null) {
                loginDialog.dispose();
                openCustomerDashboard(customer);
            } else {
                JOptionPane.showMessageDialog(loginDialog, "Invalid credentials!");
            }
        });

        cancelBtn.addActionListener(e -> loginDialog.dispose());

        loginDialog.add(panel);
        loginDialog.setVisible(true);
    }

    private void openAdminLogin() {
        JDialog loginDialog = new JDialog(this, "Admin Login", true);
        loginDialog.setSize(450, 350);
        loginDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);

        JTextField emailField = createStyledTextField(20);
        JPasswordField passwordField = createStyledPasswordField(20);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createStyledLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createStyledLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JButton loginBtn = createStyledDialogButton("Login", new Color(76, 175, 80));
        JButton cancelBtn = createStyledDialogButton("Cancel", new Color(244, 67, 54));

        // Button panel for better alignment
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.add(loginBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);

        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginDialog, "Please fill all fields!");
                return;
            }

            Admin admin = userService.authenticateAdmin(email, password);
            if (admin != null && !admin.isSuperAdmin()) {
                loginDialog.dispose();
                openAdminDashboard(admin);
            } else if (admin != null && admin.isSuperAdmin()) {
                JOptionPane.showMessageDialog(loginDialog, 
                    "You have Super Admin privileges!\nPlease use 'Super Admin Login' for full access.");
            } else {
                JOptionPane.showMessageDialog(loginDialog, "Invalid credentials!");
            }
        });

        cancelBtn.addActionListener(e -> loginDialog.dispose());

        loginDialog.add(panel);
        loginDialog.setVisible(true);
    }

    private void openSuperAdminLogin() {
        JDialog loginDialog = new JDialog(this, "Super Admin Login", true);
        loginDialog.setSize(450, 350);
        loginDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);

        // Add a header label
        JLabel headerLabel = new JLabel("Super Admin Access Only");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setForeground(new Color(139, 0, 0));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(headerLabel, gbc);

        JTextField emailField = createStyledTextField(20);
        JPasswordField passwordField = createStyledPasswordField(20);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createStyledLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createStyledLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JButton loginBtn = createStyledDialogButton("Login as Super Admin", new Color(139, 0, 0));
        loginBtn.setPreferredSize(new Dimension(160, 40)); // Make button wider for longer text
        JButton cancelBtn = createStyledDialogButton("Cancel", new Color(244, 67, 54));

        // Button panel for better alignment
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.add(loginBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);

        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginDialog, "Please fill all fields!");
                return;
            }

            Admin admin = userService.authenticateAdmin(email, password);
            if (admin != null && admin.isSuperAdmin()) {
                loginDialog.dispose();
                openSuperAdminDashboard(admin);
            } else if (admin != null && !admin.isSuperAdmin()) {
                JOptionPane.showMessageDialog(loginDialog, 
                    "Access Denied! You don't have Super Admin privileges.\nPlease use 'Admin Login' instead.");
            } else {
                JOptionPane.showMessageDialog(loginDialog, "Invalid credentials!");
            }
        });

        cancelBtn.addActionListener(e -> loginDialog.dispose());

        loginDialog.add(panel);
        loginDialog.setVisible(true);
    }

    private void openCustomerRegistration() {
        JDialog regDialog = new JDialog(this, "Customer Registration", true);
        regDialog.setSize(550, 500);
        regDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);

        JTextField nameField = createStyledTextField(20);
        JTextField phoneField = createStyledTextField(20);
        JTextField emailField = createStyledTextField(20);
        JComboBox<String> genderCombo = createStyledComboBox(new String[]{"Male", "Female", "Other"});
        JPasswordField passwordField = createStyledPasswordField(20);
        JPasswordField confirmPasswordField = createStyledPasswordField(20);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createStyledLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createStyledLabel("Phone:"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createStyledLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(createStyledLabel("Gender:"), gbc);
        gbc.gridx = 1;
        panel.add(genderCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(createStyledLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(createStyledLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);

        JButton registerBtn = createStyledDialogButton("Register", new Color(76, 175, 80));
        JButton cancelBtn = createStyledDialogButton("Cancel", new Color(244, 67, 54));

        // Button panel for better alignment
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.add(registerBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);

        registerBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String gender = (String) genderCombo.getSelectedItem();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            // Validation
            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(regDialog, "Please fill all fields!");
                return;
            }

            if (!Validator.isValidEmail(email)) {
                JOptionPane.showMessageDialog(regDialog, "Please enter a valid email!");
                return;
            }

            if (!Validator.isValidPhone(phone)) {
                JOptionPane.showMessageDialog(regDialog, "Please enter a valid phone number!");
                return;
            }

            if (password.length() < 6) {
                JOptionPane.showMessageDialog(regDialog, "Password must be at least 6 characters!");
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(regDialog, "Passwords do not match!");
                return;
            }

            if (userService.registerCustomer(name, phone, email, gender, password)) {
                JOptionPane.showMessageDialog(regDialog, "Registration successful! You can now login.");
                regDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(regDialog, "Email already exists!");
            }
        });

        cancelBtn.addActionListener(e -> regDialog.dispose());

        regDialog.add(panel);
        regDialog.setVisible(true);
    }

    private void openFlightBrowser() {
        new FlightBrowserFrame(flightService, null).setVisible(true);
    }

    private void openTicketValidation() {
        JDialog validationDialog = new JDialog(this, "Ticket Validation Service", true);
        validationDialog.setSize(850, 750);
        validationDialog.setLocationRelativeTo(this);
        validationDialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header Panel with gradient-like effect
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        
        JPanel headerContent = new JPanel(new GridLayout(2, 1, 0, 5));
        headerContent.setBackground(new Color(52, 152, 219));
        
        JLabel titleLabel = new JLabel("TICKET VALIDATION SERVICE");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JLabel subtitleLabel = new JLabel("Verify your booking with PNR code");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(220, 235, 255));
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        headerContent.add(titleLabel);
        headerContent.add(subtitleLabel);
        headerPanel.add(headerContent, BorderLayout.CENTER);

        // Main Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Input Panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // PNR Input Section
        JLabel pnrLabel = new JLabel("Enter PNR Code:");
        pnrLabel.setFont(new Font("Arial", Font.BOLD, 14));
        pnrLabel.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        inputPanel.add(pnrLabel, gbc);

        JTextField pnrField = new JTextField(15);
        pnrField.setFont(new Font("Arial", Font.PLAIN, 14));
        pnrField.setPreferredSize(new Dimension(200, 35));
        pnrField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        pnrField.setHorizontalAlignment(JTextField.CENTER);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        inputPanel.add(pnrField, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton validateBtn = createStyledDialogButton("Validate", new Color(46, 204, 113));
        JButton clearBtn = createStyledDialogButton("Clear", new Color(149, 165, 166));
        JButton closeBtn = createStyledDialogButton("Close", new Color(231, 76, 60));
        
        buttonPanel.add(validateBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(closeBtn);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 8, 8);
        inputPanel.add(buttonPanel, gbc);

        // Result Panel - Non-scrollable
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBackground(Color.WHITE);
        resultPanel.setPreferredSize(new Dimension(0, 500));
        resultPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel resultContentPanel = new JPanel();
        resultContentPanel.setLayout(new BoxLayout(resultContentPanel, BoxLayout.Y_AXIS));
        resultContentPanel.setBackground(Color.WHITE);
        
        // Initial message
        JLabel initialLabel = new JLabel("Enter a PNR code above to validate your ticket", JLabel.CENTER);
        initialLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        initialLabel.setForeground(new Color(127, 140, 141));
        initialLabel.setBorder(BorderFactory.createEmptyBorder(100, 20, 100, 20));
        resultContentPanel.add(initialLabel);
        
        resultPanel.add(resultContentPanel, BorderLayout.CENTER);

        // Event Handlers
        validateBtn.addActionListener(e -> {
            String pnr = pnrField.getText().trim().toUpperCase();
            if (pnr.isEmpty()) {
                JOptionPane.showMessageDialog(validationDialog, 
                    "Please enter a PNR code!", 
                    "Input Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            resultContentPanel.removeAll();
            Ticket ticket = bookingService.getTicketByPNR(pnr);
            
            if (ticket != null && "PURCHASED".equals(ticket.getStatus())) {
                // Valid ticket found
                Flight flight = flightService.getFlightById(ticket.getFlightId());
                Route route = flight != null ? flightService.getRouteById(flight.getRouteId()) : null;

                // Success header
                JPanel successHeader = createValidationResultHeader("VALID TICKET", 
                    "Your ticket is confirmed and valid", new Color(46, 204, 113));
                resultContentPanel.add(successHeader);
                
                resultContentPanel.add(Box.createVerticalStrut(10));

                // Ticket details in grid format
                if (flight != null && route != null) {
                    JPanel gridPanel = new JPanel(new GridLayout(2, 2, 10, 10));
                    gridPanel.setBackground(Color.WHITE);
                    gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    
                    // Booking Information
                    gridPanel.add(createValidationInfoCard("Booking Information", 
                        String.format("PNR: %s\nTicket ID: %s\nStatus: %s\nPurchase Date: %s",
                            ticket.getPnr(),
                            ticket.getId(),
                            ticket.getStatus(),
                            ticket.getPurchaseTime().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
                        ), new Color(52, 152, 219)));
                    
                    // Flight Information
                    gridPanel.add(createValidationInfoCard("Flight Information", 
                        String.format("Flight: %s\nRoute: %s → %s\nDeparture: %s\nArrival: %s",
                            flight.getFlightNumber(),
                            route.getDeparture(),
                            route.getArrival(),
                            flight.getDepartureTime().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")),
                            flight.getArrivalTime().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
                        ), new Color(155, 89, 182)));
                    
                    // Seat information
                    String seatInfo;
                    if (ticket.getSeatNumbers() != null && !ticket.getSeatNumbers().isEmpty()) {
                        if (ticket.getSeatNumbers().size() > 1) {
                            seatInfo = String.format("Seats: %s\nTotal Seats: %d\nTotal Price: $%.2f", 
                                ticket.getFormattedSeatNumbers(), 
                                ticket.getSeatCount(),
                                ticket.getTotalPrice());
                        } else {
                            seatInfo = String.format("Seat: %s\nPrice: $%.2f", 
                                ticket.getFormattedSeatNumbers(),
                                ticket.getTotalPrice());
                        }
                    } else {
                        String legacySeat = ticket.getSeatNumber();
                        seatInfo = String.format("Seat: %s\nPrice: $%.2f", 
                            (legacySeat != null && !legacySeat.isEmpty()) ? legacySeat : "No seat assigned",
                            ticket.getTotalPrice());
                    }
                    
                    gridPanel.add(createValidationInfoCard("Seat & Payment", seatInfo, new Color(230, 126, 34)));
                    
                    // Gate information
                    String gateInfo = "Gate: TBD\nTerminal: TBD\nStatus: Check-in not yet available";
                    if (ticket.getGateNumber() != null && !ticket.getGateNumber().isEmpty()) {
                        gateInfo = String.format("Gate: %s\nTerminal: %s\nStatus: Ready for check-in", 
                            ticket.getGateNumber(),
                            ticket.getTerminal() != null ? ticket.getTerminal() : "TBD");
                    }
                    
                    gridPanel.add(createValidationInfoCard("Gate Information", gateInfo, new Color(52, 73, 94)));
                    
                    resultContentPanel.add(gridPanel);
                }
                
            } else if (ticket != null) {
                // Ticket exists but cancelled
                JPanel errorHeader = createValidationResultHeader("TICKET CANCELLED", 
                    "This ticket has been cancelled", new Color(231, 76, 60));
                resultContentPanel.add(errorHeader);
                
                resultContentPanel.add(Box.createVerticalStrut(8));
                
                resultContentPanel.add(createValidationInfoCard("Ticket Information", 
                    String.format("PNR: %s\nStatus: %s\nOriginal Price: $%.2f",
                        ticket.getPnr(),
                        ticket.getStatus(),
                        ticket.getTotalPrice()
                    ), new Color(231, 76, 60)));
                
            } else {
                // Ticket not found
                JPanel errorHeader = createValidationResultHeader("TICKET NOT FOUND", 
                    "No ticket found with this PNR code", new Color(231, 76, 60));
                resultContentPanel.add(errorHeader);
                
                resultContentPanel.add(Box.createVerticalStrut(8));
                
                JLabel helpLabel = new JLabel("<html><div style='text-align: center;'>" +
                    "Please check your PNR code and try again.<br/>" +
                    "PNR codes are 6-character alphanumeric codes<br/>" +
                    "provided when you purchase your ticket." +
                    "</div></html>", JLabel.CENTER);
                helpLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                helpLabel.setForeground(new Color(127, 140, 141));
                helpLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                resultContentPanel.add(helpLabel);
            }

            resultContentPanel.revalidate();
            resultContentPanel.repaint();
        });

        clearBtn.addActionListener(e -> {
            pnrField.setText("");
            resultContentPanel.removeAll();
            JLabel clearLabel = new JLabel("Enter a PNR code above to validate your ticket", JLabel.CENTER);
            clearLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            clearLabel.setForeground(new Color(127, 140, 141));
            clearLabel.setBorder(BorderFactory.createEmptyBorder(100, 20, 100, 20));
            resultContentPanel.add(clearLabel);
            resultContentPanel.revalidate();
            resultContentPanel.repaint();
        });

        closeBtn.addActionListener(e -> validationDialog.dispose());

        // Allow Enter key to trigger validation
        pnrField.addActionListener(e -> validateBtn.doClick());

        contentPanel.add(inputPanel, BorderLayout.NORTH);
        contentPanel.add(resultPanel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        validationDialog.add(mainPanel);
        validationDialog.setVisible(true);
    }

    private JPanel createValidationResultHeader(String title, String subtitle, Color color) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(color);
        
        JLabel subtitleLabel = new JLabel(subtitle, JLabel.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(127, 140, 141));
        
        header.add(titleLabel, BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.CENTER);
        
        return header;
    }

    private JPanel createValidationInfoCard(String title, String content, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentColor, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(accentColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JTextArea contentArea = new JTextArea(content);
        contentArea.setFont(new Font("Arial", Font.PLAIN, 12));
        contentArea.setForeground(new Color(52, 73, 94));
        contentArea.setBackground(Color.WHITE);
        contentArea.setEditable(false);
        contentArea.setOpaque(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(contentArea, BorderLayout.CENTER);
        
        return card;
    }

    private JPanel createProjectSummaryCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        card.setPreferredSize(new Dimension(660, 160)); // Increased height for more content
        
        JLabel titleLabel = new JLabel("PROJECT OVERVIEW");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(70, 130, 180));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        
        JTextArea contentArea = new JTextArea(
            "This comprehensive Flight Management System provides end-to-end airline operations management. " +
            "Features include customer booking with seat selection, real-time flight status tracking, ticket validation services, " +
            "administrative controls for flight and aircraft management, gate assignments, and multi-level user authentication."
        );
        contentArea.setFont(new Font("Arial", Font.PLAIN, 12));
        contentArea.setForeground(new Color(52, 73, 94));
        contentArea.setBackground(Color.WHITE);
        contentArea.setEditable(false);
        contentArea.setOpaque(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        
        // Academic details section
        JPanel detailsPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        
        JLabel contributorsLabel = new JLabel("CONTRIBUTORS: ASHIKUR RAHMAN, SYED RAHAT HOSSAIN");
        contributorsLabel.setFont(new Font("Arial", Font.BOLD, 12));
        contributorsLabel.setForeground(new Color(70, 130, 180));
        
        JLabel academicLabel = new JLabel("COURSE: OBJECT ORIENTED PROGRAMMING | INSTRUCTOR: ANIK KUMAR SAHA | SECTION: Q");
        academicLabel.setFont(new Font("Arial", Font.BOLD, 12));
        academicLabel.setForeground(new Color(70, 130, 180));
        
        detailsPanel.add(contributorsLabel);
        detailsPanel.add(academicLabel);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(contentArea, BorderLayout.CENTER);
        card.add(detailsPanel, BorderLayout.SOUTH);
        
        return card;
    }

    private void openCustomerDashboard(Customer customer) {
        new CustomerDashboardFrame(customer, flightService, bookingService).setVisible(true);
        dispose();
    }

    private void openAdminDashboard(Admin admin) {
        new AdminDashboardFrame(admin, userService, flightService, bookingService).setVisible(true);
        dispose();
    }

    private void openSuperAdminDashboard(Admin admin) {
        new SuperAdminDashboardFrame(admin, userService, flightService, bookingService).setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}