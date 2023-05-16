import java.util.*;

public class Simulator {
    public static void main(String[] args) {
        String[] algos = {"fifo", "lru", "optimal", "random"};
        List<String> resultList = new ArrayList<>();
        resultList.add("------------------ OVERALL RESULTS ------------------\n");

        System.out.println("Start running simulations for 1 min timer.\n");
        resultList.add("Simulation Results for the 1 Min Timer:");
        for (String algo : algos) {
            SimulationTestWithTimer simulationTestWithTimer = new SimulationTestWithTimer();
            simulationTestWithTimer.simulate(algo, resultList);
        }
        System.out.println("Finished all simulations for timer.\n");

        System.out.println("Start running simulations for 100 page references.\n");
        resultList.add("\nSimulation Results for the 100 Page References:");
        for (String algo : algos) {
            SimulationTestWithLimitRefs simulationTestWithLimitRefs = new SimulationTestWithLimitRefs();
            simulationTestWithLimitRefs.simulate(algo, resultList);
        }
        System.out.println("Finished all simulations for 100 page references.\n");

        for (String resultInfo : resultList) {
            System.out.println(resultInfo);
        }
    }
}
