# Airport Flight Management System

A comprehensive Java-based Airport Flight Management System built using Object-Oriented Programming principles, Swing UI framework, and file-based JSON storage.

## Features

### User Management
- **Three User Roles:**
  - **Super Admin**: Full system access including admin management
  - **Admin**: Manages flights, aircraft, routes, and customers (cannot manage other admins)
  - **Customer**: Register, browse flights, book seats, manage tickets, validate PNR

### Flight Management
- Complete CRUD operations for flights
- Route management with departure/arrival cities
- Aircraft management with capacity and seat configuration
- Real-time seat availability tracking
- Gate and terminal assignment

### Advanced Booking System
- **Color-coded Seat Selection Grid:**
  - Green: Available seats
  - Yellow: Booked seats (reserved but not paid)
  - Red: Sold seats (confirmed and paid)
  - Blue: Currently selected seat
- Wide-view seat map with aircraft configuration
- PNR generation and validation system
- Ticket cancellation functionality

### Modern UI Features
- **Enhanced Search System:**
  - Dropdown selectors for origins and destinations
  - Real-time flight filtering
  - Aircraft information display in results
- **Improved Table Design:**
  - Proper color schemes (white background, dark text)
  - Non-editable columns for data integrity
  - Consistent styling across all tables
- **Responsive Layout:**
  - Tabbed interfaces for organized navigation
  - Modern button styling with consistent colors
  - Professional dashboard designs

### Data Management
- **JSON File Storage** (with .json extensions for better organization)
- Serialization-based persistence in `/data` directory
- Automatic data initialization with sample records
- Data validation and error handling

### Admin Features
- **Admin Dashboard:**
  - Flight, aircraft, and route management
  - Customer overview and statistics
  - Comprehensive reporting system
- **Super Admin Dashboard:**
  - All admin features plus admin management
  - Flight booking with visual seat map
  - System-wide statistics and reports

### Customer Features
- User registration with validation
- Flight search with filtering options
- Interactive seat selection with visual grid
- Ticket management (view, cancel)
- PNR validation system
- Profile management

## Technical Stack

- **Language**: Java 8+
- **UI Framework**: Java Swing
- **Architecture**: OOP with MVC pattern
- **Data Storage**: JSON files with Java serialization
- **Design Patterns**: Service layer, DAO pattern

## Project Structure

```
src/
├── Main.java                 # Application entry point
├── model/                   # Entity classes
│   ├── User.java           # Base user class
│   ├── Customer.java       # Customer entity
│   ├── Admin.java          # Admin entity
│   ├── Flight.java         # Flight entity
│   ├── Aircraft.java       # Aircraft with seat management
│   ├── Route.java          # Route entity
│   ├── Seat.java           # Seat entity
│   ├── Ticket.java         # Ticket entity
│   ├── Gate.java           # Gate entity
│   └── Terminal.java       # Terminal entity
├── service/                # Business logic
│   ├── UserService.java    # User management
│   ├── FlightService.java  # Flight operations
│   └── BookingService.java # Booking operations
├── util/                   # Utilities
│   ├── JsonUtil.java       # Data persistence
│   └── Validator.java      # Input validation
└── view/                   # UI components
    ├── LoginFrame.java         # Login interface
    ├── CustomerDashboardFrame.java    # Customer dashboard
    ├── AdminDashboardFrame.java       # Admin dashboard
    ├── SuperAdminDashboardFrame.java  # Super admin dashboard
    ├── SeatSelectionDialog.java       # Seat selection UI
    └── FlightBrowserFrame.java        # Flight browsing
```

## Installation and Setup

1. **Prerequisites:**
   - Java Development Kit (JDK) 8 or higher
   - Any Java IDE (optional) or command line tools

2. **Quick Start (Recommended):**
   ```bash
   # Build and run in one command (Windows)
   start.bat
   ```

3. **Manual Build and Run:**
   ```bash
   # Build the application
   build.bat          # For Command Prompt
   .\build.ps1        # For PowerShell
   
   # Run the application
   run.bat            # For Command Prompt
   .\run.ps1          # For PowerShell
   ```

4. **Clean Build (if needed):**
   ```bash
   # Remove all compiled files
   clean.bat
   ```

