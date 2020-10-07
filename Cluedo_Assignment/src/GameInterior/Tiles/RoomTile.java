package GameInterior.Tiles;
import GUI.AStarNode;
import GUI.TileNode;
import GameInterior.Room;
import TextDesign.Colors;

import java.awt.*;

public class RoomTile extends Tile {
    private Room room;
    private String red = Colors.ANSI_RED;
    private AStarNode node;
    private boolean movable;
    private boolean walked = false;

    public RoomTile(int y, int x, String name){
        super(y, x);
        this.name = name;
    }

    public void setNode(TileNode node) { this.node = node; }

    public TileNode getNode() { return (TileNode)node; }

    @Override
    public boolean hasWall() {
        if(north || south || east || west) return true;
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

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room r) {
        this.room = r;
    }

    @Override
    public String toString() {
        if (player == null) {
            if (north || south || east || west) {
                return red + name.substring(0, 1) + white;
            }
            else return name.substring(0, 1);
        }
        return player.nameInitial();
    }
}
