package dev.mjuarez.challenge;

import dev.mjuarez.util.CalculationsUtil;
import dev.mjuarez.util.Load;
import dev.mjuarez.util.Route;
import dev.mjuarez.util.Solution;

import java.util.*;

import static dev.mjuarez.challenge.MainApplication.DEPOT_ID;

public class AdjacencyMatrixGraph {
    private int numOfNodes;
    private double[][] matrix;
    private boolean[][] isSet;

    private List<Integer> bestPath = new ArrayList<>();

    private double minCost = Double.MAX_VALUE;
    private boolean pathFound = Boolean.FALSE;

    private Set<Integer> solved = new HashSet<>();
    private Set<Integer> unsolved = new HashSet<>();
    private Set<Integer> priority = new HashSet<>();

    private final double INITIAL_TEMPERATURE = 1.00f;
    private final double FINAL_TEMPERATURE = 0.01f;
    private double temperature = INITIAL_TEMPERATURE;
    private final boolean enableSimulatedAnnealing;

    public AdjacencyMatrixGraph(int numOfNodes, boolean enableSimulatedAnnealing) {
        this.numOfNodes = numOfNodes;

        // Simply initializes our adjacency matrix to the appropriate size, this should include origin
        this.matrix = new double[numOfNodes][numOfNodes];
        this.isSet = new boolean[numOfNodes][numOfNodes];
        this.enableSimulatedAnnealing = enableSimulatedAnnealing;

        // Initialize the unsolved with everything, except the DEPOT node
        for (int i = 0; i < numOfNodes; i++) {
            unsolved.add(i);
        }
    }

    /**
     * Main method that iterates until it finds a solution for the problem
     */
    public Solution findSolution() {
        List<Route> routes = new ArrayList<>();

        while (true) {
            // Reset these three in between each iteration
            minCost = Double.MAX_VALUE;
            pathFound = Boolean.FALSE;
            bestPath = new ArrayList<>();

            List<Integer> currentPath = new ArrayList<>();

            // Start the currentPath by starting at origin
            currentPath.add(DEPOT_ID);

            // Go recursive search in here, using nearest neighbor with prioritized loads
            recursiveNearestNeighborSearch(DEPOT_ID, new HashSet<>(), DEPOT_ID, currentPath);

            // Now, mark non-reachable anything that was solved by the previous.
            markNonReachable(bestPath);
            setSolvedAndUnsolved(bestPath);

            // Now go ahead and save this, along with the cost
            routes.add(new Route(bestPath, minCost));

            // If there are no unsolved nodes any more, we're done!
            if (unsolved.isEmpty()) {
                // Now, we have all the routes, with their cost.  Apply the formula.
                double totalCost = CalculationsUtil.calculateTotalCost(routes.size(), routes.stream().mapToDouble(Route::getCost).sum());
                return new Solution(routes, totalCost);
            }
        }
    }

    public void addEdge(int source, int destination, double weight) {
        double valueToAdd = weight;

        matrix[source][destination] = valueToAdd;
        isSet[source][destination] = true;
    }

    public void printMatrix() {
        System.out.println("\n");
        for (int i = 0; i < numOfNodes; i++) {
            for (int j = 0; j < numOfNodes; j++) {
                if (isSet[i][j]) {
                    System.out.format("%8.2f", matrix[i][j]);
                } else {
                    System.out.format("%8s", "X  ");
                }
            }
            System.out.println();
        }
    }

    public void printEdges() {
        for (int i = 0; i < numOfNodes; i++) {
            System.out.print("Node " + i + " is connected to: ");
            for (int j = 0; j < numOfNodes; j++) {
                if (isSet[i][j]) {
                    System.out.print(j + " ");
                }
            }
            System.out.println();
        }
    }

    private void setSolvedAndUnsolved(List<Integer> bestPath) {
        solved.addAll(bestPath);
        unsolved.removeAll(bestPath);
    }

    private void markNonReachable(List<Integer> bestPath) {
        for (Integer node : bestPath) {
            if (node == DEPOT_ID) {
                continue;
            }
            for (int i = 0; i < numOfNodes; i++) {
                matrix[i][node] = 0;
                isSet[i][node] = false;
            }
        }
        // Special for DEPOT
        for (Integer i : bestPath) {
            matrix[i][DEPOT_ID] = 0;
            isSet[i][DEPOT_ID] = false;
        }
    }

