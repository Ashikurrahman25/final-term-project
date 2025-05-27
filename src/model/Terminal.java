package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Terminal implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String type; // DOMESTIC, INTERNATIONAL
    private List<Gate> gates;
    private boolean active;

    // Default constructor for JSON parsing
    public Terminal() {
        this.gates = new ArrayList<>();
        this.active = true;
    }

    public Terminal(String id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.gates = new ArrayList<>();
        this.active = true;
    }

    public void addGate(Gate gate) {
        gates.add(gate);
    }

    public void removeGate(Gate gate) {
        gates.remove(gate);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public List<Gate> getGates() { return gates; }
    public void setGates(List<Gate> gates) { this.gates = gates; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
} 