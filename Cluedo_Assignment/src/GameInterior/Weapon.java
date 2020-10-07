package GameInterior;
import java.awt.Image;

import GameInterior.Tiles.Tile;
import TextDesign.Colors;

public class Weapon implements Card {

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //models.Weapon Attributes
  private String name;
  private String weaponColor = Colors.randomColor();
  private Tile tile;
  
  private Image token;

  //------------------------
  // CONSTRUCTOR
  //------------------------

public Weapon(String weapon)
  {
    name = weapon;
  }

  //------------------------
  // INTERFACE
  //------------------------
  public Tile getTile() {
    return tile;
  }

  public void setTile(Tile tile, Tile prev){
    this.tile = tile;
    getTile().setWeapon(this);
    if(prev != null) prev.setPlayer(null);
  }
  
  public Image getToken() {
	return token;
  }
	
  public void setToken(Image token) {
  this.token = token;
  }
  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public String toString()
  {
    return weaponColor + "[" + getName()+ "]" + reset;
  }
}