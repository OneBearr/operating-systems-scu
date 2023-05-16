public class ProducerConsumerTest {
    public static void main(String[] args) {

        int numConsumers = Integer.parseInt(args[0]);
        int numIntegers = Integer.parseInt(args[1]);

        IntegerBuffer integerBuffer = new IntegerBuffer(numIntegers);

        // Create and start the producer
        Thread producerThread = new Thread(new IntProducer(integerBuffer));
        producerThread.setName("Producer");
        producerThread.start();

        // Create and start multiple consumers
        for (int i = 1; i <= numConsumers; i++) {
            Thread thread = new Thread(new IntConsumer(integerBuffer));
            thread.setName("Consumer" + i);
            thread.start();
        }
    }
}