    public void recursiveNearestNeighborSearch(int current, Set<Integer> visited, double currentCost, List<Integer> currentPath) {
        // Handle the case where we have exceeded the max drive time, go back
        // Second case, where there are no more nodes to go to, handled below
        if (pathFound || currentCost > CalculationsUtil.MAX_DRIVE_TIME_MINUTES) {
            pathFound = Boolean.TRUE;
            return;
        }

        visited.add(current);
        int n = matrix.length;
        List<PointCostTuple> neighbors = new ArrayList<>();

        // Collect all neighbors and their costs
        for (int neighbor = 0; neighbor < n; neighbor++) {
            double edgeCost = matrix[current][neighbor];
            if (edgeCost > 0 && !visited.contains(neighbor)) {
                neighbors.add(new PointCostTuple(neighbor, edgeCost));
            }
        }

        recalculateTemperature();
        if (!(enableSimulatedAnnealing && Math.random() < temperature)) {
            // Sort neighbors first by presence in the priority list, and only later by closest distance/nearest neighbor
            neighbors.sort((a, b) -> {
                if (priority.contains(a.node) && !priority.contains(b.node)) {
                    return -1;
                } else if (!priority.contains(a.node) && priority.contains(b.node)) {
                    return 1;
                } else {
                    return Double.compare(a.cost, b.cost);
                }
            });
        } else {
            // If simulated annealing is enabled, and random was below temperature, shuffle the neighbors
            Collections.shuffle(neighbors);
        }

        // Explore the neighbors
        for (PointCostTuple neighbor : neighbors) {
            currentPath.add(neighbor.node);
            recursiveNearestNeighborSearch(neighbor.node, visited, currentCost + neighbor.cost, currentPath);
            currentPath.remove(currentPath.size() - 1);
            if (pathFound) {
                break;
            }
        }

        // If pathFound is True, we need to set the bestPath, and return all the way to the top
        // We need to be on a node that has a way to return to DEPOT, _AND_ bestPath not set, so that we don't set it multiple times
        // This also handles the possibility that there are no more neighbors/visitable nodes
        if (neighbors.isEmpty() || pathFound && isSet[current][DEPOT_ID]) {
            // Set pathFound to TRUE if we came here because there are no more neighbors available, so we'll just go ahead and go back to depot
            pathFound = Boolean.TRUE;
            if (bestPath.isEmpty()) {
                double distanceToDepot = matrix[current][DEPOT_ID];
                if ((currentCost + distanceToDepot) < CalculationsUtil.MAX_DRIVE_TIME_MINUTES) {
                    // This is eject, this flag will return all the way to the top
                    minCost = currentCost + distanceToDepot;
                    currentPath.add(DEPOT_ID);
                    bestPath = new ArrayList<>(currentPath);
                    return;
                }
            }
        }
        visited.remove(current);
    }

    private void recalculateTemperature() {
        // This should start high at the beginning, to add some more randomness, and taper down quickly as more routes are solved
        double ratio = ((double) solved.size() / (double) numOfNodes);
        double lambda = Math.log(INITIAL_TEMPERATURE / FINAL_TEMPERATURE);
        temperature = INITIAL_TEMPERATURE * Math.exp(-lambda * ratio);
    }

    /**
     * This builds an internal representation of the loads, sorted by priority of different distances,
     * from origin to P1, between P1 and P2, and from P2 to origin
     * Fetch the top 20% of the "worst" loads, and try to always prioritize those
     */
    public void addLoads(Load[] loads) {
        List<Load> loadsBySize = Arrays.asList(Arrays.copyOf(loads, loads.length));
        loadsBySize.sort(Comparator.comparingDouble(Load::getDistanceFromP1ToP2).reversed());
        loadsBySize = loadsBySize.subList(0, (int) (loads.length * 0.2));

        List<Load> loadsByOriginToP1 = Arrays.asList(Arrays.copyOf(loads, loads.length));
        loadsByOriginToP1.sort(Comparator.comparingDouble(Load::getDistanceFromOriginToP1).reversed());
        loadsByOriginToP1 = loadsByOriginToP1.subList(0, (int) (loads.length * 0.2));

        List<Load> loadsByP2ToOrigin = Arrays.asList(Arrays.copyOf(loads, loads.length));
        loadsByP2ToOrigin.sort(Comparator.comparingDouble(Load::getDistanceFromP2ToOrigin).reversed());
        loadsByP2ToOrigin = loadsByP2ToOrigin.subList(0, (int) (loads.length * 0.2));

        priority.addAll(loadsBySize.stream().map(x -> ((int) (x.getId() * 2) - 1)).toList());
        priority.addAll(loadsByOriginToP1.stream().map(x -> ((int) (x.getId() * 2) - 1)).toList());
        priority.addAll(loadsByP2ToOrigin.stream().map(x -> ((int) (x.getId() * 2) - 1)).toList());
    }

    static class PointCostTuple {
        private final int node;
        private final double cost;

        PointCostTuple(int node, double cost) {
            this.node = node;
            this.cost = cost;
        }
    }

}