package GUI;

import GameInterior.Player;
import GameInterior.Tiles.Tile;

import java.util.ArrayList;
import java.util.List;

public abstract class AStarNode implements Comparable {

    AStarNode pathParent;
    float costFromStart;
    float estimatedCostToGoal;

    public float getCost() {
        return costFromStart + estimatedCostToGoal;
    }

    public int compareTo(Object other) {
        float thisValue = this.getCost();
        float otherValue = ((AStarNode)other).getCost();

        float v = thisValue - otherValue;
        return (v>0)?1:(v<0)?-1:0; // sign function
    }

    /**
     Gets the cost between this node and the specified
     adjacent (AKA "neighbor" or "child") node.
     */
    public abstract float getCost(AStarNode node);

    /**
     Gets the estimated cost between this node and the
     specified node.
     */
    public abstract float getEstimatedCost(AStarNode node);

    /**
     Gets the children (AKA "neighbors" or "adjacent nodes")
     of this node.
     */
    public abstract ArrayList<AStarNode> getNeighbors(Tile[][] tiles, Player currentPlayer);

    public abstract Tile getTile();
}  