5. **Reset Data (if needed):**
   ```bash
   # Reset all JSON data files to defaults
   reset-data.bat      # For Command Prompt
   .\reset-data.ps1    # For PowerShell
   ```

6. **Manual Compilation (alternative):**
   ```bash
   # Create bin directory
   mkdir bin
   
   # Compile all Java files at once
   javac -cp src -d bin -Xlint:unchecked Main.java src/model/*.java src/service/*.java src/util/*.java src/view/*.java
   
   # Run the application
   java -cp bin Main
   ```

### Build System Features
- **Efficient Compilation**: All classes compiled in a single pass
- **Clean Builds**: Automatic cleanup of previous builds
- **Data Reset**: Complete data reset to fresh defaults
- **Error Handling**: Clear error messages and build status
- **Cross-Platform**: Both batch (.bat) and PowerShell (.ps1) scripts
- **Dependency Management**: Automatic data directory creation

## Default User Accounts

The system comes pre-configured with default accounts for testing:

### Super Admin
- **Email**: superadmin@flight.com
- **Password**: super123

### Admin
- **Email**: admin@flight.com
- **Password**: admin123

### Sample Customer
- **Email**: john.doe@email.com
- **Password**: password123

## Usage Guide

### For Customers
1. Register a new account or login with existing credentials
2. Browse available flights using the search filters
3. Select a flight and choose your preferred seat from the visual grid
4. View your tickets and manage bookings
5. Use PNR validation to check ticket status

### For Admins
1. Login with admin credentials
2. Access the admin dashboard with tabs for:
   - Flight Management: Add, edit, delete flights
   - Aircraft Management: Manage aircraft fleet
   - Route Management: Define flight routes
   - Customer Management: View customer data
   - Reports: System statistics

### For Super Admins
1. Login with super admin credentials
2. Access all admin features plus:
   - Admin Management: Add/remove admin users
   - Flight Booking: Visual seat map for any flight
   - Enhanced reporting and system overview

## System Architecture

### Design Patterns Used
- **MVC (Model-View-Controller)**: Separation of concerns
- **Service Layer**: Business logic abstraction
- **DAO Pattern**: Data access abstraction
- **Observer Pattern**: UI updates and event handling

### Key Components
- **Model Layer**: Entity classes representing business objects
- **Service Layer**: Business logic and data operations
- **View Layer**: Swing-based user interfaces
- **Utility Layer**: Common functions and validation

### Data Flow
1. User interacts with View components
2. View calls appropriate Service methods
3. Service layer handles business logic
4. Data persistence through JsonUtil
5. UI updates reflect data changes

## Features Implemented

### Core Requirements ✅
- [x] Three user roles (Super Admin, Admin, Customer)
- [x] Complete flight management system
- [x] Seat booking with color-coded grid view
- [x] Wide view software with good UI alignment
- [x] Complete data validation
- [x] Terminal and gate information in tickets

### Enhanced Features ✅
- [x] JSON file storage (instead of .dat files)
- [x] Dropdown search filters for destinations
- [x] Aircraft management with image support
- [x] Route management system
- [x] Visual seat maps with real-time status
- [x] Comprehensive admin dashboards
- [x] Modern UI with consistent styling
- [x] PNR generation and validation
- [x] Ticket management and cancellation

### UI Improvements ✅
- [x] Fixed white text on white background issues
- [x] Non-editable table columns
- [x] Proper color schemes throughout
- [x] Modern button styling
- [x] Professional dashboard layouts
- [x] Responsive design elements

## Testing

The system includes comprehensive test data:
- Sample flights between major cities
- Various aircraft with different capacities
- Pre-configured routes and pricing
- Default user accounts for all roles

## Future Enhancements

- Email notifications for bookings
- Payment gateway integration
- Flight delay/cancellation management
- Passenger check-in system
- Seat upgrade functionality
- Loyalty program management
- Real-time flight tracking
- Mobile application interface

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes following the existing code style
4. Test thoroughly
5. Submit a pull request

## License

This project is developed for educational purposes as part of an Object-Oriented Programming course.

## Support

For any issues or questions, please refer to the source code documentation or contact the development team.

---

**Note**: This system is designed for educational purposes and demonstrates Object-Oriented Programming concepts, design patterns, and Java Swing GUI development. 