import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Process extends Thread{
    private static final int MAX_FRAMES_SIZE = 4;
    private static final int REFERENCE_INTERVAL = 100;
    private static final int INITIAL_PAGE_NUMBER = 0;
    private String processName;
    private int size;
    private long arrivalTime;
    private int duration;
    private int hit;
    private int miss;
    private String algo;
    private List<Integer> frames;    // the memory map of current process
    private Random random;
    private Set<String> memoryMapSet;
    private Statistician stat;
    private boolean printPageRef;

    public Process(String name, int size, long arrivalTime, int duration, String algo,
                   Set<String> memoryMapSet, Statistician stat, boolean printPageRef) {
        this.processName = name;
        this.size = size;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.algo = algo;
        this.memoryMapSet = memoryMapSet;
        this.frames = new ArrayList<>();
        this.random = new Random();
        this.stat = stat;
        this.printPageRef = printPageRef;
    }

    @Override
    public void run() {
        String currentTime = getCurrentTime();
        System.out.println("<" + currentTime + ", " + processName + ", Enter, Size in Pages: " + size
                + ", Service Duration: " + duration + ", Memory-map: "+ memoryMapSet + ">");
        // A process is swapped in (start running)
        stat.addTotalSwappedIn();
        switch (algo) {
            case "fifo":
                startPagingInFIFO();
                break;
            case "lru":
                startPagingInLRU();
                break;
            case "optimal":
                startPagingInOptimal();
                break;
            case "random":
                startPagingInRandom();
                break;
        }
        stat.addTotalHit(hit);
        stat.addTotalMiss(miss);
        // A process has completed (exit and therefore remove from memory)
        memoryMapSet.remove(processName);
        String currentTime2 = getCurrentTime();
        System.out.println("<" + currentTime2 + ", " + processName + ", Exit, Size in Pages: " + size
                + ", Service Duration: " + duration + ", Memory-map: "+ memoryMapSet + ">");
    }

    private String getCurrentTime() {
        // Get the current timestamp
        LocalDateTime currentTime = LocalDateTime.now();
        // Define the desired date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        // Format the timestamp using the formatter
        String formattedTime = currentTime.format(formatter);
        return formattedTime;
    }

    private void startPagingInFIFO() {
        int numberOfRefs = duration / REFERENCE_INTERVAL;
        int lastPage = INITIAL_PAGE_NUMBER;
        int fifoIdx = 0;
        for (int i = 0; i < numberOfRefs; i++) {
            int nextPage = getNextPage(lastPage);
            lastPage = nextPage;
            String currentTime = getCurrentTime();
            if (frames.contains(nextPage)) {   // page hit
                hit++;
                if (printPageRef) {
                    System.out.println("<" + currentTime + ", " + processName + ", page-referenced: " + nextPage
                            + ", if-Page-in-memory: Yes" + ", page number evicted: N/A" + ">");
                }
            } else {    // page miss
                miss++;
                if (frames.size() < MAX_FRAMES_SIZE) {
                    frames.add(nextPage);
                    if (printPageRef) {
                        System.out.println("<" + currentTime + ", " + processName + ", page-referenced: " + nextPage
                                + ", if-Page-in-memory: No" + ", page number evicted: N/A" + ">");
                    }
                } else {
                    // update frame in fifo index
                    if (printPageRef) {
                        System.out.println("<" + currentTime + ", " + processName + ", page-referenced: " + nextPage
                                + ", if-Page-in-memory: No" + ", page number evicted: " + frames.get(fifoIdx) + ">");
                    }
                    frames.set(fifoIdx, nextPage);
                    fifoIdx = (fifoIdx + 1) % MAX_FRAMES_SIZE;
                }
            }
            sleep100ms();
        }
    }

    private void startPagingInRandom() {
        int numberOfRefs = duration / REFERENCE_INTERVAL;
        int lastPage = INITIAL_PAGE_NUMBER;
        for (int i = 0; i < numberOfRefs; i++) {
            int nextPage = getNextPage(lastPage);
            lastPage = nextPage;
            String currentTime = getCurrentTime();
            if (frames.contains(nextPage)) {   // page hit
                hit++;
                if (printPageRef) {
                    System.out.println("<" + currentTime + ", " + processName + ", page-referenced: " + nextPage
                            + ", if-Page-in-memory: Yes" + ", page number evicted: N/A" + ">");
                }
            } else {      // page miss
                miss++;
                if (frames.size() < MAX_FRAMES_SIZE) {
                    frames.add(nextPage);
                    if (printPageRef) {
                        System.out.println("<" + currentTime + ", " + processName + ", page-referenced: " + nextPage
                                + ", if-Page-in-memory: No" + ", page number evicted: N/A" + ">");
                    }
                } else {
                    // update frame in random index
                    int randomIdx = random.nextInt(MAX_FRAMES_SIZE);
                    if (printPageRef) {
                        System.out.println("<" + currentTime + ", " + processName + ", page-referenced: " + nextPage
                                + ", if-Page-in-memory: No" + ", page number evicted: " + frames.get(randomIdx) + ">");
                    }
                    frames.set(randomIdx, nextPage);
                }
            }
            sleep100ms();
        }
    }
    private void startPagingInOptimal() {
        int numberOfRefs = duration / REFERENCE_INTERVAL;
        int lastPage = INITIAL_PAGE_NUMBER;
        List<Integer> futurePages = new ArrayList<>();
        for (int i = 0; i < numberOfRefs; i++) {
            int nextPage = getNextPage(lastPage);
            futurePages.add(nextPage);
            lastPage = nextPage;
        }
        for (int i = 0; i < numberOfRefs; i++) {
            int nextPage = futurePages.get(i);
            String currentTime = getCurrentTime();
            if (frames.contains(nextPage)) {   // page hit
                hit++;
                if (printPageRef) {
                    System.out.println("<" + currentTime + ", " + processName + ", page-referenced: " + nextPage
                            + ", if-Page-in-memory: Yes" + ", page number evicted: N/A" + ">");
                }
            } else {      // page miss
                miss++;
                if (frames.size() < MAX_FRAMES_SIZE) {
                    frames.add(nextPage);
                    if (printPageRef) {
                        System.out.println("<" + currentTime + ", " + processName + ", page-referenced: " + nextPage
                                + ", if-Page-in-memory: No" + ", page number evicted: N/A" + ">");
                    }
                } else {
                    // update frame in optimal manner
                    // clone the current frames
                    List<Integer> cloneFrames = new ArrayList<>(frames);
                    // find pages that will be used in the near future
                    int futurePageIdx = i + 1;
                    while (cloneFrames.size() > 1 && futurePageIdx < numberOfRefs) {
                        int cloneFrameIdx = cloneFrames.indexOf(futurePages.get(futurePageIdx));
                        // remove pages that will be used in the near future
                        if (cloneFrameIdx >= 0) {
                            cloneFrames.remove(cloneFrameIdx);
                        }
                        futurePageIdx++;
                    }
                    // the remaining pages in the cloneFrames, will not be used in the near future
                    // update the page
                    int updateIdx = frames.indexOf(cloneFrames.get(0));
                    if (printPageRef) {
                        System.out.println("<" + currentTime + ", " + processName + ", page-referenced: " + nextPage
                                + ", if-Page-in-memory: No" + ", page number evicted: " + frames.get(updateIdx) + ">");
                    }
                    frames.set(updateIdx, nextPage);
                }
            }
            sleep100ms();
        }
    }
    private void startPagingInLRU() {
        int numberOfRefs = duration / REFERENCE_INTERVAL;
        int lastPage = INITIAL_PAGE_NUMBER;
        List<Long> frameTimestamps = new ArrayList<>();
        for (int i = 0; i < numberOfRefs; i++) {
            int nextPage = getNextPage(lastPage);
            lastPage = nextPage;
            String currentTime = getCurrentTime();
            int idx = frames.indexOf(nextPage);
            long newTimestamp = System.currentTimeMillis();
            if (idx >= 0) {   // page hit
                frameTimestamps.set(idx, newTimestamp);    // update the timestamp
                hit++;
                if (printPageRef) {
                    System.out.println("<" + currentTime + ", " + processName + ", page-referenced: " + nextPage
                            + ", if-Page-in-memory: Yes" + ", page number evicted: N/A" + ">");
                }
            } else {         // page miss
                miss++;
                if (frames.size() < MAX_FRAMES_SIZE) {
                    frames.add(nextPage);               // add the new frame
                    frameTimestamps.add(newTimestamp);     // add the timestamp
                    if (printPageRef) {
                        System.out.println("<" + currentTime + ", " + processName + ", page-referenced: " + nextPage
                                + ", if-Page-in-memory: No" + ", page number evicted: N/A" + ">");
                    }
                } else {
                    // find the least recently used index by the timestamps
                    int updateIdx = frameTimestamps.indexOf(Collections.min(frameTimestamps));
                    if (printPageRef) {
                        System.out.println("<" + currentTime + ", " + processName + ", page-referenced: " + nextPage
                                + ", if-Page-in-memory: No" + ", page number evicted: " + frames.get(updateIdx) + ">");
                    }
                    frames.set(updateIdx, nextPage);
                    frameTimestamps.set(updateIdx, newTimestamp);
                }
            }
            sleep100ms();
        }
    }
    private int getNextPage(int lastPage) {
        int nextPage;
        // Generate a random number between 0 and 9 (inclusive)
        int randomNumber = random.nextInt(10);
        // Apply the rules based on the probabilities
        if (randomNumber < 7) {
            // 70% probability: n is lastPage or lastPage-1 or lastPage+1
            do {
                nextPage = random.nextInt(3) - 1 + lastPage;
            } while (nextPage < 0 || nextPage >= size);
        } else {
            // 30% probability: n is a number other than (lastPage or lastPage-1 or lastPage+1)
            do {
                nextPage = random.nextInt(size);
            } while (Math.abs(nextPage - lastPage) <= 1);
        }
        return nextPage;
    }

    private void sleep100ms() {
        // every 100 ms the process references another memory location
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public String getProcessName() {
        return processName;
    }
}
