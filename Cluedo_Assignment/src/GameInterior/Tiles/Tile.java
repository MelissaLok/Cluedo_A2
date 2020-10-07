package GameInterior.Tiles;
import GUI.AStarNode;
import GUI.TileNode;
import GameInterior.Player;
import GameInterior.Room;
import GameInterior.Weapon;
import TextDesign.Colors;

import java.awt.*;

public abstract class Tile {

  protected String name;
  protected final int x ; // x cordinate of the top left corner of the square
  protected final int y ; // y cordinate of the top left corner of the square

  protected Image img;
  protected Image gimg;
  protected Image rimg;

  protected boolean movable;

  protected AStarNode node;

  // Squares Associations
  protected Player player;
  protected Weapon weapon;
  protected String white = Colors.ANSI_RESET;

  public boolean north, east, south, west;
  public abstract void setRoom(Room room);
  public abstract Room getRoom();

  public Tile(int y, int x) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }


  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }
  public void setWeapon(Weapon weapon) {
	    this.weapon = weapon;
  }
  public Weapon getWeapon() {
	    return weapon;
	  }

  public abstract String toString();

  public abstract void setNode(TileNode node);

  public abstract AStarNode getNode();

  public abstract boolean hasWall();

  public abstract void setMovable(boolean moveable);

  public abstract boolean getMovable();


  public abstract void setImage(Image img);

  public abstract void setGImage(Image gimg);

  public abstract void setRImage(Image rimg);

  public abstract Image getImage();

  public abstract Image getGImage();

  public abstract Image getRImage();

  public abstract void setWalked(boolean walked);

  public abstract boolean getWalked();

}