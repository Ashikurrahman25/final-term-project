package service;

import model.*;
import util.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserService {
    private List<Customer> customers;
    private List<Admin> admins;
    private static final String CUSTOMERS_FILE = "customers.json";
    private static final String ADMINS_FILE = "admins.json";

    public UserService() {
        customers = new ArrayList<>();
        admins = new ArrayList<>();
        loadUsers();
        if (admins.isEmpty() && !dataFilesExist()) {
            initializeDefaultAdmin();
        }
    }

    private boolean dataFilesExist() {
        java.io.File dataDir = new java.io.File("data");
        if (!dataDir.exists()) return false;
        
        java.io.File customersFile = new java.io.File("data/" + CUSTOMERS_FILE);
        java.io.File adminsFile = new java.io.File("data/" + ADMINS_FILE);
        
        return (customersFile.exists() && customersFile.length() > 10) || 
               (adminsFile.exists() && adminsFile.length() > 10);
    }

    private void loadUsers() {
        customers = JsonUtil.loadFromFile(CUSTOMERS_FILE, Customer.class);
        admins = JsonUtil.loadFromFile(ADMINS_FILE, Admin.class);
    }

    private void saveData() {
        JsonUtil.saveToFile(customers, CUSTOMERS_FILE);
        JsonUtil.saveToFile(admins, ADMINS_FILE);
    }

    // Method to reload data from files (useful for UI refresh)
    public void reloadData() {
        loadUsers();
    }

    private void initializeDefaultAdmin() {
        // Create default super admin
        Admin superAdmin = new Admin("ADMIN001", "Super Admin", "admin@airport.com", 
                                   "+1234567890", "Other", "admin123", true);
        admins.add(superAdmin);
        
        // Create default admin
        Admin admin = new Admin("ADMIN002", "Admin User", "admin2@airport.com", 
                               "+1234567891", "Other", "admin123", false);
        admins.add(admin);
        
        saveData();
    }

    public User authenticate(String email, String password) {
        // Check customers first
        Customer customer = customers.stream()
                .filter(c -> c.getEmail().equals(email) && c.getPassword().equals(password))
                .findFirst()
                .orElse(null);
        
        if (customer != null) {
            return customer;
        }

        System.out.println("Checking admins --> " + admins.size());
        // Check admins
        return admins.stream()
                .filter(a -> a.getEmail().equals(email) && a.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public Customer authenticateCustomer(String email, String password) {
        return customers.stream()
                .filter(c -> c.getEmail().equals(email) && c.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public Admin authenticateAdmin(String email, String password) {
        return admins.stream()
                .filter(a -> a.getEmail().equals(email) && a.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public boolean registerCustomer(String name, String phone, String email, String gender, String password) {
        // Check if email already exists
        if (isEmailTaken(email)) {
            return false;
        }

        String customerId = "CUST" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Customer customer = new Customer(customerId, name, email, phone, gender, password);
        customers.add(customer);
        saveData();
        return true;
    }

    public boolean isEmailTaken(String email) {
        return customers.stream().anyMatch(c -> c.getEmail().equals(email)) ||
               admins.stream().anyMatch(a -> a.getEmail().equals(email));
    }

    // Customer management
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }

    public Customer getCustomerById(String id) {
        return customers.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }

    public void updateCustomer(Customer customer) {
        customers.removeIf(c -> c.getId().equals(customer.getId()));
        customers.add(customer);
        saveData();
    }

    public void deleteCustomer(String customerId) {
        customers.removeIf(c -> c.getId().equals(customerId));
        saveData();
    }

    // Admin management
    public List<Admin> getAllAdmins() {
        return new ArrayList<>(admins);
    }

    public Admin getAdminById(String id) {
        return admins.stream().filter(a -> a.getId().equals(id)).findFirst().orElse(null);
    }

    public boolean addAdmin(String name, String phone, String email, String gender, String password, boolean isSuperAdmin) {
        if (isEmailTaken(email)) {
            return false;
        }

        String adminId = "ADMIN" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Admin admin = new Admin(adminId, name, email, phone, gender, password, isSuperAdmin);
        admins.add(admin);
        saveData();
        return true;
    }

    public void updateAdmin(Admin admin) {
        admins.removeIf(a -> a.getId().equals(admin.getId()));
        admins.add(admin);
        saveData();
    }

    public void deleteAdmin(String adminId) {
        admins.removeIf(a -> a.getId().equals(adminId));
        saveData();
    }

    public boolean canDeleteAdmin(String adminId, String currentAdminId) {
        Admin currentAdmin = getAdminById(currentAdminId);
        Admin targetAdmin = getAdminById(adminId);
        
        if (currentAdmin == null || targetAdmin == null) {
            return false;
        }
        
        // Super admin can delete anyone except themselves
        if (currentAdmin.isSuperAdmin()) {
            return !adminId.equals(currentAdminId);
        }
        
        return false; // Regular admins cannot delete other admins
    }
}