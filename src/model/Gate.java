package model;

import java.io.Serializable;

public class Gate implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String gateNumber;
    private String terminal;
    private boolean available;
    private String currentFlightId;

    // Default constructor for JSON parsing
    public Gate() {
        this.available = true;
    }

    public Gate(String id, String gateNumber, String terminal) {
        this.id = id;
        this.gateNumber = gateNumber;
        this.terminal = terminal;
        this.available = true;
        this.currentFlightId = null;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getGateNumber() { return gateNumber; }
    public void setGateNumber(String gateNumber) { this.gateNumber = gateNumber; }

    public String getTerminal() { return terminal; }
    public void setTerminal(String terminal) { this.terminal = terminal; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public String getCurrentFlightId() { return currentFlightId; }
    public void setCurrentFlightId(String currentFlightId) { 
        this.currentFlightId = currentFlightId;
        this.available = (currentFlightId == null);
    }

    @Override
    public String toString() {
        return "Gate " + gateNumber + " (Terminal " + terminal + ")";
    }
} 