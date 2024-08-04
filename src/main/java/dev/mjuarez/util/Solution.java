package dev.mjuarez.util;

import java.util.List;

public class Solution {
    private List<Route> routes;
    private int drivers;
    private double totalCost;

    public Solution(List<Route> routes, double totalCost) {
        this.routes = routes;
        this.drivers = routes.size();
        this.totalCost = totalCost;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public List<Route> getRoutes() {
        return routes;
    }
}
