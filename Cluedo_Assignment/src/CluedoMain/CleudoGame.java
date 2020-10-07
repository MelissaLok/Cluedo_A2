package CluedoMain;

import Exceptions.*;

import GUI.GUI;


import GameInterior.Tiles.Corridor;
import GameInterior.Tiles.RoomTile;
import GameInterior.Tiles.Tile;
import GameInterior.*;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class CleudoGame
{
  private boolean gameEnded;
  private Player winner;
  private List<Card> deck = new ArrayList<>(); // easier to shuffle deck if using list :)
  private Board board;
  public CardTuple solution;
  private CardTuple suggestion;
  private CardTuple accusation;
  private List<CardTuple> suspects = new ArrayList<>();
  private List<Tile> moveList = new ArrayList<>();

  ArrayList<Player> suggestedPlayers = new ArrayList<Player>();

  private CharacterCard[] charList = new CharacterCard[]{
          new CharacterCard("Miss Scarlett"),
          new CharacterCard("Mr. Green"),
          new CharacterCard("Dr. Orchid"),
          new CharacterCard("Mrs. Peacock") ,
          new CharacterCard("Professor Plum"),
          new CharacterCard("Colonel Mustard")
  };

  private Player[] players;

  private static JFrame frame = new JFrame();
  private static GUI GUI;

  public CleudoGame(){
    newGame();
  }

  /**
   * Sets up the game by setting up the players, the deck and distributing the cards
   *
   */
  private void newGame(){
    chooseCharacters(); //sets up player from user input and display them on board.
    board = new Board(players);

    solution = new CardTuple(random(board.getRooms().toArray(new Room[9])), random(board.getWeapons()), random(board.getCharacters()));
    setUpDeck(solution.getRoom(), solution.getWeapon(), solution.getCharacter());
    dealCards();
    setUpPlayers();
    setUpWeapons();
    setupGUI();
  }

  /**
   * Arrange the players' pieces on the appropriate positions on the board (depending on where the character originally starts)
   */
  private void setUpPlayers() {

    for (Player p : players) {
      Tile tile;

      if(p.getCharacter().getName().equalsIgnoreCase("Miss Scarlett"))
        tile = board.getTiles()[24][7];

      else if(p.getCharacter().getName().equalsIgnoreCase("Dr. Orchid"))
        tile = board.getTiles()[0][9];

      else if(p.getCharacter().getName().equalsIgnoreCase("Professor Plum"))
        tile = board.getTiles()[19][23];

      else if(p.getCharacter().getName().equalsIgnoreCase("Mr. Green"))
        tile = board.getTiles()[0][14];

      else if(p.getCharacter().getName().equalsIgnoreCase("Mrs. Peacock"))
        tile = board.getTiles()[6][23];

      else
        tile = board.getTiles()[17][0];

      p.setTile(tile, null);
    }
  }

  private void setUpWeapons() {

    for (Weapon w : board.getWeapons()) {
      Tile tile;

      if(w.getName().equalsIgnoreCase("Dagger"))
        tile = board.getTiles()[6][10];

      else if(w.getName().equalsIgnoreCase("Spanner"))
        tile = board.getTiles()[13][4];

      else if(w.getName().equalsIgnoreCase("Revolver"))
        tile = board.getTiles()[3][3];

      else if(w.getName().equalsIgnoreCase("Rope"))
        tile = board.getTiles()[20][12];

      else if(w.getName().equalsIgnoreCase("Lead Pipe"))
        tile = board.getTiles()[16][20];

      else
        tile = board.getTiles()[21][19];

      w.setTile(tile, null);
    }
  }

  /**
   * Arrange the players' pieces on the appropriate positions on the board (depending on where the character originally starts)
   */
  private void chooseCharacters(){

    players = new Player[GUI.playerNumPopup()];
    for(int i = 0; i < players.length; i ++){
      players[i] = new Player("Player " + (i+1));
    }
    GUI.playerCharPopup(players);

  }

  private <T> T random(T[] values) {
    return values[new Random().nextInt(values.length)];
  }

  /**
   * Player's turn is processed here. If player is out of the game, they cannot take a turn.
   * GUI provides a String to determine the state of the game.
   */
  public void playerTurn () {
    BufferedReader buff = new BufferedReader(new InputStreamReader(System.in));
    GUI.setText("Welcome to CLUEDO!");

//    GUI.repaintPane()

    for(Player player : players) {

      if(gameEnded) return;
      GUI.setCurrentPlayer(player);
      GUI.setPortrait();
      GUI.setAction("");
      GUI.setMovesLeft(-1);
      GUI.createDices(-1, -1);
      GUI.setRollSuggestEndTurn("Roll");
      player.setMadeSuggestion(false);
      player.setHasRolled(false);
      if(player.getTile() instanceof RoomTile) {
        player.setCurrentRoom(player.getTile());
      }else {
        player.setCurrentRoom(null);
      }
      player.setEnteredRoom(false);
//      GUI.clearStack();

      //====================================
      // GUI RESPONSE LOOP
      //====================================

      boolean hasRolled = false;
      if (!player.canWin()) {
        GUI.setText("Player" + player.getName() + " is out of the game, sorry!");
      } else {
        GUI.setText(player.getCharacter().getName() + ", roll your dice to begin your turn.");
        String action;
        do {
          GUI.updateCanvas();
          GUI.updateGraphics(); // is called every loop and repaints the graphics to the components - just add images and components to paint them

          action = GUI.getAction();

          if (action.equals("Take Notes")) {
            GUI.takeNotes();
          }
          if (!action.equals("unfocus")) GUI.setCanvasFocus();

          if (action.equals("Roll") && !hasRolled && GUI.getMovesLeft() == -1) {
            int dice1 = rollDice();
            GUI.dice1 = dice1;
            int dice2 = rollDice();
            GUI.dice2 = dice2;
            player.setTurns(dice1 + dice2);
            GUI.setMovesLeft(dice1 + dice2);
            player.setHasRolled(true);
            GUI.createDices(dice1, dice2);
            hasRolled = true;
            if(dice1 + dice2 == 8 || dice1 + dice2 == 11) GUI.setText("You have rolled an " + (dice1+dice2) + ". Click on the map to move!");
            else GUI.setText("You have rolled a " + (dice1 + dice2) + ". Click on the map to move!");
          }

          if (action.contains("Move") && player.getTurns() > 0) {
            if (action.charAt(5) == 'w') movePlayer(player, "w");
            if (action.charAt(5) == 's') movePlayer(player, "s");
            if (action.charAt(5) == 'a') movePlayer(player, "a");
            if (action.charAt(5) == 'd') movePlayer(player, "d");
            GUI.setAction("");
          }

          if (action.equals("Suggest") && player.getHasRolled() && !player.getMadeSuggestion()) {
            GUI.suggestPopup();
            if (player.getMadeSuggestion()) {
              suggest(player);
              action = "";
            }
          }

          if (action.equals("Accuse")) {
            GUI.accusePopup();
            if (player.getMadeAccusation()) {
              accuse(player);
              break;
            }
          }

          if (GUI.getMovesLeft() == 0 && player.getTile() instanceof Corridor) {
            GUI.createDices(-1, 0);
            GUI.setRollSuggestEndTurn("End Turn");
            GUI.setAction("");
          }

          if (action.equals("View Hand")) {
            GUI.viewHandPopup();
          }

          // The primary state control loop - restricts the options available to the player.
          if (player.getTile() instanceof RoomTile) {
            if(suggestedPlayers.contains(player))
            {
              player.setEnteredRoom(true);
              suggestedPlayers.remove(player);
              GUI.setRollSuggestEndTurn("Suggest");
              player.setHasRolled(true);
            }
            if (player.getCurrentRoom() == null || player.getCurrentRoom().getRoom() != player.getTile().getRoom()) {
              player.setEnteredRoom(true);
              player.setCurrentRoom(player.getTile());
            }
            if (player.getMadeSuggestion()) {
              GUI.setRollSuggestEndTurn("End Turn");
            } else if (player.getHasRolled() && (player.getCurrentRoom().getRoom() != player.getTile().getRoom() || player.hasEnteredRoom())) {
              GUI.setText("You have entered the " + player.getTile().getRoom().getName() + ".\nYou can either make a suggestion or accuse another a player.");
              GUI.setRollSuggestEndTurn("Suggest");
            }
          } else if (player.getTile() instanceof Corridor) {
            if (player.getHasRolled() && GUI.getMovesLeft() == 0) {
              GUI.setText("You can either end your turn, or accuse a character.");
              GUI.setRollSuggestEndTurn("End Turn");
            } else {
              GUI.setRollSuggestEndTurn("Roll");
            }
          }
        } while(!action.equals("End Turn"));
      }
    }
  }

  /**
   * Player's input after the turn is processed here. The player is allowed to either suggest (if they are in a room), accuse or end their turn.
   */
  private void suggestAccuseEndTurn(Player player, BufferedReader buff) throws GameException, IOException {
    String afterMove;
    while(true) {
      try {
        afterMove = buff.readLine();
        if (!(afterMove.equals("suggest") || afterMove.equals("accuse") || afterMove.equalsIgnoreCase("end turn"))) throw new GameException("Please re-enter your input");
        break;
      } catch (IOException | GameException e) {
      }
    }


    switch (afterMove) {
      case "suggest":
        if (!player.canWin())
          throw new GameException("Player's previous accusation was refuted, so they cannot make further suggestions or accusations");
        if(player.getTile().getRoom() == null) throw new GameException("Player is not in room");
        suggest(player);
        break;

      case "accuse":
        if (!player.canWin()) {
          // Accuser gets to see the solution, but not the other players
          throw new GameException("Player's previous accusation was refuted, so they cannot make further suggestions or accusations");
        }
//        accuse(player, buff);

      case "end turn":
        return;

      // Player is out of the game
      default:
        break;
    }
  }

  /**
   * Resets suggestion or accusation made in the previous turn
   */
  private void resetSandA(){
    suggestion = null;
    accusation = null;
    for (Player player : players) {
      player.setAccusation(null);
      player.setSuggestion(null);
    }
  }

  /**
   * This method carries out the suggestion.
   * @param player
   */
  private void suggest(Player player) {
//    suggestion = craftSandA(buff, player);
    suggestion = player.getSuggestion();
    // store suggestion into player class
//    player.makeSuggestion(suggestion);

    // teleport weapon into suggested room
    player.getTile().getRoom().setWeapon(suggestion.getWeapon());
    List<Tile> squares = (List<Tile>) suggestion.getRoom().getTiles().stream().filter(x -> x != player.getTile()).collect(toList());
    // Random tile in room
    Tile t = squares.get(new Random().nextInt(squares.size()));
    // Make sure tile is not a wall
    if(!player.hasWall(t, 1, 0) || !player.hasWall(t, -1, 0) || !player.hasWall(t, 0, -1) || !player.hasWall(t, 0, -1) && t.getPlayer() == null && t.getWeapon() == null && t instanceof RoomTile)
    {
      suggestion.getWeapon().getTile().setWeapon(null);
      suggestion.getWeapon().setTile(t, suggestion.getWeapon().getTile());

    }
    // Teleport the suggested character into suggested room
    Player suggestedPlayer = null;
    t = squares.get(new Random().nextInt(squares.size()));
    for (Player p : players)
    {
      if (p.getCharacter().getName().equals(suggestion.getCharacter().getName()) && !p.getCharacter().getName().equals(player.getCharacter().getName()))
      {
        suggestedPlayer = p;
        if(!suggestedPlayers.contains(p)) {
          suggestedPlayers.add(p);
        }
      }
    }
    if (suggestedPlayer != null)
    {
      // Make sure tile is not a wall
      if(!player.hasWall(t, 1, 0) || !player.hasWall(t, -1, 0) || !player.hasWall(t, 0, -1) || !player.hasWall(t, 0, -1) && t.getPlayer() == null && t.getWeapon() == null && t instanceof RoomTile)
      {
        suggestedPlayer.getTile().setPlayer(null);
        suggestedPlayer.setTile(t, suggestedPlayer.getTile());

      }
    }
    // ask next player to refute a card if the previous one does not have a card to refute
    int count = 0;
    Card c = null;
    Player refutePlayer = null;

    tag : for(Player p : players) {
      if (!p.getName().equals(player.getName()))
      {
        //get the first player that has a card that can refute
        Room room = player.getSuggestion().getRoom();
        Weapon weapon = player.getSuggestion().getWeapon();
        CharacterCard character = player.getSuggestion().getCharacter();

//          boolean hasCard = false;

        for (Card h : p.getCards()) {
          if (h == room || h == weapon || h == character) {
//              hasCard = true;
            c = GUI.refute(p, player.getSuggestion());
            refutePlayer = p;
            break tag;
          }
        }
      }
    }

    if (c != null) {
      GUI.setText("You suggested " + suggestion.getCharacter().getName() + " committed murder with a " + suggestion.getWeapon().getName() + " in the " + suggestion.getRoom().getName() +".\n" + "BUT... " +
              refutePlayer.getCharacter().getName() + " has refuted your suggestion with the " + c.getName() + " card.");
      count = 1;
    }

    // if all players presented relevent card, then count == 3
    if(count == 1) {
    }
    else {
      if(!suspects.contains(suggestion))// if the suggestion is already in suspects, do not add it.
        suspects.add(suggestion);
      GUI.setText("Your suggestion was not refuted. Could this be the answer??");
    }
  }

  /**
   * This method crafts the suggestion and accusations based on user inputs.
   * @param buff
   * @return
   * @throws InvalidNameException
   * @throws IOException
   */
  private CardTuple craftSandA(BufferedReader buff, Player player) throws InvalidNameException, IOException {
    if(player != null) { // If the player argument is not null, the player is trying to make a suggestion

      Room room;
      Weapon weapon;
      CharacterCard p;

      while(true) {
        try {
          room = player.getTile().getRoom();

          String w = buff.readLine();
          Weapon result = null;
          for (Weapon x1 : board.getWeapons()) {
            if (x1.getName().equalsIgnoreCase(w)) {
              result = x1;
              break;
            }
          }
          weapon = result;
          if (weapon == null) throw new InvalidNameException("No such weapon");

          String c = buff.readLine();
          CharacterCard found = null;
          for (CharacterCard x : board.getCharacters()) {
            if (x.getName().equalsIgnoreCase(c)) {
              found = x;
              break;
            }
          }
          p = found;
          if (p == null) throw new InvalidNameException("Character not found");
          break;
        } catch(GameException e) {
        }
      }
      return new CardTuple(room, weapon, p);
    }

    // The code will not reach here unless the player argument is not null, which means that the player is trying to accuse
    String r = buff.readLine();
    Room room = null;
    for (Room room1 : board.getRooms()) {
      if (room1.getName().equalsIgnoreCase(r)) {
        room = room1;
        break;
      }
    }

    if(room == null) throw new InvalidNameException("No such room");
    String w = buff.readLine();
    Weapon weapon = null;
    for (Weapon x1 : board.getWeapons()) {
      if (x1.getName().equalsIgnoreCase(w)) {
        weapon = x1;
        break;
      }
    }

    if(weapon == null) throw new InvalidNameException("No such weapon");
    String c = buff.readLine();
    CharacterCard p = null;
    for (CharacterCard x : board.getCharacters()) {
      if (x.getName().equalsIgnoreCase(c)) {
        p = x;
        break;
      }
    }

    if(p == null) throw new InvalidNameException("Character not found");
    return new CardTuple(room, weapon, p);
  }

  /**
   * Returns the card that the player is trying to get. Null if not in hand.
   * @param player
   * @param name
   * @return
   */
  public Card drawCard(Player player, String name){
    for (Card x : player.getCards()) {
      if (x.getName().equals(name)) {
        return x;
      }
    }
    return null;
  }

  /**
   * This method carries out the accusation.
   * @param player,
  //   * @param buff
   */
//  private void accuse(Player player, BufferedReader buff){
  private void accuse(Player player){
    while(true) {
      accusation = player.getAccusation();
      List<Tile> squares = (List<Tile>) accusation.getRoom().getTiles().stream().filter(x -> x != player.getTile()).collect(toList());
      // Random tile in room
      Tile t = squares.get(new Random().nextInt(squares.size()));
      // Make sure tile is not a wall
      if(!player.hasWall(t, 1, 0) || !player.hasWall(t, -1, 0) || !player.hasWall(t, 0, -1) || !player.hasWall(t, 0, -1) && t.getPlayer() == null && t.getWeapon() == null && t instanceof RoomTile)
      {
        accusation.getWeapon().getTile().setWeapon(null);
        accusation.getWeapon().setTile(t, accusation.getWeapon().getTile());

      }
      // Teleport the suggested character into suggested room
      Player suggestedPlayer = null;
      t = squares.get(new Random().nextInt(squares.size()));
      for (Player p : players)
      {
        if (p.getCharacter().getName().equals(accusation.getCharacter().getName()) && !p.getCharacter().getName().equals(player.getCharacter().getName()))
        {
          suggestedPlayer = p;
          if(!suggestedPlayers.contains(p)) {
            suggestedPlayers.add(p);
          }
        }
      }
      if (suggestedPlayer != null)
      {
        // Make sure tile is not a wall
        if(!player.hasWall(t, 1, 0) || !player.hasWall(t, -1, 0) || !player.hasWall(t, 0, -1) || !player.hasWall(t, 0, -1) && t.getPlayer() == null && t.getWeapon() == null && t instanceof RoomTile)
        {
          suggestedPlayer.getTile().setPlayer(null);
          suggestedPlayer.setTile(t, suggestedPlayer.getTile());

        }
      }
      break;
    }
    if(!accusation.equals(solution)) {

      GUI.playerLostPopup(solution);
      Tile[][] tiles = board.getTiles();
      Tile tile = tiles[0][0];
      while (true) {
        if (tile.getPlayer() != null)
        {
          tile = tiles[0][tile.getY() + 1];
        }
        else {
          break;
        }
      }
      player.setTile(tile,player.getTile());
      tile.setPlayer(player);
      player.setCanWin(false);
      int count = 0;
      for (Player p : players)
      {
        if(!p.canWin())
        {
          count++;
        }
      }
      if (count == players.length)
      {
        if(GUI.gameEndPopup(winner, solution))
        {
          new CleudoGame().play();
        }
      }
    }
    else {
      GUI.setText("You solved this heinous crime! Well done! You could be a real detective one day.");
      // gameWon
      winner = player;
      if(GUI.gameEndPopup(winner, solution))
      {
        new CleudoGame().play();
      }
    }

  }

  //=========================
  //  ACTIONS
  //=========================
  /**
   * This method moves the player around the board.
   * A player can accuse at any time during their turn.
   * @param currentPlayer
   * @param action
   */
  private void movePlayer(Player currentPlayer, String action) {
    Tile currentTile = currentPlayer.getTile();
    int turns = currentPlayer.getTurns();
    int x = currentTile.getX();
    int y = currentTile.getY();

    Tile nextTile = null;
    if (!(currentPlayer.getTile() instanceof RoomTile)) moveList.add(currentPlayer.getTile()); // adds the tile the player is standing on to a list (does not include room tiles)
    if (action.equals("w") && y != 0) {
      nextTile = board.getTiles()[y - 1][x];
    }
    if (action.equals("s") && y != 24) {
      nextTile = board.getTiles()[y + 1][x];
    }
    if (action.equals("a") && x != 0) {
      nextTile = board.getTiles()[y][x - 1];
    }
    if (action.equals("d") && x != 23) {
      nextTile = board.getTiles()[y][x + 1];
    }
    //validates move
    if(isValid(currentPlayer, currentTile, nextTile)){
      if(currentTile instanceof Corridor && nextTile instanceof RoomTile) {
        currentPlayer.setCurrentRoom(nextTile);
        currentPlayer.setEnteredRoom(true);
      }
      currentPlayer.setTile(nextTile, currentPlayer.getTile());
      if (currentPlayer.getTile() instanceof RoomTile && nextTile instanceof RoomTile) {
        currentPlayer.setTurns(++turns);
      }
      currentPlayer.setTurns(--turns);
    }
    GUI.updateMoves(currentPlayer.getTurns());
  }

  /**
   * Checks to see if the move is valid according to the game rules
   * Can enter and exit a room through entrance only. Cannot exit a room and enter the same room on the same turn.
   * @param currentPlayer
   * @param currentTile
   * @param nextTile
   * @return true if the conditions are met.
   */
  public boolean isValid(Player currentPlayer, Tile currentTile, Tile nextTile){
    if (!moveList.contains(nextTile) && (nextTile instanceof Corridor || nextTile instanceof RoomTile)) {
      if ((currentTile instanceof Corridor && nextTile instanceof Corridor) ||
              (currentTile instanceof RoomTile && nextTile instanceof RoomTile) ||
              (currentTile instanceof Corridor && !nextTile.hasWall() && (currentPlayer.getCurrentRoom() == null || !currentPlayer.getCurrentRoom().getRoom().getName().equals(nextTile.getRoom().getName()))) ||
              ((!currentTile.hasWall() && nextTile instanceof Corridor) && !currentPlayer.hasEnteredRoom())) {
        return true;
      }
    }
    return false;
  }


  public static int rollDice() {
    Random roll = new Random();

    // random integer from 0 to 5
    int rolled = roll.nextInt(6);

    // make the rolled interval to be 1 to 6 (dice starts with 1)
    rolled += 1;


    return rolled;
  }

  /**
   * This method takes all non-murder solution cards, shuffles it and sets up the deck.
   * @param r, w, c
   */
  private void setUpDeck(Room r, Weapon w, CharacterCard c){
    for (Room room : board.getRooms()) {
      if(room == r) continue;
      deck.add(room);
    }

    for (Weapon weapon : board.getWeapons()) {
      if(weapon == w) continue;
      deck.add(weapon);
    }

    for (CharacterCard character : board.getCharacters()) {
      if(character.getName().equals(c.getName()))
        continue; // add all non-murder character to the deck.
      deck.add(character);
    }
  }

  /**
   * Deal the cards to each player until deck is empty.
   */
  private void dealCards(){
    while(true)
      for (Player player : players) {
        Collections.shuffle(deck); // shuffle the cards
        player.addCard(deck.remove(deck.size() - 1)); // player is dealt one shuffled card at each loop until deck is empty
        if(deck.isEmpty()) return;
      }
  }



  public String toString(){
    String s = "";
    s += board.toString() + '\n';
    for (Player player : players) {
      s += player.toString() + '\n';
    }
    s += "Current suggestion: " + (suggestion != null ? suggestion.toString() : "null") + '\n';
    s += "Current accusation: " + (accusation != null ? accusation.toString() : "null") + '\n';
    s += "Possible Solution: " + suspects + '\n';
    return s;
  }

  /**
   * Initialises GUI variables
   */
  public void setupGUI(){
    GUI.setTiles(board.getTiles());
    GUI.setupSearch();
    GUI.setRooms(board.getRooms());
    GUI.setWeapons(board.getWeapons());
    GUI.setCharacterCards(board.getCharacters());
    GUI.setPlayers(players);
    GUI.loadAssets();
  }


  /**
   * Starts the game, and is terminated when the game ends.
   */
  public void play(){
    while(true) {
      playerTurn();
    }
  }



  public static void main(String[] args){
    GUI = new GUI(frame); //initialises the GUI
    new CleudoGame().play();
  }

}