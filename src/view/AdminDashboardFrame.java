package view;

import model.*;
import service.*;
import util.ImageUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminDashboardFrame extends JFrame {
    private Admin admin;
    private UserService userService;
    private FlightService flightService;
    private BookingService bookingService;
    private JTabbedPane tabbedPane;
    
    // Statistics panels for refresh functionality
    private JPanel flightStatsCard;
    private JPanel customerStatsCard;
    private JPanel aircraftStatsCard;
    private JPanel ticketStatsCard;

    public AdminDashboardFrame(Admin admin, UserService userService, FlightService flightService, BookingService bookingService) {
        this.admin = admin;
        this.userService = userService;
        this.flightService = flightService;
        this.bookingService = bookingService;
        
        setTitle("Admin Dashboard - " + admin.getName());
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(220, 20, 60));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        JLabel titleLabel = new JLabel("Admin Dashboard - " + admin.getName(), JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Flight Management", createFlightManagementPanel());
        tabbedPane.addTab("Aircraft Management", createAircraftManagementPanel());
        tabbedPane.addTab("Route Management", createRouteManagementPanel());
        tabbedPane.addTab("Gate Management", createGateManagementPanel());
        tabbedPane.addTab("Terminal Management", createTerminalManagementPanel());
        tabbedPane.addTab("Customer Management", createCustomerManagementPanel());
        tabbedPane.addTab("Reports", createReportsPanel());

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu accountMenu = new JMenu("Account");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> logout());
        accountMenu.add(logoutItem);
        menuBar.add(accountMenu);
        setJMenuBar(menuBar);

        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createFlightManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Flight table
        String[] columns = {"Flight Number", "Route", "Aircraft", "Departure", "Arrival", "Price", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable flightTable = new JTable(model);
        styleTable(flightTable);
        JScrollPane scrollPane = new JScrollPane(flightTable);
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton addBtn = createStyledButton("Add Flight", new Color(76, 175, 80));
        JButton editBtn = createStyledButton("Edit Flight", new Color(255, 193, 7));
        JButton deleteBtn = createStyledButton("Delete Flight", new Color(244, 67, 54));
        JButton statusBtn = createStyledButton("Update Status", new Color(33, 150, 243));
        JButton refreshBtn = createStyledButton("Refresh", new Color(158, 158, 158));

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(statusBtn);
        buttonPanel.add(refreshBtn);

        // Load flights
        loadFlights(model);

        // Event listeners
        refreshBtn.addActionListener(e -> loadFlights(model));
        addBtn.addActionListener(e -> showAddFlightDialog(model));
        
        // Add double-click listener to show flight details
        flightTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int selectedRow = flightTable.getSelectedRow();
                    if (selectedRow != -1) {
                        String flightNumber = (String) model.getValueAt(selectedRow, 0);
                        Flight flight = flightService.getFlightByNumber(flightNumber);
                        if (flight != null) {
                            showFlightDetailsDialog(flight);
                        }
                    }
                }
            }
        });
        
        editBtn.addActionListener(e -> {
            int selectedRow = flightTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a flight to edit!");
                return;
            }
            String flightNumber = (String) model.getValueAt(selectedRow, 0);
            Flight flight = flightService.getFlightByNumber(flightNumber);
            if (flight != null) {
                showEditFlightDialog(flight, model);
            }
        });

        deleteBtn.addActionListener(e -> {
            int selectedRow = flightTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a flight to delete!");
                return;
            }
            
            String flightNumber = (String) model.getValueAt(selectedRow, 0);
            Flight flight = flightService.getFlightByNumber(flightNumber);
            if (flight != null) {
                int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete flight " + flightNumber + "?",
                    "Delete Flight",
                    JOptionPane.YES_NO_OPTION);
                
                if (result == JOptionPane.YES_OPTION) {
                    flightService.deleteFlight(flight.getId());
                    loadFlights(model);
                    JOptionPane.showMessageDialog(this, "Flight deleted successfully!");
                }
            }
        });

        statusBtn.addActionListener(e -> {
            int selectedRow = flightTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a flight to update status!");
                return;
            }
            String flightNumber = (String) model.getValueAt(selectedRow, 0);
            Flight flight = flightService.getFlightByNumber(flightNumber);
            if (flight != null) {
                showFlightStatusDialog(flight, model);
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAircraftManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Aircraft table
        String[] columns = {"Aircraft ID", "Model", "Registration", "Capacity", "Manufacturer", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable aircraftTable = new JTable(model);
        styleTable(aircraftTable);
        JScrollPane scrollPane = new JScrollPane(aircraftTable);
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton addBtn = createStyledButton("Add Aircraft", new Color(76, 175, 80));
        JButton editBtn = createStyledButton("Edit Aircraft", new Color(255, 193, 7));
        JButton deleteBtn = createStyledButton("Delete Aircraft", new Color(244, 67, 54));
        JButton statusBtn = createStyledButton("Update Status", new Color(33, 150, 243));
        JButton refreshBtn = createStyledButton("Refresh", new Color(158, 158, 158));

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(statusBtn);
        buttonPanel.add(refreshBtn);

        // Load aircraft
        loadAircraft(model);

        // Event listeners
        refreshBtn.addActionListener(e -> loadAircraft(model));
        addBtn.addActionListener(e -> showAddAircraftDialog(model));
        
        // Add double-click listener to show aircraft details
        aircraftTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int selectedRow = aircraftTable.getSelectedRow();
                    if (selectedRow != -1) {
                        String aircraftId = (String) model.getValueAt(selectedRow, 0);
                        Aircraft aircraft = flightService.getAircraftById(aircraftId);
                        if (aircraft != null) {
                            showAircraftDetailsDialog(aircraft);
                        }
                    }
                }
            }
        });
        
        editBtn.addActionListener(e -> {
            int selectedRow = aircraftTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an aircraft to edit!");
                return;
            }
            String aircraftId = (String) model.getValueAt(selectedRow, 0);
            Aircraft aircraft = flightService.getAircraftById(aircraftId);
            if (aircraft != null) {
                showEditAircraftDialog(aircraft, model);
            }
        });

        deleteBtn.addActionListener(e -> {
            int selectedRow = aircraftTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an aircraft to delete!");
                return;
            }
            
            String aircraftId = (String) model.getValueAt(selectedRow, 0);
            String aircraftModel = (String) model.getValueAt(selectedRow, 1);
            
            int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete aircraft: " + aircraftModel + "?",
                "Delete Aircraft",
                JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                flightService.deleteAircraft(aircraftId);
                loadAircraft(model);
                JOptionPane.showMessageDialog(this, "Aircraft deleted successfully!");
            }
        });

        statusBtn.addActionListener(e -> {
            int selectedRow = aircraftTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an aircraft to update status!");
                return;
            }
            String aircraftId = (String) model.getValueAt(selectedRow, 0);
            Aircraft aircraft = flightService.getAircraftById(aircraftId);
            if (aircraft != null) {
                showAircraftStatusDialog(aircraft, model);
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRouteManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Route table
        String[] columns = {"Route ID", "Departure", "Arrival", "Distance (km)", "Duration (min)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable routeTable = new JTable(model);
        styleTable(routeTable);
        JScrollPane scrollPane = new JScrollPane(routeTable);
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton addBtn = createStyledButton("Add Route", new Color(76, 175, 80));
        JButton editBtn = createStyledButton("Edit Route", new Color(255, 193, 7));
        JButton deleteBtn = createStyledButton("Delete Route", new Color(244, 67, 54));
        JButton refreshBtn = createStyledButton("Refresh", new Color(158, 158, 158));

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        // Load routes
        loadRoutes(model);

        // Event listeners
        refreshBtn.addActionListener(e -> loadRoutes(model));
        addBtn.addActionListener(e -> showAddRouteDialog(model));
        
        editBtn.addActionListener(e -> {
            int selectedRow = routeTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a route to edit!");
                return;
            }
            String routeId = (String) model.getValueAt(selectedRow, 0);
            Route route = flightService.getRouteById(routeId);
            if (route != null) {
                showEditRouteDialog(route, model);
            }
        });

        deleteBtn.addActionListener(e -> {
            int selectedRow = routeTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a route to delete!");
                return;
            }
            
            String routeId = (String) model.getValueAt(selectedRow, 0);
            String departure = (String) model.getValueAt(selectedRow, 1);
            String arrival = (String) model.getValueAt(selectedRow, 2);
            
            int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete route: " + departure + " → " + arrival + "?",
                "Delete Route",
                JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                flightService.deleteRoute(routeId);
                loadRoutes(model);
                JOptionPane.showMessageDialog(this, "Route deleted successfully!");
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCustomerManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Customer table
        String[] columns = {"Customer ID", "Name", "Email", "Phone", "Gender", "Total Bookings"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable customerTable = new JTable(model);
        styleTable(customerTable);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton refreshBtn = createStyledButton("Refresh", new Color(158, 158, 158));
        buttonPanel.add(refreshBtn);

        // Load customers
        loadCustomers(model);

        // Event listeners
        refreshBtn.addActionListener(e -> loadCustomers(model));

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        // Statistics cards
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBackground(Color.WHITE);

        // Create and store statistics cards
        flightStatsCard = createStatsCard("Total Flights", String.valueOf(flightService.getAllFlights().size()), new Color(76, 175, 80));
        customerStatsCard = createStatsCard("Total Customers", String.valueOf(userService.getAllCustomers().size()), new Color(33, 150, 243));
        aircraftStatsCard = createStatsCard("Total Aircraft", String.valueOf(flightService.getAllAircrafts().size()), new Color(255, 193, 7));
        ticketStatsCard = createStatsCard("Total Bookings", String.valueOf(bookingService.getAllTickets().size()), new Color(156, 39, 176));

        statsPanel.add(flightStatsCard);
        statsPanel.add(customerStatsCard);
        statsPanel.add(aircraftStatsCard);
        statsPanel.add(ticketStatsCard);

        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(statsPanel, gbc);

        // Refresh button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        JButton refreshBtn = createStyledButton("Refresh Stats", new Color(33, 150, 243));
        refreshBtn.addActionListener(e -> refreshStatistics());
        buttonPanel.add(refreshBtn);

        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStatsCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(200, 120));

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);

        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(Color.WHITE);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private void updateStatsCard(JPanel card, String newValue) {
        // Find the value label (it's the CENTER component)
        Component centerComponent = ((BorderLayout) card.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (centerComponent instanceof JLabel) {
            ((JLabel) centerComponent).setText(newValue);
        }
    }

    private void refreshStatistics() {
        // Update all statistics cards with current data
        updateStatsCard(flightStatsCard, String.valueOf(flightService.getAllFlights().size()));
        updateStatsCard(customerStatsCard, String.valueOf(userService.getAllCustomers().size()));
        updateStatsCard(aircraftStatsCard, String.valueOf(flightService.getAllAircrafts().size()));
        updateStatsCard(ticketStatsCard, String.valueOf(bookingService.getAllTickets().size()));
        
        // Repaint the panel to show updates
        if (flightStatsCard != null) {
            flightStatsCard.getParent().repaint();
        }
    }

    private void styleTable(JTable table) {
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        table.setSelectionBackground(new Color(184, 207, 229));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(Color.LIGHT_GRAY);
        
        // Add status column renderer for color coding
        if (table.getColumnCount() > 6) { // Check if this table has a status column
            table.getColumnModel().getColumn(6).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public java.awt.Component getTableCellRendererComponent(JTable table, Object value, 
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    
                    if (value != null) {
                        String status = value.toString();
                        setFont(new Font("Arial", Font.BOLD, 12));
                        
                        if (!isSelected) {
                            // Flight statuses
                            switch (status) {
                                case "SCHEDULED":
                                    setForeground(new Color(33, 150, 243)); // Blue
                                    break;
                                case "BOARDING":
                                    setForeground(new Color(255, 193, 7)); // Yellow
                                    break;
                                case "DEPARTED":
                                case "ARRIVED":
                                    setForeground(new Color(76, 175, 80)); // Green
                                    break;
                                case "CANCELLED":
                                    setForeground(new Color(244, 67, 54)); // Red
                                    break;
                                case "DELAYED":
                                    setForeground(new Color(255, 87, 34)); // Orange
                                    break;
                                // Aircraft statuses
                                case "ACTIVE":
                                    setForeground(new Color(76, 175, 80)); // Green
                                    break;
                                case "MAINTENANCE":
                                    setForeground(new Color(255, 193, 7)); // Yellow
                                    break;
                                case "DISCONTINUED":
                                    setForeground(new Color(244, 67, 54)); // Red
                                    break;
                                default:
                                    setForeground(new Color(96, 125, 139)); // Gray
                            }
                        } else {
                            setForeground(Color.BLACK);
                        }
                    }
                    return this;
                }
            });
        }
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(120, 35));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }

    // Data loading methods
    private void loadFlights(DefaultTableModel model) {
        model.setRowCount(0);
        List<Flight> flights = flightService.getAllFlights();
        for (Flight flight : flights) {
            Route route = flightService.getRouteById(flight.getRouteId());
            Aircraft aircraft = flightService.getAircraftById(flight.getAircraftId());
            
            String routeInfo = route != null ? route.getDeparture() + " → " + route.getArrival() : "Unknown";
            String aircraftInfo = aircraft != null ? aircraft.getModel() : "Unknown";
            
            Object[] row = {
                flight.getFlightNumber(),
                routeInfo,
                aircraftInfo,
                flight.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                flight.getArrivalTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                String.format("$%.2f", flight.getPrice()),
                flight.getStatus() != null ? flight.getStatus() : "SCHEDULED"
            };
            model.addRow(row);
        }
    }

    private void loadAircraft(DefaultTableModel model) {
        model.setRowCount(0);
        List<Aircraft> aircrafts = flightService.getAllAircrafts();
        for (Aircraft aircraft : aircrafts) {
            Object[] row = {
                aircraft.getId(),
                aircraft.getModel(),
                aircraft.getRegistrationNumber(),
                aircraft.getCapacity(),
                aircraft.getManufacturer(),
                aircraft.getStatus() != null ? aircraft.getStatus() : "ACTIVE"
            };
            model.addRow(row);
        }
    }

    private void loadRoutes(DefaultTableModel model) {
        model.setRowCount(0);
        List<Route> routes = flightService.getAllRoutes();
        for (Route route : routes) {
            Object[] row = {
                route.getId(),
                route.getDeparture(),
                route.getArrival(),
                route.getDistance(),
                route.getDuration()
            };
            model.addRow(row);
        }
    }

    private void loadCustomers(DefaultTableModel model) {
        model.setRowCount(0);
        List<Customer> customers = userService.getAllCustomers();
        for (Customer customer : customers) {
            int bookingCount = bookingService.getCustomerTickets(customer.getId()).size();
            Object[] row = {
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getGender(),
                bookingCount
            };
            model.addRow(row);
        }
    }

    // Dialog methods
    private void showAddFlightDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Add New Flight", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Flight number
        JTextField flightNumberField = new JTextField(15);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Flight Number:"), gbc);
        gbc.gridx = 1;
        panel.add(flightNumberField, gbc);

        // Route selection
        JComboBox<String> routeCombo = new JComboBox<>();
        List<Route> routes = flightService.getAllRoutes();
        for (Route route : routes) {
            routeCombo.addItem(route.getDeparture() + " → " + route.getArrival() + " (" + route.getId() + ")");
        }
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Route:"), gbc);
        gbc.gridx = 1;
        panel.add(routeCombo, gbc);

        // Aircraft selection (only ACTIVE aircraft)
        JComboBox<String> aircraftCombo = new JComboBox<>();
        List<Aircraft> aircrafts = flightService.getAllAircrafts();
        List<Aircraft> activeAircrafts = new ArrayList<>();
        for (Aircraft aircraft : aircrafts) {
            String status = aircraft.getStatus() != null ? aircraft.getStatus() : "ACTIVE";
            if ("ACTIVE".equals(status)) {
                aircraftCombo.addItem(aircraft.getModel() + " (" + aircraft.getId() + ")");
                activeAircrafts.add(aircraft);
            }
        }
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Aircraft:"), gbc);
        gbc.gridx = 1;
        panel.add(aircraftCombo, gbc);

        // Gate selection
        JComboBox<String> gateCombo = new JComboBox<>();
        gateCombo.addItem("No Gate Assigned");
        List<Gate> availableGates = flightService.getAvailableGates();
        for (Gate gate : availableGates) {
            Terminal terminal = flightService.getTerminalById(gate.getTerminal());
            String terminalName = terminal != null ? terminal.getName() : gate.getTerminal();
            gateCombo.addItem("Gate " + gate.getGateNumber() + " (" + terminalName + ")");
        }
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Gate:"), gbc);
        gbc.gridx = 1;
        panel.add(gateCombo, gbc);

        // Departure time
        JTextField departureField = new JTextField(15);
        LocalDateTime defaultDeparture = LocalDateTime.now().plusHours(24);
        departureField.setText(defaultDeparture.toString());
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Departure (YYYY-MM-DDTHH:MM):"), gbc);
        gbc.gridx = 1;
        panel.add(departureField, gbc);

        // Arrival time
        JTextField arrivalField = new JTextField(15);
        LocalDateTime defaultArrival = LocalDateTime.now().plusHours(30);
        arrivalField.setText(defaultArrival.toString());
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Arrival (YYYY-MM-DDTHH:MM):"), gbc);
        gbc.gridx = 1;
        panel.add(arrivalField, gbc);

        // Price
        JTextField priceField = new JTextField(15);
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        JButton saveBtn = createStyledButton("Save", new Color(76, 175, 80));
        JButton cancelBtn = createStyledButton("Cancel", new Color(244, 67, 54));
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        saveBtn.addActionListener(e -> {
            try {
                String flightNumber = flightNumberField.getText().trim();
                
                if (routeCombo.getSelectedIndex() == -1 || aircraftCombo.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(dialog, "Please select both route and aircraft!");
                    return;
                }
                
                if (activeAircrafts.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "No active aircraft available! Only active aircraft can be assigned to flights.");
                    return;
                }
                
                String routeId = routes.get(routeCombo.getSelectedIndex()).getId();
                String aircraftId = activeAircrafts.get(aircraftCombo.getSelectedIndex()).getId();
                LocalDateTime departureTime = LocalDateTime.parse(departureField.getText().trim());
                LocalDateTime arrivalTime = LocalDateTime.parse(arrivalField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());

                if (flightNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a flight number!");
                    return;
                }

                if (price <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Price must be a positive number!");
                    return;
                }

                if (arrivalTime.isBefore(departureTime)) {
                    JOptionPane.showMessageDialog(dialog, "Arrival time must be after departure time!");
                    return;
                }

                String flightId = "F" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                Flight flight = new Flight(flightId, flightNumber, routeId, aircraftId, 
                                         departureTime, arrivalTime, price);

                // Handle gate assignment
                if (gateCombo.getSelectedIndex() > 0) { // 0 is "No Gate Assigned"
                    Gate selectedGate = availableGates.get(gateCombo.getSelectedIndex() - 1);
                    flight.setGateId(selectedGate.getId());
                    // Mark gate as occupied
                    selectedGate.setCurrentFlightId(flightId);
                    flightService.updateGate(selectedGate);
                }

                flightService.addFlight(flight);
                loadFlights(model);
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Flight added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage() + 
                    "\nPlease use format YYYY-MM-DDTHH:MM for date/time fields.");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditFlightDialog(Flight flight, DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Edit Flight", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Flight number
        JTextField flightNumberField = new JTextField(flight.getFlightNumber(), 15);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Flight Number:"), gbc);
        gbc.gridx = 1;
        panel.add(flightNumberField, gbc);

        // Route selection
        JComboBox<String> routeCombo = new JComboBox<>();
        List<Route> routes = flightService.getAllRoutes();
        int selectedRouteIndex = -1;
        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
            routeCombo.addItem(route.getDeparture() + " → " + route.getArrival() + " (" + route.getId() + ")");
            if (route.getId().equals(flight.getRouteId())) {
                selectedRouteIndex = i;
            }
        }
        if (selectedRouteIndex >= 0) {
            routeCombo.setSelectedIndex(selectedRouteIndex);
        }
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Route:"), gbc);
        gbc.gridx = 1;
        panel.add(routeCombo, gbc);

        // Aircraft selection
        JComboBox<String> aircraftCombo = new JComboBox<>();
        List<Aircraft> aircrafts = flightService.getAllAircrafts();
        int selectedAircraftIndex = -1;
        for (int i = 0; i < aircrafts.size(); i++) {
            Aircraft aircraft = aircrafts.get(i);
            aircraftCombo.addItem(aircraft.getModel() + " (" + aircraft.getId() + ")");
            if (aircraft.getId().equals(flight.getAircraftId())) {
                selectedAircraftIndex = i;
            }
        }
        if (selectedAircraftIndex >= 0) {
            aircraftCombo.setSelectedIndex(selectedAircraftIndex);
        }
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Aircraft:"), gbc);
        gbc.gridx = 1;
        panel.add(aircraftCombo, gbc);

        // Gate selection
        JComboBox<String> gateCombo = new JComboBox<>();
        gateCombo.addItem("No Gate Assigned");
        List<Gate> availableGates = flightService.getAvailableGates();
        // Add current gate if assigned
        final Gate currentGate = flight.getGateId() != null ? flightService.getGateById(flight.getGateId()) : null;
        if (currentGate != null) {
            Terminal terminal = flightService.getTerminalById(currentGate.getTerminal());
            String terminalName = terminal != null ? terminal.getName() : currentGate.getTerminal();
            gateCombo.addItem("Gate " + currentGate.getGateNumber() + " (" + terminalName + ") [Current]");
        }
        // Add available gates
        for (Gate gate : availableGates) {
            Terminal terminal = flightService.getTerminalById(gate.getTerminal());
            String terminalName = terminal != null ? terminal.getName() : gate.getTerminal();
            gateCombo.addItem("Gate " + gate.getGateNumber() + " (" + terminalName + ")");
        }
        // Set selection
        if (currentGate != null) {
            gateCombo.setSelectedIndex(1); // Select the current gate
        }
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Gate:"), gbc);
        gbc.gridx = 1;
        panel.add(gateCombo, gbc);

        // Departure time
        JTextField departureField = new JTextField(flight.getDepartureTime().toString(), 15);
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Departure (YYYY-MM-DDTHH:MM):"), gbc);
        gbc.gridx = 1;
        panel.add(departureField, gbc);

        // Arrival time
        JTextField arrivalField = new JTextField(flight.getArrivalTime().toString(), 15);
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Arrival (YYYY-MM-DDTHH:MM):"), gbc);
        gbc.gridx = 1;
        panel.add(arrivalField, gbc);

        // Price
        JTextField priceField = new JTextField(String.valueOf(flight.getPrice()), 15);
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        JButton updateBtn = createStyledButton("Update", new Color(76, 175, 80));
        JButton cancelBtn = createStyledButton("Cancel", new Color(244, 67, 54));
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        updateBtn.addActionListener(e -> {
            try {
                String flightNumber = flightNumberField.getText().trim();
                
                if (routeCombo.getSelectedIndex() == -1 || aircraftCombo.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(dialog, "Please select both route and aircraft!");
                    return;
                }
                
                String routeId = routes.get(routeCombo.getSelectedIndex()).getId();
                String aircraftId = aircrafts.get(aircraftCombo.getSelectedIndex()).getId();
                LocalDateTime departureTime = LocalDateTime.parse(departureField.getText().trim());
                LocalDateTime arrivalTime = LocalDateTime.parse(arrivalField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());

                if (flightNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a flight number!");
                    return;
                }

                if (price <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Price must be a positive number!");
                    return;
                }

                if (arrivalTime.isBefore(departureTime)) {
                    JOptionPane.showMessageDialog(dialog, "Arrival time must be after departure time!");
                    return;
                }

                // Handle gate assignment changes
                String oldGateId = flight.getGateId();
                String newGateId = null;
                
                if (gateCombo.getSelectedIndex() == 0) {
                    // No gate assigned
                    newGateId = null;
                } else if (gateCombo.getSelectedIndex() == 1 && currentGate != null) {
                    // Keep current gate
                    newGateId = currentGate.getId();
                } else {
                    // New gate selected
                    int gateIndex = currentGate != null ? gateCombo.getSelectedIndex() - 2 : gateCombo.getSelectedIndex() - 1;
                    if (gateIndex >= 0 && gateIndex < availableGates.size()) {
                        newGateId = availableGates.get(gateIndex).getId();
                    }
                }
                
                // Update gate assignments if changed
                if (!java.util.Objects.equals(oldGateId, newGateId)) {
                    // Free up old gate
                    if (oldGateId != null) {
                        Gate oldGate = flightService.getGateById(oldGateId);
                        if (oldGate != null) {
                            oldGate.setCurrentFlightId(null);
                            flightService.updateGate(oldGate);
                        }
                    }
                    
                    // Assign new gate
                    if (newGateId != null) {
                        Gate newGate = flightService.getGateById(newGateId);
                        if (newGate != null) {
                            newGate.setCurrentFlightId(flight.getId());
                            flightService.updateGate(newGate);
                        }
                    }
                }

                // Update flight
                flight.setFlightNumber(flightNumber);
                flight.setRouteId(routeId);
                flight.setAircraftId(aircraftId);
                flight.setGateId(newGateId);
                flight.setDepartureTime(departureTime);
                flight.setArrivalTime(arrivalTime);
                flight.setPrice(price);

                flightService.updateFlight(flight);
                loadFlights(model);
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Flight updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage() + 
                    "\nPlease use format YYYY-MM-DDTHH:MM for date/time fields.");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAddAircraftDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Add New Aircraft", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField modelField = new JTextField(15);
        JTextField registrationField = new JTextField(15);
        JTextField capacityField = new JTextField(15);
        JTextField manufacturerField = new JTextField(15);
        JTextField imagePathField = new JTextField(15);
        imagePathField.setEditable(false);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Model:"), gbc);
        gbc.gridx = 1;
        panel.add(modelField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Registration:"), gbc);
        gbc.gridx = 1;
        panel.add(registrationField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Capacity:"), gbc);
        gbc.gridx = 1;
        panel.add(capacityField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Manufacturer:"), gbc);
        gbc.gridx = 1;
        panel.add(manufacturerField, gbc);

        // Image selection
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Aircraft Image:"), gbc);
        gbc.gridx = 1;
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        imagePanel.setBackground(Color.WHITE);
        imagePanel.add(imagePathField);
        
        JButton browseBtn = new JButton("Browse");
        browseBtn.setPreferredSize(new Dimension(80, 25));
        browseBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Image files", "jpg", "jpeg", "png", "gif", "bmp"));
            
            if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                String selectedPath = fileChooser.getSelectedFile().getAbsolutePath();
                imagePathField.setText(selectedPath);
            }
        });
        imagePanel.add(browseBtn);
        panel.add(imagePanel, gbc);

        // Image preview
        JLabel imagePreview = new JLabel("No image selected", JLabel.CENTER);
        imagePreview.setPreferredSize(new Dimension(150, 100));
        imagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        imagePreview.setBackground(Color.LIGHT_GRAY);
        imagePreview.setOpaque(true);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(imagePreview, gbc);

        // Update preview when image path changes
        imagePathField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            
            private void updatePreview() {
                String imagePath = imagePathField.getText();
                if (imagePath != null && !imagePath.trim().isEmpty()) {
                    // Use ImageUtil for proper scaling
                    ImageIcon scaledIcon = ImageUtil.getAircraftImage(imagePath, 150, 100);
                    if (scaledIcon != null) {
                        imagePreview.setIcon(scaledIcon);
                        imagePreview.setText("");
                    } else {
                        imagePreview.setIcon(null);
                        imagePreview.setText("No image");
                    }
                } else {
                    imagePreview.setIcon(null);
                    imagePreview.setText("No image selected");
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        JButton saveBtn = createStyledButton("Save", new Color(76, 175, 80));
        JButton cancelBtn = createStyledButton("Cancel", new Color(244, 67, 54));
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        saveBtn.addActionListener(e -> {
            try {
                String aircraftId = "A" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                String modelText = modelField.getText().trim();
                String registration = registrationField.getText().trim();
                int capacity = Integer.parseInt(capacityField.getText().trim());
                String manufacturer = manufacturerField.getText().trim();
                String imagePath = imagePathField.getText().trim();

                if (modelText.isEmpty() || registration.isEmpty() || manufacturer.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all required fields!");
                    return;
                }

                if (capacity <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Capacity must be a positive number!");
                    return;
                }

                // Use default image if none selected
                if (imagePath.isEmpty()) {
                    imagePath = "images/default-aircraft.jpg";
                }

                Aircraft aircraft = new Aircraft(aircraftId, modelText, registration, capacity, manufacturer, imagePath);
                flightService.addAircraft(aircraft);
                loadAircraft(model);
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Aircraft added successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Capacity must be a valid number!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAddRouteDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Add New Route", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField departureField = new JTextField(15);
        JTextField arrivalField = new JTextField(15);
        JTextField distanceField = new JTextField(15);
        JTextField durationField = new JTextField(15);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Departure:"), gbc);
        gbc.gridx = 1;
        panel.add(departureField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Arrival:"), gbc);
        gbc.gridx = 1;
        panel.add(arrivalField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Distance (km):"), gbc);
        gbc.gridx = 1;
        panel.add(distanceField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Duration (min):"), gbc);
        gbc.gridx = 1;
        panel.add(durationField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        JButton saveBtn = createStyledButton("Save", new Color(76, 175, 80));
        JButton cancelBtn = createStyledButton("Cancel", new Color(244, 67, 54));
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        saveBtn.addActionListener(e -> {
            try {
                String routeId = "R" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                String departure = departureField.getText().trim();
                String arrival = arrivalField.getText().trim();
                int distance = Integer.parseInt(distanceField.getText().trim());
                int duration = Integer.parseInt(durationField.getText().trim());

                Route route = new Route(routeId, departure, arrival, distance, duration);
                flightService.addRoute(route);
                loadRoutes(model);
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Route added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditRouteDialog(Route route, DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Edit Route", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField departureField = new JTextField(route.getDeparture(), 15);
        JTextField arrivalField = new JTextField(route.getArrival(), 15);
        JTextField distanceField = new JTextField(String.valueOf(route.getDistance()), 15);
        JTextField durationField = new JTextField(String.valueOf(route.getDuration()), 15);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Departure:"), gbc);
        gbc.gridx = 1;
        panel.add(departureField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Arrival:"), gbc);
        gbc.gridx = 1;
        panel.add(arrivalField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Distance (km):"), gbc);
        gbc.gridx = 1;
        panel.add(distanceField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Duration (min):"), gbc);
        gbc.gridx = 1;
        panel.add(durationField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        JButton updateBtn = createStyledButton("Update", new Color(76, 175, 80));
        JButton cancelBtn = createStyledButton("Cancel", new Color(244, 67, 54));
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        updateBtn.addActionListener(e -> {
            try {
                String departure = departureField.getText().trim();
                String arrival = arrivalField.getText().trim();
                int distance = Integer.parseInt(distanceField.getText().trim());
                int duration = Integer.parseInt(durationField.getText().trim());

                if (departure.isEmpty() || arrival.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all fields!");
                    return;
                }

                if (distance <= 0 || duration <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Distance and duration must be positive numbers!");
                    return;
                }

                route.setDeparture(departure);
                route.setArrival(arrival);
                route.setDistance(distance);
                route.setDuration(duration);

                flightService.updateRoute(route);
                loadRoutes(model);
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Route updated successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Distance and duration must be valid numbers!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showFlightDetailsDialog(Flight flight) {
        JDialog dialog = new JDialog(this, "Flight Details - " + flight.getFlightNumber(), true);
        dialog.setSize(800, 700);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("Flight " + flight.getFlightNumber() + " Details", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Get related data
        Route route = flightService.getRouteById(flight.getRouteId());
        Aircraft aircraft = flightService.getAircraftById(flight.getAircraftId());

        // Create main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Aircraft Image at the top (if available and found)
        if (aircraft != null) {
            String aircraftImagePath = aircraft.getImagePath();
            if (aircraftImagePath != null && !aircraftImagePath.trim().isEmpty()) {
                // Use ImageUtil for proper scaling
                ImageIcon scaledIcon = ImageUtil.getAircraftImage(aircraftImagePath, 400, 200);
                if (scaledIcon != null) {
                    JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                    imagePanel.setBackground(Color.WHITE);
                    
                    JLabel aircraftImage = new JLabel(scaledIcon, JLabel.CENTER);
                    aircraftImage.setBackground(Color.WHITE);
                    aircraftImage.setOpaque(false);
                    aircraftImage.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
                    
                    imagePanel.add(aircraftImage);
                    contentPanel.add(imagePanel, BorderLayout.NORTH);
                }
            }
        }

        // Details panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Flight Information Section
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        JLabel flightInfoLabel = new JLabel("Flight Information");
        flightInfoLabel.setFont(new Font("Arial", Font.BOLD, 18));
        flightInfoLabel.setForeground(new Color(70, 130, 180));
        detailsPanel.add(flightInfoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        detailsPanel.add(new JLabel("Flight Number:"), gbc);
        gbc.gridx = 1;
        JLabel flightNumLabel = new JLabel(flight.getFlightNumber());
        flightNumLabel.setFont(new Font("Arial", Font.BOLD, 12));
        detailsPanel.add(flightNumLabel, gbc);

        if (route != null) {
            gbc.gridx = 2; gbc.gridy = 1;
            detailsPanel.add(new JLabel("Route:"), gbc);
            gbc.gridx = 3;
            JLabel routeLabel = new JLabel(route.getDeparture() + " → " + route.getArrival());
            routeLabel.setFont(new Font("Arial", Font.BOLD, 12));
            detailsPanel.add(routeLabel, gbc);
        }

        gbc.gridx = 0; gbc.gridy = 2;
        detailsPanel.add(new JLabel("Departure:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(flight.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))), gbc);

        gbc.gridx = 2; gbc.gridy = 2;
        detailsPanel.add(new JLabel("Arrival:"), gbc);
        gbc.gridx = 3;
        detailsPanel.add(new JLabel(flight.getArrivalTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        detailsPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        JLabel statusLabel = new JLabel(flight.getStatus() != null ? flight.getStatus() : "SCHEDULED");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        // Color code the status
        switch (statusLabel.getText()) {
            case "SCHEDULED":
                statusLabel.setForeground(new Color(33, 150, 243)); // Blue
                break;
            case "BOARDING":
                statusLabel.setForeground(new Color(255, 193, 7)); // Yellow
                break;
            case "DEPARTED":
                statusLabel.setForeground(new Color(76, 175, 80)); // Green
                break;
            case "ARRIVED":
                statusLabel.setForeground(new Color(76, 175, 80)); // Green
                break;
            case "CANCELLED":
                statusLabel.setForeground(new Color(244, 67, 54)); // Red
                break;
            case "DELAYED":
                statusLabel.setForeground(new Color(255, 87, 34)); // Orange
                break;
            default:
                statusLabel.setForeground(new Color(96, 125, 139)); // Gray
        }
        detailsPanel.add(statusLabel, gbc);

        gbc.gridx = 2; gbc.gridy = 3;
        detailsPanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 3;
        JLabel priceLabel = new JLabel(String.format("$%.2f", flight.getPrice()));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 12));
        priceLabel.setForeground(new Color(76, 175, 80));
        detailsPanel.add(priceLabel, gbc);

        // Seat availability information
        if (aircraft != null) {
            List<FlightSeat> flightSeats = flightService.getFlightSeats(flight.getId());
            int totalSeats = aircraft.getCapacity();
            int availableSeats = (int) flightSeats.stream().filter(FlightSeat::isAvailable).count();
            int soldSeats = (int) flightSeats.stream().filter(FlightSeat::isSold).count();

            gbc.gridx = 0; gbc.gridy = 4;
            detailsPanel.add(new JLabel("Available Seats:"), gbc);
            gbc.gridx = 1;
            JLabel availableLabel = new JLabel(String.valueOf(availableSeats));
            availableLabel.setFont(new Font("Arial", Font.BOLD, 12));
            availableLabel.setForeground(new Color(76, 175, 80));
            detailsPanel.add(availableLabel, gbc);


            gbc.gridx = 0; gbc.gridy = 4;
            detailsPanel.add(new JLabel("Sold Seats:"), gbc);
            gbc.gridx = 1;
            JLabel soldLabel = new JLabel(String.valueOf(soldSeats));
            soldLabel.setFont(new Font("Arial", Font.BOLD, 12));
            soldLabel.setForeground(new Color(244, 67, 54));
            detailsPanel.add(soldLabel, gbc);
        }

        // Aircraft Information Section
        if (aircraft != null) {
            gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 4;
            gbc.insets = new Insets(25, 10, 8, 10);
            JLabel aircraftInfoLabel = new JLabel("Aircraft Information");
            aircraftInfoLabel.setFont(new Font("Arial", Font.BOLD, 18));
            aircraftInfoLabel.setForeground(new Color(70, 130, 180));
            detailsPanel.add(aircraftInfoLabel, gbc);

            gbc.gridwidth = 1;
            gbc.insets = new Insets(8, 10, 8, 10);
            
            gbc.gridx = 0; gbc.gridy = 7;
            detailsPanel.add(new JLabel("Model:"), gbc);
            gbc.gridx = 1;
            JLabel modelLabel = new JLabel(aircraft.getModel());
            modelLabel.setFont(new Font("Arial", Font.BOLD, 12));
            detailsPanel.add(modelLabel, gbc);

            gbc.gridx = 2; gbc.gridy = 7;
            detailsPanel.add(new JLabel("Registration:"), gbc);
            gbc.gridx = 3;
            detailsPanel.add(new JLabel(aircraft.getRegistrationNumber()), gbc);

            gbc.gridx = 0; gbc.gridy = 8;
            detailsPanel.add(new JLabel("Manufacturer:"), gbc);
            gbc.gridx = 1;
            detailsPanel.add(new JLabel(aircraft.getManufacturer()), gbc);

            gbc.gridx = 2; gbc.gridy = 8;
            detailsPanel.add(new JLabel("Capacity:"), gbc);
            gbc.gridx = 3;
            JLabel capacityLabel = new JLabel(String.valueOf(aircraft.getCapacity()) + " passengers");
            capacityLabel.setFont(new Font("Arial", Font.BOLD, 12));
            detailsPanel.add(capacityLabel, gbc);
        }

        contentPanel.add(detailsPanel, BorderLayout.CENTER);

        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        JButton closeBtn = createStyledButton("Close", new Color(158, 158, 158));
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void showAircraftDetailsDialog(Aircraft aircraft) {
        JDialog dialog = new JDialog(this, "Aircraft Details - " + aircraft.getModel(), true);
        dialog.setSize(700, 600);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("Aircraft " + aircraft.getModel() + " Details", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Aircraft Image at the top (if available)
        String imagePath = aircraft.getImagePath();
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            try {
                java.io.File imageFile = new java.io.File(imagePath);
                if (imageFile.exists()) {
                    ImageIcon imageIcon = new ImageIcon(imagePath);
                    // Calculate proper aspect ratio
                    int originalWidth = imageIcon.getIconWidth();
                    int originalHeight = imageIcon.getIconHeight();
                    int maxWidth = 600;
                    int maxHeight = 250;
                    
                    double scale = Math.min((double)maxWidth/originalWidth, (double)maxHeight/originalHeight);
                    int newWidth = (int)(originalWidth * scale);
                    int newHeight = (int)(originalHeight * scale);
                    
                    Image image = imageIcon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                    JLabel aircraftImage = new JLabel(new ImageIcon(image), JLabel.CENTER);
                    aircraftImage.setBackground(Color.WHITE);
                    aircraftImage.setOpaque(false);
                    aircraftImage.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
                    contentPanel.add(aircraftImage, BorderLayout.NORTH);
                }
            } catch (Exception ex) {
                // Ignore error and don't show image
            }
        }

        // Aircraft Information Panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Aircraft Information Header
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        JLabel aircraftInfoLabel = new JLabel("Aircraft Information");
        aircraftInfoLabel.setFont(new Font("Arial", Font.BOLD, 18));
        aircraftInfoLabel.setForeground(new Color(70, 130, 180));
        infoPanel.add(aircraftInfoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(new JLabel("Aircraft ID:"), gbc);
        gbc.gridx = 1;
        JLabel idLabel = new JLabel(aircraft.getId());
        idLabel.setFont(new Font("Arial", Font.BOLD, 12));
        infoPanel.add(idLabel, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        infoPanel.add(new JLabel("Model:"), gbc);
        gbc.gridx = 3;
        JLabel modelLabel = new JLabel(aircraft.getModel());
        modelLabel.setFont(new Font("Arial", Font.BOLD, 12));
        infoPanel.add(modelLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        infoPanel.add(new JLabel("Registration:"), gbc);
        gbc.gridx = 1;
        JLabel regLabel = new JLabel(aircraft.getRegistrationNumber());
        regLabel.setFont(new Font("Arial", Font.BOLD, 12));
        infoPanel.add(regLabel, gbc);

        gbc.gridx = 2; gbc.gridy = 2;
        infoPanel.add(new JLabel("Manufacturer:"), gbc);
        gbc.gridx = 3;
        JLabel mfgLabel = new JLabel(aircraft.getManufacturer());
        mfgLabel.setFont(new Font("Arial", Font.BOLD, 12));
        infoPanel.add(mfgLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        infoPanel.add(new JLabel("Capacity:"), gbc);
        gbc.gridx = 1;
        JLabel capacityLabel = new JLabel(String.valueOf(aircraft.getCapacity()) + " passengers");
        capacityLabel.setFont(new Font("Arial", Font.BOLD, 12));
        capacityLabel.setForeground(new Color(76, 175, 80));
        infoPanel.add(capacityLabel, gbc);

        contentPanel.add(infoPanel, BorderLayout.CENTER);

        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        JButton closeBtn = createStyledButton("Close", new Color(158, 158, 158));
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void showEditAircraftDialog(Aircraft aircraft, DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Edit Aircraft", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField modelField = new JTextField(aircraft.getModel(), 15);
        JTextField registrationField = new JTextField(aircraft.getRegistrationNumber(), 15);
        JTextField capacityField = new JTextField(String.valueOf(aircraft.getCapacity()), 15);
        JTextField manufacturerField = new JTextField(aircraft.getManufacturer(), 15);
        JTextField imagePathField = new JTextField(aircraft.getImagePath(), 15);
        imagePathField.setEditable(false);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Model:"), gbc);
        gbc.gridx = 1;
        panel.add(modelField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Registration:"), gbc);
        gbc.gridx = 1;
        panel.add(registrationField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Capacity:"), gbc);
        gbc.gridx = 1;
        panel.add(capacityField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Manufacturer:"), gbc);
        gbc.gridx = 1;
        panel.add(manufacturerField, gbc);

        // Image selection
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Aircraft Image:"), gbc);
        gbc.gridx = 1;
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        imagePanel.setBackground(Color.WHITE);
        imagePanel.add(imagePathField);
        
        JButton browseBtn = new JButton("Browse");
        browseBtn.setPreferredSize(new Dimension(80, 25));
        browseBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Image files", "jpg", "jpeg", "png", "gif", "bmp"));
            
            if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                String selectedPath = fileChooser.getSelectedFile().getAbsolutePath();
                imagePathField.setText(selectedPath);
            }
        });
        imagePanel.add(browseBtn);
        panel.add(imagePanel, gbc);

        // Image preview
        JLabel imagePreview = new JLabel("No image selected", JLabel.CENTER);
        imagePreview.setPreferredSize(new Dimension(150, 100));
        imagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        imagePreview.setBackground(Color.LIGHT_GRAY);
        imagePreview.setOpaque(true);
        
        // Load current image
        String currentImagePath = aircraft.getImagePath();
        if (currentImagePath != null && !currentImagePath.trim().isEmpty()) {
            try {
                ImageIcon imageIcon = new ImageIcon(currentImagePath);
                Image image = imageIcon.getImage().getScaledInstance(150, 100, Image.SCALE_SMOOTH);
                imagePreview.setIcon(new ImageIcon(image));
                imagePreview.setText("");
            } catch (Exception ex) {
                imagePreview.setText("Current image not found");
            }
        }
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(imagePreview, gbc);

        // Update preview when image path changes
        imagePathField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            
            private void updatePreview() {
                String imagePath = imagePathField.getText();
                if (imagePath != null && !imagePath.trim().isEmpty()) {
                    try {
                        java.io.File imageFile = new java.io.File(imagePath);
                        if (imageFile.exists()) {
                            ImageIcon imageIcon = new ImageIcon(imagePath);
                            // Maintain aspect ratio
                            int originalWidth = imageIcon.getIconWidth();
                            int originalHeight = imageIcon.getIconHeight();
                            int maxWidth = 150;
                            int maxHeight = 100;
                            
                            double scale = Math.min((double)maxWidth/originalWidth, (double)maxHeight/originalHeight);
                            int newWidth = (int)(originalWidth * scale);
                            int newHeight = (int)(originalHeight * scale);
                            
                            Image image = imageIcon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                            imagePreview.setIcon(new ImageIcon(image));
                            imagePreview.setText("");
                        } else {
                            imagePreview.setIcon(null);
                            imagePreview.setText("File not found");
                        }
                    } catch (Exception ex) {
                        imagePreview.setIcon(null);
                        imagePreview.setText("Error loading image");
                    }
                } else {
                    imagePreview.setIcon(null);
                    imagePreview.setText("No image selected");
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        JButton updateBtn = createStyledButton("Update", new Color(76, 175, 80));
        JButton cancelBtn = createStyledButton("Cancel", new Color(244, 67, 54));
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        updateBtn.addActionListener(e -> {
            try {
                String modelText = modelField.getText().trim();
                String registration = registrationField.getText().trim();
                int capacity = Integer.parseInt(capacityField.getText().trim());
                String manufacturer = manufacturerField.getText().trim();
                String imagePath = imagePathField.getText().trim();

                if (modelText.isEmpty() || registration.isEmpty() || manufacturer.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all required fields!");
                    return;
                }

                if (capacity <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Capacity must be a positive number!");
                    return;
                }

                // Update aircraft
                aircraft.setModel(modelText);
                aircraft.setRegistrationNumber(registration);
                aircraft.setCapacity(capacity);
                aircraft.setManufacturer(manufacturer);
                aircraft.setImagePath(imagePath);

                flightService.updateAircraft(aircraft);
                loadAircraft(model);
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Aircraft updated successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Capacity must be a valid number!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

        private JPanel createGateManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Gate table
        String[] columns = {"Gate ID", "Gate Number", "Terminal", "Status", "Current Flight"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable gateTable = new JTable(model);
        styleTable(gateTable);
        JScrollPane scrollPane = new JScrollPane(gateTable);
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton addBtn = createStyledButton("Add Gate", new Color(76, 175, 80));
        JButton editBtn = createStyledButton("Edit Gate", new Color(255, 193, 7));
        JButton deleteBtn = createStyledButton("Delete Gate", new Color(244, 67, 54));
        JButton refreshBtn = createStyledButton("Refresh", new Color(158, 158, 158));

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        // Load gates
        loadGates(model);

        // Event listeners
        refreshBtn.addActionListener(e -> loadGates(model));
        addBtn.addActionListener(e -> showAddGateDialog(model));
        
        editBtn.addActionListener(e -> {
            int selectedRow = gateTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a gate to edit!");
                return;
            }
            String gateId = (String) model.getValueAt(selectedRow, 0);
            Gate gate = flightService.getGateById(gateId);
            if (gate != null) {
                showEditGateDialog(gate, model);
            }
        });

        deleteBtn.addActionListener(e -> {
            int selectedRow = gateTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a gate to delete!");
                return;
            }
            
            String gateId = (String) model.getValueAt(selectedRow, 0);
            String gateNumber = (String) model.getValueAt(selectedRow, 1);
            
            int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete gate " + gateNumber + "?",
                "Delete Gate",
                JOptionPane.YES_NO_OPTION);
                
            if (result == JOptionPane.YES_OPTION) {
                flightService.deleteGate(gateId);
                loadGates(model);
                JOptionPane.showMessageDialog(this, "Gate deleted successfully!");
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createTerminalManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Terminal table
        String[] columns = {"Terminal ID", "Name", "Type", "Gates Count", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable terminalTable = new JTable(model);
        styleTable(terminalTable);
        JScrollPane scrollPane = new JScrollPane(terminalTable);
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton addBtn = createStyledButton("Add Terminal", new Color(76, 175, 80));
        JButton editBtn = createStyledButton("Edit Terminal", new Color(255, 193, 7));
        JButton deleteBtn = createStyledButton("Delete Terminal", new Color(244, 67, 54));
        JButton refreshBtn = createStyledButton("Refresh", new Color(158, 158, 158));

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        // Load terminals
        loadTerminals(model);

        // Event listeners
        refreshBtn.addActionListener(e -> loadTerminals(model));
        addBtn.addActionListener(e -> showAddTerminalDialog(model));
        
        editBtn.addActionListener(e -> {
            int selectedRow = terminalTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a terminal to edit!");
                return;
            }
            String terminalId = (String) model.getValueAt(selectedRow, 0);
            Terminal terminal = flightService.getTerminalById(terminalId);
            if (terminal != null) {
                showEditTerminalDialog(terminal, model);
            }
        });

        deleteBtn.addActionListener(e -> {
            int selectedRow = terminalTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a terminal to delete!");
                return;
            }
            
            String terminalId = (String) model.getValueAt(selectedRow, 0);
            String terminalName = (String) model.getValueAt(selectedRow, 1);
            
            int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete terminal " + terminalName + "?",
                "Delete Terminal",
                JOptionPane.YES_NO_OPTION);
                
            if (result == JOptionPane.YES_OPTION) {
                flightService.deleteTerminal(terminalId);
                loadTerminals(model);
                JOptionPane.showMessageDialog(this, "Terminal deleted successfully!");
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    // Data loading methods for gates and terminals
    private void loadGates(DefaultTableModel model) {
        model.setRowCount(0);
        List<Gate> gates = flightService.getAllGates();
        for (Gate gate : gates) {
            String status = gate.isAvailable() ? "Available" : "Occupied";
            String currentFlight = gate.getCurrentFlightId() != null ? gate.getCurrentFlightId() : "None";
            
            Object[] row = {
                gate.getId(),
                gate.getGateNumber(),
                gate.getTerminal(),
                status,
                currentFlight
            };
            model.addRow(row);
        }
    }

    private void loadTerminals(DefaultTableModel model) {
        model.setRowCount(0);
        List<Terminal> terminals = flightService.getAllTerminals();
        for (Terminal terminal : terminals) {
            // Count gates for this terminal
            long gateCount = flightService.getAllGates().stream()
                .filter(gate -> gate.getTerminal().equals(terminal.getId()))
                .count();
            
            Object[] row = {
                terminal.getId(),
                terminal.getName(),
                terminal.getType(),
                String.valueOf(gateCount),
                terminal.isActive() ? "Active" : "Inactive"
            };
            model.addRow(row);
        }
    }

    // Dialog methods for gates and terminals
    private void showAddGateDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Add New Gate", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField gateNumberField = new JTextField(15);
        JComboBox<String> terminalCombo = new JComboBox<>();
        
        // Load terminals for selection
        List<Terminal> terminals = flightService.getAllTerminals();
        for (Terminal terminal : terminals) {
            terminalCombo.addItem(terminal.getName() + " (" + terminal.getId() + ")");
        }

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Gate Number:"), gbc);
        gbc.gridx = 1;
        panel.add(gateNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Terminal:"), gbc);
        gbc.gridx = 1;
        panel.add(terminalCombo, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        JButton saveBtn = createStyledButton("Save", new Color(76, 175, 80));
        JButton cancelBtn = createStyledButton("Cancel", new Color(244, 67, 54));
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        saveBtn.addActionListener(e -> {
            try {
                String gateNumber = gateNumberField.getText().trim();
                
                if (gateNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a gate number!");
                    return;
                }
                
                if (terminalCombo.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(dialog, "Please select a terminal!");
                    return;
                }

                String gateId = "G" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                String terminalId = terminals.get(terminalCombo.getSelectedIndex()).getId();
                
                Gate gate = new Gate(gateId, gateNumber, terminalId);
                flightService.addGate(gate);
                loadGates(model);
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Gate added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditGateDialog(Gate gate, DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Edit Gate", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField gateNumberField = new JTextField(gate.getGateNumber(), 15);
        JComboBox<String> terminalCombo = new JComboBox<>();
        
        // Load terminals for selection
        List<Terminal> terminals = flightService.getAllTerminals();
        int selectedTerminalIndex = -1;
        for (int i = 0; i < terminals.size(); i++) {
            Terminal terminal = terminals.get(i);
            terminalCombo.addItem(terminal.getName() + " (" + terminal.getId() + ")");
            if (terminal.getId().equals(gate.getTerminal())) {
                selectedTerminalIndex = i;
            }
        }
        if (selectedTerminalIndex >= 0) {
            terminalCombo.setSelectedIndex(selectedTerminalIndex);
        }

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Gate Number:"), gbc);
        gbc.gridx = 1;
        panel.add(gateNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Terminal:"), gbc);
        gbc.gridx = 1;
        panel.add(terminalCombo, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        JButton updateBtn = createStyledButton("Update", new Color(76, 175, 80));
        JButton cancelBtn = createStyledButton("Cancel", new Color(244, 67, 54));
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        updateBtn.addActionListener(e -> {
            try {
                String gateNumber = gateNumberField.getText().trim();
                
                if (gateNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a gate number!");
                    return;
                }
                
                if (terminalCombo.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(dialog, "Please select a terminal!");
                    return;
                }

                String terminalId = terminals.get(terminalCombo.getSelectedIndex()).getId();
                
                gate.setGateNumber(gateNumber);
                gate.setTerminal(terminalId);
                
                flightService.updateGate(gate);
                loadGates(model);
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Gate updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAddTerminalDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Add New Terminal", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField nameField = new JTextField(15);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"DOMESTIC", "INTERNATIONAL"});

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Terminal Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        panel.add(typeCombo, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        JButton saveBtn = createStyledButton("Save", new Color(76, 175, 80));
        JButton cancelBtn = createStyledButton("Cancel", new Color(244, 67, 54));
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String type = (String) typeCombo.getSelectedItem();
                
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a terminal name!");
                    return;
                }

                String terminalId = "T" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                Terminal terminal = new Terminal(terminalId, name, type);
                flightService.addTerminal(terminal);
                loadTerminals(model);
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Terminal added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditTerminalDialog(Terminal terminal, DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Edit Terminal", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField nameField = new JTextField(terminal.getName(), 15);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"DOMESTIC", "INTERNATIONAL"});
        typeCombo.setSelectedItem(terminal.getType());

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Terminal Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        panel.add(typeCombo, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        JButton updateBtn = createStyledButton("Update", new Color(76, 175, 80));
        JButton cancelBtn = createStyledButton("Cancel", new Color(244, 67, 54));
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        updateBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String type = (String) typeCombo.getSelectedItem();
                
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a terminal name!");
                    return;
                }

                terminal.setName(name);
                terminal.setType(type);
                
                flightService.updateTerminal(terminal);
                loadTerminals(model);
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Terminal updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showFlightStatusDialog(Flight flight, DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Update Flight Status", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Flight info
        JLabel flightLabel = new JLabel("Flight: " + flight.getFlightNumber());
        flightLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(flightLabel, gbc);

        // Current status
        JLabel currentStatusLabel = new JLabel("Current Status: " + (flight.getStatus() != null ? flight.getStatus() : "SCHEDULED"));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(currentStatusLabel, gbc);

        // New status selection
        JLabel newStatusLabel = new JLabel("New Status:");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        panel.add(newStatusLabel, gbc);

        String[] statuses = {"SCHEDULED", "BOARDING", "DEPARTED", "ARRIVED", "CANCELLED", "DELAYED"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        statusCombo.setSelectedItem(flight.getStatus() != null ? flight.getStatus() : "SCHEDULED");
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(statusCombo, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        JButton updateBtn = createStyledButton("Update", new Color(76, 175, 80));
        JButton cancelBtn = createStyledButton("Cancel", new Color(244, 67, 54));
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        updateBtn.addActionListener(e -> {
            String newStatus = (String) statusCombo.getSelectedItem();
            flight.setStatus(newStatus);
            flightService.updateFlight(flight);
            loadFlights(model);
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Flight status updated successfully!");
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAircraftStatusDialog(Aircraft aircraft, DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Update Aircraft Status", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Aircraft info
        JLabel aircraftLabel = new JLabel("Aircraft: " + aircraft.getModel() + " (" + aircraft.getRegistrationNumber() + ")");
        aircraftLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(aircraftLabel, gbc);

        // Current status
        JLabel currentStatusLabel = new JLabel("Current Status: " + (aircraft.getStatus() != null ? aircraft.getStatus() : "ACTIVE"));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(currentStatusLabel, gbc);

        // New status selection
        JLabel newStatusLabel = new JLabel("New Status:");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        panel.add(newStatusLabel, gbc);

        String[] statuses = {"ACTIVE", "MAINTENANCE", "DISCONTINUED"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        statusCombo.setSelectedItem(aircraft.getStatus() != null ? aircraft.getStatus() : "ACTIVE");
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(statusCombo, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        JButton updateBtn = createStyledButton("Update", new Color(76, 175, 80));
        JButton cancelBtn = createStyledButton("Cancel", new Color(244, 67, 54));
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        updateBtn.addActionListener(e -> {
            String newStatus = (String) statusCombo.getSelectedItem();
            aircraft.setStatus(newStatus);
            flightService.updateAircraft(aircraft);
            loadAircraft(model);
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Aircraft status updated successfully!");
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void logout() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout",
            JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
} 