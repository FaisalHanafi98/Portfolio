# Windows Development Environment Setup

Complete guide to setting up development tools on Windows and making them globally accessible.

---

## Understanding PATH Environment Variable

The **PATH** is a system variable that tells Windows where to find executable programs. When you type a command like `gradle` or `java`, Windows searches through all directories listed in PATH.

---

## Part 1: Install Gradle

### Step 1: Download Gradle

1. Go to: https://gradle.org/releases/
2. Download **Gradle 8.5** (Binary-only)
3. You'll get a file like: `gradle-8.5-bin.zip`

### Step 2: Extract Gradle

1. Create a directory: `C:\Tools\`
2. Extract `gradle-8.5-bin.zip` to `C:\Tools\`
3. You should now have: `C:\Tools\gradle-8.5\`

### Step 3: Add Gradle to PATH

#### Using GUI (Easier):

1. **Open System Properties:**
   - Press `Windows + R`
   - Type: `sysdm.cpl`
   - Press Enter

2. **Open Environment Variables:**
   - Click **Advanced** tab
   - Click **Environment Variables** button

3. **Edit PATH:**
   - Under **System variables** (bottom section)
   - Find and select **Path**
   - Click **Edit**

4. **Add Gradle:**
   - Click **New**
   - Add: `C:\Tools\gradle-8.5\bin`
   - Click **OK** on all windows

5. **Verify Installation:**
   - **Open a NEW PowerShell window** (important!)
   - Run:
     ```powershell
     gradle --version
     ```
   - You should see Gradle version info

#### Using PowerShell (Alternative):

```powershell
# Run PowerShell as Administrator
# Add to User PATH (recommended for personal machine)
$currentPath = [Environment]::GetEnvironmentVariable("Path", "User")
$newPath = $currentPath + ";C:\Tools\gradle-8.5\bin"
[Environment]::SetEnvironmentVariable("Path", $newPath, "User")

# Verify (open NEW PowerShell window)
gradle --version
```

---

## Part 2: Install Java (Required for Gradle)

Gradle needs Java to run.

### Check if Java is Installed

```powershell
java -version
```

If not installed:

### Option A: Download from Oracle

1. Go to: https://www.oracle.com/java/technologies/downloads/
2. Download **Java 17** (JDK)
3. Run installer
4. Java installer usually adds itself to PATH automatically

### Option B: Use Microsoft Build of OpenJDK

```powershell
# Download Microsoft OpenJDK 17
# URL: https://learn.microsoft.com/en-us/java/openjdk/download
# Download the .msi installer and run it
# It will automatically configure PATH
```

### Verify Java

```powershell
# Open NEW PowerShell window
java -version
javac -version
```

---

## Part 3: Install Chocolatey (Optional but Recommended)

Chocolatey is like `apt` or `brew` for Windows - makes installing tools easier.

### Install Chocolatey

1. **Open PowerShell as Administrator:**
   - Right-click PowerShell
   - Select "Run as Administrator"

2. **Run this command:**
   ```powershell
   Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
   ```

3. **Close and reopen PowerShell as Admin**

4. **Verify:**
   ```powershell
   choco --version
   ```

### Using Chocolatey to Install Tools

Once Chocolatey is installed, you can easily install tools:

```powershell
# Install Node.js
choco install nodejs -y

# Install Git
choco install git -y

# Install Java
choco install openjdk17 -y

# Install Gradle
choco install gradle -y

# Install multiple at once
choco install nodejs git openjdk17 gradle -y
```

---

## Part 4: Common Development Tools Setup

### Node.js and npm

**Check if installed:**
```powershell
node --version
npm --version
```

**If not installed:**
1. Download from: https://nodejs.org/
2. Install the LTS version
3. Installer adds to PATH automatically

**Verify (new PowerShell):**
```powershell
node --version
npm --version
```

### Git

**Check if installed:**
```powershell
git --version
```

**If not installed:**
1. Download from: https://git-scm.com/download/win
2. Run installer (use default options)
3. It adds to PATH automatically

---

## Part 5: Understanding PATH Priority

Windows searches PATH directories **in order**. If multiple programs have the same name, the first one found is used.

### View Current PATH

```powershell
# View all PATH entries
$env:Path -split ';'

