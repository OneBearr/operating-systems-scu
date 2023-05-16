public class IntConsumer implements Runnable{
    private IntegerBuffer integerBuffer;
    public IntConsumer(IntegerBuffer integerBuffer) {
        this.integerBuffer = integerBuffer;
    }
    @Override
    public void run() {
        // Consume integers
        while (true) {
            synchronized (integerBuffer) {
                // if current size of buffer is 0 and will have more numbers incoming
                if (integerBuffer.getSize() == 0 && integerBuffer.hasMoreNumber()) {
                    try {
                        // The consumer needs to wait and release the lock
                        integerBuffer.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                boolean success = integerBuffer.consumeNumber();
                // notify other waiting threads
                integerBuffer.notifyAll();
                // If reached the max number of consuming, break the loop
                if (!success) break;
            }
            // It's optional here, I just don't want the same consumer got the buffer again right after
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
