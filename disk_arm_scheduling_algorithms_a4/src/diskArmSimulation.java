import java.util.*;

public class diskArmSimulation {
    private static final int START_POSITION = 50;
    private static final int REQUEST_RANGE = 100;
    public static void main(String[] args) {
        int size;
        // Get the size from input if specified, otherwise 10
        if (args.length > 0) {
            size = Integer.parseInt(args[0]);
        } else {
            size = 10;
        }
        List<Integer> cylinderRequests = new ArrayList<>(size);
        // generate requests randomly in 0-99
        Random rand = new Random();
        for (int i = 0; i < size; i++) {
            cylinderRequests.add(rand.nextInt(REQUEST_RANGE));
        }
        System.out.println("The cylinder requests are: " + cylinderRequests + "\n");
        // the three different algorithms for scheduling the arm on a hard disk
        firstComeFirstServe(new ArrayList<>(cylinderRequests));
        shortestSeekFirst(new ArrayList<>(cylinderRequests));
        elevatorScan(new ArrayList<>(cylinderRequests));
    }

    private static void elevatorScan(List<Integer> cylinderRequests) {
        Collections.sort(cylinderRequests);
        System.out.println("Elevator (SCAN) Algorithm, assume the disk arm starts at cylinder/track 50.\n");
        System.out.println("The sorted cylinder requests are: " + cylinderRequests + "\n");
        int lastReq = START_POSITION, currentTrack = START_POSITION, totalMoved = 0;
        // moving up
        while(currentTrack < REQUEST_RANGE) {
            while (cylinderRequests.contains(currentTrack)) {
                int curReq = currentTrack;
                int curMoved = Math.abs(curReq - lastReq);
                System.out.println("Reading track " + curReq + ", distance moved " + curMoved + "\n");
                lastReq = curReq;
                totalMoved += curMoved;
                cylinderRequests.remove(Integer.valueOf(currentTrack));
            }
            currentTrack++;
        }
        int end = currentTrack - 1;      // at cylinder/track 99
        int Moved = Math.abs(end - lastReq);
        System.out.println("The disk arm reached cylinder/track 99, distance moved " + Moved + "\n");
        lastReq = end;
        totalMoved += Moved;
        // moving down
        while(currentTrack >= 0) {
            while (currentTrack < START_POSITION && cylinderRequests.contains(currentTrack)) {
                int curReq = currentTrack;
                int curMoved = Math.abs(curReq - lastReq);
                System.out.println("Reading track " + curReq + ", distance moved " + curMoved + "\n");
                lastReq = curReq;
                totalMoved += curMoved;
                cylinderRequests.remove(Integer.valueOf(currentTrack));
            }
            currentTrack--;
        }
        // report the total distance (number of cylinders) the disk arm had to traverse
        System.out.println("SCAN Total distance: " + totalMoved + "\n\n");
    }

    private static void shortestSeekFirst(List<Integer> cylinderRequests) {
        System.out.println("Shortest Seek First (SSF) Algorithm, assume the disk arm starts at cylinder/track 50.\n");
        int lastPos = START_POSITION, totalMoved = 0;
        while (!cylinderRequests.isEmpty()) {
            int shortestDistance = Integer.MAX_VALUE;
            int nextTrack = -1;
            // find the shortest track
            for (int track : cylinderRequests) {
                int distance = Math.abs(track - lastPos);
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    nextTrack = track;
                }
            }
            int curMoved = Math.abs(nextTrack - lastPos);
            System.out.println("Reading track " + nextTrack + ", distance moved " + curMoved + "\n");
            cylinderRequests.remove(Integer.valueOf(nextTrack));
            lastPos = nextTrack;
            totalMoved += curMoved;
        }
        // report the total distance (number of cylinders) the disk arm had to traverse
        System.out.println("SSF Total distance: " + totalMoved + "\n\n");
    }

    private static void firstComeFirstServe(List<Integer> cylinderRequests) {
        System.out.println("First Come-First Serve Algorithm, assume the disk arm starts at cylinder/track 50.\n");
        int lastPos = START_POSITION, totalMoved = 0;
        // the order in which the cylinders/tracks are read, and the total distance traveled
        for (int i = 0; i < cylinderRequests.size(); i++) {
            int curPos = cylinderRequests.get(i);
            int curMoved = Math.abs(curPos - lastPos);
            System.out.println("Reading track " + curPos + ", distance moved " + curMoved + "\n");
            lastPos = curPos;
            totalMoved += curMoved;
        }
        // report the total distance (number of cylinders) the disk arm had to traverse
        System.out.println("FCFS Total distance: " + totalMoved + "\n\n");
    }
}
