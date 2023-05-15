public class Statistician {
    private int totalHit;
    private int totalMiss;

    public int getTotalHit() {
        return totalHit;
    }

    public void addTotalHit(int hit) {
        this.totalHit += hit;
    }

    public int getTotalMiss() {
        return totalMiss;
    }

    public void addTotalMiss(int miss) {
        this.totalMiss += miss;
    }
}
