package com.uja.game.objectUtilities.pathing;

import java.util.ArrayDeque;
import java.util.Comparator;

/**
 * Created by jonathanalp on 2016/10/11.
 */

public class Path implements Comparator<Path>, Comparable<Path> {

    private ArrayDeque<VirtualNode> path;
    private double cost;

    public Path() {
        path = new ArrayDeque<VirtualNode>();
        cost = 0;
    }

    public Path(VirtualNode startNode) {
        path = new ArrayDeque<VirtualNode>();
        path.add(startNode);
        cost = startNode.cost();
    }

    public Path(Path path) {
        this.path = path.path.clone();
        this.cost = path.cost;
    }

    public void addNode(VirtualNode node) {
        path.add(node);
        cost += node.cost();
    }

    public VirtualNode currentNode() {
        return path.getLast();
    }

    public VirtualNode secondNode() {
        if (path.size() > 1) {
            path.removeFirst();
            return path.getFirst();
        }
        if (path.size() == 1) return path.getFirst();
        return null;
    }

    public ArrayDeque<VirtualNode> getPath() {
        return path.clone();
    }

    public double totalCost() {
        return cost + currentNode().heuristicCost();
    }

    ;

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Path)) return false;
        Path path2 = (Path) object;
        if (path2.path.size() != path.size()) return false;
        return path2.path.containsAll(path) && path.containsAll(path2.path);
    }

    @Override
    public int compare(Path o1, Path o2) {
        return (int) Math.signum(o1.totalCost() - o2.totalCost());
    }

    @Override
    public int compareTo(Path o) {
        //return (int)Math.signum(o.totalCost() - totalCost()); // Wrong one
        return (int) Math.signum(totalCost() - o.totalCost());
    }
}
