package service;

import model.*;
import util.JsonUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlightService {
    private List<Flight> flights;
    private List<Route> routes;
    private List<Aircraft> aircrafts;
    private List<Gate> gates;
    private List<Terminal> terminals;
    
    private static final String FLIGHTS_FILE = "flights.json";
    private static final String ROUTES_FILE = "routes.json";
    private static final String AIRCRAFTS_FILE = "aircrafts.json";
    private static final String GATES_FILE = "gates.json";
    private static final String TERMINALS_FILE = "terminals.json";

    public FlightService() {
        loadData();
        if (flights.isEmpty() && !dataFilesExist()) {
            initializeDefaultData();
        }
    }

    private boolean dataFilesExist() {
        java.io.File dataDir = new java.io.File("data");
        if (!dataDir.exists()) return false;
        
        java.io.File flightsFile = new java.io.File("data/" + FLIGHTS_FILE);
        java.io.File routesFile = new java.io.File("data/" + ROUTES_FILE);
        java.io.File aircraftsFile = new java.io.File("data/" + AIRCRAFTS_FILE);
        java.io.File gatesFile = new java.io.File("data/" + GATES_FILE);
        java.io.File terminalsFile = new java.io.File("data/" + TERMINALS_FILE);
        
        return (flightsFile.exists() && flightsFile.length() > 10) || 
               (routesFile.exists() && routesFile.length() > 10) || 
               (aircraftsFile.exists() && aircraftsFile.length() > 10) || 
               (gatesFile.exists() && gatesFile.length() > 10) || 
               (terminalsFile.exists() && terminalsFile.length() > 10);
    }

    private void loadData() {
        flights = JsonUtil.loadFromFile(FLIGHTS_FILE, Flight.class);
        routes = JsonUtil.loadFromFile(ROUTES_FILE, Route.class);
        aircrafts = JsonUtil.loadFromFile(AIRCRAFTS_FILE, Aircraft.class);
        gates = JsonUtil.loadFromFile(GATES_FILE, Gate.class);
        terminals = JsonUtil.loadFromFile(TERMINALS_FILE, Terminal.class);
        
        // Initialize seats for flights that don't have them yet
        initializeFlightSeats();
    }

    // Public method to reload data from files (useful after external changes)
    public void reloadData() {
        loadData();
    }

    private void saveData() {
        JsonUtil.saveToFile(flights, FLIGHTS_FILE);
        JsonUtil.saveToFile(routes, ROUTES_FILE);
        JsonUtil.saveToFile(aircrafts, AIRCRAFTS_FILE);
        JsonUtil.saveToFile(gates, GATES_FILE);
        JsonUtil.saveToFile(terminals, TERMINALS_FILE);
    }

    private void initializeFlightSeats() {
        // Create seats for flights that don't have them yet
        boolean seatsCreated = false;
        for (Flight flight : flights) {
            if (flight.getSeats() == null || flight.getSeats().isEmpty()) {
                createSeatsForFlight(flight);
                seatsCreated = true;
            }
        }
        // Save data if any seats were created
        if (seatsCreated) {
            saveData();
        }
    }

    private void createSeatsForFlight(Flight flight) {
        Aircraft aircraft = getAircraftById(flight.getAircraftId());
        if (aircraft != null) {
            char[] seatLetters = {'A', 'B', 'C', 'D', 'E', 'F'};
            int capacity = aircraft.getCapacity();
            int rows = (capacity + 5) / 6; // Round up to ensure we create enough seats
            
            List<FlightSeat> seats = new ArrayList<>();
            int seatsCreated = 0;
            
            for (int row = 1; row <= rows && seatsCreated < capacity; row++) {
                for (int col = 0; col < 6 && seatsCreated < capacity; col++) {
                    String seatNumber = row + "" + seatLetters[col];
                    seats.add(new FlightSeat(seatNumber));
                    seatsCreated++;
                }
            }
            flight.setSeats(seats);
        }
    }

    private void initializeDefaultData() {
        // Initialize default terminals
        Terminal terminal1 = new Terminal("T1", "Terminal 1", "DOMESTIC");
        Terminal terminal2 = new Terminal("T2", "Terminal 2", "INTERNATIONAL");
        terminals.add(terminal1);
        terminals.add(terminal2);

        // Initialize default gates
        for (int i = 1; i <= 10; i++) {
            String terminalType = i <= 5 ? "T1" : "T2";
            gates.add(new Gate("G" + i, "G" + i, terminalType));
        }

        // Initialize default routes
        routes.add(new Route("R1", "New York", "Los Angeles", 3944, 360));
        routes.add(new Route("R2", "London", "Paris", 344, 75));
        routes.add(new Route("R3", "Tokyo", "Seoul", 1160, 135));
        routes.add(new Route("R4", "Dubai", "Mumbai", 1926, 195));

        // Initialize default aircrafts
        aircrafts.add(new Aircraft("A1", "Boeing 737", "N12345", 180, "Boeing", "boeing737.jpg"));
        aircrafts.add(new Aircraft("A2", "Airbus A320", "F-WXYZ", 150, "Airbus", "airbusa320.jpg"));
        aircrafts.add(new Aircraft("A3", "Boeing 777", "G-ABCD", 300, "Boeing", "boeing777.jpg"));

        // Initialize sample flights
        LocalDateTime now = LocalDateTime.now();
        flights.add(new Flight("F1", "AA101", "R1", "A1", now.plusHours(2), now.plusHours(8), 299.99));
        flights.add(new Flight("F2", "BA201", "R2", "A2", now.plusHours(4), now.plusHours(5).plusMinutes(15), 150.00));
        flights.add(new Flight("F3", "JL301", "R3", "A3", now.plusDays(1), now.plusDays(1).plusHours(2).plusMinutes(15), 450.00));

        // Initialize seats for all flights
        for (Flight flight : flights) {
            createSeatsForFlight(flight);
        }
        
        saveData();
    }

    // Flight management
    public List<Flight> getAllFlights() {
        return new ArrayList<>(flights);
    }

    public Flight getFlightById(String id) {
        return flights.stream().filter(f -> f.getId().equals(id)).findFirst().orElse(null);
    }

    public Flight getFlightByNumber(String flightNumber) {
        return flights.stream().filter(f -> f.getFlightNumber().equals(flightNumber)).findFirst().orElse(null);
    }

    public List<Flight> searchFlights(String departure, String arrival, LocalDateTime date) {
        return flights.stream()
                .filter(f -> {
                    Route route = getRouteById(f.getRouteId());
                    return route != null && 
                           route.getDeparture().toLowerCase().contains(departure.toLowerCase()) &&
                           route.getArrival().toLowerCase().contains(arrival.toLowerCase()) &&
                           (date == null || f.getDepartureTime().toLocalDate().equals(date.toLocalDate()));
                })
                .collect(Collectors.toList());
    }

    public void addFlight(Flight flight) {
        flights.add(flight);
        createSeatsForFlight(flight);
        saveData();
    }

    public void updateFlight(Flight flight) {
        flights.removeIf(f -> f.getId().equals(flight.getId()));
        flights.add(flight);
        saveData();
    }

    public void deleteFlight(String flightId) {
        flights.removeIf(f -> f.getId().equals(flightId));
        saveData();
    }

    // Flight seat management - now working with seats within Flight objects
    public List<FlightSeat> getFlightSeats(String flightId) {
        Flight flight = getFlightById(flightId);
        if (flight != null && flight.getSeats() != null) {
            return new ArrayList<>(flight.getSeats());
        }
        return new ArrayList<>();
    }

    public List<FlightSeat> getAvailableSeats(String flightId) {
        Flight flight = getFlightById(flightId);
        if (flight != null) {
            return flight.getAvailableSeats();
        }
        return new ArrayList<>();
    }

    public FlightSeat getSeatByFlightAndNumber(String flightId, String seatNumber) {
        Flight flight = getFlightById(flightId);
        if (flight != null) {
            return flight.getSeatByNumber(seatNumber);
        }
        return null;
    }

    public void updateFlightSeat(FlightSeat seat, String flightId) {
        Flight flight = getFlightById(flightId);
        if (flight != null) {
            FlightSeat existingSeat = flight.getSeatByNumber(seat.getSeatNumber());
            if (existingSeat != null) {
                existingSeat.setStatus(seat.getStatus());
                existingSeat.setPassengerId(seat.getPassengerId());
                saveData();
            }
        }
    }

    // Route management
    public List<Route> getAllRoutes() {
        return new ArrayList<>(routes);
    }

    public Route getRouteById(String id) {
        return routes.stream().filter(r -> r.getId().equals(id)).findFirst().orElse(null);
    }

    public void addRoute(Route route) {
        routes.add(route);
        saveData();
    }

    public void updateRoute(Route route) {
        routes.removeIf(r -> r.getId().equals(route.getId()));
        routes.add(route);
        saveData();
    }

    public void deleteRoute(String routeId) {
        routes.removeIf(r -> r.getId().equals(routeId));
        saveData();
    }

    // Aircraft management
    public List<Aircraft> getAllAircrafts() {
        return new ArrayList<>(aircrafts);
    }

    public Aircraft getAircraftById(String id) {
        return aircrafts.stream().filter(a -> a.getId().equals(id)).findFirst().orElse(null);
    }

    public void addAircraft(Aircraft aircraft) {
        aircrafts.add(aircraft);
        saveData();
    }

    public void updateAircraft(Aircraft aircraft) {
        aircrafts.removeIf(a -> a.getId().equals(aircraft.getId()));
        aircrafts.add(aircraft);
        saveData();
    }

    public void deleteAircraft(String aircraftId) {
        aircrafts.removeIf(a -> a.getId().equals(aircraftId));
        saveData();
    }

    // Gate management
    public List<Gate> getAllGates() {
        return new ArrayList<>(gates);
    }

    public Gate getGateById(String id) {
        return gates.stream().filter(g -> g.getId().equals(id)).findFirst().orElse(null);
    }

    public List<Gate> getAvailableGates() {
        return gates.stream().filter(g -> g.isAvailable()).collect(Collectors.toList());
    }

    public void addGate(Gate gate) {
        gates.add(gate);
        saveData();
    }

    public void updateGate(Gate gate) {
        gates.removeIf(g -> g.getId().equals(gate.getId()));
        gates.add(gate);
        saveData();
    }

    public void deleteGate(String gateId) {
        gates.removeIf(g -> g.getId().equals(gateId));
        saveData();
    }

    // Terminal management
    public List<Terminal> getAllTerminals() {
        return new ArrayList<>(terminals);
    }

    public Terminal getTerminalById(String id) {
        return terminals.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
    }

    public void addTerminal(Terminal terminal) {
        terminals.add(terminal);
        saveData();
    }

    public void updateTerminal(Terminal terminal) {
        terminals.removeIf(t -> t.getId().equals(terminal.getId()));
        terminals.add(terminal);
        saveData();
    }

    public void deleteTerminal(String terminalId) {
        terminals.removeIf(t -> t.getId().equals(terminalId));
        saveData();
    }
} 