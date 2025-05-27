package model;

import java.io.Serializable;

public class Aircraft implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String model;
    private String registrationNumber;
    private int capacity;
    private String manufacturer;
    private String imagePath;
    private String status; // ACTIVE, MAINTENANCE, DISCONTINUED
    private int rows;
    private int seatsPerRow;

    // Default constructor for JSON parsing
    public Aircraft() {
        this.seatsPerRow = 6;
        this.status = "ACTIVE";
    }

    public Aircraft(String id, String model, String registrationNumber, 
                   int capacity, String manufacturer, String imagePath) {
        this.id = id;
        this.model = model;
        this.registrationNumber = registrationNumber;
        this.capacity = capacity;
        this.manufacturer = manufacturer;
        this.imagePath = imagePath;
        this.status = "ACTIVE";
        this.rows = (capacity + 5) / 6; // Round up to ensure all seats are included
        this.seatsPerRow = 6;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { 
        this.capacity = capacity; 
        this.rows = (capacity + 5) / 6; // Round up to ensure all seats are included
    }
    
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getRows() { 
        if (rows == 0 && capacity > 0) {
            rows = (capacity + 5) / 6; // Round up to ensure all seats are included
        }
        return rows; 
    }
    
    public int getSeatsPerRow() { return seatsPerRow; }
}