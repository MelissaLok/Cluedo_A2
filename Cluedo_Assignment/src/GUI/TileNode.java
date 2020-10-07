package GUI;

import GameInterior.Player;
import GameInterior.Tiles.Corridor;
import GameInterior.Tiles.RoomTile;
import GameInterior.Tiles.Tile;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TileNode extends AStarNode {

    Tile tile;
    int x;
    int y;
    Point p;

    TileNode (Tile tile){
        this.tile = tile;
        this.x = tile.getX();
        this.y = tile.getY();
        this.p = new Point(tile.getX(), tile.getY());
    }

    @Override
    public float getCost(AStarNode node) {
        return 1;
    }

    @Override
    public float getEstimatedCost(AStarNode node) {
        //get hypotenuse from this node to target node
        Point otherP = new Point(((TileNode)node).x, ((TileNode)node).y);
        double ac = Math.abs(otherP.getY() - y);
        double cb = Math.abs(otherP.getX() - x);
        return (float)Math.hypot(ac, cb);
    }

    @Override
    public ArrayList<AStarNode> getNeighbors(Tile[][] board, Player currentPlayer) {
        ArrayList<AStarNode> neighbours = new ArrayList<>();
        Tile currentTile = board[y][x];
        // ensures the next tile is a room or corridor and there is no player on that tile.
        if (x > 0  && (getNode(currentTile, board[y][x - 1], currentPlayer)) != null && board[y][x - 1].getPlayer() == null) neighbours.add(getNode(currentTile, board[y][x - 1], currentPlayer));
        if (x < 23 && (getNode(currentTile, board[y][x + 1], currentPlayer)) != null && board[y][x + 1].getPlayer() == null) neighbours.add(getNode(currentTile, board[y][x + 1], currentPlayer));
        if (y > 0  && (getNode(currentTile, board[y - 1][x], currentPlayer)) != null && board[y - 1][x].getPlayer() == null) neighbours.add(getNode(currentTile, board[y - 1][x], currentPlayer));
        if (y < 24 && (getNode(currentTile, board[y + 1][x], currentPlayer)) != null && board[y + 1][x].getPlayer() == null) neighbours.add(getNode(currentTile, board[y + 1][x], currentPlayer));

        return neighbours;
    }

    @Override
    public Tile getTile() {
        return tile;
    }


    /**
     * This controls the validity of the next tile according to game rules.
     * @param currentTile
     * @param nextTile
     * @param currentPlayer
     * @return nextTile - if all conditions are met
     */
    public AStarNode getNode(Tile currentTile, Tile nextTile, Player currentPlayer){
        AStarNode node = null;
        if((currentTile instanceof Corridor && nextTile instanceof Corridor)||
            (currentTile instanceof RoomTile && nextTile instanceof RoomTile)||
            (currentTile instanceof Corridor && !nextTile.hasWall())||
            ((!currentTile.hasWall() && nextTile instanceof Corridor) && !currentPlayer.hasEnteredRoom()) && !nextTile.getWalked()) {
            node = nextTile.getNode();
        }
        return node;
    }

}
