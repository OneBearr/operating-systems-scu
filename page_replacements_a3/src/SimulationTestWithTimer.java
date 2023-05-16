import java.util.*;

public class SimulationTestWithTimer {
    private static final int MAX_MEMORY_SIZE = 100;
    private static final int MAX_FRAMES_SIZE = 4;
    private static final int MAX_PROCESSES_ALLOWED = MAX_MEMORY_SIZE / MAX_FRAMES_SIZE;
    private static final int NUM_PROCESSES = 150;
    private static final int TIMER_LIMIT = 60;      // in secs
    private static final int NUM_RUNS = 5;
    private static final int[] PROCESS_SIZES = {5, 11, 17, 31};
    private static final int[] PROCESS_DURATIONS = {1000, 2000, 3000, 4000, 5000};   // in ms
    private String algo = "fifo";    // default algorithm
    private List<Integer> swapsList = new ArrayList<>();
    private List<Double> ratiosList = new ArrayList<>();
    private boolean isTimerExpired = false;

    public void simulate(String algo, List<String> resultList) {
        for (int i = 1; i <= NUM_RUNS; i++) {
            System.out.println("For " + algo + ", run " + i + ":");
            singleSimulate(algo);
        }
        System.out.println("For " + algo
                + ", the number of processes successfully swapped-in for each run in 1 min: " + swapsList);
        System.out.println("For " + algo +", the hit/miss ratio for each run in 1 min: " + ratiosList);
        int swapSum = 0;
        double ratioSum = 0;
        for (int swap : swapsList) swapSum += swap;
        for (double ratio : ratiosList) ratioSum += ratio;
        String resultInfo1 = "For " + algo
                + ", the average of processes successfully swapped-in in 1 min is (over the 5 runs): "
                + swapSum / swapsList.size();
        System.out.println(resultInfo1);
        resultList.add(resultInfo1);
        String resultInfo2 = "For " +algo+ ", the average hit/miss ratio in 1 min is (over the 5 runs): "
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

        // create processes
        for (int i = 1; i <= NUM_PROCESSES; i++) {
            String name = "P" + i;
            int size = PROCESS_SIZES[rand.nextInt(PROCESS_SIZES.length)];
            long arrivalTime = System.currentTimeMillis();
            int duration = PROCESS_DURATIONS[rand.nextInt(PROCESS_DURATIONS.length)];
            processList.add(new Process(name, size, arrivalTime, duration, algo, memoryMapSet, stat, false));
        }
        System.out.println("All " + processList.size() + " processes in " + algo + " algorithm are created.");

        // Processes are sorted according to the arrival time
        processList.sort((p1, p2) -> Math.toIntExact(p1.getArrivalTime() - p2.getArrivalTime()));

        // Schedule a timer in limit amount of time for processes running
        isTimerExpired = false;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                isTimerExpired = true;
                System.out.println("Time is up. Terminating all processes...");
                printStatResult(stat);
                timer.cancel();
            }
        }, TIMER_LIMIT * 1000); // timer in ms

        // start running processes
        System.out.println("Processes start running...");
        for (Process process : processList) {
            if (isTimerExpired) break;
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
            if (isTimerExpired) break;
            try {
                process.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (isTimerExpired) return;
        System.out.println("All processes have finished execution.\n");
        printStatResult(stat);
        timer.cancel();
    }

    private void printStatResult(Statistician stat) {
        int totalHit = stat.getTotalHit();
        int totalMiss = stat.getTotalMiss();
        double ratio = (double) totalHit / totalMiss;
        int swappedIn = stat.getTotalSwappedIn();
        System.out.println("For " + algo +", processes total hit is " + totalHit
                + ", total miss is " + totalMiss + ", hit/miss ratio is " + ratio);
        System.out.println("For " + algo +", processes total swapped-in is " + swappedIn);
        System.out.println();
        swapsList.add(swappedIn);
        ratiosList.add(ratio);
    }
}
