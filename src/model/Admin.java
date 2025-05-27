package model;

public class Admin extends User {
    private boolean isSuperAdmin;

    // Default constructor for JSON parsing
    public Admin() {
        super();
    }

    public Admin(String id, String name, String email, String phone, 
                 String gender, String password, boolean isSuperAdmin) {
        super(id, name, email, phone, gender, password, "ADMIN");
        this.isSuperAdmin = isSuperAdmin;
    }
    
    public Admin(String id, String name, String email, String phone, 
                 String gender, String password) {
        this(id, name, email, phone, gender, password, false);
    }

    public boolean isSuperAdmin() { return isSuperAdmin; }
    public void setSuperAdmin(boolean superAdmin) { isSuperAdmin = superAdmin; }
}