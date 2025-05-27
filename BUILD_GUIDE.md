# Build Guide - Airport Management System

This document explains all the available build and run options for the Airport Management System.

## Quick Start (Recommended)

For the fastest way to build and run the application:

```bash
# Windows Command Prompt or PowerShell
start.bat
```

This single command will:
1. Clean any previous builds
2. Compile all Java source files
3. Start the application

## Individual Build Commands

### 1. Build Only
Compile all Java files without running:

```bash
# Command Prompt
build.bat

# PowerShell (with colors)
.\build.ps1
```

### 2. Run Only
Run the application (requires prior compilation):

```bash
# Command Prompt
run.bat

# PowerShell (with colors)
.\run.ps1
```

### 3. Clean Build Files
Remove all compiled classes and build artifacts:

```bash
clean.bat
```

### 4. Reset Data Files
Reset all JSON data files to fresh defaults:

```bash
# Command Prompt
reset-data.bat

# PowerShell (with colors)
.\reset-data.ps1
```

## Make-Style Commands

For users familiar with make-style build systems:

```bash
# Show available targets
make help

# Build the application
make build

# Run the application
make run

# Clean build files
make clean

# Reset data files to defaults
make reset

# Build and run in one command
make start
```

## Manual Compilation

If you prefer manual control over the build process:

```bash
# Create output directory
mkdir bin

# Compile all Java files at once
javac -cp src -d bin -Xlint:unchecked Main.java src/model/*.java src/service/*.java src/util/*.java src/view/*.java

# Run the application
java -cp bin Main
```

## Build System Features

### Efficient Compilation
- **Single Pass**: All Java files are compiled in one javac command
- **No Duplicates**: Each class is compiled only once
- **Dependency Resolution**: Automatic handling of class dependencies
- **Optimized Output**: All classes organized in the `bin` directory

### Clean Builds
- **Automatic Cleanup**: Previous builds are automatically removed
- **Fresh Start**: Ensures no stale class files interfere
- **Consistent State**: Every build starts from a clean slate

### Error Handling
- **Clear Messages**: Descriptive error messages for build failures
- **Exit Codes**: Proper exit codes for script automation
- **Validation**: Checks for required files and directories

### Cross-Platform Support
- **Batch Files**: Native Windows Command Prompt support
- **PowerShell**: Enhanced PowerShell scripts with colors
- **Manual Options**: Platform-independent manual compilation

## Directory Structure

After a successful build, your directory structure will look like:

```
FlightManagement/
├── bin/                    # Compiled Java classes
│   ├── Main.class
│   ├── model/
│   ├── service/
│   ├── util/
│   └── view/
├── data/                   # JSON data files (created automatically)
├── src/                    # Java source files
├── build.bat              # Build script
├── run.bat                # Run script
├── start.bat              # Build and run script
├── clean.bat              # Clean script
├── reset-data.bat         # Data reset script
└── make.bat               # Make-style script
```

## Troubleshooting

### Common Issues

1. **"javac is not recognized"**
   - Ensure Java JDK is installed and added to PATH
   - Verify with: `javac -version`

2. **"Application not compiled"**
   - Run `build.bat` before `run.bat`
   - Or use `start.bat` to build and run together

3. **Permission denied errors**
   - Run Command Prompt as Administrator
   - Or use PowerShell with execution policy: `Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser`

4. **Build fails with errors**
   - Check Java source files for syntax errors
   - Ensure all required files are present in src/ directory
   - Use `clean.bat` and try building again

### Performance Tips

1. **Use start.bat** for development - it ensures a clean build every time
2. **Use build.bat + run.bat** for production - faster if no changes made
3. **Use clean.bat** when switching between different versions
4. **Use make.bat** for automated build scripts

## IDE Integration

You can also import this project into your favorite Java IDE:

### IntelliJ IDEA
1. Open IntelliJ IDEA
2. File → Open → Select the FlightManagement directory
3. Mark `src` as Sources Root
4. Set output directory to `bin`

### Eclipse
1. File → Import → Existing Projects into Workspace
2. Select the FlightManagement directory
3. Configure build path to include `src` directory

### VS Code
1. Open the FlightManagement directory
2. Install Java Extension Pack
3. The project should be automatically configured

## Automation

For continuous integration or automated testing:

```bash
# Build and test script
@echo off
call clean.bat
call build.bat
if %errorlevel% neq 0 exit /b 1
echo Build successful - ready for deployment
```

This build system ensures efficient, reliable compilation without generating duplicate class files, making your development process smooth and fast. 