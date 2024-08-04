package dev.mjuarez.util;

import java.util.List;

public class Route {
    private List<Integer> path;
    private double cost;

    public Route(List<Integer> path, double cost) {
        this.path = path;
        this.cost = cost;
    }

    public List<Integer> getPath() {
        return path;
    }

    public double getCost() {
        return cost;
    }
}
