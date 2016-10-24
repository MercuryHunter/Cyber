package com.uja.game.objectUtilities.pathing;

/**
 * Created by jonathanalp on 2016/10/11.
 */

public class VirtualNode {

    private int x, y;
    private double cost, heuristicCostToEnd;

    public VirtualNode(int x, int y, double cost, double heuristicCostToEnd) {
        this.x = x;
        this.y = y;
        this.cost = cost;
        this.heuristicCostToEnd = heuristicCostToEnd;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double cost() {
        return cost;
    }

    public double heuristicCost() {
        return heuristicCostToEnd;
    }

    public double totalCost() {
        return cost + heuristicCostToEnd;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof VirtualNode)) return false;
        VirtualNode node2 = (VirtualNode) object;
        return node2.x == x && node2.y == y;
    }

    @Override
    public int hashCode() {
        return x * 1000 + y;
    }

    public String toString() {
        return String.format("%d,%d (%.2f, %.2f)", x, y, cost, heuristicCostToEnd);
    }

}
