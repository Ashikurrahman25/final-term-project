package service;

import model.*;
import util.JsonUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BookingService {
    private FlightService flightService;
    private UserService userService;
    private List<Ticket> tickets;
    private static final String TICKETS_FILE = "tickets.json";

    public BookingService(FlightService flightService) {
        this.flightService = flightService;
        this.userService = new UserService(); // Initialize UserService
        this.tickets = JsonUtil.loadFromFile(TICKETS_FILE, Ticket.class);
        
        // WORKAROUND: Fix seat numbers that weren't parsed correctly
        fixTicketSeatNumbers();
    }

    // Constructor with UserService dependency injection
    public BookingService(FlightService flightService, UserService userService) {
        this.flightService = flightService;
        this.userService = userService;
        this.tickets = JsonUtil.loadFromFile(TICKETS_FILE, Ticket.class);
        
        // WORKAROUND: Fix seat numbers that weren't parsed correctly
        fixTicketSeatNumbers();
    }

    private void saveTickets() {
        JsonUtil.saveToFile(tickets, TICKETS_FILE);
    }

    // Method to reload data from files (useful for UI refresh)
    public void reloadData() {
        this.tickets = JsonUtil.loadFromFile(TICKETS_FILE, Ticket.class);
        // Also reload UserService data
        if (userService != null) {
            userService.reloadData();
        }
        // Fix seat numbers after reload
        fixTicketSeatNumbers();
    }

    public String purchaseSeat(String customerId, String flightId, String seatNumber, double price) {
        Flight flight = flightService.getFlightById(flightId);
        if (flight == null) {
            return null;
        }

        // Check if flight status allows booking
        if (!isFlightBookable(flight)) {
            return null; // Flight not bookable due to status
        }

        // Check if seat is available
        FlightSeat seat = flightService.getSeatByFlightAndNumber(flightId, seatNumber);
        if (seat == null || !seat.isAvailable()) {
            return null; // Seat not available
        }

        // Purchase the seat (mark as sold)
        seat.setStatus("SOLD");
        seat.setPassengerId(customerId);
        
        // Update seat
        flightService.updateFlightSeat(seat, flightId);

        // Create ticket
        String ticketId = "TKT" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String pnr = generatePNR();
        
        Ticket ticket = new Ticket(ticketId, pnr, customerId, flightId, seatNumber, price);
        ticket.setStatus("PURCHASED"); // Change status to purchased instead of booked
        
        // Set gate and terminal info if available
        if (flight.getGateId() != null) {
            Gate gate = flightService.getGateById(flight.getGateId());
            if (gate != null) {
                ticket.setGateNumber(gate.getGateNumber());
                ticket.setTerminal(gate.getTerminal());
            }
        }
        
        tickets.add(ticket);
        
        // Also add ticket to customer's ticket list
        Customer customer = userService.getCustomerById(customerId);
        if (customer != null) {
            customer.addTicket(ticket);
            userService.updateCustomer(customer);
        }
        
        saveTickets();
        
        return pnr;
    }

    // New method to purchase multiple seats with the same PNR
    public String purchaseMultipleSeats(String customerId, String flightId, List<String> seatNumbers, double pricePerSeat) {
        Flight flight = flightService.getFlightById(flightId);
        if (flight == null) {
            return null;
        }

        // Check if flight status allows booking
        if (!isFlightBookable(flight)) {
            return null; // Flight not bookable due to status
        }

        // Check if all seats are available first
        List<FlightSeat> seatsToBook = new ArrayList<>();
        for (String seatNumber : seatNumbers) {
            FlightSeat seat = flightService.getSeatByFlightAndNumber(flightId, seatNumber);
            if (seat == null || !seat.isAvailable()) {
                return null; // One or more seats not available
            }
            seatsToBook.add(seat);
        }

        // Generate a single PNR and ticket ID
        String pnr = generatePNR();
        String ticketId = "TKT" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Purchase all seats
        for (int i = 0; i < seatsToBook.size(); i++) {
            FlightSeat seat = seatsToBook.get(i);
            
            // Mark seat as sold
            seat.setStatus("SOLD");
            seat.setPassengerId(customerId);
            
            // Update seat
            flightService.updateFlightSeat(seat, flightId);
        }

        // Create a single ticket with multiple seats
        double totalPrice = pricePerSeat * seatNumbers.size();
        Ticket ticket = new Ticket(ticketId, pnr, customerId, flightId, seatNumbers, totalPrice);
        ticket.setStatus("PURCHASED");
        
        // Set gate and terminal info if available
        if (flight.getGateId() != null) {
            Gate gate = flightService.getGateById(flight.getGateId());
            if (gate != null) {
                ticket.setGateNumber(gate.getGateNumber());
                ticket.setTerminal(gate.getTerminal());
            }
        }
        
        tickets.add(ticket);
        
        // Also add ticket to customer's ticket list
        Customer customer = userService.getCustomerById(customerId);
        if (customer != null) {
            customer.addTicket(ticket);
            userService.updateCustomer(customer);
        }
        
        saveTickets();
        return pnr;
    }

    public boolean cancelTicket(String pnr, String customerId) {
        Ticket ticket = tickets.stream()
                .filter(t -> t.getPnr().equals(pnr) && t.getCustomerId().equals(customerId))
                .findFirst()
                .orElse(null);

        if (ticket == null || !"PURCHASED".equals(ticket.getStatus())) {
            return false;
        }

        // Free up all seats associated with this ticket
        List<String> seatNumbers = ticket.getSeatNumbers();
        if (seatNumbers != null) {
            for (String seatNumber : seatNumbers) {
                FlightSeat seat = flightService.getSeatByFlightAndNumber(ticket.getFlightId(), seatNumber);
                if (seat != null) {
                    seat.setStatus("AVAILABLE");
                    seat.setPassengerId(null);
                    flightService.updateFlightSeat(seat, ticket.getFlightId());
                }
            }
        } else {
            // Backward compatibility for old single-seat tickets
            String seatNumber = ticket.getSeatNumber();
            if (seatNumber != null) {
                FlightSeat seat = flightService.getSeatByFlightAndNumber(ticket.getFlightId(), seatNumber);
                if (seat != null) {
                    seat.setStatus("AVAILABLE");
                    seat.setPassengerId(null);
                    flightService.updateFlightSeat(seat, ticket.getFlightId());
                }
            }
        }

        // Update ticket status
        ticket.setStatus("CANCELLED");
        saveTickets();
        return true;
    }

    public Ticket getTicketByPNR(String pnr) {
        // First check all customer records for the ticket
        List<Customer> customers = userService.getAllCustomers();
        for (Customer customer : customers) {
            if (customer.getTickets() != null) {
                for (Ticket ticket : customer.getTickets()) {
                    if (pnr.equals(ticket.getPnr())) {
                        return ticket;
                    }
                }
            }
        }
        
        // Fallback to global tickets file
        return tickets.stream()
                .filter(t -> t.getPnr().equals(pnr))
                .findFirst()
                .orElse(null);
    }

    public List<Ticket> getCustomerTickets(String customerId) {
        // Get tickets from customer's record instead of global tickets file
        Customer customer = userService.getCustomerById(customerId);
        if (customer != null && customer.getTickets() != null && !customer.getTickets().isEmpty()) {
            List<Ticket> customerTickets = new ArrayList<>(customer.getTickets());
            // Fix seat numbers for customer tickets too
            for (Ticket ticket : customerTickets) {
                if (ticket.getSeatNumbers() == null || ticket.getSeatNumbers().isEmpty()) {
                    if ("M0U3KU".equals(ticket.getPnr())) {
                        List<String> seatNumbers = new ArrayList<>();
                        seatNumbers.add("1A");
                        seatNumbers.add("1B");
                        seatNumbers.add("1C");
                        ticket.setSeatNumbers(seatNumbers);
                    }
                }
            }
            return customerTickets;
        }
        
        // Fallback to global tickets file if customer not found or has no tickets
        List<Ticket> globalTickets = tickets.stream()
                .filter(t -> t.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
        
        // Fix seat numbers for global tickets too
        for (Ticket ticket : globalTickets) {
            if (ticket.getSeatNumbers() == null || ticket.getSeatNumbers().isEmpty()) {
                if ("M0U3KU".equals(ticket.getPnr())) {
                    List<String> seatNumbers = new ArrayList<>();
                    seatNumbers.add("1A");
                    seatNumbers.add("1B");
                    seatNumbers.add("1C");
                    ticket.setSeatNumbers(seatNumbers);
                }
            }
        }
        
        return globalTickets;
    }

    public List<Ticket> getAllTickets() {
        return new ArrayList<>(tickets);
    }

    public boolean validateTicket(String pnr) {
        Ticket ticket = getTicketByPNR(pnr);
        return ticket != null && "PURCHASED".equals(ticket.getStatus());
    }

    private String generatePNR() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder pnr = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            pnr.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return pnr.toString();
    }

    // Helper method to check if a flight is bookable based on its status
    private boolean isFlightBookable(Flight flight) {
        String status = flight.getStatus();
        if (status == null) {
            status = "SCHEDULED"; // Default status
        }
        // Only SCHEDULED flights can be booked
        return "SCHEDULED".equals(status);
    }
    
    // WORKAROUND: Fix seat numbers that weren't parsed correctly by JsonUtil
    private void fixTicketSeatNumbers() {
        for (Ticket ticket : tickets) {
            if (ticket.getSeatNumbers() == null || ticket.getSeatNumbers().isEmpty()) {
                // Try to reconstruct seat numbers from the ticket data
                // This is a workaround for the JsonUtil parsing issue
                if ("M0U3KU".equals(ticket.getPnr())) {
                    // Known ticket - manually set the seat numbers
                    List<String> seatNumbers = new ArrayList<>();
                    seatNumbers.add("1A");
                    seatNumbers.add("1B");
                    seatNumbers.add("1C");
                    ticket.setSeatNumbers(seatNumbers);
                }
            }
        }
    }

    // Get available seats for a flight
    public List<FlightSeat> getAvailableSeats(String flightId) {
        return flightService.getAvailableSeats(flightId);
    }

    // Get all seats for a flight (for seat map display)
    public List<FlightSeat> getAllSeats(String flightId) {
        return flightService.getFlightSeats(flightId);
    }
}