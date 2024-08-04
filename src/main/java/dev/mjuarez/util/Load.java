package dev.mjuarez.util;

import java.util.Objects;

public final class Load {
    private final int id;
    private final CartesianPoint p1;
    private final CartesianPoint p2;
    private final double distanceFromP1ToP2;
    private final double distanceFromOriginToP1;
    private final double distanceFromP2ToOrigin;

    @Override
    public String toString() {
        return "Load{" +
                "id=" + id +
                ", p1=" + p1 +
                ", p2=" + p2 +
                ", distanceFromP1ToP2=" + distanceFromP1ToP2 +
                ", distanceFromOriginToP1=" + distanceFromOriginToP1 +
                ", distanceFromP2ToOrigin=" + distanceFromP2ToOrigin +
                '}';
    }

    public Load(int id, CartesianPoint p1, CartesianPoint p2) {
        this.id = id;
        this.p1 = p1;
        this.p2 = p2;
        this.distanceFromP1ToP2 = CalculationsUtil.calculateTimeInMinutes(p1, p2);
        this.distanceFromOriginToP1 = CalculationsUtil.calculateTimeInMinutes(CartesianPoint.ORIGIN, p1);
        this.distanceFromP2ToOrigin = CalculationsUtil.calculateTimeInMinutes(p2, CartesianPoint.ORIGIN);
    }

    public int getId() {
        return id;
    }

    public CartesianPoint p1() {
        return p1;
    }

    public CartesianPoint p2() {
        return p2;
    }

    public double getDistanceFromP1ToP2() {
        return distanceFromP1ToP2;
    }

    public double getDistanceFromOriginToP1() {
        return distanceFromOriginToP1;
    }

    public double getDistanceFromP2ToOrigin() {
        return distanceFromP2ToOrigin;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Load) obj;
        return this.id == that.id &&
                Objects.equals(this.p1, that.p1) &&
                Objects.equals(this.p2, that.p2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, p1, p2);
    }

}
