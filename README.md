# System Monitor
> A terminal-based system diagnostics tool built with Java and the OSHI library.  
> Displays hardware, software, and OS-level information through an interactive menu.

---

## What It Does

This program gives you a live, readable snapshot of your machine — CPU load, RAM usage, disk speeds, battery state, USB devices, display info, and more. Everything runs in the terminal, no GUI required.

Built for a module project at the University of Limerick, but usable on any Windows, macOS, or Linux machine with Java installed.

---

## Prerequisites

| Requirement | Version | Notes |
|-------------|---------|-------|
| Java JDK | 17 or higher | JDK 24 used in development |
| Apache Maven | 3.6+ | For building and dependency management |

**Checking if you have them:**

```bash
java -version
mvn -version
```

If either command is not found, see the installation links below.

- **Java JDK:** https://adoptium.net (Temurin is a solid free choice)
- **Maven:** https://maven.apache.org/download.cgi

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/your-username/git-pushers.git
cd git-pushers
```

### 2. Build the project

Maven will automatically download the OSHI dependency on first build.

```bash
mvn compile
```

### 3. Run the program

```bash
mvn exec:java -Dexec.mainClass="org.example.Main"
```

Alternatively, package it as a JAR and run that:

```bash
mvn package
java -jar target/oshi-demo-1.0.0.jar
```

> **Note for macOS users:** Some hardware sensors (CPU temperature, voltage) may return "Unavailable" — this is a known OSHI limitation on Apple Silicon and newer macOS versions, not a bug in the code.

> **Note for Linux users:** Some readings (RAM module details, battery info) may require running with `sudo` depending on your distro's permissions.

---

## Menu Options

Once running, you'll see an interactive numbered menu. Enter the number for the section you want.

```
===MENU===
 1.  Display SYSTEM INFO
 2.  Display TCPv4 Stats
 3.  Display CPU Info
 4.  Hardware Info
 5.  USB Devices
 6.  Memory Info
 7.  Disk Info
 8.  PCI Info
 9.  Battery Info
 10. System Boot and Updates
 11. Task Manager
 12. User Information
 13. Display Monitor Info
 14. Exit
```

---

### Option 1 — System Info
Displays the machine's manufacturer, model, serial number, operating system, and platform.

---

### Option 2 — TCP/UDP Stats
Outputs network protocol statistics for TCPv4, TCPv6, UDPv4, and UDPv6 — useful for checking connection counts and error rates.

---

### Option 3 — CPU Info
The most detailed section. Shows:

- Processor name and microarchitecture
- Physical and logical core counts
- Cache hierarchy (L1/L2/L3 with sizes)
- CPU temperature and voltage (where available)
- Per-core frequencies in MHz
- Average CPU load (sampled over 1 second)
- Per-core utilisation, sorted from most to least active
- CPU health status (flags "High Load" above 80%)

> This option takes ~2 seconds to complete due to load sampling.

---

### Option 4 — Hardware Info
Firmware (BIOS/UEFI) and baseboard (motherboard) details: manufacturer, model, version, release date, and serial number.

---

### Option 5 — USB Devices
Lists all connected USB devices, both in a hierarchical tree (showing parent/child relationships) and as a flat list of directly connected devices. Shows name, vendor, vendor ID, product ID, and serial number where available.

---

### Option 6 — Memory Info
RAM overview including:

- Total, used, and free memory in GB with percentage
- Memory efficiency score
- Swap / virtual memory usage
- Installed RAM module details (manufacturer, capacity, type, clock speed)
- A visual ASCII usage bar

---

### Option 7 — Disk Info
For each detected drive:

- File system type, mount point, and label
- Total, used, and free space in GB
- ASCII usage bar
- Live read/write speed measured over 5 seconds

> This option takes ~5 seconds per disk due to speed measurement.

---

### Option 8 — PCI Info
Sub-menu with three options:

```
 1. Graphics Card Info     — GPU name, vendor, device ID, VRAM
 2. Network Interfaces     — MAC, IPv4/IPv6, speed, upload/download rate, adapter type
 3. Disk Stats             — Model, serial number, read/write counts
```

---

### Option 9 — Battery Info
Displays battery name, charging state, temperature, voltage, manufacturer, and estimated time remaining. Shows "No battery detected" on desktops.

---

### Option 10 — System Boot and Updates
Shows the system boot timestamp, the epoch value, and total uptime in hours.

---

### Option 11 — Task Manager
Three things in one view:

- All running system services with their state and PID
- Top 20 processes sorted by CPU usage
- Current thread details (ID, state, kernel/user time, priority)

---

### Option 12 — User Information
Current username, home directory, hostname, domain name, DNS servers, and whether the program is running with elevated (admin/sudo) permissions.

---

### Option 13 — Display / Monitor Info
Uses EDID data to read:

- Monitor manufacturer
- Physical dimensions (cm) and approximate aspect ratio
- Digital or analog connection type

Also uses Java AWT to retrieve current resolution, refresh rate, and bit depth for each connected screen.

---

### Option 14 — Exit
Closes the scanner and exits cleanly.

---

## Project Structure

```
git-pushers/
├── src/
│   └── main/
│       └── java/
│           └── org/example/
│               └── Main.java       ← All program logic
├── pom.xml                         ← Maven config and OSHI dependency
└── README.md
```

---

## Dependencies

This project uses one external library, declared in `pom.xml` and fetched automatically by Maven:

| Library | Version | Purpose |
|---------|---------|---------|
| [OSHI](https://github.com/oshi/oshi) | 6.4.5 | OS and hardware information via native APIs |

OSHI itself bundles JNA (Java Native Access) to talk to the underlying OS — no additional installs needed.

---

## Known Limitations

- CPU temperature and voltage may show as `0` or "Unavailable" on Windows (OSHI limitation, not a bug)
- Disk speed measurement adds a 5-second delay per drive
- CPU load sampling adds ~2 seconds to the CPU Info section
- Some readings on Linux may need `sudo` for full access
- RAM module details are not always available on virtualised machines

---
