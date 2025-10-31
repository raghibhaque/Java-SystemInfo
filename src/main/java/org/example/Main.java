package org.example;
import oshi.SystemInfo; // for sys info
import oshi.hardware.*;
import oshi.software.os.InternetProtocolStats;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import java.util.*; // for scanner
import oshi.hardware.NetworkIF;//for network
import java.util.Arrays;
import java.util.List;
import oshi.software.os.OSProcess;
import oshi.util.EdidUtil;
import oshi.software.os.OSThread;
import oshi.software.os.OSService;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SystemInfo si = new SystemInfo();

        while (true) {
            System.out.println("===MENU===" +
                    "\n 1. Display SYSTEM INFO." +
                    "\n 2. Display TCPv4 Stats." +
                    "\n 3. Display CPU Info" +
                    "\n 4. Hardware Info" +
                    "\n 5. USB Devices" +
                    "\n 6. Memory Info" +
                    "\n 7. Disk Info" +
                    "\n 8. PCI Info" +
                    "\n 9. Battery Info" +
                    "\n 10. System boot and updates" +
                    "\n 11. Task manager" +
                    "\n 12. User information" +
                    "\n 13. Display Monitor Info" +
                    "\n 14. Exit");

            if (!sc.hasNextInt()) {
                System.out.println("Invalid input, please enter a number!");
                sc.nextLine();
                continue;
            }

            switch (sc.nextInt()) {
                case 1:
                    ComputerSystem cs2 = si.getHardware().getComputerSystem();
                    System.out.println("\n=== SYSTEM DETAILS ===");
                    System.out.println("Manufacturer : " + cs2.getManufacturer());
                    System.out.println("Model : " + cs2.getModel());
                    System.out.println("Serial Number : " + cs2.getSerialNumber());
                    System.out.println("OS: " + si.getOperatingSystem());
                    System.out.println("Current Platform : " + SystemInfo.getCurrentPlatform());
                    break;

                case 2:
                    InternetProtocolStats ipStats = si.getOperatingSystem().getInternetProtocolStats();
                    System.out.println("=== TCPv4 & UDP Statistics ===");
                    System.out.println("TCPv4 Stats: " + ipStats.getTCPv4Stats()); // connection oriented
                    System.out.println("TCPv6 Stats: " + ipStats.getTCPv6Stats());
                    System.out.println("UDPv4 Stats: " + ipStats.getUDPv4Stats());
                    System.out.println("UDPv6 Stats: " + ipStats.getUDPv6Stats());
                    break;

                case 3: // Get access to CPU and sensor information aka temps,freqs , the lot
                    CentralProcessor processor = si.getHardware().getProcessor();
                    Sensors sensors = si.getHardware().getSensors();
                    System.out.println("\n=== CPU INFORMATION ===");// Basic CPU details

                    System.out.println("Processor: " + processor.getProcessorIdentifier().getName());
                    System.out.println("Architecture: " + processor.getProcessorIdentifier().getMicroarchitecture());
                    System.out.println("Logical Cores: " + processor.getLogicalProcessorCount());
                    System.out.println("Physical Cores: " + processor.getPhysicalProcessorCount());
                    //num of physical cpu chips
                    System.out.println("Packages: " + processor.getPhysicalPackageCount());
                    System.out.println("CPU Voltage: " + sensors.getCpuVoltage() + " V");
                    //Liza
                    System.out.println("CPU Logical Thread Count: " + processor.getLogicalProcessorCount());
                    System.out.println("System Uptime: " + si.getOperatingSystem().getSystemUptime() / 60 + " minutes");
                    //

                    System.out.println("\n=== Cache Hierarchy ===");
                    if(si.getOperatingSystem().getFamily().equalsIgnoreCase("Windows")){
                        System.out.println("Subject to inaccuracy for Windows");
                    }
                    List<CentralProcessor.ProcessorCache> caches = processor.getProcessorCaches(); // Gets a list of all cache levels

                    for (CentralProcessor.ProcessorCache cache : caches) // Loops through each cache found in the CPU.
                    {
                        long size = cache.getCacheSize();
                        if (size>=1_000_000) { // Jerry
                            String cacheSize = String.format("%.2f MB", size / 1_000_000.0); // div by 1,000,000 - base 10
                            System.out.printf("Level %d %s Cache: %s%n", cache.getLevel(), cache.getType(), cacheSize);
                        }
                        else if(size>=1000){
                            String cacheSize = String.format("%.2f KB", size / 1_000.0); // div by 1,000- base 10
                            System.out.printf("Level %d %s Cache: %s%n", cache.getLevel(), cache.getType(), cacheSize);
                        }
                        else{
                            String cacheSize = size > 0 ? String.format("%s Bytes", size) : "Unavailable";
                            System.out.printf("Level %d %s Cache: %s,%n", cache.getLevel(), cache.getType(), cacheSize);
                        }
                    }

                    double temp = sensors.getCpuTemperature();
                    System.out.println("CPU Temperature: " + (temp > 0 ? temp + " °C" : "Unavailable"));

                    long[] freqs = processor.getCurrentFreq();
                    System.out.println("\n=== Core Frequencies (MHz) ===");
                    for (int i = 0; i < freqs.length; i++) {
                        System.out.printf("Core %d: %.2f MHz%n", i, freqs[i] / 1_000_000.0);
                    }

                    System.out.println("\nCollecting CPU usage snapshot...");
                    //time cpu spends doing work since start
                    long[] prevTicks = processor.getSystemCpuLoadTicks();
                    //need time to pass between two CPU tick how much cpu was used
                    try {
                        Thread.sleep(1000); } catch (InterruptedException ignored) {
                    }
                    //compares the current CPU tick data with the previous snapshot prevtick
                    double avgLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
                    System.out.printf("Average CPU Load: %.1f%%%n", avgLoad);
                    //Liza
                    String status = avgLoad > 80 ? "High Load" : "Normal";
                    System.out.println("CPU Health: " + status);
                    //

                    long[][] prevCoreTicks = processor.getProcessorCpuLoadTicks();
                    try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                    double[] perCore = processor.getProcessorCpuLoadBetweenTicks(prevCoreTicks);

                    if (perCore != null) {
                        System.out.println("\n=== Core Utilization ===");
                        //Liza
                        double[] coreLoads = new double[perCore.length];
                        //converts to %
                        for (int i = 0; i < perCore.length; i++) {
                            coreLoads[i] = perCore[i] * 100;
                            System.out.printf("Core %d: %.1f%%%n", i + 1, coreLoads[i]);
                        }
                        double[] sortedLoads = Arrays.copyOf(coreLoads, coreLoads.length);
                        Arrays.sort(sortedLoads);
                        boolean[] printed = new boolean[coreLoads.length];
                        System.out.println("\n=== Most Active Cores ===");
                        //sorting by greatest value
                        for (int i = sortedLoads.length - 1; i >= 0; i--) {
                            for (int j = 0; j < coreLoads.length; j++) {
                                if (!printed[j] && sortedLoads[i] == coreLoads[j]) {
                                    System.out.printf("Core %d: %.1f%%%n", j +1, sortedLoads[i]);
                                    printed[j] = true;
                                    break;
                                }
                            }
                        }
                    }
                    break;
                case 4:
                    ComputerSystem cs = si.getHardware().getComputerSystem();
                    Firmware firm = cs.getFirmware();
                    Baseboard base = cs.getBaseboard();

                    System.out.println("=== Firmware Information ===");
                    //low-level software used before your operating system runs
                    System.out.println("Name: " + firm.getName());
                    System.out.println("Version: " + firm.getVersion());
                    System.out.println("Manufacturer: " + firm.getManufacturer());
                    System.out.println("Release Date: " + firm.getReleaseDate());

                    System.out.println("\n=== Baseboard Information ===");
                    //motherboard
                    System.out.println("Manufacturer: " + base.getManufacturer());
                    System.out.println("Model: " + base.getModel());
                    System.out.println("Version: " + base.getVersion());
                    System.out.println("Serial Number: " + base.getSerialNumber());
                    break;

                case 5:
                    System.out.println("=== USB DEVICE INFORMATION ===");
                    //tree structure devices and their child devices
                    List<UsbDevice> usbDevices = si.getHardware().getUsbDevices(true);
                    System.out.println("USB Devices - Hierarchical (All Connected):");
                    for (UsbDevice device : usbDevices) {
                        System.out.println("-------------------------------------------------");
                        System.out.println("Name : " + device.getName());
                        System.out.println("Vendor : " + device.getVendor());
                        System.out.println("Vendor ID : " + device.getVendorId());
                        System.out.println("Product ID : " + device.getProductId());
                        System.out.println("Serial Num : " + device.getSerialNumber());
                    }

                    System.out.println("\nUSB Devices - Directly Connected:");
                    //flat list just the top-level USB devices
                    List<UsbDevice> topDevices = si.getHardware().getUsbDevices(false);
                    for (UsbDevice device : topDevices) { // for every device in the list we print its own details
                        System.out.println("=====");
                        System.out.println("Name : " + device.getName());
                        System.out.println("Vendor : " + device.getVendor());
                        System.out.println("Vendor ID : " + device.getVendorId());
                        System.out.println("Product ID : " + device.getProductId());
                    }
                    break;

                case 6:  //  Memory stats - API = https://javadoc.io/static/com.github.oshi/oshi-core/5.6.1/oshi/hardware/GlobalMemory.html
                    GlobalMemory memory = si.getHardware().getMemory();
                    System.out.println("\n=== MEMORY INFORMATION ===");
                    long total = memory.getTotal();
                    long available = memory.getAvailable();
                    long used = total - available;
                    double usedGB = used / (1024.0 * 1024 * 1024);
                    double totalGB = total / (1024.0 * 1024 * 1024);
                    double percentUsed = (used * 100.0) / total;
                    //Liza
                    double memEff = (available / (double) total) * 100;

                    System.out.printf("Total Memory: %.2f GB%n", totalGB);
                    System.out.printf("Used Memory : %.2f GB (%.1f%%)%n", usedGB, percentUsed);
                    System.out.printf("Free Memory : %.2f GB%n", available / (1024.0 * 1024 * 1024));
                    System.out.printf("Memory Efficiency: %.2f%% free%n", memEff);

                    //  Swap memory back up eg SSD
                    VirtualMemory swap = memory.getVirtualMemory();
                    double swapUsedGB = swap.getSwapUsed() / (1024.0 * 1024 * 1024);
                    double swapTotalGB = swap.getSwapTotal() / (1024.0 * 1024 * 1024);
                    if (swapTotalGB > 0) {
                        double swapPercent = (swapUsedGB * 100) / swapTotalGB;
                        System.out.printf("Swap Used: %.2f / %.2f GB (%.1f%%)%n", swapUsedGB, swapTotalGB, swapPercent);
                    } else {
                        System.out.println("Swap: Not available or disabled");
                    }
                    System.out.println("Max Virtual Memory : " + swap.getVirtualMax()/(1024*1024*1024) + "GB");
                    System.out.println("Current Virtual Memory : " + swap.getVirtualInUse()/(1024*1024*1024) + "GB");



                    List<PhysicalMemory> ramModules = memory.getPhysicalMemory();
                    if (!ramModules.isEmpty()) {
                        System.out.println("\n=== Installed RAM Modules ===");
                        for (PhysicalMemory ram : ramModules) {
                            System.out.printf("%s: %.2f GB, %s, %d MHz%n",
                                    ram.getManufacturer(),
                                    ram.getCapacity() / (1024.0 * 1024 * 1024), // convert bytes to GB
                                    ram.getMemoryType(),
                                    ram.getClockSpeed() / 1_000_000);// convert Hz to MHz
                        }
                    } else {
                        System.out.println("RAM module information unavailable.");
                    }
                    int barLength = 30;
                    int barFilled = (int) (barLength * percentUsed / 100);
                    String bar = "[" + "#".repeat(barFilled) + "-".repeat(barLength - barFilled) + "]";
                    System.out.println("Usage: " + bar + " " + String.format("%.1f%%", percentUsed));
                    break;

                // --- DISK INFO ---
                case 7:
                    List<HWDiskStore> diskInfo = si.getHardware().getDiskStores();
                    List<OSFileStore> diskSoft = si.getOperatingSystem().getFileSystem().getFileStores();

                    System.out.println("\n=== DISK INFORMATION ===");
                    System.out.println("Hardware disks found: " + diskInfo.size());
                    System.out.println("File stores found: " + diskSoft.size());

                    int minSize = Math.min(diskInfo.size(), diskSoft.size());

                    for (int i = 0; i < minSize; i++) {
                        HWDiskStore disk = diskInfo.get(i);
                        OSFileStore currentDisk = diskSoft.get(i);

                        disk.updateAttributes();
                        currentDisk.updateAttributes();

                        System.out.println("\n--------------------------------------------------");
                        System.out.println("Disk " + (i + 1) + ": " + currentDisk.getLabel() + " (" + currentDisk.getName() + ")");
                        System.out.println("File System Type: " + currentDisk.getType());
                        System.out.println("Mount Point: " + currentDisk.getMount());
                        System.out.println("Description: " + currentDisk.getDescription());

                        double totalSpace = currentDisk.getTotalSpace();
                        double freeSpace = currentDisk.getFreeSpace();
                        double usedSpace = totalSpace - freeSpace;

                        double usedPercent = (usedSpace / totalSpace) * 100.0;

                        System.out.printf("Total Space: %.2f GB%n", totalSpace / 1_000_000_000);
                        System.out.printf("Used Space : %.2f GB (%.1f%%)%n", usedSpace / 1_000_000_000, usedPercent);
                        System.out.printf("Free Space : %.2f GB%n", freeSpace / 1_000_000_000);

                        int barLength2 = 30;
                        int barFilled3 = (int) (barLength2 * usedPercent / 100);
                        String bar2 = "[" + "#".repeat(barFilled3) + "-".repeat(barLength2 - barFilled3) + "]";
                        System.out.println("Usage: " + bar2);

                        System.out.println("\nMeasuring disk activity... please wait 5 seconds...");
                        long readsStart = disk.getReadBytes();
                        long writesStart = disk.getWriteBytes();
                        long timeStart = System.currentTimeMillis();

                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException ignored) {}

                        disk.updateAttributes();

                        long readsEnd = disk.getReadBytes();
                        long writesEnd = disk.getWriteBytes();
                        long elapsed = System.currentTimeMillis() - timeStart;

                        double readSpeedMBs = (readsEnd - readsStart) / 1_000_000.0 / (elapsed / 1000.0);
                        double writeSpeedMBs = (writesEnd - writesStart) / 1_000_000.0 / (elapsed / 1000.0);

                        System.out.printf("Read Speed : %.2f MB/s%n", readSpeedMBs);
                        System.out.printf("Write Speed: %.2f MB/s%n", writeSpeedMBs);
                        System.out.println("--------------------------------------------------");
                    }

                    if (minSize == 0) {
                        System.out.println("No disks detected or unable to retrieve disk info.");
                    }

                    break;

                // --- PCI INFO ---
                case 8: // By Jerry H.
                    System.out.println("===PCI Info===" +
                            "\n 1. Graphics Card Info" +
                            "\n 2. Network Interfaces" +
                            "\n 3. Disk Stats");
                    int choice = sc.nextInt();

                    if (choice == 1) {
                        List<GraphicsCard> gpus = si.getHardware().getGraphicsCards();
                        for (GraphicsCard gpu : gpus) {
                            System.out.println("GPU: " + gpu.getName());
                            System.out.println("Vendor: " + gpu.getVendor());
                            System.out.println("Device ID: " + gpu.getDeviceId());
                            System.out.printf("VRAM: %.2f GB%n", gpu.getVRam() / (1024.0 * 1024 * 1024));
                        }
                    } else if (choice == 2) {
                        //Liza
                        for (NetworkIF net : si.getHardware().getNetworkIFs()) {
                            net.updateAttributes();
                            long sentStart = net.getBytesSent();
                            long recvStart = net.getBytesRecv();//received
                            long startTime = System.currentTimeMillis();

                            net.updateAttributes();//clear cache
                            long sentEnd = net.getBytesSent();
                            long recvEnd = net.getBytesRecv();
                            long elapsedMs = System.currentTimeMillis() - startTime;

                            //bytes transfered converted to KB per sec
                            double uploadKBs = (sentEnd - sentStart) / 1024.0 / (elapsedMs / 1000.0);
                            double downloadKBs = (recvEnd - recvStart) / 1024.0 / (elapsedMs / 1000.0);

                            System.out.printf("%s - Upload: %.2f KB/s, Download: %.2f KB/s%n",
                                    net.getName(), uploadKBs, downloadKBs);

                            System.out.println("Name: " + net.getName());
                            System.out.println("MAC: " + net.getMacaddr());
                            //internet protocols
                            System.out.println("IPv4: " + Arrays.toString(net.getIPv4addr()));
                            System.out.println("IPv6: " + Arrays.toString(net.getIPv6addr()));
                            //speed in bits per sec to megabits
                            System.out.println("Interface speed: " + net.getSpeed() / 1000000 + " Mbps");
                            //kind of network
                            System.out.println("Physical medium/kind: " + net.getNdisPhysicalMediumType());
                            //wireless?
                            System.out.println("Connector present: " + net.isConnectorPresent());
                            System.out.println("Alias: " + net.getIfAlias());

                        }
                    } else if (choice == 3) {
                        for (HWDiskStore disk : si.getHardware().getDiskStores()) {
                            System.out.println("Disk: " + disk.getModel());
                            System.out.println("Serial: " + disk.getSerial());
                            System.out.println("Reads: " + disk.getReads());
                            System.out.println("Writes: " + disk.getWrites());
                        }
                    } else {
                        System.out.println("Invalid PCI option!");
                    }
                    break;

                // Raghib - battery info
                case 9: // https://www.oshi.ooo/oshi-core/apidocs/oshi/hardware/PowerSource.html
                    List<PowerSource> batteries = si.getHardware().getPowerSources();
                    System.out.println("=== Battery Info ===");

                    if (batteries.isEmpty()) {
                        System.out.println("No battery detected.");
                    }
                    else {
                        for (PowerSource bat : batteries)
                        {
                            System.out.printf("Name: %s%n", bat.getName());
                            System.out.printf("Charging: %s%n", bat.isCharging() ? "Yes" : "No");
                            System.out.println("Battery Temperature : " + bat.getTemperature() + "°C");
                            System.out.println("Battery Voltage : " + bat.getVoltage() + "V");
                            System.out.println("Battery Manufacturer : " + bat.getManufacturer());

                            double timeRemaining = bat.getTimeRemainingEstimated();
                            if (timeRemaining >= 0)
                            {
                                System.out.printf("Time Remaining: %.1f minutes%n", timeRemaining / 60.0);
                            }
                            else
                            {
                                System.out.println("Time Remaining: Unknown");
                            }
                            System.out.println();
                        }
                    }
                    break;
                case 10:
                    //System boot and updates
                    OperatingSystem osBoot = si.getOperatingSystem();
                    long bootTime = osBoot.getSystemBootTime();
                    long up = osBoot.getSystemUptime();
                    System.out.println("=== SYSTEM BOOT INFO ===");
                    System.out.println("Boot Time (epoch): " + bootTime);
                    System.out.println("System Uptime: " + (up / 3600) + " hours");
                    System.out.println("Booted Since: " + new java.util.Date(bootTime * 1000L));


                    break;
                case 11:
                    //Task manager view
                    OperatingSystem os18 = si.getOperatingSystem();

                    List<OSService> services = si.getOperatingSystem().getServices();
                    System.out.println("=== SYSTEM SERVICES ===");
                    for (OSService service : services) {
                        System.out.println(service.getName() + " - Status: " + service.getState() + " - PID: " + service.getProcessID());
                    }
                    //list of running processes / no filter
                    List<OSProcess> procs = os18.getProcesses(null, OperatingSystem.ProcessSorting.CPU_DESC, 20);
                    for (oshi.software.os.OSProcess p : procs) {
                        System.out.printf("%s CPU: %.1f%%%n",
                                p.getName(),
                                100d * p.getProcessCpuLoadCumulative());
                    }

                    OSThread thread = si.getOperatingSystem().getCurrentThread();
                    System.out.println("Current Thread ID: " + thread.getThreadId());
                    System.out.println("Current Thread State: " + thread.getState());
                    System.out.println("Thread CPU Time: " + thread.getKernelTime() + " ms kernel, " + thread.getUserTime() + " ms user");
                    System.out.println("Thread Name: " + thread.getName());
                    System.out.println("Thread Priority: " + thread.getPriority());
                    break;
                case 12:
                    //User info dir
                    OperatingSystem os21 = si.getOperatingSystem();
                    //class System api / system info / InetAdress
                    System.out.println("=== USER INFO ===");
                    //key-value
                    System.out.println("Current User: " + System.getProperty("user.name"));
                    System.out.println("Home Directory: " + System.getProperty("user.home"));
                    //host name for IP address
                    System.out.println("Host Name: " + os21.getNetworkParams().getHostName());
                    System.out.println("Domain Name: " + os21.getNetworkParams().getDomainName());
                    System.out.println("DNS Servers: " + Arrays.toString(os21.getNetworkParams().getDnsServers()));

                    boolean elevated = si.getOperatingSystem().isElevated();
                    System.out.println("Elevated Permissions: " + (elevated ? "Yes (Admin/Sudo)" : "No"));

                    break;

                case 13: // raghib
                    // OSHI API: https://www.oshi.ooo/oshi-core/apidocs/oshi/hardware/Display.html + https://www.oshi.ooo/oshi-core/apidocs/oshi/util/EdidUtil.html
                    System.out.println("=== DISPLAY / EDID INFORMATION ===");
                    List<Display> displays = si.getHardware().getDisplays();

                    if (displays.isEmpty()) {
                        System.out.println("No displays detected.");
                    }
                    else
                    {
                        int displayIndex = 1;
                        for (Display display : displays) {
                            System.out.println("\n--- Display " + displayIndex + " ---");

                            // Extract EDID raw bytes
                            byte[] edid = display.getEdid();

                            String manufacturer = EdidUtil.getManufacturerID(edid);
                            int widthCm  = EdidUtil.getHcm(edid);
                            int heightCm = EdidUtil.getVcm(edid);
                            boolean isDigital = EdidUtil.isDigital(edid);

                            System.out.println("Manufacturer: " + manufacturer);
                            System.out.println("Display Type: " + (isDigital ? "Digital" : "Analog"));
                            System.out.println("Width: " + widthCm + " cm");
                            System.out.println("Height: " + heightCm + " cm");


                            if (widthCm > 0 && heightCm > 0)
                            {
                                double aspect = (double) widthCm / heightCm;
                                System.out.printf("Approx. Aspect Ratio: %.2f:1%n", aspect);
                            }
                            displayIndex++;
                        }
                    }
                    try {
                        java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
                        java.awt.GraphicsDevice[] screens = ge.getScreenDevices();

                        int screenNum = 1;
                        for (java.awt.GraphicsDevice screen : screens) {
                            java.awt.DisplayMode mode = screen.getDisplayMode();
                            // https://docs.oracle.com/javase/8/docs/api/java/awt/DisplayMode.html
                            System.out.println("\n=== Current Display Settings for Screen " + screenNum + " ===");
                            System.out.println("Resolution: " + mode.getWidth() + " x " + mode.getHeight());
                            System.out.println("Refresh Rate: " + mode.getRefreshRate() + " Hz");
                            System.out.println("Bit Depth: " + mode.getBitDepth() + " bits per pixel");
                            screenNum++;
                        }
                    } catch (Exception e) {
                        System.out.println("Unable to retrieve current display settings: " + e.getMessage());
                    }
                    break;

                case 14:
                    System.out.println("Exiting program...");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                System.out.println("Sleep interrupted!");
            }
            sc.nextLine();
        }
    }
}