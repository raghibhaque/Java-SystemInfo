package org.example;
import oshi.SystemInfo; // sys info importing
import oshi.hardware.*;
import oshi.software.os.InternetProtocolStats;
import oshi.software.os.OperatingSystem;

import java.util.*; // for scanner


public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SystemInfo si = new SystemInfo();
        while (true) {


            System.out.println("===MENU===" +
                    " \n 1. Display SYSTEM INFO." +
                    " \n 2. Display TCPv4 Stats. " +
                    "\n 3. Display CPU Info " +
                    "\n 4. Hardware Info" +
                    "\n 5. USB Devices" +
                    "\n 6. Memory Info" +
                    "\n 7. Disk Info" +
                    "\n 8. Exit");

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
                    System.out.println("TCPv4 Stats: " + ipStats.getTCPv4Stats());
                    break;

                case 3:
                    // Get access to CPU and sensor information aka temps,freqs , the lot
                    CentralProcessor processor = si.getHardware().getProcessor();
                    Sensors sensors = si.getHardware().getSensors();

                    System.out.println("\n=== CPU INFORMATION ===");

                    // Basic CPU details
                    System.out.println("Processor: " + processor.getProcessorIdentifier().getName());
                    System.out.println("Architecture: " + processor.getProcessorIdentifier().getMicroarchitecture());
                    System.out.println("Logical Cores: " + processor.getLogicalProcessorCount());
                    System.out.println("Physical Cores: " + processor.getPhysicalProcessorCount());
                    System.out.println("Packages: " + processor.getPhysicalPackageCount());
                    System.out.println("CPU Voltage: " + sensors.getCpuVoltage() + " V");

                    // --- CACHE INFORMATION ---
                    System.out.println("\n=== Cache Hierarchy ===");
                    try {
                        List<CentralProcessor.ProcessorCache> caches = processor.getProcessorCaches();
                        for (CentralProcessor.ProcessorCache cache : caches) {
                            // Some OSHI versions use getCacheSize(), others use getSize()
                            // we also use reflection here since the oshi version can differ i.e. we try both
                            long size = 0; // storing cache here
                            try {
                                size = (long) cache.getClass().getMethod("getCacheSize").invoke(cache);
                            } catch (Exception e) {
                                try {
                                    size = (long) cache.getClass().getMethod("getSize").invoke(cache);
                                } catch (Exception ignored) {}
                            }

                            // Print cache level, type (e.g. DATA/INSTRUCTION/UNIFIED), and size in MB
                            String readableSize = size > 0 ? String.format("%.2f MB", size / 1_000_000.0) : "Unavailable";
                            System.out.printf("Level %d %s Cache: %s%n", cache.getLevel(), cache.getType(), readableSize);
                        }
                    } catch (Exception e) {
                        System.out.println("Cache information not available for this OSHI version.");
                    }

                    //  TEMPERATURE
                    double temp = sensors.getCpuTemperature();
                    System.out.println("CPU Temperature: " + (temp > 0 ? temp + " °C" : "Unavailable"));

                    //  FREQUENCY (current clock speed per core)
                    long[] freqs = processor.getCurrentFreq();
                    System.out.println("\n=== Core Frequencies (MHz) ===");
                    for (int i = 0; i < freqs.length; i++) {
                        System.out.printf("Core %d: %.2f MHz%n", i, freqs[i] / 1_000_000.0); // convert hz to MHz
                    }

                    // --- CPU UTILIZATION (Average and Per-Core) ---
                    System.out.println("\nCollecting CPU usage snapshot...");
                    long[] prevTicks = processor.getSystemCpuLoadTicks();
                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException ignored) {

                    }

                    // Overall CPU load
                    double avgLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100; // implementing because it makes it into a %, could leave it out if I wanted to
                    System.out.printf("Average CPU Load: %.1f%%%n", avgLoad);

                    // Per-core utilization (works in OSHI 5.x)
                    long[][] prevCoreTicks = processor.getProcessorCpuLoadTicks();
                    try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                    double[] perCore = processor.getProcessorCpuLoadBetweenTicks(prevCoreTicks);

                    if (perCore != null) {
                        System.out.println("\n=== Core Utilization ===");
                        for (int i = 0; i < perCore.length; i++) {
                            System.out.printf("Core %d: %.1f%%%n", i, perCore[i] * 100);
                        }
                    }
                    break;

                case 4: // Firmware

                    ComputerSystem cs = si.getHardware().getComputerSystem();
                    Firmware firm = cs.getFirmware();
                    Baseboard base = cs.getBaseboard();

                    System.out.println("=== Firmware Information ===");
                    System.out.println("Name: " + firm.getName());
                    System.out.println("Version: " + firm.getVersion());
                    System.out.println("Manufacturer: " + firm.getManufacturer());
                    System.out.println("Release Date: " + firm.getReleaseDate());

                    System.out.println("\n=== Baseboard Information ===");
                    System.out.println("Manufacturer: " + base.getManufacturer());
                    System.out.println("Model: " + base.getModel());
                    System.out.println("Version: " + base.getVersion());
                    System.out.println("Serial Number: " + base.getSerialNumber());
                    break;

                case 5:
                    System.out.println("=== USB DEVICE INFORMATION ===");

                    // Gets full history of usb's ever connected since Boolean b is true
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

                    // get only CURRENT connections
                    System.out.println("\nUSB Devices - Directly Connected:");
                    List<UsbDevice> topDevices = si.getHardware().getUsbDevices(false);
                    for (UsbDevice device : topDevices) { // for every device in the list we print its own details
                        System.out.println("-------------------------------------------------");
                        System.out.println("Name : " + device.getName());
                        System.out.println("Vendor : " + device.getVendor());
                        System.out.println("Vendor ID : " + device.getVendorId());
                        System.out.println("Product ID : " + device.getProductId());
                    }
                break;

                case 6:
                    GlobalMemory memory = si.getHardware().getMemory();

                    System.out.println("\n=== MEMORY INFORMATION ===");

                    //  Memory stats - API = https://javadoc.io/static/com.github.oshi/oshi-core/5.6.1/oshi/hardware/GlobalMemory.html
                    // WIP COME BACK TO THIS.
                    long total = memory.getTotal();
                    long available = memory.getAvailable();
                    long used = total - available;

                    double usedGB = used / (1024.0 * 1024 * 1024);
                    double totalGB = total / (1024.0 * 1024 * 1024);
                    double percentUsed = (used * 100.0) / total;

                    System.out.printf("Total Memory: %.2f GB%n", totalGB);
                    System.out.printf("Used Memory : %.2f GB (%.1f%%)%n", usedGB, percentUsed);
                    System.out.printf("Free Memory : %.2f GB%n", available / (1024.0 * 1024 * 1024));


                    // --- Swap memory ---
                    VirtualMemory swap = memory.getVirtualMemory();
                    double swapUsedGB = swap.getSwapUsed() / (1024.0 * 1024 * 1024);
                    double swapTotalGB = swap.getSwapTotal() / (1024.0 * 1024 * 1024);
                    if (swapTotalGB > 0) {
                        double swapPercent = (swapUsedGB * 100) / swapTotalGB;
                        System.out.printf("Swap Used: %.2f / %.2f GB (%.1f%%)%n", swapUsedGB, swapTotalGB, swapPercent);
                    } else {
                        System.out.println("Swap: Not available or disabled");
                    }

                    // RAM modules/info
                    List<PhysicalMemory> ramModules = memory.getPhysicalMemory();

                    if (!ramModules.isEmpty()) {
                        System.out.println("\n=== Installed RAM Modules ===");
                        for (PhysicalMemory ram : ramModules) {
                            System.out.printf("%s: %.2f GB, %s, %d MHz%n",
                                    ram.getManufacturer(),
                                    ram.getCapacity() / (1024.0 * 1024 * 1024),        // convert bytes to GB
                                    ram.getMemoryType(),                               // e.g. DDR4 / DDR5
                                    ram.getClockSpeed() / 1_000_000);                  // convert Hz to MHz
                        }
                    }
                    else
                    {
                        System.out.println("RAM module information unavailable.");
                    }
                    int barlength = 30;
                    int barfilled = (int) (barlength * percentUsed / 100);
                    String bar = "[" + "#".repeat(barfilled) + "-".repeat(barlength - barfilled) + "]";
                    System.out.println("Usage: " + bar + " " + String.format("%.1f%%", percentUsed));
                    break;

                case 7:
                    List<HWDiskStore> diskInfo = si.getHardware().getDiskStores();//hard drive section start

                    for (int i=0;i< diskInfo.size();i++) {
                        diskInfo.get(i).updateAttributes();
                        System.out.println("Displaying disk "+i +"'s information");//disk name as number
                        double diskSize = diskInfo.get(i).getSize();

                        //section to get the file type
                        OperatingSystem os=si.getOperatingSystem();
                        System.out.println("it uses the "+os.getFileSystem().getFileStores().get(0).getType()+" file system");


                        //section for total size
                        if (diskSize >= 1000000 && diskSize < 1000000000) {//makes MB if best
                            int diskSizeUnit = (int) (diskSize / 1000000);
                            System.out.println("Disk has " + diskSizeUnit + "MB total");
                        }
                        if (diskSize >= 1000000000) {//makes GB if best
                            int diskSizeUnit = (int) (diskSize / 1000000000);
                            System.out.println("Disk has " + diskSizeUnit + "GB total");
                        }
                        if (diskSize < 1000000) {//makes Bytes if nothing else applies
                            int diskSizeUnit = (int) diskSize;
                            System.out.println("Disk has " + diskSizeUnit + "Bytes total");
                        }

                        // section to get amt disk used
                        double diskUsed = diskInfo.get(i).getWriteBytes();
                        if (diskUsed >= 1000000 && diskUsed < 1000000000) {//makes MB if best
                            int diskUsedUnit = (int) (diskUsed / 1000000);
                            System.out.println("Disk has " + diskUsedUnit + "MB in use");
                        }
                        if (diskSize >= 1000000000) {//makes GB if best
                            int diskUsedUnit = (int) (diskUsed / 1000000000);
                            System.out.println("Disk has " + diskUsedUnit + "GB in use");
                        }
                        if (diskSize < 1000000) {//makes Bytes if nothing else applies
                            int diskUsedUnit = (int) diskUsed;
                            System.out.println("Disk has " + diskUsedUnit + "Bytes in use");
                        }

                        //section to get amount of disk free
                        double diskRemaining = diskSize - diskUsed;
                        if (diskRemaining >= 1000000 && diskRemaining < 1000000000) {//makes MB if best
                            int diskRemainingUnit = (int) (diskRemaining / 1000000);
                            System.out.println("Disk has " + diskRemainingUnit + "MB free");
                        }
                        if (diskSize >= 1000000000) {//makes GB if best
                            int diskRemainingUnit = (int) (diskRemaining / 1000000000);
                            System.out.println("Disk has " + diskRemainingUnit + "GB free");
                        }
                        if (diskSize < 1000000) {//makes Bytes if nothing else applies
                            int diskRemainingUnit = (int) diskRemaining;
                            System.out.println("Disk has " + diskRemainingUnit + "Bytes free");
                        }

                        //amount of disk free as %
                        double diskPercentInt = (int) ((diskUsed / diskSize) * 10000);
                        double diskPercent = diskPercentInt / 100;
                        System.out.println(diskPercent + "% of the disk's space is in use");

                    }
                        break;
                case 8: // finally made an exit method
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