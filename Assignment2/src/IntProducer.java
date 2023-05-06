public class IntProducer implements Runnable{
    private IntegerBuffer integerBuffer;
    public IntProducer(IntegerBuffer integerBuffer) {
        this.integerBuffer = integerBuffer;
    }
    @Override
    public void run() {
        // Produce integers
        while (true) {
            synchronized (integerBuffer) {
                if (integerBuffer.getSize() > 9) {      // max size of the buffer is 10
                    try {
                        // Already reached the max size of buffer, producer need to wait and release lock
                        integerBuffer.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                // produce a number
                boolean success = integerBuffer.produceNumber();
                // notify other waiting threads
                integerBuffer.notifyAll();
                // If reached the max number of producing, break the loop
                if (!success) break;
            }
        }
    }
}
