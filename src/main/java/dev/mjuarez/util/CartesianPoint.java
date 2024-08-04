package dev.mjuarez.util;

import java.util.Objects;

public final class CartesianPoint {
    public static final CartesianPoint ORIGIN = new CartesianPoint(0, 0);
    private final double x;
    private final double y;

    public CartesianPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CartesianPoint) obj;
        return Double.doubleToLongBits(this.x) == Double.doubleToLongBits(that.x) &&
                Double.doubleToLongBits(this.y) == Double.doubleToLongBits(that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "[" +
                "x=" + CalculationsUtil.decimalFormat.format(x) + ", " +
                "y=" + CalculationsUtil.decimalFormat.format(y) + ']';
    }
}
