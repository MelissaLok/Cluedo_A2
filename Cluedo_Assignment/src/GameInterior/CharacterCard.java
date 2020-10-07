package GameInterior;
import TextDesign.Colors;

public class CharacterCard implements Card {

  private String name;
  private String characterColor = Colors.randomColor();
  //------------------------
  // CONSTRUCTOR
  //------------------------

  public CharacterCard(String name)
  {
    this.name = name;
  }

  //------------------------
  // GETTERS, SETTERS AND TO STRINGS
  //------------------------

  @Override
  public String getName() {
    return name;
  }
  @Override
  public String toString()
  {
    return characterColor + "[" + name + "]" + reset;
  }  

}