package dev.mjuarez.challenge;

import dev.mjuarez.util.CalculationsUtil;
import dev.mjuarez.util.CartesianPoint;
import dev.mjuarez.util.Solution;
import dev.mjuarez.util.Load;
import dev.mjuarez.util.Route;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MainApplication {
    final public static long MAX_ALLOWED_RUNTIME_MS = 25_000;
    final public static int DEPOT_ID = 0;

    private static List<Solution> solutions = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        Load[] loads = getLoadsFromFile(args);

        long startTime = System.currentTimeMillis();
        long iterations = 0;

        // First, run a baseline without simulated annealing or priority loads
        AdjacencyMatrixGraph matrixGraph = createAdjacencyMatrixGraph(loads, false, false);
        Solution solution = matrixGraph.findSolution();
        solutions.add(solution);

        // Now, go ahead and run as many combinations as possible, using simulated annealing, with 50% of priority loads
        while (true) {
            matrixGraph = createAdjacencyMatrixGraph(loads, true, iterations++ % 2 == 0);
            solution = matrixGraph.findSolution();
            solutions.add(solution);

            if (System.currentTimeMillis() - startTime > MAX_ALLOWED_RUNTIME_MS) {
                // We will be penalized if the program takes longer than 25 seconds, so stop here
                break;
            }
        }
        printFinalOutput();
    }

    private static AdjacencyMatrixGraph createAdjacencyMatrixGraph(Load[] loads, boolean simulatedAnnealing, boolean priorityLoads) {
        // Number of total nodes will be loads * 2, plus 1 for Origin
        int totalNodes = loads.length * 2 + 1;

        AdjacencyMatrixGraph matrixGraph = new AdjacencyMatrixGraph(totalNodes, simulatedAnnealing);
        List<Integer> initialNodes = new ArrayList<>();
        List<Integer> terminalNodes = new ArrayList<>();

        // First, add the edges for each of the loads
        for (Load load : loads) {
            // This needs to return 1, 2 for first load, 3, 4 for second load, etc.
            int secondId = load.getId() * 2;
            int firstId = secondId - 1;

            matrixGraph.addEdge(DEPOT_ID, firstId, CalculationsUtil.calculateTimeInMinutes(CartesianPoint.ORIGIN, load.p1()));
            matrixGraph.addEdge(firstId, secondId, CalculationsUtil.calculateTimeInMinutes(load.p1(), load.p2()));
            matrixGraph.addEdge(secondId, DEPOT_ID, CalculationsUtil.calculateTimeInMinutes(load.p2(), CartesianPoint.ORIGIN));

            initialNodes.add(firstId);
            terminalNodes.add(secondId);
        }

        // Now, for every terminal node (even ones), add an edge to every other initial node that is not it's own counterpart
        for (int terminalNode : terminalNodes) {
            for (int initialNode : initialNodes) {
                if (initialNode != terminalNode - 1) {
                    // Loads are zero-based, always substract 1 from the index
                    Load initialLoad = loads[(int) ((initialNode + 1) / 2) - 1];
                    Load terminalLoad = loads[(int) (terminalNode / 2) - 1];
                    matrixGraph.addEdge(terminalNode, initialNode, CalculationsUtil.calculateTimeInMinutes(terminalLoad.p2(), initialLoad.p1()));
                }
            }
        }

        // Optionally, add the loads so they're available for optimization inside the matrixGraph
        if (priorityLoads) {
            matrixGraph.addLoads(loads);
        }

        return matrixGraph;
    }

    /**
     * This outputs the routes by driver, according to the instructions, something like this:
     * [1]
     * [4,2]
     * [3]
     * The above means there's 3 drivers, first one does load 1, second one does load 4 followed by 2,
     * and 3rd one only does load 3.
     */
    private static void printFinalOutput() {
        double minimumCost = Double.MAX_VALUE;
        Solution minimumSolution = null;

        for (Solution solution : solutions) {
            if (solution.getTotalCost() < minimumCost) {
                minimumCost = solution.getTotalCost();
                minimumSolution = solution;
            }
        }

        // Now that we know which one has the lowest cost, go ahead and display that
        if (minimumSolution != null) {
            for (Route route : minimumSolution.getRoutes()) {
                List<Integer> finalRoute = new ArrayList<>();
                for (Integer node : route.getPath()) {
                    if (node == 0) {
                        continue;
                    }
                    int load = getLoadIdFromNode(node);
                    if (!finalRoute.contains(load)) {
                        finalRoute.add(load);
                    }
                }
                System.out.println(finalRoute.toString().replaceAll(" ", ""));
            }
        }
    }

    public static int getLoadIdFromNode(int node) {
        return ((int) ((node + 1) / 2));
    }

    private static Load[] getLoadsFromFile(String[] args) throws IOException {
        String path = args[0];
        Path loadFile = Paths.get(path);

        // Check that the file exists, is readable, and is not a directory
        File file = new File(path);
        if (!file.exists() || !file.canRead() || file.isDirectory()) {
            System.out.println(String.format("Cannot load file [%s]", path));
            System.exit(1);
        }

        // Going to assume these files are relatively small, so we can load the whole thing at once.
        List<String> lines = Files.readAllLines(loadFile, StandardCharsets.UTF_8);

        Load[] loads = new Load[lines.size() - 1];

        int counter = 0;
        for (String line : lines) {
            // Skip lines that are not actual data.
            if (line.startsWith("loadNumber") || line.isBlank()) {
                continue;
            }
            loads[counter++] = CalculationsUtil.parseLoadFromInputLine(line);
        }
        return loads;
    }
}
