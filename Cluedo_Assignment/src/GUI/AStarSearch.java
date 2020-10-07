package GUI;

import GameInterior.Player;
import GameInterior.Tiles.Tile;

import java.util.*;

// This A* search (and AStarNode class) with help from https://www.peachpit.com/articles/article.aspx?p=101142&seqNum=2

public class AStarSearch {

    Tile[][] board;
    public void setBoard(Tile[][] board){
        this.board = board;
    }

    /**
     Construct the path, not including the start node.
     */
    protected ArrayList<Tile> constructPath(AStarNode node) {
        ArrayList<Tile> path = new ArrayList<>();
        while (node.pathParent != null) {
            path.add(0, node.getTile());
            node = node.pathParent;
        }
        return path;
    }

    /**
     Find the path from the start node to the end node. A list
     of AStarNodes is returned, or null if the path is not
     found.
     */
    public ArrayList<Tile> findPath(AStarNode startNode, AStarNode goalNode, Player currentPlayer) {

        PriorityQueue<AStarNode> openList = new PriorityQueue<>();
        ArrayList<AStarNode> closedList = new ArrayList<>();

        startNode.costFromStart = 0;
        startNode.estimatedCostToGoal = startNode.getEstimatedCost(goalNode);
        startNode.pathParent = null;
        openList.add(startNode);

        while (!openList.isEmpty()) {
            AStarNode node = openList.poll();
            if (node == goalNode) {
                // construct the path from start to goal
                return constructPath(goalNode);
            }

            ArrayList<AStarNode> neighbors = node.getNeighbors(board, currentPlayer);
            for (AStarNode neighborNode : neighbors) {
                boolean isOpen = openList.contains(neighborNode);
                boolean isClosed =
                        closedList.contains(neighborNode);
                float costFromStart = node.costFromStart +
                        node.getCost(neighborNode);

                // check if the neighbor node has not been
                // traversed or if a shorter path to this
                // neighbor node is found.
                if ((!isOpen && !isClosed) ||
                        costFromStart < neighborNode.costFromStart) {
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

        // no path found
        return null;
    }

}