// src/model/Court.java
package model;

public class Court {
    private int id;
    private String courtName;
    private String type;
    private double pricePerHour;
    private String status;

    public Court(int id, String courtName, String type,
                 double pricePerHour, String status) {
        this.id = id;
        this.courtName = courtName;
        this.type = type;
        this.pricePerHour = pricePerHour;
        this.status = status;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCourtName() { return courtName; }
    public void setCourtName(String courtName) { this.courtName = courtName; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(double pricePerHour) { this.pricePerHour = pricePerHour; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() { return courtName + " - " + type; }
}