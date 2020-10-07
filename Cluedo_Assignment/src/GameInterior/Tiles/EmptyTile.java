package GameInterior.Tiles;
import GUI.AStarNode;
import GUI.TileNode;
import GameInterior.Room;

import java.awt.*;

public class EmptyTile extends Tile {

    //------------------------
    // CONSTRUCTOR
    //------------------------

    public EmptyTile(int y, int x){
        super(y, x);
    }

    //------------------------
    // GETTERS AND SETTERS
    //------------------------

    @Override
    public Room getRoom(){
        return null;
    }

    @Override
    public void setRoom(Room room) {}

    @Override
    public String toString() {
        return " ";
    }

    @Override
    public void setNode(TileNode node) {}

    @Override
    public AStarNode getNode() {
        return null;
    }

    @Override
    public boolean hasWall() {
        return false;
    }

    @Override
    public void setMovable(boolean moveable) {}

    @Override
    public boolean getMovable() {
        return false;
    }

    @Override
    public void setImage(Image img) {
        this.img = img;
    }

    @Override
    public void setGImage(Image gimg) {}

    @Override
    public void setRImage(Image rimg) {}

    @Override
    public Image getImage() { return null; }

    @Override
    public Image getGImage() {
        return null;
    }

    @Override
    public Image getRImage() {
        return null;
    }

    @Override
    public void setWalked(boolean walked) {}

    @Override
    public boolean getWalked() {
        return false;
    }


}
