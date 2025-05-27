package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Ticket implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String pnr; // Passenger Name Record
    private String customerId;
    private String flightId;
    private List<String> seatNumbers; // Changed to support multiple seats
    private String status; // PURCHASED, CANCELLED, REFUNDED
    private double totalPrice; // Total price for all seats
    private LocalDateTime purchaseTime;
    private String gateNumber;
    private String terminal;

    // Default constructor for JSON parsing
    public Ticket() {
        this.status = "PURCHASED";
        this.purchaseTime = LocalDateTime.now();
        this.seatNumbers = new ArrayList<>();
    }

    // Constructor for single seat (backward compatibility)
    public Ticket(String id, String pnr, String customerId, String flightId, 
                 String seatNumber, double price) {
        this.id = id;
        this.pnr = pnr;
        this.customerId = customerId;
        this.flightId = flightId;
        this.seatNumbers = new ArrayList<>();
        this.seatNumbers.add(seatNumber);
        this.totalPrice = price;
        this.status = "PURCHASED";
        this.purchaseTime = LocalDateTime.now();
    }

    // Constructor for multiple seats
    public Ticket(String id, String pnr, String customerId, String flightId, 
                 List<String> seatNumbers, double totalPrice) {
        this.id = id;
        this.pnr = pnr;
        this.customerId = customerId;
        this.flightId = flightId;
        this.seatNumbers = new ArrayList<>(seatNumbers);
        this.totalPrice = totalPrice;
        this.status = "PURCHASED";
        this.purchaseTime = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getFlightId() { return flightId; }
    public void setFlightId(String flightId) { this.flightId = flightId; }
    
    public List<String> getSeatNumbers() { return seatNumbers; }
    public void setSeatNumbers(List<String> seatNumbers) { this.seatNumbers = seatNumbers; }
    
    // Backward compatibility method
    public String getSeatNumber() { 
        if (seatNumbers != null && !seatNumbers.isEmpty()) {
            return seatNumbers.get(0);
        }
        return null;
    }
    
    // Backward compatibility method
    public void setSeatNumber(String seatNumber) { 
        if (this.seatNumbers == null) {
            this.seatNumbers = new ArrayList<>();
        }
        this.seatNumbers.clear();
        this.seatNumbers.add(seatNumber);
    }
    
    // Helper method to get formatted seat numbers
    public String getFormattedSeatNumbers() {
        if (seatNumbers == null || seatNumbers.isEmpty()) {
            return "";
        }
        return String.join(", ", seatNumbers);
    }
    
    // Helper method to get seat count
    public int getSeatCount() {
        return seatNumbers != null ? seatNumbers.size() : 0;
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    
    // Backward compatibility method
    public double getPrice() { return totalPrice; }
    public void setPrice(double price) { this.totalPrice = price; }
    
    public LocalDateTime getPurchaseTime() { return purchaseTime; }
    public void setPurchaseTime(LocalDateTime purchaseTime) { this.purchaseTime = purchaseTime; }
    
    public String getGateNumber() { return gateNumber; }
    public void setGateNumber(String gateNumber) { this.gateNumber = gateNumber; }
    
    public String getTerminal() { return terminal; }
    public void setTerminal(String terminal) { this.terminal = terminal; }
}