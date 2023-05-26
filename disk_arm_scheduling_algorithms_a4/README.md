## COEN 283 Assignment 4 (Disk-Arm Scheduling Algorithms)

Architecture
------------------------------------------
- disk_arm_scheduling_algorithms
    - src
        - diskArmSimulation.java
    - output.txt
    - README.md

1. `diskArmSimulation.java` is to simulate the motion of the disk arm by taking a set of requests for cylinders/tracks 
to read and then determining the order in which they will be read


How to run the program
------------------------------------------
1. Setup Java environment and go to the "src" directory
2. Compile all the Java files  
   `$ javac *.java`
3. Run the Simulator  
   `$ java diskArmSimulation.java`

Introduction
-------------
In this assignment, write a program that will implement three different algorithms for scheduling the arm 
on a hard disk. That is, it will simulate the motion of the disk arm by taking a set of requests for cylinders/tracks 
to read and then determining the order in which they will be read. Then report the total distance 
(number of cylinders) the disk arm had to traverse in the three algorithms using the same set of random numbers.

Workload Generation
------------------------------
Write a program that implements the following disk scheduling algorithms: First Come-First Serve (FCFS), 
Shortest Seek First (SSF) and the Elevator (SCAN) algorithms. 

The requests can be generated randomly or read from the standard input. The requests should be integer numbers 
between 0 and 99. Assume the disk arm starts at cylinder/track 50. For each algorithm, the output of the program 
should show the order in which the cylinders/tracks are read, and the total distance traveled.

Example: Suppose the 10 random unique numbers read from the stdin are Cylinder/Track 5 28 10 7 39 20 45 67 36 35

For First Come-First Serve (FCFS), the program output will look like this:

First Come-First Serve Algorithm

Reading track 05      distance moved …

Reading track 28       distance moved …

Reading track 10       distance moved …

Reading track 07       distance moved …

Reading track 39       distance moved …

Reading track 20       distance moved …

Reading track 45       distance moved …

Reading track 67       distance moved …

Reading track 36       distance moved …

Reading track 35       distance moved …

FCFS Total distance: 219

Notes:

The SSF algorithm looks for the track closest to where the disk arm currently is positioned, not to where it started. 
In the case where two tracks are both equally as close to the current position, choose either one (the higher 
one or the lower one), as long as it's a "local" decision (i.e., do not consider what will do after that).

In the Elevator disk scheduling algorithm (sometimes called the LOOK algorithm), choose whether the disk arm 
is moving up or is moving down at the start of the program.