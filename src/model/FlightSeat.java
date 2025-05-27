package model;

import java.io.Serializable;

public class FlightSeat implements Serializable {
    private static final long serialVersionUID = 1L;
    private String seatNumber;
    private String status; // AVAILABLE, SOLD
    private String passengerId;

    // Default constructor for JSON parsing
    public FlightSeat() {
        this.status = "AVAILABLE";
    }

    public FlightSeat(String seatNumber) {
        this.seatNumber = seatNumber;
        this.status = "AVAILABLE";
        this.passengerId = null;
    }

    public FlightSeat(String seatNumber, String status) {
        this.seatNumber = seatNumber;
        this.status = status;
        this.passengerId = null;
    }

    // Getters and Setters

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPassengerId() { return passengerId; }
    public void setPassengerId(String passengerId) { this.passengerId = passengerId; }
    
    public boolean isAvailable() { return "AVAILABLE".equals(status); }
    public boolean isSold() { return "SOLD".equals(status); }
} 