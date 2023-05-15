import java.util.*;
import java.util.concurrent.Semaphore;

public class SimulationTest {
    private static final int MAX_MEMORY_SIZE = 100;
    private static final int NUM_PROCESSES = 150;
    private static final int PAGES_PER_PROCESS = 4;
    private static String algo = "random";

    public static void main(String[] args) {
//        if (args.length != 0) {
//            algo = args[0];
//            if (!"fifo".equals(algo) && !"lru".equals(algo)
//                    && !"optimal".equals(algo) && !"random".equals(algo)) {
//                throw new RuntimeException("Invalid algorithm type.");
//            }
//        }
        // calculate total hits and misses
        Statistician stat = new Statistician();
        // 100 semaphores for 100MB main memory, each process takes 4 semaphores to run
        Semaphore memorySemaphores = new Semaphore(MAX_MEMORY_SIZE, true);
        List<Process> processList = new LinkedList<>();
        Random random = new Random();

        int[] sizes = {5, 11, 17, 31};
        int[] durations = {1000, 2000, 3000, 4000, 5000};   // in ms

        // create processes
        for (int i = 1; i <= NUM_PROCESSES; i++) {
            String name = "P" + i;
            int size = sizes[random.nextInt(sizes.length)];
            long arrivalTime = System.currentTimeMillis();
            int duration = durations[random.nextInt(durations.length)];
            processList.add(new Process(name, size, arrivalTime, duration, algo, memorySemaphores, stat));
        }
        System.out.println("All processes are created.");

        // Processes are sorted according to the arrival time
        processList.sort((p1, p2) -> Math.toIntExact(p1.getArrivalTime() - p2.getArrivalTime()));

        // start running processes
        for (Process process : processList) {
            process.start();
            // Keep those print statements in order
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // wait all processes finished
        for (Process process : processList) {
            try {
                process.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        printStatResult(stat);
        System.out.println("All processes have finished execution.");
        System.exit(0); // Terminate the Java Virtual Machine
    }
    private static void printStatResult(Statistician stat) {
        int totalHit = stat.getTotalHit();
        int totalMiss = stat.getTotalMiss();
        double ratio = (double) totalHit / (totalHit + totalMiss);
        double ratio2 = (double) totalHit / totalMiss;
        System.out.println("For " + algo +", processes total hit is " + totalHit
                + ", total miss is " + totalMiss + ", hit/(hit+miss) ratio is " + ratio);
        System.out.println("For " + algo +", processes total hit is " + totalHit
                + ", total miss is " + totalMiss + ", hit/miss ratio is " + ratio2);
    }
}
