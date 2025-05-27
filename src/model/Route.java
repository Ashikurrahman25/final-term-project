package model;

import java.io.Serializable;

public class Route implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String departure;
    private String arrival;
    private double distance;
    private int duration; // in minutes
    private boolean active;

    // Default constructor for JSON parsing
    public Route() {
        this.active = true;
    }

    public Route(String id, String departure, String arrival, double distance, int duration) {
        this.id = id;
        this.departure = departure;
        this.arrival = arrival;
        this.distance = distance;
        this.duration = duration;
        this.active = true;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDeparture() { return departure; }
    public void setDeparture(String departure) { this.departure = departure; }

    public String getArrival() { return arrival; }
    public void setArrival(String arrival) { this.arrival = arrival; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return departure + " → " + arrival + " (" + distance + " km)";
    }
} 