package model;

import java.util.ArrayList;
import java.util.List;

public class Passenger extends User {
    private String passport;
    private List<Ticket> tickets;

    public Passenger(String id, String name, String email, String phone, 
                    String gender, String password, String passport) {
        super(id, name, email, phone, gender, password, "PASSENGER");
        this.passport = passport;
        this.tickets = new ArrayList<>();
    }

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }

    public String getPassport() { return passport; }
    public void setPassport(String passport) { this.passport = passport; }
    
    public List<Ticket> getTickets() { return tickets; }
    public void setTickets(List<Ticket> tickets) { this.tickets = tickets; }
}