# Or more readable
[Environment]::GetEnvironmentVariable("Path", "User") -split ';'
[Environment]::GetEnvironmentVariable("Path", "Machine") -split ';'
```

### Best Practices for PATH

1. **User vs System PATH:**
   - **User PATH**: Only for your account
   - **System PATH**: For all users (needs admin)
   - Use User PATH for personal development tools

2. **Directory Structure:**
   ```
   C:\Tools\
   ├── gradle-8.5\
   ├── maven-3.9\
   └── custom-scripts\
   ```

3. **Add to PATH in this order:**
   ```
   C:\Tools\gradle-8.5\bin
   C:\Program Files\Java\jdk-17\bin
   C:\Program Files\nodejs\
   ```

---

## Part 6: Quick Reference - Adding Programs to PATH

### For Any Program:

1. **Find the executable:**
   - Example: `C:\Tools\SomeProgram\bin\program.exe`

2. **Add parent directory to PATH:**
   - Add: `C:\Tools\SomeProgram\bin`
   - NOT: `C:\Tools\SomeProgram\bin\program.exe`

3. **Restart terminal** to apply changes

### Common Locations:

| Program | Typical Location | Add to PATH |
|---------|------------------|-------------|
| Java | `C:\Program Files\Java\jdk-17` | Add `...\bin` |
| Node.js | `C:\Program Files\nodejs` | Entire folder |
| Python | `C:\Python311` | Add both `...` and `...\Scripts` |
| Git | `C:\Program Files\Git` | Add `...\cmd` |
| Gradle | `C:\Tools\gradle-8.5` | Add `...\bin` |

---

## Part 7: Troubleshooting

### Command Not Found After Adding to PATH

**Problem:** Typed `gradle` but still get "not recognized"

**Solutions:**
1. **Restart PowerShell** - PATH changes need new session
2. **Check spelling** - Ensure directory exists
3. **Check PATH was saved:**
   ```powershell
   $env:Path -split ';' | Select-String gradle
   ```
4. **Try full path:**
   ```powershell
   C:\Tools\gradle-8.5\bin\gradle.bat --version
   ```

### Multiple Versions Conflict

**Problem:** Have both Gradle 7 and 8, wrong version runs

**Solution:** Order matters in PATH. Put desired version first:
```powershell
# View order
$env:Path -split ';'

# Desired version should appear first
C:\Tools\gradle-8.5\bin      ← This will be used
C:\Tools\gradle-7.6\bin      ← Ignored
```

### Permission Denied

**Problem:** "Access denied" when modifying System PATH

**Solution:**
1. Use **User PATH** instead, OR
2. Run PowerShell as Administrator

---

## Part 8: Your Portfolio Project Setup Checklist

Based on your portfolio project, ensure these are installed and in PATH:

```powershell
# Check all required tools
node --version     # Should show v18+
npm --version      # Should show 9+
java -version      # Should show 17+
gradle --version   # Should show 8.5
git --version      # Should show 2.x
```

### If Any Are Missing:

#### Quick Install All (with Chocolatey):
```powershell
# Run as Administrator
choco install nodejs openjdk17 gradle git -y
```

#### Manual Install:
1. **Node.js**: https://nodejs.org/
2. **Java 17**: https://www.oracle.com/java/technologies/downloads/
3. **Gradle**: https://gradle.org/releases/
4. **Git**: https://git-scm.com/download/win

---

## Part 9: Build Your Portfolio (After Setup)

Once Gradle is in PATH:

```powershell
# Navigate to backend
cd C:\Users\asbou\OneDrive\Desktop\Work\Portfolio\backend

# Build the JAR
gradle build -x test

# JAR will be created at:
# build\libs\portfolio-0.0.1-SNAPSHOT.jar
```

For frontend:
```powershell
# Navigate to frontend
cd C:\Users\asbou\OneDrive\Desktop\Work\Portfolio\frontend

# Build
npm install
npm run build

# Built files will be in: dist\
```

---

## Part 10: Quick Commands Reference

```powershell
# Refresh PATH in current session (without restarting)
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

# Add directory to PATH temporarily (current session only)
$env:Path += ";C:\Tools\SomeProgram\bin"

# View PATH directories (formatted)
$env:Path -split ';' | ForEach-Object { $_ }

# Check if program is in PATH
Get-Command gradle
Get-Command java
Get-Command node
```

---

## Summary

1. **Extract Gradle** to `C:\Tools\gradle-8.5\`
2. **Add to PATH**: `C:\Tools\gradle-8.5\bin`
3. **Restart PowerShell**
4. **Verify**: `gradle --version`
5. **Build backend**: `gradle build -x test`

**Remember:** Always open a **NEW** PowerShell window after changing PATH!

---

Created for Portfolio Platform project setup on Windows.
