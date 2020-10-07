package GameInterior.Tiles;
import GUI.AStarNode;
import GUI.TileNode;
import GameInterior.Room;

import java.awt.*;

public class Corridor extends Tile {

    AStarNode node;
    private boolean movable;
    private int walkNum;
    private boolean walked = false;

    //------------------------
    // CONSTRUCTOR
    //------------------------
    public Corridor(int y, int x) {
        super(y, x);
    }

    //------------------------
    // GETTERS AND SETTERS
    //------------------------

    public void setNode(TileNode node) { this.node = node; }

    public TileNode getNode() { return (TileNode)node; }

    @Override
    public boolean hasWall() {
        return false;
    }

    @Override
    public void setMovable(boolean moveable) {
        this.movable = moveable;
    }

    @Override
    public boolean getMovable() {
        return movable;
    }

    @Override
    public Room getRoom() {
        return null;
    }

    @Override
    public void setRoom(Room room) {}

    @Override
    public void setImage(Image img) {
        this.img = img;
    }

    @Override
    public void setGImage(Image gimg) {
        this.gimg = gimg;
    }

    @Override
    public void setRImage(Image rimg) {
        this.rimg = rimg;
    }

    @Override
    public Image getImage() {
        return img;
    }

    @Override
    public String toString() {
        if(player == null) return "-";
        return player.nameInitial();
    }

    @Override
    public Image getGImage() {
        return gimg;
    }

    @Override
    public Image getRImage() {
        return rimg;
    }

    @Override
    public void setWalked(boolean walked) {
        this.walked = walked;
    }

    @Override
    public boolean getWalked() {
        return walked;
    }
}
