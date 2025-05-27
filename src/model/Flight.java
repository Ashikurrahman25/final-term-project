package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Flight implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String flightNumber;
    private String routeId;
    private String aircraftId;
    private String gateId;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String status; // SCHEDULED, BOARDING, DEPARTED, ARRIVED, CANCELLED, DELAYED
    private double price;
    private List<FlightSeat> seats;

    // Default constructor for JSON parsing
    public Flight() {
        this.status = "SCHEDULED";
        this.seats = new ArrayList<>();
    }

    public Flight(String id, String flightNumber, String routeId, String aircraftId, 
                 LocalDateTime departureTime, LocalDateTime arrivalTime, double price) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.routeId = routeId;
        this.aircraftId = aircraftId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.status = "SCHEDULED";
        this.gateId = null;
        this.seats = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    
    public String getRouteId() { return routeId; }
    public void setRouteId(String routeId) { this.routeId = routeId; }
    
    public String getAircraftId() { return aircraftId; }
    public void setAircraftId(String aircraftId) { this.aircraftId = aircraftId; }
    
    public String getGateId() { return gateId; }
    public void setGateId(String gateId) { this.gateId = gateId; }
    
    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }
    
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public List<FlightSeat> getSeats() { return seats; }
    public void setSeats(List<FlightSeat> seats) { this.seats = seats; }
    
    // Helper methods for seat management
    public void addSeat(FlightSeat seat) {
        if (this.seats == null) {
            this.seats = new ArrayList<>();
        }
        this.seats.add(seat);
    }
    
    public FlightSeat getSeatByNumber(String seatNumber) {
        if (this.seats == null) return null;
        return this.seats.stream()
                .filter(seat -> seat.getSeatNumber().equals(seatNumber))
                .findFirst()
                .orElse(null);
    }
    
    public List<FlightSeat> getAvailableSeats() {
        if (this.seats == null) return new ArrayList<>();
        return this.seats.stream()
                .filter(FlightSeat::isAvailable)
                .collect(java.util.stream.Collectors.toList());
    }
    
    public List<FlightSeat> getSoldSeats() {
        if (this.seats == null) return new ArrayList<>();
        return this.seats.stream()
                .filter(FlightSeat::isSold)
                .collect(java.util.stream.Collectors.toList());
    }
    
    public int getAvailableSeatsCount() {
        return getAvailableSeats().size();
    }
    
    public int getSoldSeatsCount() {
        return getSoldSeats().size();
    }
}