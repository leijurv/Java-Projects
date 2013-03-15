/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package astar;

import java.util.*;

/**
 *
 * @author leif
 */
public class Graph {

    static Graph current;
    AStarNode startNode;
    AStarNode goalNode;
    ArrayList<AStarNode> blocked;
    int XMin;
    int XMax;
    int YMin;
    int YMax;

    public Graph(ArrayList<AStarNode> blocks, AStarNode start, AStarNode end, int xMin, int xMax, int yMin, int yMax) {
        startNode = start;
        goalNode = end;
        blocked = blocks;
        XMin = xMin;
        XMax = xMax;
        YMin = yMin;
        YMax = yMax;
    }

    public static class PriorityList extends LinkedList {

        public void add(Comparable object) {
            for (int i = 0; i < size(); i++) {
                if (object.compareTo(get(i)) <= 0) {
                    add(i, object);
                    return;
                }
            }
            addLast(object);
        }
    }

    protected ArrayList constructPath(AStarNode node) {
        LinkedList path1 = new LinkedList();
        while (node.pathParent != null) {
            path1.addFirst(node);
            node = node.pathParent;
        }
        ArrayList n = new ArrayList(path1);
        return n;
    }

    public void solve() {
        PriorityList openList = new PriorityList();
        LinkedList closedList = new LinkedList();
        startNode.costFromStart = 0;
        startNode.estimatedCostToGoal =
                startNode.getEstimatedCost(goalNode);
        startNode.pathParent = null;
        openList.add(startNode);
        while (!openList.isEmpty()) {

            AStarNode node = (AStarNode) openList.removeFirst();
            if (node.equals(goalNode)) {
                path = constructPath(node);
                if (!path.contains(startNode)) {
                    path.add(startNode);
                }
                if (!path.contains(goalNode)) {
                    path.add(goalNode);
                }
                return;
            }

            List neighbors = node.getNeighbors();
            for (int i = 0; i < neighbors.size(); i++) {
                AStarNode neighborNode =
                        (AStarNode) neighbors.get(i);
                boolean isOpen = openList.contains(neighborNode);
                boolean isClosed =
                        closedList.contains(neighborNode);
                float costFromStart = node.costFromStart
                        + node.getCost(neighborNode);
                if ((!isOpen && !isClosed)
                        || costFromStart < neighborNode.costFromStart) {
                    neighborNode.pathParent = node;


                    neighborNode.costFromStart = costFromStart;
                    neighborNode.estimatedCostToGoal =
                            neighborNode.getEstimatedCost(goalNode);
                    if (isClosed) {
                        closedList.remove(neighborNode);
                    }
                    if (!isOpen) {
                        openList.add(neighborNode);
                    }

                }
            }
            closedList.add(node);
        }
        path.clear();
        path.add(startNode);
        path.add(goalNode);
    }
    ArrayList<AStarNode> path = new ArrayList();
}
