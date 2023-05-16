import java.util.*;

public class SimulationTestWithLimitRefs {
    private static final int MAX_MEMORY_SIZE = 100;
    private static final int MAX_FRAMES_SIZE = 4;
    private static final int MAX_PROCESSES_ALLOWED = MAX_MEMORY_SIZE / MAX_FRAMES_SIZE;
    private static final int NUM_PROCESSES = 150;
    private static final int REFERENCE_LIMIT = 100;
    private static final int NUM_RUNS = 5;
    private static final int[] PROCESS_SIZES = {5, 11, 17, 31};
    private static final int[] PROCESS_DURATIONS = {1000, 2000, 3000, 4000, 5000};   // in ms
    private static String algo = "fifo";    // default algorithm
    private List<Integer> swapsList = new ArrayList<>();
    private List<Double> ratiosList = new ArrayList<>();

    public void simulate(String algo, List<String> resultList) {
        for (int i = 1; i <= NUM_RUNS; i++) {
            System.out.println("For " + algo + ", run " + i + ":");
            singleSimulate(algo);
        }
        System.out.println("For " + algo
                + ", the number of processes successfully swapped-in for each run in 100 page refs: " + swapsList);
        System.out.println("For " + algo +", the hit/miss ratio for each run in 100 page refs: " + ratiosList);
        int swapSum = 0;
        double ratioSum = 0;
        for (int swap : swapsList) swapSum += swap;
        for (double ratio : ratiosList) ratioSum += ratio;
        String resultInfo1 = "For " + algo
                + ", the average of processes successfully swapped-in in 100 page refs (over the 5 runs): "
                + swapSum / swapsList.size();
        System.out.println(resultInfo1);
        resultList.add(resultInfo1);
        String resultInfo2 = "For " + algo +", the average hit/miss ratio in 100 page refs (over the 5 runs): "
                + ratioSum / ratiosList.size();
        System.out.println(resultInfo2);
        resultList.add(resultInfo2);
        System.out.println();
    }

    private void singleSimulate(String algo) {
        if ("fifo".equals(algo) || "lru".equals(algo) ||
                "optimal".equals(algo) || "random".equals(algo)) {
            this.algo = algo;
        }
        // calculate total hits and misses
        Statistician stat = new Statistician();
        // only the number of MAX_PROCESSES_ALLOWED processes allowed in the memory set
        Set<String> memoryMapSet = Collections.synchronizedSet(new LinkedHashSet<>(MAX_PROCESSES_ALLOWED));

        List<Process> processList = new LinkedList<>();
        Random rand = new Random();

        int refsLeft = REFERENCE_LIMIT;
        // create processes
        for (int i = 1; i <= NUM_PROCESSES && refsLeft > 0; i++) {
            String name = "P" + i;
            int size = PROCESS_SIZES[rand.nextInt(PROCESS_SIZES.length)];
            long arrivalTime = System.currentTimeMillis();
            int duration = PROCESS_DURATIONS[rand.nextInt(PROCESS_DURATIONS.length)];
            duration = Math.min(duration, refsLeft * 100);
            processList.add(new Process(name, size, arrivalTime, duration, algo, memoryMapSet, stat, true));
            refsLeft -= duration / 100;
        }
        System.out.println("All " + processList.size() + " processes in " + algo + " algorithm are created.");

        // Processes are sorted according to the arrival time
        processList.sort((p1, p2) -> Math.toIntExact(p1.getArrivalTime() - p2.getArrivalTime()));

        // start running processes
        System.out.println("Processes start running...");
        for (Process process : processList) {
            // only the number of MAX_PROCESSES_ALLOWED processes is allowed to enter the memory
            while (memoryMapSet.size() >= MAX_PROCESSES_ALLOWED) {}
            memoryMapSet.add(process.getProcessName());
            process.start();
            // simulate context-switching or context-preparing for each process
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
        System.out.println("All processes have finished execution.\n");
        printStatResult(stat);
    }

    private void printStatResult(Statistician stat) {
        int totalHit = stat.getTotalHit();
        int totalMiss = stat.getTotalMiss();
        double ratio = (double) totalHit / totalMiss;
        int swappedIn = stat.getTotalSwappedIn();
        System.out.println("For " + algo +", processes total hit is " + totalHit
                + ", total miss is " + totalMiss + ", hit/miss ratio is " + ratio);
        System.out.println("For " + algo +", processes total swapped-in is " + stat.getTotalSwappedIn());
        System.out.println();
        swapsList.add(swappedIn);
        ratiosList.add(ratio);
    }
}
