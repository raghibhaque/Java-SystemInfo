<p align="center">
  <img src="https://github.com/raghibhaque/Java-SystemInfo/blob/main/image_2025-10-24_191045291.png?raw=true" 
       alt="Java System Info CLI Banner" 
       width="100%">
</p>

<h1 align="center">🖥️ Java System Info CLI</h1>


<h1 align="center">🖥️ Java System Info CLI</h1>

<p align="center">
  <i>A clean & powerful system information tool built in pure Java using the <a href="https://github.com/oshi/oshi">OSHI</a> library.</i><br>
  <b>Displays live CPU, memory, disk, network, battery, and system stats — all in your terminal. Done over the course of a week for a group project.</b>
</p>

---

<p align="center">
  <img src="https://img.shields.io/badge/Made%20with-Java%2017-orange?style=for-the-badge&logo=openjdk" alt="Java">
  <img src="https://img.shields.io/badge/Build-Maven%20%F0%9F%94%A7-blue?style=for-the-badge&logo=apachemaven" alt="Maven">
  <img src="https://img.shields.io/github/license/raghibhaque/Java-SystemInfo?style=for-the-badge" alt="License">
</p>

---

## ✨ Features

> 🔹 **System Overview** – Manufacturer, model, serial number, OS platform  
> 🔹 **CPU Monitor** – Temperature, voltage, per-core usage, health status  
> 🔹 **Memory & Swap Stats** – Free, used, total, and visual progress bar  
> 🔹 **Disk Info** – Capacity, free space, read/write speeds, filesystem type  
> 🔹 **Network** – Upload/download rate, MAC/IP info, link speed  
> 🔹 **Battery** – Voltage, temperature, charge state, time remaining  
> 🔹 **Hardware & Firmware** – BIOS, baseboard, and GPU/PCI details  
> 🔹 **Processes** – Simple task-manager view sorted by CPU usage  
> 🔹 **User & Boot Info** – Hostname, domain, DNS, uptime, and boot time  

---

## 🧰 Tech Stack

| Component | Description |
|------------|-------------|
| ☕ **Java 17** | Core programming language |
| 🧩 **OSHI** | Hardware and OS metrics |
| ⚙️ **Maven** | Dependency management & build tool |
| 🖥️ **CLI** | Text-based interactive menu system |

---

## 🚀 Quick Start

```bash
# Clone the repo
git clone https://github.com/raghibhaque/Java-SystemInfo.git
cd Java-SystemInfo

# Compile & run
mvn compile
mvn exec:java -Dexec.mainClass="org.example.Main"
```

💡 Future Enhancements

🌐 Add colored CLI output (ANSI colors)

📦 Build an executable .jar

📊 Export data to JSON or CSV

🧠 Integrate OSHI sensor graphs with Swing or JavaFX


🧑‍💻 Author

Raghib Haque
🎓 ISE @ University of Limerick | ☕ Java & Systems Engineering
Made with Coffee <3
