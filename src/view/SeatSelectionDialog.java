package view;

import model.*;
import service.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class SeatSelectionDialog extends JDialog {
    private Flight flight;
    private Customer customer;
    private FlightService flightService;
    private BookingService bookingService;
    private JPanel seatGridPanel;
    private List<FlightSeat> selectedSeats;
    private JButton[][] seatButtons;
    private JLabel selectedSeatLabel;
    private JLabel priceLabel;
    private static final int MAX_SEATS = 4;
    
    public SeatSelectionDialog(JFrame parent, Flight flight, Customer customer, FlightService flightService) {
        super(parent, "Select Your Seats - " + flight.getFlightNumber(), true);
        this.flight = flight;
        this.customer = customer;
        this.flightService = flightService;
        this.bookingService = new BookingService(flightService);
        this.selectedSeats = new ArrayList<>();
        
        // Reload data to ensure we have the latest seat information
        this.flightService.reloadData();
        
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initializeUI();
        updateBookingService();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        // Header Panel with Flight Info
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content with seat grid
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        // Bottom panel with booking controls
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(70, 130, 180));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Flight details
        Aircraft aircraft = flightService.getAircraftById(flight.getAircraftId());
        Route route = flightService.getRouteById(flight.getRouteId());
        
        JLabel flightInfo = new JLabel(String.format(
            "<html><center><h2 style='color: white;'>%s</h2>" +
            "<p style='color: white; font-size: 14px;'>%s → %s</p>" +
            "<p style='color: white; font-size: 12px;'>Aircraft: %s (Capacity: %d)</p></center></html>",
            flight.getFlightNumber(),
            route.getDeparture(),
            route.getArrival(),
            aircraft.getModel(),
            aircraft.getCapacity()
        ), JLabel.CENTER);
        
        panel.add(flightInfo, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Legend Panel
        JPanel legendPanel = createLegendPanel();
        panel.add(legendPanel, BorderLayout.NORTH);

        // Seat Grid Panel
        seatGridPanel = createSeatGridPanel();
        JScrollPane scrollPane = new JScrollPane(seatGridPanel);
        scrollPane.setPreferredSize(new Dimension(700, 350));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Seat Legend"));

        // Legend items
        String[] labels = {"Available", "Sold", "Selected"};
        Color[] colors = {new Color(76, 175, 80), new Color(244, 67, 54), new Color(33, 150, 243)};

        for (int i = 0; i < labels.length; i++) {
            JPanel legendItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            legendItem.setBackground(Color.WHITE);
            
            JPanel colorBox = new JPanel();
            colorBox.setPreferredSize(new Dimension(20, 20));
            colorBox.setBackground(colors[i]);
            colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Arial", Font.PLAIN, 12));
            
            legendItem.add(colorBox);
            legendItem.add(label);
            panel.add(legendItem);
        }

        return panel;
    }

    private JPanel createSeatGridPanel() {
        Aircraft aircraft = flightService.getAircraftById(flight.getAircraftId());
        List<FlightSeat> seats = flightService.getFlightSeats(flight.getId());
        
        // Calculate grid dimensions (6 seats per row: 3-3 with aisle)
        int seatsPerRow = 6;
        int rows = aircraft.getRows();
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        seatButtons = new JButton[rows][seatsPerRow];

        // Add cockpit indicator
        JLabel cockpitLabel = new JLabel("✈ COCKPIT", JLabel.CENTER);
        cockpitLabel.setFont(new Font("Arial", Font.BOLD, 14));
        cockpitLabel.setForeground(new Color(70, 130, 180));
        cockpitLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        panel.add(cockpitLabel);

        // Create seat grid
        for (int row = 0; row < rows; row++) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            rowPanel.setBackground(Color.WHITE);

            // Row label
            JLabel rowLabel = new JLabel(String.valueOf(row + 1));
            rowLabel.setPreferredSize(new Dimension(30, 40));
            rowLabel.setHorizontalAlignment(JLabel.CENTER);
            rowLabel.setFont(new Font("Arial", Font.BOLD, 12));
            rowPanel.add(rowLabel);

            // Left side seats (A, B, C)
            for (int col = 0; col < 3; col++) {
                createSeatButton(row, col, rowPanel, seats, seatsPerRow);
            }

            // Aisle space
            JLabel aisleLabel = new JLabel("   ");
            aisleLabel.setPreferredSize(new Dimension(30, 40));
            rowPanel.add(aisleLabel);

            // Right side seats (D, E, F)
            for (int col = 3; col < 6; col++) {
                createSeatButton(row, col, rowPanel, seats, seatsPerRow);
            }

            panel.add(rowPanel);
        }

        return panel;
    }

    private void createSeatButton(int row, int col, JPanel rowPanel, List<FlightSeat> seats, int seatsPerRow) {
        int seatIndex = row * seatsPerRow + col;
        JButton seatButton = new JButton();
        
        if (seatIndex < seats.size()) {
            FlightSeat seat = seats.get(seatIndex);
            seatButton.setText(seat.getSeatNumber());
            seatButton.setPreferredSize(new Dimension(50, 40));
            seatButton.setFont(new Font("Arial", Font.BOLD, 12)); // Increased font size and made bold
            
            // Make button fully opaque
            seatButton.setOpaque(true);
            seatButton.setBorderPainted(false);
            seatButton.setFocusPainted(false);
            
            // Set color based on seat status
            if (seat.isSold()) {
                seatButton.setBackground(new Color(244, 67, 54)); // Red - Sold
                seatButton.setForeground(Color.WHITE);
                seatButton.setEnabled(false);
            } else {
                seatButton.setBackground(new Color(76, 175, 80)); // Green - Available
                seatButton.setForeground(Color.WHITE);
                seatButton.setEnabled(true);
                
                // Add click listener for available seats
                seatButton.addActionListener(e -> toggleSeatSelection(seat, seatButton));
            }
            
            seatButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
            seatButtons[row][col] = seatButton;
            rowPanel.add(seatButton);
        } else {
            // Empty seat placeholder - don't add invisible buttons, just add spacing
            JLabel spacer = new JLabel();
            spacer.setPreferredSize(new Dimension(50, 40));
            rowPanel.add(spacer);
        }
    }

    private void toggleSeatSelection(FlightSeat seat, JButton button) {
        if (selectedSeats.contains(seat)) {
            // Deselect seat
            selectedSeats.remove(seat);
            button.setBackground(new Color(76, 175, 80)); // Green - Available
            button.setForeground(Color.WHITE);
        } else {
            // Check if we can select more seats
            if (selectedSeats.size() >= MAX_SEATS) {
                JOptionPane.showMessageDialog(this, 
                    "You can select a maximum of " + MAX_SEATS + " seats per purchase.", 
                    "Maximum Seats Reached", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Select seat
            selectedSeats.add(seat);
            button.setBackground(new Color(33, 150, 243)); // Blue - Selected
            button.setForeground(Color.WHITE);
        }
        
        // Update labels
        updateSelectionInfo();
    }

    private void updateSelectionInfo() {
        if (!selectedSeats.isEmpty()) {
            if (selectedSeats.size() == 1) {
                selectedSeatLabel.setText("Selected Seat: " + selectedSeats.get(0).getSeatNumber());
            } else {
                StringBuilder seatNumbers = new StringBuilder();
                for (int i = 0; i < selectedSeats.size(); i++) {
                    if (i > 0) seatNumbers.append(", ");
                    seatNumbers.append(selectedSeats.get(i).getSeatNumber());
                }
                selectedSeatLabel.setText("Selected Seats (" + selectedSeats.size() + "): " + seatNumbers.toString());
            }
            double totalPrice = flight.getPrice() * selectedSeats.size();
            priceLabel.setText(String.format("Total Price: $%.2f", totalPrice));
        } else {
            selectedSeatLabel.setText("No seats selected");
            priceLabel.setText("Total Price: $0.00");
        }
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Selection info panel
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setBackground(Color.WHITE);
        
        selectedSeatLabel = new JLabel("No seats selected");
        selectedSeatLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        priceLabel = new JLabel("Total Price: $0.00");
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        priceLabel.setForeground(new Color(76, 175, 80));
        
        infoPanel.add(selectedSeatLabel);
        infoPanel.add(priceLabel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setBackground(new Color(244, 67, 54));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));
        cancelButton.addActionListener(e -> dispose());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.setBackground(new Color(158, 158, 158));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.addActionListener(e -> refreshSeatMap());

        JButton purchaseButton = new JButton("Purchase Seats");
        purchaseButton.setPreferredSize(new Dimension(130, 35));
        purchaseButton.setBackground(new Color(76, 175, 80));
        purchaseButton.setForeground(Color.WHITE);
        purchaseButton.setFont(new Font("Arial", Font.BOLD, 12));
        purchaseButton.addActionListener(this::purchaseSelectedSeats);

        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(purchaseButton);

        panel.add(infoPanel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private void purchaseSelectedSeats(ActionEvent e) {
        if (selectedSeats.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please select at least one seat before purchasing!", 
                "No Seats Selected", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if flight status allows booking
        String flightStatus = flight.getStatus() != null ? flight.getStatus() : "SCHEDULED";
        if (!"SCHEDULED".equals(flightStatus)) {
            String message = "This flight is not available for booking.\n\n" +
                           "Flight Status: " + flightStatus + "\n" +
                           "Only flights with 'SCHEDULED' status can be booked.";
            JOptionPane.showMessageDialog(this, message, "Flight Not Bookable", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Prepare seat numbers list
            List<String> seatNumbers = new ArrayList<>();
            StringBuilder seatLabels = new StringBuilder();
            
            for (int i = 0; i < selectedSeats.size(); i++) {
                FlightSeat seat = selectedSeats.get(i);
                String seatLabel = getSeatLabel(seat);
                seatNumbers.add(seatLabel);
                
                if (i > 0) seatLabels.append(", ");
                seatLabels.append(seatLabel);
            }
            
            double totalPrice = flight.getPrice() * selectedSeats.size();
            
            // Use the new multi-seat purchase method
            String pnr = bookingService.purchaseMultipleSeats(customer.getId(), flight.getId(), seatNumbers, flight.getPrice());
            
            if (pnr != null) {
                String message = String.format("Purchase Successful!\n\nPNR: %s\nFlight: %s\nSeats: %s\nTotal Price: $%.2f\n\nPlease save your PNR for future reference.",
                    pnr,
                    flight.getFlightNumber(),
                    seatLabels.toString(),
                    totalPrice);
                
                JOptionPane.showMessageDialog(this, message, "Purchase Confirmed", JOptionPane.INFORMATION_MESSAGE);
                
                // Reload flight service data to reflect seat changes
                flightService.reloadData();
                
                // Refresh parent window if it's a CustomerDashboardFrame
                java.awt.Window parentWindow = SwingUtilities.getWindowAncestor(this);
                if (parentWindow instanceof CustomerDashboardFrame) {
                    CustomerDashboardFrame parentFrame = (CustomerDashboardFrame) parentWindow;
                    parentFrame.refreshTicketsAndSwitchTab();
                }
                
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Purchase failed: One or more seats may no longer be available.",
                    "Purchase Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Purchase failed: " + ex.getMessage(),
                "Purchase Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getSeatLabel(FlightSeat seat) {
        return seat.getSeatNumber();
    }

    private void updateBookingService() {
        // Refresh the booking service to ensure we have the latest seat availability
        this.bookingService = new BookingService(flightService);
    }

    // Method to refresh the seat map display
    public void refreshSeatMap() {
        // Reload data from files
        flightService.reloadData();
        
        // Clear selected seats
        selectedSeats.clear();
        
        // Recreate the seat grid panel
        seatGridPanel.removeAll();
        
        // Recreate the seat grid with updated data
        Aircraft aircraft = flightService.getAircraftById(flight.getAircraftId());
        List<FlightSeat> seats = flightService.getFlightSeats(flight.getId());
        
        // Calculate grid dimensions (6 seats per row: 3-3 with aisle)
        int seatsPerRow = 6;
        int rows = aircraft.getRows();
        
        seatGridPanel.setLayout(new BoxLayout(seatGridPanel, BoxLayout.Y_AXIS));
        seatGridPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        seatButtons = new JButton[rows][seatsPerRow];

        // Add cockpit indicator
        JLabel cockpitLabel = new JLabel("✈ COCKPIT", JLabel.CENTER);
        cockpitLabel.setFont(new Font("Arial", Font.BOLD, 14));
        cockpitLabel.setForeground(new Color(70, 130, 180));
        cockpitLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        seatGridPanel.add(cockpitLabel);

        // Create seat grid
        for (int row = 0; row < rows; row++) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            rowPanel.setBackground(Color.WHITE);

            // Row label
            JLabel rowLabel = new JLabel(String.valueOf(row + 1));
            rowLabel.setPreferredSize(new Dimension(30, 40));
            rowLabel.setHorizontalAlignment(JLabel.CENTER);
            rowLabel.setFont(new Font("Arial", Font.BOLD, 12));
            rowPanel.add(rowLabel);

            // Left side seats (A, B, C)
            for (int col = 0; col < 3; col++) {
                createSeatButton(row, col, rowPanel, seats, seatsPerRow);
            }

            // Aisle space
            JLabel aisleLabel = new JLabel("   ");
            aisleLabel.setPreferredSize(new Dimension(30, 40));
            rowPanel.add(aisleLabel);

            // Right side seats (D, E, F)
            for (int col = 3; col < 6; col++) {
                createSeatButton(row, col, rowPanel, seats, seatsPerRow);
            }

            seatGridPanel.add(rowPanel);
        }
        
        // Update selection info
        updateSelectionInfo();
        
        // Refresh the display
        seatGridPanel.revalidate();
        seatGridPanel.repaint();
    }
} 