package org.example;
import oshi.SystemInfo; // sys info importing
import oshi.hardware.CentralProcessor;
import oshi.software.os.InternetProtocolStats;
import java.util.*; // for scanner

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SystemInfo si = new SystemInfo();
        System.out.println("===MENU=== \n 1. Display OS. \n 2. Display TCPv4 Stats. \n 3. Display CPU Info");
        switch (sc.nextInt()) {
            case 1:
                System.out.println("OS: " + si.getOperatingSystem());
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
                System.out.println("placeholder");
            default:
                System.out.println("Invalid choice!");
        }
    }
}

