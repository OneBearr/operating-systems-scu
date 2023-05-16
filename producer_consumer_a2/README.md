## COEN 283 Assignment 2 (Producer-Consumer Programming)

Introduction
-------------
One thread, the producer, will generate a sequence of integers and write them in order to 
successive buffer cells. Several consumer threads will then read these integers from the 
buffer and print them out in the order read. The is a circular buffer shared between the 
producer and consumers.

Tasks
------------------------------
To complete this project, create a single producer thread that produces a stream of integers 
and writes them into a circular buffer, and several consumer threads that read the integers 
from the buffer and print them.

The producer thread should produce integers until the buffer is full; then, it will wait 
until the consumers remove some entries from the buffer. The producer should continue to 
create integers until it reaches a maximum specified on the command line, then it should exit.

The consumer threads should consume integers from the buffer until it is empty, then wait 
for the producer to put more integers into the buffer. The consumers should continue to 
remove integers from the buffer until they have removed and printed all of them; then, 
they should exit.

Note that this project requires several shared variables and data structures: the buffer, 
input and output locations, and counts of integers produced and consumed.

How to run the producer-consumer program
------------------------------------------
1. Setup Java environment and go to the "src" folder
2. Compile all the Java files  
   `$ javac *.java`
3. Run the ProducerConsumerTest file with input parameters  
   `$ java ProducerConsumerTest.java <number of consumers> <number of integers>`

