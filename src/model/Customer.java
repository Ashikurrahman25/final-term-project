package model;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private List<Ticket> tickets;

    // Default constructor for JSON parsing
    public Customer() {
        super();
        this.tickets = new ArrayList<>();
    }

    public Customer(String id, String name, String email, String phone, 
                   String gender, String password) {
        super(id, name, email, phone, gender, password, "CUSTOMER");
        this.tickets = new ArrayList<>();
    }

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }

    public List<Ticket> getTickets() {
        return tickets;
    }
    
    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
}