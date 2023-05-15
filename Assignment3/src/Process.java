import java.util.*;
import java.util.concurrent.Semaphore;

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
    private Semaphore pageSemaphore;
    private Statistician stat;

    public Process(String name, int size, long arrivalTime, int duration, String algo,
                   Semaphore pageSemaphore, Statistician stat) {
        this.processName = name;
        this.size = size;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.algo = algo;
        this.pageSemaphore = pageSemaphore;
        this.frames = new ArrayList<>();
        this.random = new Random();
        this.stat = stat;
    }

    @Override
    public void run() {
        try {
            System.out.println("Process " + processName + " is waiting for pages.");
            pageSemaphore.acquire(MAX_FRAMES_SIZE);
            System.out.println("Process " + processName + " has acquired "
                    + MAX_FRAMES_SIZE + " pages and start running " + duration + " ms" + " in size " + size);
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
            synchronized (stat) {
                stat.addTotalHit(hit);
                stat.addTotalMiss(miss);
            }
            System.out.println("Process " + processName + " has released "
                    + MAX_FRAMES_SIZE + " pages and finished running.");
            pageSemaphore.release(MAX_FRAMES_SIZE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startPagingInFIFO() {
        System.out.println("start paging in fifo.");
        int numberOfRefs = duration / REFERENCE_INTERVAL;
        int lastPage = INITIAL_PAGE_NUMBER;
        int fifoIdx = 0;
        for (int i = 0; i < numberOfRefs; i++) {
            int nextPage = getNextPage(lastPage);
            lastPage = nextPage;
//            System.out.println("Start page referencing " + i);
            if (frames.contains(nextPage)) {   // page hit
                hit++;
                System.out.println(processName + " page hit, nextPage is " + nextPage);
            } else {                            // page miss
                miss++;
                if (frames.size() < MAX_FRAMES_SIZE) {
                    frames.add(nextPage);
                    System.out.println(processName + " page miss, added page " + nextPage);
                    System.out.println("New frames: " + frames);
                } else {
                    System.out.println("Old frames: " + frames);
                    // update frame in fifo index
                    frames.set(fifoIdx, nextPage);
                    fifoIdx = (fifoIdx + 1) % MAX_FRAMES_SIZE;
                    System.out.println(processName + " page missed, fifo update, nextPage is " + nextPage);
                    System.out.println("New frames: " + frames);
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void startPagingInRandom() {
        System.out.println("start paging in random.");
        int numberOfRefs = duration / REFERENCE_INTERVAL;
        int lastPage = INITIAL_PAGE_NUMBER;
        for (int i = 0; i < numberOfRefs; i++) {
            int nextPage = getNextPage(lastPage);
            lastPage = nextPage;
            if (frames.contains(nextPage)) {   // page hit
                hit++;
                System.out.println(processName + " page hit, nextPage is " + nextPage);
            } else {                            // page miss
                miss++;
                if (frames.size() < MAX_FRAMES_SIZE) {
                    frames.add(nextPage);
                    System.out.println(processName + " page miss, added page " + nextPage);
                    System.out.println("New frames: " + frames);
                } else {
                    System.out.println("Old frames: " + frames);
                    // update frame in random index
                    frames.set(random.nextInt(MAX_FRAMES_SIZE), nextPage);
                    System.out.println(processName + " page missed, fifo update, nextPage is " + nextPage);
                    System.out.println("New frames: " + frames);
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void startPagingInOptimal() {
        System.out.println("start paging in optimal.");
        int numberOfRefs = duration / REFERENCE_INTERVAL;
        int lastPage = INITIAL_PAGE_NUMBER;
        List<Integer> futurePages = new ArrayList<>();
        for (int i = 0; i < numberOfRefs; i++) {
            int nextPage = getNextPage(lastPage);
            futurePages.add(nextPage);
            lastPage = nextPage;
        }
        System.out.println("future page queue is : " + futurePages);
        System.out.println("future page queue size is : " + futurePages.size());
        for (int i = 0; i < numberOfRefs; i++) {
            int nextPage = futurePages.get(i);
            if (frames.contains(nextPage)) {   // page hit
                hit++;
                System.out.println(processName + " page hit, nextPage is " + nextPage);
            } else {                            // page miss
                miss++;
                if (frames.size() < MAX_FRAMES_SIZE) {
                    frames.add(nextPage);
                    System.out.println(processName + " page miss, added page " + nextPage);
                    System.out.println("New frames: " + frames);
                } else {
                    System.out.println("Old frames: " + frames);
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
                    frames.set(updateIdx, nextPage);
                    System.out.println(processName + " page missed, fifo update, nextPage is " + nextPage);
                    System.out.println("New frames: " + frames);
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void startPagingInLRU() {
        System.out.println("start paging in lru.");
        int numberOfRefs = duration / REFERENCE_INTERVAL;
        int lastPage = INITIAL_PAGE_NUMBER;
        List<Long> frameTimestamps = new ArrayList<>();
        for (int i = 0; i < numberOfRefs; i++) {
            int nextPage = getNextPage(lastPage);
            lastPage = nextPage;

            int idx = frames.indexOf(nextPage);
            long newTimestamp = System.currentTimeMillis();
            if (idx >= 0) {   // page hit
                frameTimestamps.set(idx, newTimestamp);    // update the timestamp
                hit++;
                System.out.println(processName + " page hit, nextPage is " + nextPage);
            } else {         // page miss
                miss++;
                if (frames.size() < MAX_FRAMES_SIZE) {
                    frames.add(nextPage);               // add the new frame
                    frameTimestamps.add(newTimestamp);     // add the timestamp
                    System.out.println(processName + " page miss, added page " + nextPage);
                    System.out.println("New frames: " + frames);
                } else {
                    System.out.println("Old frames: " + frames);
                    // find the least recently used index by the timestamps
                    int updateIdx = frameTimestamps.indexOf(Collections.min(frameTimestamps));
                    frames.set(updateIdx, nextPage);
                    frameTimestamps.set(updateIdx, newTimestamp);
                    System.out.println(processName + " page missed, fifo update, nextPage is " + nextPage);
                    System.out.println("New frames: " + frames);
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void updatePageMapByRandom(int nextPage) {
        System.out.println(processName + " page missed, random update, nextPage is " + nextPage);
    }

    private void updatePageMapByOptimal(int nextPage) {
        System.out.println(processName + " page missed, optimal update, nextPage is " + nextPage);

    }

    private void updatePageMapByLRU(int nextPage) {
        System.out.println(processName + " page missed, lru update, nextPage is " + nextPage);

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
//    private boolean inPageMap(int nextPage) {
//        for (int p : pageMap) {
//            if (p == nextPage) return true;
//        }
//        return false;
//    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String name) {
        this.processName = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Semaphore getPageSemaphore() {
        return pageSemaphore;
    }

    public void setPageSemaphore(Semaphore pageSemaphore) {
        this.pageSemaphore = pageSemaphore;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}
