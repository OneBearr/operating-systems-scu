public class Statistician {
    private int totalHit;
    private int totalMiss;
    private int totalSwappedIn;

    public synchronized int getTotalHit() {
        return totalHit;
    }

    public synchronized void addTotalHit(int hit) {
        this.totalHit += hit;
    }

    public synchronized int getTotalMiss() {
        return totalMiss;
    }

    public synchronized void addTotalMiss(int miss) {
        this.totalMiss += miss;
    }

    public synchronized int getTotalSwappedIn() {
        return totalSwappedIn;
    }

    public synchronized void addTotalSwappedIn() {
        this.totalSwappedIn += 1;
    }
}
