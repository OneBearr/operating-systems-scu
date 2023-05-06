import java.util.concurrent.ThreadLocalRandom;

public class IntegerBuffer {
    private int[] buffer;
    private int wrtIdx;
    private int rdIdx;
    private int size;
    private int producedNum;
    private int consumedNum;
    private int maxNum;
    public IntegerBuffer() {
        this.buffer = new int[10];
        this.producedNum = 1;
        this.maxNum = 20;
    }
    public IntegerBuffer(int maxNum) {
        this.buffer = new int[10];
        this.producedNum = 1;
        this.maxNum = maxNum;
    }
    public boolean produceNumber() {
        if (producedNum <= maxNum) {
            buffer[wrtIdx] = producedNum;
            System.out.println(Thread.currentThread().getName() + " produced number " + buffer[wrtIdx] + " at index " + wrtIdx);
            wrtIdx = (wrtIdx + 1) % 10;
            producedNum++;
            size++;
            return true;
        }
        return false;
    }
    public boolean consumeNumber() {
        if (this.size > 0) {
            System.out.println(Thread.currentThread().getName() + " consumed number " + buffer[rdIdx] + " at index " + rdIdx);
            buffer[rdIdx] = 0;
            rdIdx = (rdIdx + 1) % 10;
            consumedNum++;
            size--;
            return true;
        }
        return false;
    }
    public boolean hasMoreNumber() {    // if there are more numbers will be produced and consumed
        return this.consumedNum < this.maxNum;
    }
    public int getSize() {
        return this.size;
    }
}
