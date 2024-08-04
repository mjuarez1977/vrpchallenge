package dev.mjuarez.util;

import java.text.DecimalFormat;

public class CalculationsUtil {

    public static final float MAX_DRIVE_TIME_MINUTES = 12 * 60;
    public static final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public static double calculateTimeInMinutes(CartesianPoint p1, CartesianPoint p2) {
        return Math.sqrt(Math.pow((p2.x() - p1.x()), 2) + Math.pow((p2.y() - p1.y()), 2));
    }

    public static double calculateTotalCost(int drivers, double total_driven_minutes) {
        return 500 * drivers * total_driven_minutes;
    }

    // Sample input:
    // 1 (-9.100071078494038,-48.89301103772511) (-116.78442279683607,76.80147820713637)
    public static Load parseLoadFromInputLine(String input) {
        // Assume field separator is at least one space
        String[] fields = input.split(" +");
        return new Load(Integer.parseInt(fields[0]), parseCartesianPointFromString(fields[1]), parseCartesianPointFromString(fields[2]));
    }

    // Sample input:
    // (-9.100071078494038,-48.89301103772511)
    public static CartesianPoint parseCartesianPointFromString(String input) {
        String[] fields = input.split(",");
        double x = Double.parseDouble(fields[0].replaceAll("[()]", ""));
        double y = Double.parseDouble(fields[1].replaceAll("[()]", ""));
        return new CartesianPoint(x, y);
    }
}
