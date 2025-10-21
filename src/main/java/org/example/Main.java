package org.example;
import oshi.SystemInfo; // sys info importing
import oshi.hardware.*;
import oshi.software.os.InternetProtocolStats;
import java.util.*; // for scanner


public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SystemInfo si = new SystemInfo();
        boolean isRunning = true;

        while (true) {


            System.out.println("===MENU===" +
                    " \n 1. Display SYSTEM INFO." +
                    " \n 2. Display TCPv4 Stats. " +
                    "\n 3. Display CPU Info " +
                    "\n 4. Hardware Info" +
                    "\n 5. USB Devices");
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
                    CentralProcessor processor = si.getHardware().getProcessor();

                    System.out.println("Logical processor count: " + processor.getLogicalProcessorCount());
                    System.out.println("Physical core count: " + processor.getPhysicalProcessorCount());
                    System.out.println("Physical package count: " + processor.getPhysicalPackageCount());
                    System.out.println("CPU utilization: " + Arrays.toString(processor.getSystemCpuLoadTicks()));
                    break;
                case 4: // placeholder

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
                default:
                    System.out.println("Invalid choice!");
            }
            isRunning = !isRunning;
            sc.nextLine();
        }
    }
}
