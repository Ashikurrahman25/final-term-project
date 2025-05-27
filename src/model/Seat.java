package model;

import java.io.Serializable;

public class Seat implements Serializable {
    private static final long serialVersionUID = 1L;
    private String seatNumber;
    private String status; // AVAILABLE, BOOKED, SOLD
    private String passengerId;

    // Default constructor for JSON parsing
    public Seat() {
        this.status = "AVAILABLE";
    }

    public Seat(String seatNumber, String status) {
        this.seatNumber = seatNumber;
        this.status = status;
        this.passengerId = null;
    }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPassengerId() { return passengerId; }
    public void setPassengerId(String passengerId) { this.passengerId = passengerId; }
    
    public boolean isAvailable() { return "AVAILABLE".equals(status); }
    public boolean isBooked() { return "BOOKED".equals(status); }
    public boolean isSold() { return "SOLD".equals(status); }
}