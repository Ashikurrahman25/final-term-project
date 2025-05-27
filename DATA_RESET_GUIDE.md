# Data Reset Guide - Airport Management System

This guide explains how to reset all JSON data files to start fresh with default data.

## When to Use Data Reset

You might want to reset the data when:
- **Testing**: Starting fresh for testing different scenarios
- **Development**: Clearing test data during development
- **Demonstration**: Resetting to clean state for demos
- **Corrupted Data**: Fixing issues with corrupted JSON files
- **Fresh Start**: Beginning with clean default data

## What Gets Reset

The data reset will remove ALL existing data including:

### User Data
- ❌ All customer accounts (except defaults that get recreated)
- ❌ All admin accounts (except defaults that get recreated)
- ❌ All user profiles and settings

### Flight Data
- ❌ All flight bookings and tickets
- ❌ All custom flights (except default sample flights)
- ❌ All seat reservations and assignments
- ❌ All PNR records

### System Data
- ❌ All custom routes (except default routes)
- ❌ All custom aircraft (except default aircraft)
- ❌ All custom gates and terminals (except defaults)

## What Gets Recreated

After reset, the system automatically creates:

### Default User Accounts
- ✅ **Super Admin**: superadmin@flight.com / super123
- ✅ **Admin**: admin@flight.com / admin123
- ✅ **Sample Customer**: john.doe@email.com / password123

### Default System Data
- ✅ **Sample Routes**: Major city pairs (NYC-LA, London-Paris, etc.)
- ✅ **Sample Aircraft**: Boeing 737, Airbus A320, Boeing 777
- ✅ **Sample Flights**: 3-4 flights with different schedules
- ✅ **Terminals**: Domestic and International terminals
- ✅ **Gates**: 10 gates distributed across terminals

## How to Reset Data

### Method 1: Direct Reset Scripts

#### Command Prompt
```bash
reset-data.bat
```

#### PowerShell (with colors)
```bash
.\reset-data.ps1
```

### Method 2: Make-Style Command
```bash
make reset
```

### Method 3: Manual Reset
```bash
# Delete all JSON files manually
del data\*.json

# Then start the application to recreate defaults
start.bat
```

## Reset Process

1. **Confirmation**: The script will ask for confirmation before proceeding
2. **File Deletion**: All JSON files in the `data/` directory are removed
3. **Automatic Recreation**: When you next start the application, fresh default data is created

## Safety Features

### Confirmation Required
- The script requires explicit confirmation (type "y") before proceeding
- This prevents accidental data loss

### Backup Recommendation
Before resetting, consider backing up your data:
```bash
# Create backup directory
mkdir backup

# Copy all data files
copy data\*.json backup\
```

### Restore from Backup
To restore from backup:
```bash
# Copy backup files back
copy backup\*.json data\
```

## Step-by-Step Reset Process

### 1. Backup (Optional but Recommended)
```bash
mkdir backup
copy data\*.json backup\
```

### 2. Run Reset Script
```bash
# Choose one method:
reset-data.bat          # Command Prompt
.\reset-data.ps1        # PowerShell
make reset              # Make-style
```

### 3. Confirm Reset
- Type "y" when prompted
- The script will delete all data files

### 4. Start Application
```bash
start.bat
```

### 5. Verify Fresh Data
- Login with default credentials
- Check that only default data exists
- Verify all custom data has been removed

## Default Data Details

### Sample Flights
- **AA101**: New York → Los Angeles (Boeing 737)
- **BA201**: London → Paris (Airbus A320)  
- **JL301**: Tokyo → Seoul (Boeing 777)

### Sample Routes
- New York ↔ Los Angeles (3,944 km, 6h)
- London ↔ Paris (344 km, 1h 15m)
- Tokyo ↔ Seoul (1,160 km, 2h 15m)
- Dubai ↔ Mumbai (1,926 km, 3h 15m)

### Sample Aircraft
- **Boeing 737**: 180 seats, Registration N12345
- **Airbus A320**: 150 seats, Registration F-WXYZ
- **Boeing 777**: 300 seats, Registration G-ABCD

## Troubleshooting

### "Access Denied" Error
- Close the application before running reset
- Run Command Prompt as Administrator
- Check file permissions in data directory

### "Files Not Found" Error
- This is normal if data directory is already empty
- The script will still complete successfully

### Application Won't Start After Reset
- Check if Java is properly installed
- Verify build was successful: `build.bat`
- Check for error messages in console

### Data Not Recreated
- Ensure you start the application after reset
- Check that `data/` directory exists
- Verify no permission issues with data directory

## Advanced Usage

### Automated Reset (for Scripts)
```bash
# Reset without confirmation prompt
echo y | reset-data.bat
```

### Reset Specific Files Only
```bash
# Reset only customer data
del data\customers.json
del data\tickets.json

# Reset only flight data  
del data\flights.json
del data\flightseats.json
```

### Custom Default Data
To customize the default data that gets created:
1. Modify the `initializeDefaultData()` methods in service classes
2. Rebuild the application: `build.bat`
3. Run reset to use new defaults

## Best Practices

1. **Always backup** important data before resetting
2. **Close the application** before running reset
3. **Test thoroughly** after reset to ensure everything works
4. **Document any custom data** you want to recreate manually
5. **Use reset sparingly** in production environments

## Integration with Build System

The reset functionality is integrated with the build system:

```bash
# Complete fresh start
make clean      # Remove compiled files
make reset      # Reset data files  
make start      # Build and run with fresh data
```

This ensures a completely clean environment for development and testing.

---

**⚠️ Warning**: Data reset is irreversible. Always backup important data before proceeding. 