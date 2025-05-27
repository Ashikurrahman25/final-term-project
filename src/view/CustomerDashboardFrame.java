package view;

import model.*;
import service.*;
import util.ImageUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CustomerDashboardFrame extends JFrame {
    private Customer customer;
    private FlightService flightService;
    private BookingService bookingService;
    private JTabbedPane tabbedPane;
    private DefaultTableModel ticketsTableModel;

    public CustomerDashboardFrame(Customer customer, FlightService flightService, BookingService bookingService) {
        this.customer = customer;
        this.flightService = flightService;
        this.bookingService = bookingService;
        
        setTitle("Customer Dashboard - " + customer.getName());
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        JLabel titleLabel = new JLabel("Welcome, " + customer.getName() + "!", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Search Flights", createSearchFlightsPanel());
        tabbedPane.addTab("My Tickets", createMyTicketsPanel());
        tabbedPane.addTab("Profile", createProfilePanel());

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

    private JPanel createSearchFlightsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Search Panel
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Flights"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Get all unique destinations from routes
        String[] destinations = {"All Destinations", "New York", "London", "Paris", "Tokyo", "Dubai", "Sydney", "Singapore", "Mumbai", "Los Angeles"};
        
        JComboBox<String> fromCombo = new JComboBox<>(destinations);
        JComboBox<String> toCombo = new JComboBox<>(destinations);
        JButton searchBtn = new JButton("Search Flights");
        JButton clearBtn = new JButton("Clear");
        
        // Style the buttons
        searchBtn.setBackground(new Color(76, 175, 80));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("Arial", Font.BOLD, 12));
        searchBtn.setOpaque(true); // Required for Mac compatibility
        
        clearBtn.setBackground(new Color(158, 158, 158));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFont(new Font("Arial", Font.BOLD, 12));
        clearBtn.setOpaque(true); // Required for Mac compatibility

        gbc.gridx = 0; gbc.gridy = 0;
        searchPanel.add(new JLabel("From:"), gbc);
        gbc.gridx = 1;
        searchPanel.add(fromCombo, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        searchPanel.add(new JLabel("To:"), gbc);
        gbc.gridx = 3;
        searchPanel.add(toCombo, gbc);

        gbc.gridx = 4; gbc.gridy = 0;
        searchPanel.add(searchBtn, gbc);
        gbc.gridx = 5;
        searchPanel.add(clearBtn, gbc);

        // Flight Table
        String[] columns = {"Flight", "From", "To", "Departure", "Arrival", "Aircraft", "Price", "Available", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable flightTable = new JTable(model);
        flightTable.getTableHeader().setBackground(Color.BLACK);
        flightTable.getTableHeader().setForeground(Color.WHITE);
        flightTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        flightTable.getTableHeader().setReorderingAllowed(false);
        flightTable.getTableHeader().setResizingAllowed(false);
        flightTable.setRowHeight(30);
        flightTable.setFont(new Font("Arial", Font.PLAIN, 12));
        flightTable.setBackground(Color.WHITE);
        flightTable.setForeground(Color.BLACK);
        flightTable.setSelectionBackground(new Color(184, 207, 229));
        flightTable.setSelectionForeground(Color.BLACK);
        
        // Add status column renderer for color coding
        flightTable.getColumnModel().getColumn(8).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value != null) {
                    String status = value.toString();
                    setFont(new Font("Arial", Font.BOLD, 12));
                    
                    if (!isSelected) {
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
        JScrollPane scrollPane = new JScrollPane(flightTable);
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        JButton bookBtn = new JButton("Book Selected Flight");
        bookBtn.setBackground(new Color(72, 201, 176));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFont(new Font("Arial", Font.BOLD, 12));
        bookBtn.setPreferredSize(new Dimension(180, 35));
        bookBtn.setOpaque(true); // Required for Mac compatibility
        buttonPanel.add(bookBtn);

        // Load all flights initially
        loadFlights(model, null, null);

        // Event Listeners
        searchBtn.addActionListener(e -> {
            String from = (String) fromCombo.getSelectedItem();
            String to = (String) toCombo.getSelectedItem();
            if ("All Destinations".equals(from)) from = null;
            if ("All Destinations".equals(to)) to = null;
            loadFlights(model, from, to);
        });

        clearBtn.addActionListener(e -> {
            fromCombo.setSelectedIndex(0);
            toCombo.setSelectedIndex(0);
            loadFlights(model, null, null);
        });

        bookBtn.addActionListener(e -> {
            int selectedRow = flightTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a flight to book!");
                return;
            }
            String flightNumber = (String) model.getValueAt(selectedRow, 0);
            Flight flight = flightService.getFlightByNumber(flightNumber);
            if (flight != null) {
                // Check if flight status allows booking
                String flightStatus = flight.getStatus() != null ? flight.getStatus() : "SCHEDULED";
                if (!"SCHEDULED".equals(flightStatus)) {
                    String message = "This flight is currently " + flightStatus.toLowerCase() + " and cannot be booked.";
                    if ("CANCELLED".equals(flightStatus)) {
                        message = "This flight has been cancelled and cannot be booked.";
                    } else if ("BOARDING".equals(flightStatus)) {
                        message = "This flight is currently boarding and cannot be booked.";
                    } else if ("DEPARTED".equals(flightStatus)) {
                        message = "This flight has already departed and cannot be booked.";
                    } else if ("ARRIVED".equals(flightStatus)) {
                        message = "This flight has already arrived and cannot be booked.";
                    } else if ("DELAYED".equals(flightStatus)) {
                        message = "This flight is currently delayed. Please check back later or contact customer service.";
                    }
                    JOptionPane.showMessageDialog(this, message, "Flight Not Available", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                new SeatSelectionDialog(this, flight, customer, flightService).setVisible(true);
                // Refresh the table after booking
                String from = (String) fromCombo.getSelectedItem();
                String to = (String) toCombo.getSelectedItem();
                if ("All Destinations".equals(from)) from = null;
                if ("All Destinations".equals(to)) to = null;
                loadFlights(model, from, to);
            }
        });
        
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

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void loadFlights(DefaultTableModel model, String from, String to) {
        // Reload data to ensure we have the latest seat information
        flightService.reloadData();
        
        model.setRowCount(0);
        List<Flight> flights;
        
        if (from != null && !from.isEmpty() && to != null && !to.isEmpty()) {
            flights = flightService.searchFlights(from, to, null);
        } else {
            flights = flightService.getAllFlights();
        }
        
        for (Flight flight : flights) {
            Route route = flightService.getRouteById(flight.getRouteId());
            Aircraft aircraft = flightService.getAircraftById(flight.getAircraftId());
            
            if (route != null && aircraft != null) {
                List<FlightSeat> flightSeats = flightService.getFlightSeats(flight.getId());
                int availableSeats = (int) flightSeats.stream().filter(FlightSeat::isAvailable).count();
                int soldSeats = (int) flightSeats.stream().filter(FlightSeat::isSold).count();
                
                Object[] row = {
                    flight.getFlightNumber(),
                    route.getDeparture(),
                    route.getArrival(),
                    flight.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    flight.getArrivalTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    aircraft.getModel(),
                    String.format("$%.2f", flight.getPrice()),
                    availableSeats + "/" + aircraft.getCapacity(),
                    flight.getStatus() != null ? flight.getStatus() : "SCHEDULED"
                };
                model.addRow(row);
            }
        }
    }

    private JPanel createMyTicketsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        String[] columns = {"PNR", "Flight", "From", "To", "Seat", "Departure", "Gate", "Status", "Price"};
        ticketsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable ticketTable = new JTable(ticketsTableModel);
        ticketTable.getTableHeader().setBackground(Color.BLACK);
        ticketTable.getTableHeader().setForeground(Color.WHITE);
        ticketTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        ticketTable.getTableHeader().setReorderingAllowed(false);
        ticketTable.getTableHeader().setResizingAllowed(false);
        ticketTable.setRowHeight(30);
        ticketTable.setFont(new Font("Arial", Font.PLAIN, 12));
        ticketTable.setBackground(Color.WHITE);
        ticketTable.setForeground(Color.BLACK);
        ticketTable.setSelectionBackground(new Color(184, 207, 229));
        ticketTable.setSelectionForeground(Color.BLACK);
        ticketTable.setToolTipText("Double-click on a ticket to view detailed information");
        JScrollPane scrollPane = new JScrollPane(ticketTable);
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        JButton cancelBtn = new JButton("Cancel Ticket");
        JButton refreshBtn = new JButton("Refresh");
        
        cancelBtn.setBackground(new Color(255, 87, 87));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 12));
        cancelBtn.setPreferredSize(new Dimension(120, 35));
        cancelBtn.setOpaque(true); // Required for Mac compatibility
        
        refreshBtn.setBackground(new Color(158, 158, 158));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 12));
        refreshBtn.setPreferredSize(new Dimension(120, 35));
        refreshBtn.setOpaque(true); // Required for Mac compatibility
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(refreshBtn);

        // Load tickets
        loadCustomerTickets(ticketsTableModel);

        // Add double-click listener to show ticket details
        ticketTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int selectedRow = ticketTable.getSelectedRow();
                    if (selectedRow != -1) {
                        String pnr = (String) ticketsTableModel.getValueAt(selectedRow, 0);
                        showTicketDetailsDialog(pnr);
                    }
                }
            }
        });

        // Event Listeners
        refreshBtn.addActionListener(e -> {
            // Reload data to ensure we have the latest tickets
            bookingService.reloadData();
            loadCustomerTickets(ticketsTableModel);
        });
        
        cancelBtn.addActionListener(e -> {
            int selectedRow = ticketTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a ticket to cancel!");
                return;
            }
            
            String pnr = (String) ticketsTableModel.getValueAt(selectedRow, 0);
            String status = (String) ticketsTableModel.getValueAt(selectedRow, 7);
            String seatInfo = (String) ticketsTableModel.getValueAt(selectedRow, 4);
            
            if (!"PURCHASED".equals(status)) {
                JOptionPane.showMessageDialog(this, "Only purchased tickets can be cancelled!");
                return;
            }
            
            // Enhanced confirmation dialog with seat information
            String message = String.format(
                "Are you sure you want to cancel this ticket?\n\n" +
                "PNR: %s\n" +
                "Seats: %s\n\n" +
                "This action cannot be undone.",
                pnr, seatInfo
            );
            
            int result = JOptionPane.showConfirmDialog(this,
                message,
                "Cancel Ticket",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
                
            if (result == JOptionPane.YES_OPTION) {
                if (bookingService.cancelTicket(pnr, customer.getId())) {
                    // Reload flight service data to reflect seat changes
                    flightService.reloadData();
                    
                    JOptionPane.showMessageDialog(this, 
                        "Ticket cancelled successfully!\nAll seats have been freed up.", 
                        "Cancellation Successful", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadCustomerTickets(ticketsTableModel);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to cancel ticket!\nPlease try again or contact support.", 
                        "Cancellation Failed", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void loadCustomerTickets(DefaultTableModel model) {
        // Always reload data to ensure we have the latest tickets
        bookingService.reloadData();
        
        model.setRowCount(0);
        List<Ticket> tickets = bookingService.getCustomerTickets(customer.getId());
        
        for (Ticket ticket : tickets) {
            Flight flight = flightService.getFlightById(ticket.getFlightId());
            if (flight != null) {
                Route route = flightService.getRouteById(flight.getRouteId());
                if (route != null) {
                    String gateInfo = "TBD";
                    if (ticket.getGateNumber() != null && !ticket.getGateNumber().isEmpty()) {
                        gateInfo = ticket.getGateNumber();
                        if (ticket.getTerminal() != null && !ticket.getTerminal().isEmpty()) {
                            gateInfo += " (Terminal " + ticket.getTerminal() + ")";
                        }
                    }
                    
                    // Display seats properly - either single seat or multiple seats
                    String seatDisplay;
                    if (ticket.getSeatNumbers() != null && !ticket.getSeatNumbers().isEmpty()) {
                        if (ticket.getSeatNumbers().size() > 1) {
                            seatDisplay = ticket.getFormattedSeatNumbers() + " (" + ticket.getSeatCount() + " seats)";
                        } else {
                            seatDisplay = ticket.getFormattedSeatNumbers();
                        }
                    } else {
                        // Handle legacy tickets or tickets with no seat data
                        String legacySeat = ticket.getSeatNumber();
                        seatDisplay = (legacySeat != null && !legacySeat.isEmpty()) ? legacySeat : "No seat assigned";
                    }
                    
                    Object[] row = {
                        ticket.getPnr(),
                        flight.getFlightNumber(),
                        route.getDeparture(),
                        route.getArrival(),
                        seatDisplay,
                        flight.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        gateInfo,
                        ticket.getStatus(),
                        String.format("$%.2f", ticket.getTotalPrice())
                    };
                    model.addRow(row);
                }
            }
        }
    }





    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField nameField = new JTextField(customer.getName(), 20);
        JTextField emailField = new JTextField(customer.getEmail(), 20);
        JTextField phoneField = new JTextField(customer.getPhone(), 20);
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setSelectedItem(customer.getGender());

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Customer ID:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(customer.getId()), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        panel.add(genderCombo, gbc);

        JButton updateBtn = new JButton("Update Profile");
        updateBtn.setBackground(new Color(72, 201, 176));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setOpaque(true); // Required for Mac compatibility
        
        gbc.gridx = 1; gbc.gridy = 5;
        panel.add(updateBtn, gbc);

        updateBtn.addActionListener(e -> {
            customer.setName(nameField.getText().trim());
            customer.setEmail(emailField.getText().trim());
            customer.setPhone(phoneField.getText().trim());
            customer.setGender((String) genderCombo.getSelectedItem());
            
            // Note: In a real implementation, you would update through UserService
            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
        });

        return panel;
    }

        // Public method to refresh tickets and switch to tickets tab
    public void refreshTicketsAndSwitchTab() {
        // Reload data to ensure we have the latest tickets
        bookingService.reloadData();
        
        if (ticketsTableModel != null) {
            loadCustomerTickets(ticketsTableModel);
        }
        // Switch to "My Tickets" tab (index 1)
        if (tabbedPane != null) {
            tabbedPane.setSelectedIndex(1);
        }
    }

        private void showTicketDetailsDialog(String pnr) {
        Ticket ticket = bookingService.getTicketByPNR(pnr);
        if (ticket == null) {
            JOptionPane.showMessageDialog(this, "Ticket not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Ticket Details - " + pnr, true);
        dialog.setSize(950, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Get flight and route information
        Flight flight = flightService.getFlightById(ticket.getFlightId());
        Route route = flight != null ? flightService.getRouteById(flight.getRouteId()) : null;
        Aircraft aircraft = flight != null ? flightService.getAircraftById(flight.getAircraftId()) : null;

        // Header Panel with gradient background
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setPreferredSize(new Dimension(0, 100));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JPanel headerLeft = new JPanel(new GridLayout(3, 1, 0, 2));
        headerLeft.setBackground(new Color(70, 130, 180));
        
        JLabel titleLabel = new JLabel("BOARDING PASS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel pnrLabel = new JLabel("PNR: " + ticket.getPnr());
        pnrLabel.setFont(new Font("Arial", Font.BOLD, 16));
        pnrLabel.setForeground(new Color(220, 220, 220));
        
        JLabel statusLabel = new JLabel("Status: " + ticket.getStatus());
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground("PURCHASED".equals(ticket.getStatus()) ? 
            new Color(144, 238, 144) : new Color(255, 182, 193));
        
        headerLeft.add(titleLabel);
        headerLeft.add(pnrLabel);
        headerLeft.add(statusLabel);
        
        headerPanel.add(headerLeft, BorderLayout.WEST);

        // Main Content Panel - Two columns layout
        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Left Column - Flight and Passenger Information
        JPanel leftColumn = new JPanel(new GridBagLayout());
        leftColumn.setBackground(Color.WHITE);
        leftColumn.setPreferredSize(new Dimension(450, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 0, 6, 15);

        // Flight Information Section
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel flightSectionLabel = new JLabel("FLIGHT INFORMATION");
        flightSectionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        flightSectionLabel.setForeground(new Color(70, 130, 180));
        flightSectionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        leftColumn.add(flightSectionLabel, gbc);

        gbc.gridwidth = 1;
        if (flight != null && route != null) {
            gbc.gridx = 0; gbc.gridy = 1;
            leftColumn.add(createInfoLabel("Flight:", new Color(100, 100, 100)), gbc);
            gbc.gridx = 1;
            leftColumn.add(createValueLabel(flight.getFlightNumber()), gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            leftColumn.add(createInfoLabel("From:", new Color(100, 100, 100)), gbc);
            gbc.gridx = 1;
            leftColumn.add(createValueLabel(route.getDeparture()), gbc);

            gbc.gridx = 0; gbc.gridy = 3;
            leftColumn.add(createInfoLabel("To:", new Color(100, 100, 100)), gbc);
            gbc.gridx = 1;
            leftColumn.add(createValueLabel(route.getArrival()), gbc);

            gbc.gridx = 0; gbc.gridy = 4;
            leftColumn.add(createInfoLabel("Departure:", new Color(100, 100, 100)), gbc);
            gbc.gridx = 1;
            leftColumn.add(createValueLabel(flight.getDepartureTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))), gbc);

            gbc.gridx = 0; gbc.gridy = 5;
            leftColumn.add(createInfoLabel("Arrival:", new Color(100, 100, 100)), gbc);
            gbc.gridx = 1;
            leftColumn.add(createValueLabel(flight.getArrivalTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))), gbc);
        }

        // Seat Information Section
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 0, 8, 15);
        JLabel seatSectionLabel = new JLabel("SEAT INFORMATION");
        seatSectionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        seatSectionLabel.setForeground(new Color(255, 152, 0));
        leftColumn.add(seatSectionLabel, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 0, 6, 15);
        String seatInfo;
        if (ticket.getSeatNumbers() != null && !ticket.getSeatNumbers().isEmpty()) {
            if (ticket.getSeatNumbers().size() > 1) {
                seatInfo = ticket.getFormattedSeatNumbers() + " (" + ticket.getSeatCount() + " seats)";
            } else {
                seatInfo = ticket.getFormattedSeatNumbers();
            }
        } else {
            String legacySeat = ticket.getSeatNumber();
            seatInfo = (legacySeat != null && !legacySeat.isEmpty()) ? legacySeat : "No seat assigned";
        }

        gbc.gridx = 0; gbc.gridy = 7;
        leftColumn.add(createInfoLabel("Seat(s):", new Color(100, 100, 100)), gbc);
        gbc.gridx = 1;
        leftColumn.add(createValueLabel(seatInfo), gbc);

        // Gate Information Section
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 0, 8, 15);
        JLabel gateSectionLabel = new JLabel("GATE INFORMATION");
        gateSectionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gateSectionLabel.setForeground(new Color(96, 125, 139));
        leftColumn.add(gateSectionLabel, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 0, 6, 15);
        String gateNumber = (ticket.getGateNumber() != null && !ticket.getGateNumber().isEmpty()) ? 
            ticket.getGateNumber() : "TBD";
        String terminal = (ticket.getTerminal() != null && !ticket.getTerminal().isEmpty()) ? 
            ticket.getTerminal() : "TBD";

        gbc.gridx = 0; gbc.gridy = 9;
        leftColumn.add(createInfoLabel("Gate:", new Color(100, 100, 100)), gbc);
        gbc.gridx = 1;
        leftColumn.add(createValueLabel(gateNumber), gbc);

        gbc.gridx = 0; gbc.gridy = 10;
        leftColumn.add(createInfoLabel("Terminal:", new Color(100, 100, 100)), gbc);
        gbc.gridx = 1;
        leftColumn.add(createValueLabel(terminal), gbc);

        // Payment Information Section
        gbc.gridx = 0; gbc.gridy = 11; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 0, 8, 15);
        JLabel paymentSectionLabel = new JLabel("PAYMENT INFORMATION");
        paymentSectionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        paymentSectionLabel.setForeground(new Color(156, 39, 176));
        leftColumn.add(paymentSectionLabel, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 0, 6, 15);
        gbc.gridx = 0; gbc.gridy = 12;
        leftColumn.add(createInfoLabel("Total Price:", new Color(100, 100, 100)), gbc);
        gbc.gridx = 1;
        leftColumn.add(createValueLabel(String.format("$%.2f", ticket.getTotalPrice())), gbc);

        gbc.gridx = 0; gbc.gridy = 13;
        leftColumn.add(createInfoLabel("Purchase Date:", new Color(100, 100, 100)), gbc);
        gbc.gridx = 1;
        String purchaseDate = "Not available";
        if (ticket.getPurchaseTime() != null) {
            purchaseDate = ticket.getPurchaseTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
        }
        leftColumn.add(createValueLabel(purchaseDate), gbc);

        // Right Column - Aircraft Information and Image
        JPanel rightColumn = new JPanel(new BorderLayout());
        rightColumn.setBackground(Color.WHITE);
        rightColumn.setPreferredSize(new Dimension(400, 0));

        if (aircraft != null) {
            // Aircraft Information Header
            JPanel aircraftHeaderPanel = new JPanel(new BorderLayout());
            aircraftHeaderPanel.setBackground(Color.WHITE);
            aircraftHeaderPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
            
            JLabel aircraftSectionLabel = new JLabel("AIRCRAFT INFORMATION");
            aircraftSectionLabel.setFont(new Font("Arial", Font.BOLD, 16));
            aircraftSectionLabel.setForeground(new Color(76, 175, 80));
            aircraftHeaderPanel.add(aircraftSectionLabel, BorderLayout.NORTH);

            // Aircraft Image Panel (only if image is available)
            JPanel aircraftImagePanel = null;
            String aircraftImagePath = aircraft.getImagePath();
            if (aircraftImagePath != null && !aircraftImagePath.trim().isEmpty()) {
                ImageIcon aircraftIcon = ImageUtil.getAircraftImage(aircraftImagePath, 320, 160);
                if (aircraftIcon != null) {
                    aircraftImagePanel = new JPanel(new BorderLayout());
                    aircraftImagePanel.setBackground(Color.WHITE);
                    aircraftImagePanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    ));
                    
                    JLabel aircraftImageLabel = new JLabel(aircraftIcon, JLabel.CENTER);
                    aircraftImagePanel.add(aircraftImageLabel, BorderLayout.CENTER);
                }
            }

            // Aircraft Details Panel
            JPanel aircraftDetailsPanel = new JPanel(new GridBagLayout());
            aircraftDetailsPanel.setBackground(Color.WHITE);
            aircraftDetailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            
            GridBagConstraints aircraftGbc = new GridBagConstraints();
            aircraftGbc.anchor = GridBagConstraints.WEST;
            aircraftGbc.insets = new Insets(4, 0, 4, 10);

            aircraftGbc.gridx = 0; aircraftGbc.gridy = 0;
            aircraftDetailsPanel.add(createInfoLabel("Model:", new Color(100, 100, 100)), aircraftGbc);
            aircraftGbc.gridx = 1;
            aircraftDetailsPanel.add(createValueLabel(aircraft.getModel()), aircraftGbc);

            aircraftGbc.gridx = 0; aircraftGbc.gridy = 1;
            aircraftDetailsPanel.add(createInfoLabel("Registration:", new Color(100, 100, 100)), aircraftGbc);
            aircraftGbc.gridx = 1;
            aircraftDetailsPanel.add(createValueLabel(aircraft.getRegistrationNumber()), aircraftGbc);

            aircraftGbc.gridx = 0; aircraftGbc.gridy = 2;
            aircraftDetailsPanel.add(createInfoLabel("Manufacturer:", new Color(100, 100, 100)), aircraftGbc);
            aircraftGbc.gridx = 1;
            aircraftDetailsPanel.add(createValueLabel(aircraft.getManufacturer()), aircraftGbc);

            aircraftGbc.gridx = 0; aircraftGbc.gridy = 3;
            aircraftDetailsPanel.add(createInfoLabel("Capacity:", new Color(100, 100, 100)), aircraftGbc);
            aircraftGbc.gridx = 1;
            aircraftDetailsPanel.add(createValueLabel(aircraft.getCapacity() + " passengers"), aircraftGbc);

            rightColumn.add(aircraftHeaderPanel, BorderLayout.NORTH);
            if (aircraftImagePanel != null) {
                rightColumn.add(aircraftImagePanel, BorderLayout.CENTER);
                rightColumn.add(aircraftDetailsPanel, BorderLayout.SOUTH);
            } else {
                // No image available, put details in center for better spacing
                rightColumn.add(aircraftDetailsPanel, BorderLayout.CENTER);
            }
        } else {
            // No aircraft information available
            JLabel noAircraftLabel = new JLabel("Aircraft information not available", JLabel.CENTER);
            noAircraftLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            noAircraftLabel.setForeground(new Color(150, 150, 150));
            rightColumn.add(noAircraftLabel, BorderLayout.CENTER);
        }

        contentPanel.add(leftColumn, BorderLayout.WEST);
        contentPanel.add(rightColumn, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(8, 0, 8, 0)
        ));

        if ("PURCHASED".equals(ticket.getStatus())) {
            JButton cancelBtn = new JButton("Cancel Ticket");
            cancelBtn.setPreferredSize(new Dimension(130, 40));
            cancelBtn.setBackground(new Color(244, 67, 54));
            cancelBtn.setForeground(Color.WHITE);
            cancelBtn.setFont(new Font("Arial", Font.BOLD, 12));
            cancelBtn.setFocusPainted(false);
            cancelBtn.setOpaque(true); // Required for Mac compatibility
            cancelBtn.addActionListener(e -> {
                dialog.dispose();
                // Trigger the cancel ticket functionality
                int result = JOptionPane.showConfirmDialog(this,
                    String.format("Are you sure you want to cancel this ticket?\n\nPNR: %s\nSeats: %s\n\nThis action cannot be undone.",
                        ticket.getPnr(), seatInfo),
                    "Cancel Ticket",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                    
                if (result == JOptionPane.YES_OPTION) {
                    if (bookingService.cancelTicket(ticket.getPnr(), customer.getId())) {
                        JOptionPane.showMessageDialog(this, 
                            "Ticket cancelled successfully!\nAll seats have been freed up.", 
                            "Cancellation Successful", 
                            JOptionPane.INFORMATION_MESSAGE);
                        loadCustomerTickets(ticketsTableModel);
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Failed to cancel ticket!\nPlease try again or contact support.", 
                            "Cancellation Failed", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            buttonPanel.add(cancelBtn);
        }

        JButton closeBtn = new JButton("Close");
        closeBtn.setPreferredSize(new Dimension(100, 40));
        closeBtn.setBackground(new Color(158, 158, 158));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFont(new Font("Arial", Font.BOLD, 12));
        closeBtn.setFocusPainted(false);
        closeBtn.setOpaque(true); // Required for Mac compatibility
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    // Helper methods for creating consistent labels
    private JLabel createInfoLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        label.setForeground(color);
        label.setPreferredSize(new Dimension(110, 18));
        return label;
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(new Color(50, 50, 50));
        label.setPreferredSize(new Dimension(250, 18));
        return label;
    }

    private JPanel createDetailSection(String title, String content, Color accentColor) {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 0, 10, 0),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            )
        ));

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(accentColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        // Content
        JTextArea contentArea = new JTextArea(content);
        contentArea.setFont(new Font("Arial", Font.PLAIN, 12));
        contentArea.setForeground(new Color(60, 60, 60));
        contentArea.setBackground(Color.WHITE);
        contentArea.setEditable(false);
        contentArea.setOpaque(false);

        section.add(titleLabel, BorderLayout.NORTH);
        section.add(contentArea, BorderLayout.CENTER);

        return section;
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

    private void showFlightDetailsDialog(Flight flight) {
        JDialog dialog = new JDialog(this, "Flight Details - " + flight.getFlightNumber(), true);
        dialog.setSize(700, 600);
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

        // Content Panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Get related data
        Route route = flightService.getRouteById(flight.getRouteId());
        Aircraft aircraft = flightService.getAircraftById(flight.getAircraftId());

        // Flight Information
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel flightInfoLabel = new JLabel("Flight Information");
        flightInfoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        contentPanel.add(flightInfoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        contentPanel.add(new JLabel("Flight Number:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(new JLabel(flight.getFlightNumber()), gbc);

        if (route != null) {
            gbc.gridx = 0; gbc.gridy = 2;
            contentPanel.add(new JLabel("Route:"), gbc);
            gbc.gridx = 1;
            contentPanel.add(new JLabel(route.getDeparture() + " → " + route.getArrival()), gbc);
        }

        gbc.gridx = 0; gbc.gridy = 3;
        contentPanel.add(new JLabel("Departure:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(new JLabel(flight.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))), gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        contentPanel.add(new JLabel("Arrival:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(new JLabel(flight.getArrivalTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))), gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        contentPanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(new JLabel(String.format("$%.2f", flight.getPrice())), gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        contentPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        JLabel statusLabel = new JLabel(flight.getStatus() != null ? flight.getStatus() : "SCHEDULED");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        // Color code the status
        String status = statusLabel.getText();
        switch (status) {
            case "SCHEDULED":
                statusLabel.setForeground(new Color(33, 150, 243)); // Blue
                break;
            case "BOARDING":
                statusLabel.setForeground(new Color(255, 193, 7)); // Yellow
                break;
            case "DEPARTED":
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
        contentPanel.add(statusLabel, gbc);

        // Aircraft Information
        if (aircraft != null) {
            gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
            JLabel aircraftInfoLabel = new JLabel("Aircraft Information");
            aircraftInfoLabel.setFont(new Font("Arial", Font.BOLD, 16));
            contentPanel.add(aircraftInfoLabel, gbc);

            gbc.gridwidth = 1;
            gbc.gridx = 0; gbc.gridy = 7;
            contentPanel.add(new JLabel("Model:"), gbc);
            gbc.gridx = 1;
            contentPanel.add(new JLabel(aircraft.getModel()), gbc);

            gbc.gridx = 0; gbc.gridy = 8;
            contentPanel.add(new JLabel("Registration:"), gbc);
            gbc.gridx = 1;
            contentPanel.add(new JLabel(aircraft.getRegistrationNumber()), gbc);

            gbc.gridx = 0; gbc.gridy = 9;
            contentPanel.add(new JLabel("Manufacturer:"), gbc);
            gbc.gridx = 1;
            contentPanel.add(new JLabel(aircraft.getManufacturer()), gbc);

            gbc.gridx = 0; gbc.gridy = 10;
            contentPanel.add(new JLabel("Capacity:"), gbc);
            gbc.gridx = 1;
            contentPanel.add(new JLabel(String.valueOf(aircraft.getCapacity())), gbc);

            // Aircraft Image (only if available and found)
            String aircraftImagePath = aircraft.getImagePath();
            if (aircraftImagePath != null && !aircraftImagePath.trim().isEmpty()) {
                // Use ImageUtil for proper scaling
                ImageIcon scaledIcon = ImageUtil.getAircraftImage(aircraftImagePath, 200, 120);
                if (scaledIcon != null) {
                    gbc.gridx = 0; gbc.gridy = 11; gbc.gridwidth = 2;
                    gbc.anchor = GridBagConstraints.CENTER;
                    
                    JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                    imagePanel.setBackground(Color.WHITE);
                    
                    JLabel aircraftImage = new JLabel(scaledIcon, JLabel.CENTER);
                    aircraftImage.setPreferredSize(new Dimension(200, 120));
                    
                    imagePanel.add(aircraftImage);
                    contentPanel.add(imagePanel, gbc);
                    
                    gbc.anchor = GridBagConstraints.WEST; // Reset anchor for other components
                }
            }
        }

        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(158, 158, 158));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFont(new Font("Arial", Font.BOLD, 12));
        closeBtn.setPreferredSize(new Dimension(100, 35));
        closeBtn.setOpaque(true); // Required for Mac compatibility
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
} 