package view;

import model.*;
import service.FlightService;
import util.ImageUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FlightBrowserFrame extends JFrame {
    private FlightService flightService;
    private Customer customer;
    private JTable flightTable;
    private DefaultTableModel tableModel;

    public FlightBrowserFrame(FlightService flightService, Customer customer) {
        this.flightService = flightService;
        this.customer = customer;
        
        setTitle("Browse Flights");
        setSize(1000, 600);
        setDefaultCloseOperation(customer == null ? JFrame.DISPOSE_ON_CLOSE : JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initializeUI();
        loadFlights();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("Flight Information", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Search Panel with better layout
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Flights"));
        searchPanel.setPreferredSize(new Dimension(220, 200));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Get unique origins and destinations from all flights
        java.util.Set<String> origins = new java.util.HashSet<>();
        java.util.Set<String> destinations = new java.util.HashSet<>();
        
        List<Flight> allFlights = flightService.getAllFlights();
        for (Flight flight : allFlights) {
            Route route = flightService.getRouteById(flight.getRouteId());
            if (route != null) {
                origins.add(route.getDeparture());
                destinations.add(route.getArrival());
            }
        }
        
        // Style the dropdowns
        JComboBox<String> departureCombo = new JComboBox<>();
        departureCombo.addItem("All Origins");
        for (String origin : origins) {
            departureCombo.addItem(origin);
        }
        departureCombo.setPreferredSize(new Dimension(180, 35));
        departureCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JComboBox<String> arrivalCombo = new JComboBox<>();
        arrivalCombo.addItem("All Destinations");
        for (String destination : destinations) {
            arrivalCombo.addItem(destination);
        }
        arrivalCombo.setPreferredSize(new Dimension(180, 35));
        arrivalCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Style the buttons
        JButton searchBtn = new JButton("Search");
        searchBtn.setPreferredSize(new Dimension(80, 35));
        searchBtn.setBackground( new Color(76, 175, 80));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("Arial", Font.BOLD, 12));
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        
        JButton clearBtn = new JButton("Clear");
        clearBtn.setPreferredSize(new Dimension(80, 35));
        clearBtn.setBackground(new Color(244, 67, 54));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFont(new Font("Arial", Font.BOLD, 12));
        clearBtn.setFocusPainted(false);
        clearBtn.setBorderPainted(false);

        // Layout components vertically
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel fromLabel = new JLabel("From:");
        fromLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchPanel.add(fromLabel, gbc);
        
        gbc.gridy = 1;
        searchPanel.add(departureCombo, gbc);
        
        gbc.gridy = 2;
        JLabel toLabel = new JLabel("To:");
        toLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchPanel.add(toLabel, gbc);
        
        gbc.gridy = 3;
        searchPanel.add(arrivalCombo, gbc);
        
        // Search button panel
        gbc.gridy = 4; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel searchButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        searchButtonPanel.setBackground(searchPanel.getBackground());
        searchButtonPanel.add(searchBtn);
        searchButtonPanel.add(clearBtn);
        
        gbc.gridwidth = 2;
        searchPanel.add(searchButtonPanel, gbc);

        // Table
        String[] columns = {"Flight Number", "From", "To", "Departure", "Arrival", "Price", "Available Seats", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        flightTable = new JTable(tableModel);
        flightTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Style table header
        flightTable.getTableHeader().setBackground(Color.BLACK);
        flightTable.getTableHeader().setForeground(Color.WHITE);
        flightTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        flightTable.getTableHeader().setReorderingAllowed(false); // Prevent column reordering
        flightTable.getTableHeader().setResizingAllowed(false); // Prevent column resizing
        
        flightTable.setRowHeight(25);
        flightTable.setBackground(Color.WHITE);
        flightTable.setForeground(Color.BLACK);
        flightTable.setSelectionBackground(new Color(184, 207, 229));
        flightTable.setSelectionForeground(Color.BLACK);
        flightTable.setGridColor(Color.LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane(flightTable);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        if (customer != null) {
            JButton bookBtn = new JButton("Book Flight");
            bookBtn.setBackground(new Color(72, 201, 176));
            bookBtn.setForeground(Color.WHITE);
            bookBtn.addActionListener(e -> bookSelectedFlight());
            buttonPanel.add(bookBtn);
        }
        
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        buttonPanel.add(closeBtn);
        
        // Add double-click listener to show flight details
        flightTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int selectedRow = flightTable.getSelectedRow();
                    if (selectedRow != -1) {
                        String flightNumber = (String) tableModel.getValueAt(selectedRow, 0);
                        Flight flight = flightService.getFlightByNumber(flightNumber);
                        if (flight != null) {
                            showFlightDetailsDialog(flight);
                        }
                    }
                }
            }
        });

        // Add logic to prevent same origin and destination
        departureCombo.addActionListener(e -> {
            String selectedDeparture = (String) departureCombo.getSelectedItem();
            String selectedArrival = (String) arrivalCombo.getSelectedItem();
            
            if (selectedDeparture != null && selectedDeparture.equals(selectedArrival) && 
                !selectedDeparture.equals("All Origins") && !selectedArrival.equals("All Destinations")) {
                JOptionPane.showMessageDialog(this, "Origin and destination cannot be the same!");
                departureCombo.setSelectedIndex(0); // Reset to "All Origins"
            }
        });
        
        arrivalCombo.addActionListener(e -> {
            String selectedDeparture = (String) departureCombo.getSelectedItem();
            String selectedArrival = (String) arrivalCombo.getSelectedItem();
            
            if (selectedDeparture != null && selectedDeparture.equals(selectedArrival) && 
                !selectedDeparture.equals("All Origins") && !selectedArrival.equals("All Destinations")) {
                JOptionPane.showMessageDialog(this, "Origin and destination cannot be the same!");
                arrivalCombo.setSelectedIndex(0); // Reset to "All Destinations"
            }
        });

        // Event Listeners
        searchBtn.addActionListener(e -> {
            String departure = (String) departureCombo.getSelectedItem();
            String arrival = (String) arrivalCombo.getSelectedItem();
            
            // Convert "All Origins" and "All Destinations" to empty strings for search
            if ("All Origins".equals(departure)) departure = "";
            if ("All Destinations".equals(arrival)) arrival = "";
            
            searchFlights(departure, arrival);
        });

        clearBtn.addActionListener(e -> {
            departureCombo.setSelectedIndex(0); // "All Origins"
            arrivalCombo.setSelectedIndex(0); // "All Destinations"
            loadFlights();
        });

        add(headerPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadFlights() {
        // Reload data to ensure we have the latest seat information
        flightService.reloadData();
        
        tableModel.setRowCount(0);
        List<Flight> flights = flightService.getAllFlights();
        
        for (Flight flight : flights) {
            Route route = flightService.getRouteById(flight.getRouteId());
            Aircraft aircraft = flightService.getAircraftById(flight.getAircraftId());
            
            if (route != null && aircraft != null) {
                Object[] row = {
                    flight.getFlightNumber(),
                    route.getDeparture(),
                    route.getArrival(),
                    flight.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    flight.getArrivalTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    String.format("$%.2f", flight.getPrice()),
                    getAvailableSeats(aircraft, flight.getId()),
                    flight.getStatus() != null ? flight.getStatus() : "SCHEDULED"
                };
                tableModel.addRow(row);
            }
        }
    }

    private void searchFlights(String departure, String arrival) {
        // Reload data to ensure we have the latest seat information
        flightService.reloadData();
        
        tableModel.setRowCount(0);
        List<Flight> flights = flightService.searchFlights(departure, arrival, null);
        
        for (Flight flight : flights) {
            Route route = flightService.getRouteById(flight.getRouteId());
            Aircraft aircraft = flightService.getAircraftById(flight.getAircraftId());
            
            if (route != null && aircraft != null) {
                Object[] row = {
                    flight.getFlightNumber(),
                    route.getDeparture(),
                    route.getArrival(),
                    flight.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    flight.getArrivalTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    String.format("$%.2f", flight.getPrice()),
                    getAvailableSeats(aircraft, flight.getId()),
                    flight.getStatus() != null ? flight.getStatus() : "SCHEDULED"
                };
                tableModel.addRow(row);
            }
        }
    }

    private int getAvailableSeats(Aircraft aircraft, String flightId) {
        return flightService.getAvailableSeats(flightId).size();
    }

    private void bookSelectedFlight() {
        int selectedRow = flightTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a flight to book!");
            return;
        }

        String flightNumber = (String) tableModel.getValueAt(selectedRow, 0);
        Flight flight = flightService.getFlightByNumber(flightNumber);
        
        if (flight != null) {
            // Open seat selection dialog
            new SeatSelectionDialog(this, flight, customer, flightService).setVisible(true);
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
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
} 