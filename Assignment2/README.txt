COEN 283 Assignment 2

My Info
---------------------------
First Name: Xiongsheng
Last Name: Yi
SCU #: 1606976
Email: xyi@scu.edu

How to run the producer-consumer program
------------------------------------------
1. Setup java environment and go to the "src" folder
2. Compile all the java files
    $ javac *.java
3. Run the ProducerConsumerTest file with input parameters
    $ java ProducerConsumerTest.java <number of consumers> <number of integers>

Results
--------------------------
In what order were the integers printed?

Assumptions:
1. The max capacity of the circular buffer is 10.
2. The integers produced and consumed is from 1 to 20.
3. The number of consumers is 3.

Producer produced number 1 at index 0
Producer produced number 2 at index 1
Producer produced number 3 at index 2
Producer produced number 4 at index 3
Producer produced number 5 at index 4
Producer produced number 6 at index 5
Producer produced number 7 at index 6
Producer produced number 8 at index 7
Producer produced number 9 at index 8
Producer produced number 10 at index 9
Consumer3 consumed number 1 at index 0
Consumer2 consumed number 2 at index 1
Consumer1 consumed number 3 at index 2
Producer produced number 11 at index 0
Producer produced number 12 at index 1
Producer produced number 13 at index 2
Consumer3 consumed number 4 at index 3
Producer produced number 14 at index 3
Consumer2 consumed number 5 at index 4
Producer produced number 15 at index 4
Consumer1 consumed number 6 at index 5
Producer produced number 16 at index 5
Consumer2 consumed number 7 at index 6
Producer produced number 17 at index 6
Consumer3 consumed number 8 at index 7
Producer produced number 18 at index 7
Consumer1 consumed number 9 at index 8
Producer produced number 19 at index 8
Consumer2 consumed number 10 at index 9
Producer produced number 20 at index 9
Consumer3 consumed number 11 at index 0
Consumer1 consumed number 12 at index 1
Consumer3 consumed number 13 at index 2
Consumer2 consumed number 14 at index 3
Consumer3 consumed number 15 at index 4
Consumer1 consumed number 16 at index 5
Consumer2 consumed number 17 at index 6
Consumer1 consumed number 18 at index 7
Consumer2 consumed number 19 at index 8
Consumer3 consumed number 20 at index 9

Discussion
----------------------------------------
Assumptions:
1. The max capacity of the circular buffer is 10.
2. The integers produced and consumed is from 1 to 20.
3. The number of consumers is 3.

A. How many of each integer should you see printed?
    For each integer, we should see it was produced once and was consumed once. Since I have 20 integers, so we will
    see a total of 40 times printing.

B. In what order should you expect to see them printed? Why?
    The ordering could be random.
    But there are some rules:
    1. Any integer need to be produced first before it is consumed.
    2. The producer thread should produce integers until the buffer is full; then, it will wait until the consumers
    remove some entries from the buffer. The producer should continue to create integers until it reaches a maximum
    specified on the command line, then it should exit.
    3. The consumer threads should consume integers from the buffer until it is empty, then wait for the producer to
    put more integers into the buffer. The consumers should continue to remove integers from the buffer until they
    have removed and printed all of them; then, they should exit.

C. Did your results differ from your answers in (A) and (B)?
    The result is as same as I described in (A) and (B).
    Because:
    1. Total number of printings was 40. For each integer, it was produced once and was consumed once.
    2. Every integer was produced before it was consumed.
    3. The producer thread produced integers until the buffer was full; then, it waited until the consumers
    removed some integers from the buffer. The producer continued to create integers until it reached a maximum
    specified on the command line, then it exited.
    4. The consumer threads consumed integers from the buffer until it was empty, then waited for the producer to
    put more integers into the buffer. The consumers continued to remove integers from the buffer until they
    had removed and printed all of them; then they exited.