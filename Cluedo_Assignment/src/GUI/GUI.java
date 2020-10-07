package GUI;

import CluedoMain.CleudoGame;
import GameInterior.*;
import GameInterior.Tiles.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;


public class GUI extends JPanel {

    // GLOBAL VARIABLES - passed in by CleudoMain.java
    private Player[] players;
    private Tile[][] tiles;
    private List<Room> rooms;
    private Weapon[] weapons;
    private CharacterCard[] charactersList;

    // MAIN WINDOW FIELDS
    private JMenuBar menuBar;
    private JMenuItem quit;
    private JPanel portrait = new JPanel();
    private JLabel portraitLabel = new JLabel("");
    private Canvas mainCanvas = new Canvas();
    private JButton rollSuggest = new JButton("Roll");
    private JButton accuse = new JButton("Accuse");
    private JButton viewHand = new JButton("View Hand");
    private JTextArea textArea = new JTextArea();
    private JLayeredPane topPanel = new JLayeredPane();

    // DICE AREA FIELDS
    private JPanel dices = new JPanel(new GridLayout(2, 2));
    private JLabel diceLabel1 = new JLabel("");
    private JLabel diceLabel2 = new JLabel("");
    private JLabel movesLeftMarker = new JLabel("");
    private JLabel moves = new JLabel("");
    // dice values
    public int dice1, dice2;
    private int d1 = 1, d2 = 1;

    // UTILITY FIELDS
    private Player currentPlayer;
    private int movesLeft = -1;
    private Tile prevTile = null;
    private Tile highlight = null;
    public String action = "";

    // MOVEMENT FIELDS
    private ArrayList<Tile> moveStack = new ArrayList<>();
    private ArrayList<Tile> prevMoves = new ArrayList<>();

    // global int for tile size
    private int square = 20;

    // Radio button initialisation for Character select state
    private static final String[] playerNum = {"3 Players", "4 Players", "5 Players", "6 Players"}; // initalises the names for the player count selection
    private JRadioButton[] pRadios = new JRadioButton[playerNum.length];
    private static final String[] characterNames = {"Miss Scarlett", "Colonel Mustard", "Mr. Green", "Dr. Orchid", "Professor Plum", "Mrs. Peacock"}; // initialises names of characters
    private JRadioButton[] radios = new JRadioButton[characterNames.length];

    private static final String[] weaponNames = {"Candlestick", "Dagger", "Lead Pipe", "Revolver", "Rope", "Spanner"};
    private static final String[] roomNames = {"Study", "Hall", "Lounge", "Library", "Billiard Room", "Dining Room", "Conservatory", "Ball Room", "Kitchen"};

    // Mouse position fields
    private int mouseX = 0;
    private int mouseY = 0;

    // A* search initialisation
    public AStarSearch search = new AStarSearch();

    // asset path initialisation
    File path = new File("src/assets/"); //source path for assets

    /**
     * Main GUI constructor
     * Sets up all the core buttons and the canvas. Uses a GridBagLayout and initialises mouse and key listeners.
     */
    public GUI(JFrame frame) {
        // Create and specify a layout manager
        frame.setResizable(false);
        frame.setTitle("CLUEDO");
        ImageIcon icon = new ImageIcon("src/assets/icon.png");
        frame.setIconImage(icon.getImage());

        frame.setMinimumSize(new Dimension(700, 730));
        this.setLayout(new GridBagLayout());
        // Create a constraints object, and specify some default values
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH; // components grow in both dimensions
        c.insets = new Insets(5, 5, 5, 5); // 5-pixel margins on all sides
        topPanel.setVisible(true);
        topPanel.setOpaque(true);

        // Create and add a bunch of buttons, specifying different grid
        // position, and size for each.

        this.menuBar = new JMenuBar();
        this.quit = new JMenuItem("Quit Cluedo");
        this.quit.addActionListener((e) -> {
            int button = 0;
            int result = JOptionPane.showConfirmDialog((Component)null, "Are you sure you want to quit? :(", "HOLD ON!", button);
            if (result == 0) {
                System.exit(0);
            }

        });
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.menuBar.add(this.quit);
        this.menuBar.setLayout(new GridBagLayout());
        JMenuItem notes = new JMenuItem("View Notes");
        notes.addActionListener((e) -> {
            action = "Take Notes";
        });
        this.menuBar.add(notes);

        // Sets up the canvas
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 4;
        c.gridheight = 4;
        c.weightx = c.weighty = 0.0;
        mainCanvas.setSize(new Dimension(481, 501));
        mainCanvas.setBackground(Color.gray);
        this.add(mainCanvas, c);

        //sets up portrait
        c.gridx = 4;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;

        portrait.add(portraitLabel);
        portrait.setSize(185, 185);
        this.add(portrait, c);
        setPortrait();

        //sets up buttons
        c.gridx = 4;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = c.weighty = 1;
        JPanel actionButtons = new JPanel();
        actionButtons.setLayout(new GridLayout(3, 1));
        actionButtons.add(rollSuggest);
        actionButtons.add(accuse);
        actionButtons.add(viewHand);
        this.add(actionButtons, c);

        //sets up text field
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 3;
        c.gridheight = 1;
        c.weightx = c.weighty = 1;
        textArea.setFont(new Font("Monospaced",Font.BOLD,16));
        textArea.setMinimumSize(new Dimension(683, 200));
        textArea.setText("Selecting Characters");
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        this.add(textArea, c);

        //sets up dice view area
        c.gridx = 4;
        c.gridy = 4;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.ipadx = 0;
        c.ipady = 0;
        c.weightx = 0;
        c.weighty = 0;
        dices.setMinimumSize(new Dimension(153, 153));
        dices.setMinimumSize(new Dimension(153, 153));
        dices.setBackground(new Color(238, 238, 238));
        this.add(dices, c);

        diceLabel1.setIcon(new ImageIcon("src/assets/dice_"+ d1 + ".png"));
        diceLabel2.setIcon(new ImageIcon("src/assets/dice_"+ d2 + ".png"));

        movesLeftMarker.setIcon(new ImageIcon("src/assets/moves/no_moves_left.png"));
        moves.setIcon(new ImageIcon("src/assets/moves/moves_blank.png"));

        dices.add(diceLabel1);
        dices.add(movesLeftMarker);
        dices.add(diceLabel2);
        dices.add(moves);

        dices.setSize(180, 180);
        dices.setVisible(true);
        frame.add(this);
        createDices(0, 0);

        //allows for closing window using 'X'
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.getGlassPane().setVisible(true);

        //finalises JFrame
        frame.setJMenuBar(this.menuBar);
        frame.setContentPane(this);
        this.add(topPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


        //========================
        // BUTTON/MOUSE ACTIONS
        //========================
        mainCanvas.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {/*unused*/}

            @Override
            public void mousePressed(MouseEvent e) {/*unused*/}

            // Character movement using A* search/movement highlighting
            @Override
            public void mouseReleased(MouseEvent e) {
                Tile t = findTile(e.getX(), e.getY(), 1);
                if(moveStack.contains(t)) {
                    for (Tile m : moveStack) {
                        currentPlayer.setTile(m, currentPlayer.getTile());
                        m.setMovable(false);
                        prevMoves.add(m);
                        if (m instanceof RoomTile) {
                            continue;
                        } else {
                            movesLeft--;
                        }
                        if (movesLeft == 0 || m == t) break;
                    }
                }
                if(movesLeft == 0) {
                    prevMoves = new ArrayList<>();
                    resetTiles();
                }
                createDices(dice1, dice2);
                update(mainCanvas.getGraphics());
                updateGraphics();
            }

            @Override
            public void mouseEntered(MouseEvent e) {/*unused*/}

            @Override
            public void mouseExited(MouseEvent e) {/*unused*/}
        });
        // Detects and finds tiles on the canvas - is used for tooltips and A* search
        mainCanvas.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {/*unused*/}

            @Override
            public void mouseMoved(MouseEvent e) {
                if(tiles != null) {
                    resetTiles();
                    if (currentPlayer != null && tiles != null) {
                        mouseX = e.getX();
                        mouseY = e.getY();
                        Tile t = findTile(e.getX(), e.getY(), 0);
                        if (t != null) {
                            highlight = t;
                            update(mainCanvas.getGraphics());
                        }
                    }
                }
            }
        });

        mainCanvas.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {/*unused*/}

            @Override
            public void keyPressed(KeyEvent e) {/*unused*/}

            //===========================
            // DIRECTIONAL CHAR MOVEMENT
            //===========================
            @Override
            public void keyReleased(KeyEvent e) {
                Tile currentTile = currentPlayer.getTile();
                int x = currentTile.getX();
                int y = currentTile.getY();

                if(x >= 0 && x < 24 && y >= 0 && y < 25 && movesLeft > 0){
                    if (e.getKeyChar() == 'w') {
                        action = "Move w";
                    }
                    if (e.getKeyChar() == 's') {
                        action = "Move s";
                    }
                    if (e.getKeyChar() == 'a') {
                        action = "Move a";
                    }
                    if (e.getKeyChar() == 'd') {
                        action = "Move d";
                    }
                }
            }
        });

        //Button listeners
        rollSuggest.addActionListener(e -> {
            if(rollSuggest.getText().equals("Roll")){
                action = "Roll";
            }else if(rollSuggest.getText().equals("Suggest")){
                action = "Suggest";
            }else{
                action = "End Turn";
                moveStack.clear();
            }
        });
        accuse.addActionListener(e -> this.action = "Accuse");
        viewHand.addActionListener(e -> this.action = "View Hand");
    }

    /**
     * Is called initially to set the portrait to default (when starting game)
     * To be called each time a new player turn starts.
     */
    public void setPortrait(){
        if(currentPlayer != null) {
            portraitLabel.setIcon(new ImageIcon(currentPlayer.getPortrait()));
            switch (currentPlayer.getCharacter().getName()) {
                case "Miss Scarlett":
                    portrait.setBackground(new Color(225, 73, 71));
                    break;
                case "Colonel Mustard":
                    portrait.setBackground(new Color(217, 215, 45));
                    break;
                case "Dr. Orchid":
                    portrait.setBackground(new Color(172, 92, 164));
                    break;
                case "Professor Plum":
                    portrait.setBackground(new Color(123, 53, 138));
                    break;
                case "Mr. Green":
                    portrait.setBackground(new Color(57, 157, 53));
                    break;
                case "Mrs. Peacock":
                    portrait.setBackground(new Color(72, 73, 202));
                    break;
            }
        }
        else{
            portraitLabel.setIcon(new ImageIcon("src/assets/default.png"));
            portrait.setBackground(Color.gray);
        }
        action = "";
    }

    //=======================
    // DICE DISPLAY
    //=======================

    /**
     * Displays the assets for the dice display.
     * @param d1 Dice number 1 - -1 if there are no moves left - will gray out the 'moves left' icon
     * @param d2 Dice number 2 - -1 to put the dice roll area into default state.
     */
    public void createDices(int d1, int d2) {

        if(d1 == -1 || d2 == -1){
            if(d1 == -1) movesLeftMarker.setIcon(new ImageIcon("src/assets/moves/no_moves_left.png"));
            if(d2 == -1){
                diceLabel1.setIcon(new ImageIcon("src/assets/dice_0.png"));
                diceLabel2.setIcon(new ImageIcon("src/assets/dice_0.png"));
                moves.setIcon(new ImageIcon("src/assets/moves/moves_blank.png"));
            }
        }else {
            if (currentPlayer == null) {
                diceLabel1.setIcon(new ImageIcon("src/assets/dice_" + d1 + ".png"));
                diceLabel2.setIcon(new ImageIcon("src/assets/dice_" + d2 + ".png"));

            } else {
                diceLabel1.setIcon(new ImageIcon("src/assets/dice_" + d1 + ".png"));
                diceLabel2.setIcon(new ImageIcon("src/assets/dice_" + d2 + ".png"));
                if (currentPlayer.getHasRolled()) {
                    movesLeftMarker.setIcon(new ImageIcon("src/assets/moves/moves_left.png"));
                    moves.setIcon(new ImageIcon("src/assets/moves/moves_" + movesLeft + ".png"));
                }
                else movesLeftMarker.setIcon(new ImageIcon("src/assets/moves/no_moves_left.png"));
            }
        }
        action = "";
    }

    /**
     * Used by CluedoGame.java to change the dice for every move (for directional key movement only)
     * @param m
     */
    public void updateMoves(int m){
        moves.setIcon(new ImageIcon("src/assets/moves/moves_"+m+".png"));
    }

    /**
     * Changes the text, and therefore the function of the button
     * @param action state of the button
     */
    public void setRollSuggestEndTurn(String action){
        switch (action) {
            case "Roll":
            case "Suggest":
            case "End Turn":
                rollSuggest.setText(action);
                break;
        }
        //sets the roll button state
        if (action.equals("Roll") && movesLeft > -1){
            rollSuggest.setEnabled(false);
        }else{
            rollSuggest.setEnabled(true);
        }
    }

    //calls the initialisation steps - creates a dialog for how many players
    public int playerNumPopup(){
        JDialog frame = new JDialog((Frame) null, "MC Immovable"); //makes the dialog unmovable
        JPanel panel = new JPanel();
        ButtonGroup bg = new ButtonGroup();

        frame.setUndecorated(true);
        frame.setPreferredSize(new Dimension(400, 400));

        panel.setSize(400,400);
        panel.add(new JLabel("Select how many players"));
        panel.setLayout(new FlowLayout());

        JButton next = new JButton("Next");

        //creating and adding radio buttons to the panel
        for(int i = 0 ; i < pRadios.length; i ++){
            pRadios[i] = new JRadioButton(playerNum[i]);
            bg.add(pRadios[i]);
            panel.add(pRadios[i]);
        }

        panel.add(next);

        frame.setAlwaysOnTop (true);
        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        final int[] num = new int[1];

        //will continue to wait for an input - will not break if nothing is selected
        while(num[0] == 0) {
            next.addActionListener(e -> {
                if (pRadios[0].isSelected()) num[0] = 3;
                else if (pRadios[1].isSelected()) num[0] = 4;
                else if (pRadios[2].isSelected()) num[0] = 5;
                else if (pRadios[3].isSelected()) num[0] = 6;
            });
            updateGraphics();
        }

        frame.dispose();
        return num[0];
    }

    /**
     * Shows an unmovable dialog box that requires the user to select which character they want to play (based on the name - currently
     * @param players List of all players currently playing
     */
    public void playerCharPopup(Player[] players){
        JDialog frame = new JDialog((Frame) null, "MC Immovable"); //makes the dialog unmovable

        JLabel title = new JLabel("");
        JPanel panel = new JPanel();

        ButtonGroup playerNumSelect = new ButtonGroup();

        frame.setUndecorated(true);
        frame.setPreferredSize(new Dimension(400, 400));
        panel.setSize(400,400);
        panel.add(title);

        JButton next = new JButton("Next");

        //creating and adding radio buttons to the panel
        for (int i = 0; i < radios.length; i++) {
            radios[i] = new JRadioButton(characterNames[i]);
            radios[i].setActionCommand(characterNames[i]);
            playerNumSelect.add(radios[i]);
            panel.add(radios[i]);
        }

        panel.setLayout(new FlowLayout());

        panel.add(next);

        frame.setAlwaysOnTop (true);
        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        for (Player p : players) {
            title.setText(p.getName() + ", select your character.");

            final boolean[] actionPerformed = {false};

            //will continue to wait for an input - will not break if nothing is selected
            while (!actionPerformed[0]) {
                next.addActionListener(e -> {
                    for (JRadioButton r : radios) {
                        if (r.isSelected() && r.isEnabled()) {
                            p.setCharacter(new CharacterCard(r.getActionCommand()));
                            r.setSelected(false);
                            r.setEnabled(false);
                            actionPerformed[0] = true;
                        }
                    }
                });
                updateGraphics();
            }
        }

        frame.dispose();
    }

    // Provides selection for three cards - Character, Weapon, Room
    public void accusePopup(){
        JDialog frame = new JDialog((Frame) null, "MC Immovable"); //makes the dialog unmovable
        this.textArea.setText("Accusing...");
        JLabel title = new JLabel("Select the conditions of the murder.");
        JPanel panel = new JPanel();

        frame.setUndecorated(true);
        frame.setPreferredSize(new Dimension(400, 400));
        panel.setSize(400,400);
        panel.add(title);
        panel.setVisible(true);

        JButton next = new JButton("Next");
        JButton cancel = new JButton("Cancel");

        //creating and adding radio buttons to the panel
        JComboBox<String> roomList = new JComboBox<>();
        roomList.addItem("ROOMS");
        for(String r : roomNames){
            roomList.addItem(r);
        }
        JComboBox<String> weaponList = new JComboBox<>();
        weaponList.addItem("WEAPONS");
        for(String w : weaponNames){
            weaponList.addItem(w);
        }
        JComboBox<String> characterList = new JComboBox<>();
        characterList.addItem("CHARACTERS");
        for(String c : characterNames){
            characterList.addItem(c);
        }

        panel.add(roomList);
        panel.add(weaponList);
        panel.add(characterList);

        panel.setLayout(new FlowLayout());

        panel.add(next);
        panel.add(cancel);

        final String[] selection = new String[3];

        frame.setAlwaysOnTop (true);
        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        roomList.addItemListener(e -> {
            if(e.getSource() == roomList){
                selection[0] = (String)roomList.getSelectedItem();
            }
        });
        weaponList.addItemListener(e -> {
            if(e.getSource() == weaponList){

                selection[1] = (String)weaponList.getSelectedItem();
            }
        });
        characterList.addItemListener(e -> {
            if(e.getSource() == characterList){

                selection[2] = (String)characterList.getSelectedItem();
            }
        });

        //loops within the popup to pause the main game loop - breaks when a selection has been made or cancelled
        final boolean[] actionPerformed = {false};
        while(!actionPerformed[0]) {
            next.addActionListener(e -> {
                if (roomList.getSelectedIndex() != 0 && weaponList.getSelectedIndex() != 0 && characterList.getSelectedIndex() != 0) {
                    Weapon weapon = null;
                    Room room = null;
                    CharacterCard character = null;

                    for (Room r : rooms) {
                        if (r.getName().equals(selection[0])) {
                            room = r;
                        }
                    }
                    for (Weapon w : weapons) {
                        if (w.getName().equals(selection[1])) {
                            weapon = w;
                        }
                    }
                    for (CharacterCard c : charactersList) {
                        if (c.getName().equals(selection[2])) {
                            character = c;
                        }
                    }
                    currentPlayer.setAccusation(new CardTuple(room, weapon, character));
                    currentPlayer.setMadeAccusation(true);
                    action = "";
                    actionPerformed[0] = true;
                }
            });
            cancel.addActionListener(e -> {
                action = "Cancel";
                actionPerformed[0] = true;
            });
            updateGraphics();
        }
        frame.dispose();
    }

    // provides selections for Character and Weapon
    public void suggestPopup(){
        JDialog frame = new JDialog((Frame) null, "MC Immovable"); //makes the dialog unmovable
        this.textArea.setText("Suggesting...");
        JLabel title = new JLabel("Make your suggestion");
        JLabel currentRoom = new JLabel("You are in the " + currentPlayer.getTile().getRoom().getName() + ".");
        JPanel panel = new JPanel();

        frame.setUndecorated(true);
        frame.setPreferredSize(new Dimension(400, 400));
        panel.setSize(400,400);
        panel.setVisible(true);

        JButton confirm = new JButton("Confirm");
        JButton cancel = new JButton("Cancel");

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new FlowLayout());
        gridPanel.setMinimumSize(new Dimension(100, 100));

        JComboBox<String> characterList = new JComboBox<String>();
        characterList.addItem("CHARACTERS");
        for(String c : characterNames){
            characterList.addItem(c);
        }
        JComboBox<String> weaponList = new JComboBox<String>();
        weaponList.addItem("WEAPONS");
        for(String w : weaponNames){
            weaponList.addItem(w);
        }

        gridPanel.add(weaponList);
        gridPanel.add(characterList);

        final String[] selection = new String[2];

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(confirm);
        buttonPanel.add(cancel);

        panel.add(title);
        panel.add(currentRoom);
        panel.add(gridPanel);
        panel.add(buttonPanel);

        frame.setAlwaysOnTop (true);
        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        weaponList.addItemListener(evt -> {
            if(evt.getSource() == weaponList) {
                selection[0] = (String) weaponList.getSelectedItem();
            }
        });
        characterList.addItemListener(evt -> {
            if(evt.getSource() == characterList) {
                selection[1] = (String) characterList.getSelectedItem();
            }
        });

        //loops within the popup to pause the main game loop - breaks when a selection has been made or cancelled
        final boolean[] actionPerformed = {false};
        while(!actionPerformed[0]) {
            confirm.addActionListener(e -> {
                if (weaponList.getSelectedIndex() != 0 && characterList.getSelectedIndex() != 0) {
                    Weapon weapon = null;
                    Room room = currentPlayer.getTile().getRoom();
                    CharacterCard character = null;
                    for (Weapon w : weapons) {
                        if (w.getName().equals(selection[0])) {
                            weapon = w;
                        }
                    }
                    for (CharacterCard c : charactersList) {
                        if (c.getName().equals(selection[1])) {
                            character = c;
                        }
                    }
                    currentPlayer.setSuggestion(new CardTuple(room, weapon, character));
                    currentPlayer.setMadeSuggestion(true);
                    actionPerformed[0] = true;
                }
            });
            cancel.addActionListener(e -> {
                action = "Cancel";
                actionPerformed[0] = true;
            });
            updateGraphics();
        }
        frame.dispose();
    }

    /**
     * Allows the next valid player to refute the suggestion with a card from their hand.
     * @param player The next immediate player that has at least one card in the suggestion
     * @param suggestion CardTuple containing the suggestion elements
     * @return
     */
    public Card refute(Player player, CardTuple suggestion){

        JDialog frame = new JDialog((Frame) null, "MC Immovable"); //makes the dialog unmovable
        this.textArea.setText("Refuting...");
        JLabel title = new JLabel(player.getName() + "'s Cards");
        JPanel panel = new JPanel();
        JLabel card = new JLabel(new ImageIcon("src/assets/default_card.png"));

        frame.setUndecorated(true);
        frame.setPreferredSize(new Dimension(400, 400));
        Container pane = frame.getContentPane();
        pane.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setSize(400,400);
        panel.setVisible(true);

        JLabel[] cards = new JLabel[currentPlayer.getCards().size()];

        JButton refute = new JButton("Refute Suggestion");

        ButtonGroup radioGroup = new ButtonGroup();

        JRadioButton one = new JRadioButton("1");
        JRadioButton two = new JRadioButton("2");
        JRadioButton three = new JRadioButton("3");
        radioGroup.add(one);
        radioGroup.add(two);
        radioGroup.add(three);
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0;
        pane.add(title, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1;
        gbc.weighty = 0;
        pane.add(one, gbc);
        gbc.gridy = 3;
        pane.add(two, gbc);
        gbc.gridy = 4;
        pane.add(three, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0;
        pane.add(card, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.SOUTH;
        pane.add(refute, gbc);
        one.setVisible(false);
        two.setVisible(false);
        three.setVisible(false);
        String[] selected = {""};


        Room suggestedRoom = suggestion.getRoom();
        Weapon suggestWeapon = suggestion.getWeapon();
        CharacterCard suggestedCharacter = suggestion.getCharacter();

        for(Card c : player.getCards()){
            if(c == suggestedRoom || c == suggestWeapon || c == suggestedCharacter){
                if(one.getText().equals("1")) {
                    one.setText(c.getName());
                    one.setVisible(true);
                }
                else if(two.getText().equals("2")){
                    two.setText(c.getName());
                    two.setVisible(true);
                }
                else if(three.getText().equals("3")){
                    three.setText(c.getName());
                    three.setVisible(true);
                }
            }
        }

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        refute.setAlignmentX(Component.CENTER_ALIGNMENT);

        frame.setAlwaysOnTop (true);
        frame.setContentPane(pane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        final Card[] refuteCard = new Card[1];
        //loops within the popup to pause the main game loop - breaks when a selection has been made or cancelled
        final boolean[] actionPerformed = {false};
        while(!actionPerformed[0]) {

            one.addActionListener(e -> {
                selected[0] = one.getText();
                card.setIcon(new ImageIcon("src/assets/" + one.getText() + ".png"));
            });
            two.addActionListener(e -> {
                selected[0] = two.getText();
                card.setIcon(new ImageIcon("src/assets/" + two.getText() + ".png"));
            });
            three.addActionListener(e -> {
                selected[0] = three.getText();
                card.setIcon(new ImageIcon("src/assets/" + three.getText() + ".png"));
            });

            refute.addActionListener(e -> {

                if(!selected[0].equals("")){
                    for(Card c : player.getCards()){
                        if(c.getName().equals(selected[0])) refuteCard[0] = c;
                        action = "";
                        actionPerformed[0] = true;
                    }
                }
            });
            updateGraphics();
        }

        frame.dispose();
        return refuteCard[0];
    }

    /**
     * HAND VIEW - displays you hand
     */
    public void viewHandPopup() {
        this.textArea.setText("Viewing Hand...");
        JDialog frame = new JDialog((Frame)null, "MC Immovable");
        JLabel title = new JLabel("Your Cards");
        JPanel panel = new JPanel();
        frame.setUndecorated(true);
        frame.setPreferredSize(new Dimension(400, 400));
        panel.setSize(400, 400);
        panel.setVisible(true);
        JLabel[] cards = new JLabel[this.currentPlayer.getCards().size()];

        for(int i = 0; i < cards.length; ++i) {
            cards[i] = new JLabel(new ImageIcon("src/assets/" + (this.currentPlayer.getCards().get(i)).getName() + ".png"));
        }

        JButton cancel = new JButton("Close");
        panel.setLayout(new BoxLayout(panel, 1));
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new FlowLayout());
        gridPanel.setMinimumSize(new Dimension(100, 100));
        JLabel[] var10 = cards;
        int var9 = cards.length;

        for(int var8 = 0; var8 < var9; ++var8) {
            JLabel c = var10[var8];
            if (c != null) {
                gridPanel.add(c);
            }
        }

        panel.add(title);
        panel.add(gridPanel);
        panel.add(cancel);
        frame.setAlwaysOnTop(true);
        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(2);
        frame.setVisible(true);
        boolean[] actionPerformed = new boolean[1];

        while(!actionPerformed[0]) {
            cancel.addActionListener((e) -> {
                this.action = "Cancel";
                actionPerformed[0] = true;
            });
            this.updateGraphics();
        }

        frame.dispose();
    }

    /**
     * Allows each player to take notes (such as what cards have been seen etc)
     */
    public void takeNotes() {
        action = "unfocus";
        if (this.players != null && this.players.length != 0) {
            GridBagConstraints c = new GridBagConstraints();
            c.weightx = 2.0D;
            c.weighty = 1.0D;
            c.fill = 2;
            JDialog noteFrame = new JDialog((Frame)null);
            JLabel title = new JLabel("Your Notes");
            JPanel panel = new JPanel();
            noteFrame.setPreferredSize(new Dimension(200, 500));
            panel.setSize(100, 300);
            panel.setVisible(true);
            JButton cancel = new JButton("I'm done taking / look at my notes!");
            panel.setLayout(new BoxLayout(panel, 1));
            Player p = this.currentPlayer;
            StringBuilder notes = new StringBuilder();
            notes.append("<html>");
            notes.append(p.getNotes());
            notes.append("</html>");
            JLabel prevNotes = new JLabel(notes.toString(), 0);
            JTextField textField = new JTextField("Please enter your notes here!", 0);
            textField.addActionListener((e) -> {
                String notesWritten = textField.getText();
                p.setNotes(notesWritten);
                textField.setText("");
                StringBuilder sb = new StringBuilder();
                sb.append("<html>");
                sb.append(p.getNotes());
                sb.append("</html>");
                prevNotes.setText(sb.toString());
            });
            cancel.addActionListener((e) -> {
                noteFrame.dispose();
            });
            textField.selectAll();
            panel.add(title);
            panel.add(prevNotes);
            panel.add(textField);
            panel.add(cancel);
            noteFrame.setAlwaysOnTop(true);
            noteFrame.setContentPane(panel);
            noteFrame.pack();
            noteFrame.setLocationRelativeTo((Component)null);
            noteFrame.setDefaultCloseOperation(2);
            noteFrame.setVisible(true);
        }
    }

    /**
     * Displays a popup when a player has lost
     * @param solution Murder solution to be displayed
     */
    public void playerLostPopup(CardTuple solution) {
        this.textArea.setText("You accused incorrectly. You are out of the game. Sorry!");
        JDialog frame = new JDialog((Frame)null, "MC Immovable");
        JLabel title = new JLabel("Here's the solution. Don't tell anyone!");
        JPanel panel = new JPanel();
        frame.setUndecorated(true);
        frame.setPreferredSize(new Dimension(400, 275));
        panel.setSize(400, 275);
        panel.setVisible(true);
        JLabel[] cards = new JLabel[solution.getCards().size()];

        for(int i = 0; i < cards.length; ++i) {
            cards[i] = new JLabel(new ImageIcon("src/assets/" + (solution.getCards().get(i)).getName() + ".png"));
        }

        JButton cancel = new JButton("Close");
        panel.setLayout(new BoxLayout(panel, 1));
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new FlowLayout());
        gridPanel.setMinimumSize(new Dimension(100, 100));
        JLabel[] var10 = cards;
        int var9 = cards.length;

        for(int var8 = 0; var8 < var9; ++var8) {
            JLabel c = var10[var8];
            if (c != null) {
                gridPanel.add(c);
            }
        }

        panel.add(title);
        panel.add(gridPanel);
        panel.add(cancel);
        frame.setAlwaysOnTop(true);
        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(2);
        frame.setVisible(true);
        boolean[] actionPerformed = new boolean[1];

        while(!actionPerformed[0]) {
            cancel.addActionListener((e) -> {
                this.action = "Cancel";
                actionPerformed[0] = true;
            });
            this.updateGraphics();
        }
        frame.dispose();
    }

    /**
     * FINAL END GAME STATE
     * @param winner
     * @param solution
     * @return Always true. Will only return if 'Play Again' is chosen. Game will terminate otherwise.
     */
    public boolean gameEndPopup(Player winner, CardTuple solution){
    	this.textArea.setText("Game Over...");
        JDialog frame = new JDialog((Frame)null, "MC Immovable");
        JLabel title = new JLabel("Unfortunately nobody got the solution. Nobody wins...");
        JLabel cont = new JLabel("This was the solution.");
        if(winner != null) {
        	title = new JLabel(this.currentPlayer.getName() + " Wins!!");
        	cont = new JLabel("\nHere's the solution in case anyone was wondering!");
        }
        JPanel panel = new JPanel();
        frame.setUndecorated(true);
        frame.setPreferredSize(new Dimension(400, 275));
        panel.setSize(400, 275);
        panel.setVisible(true);
        JLabel[] cards = new JLabel[solution.getCards().size()];

        for(int i = 0; i < cards.length; ++i) {
            cards[i] = new JLabel(new ImageIcon("src/assets/" + ((Card)solution.getCards().get(i)).getName() + ".png"));
        }

        JButton cancel = new JButton("Exit");
        JButton retry = new JButton("Play Again");
        panel.setLayout(new BoxLayout(panel, 1));
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new FlowLayout());
        gridPanel.setMinimumSize(new Dimension(100, 100));
        JLabel[] var10 = cards;
        int var9 = cards.length;

        for(int var8 = 0; var8 < var9; ++var8) {
            JLabel c = var10[var8];
            if (c != null) {
                gridPanel.add(c);
            }
        }

        panel.add(title);
        panel.add(cont);
        panel.add(gridPanel);
        panel.add(cancel);
        panel.add(retry);
        frame.setAlwaysOnTop(true);
        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo((Component)null);
        frame.setDefaultCloseOperation(2);
        frame.setVisible(true);
        boolean[] actionPerformed = new boolean[1];

        while(!actionPerformed[0]) {
            cancel.addActionListener((e) -> {
                this.action = "Exit";
                actionPerformed[0] = true;
                System.exit(0);
            });
            retry.addActionListener((e) -> {
                this.action = "Play Again";
                actionPerformed[0] = true;
            });
        }
        frame.dispose();
        return true;
    }

    //=======================
    // DRAW FUNCTIONS
    //=======================

    private Image doubleBuffer;

    /**
     * Is called to buffer the graphics pane. This will allow the game to not flicker
     * @param g The graphics object for the Graphics pane
     */
    public void update(Graphics g) {
        Dimension size = getSize();
        if (doubleBuffer == null ||
            doubleBuffer.getWidth(this) != size.width ||
            doubleBuffer.getHeight(this) != size.height)
        {
            doubleBuffer = createImage(size.width, size.height);
        }
        if (doubleBuffer != null) {
            // paint to double buffer
            Graphics g2 = doubleBuffer.getGraphics();
            draw(g2);
            g2.dispose();
            // copy double buffer to screen
            g.drawImage(doubleBuffer, 0, 0, null);
        }
        else {
            // couldn't create double buffer, just paint to screen
            draw(g);
        }
    }

    /**
     * DO NOT call this directly. Call update() to allow double buffering
     * @param g
     */
    public void draw(Graphics g){
        drawTiles(g);
        drawPath(g);
        drawWalls(g);
        drawPlayers(g);
        drawToolTips(g);
        drawWeapons(g);
    }

    // Draws all board tiles room/corridor graphics
    public void drawTiles(Graphics g){
        Tile tile;
        for (int row = 0; row <= 23; ++row) {
            for (int col = 0; col <= 24; ++col) {
                //these can be changed to put in images
                //will need to add the images to the asset loader
                tile = tiles[col][row];
                if(tile instanceof EmptyTile) {
                    g.setColor(Color.gray);
                    g.fillRect(row * square, col * square, square, square);
                }else{
                    g.drawImage(tile.getImage(), row*square, col*square, square, square, null);
                }
                g.setColor(Color.black);
                g.drawRect(row * square, col * square, square, square);

            }
        }
    }

    // Uses tile data to manually display a tooltip regarding what is on the map
    public void drawToolTips(Graphics g){
        if(highlight != null){
            int textWidth = 0;
            int textHeight = 0;
            int toolTipXOffset = 15;
            int toolTipYOffset = 15;
            int textXOffset = 18;
            int textYOffset = 28;
            String name = "";
            Font font = new Font("Consolas", Font.PLAIN, 12);
            if(highlight instanceof RoomTile || highlight instanceof Corridor){
                for(Player p : players){
                    if(p.getTile() == highlight) name = p.getCharacter().getName();
                }
                for (Weapon w : weapons) {
                    if (w.getTile() == highlight)
                        name = w.getName();
                }
                if (highlight instanceof RoomTile && name.equals("")) name = highlight.getRoom().getName();
                AffineTransform affinetransform = new AffineTransform();
                FontRenderContext frc = new FontRenderContext(affinetransform,true,true);

                textWidth = (int)((font.getStringBounds(name, frc).getWidth())*1.3) ;
                textHeight = (int)((font.getStringBounds(name, frc).getHeight())*1.3);
            }
            if(!name.equals("")) {
                if (mouseX + toolTipXOffset + textWidth > 470) {
                    toolTipXOffset -= (textWidth + 22);
                    textXOffset -= textWidth+22;
                }
                if (mouseY + toolTipYOffset + textHeight > 500) {
                    toolTipYOffset -= 30;
                    textYOffset -= 30;
                }
                g.setColor(new Color(138, 169, 225));
                g.fillRect(mouseX + toolTipXOffset, mouseY + toolTipYOffset, textWidth, textHeight);
                g.setColor(Color.black);
                g.drawRect(mouseX + toolTipXOffset, mouseY + toolTipYOffset, textWidth, textHeight);
                g.setColor(Color.black);
                g.setFont(font);
                g.drawString(name, mouseX + textXOffset, mouseY + textYOffset);
            }
        }
    }

    // Uses the A* search to find and draw a path to the targeted location
    public void drawPath(Graphics g){
        if(tiles != null) {
            if ((currentPlayer != null && currentPlayer.getHasRolled()) && movesLeft > 0 && !currentPlayer.getMadeSuggestion() && highlight != null && !currentPlayer.hasEnteredRoom()) {
                List<Tile> mpath = findPath();
                // Determines how many tiles will be highlighted in green (places available to move to)
                int count;
                if(mpath != null) {
                    if (mpath.size() <= movesLeft) {
                        count = mpath.size();
                    } else {
                        count = movesLeft;
                        for(Tile p : mpath){
                            if(p instanceof RoomTile && count < mpath.size()) count++;
                            else break;
                        }
                    }
                    // Adds the tiles the player can move to to a global list - will not allow being the same room twice during a turn
                    for (int i = 0; i < count; i++) {
                        if (currentPlayer.getTile() instanceof Corridor) {
                            if (mpath.get(i) instanceof Corridor || (mpath.get(i) instanceof RoomTile && (currentPlayer.getCurrentRoom() == null || mpath.get(i).getRoom() != currentPlayer.getCurrentRoom().getRoom()))) {
                                mpath.get(i).setMovable(true);
                                moveStack.add(mpath.get(i));
                                if (mpath.get(i) instanceof RoomTile) break;
                            }
                        }
                        if (currentPlayer.getTile() instanceof RoomTile) {
                            if (mpath.get(i) instanceof RoomTile || mpath.get(i) instanceof Corridor) {
                                mpath.get(i).setMovable(true);
                                moveStack.add(mpath.get(i));
                                if (mpath.get(i) instanceof RoomTile && (currentPlayer.getCurrentRoom() == null || currentPlayer.getCurrentRoom().getRoom() != mpath.get(i).getRoom()))
                                    break;
                            }
                        }
                    }
                    // Draws the highlighted squares using custom graphics - green for available, red otherwise.
                    if (highlight instanceof RoomTile || highlight instanceof Corridor) {
                        for (Tile p : mpath) {
                            if (p.getMovable()) {
                                g.drawImage(p.getGImage(), p.getX() * square, p.getY() * square, square, square, null);
                            } else {
                                g.drawImage(p.getRImage(), p.getX() * square, p.getY() * square, square, square, null);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Draws token of all players currently in the game
     * @param g
     */
    public void drawPlayers(Graphics g){
        for(Player p : players){
            g.setColor(Color.red);
            g.drawImage(p.getToken(), p.getTile().getX()*square, p.getTile().getY()*square, null);
        }
    }

    /**
     * Draws all weapons on the map
     * @param g
     */
    public void drawWeapons(Graphics g){
        for(Weapon w : weapons){
            g.setColor(Color.red);
            g.drawImage(w.getToken(), w.getTile().getX()*square, w.getTile().getY()*square, null);
        }
    }

    /**
     * Draws a wall on each walled-RoomTile according to it's placement direction
     * @param g
     */
    public void drawWalls(Graphics g){
        Tile tile;
        int square = 20;
        for (int row = 0; row <= 23; ++row) {
            for (int col = 0; col <= 24; ++col) {
                tile = tiles[col][row];
                if (tile instanceof RoomTile && tile.hasWall()) {
                    if (tile.west) {
                        g.setColor(Color.darkGray);
                        g.fillRect(row * square, col * square, 3, square);
                    }
                    if (tile.north) {
                        g.setColor(Color.darkGray);
                        g.fillRect(row * square, col * square, square, 3);
                    }
                    if (tile.east) {
                        g.setColor(Color.darkGray);
                        g.fillRect(row * square + square - 3, col * square, 3, square);
                    }
                    if (tile.south) {
                        g.setColor(Color.darkGray);
                        g.fillRect(row * square, col * square + square - 3, square, 3);
                    }

                }
            }
        }
    }

    // Constructs a list of all tiles between the player and the mouse location.
    private List findPath(){
        List path = new ArrayList<>();
        moveStack.clear();
        if(tiles!=null && currentPlayer != null) {
            if (highlight instanceof Corridor || highlight instanceof RoomTile) {
                path = search.findPath(currentPlayer.getTile().getNode(), highlight.getNode(), currentPlayer);
            }
        }
        return path;
    }



    //=====================
    // TILE FINDER
    //=====================

    /**
     * Can be used to find a tile from an x/y coordinate on the screen
     * @param x x-value from mouse location
     * @param y y-value from mouse location
     * @return Tile that it is in that location
     */
    public Tile findTile(double x, double y, int mouseEvent){
        int square = 20;
        for(int row = 0; row < 24; ++row){
            for (int col = 0; col < 25; ++col) {
                if((x >= row*square && x <= row*square+square)&&(y >= col*square && y <= col*square+square)){
                    Tile tile = tiles[col][row];
                    if(mouseEvent == 0) {
                        if (prevTile == null) {
                            prevTile = tile;
                            return tile;
                        } else if (prevTile != tile) {
                            prevTile = tile;
                            return tile;
                        }
                    }
                    else{
                        if(tile instanceof RoomTile || tile instanceof Corridor) {
                            return tile;
                        }
                    }
                }
            }
        }
        return prevTile;
    }

    //====================================
    // GRAPHICS UPDATER
    //====================================
    // this is called on every loop during the main gui loop (not while in the popups - you need to call this inside the popup loops if you want them to show)

    public void updateGraphics(){

        BufferedImage img;
        try {
            img = ImageIO.read(new File(path, "portrait_border.png"));
            portrait.getGraphics().drawImage(img, 0, 0, null);
            img = ImageIO.read(new File(path, "dice_border.png"));
            diceLabel1.getGraphics().drawImage(img, 0, 0, null);
            diceLabel2.getGraphics().drawImage(img, 0, 0, null);
            if(rollSuggest.getText().equals("Roll")) {
                if (rollSuggest.isEnabled()) img = ImageIO.read(new File(path, "buttons/roll_dice_enabled.png"));
                else img = ImageIO.read(new File(path, "buttons/roll_dice_disabled.png"));
            }else if(rollSuggest.getText().equals("Suggest")) {
                img = ImageIO.read(new File(path, "buttons/suggest.png"));
            }else if(rollSuggest.getText().equals("End Turn")){
                img = ImageIO.read(new File(path, "/buttons/end_turn.png"));
            }
            rollSuggest.getGraphics().drawImage(img, 0, 0, null);

            img = ImageIO.read(new File(path, "buttons/accuse.png"));
            accuse.getGraphics().drawImage(img, 0, 0, null);
            img = ImageIO.read(new File(path, "buttons/view_hand.png"));
            viewHand.getGraphics().drawImage(img, 0, 0, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //=======================
    // ASSET LOADER
    //=======================

    /**
     * Is used to load assets into the game.
     */
    public void loadAssets() {
        try {
            for (int row = 0; row < 24; row++) {
                for (int col = 0; col < 25; col++) {
                    Tile tile = tiles[col][row];
                    if(!(tile instanceof EmptyTile)) {
                        if (tile instanceof Corridor) {
                            tile.setImage(ImageIO.read(new File(path, "tiles/corridor.png")));
                            tile.setGImage(ImageIO.read(new File(path, "tiles/gcorridor.png")));
                            tile.setRImage(ImageIO.read(new File(path, "tiles/rcorridor.png")));
                        }
                        else if (tile instanceof Inaccessible){
                            tile.setImage(ImageIO.read(new File(path, "tiles/inaccessible.png")));
                        }
                        else if (tile instanceof RoomTile) {
                            String name = tile.getRoom().getName();
                            if (name.equals("Ball Room")) {
                                tile.setImage(ImageIO.read(new File(path, "tiles/Ball Room.png")));
                                tile.setGImage(ImageIO.read(new File(path, "tiles/gBall Room.png")));
                                tile.setRImage(ImageIO.read(new File(path, "tiles/rBall Room.png")));
                            }else if (name.equals("Conservatory")) {
                                tile.setImage(ImageIO.read(new File(path, "tiles/Conservatory.png")));
                                tile.setGImage(ImageIO.read(new File(path, "tiles/gConservatory.png")));
                                tile.setRImage(ImageIO.read(new File(path, "tiles/rConservatory.png")));
                            }else if (name.equals("Hall")){
                                tile.setImage(ImageIO.read(new File(path, "tiles/Hall.png")));
                                tile.setGImage(ImageIO.read(new File(path, "tiles/gHall.png")));
                                tile.setRImage(ImageIO.read(new File(path, "tiles/rHall.png")));
                            }else if (name.equals("Kitchen")) {
                                tile.setImage(ImageIO.read(new File(path, "tiles/Kitchen.png")));
                                tile.setGImage(ImageIO.read(new File(path, "tiles/gKitchen.png")));
                                tile.setRImage(ImageIO.read(new File(path, "tiles/rKitchen.png")));
                            }else if (name.equals("Lounge")) {
                                tile.setImage(ImageIO.read(new File(path, "tiles/Lounge.png")));
                                tile.setGImage(ImageIO.read(new File(path, "tiles/gLounge.png")));
                                tile.setRImage(ImageIO.read(new File(path, "tiles/rLounge.png")));
                            }else if (name.equals("Study")) {
                                tile.setImage(ImageIO.read(new File(path, "tiles/Study.png")));
                                tile.setGImage(ImageIO.read(new File(path, "tiles/gStudy.png")));
                                tile.setRImage(ImageIO.read(new File(path, "tiles/rStudy.png")));
                            }else{
                                tile.setImage(ImageIO.read(new File(path, "tiles/wood.png")));
                                tile.setGImage(ImageIO.read(new File(path, "tiles/gwood.png")));
                                tile.setRImage(ImageIO.read(new File(path, "tiles/rwood.png")));
                            }
                        }
                    }
                }
            }
        }catch(Exception e){ System.out.println("Error loading asset"); }

        BufferedImage background;
        try {
            BufferedImage token;
            BufferedImage portrait;
            for (Player p : players) {
                if(p.getCharacter().getName().equals("Miss Scarlett")) {
                    token = ImageIO.read(new File(path, "miss_scarlett_token.png"));
                    portrait = ImageIO.read(new File(path, "miss_scarlett.png"));
                    p.setAssets(portrait, token);
                }
                if(p.getCharacter().getName().equals("Colonel Mustard")) {
                    token = ImageIO.read(new File(path, "colonel_mustard_token.png"));
                    portrait = ImageIO.read(new File(path, "colonel_mustard.png"));
                    p.setAssets(portrait, token);
                }
                if(p.getCharacter().getName().equals("Mr. Green")) {
                    token = ImageIO.read(new File(path, "mr_green_token.png"));
                    portrait = ImageIO.read(new File(path, "mr_green.png"));
                    p.setAssets(portrait, token);
                }
                if(p.getCharacter().getName().equals("Dr. Orchid")) {
                    token = ImageIO.read(new File(path, "dr_orchid_token.png"));
                    portrait = ImageIO.read(new File(path, "dr_orchid.png"));
                    p.setAssets(portrait, token);
                }
                if(p.getCharacter().getName().equals("Professor Plum")) {
                    token = ImageIO.read(new File(path, "professor_plum_token.png"));
                    portrait = ImageIO.read(new File(path, "professor_plum.png"));
                    p.setAssets(portrait, token);
                }
                if(p.getCharacter().getName().equals("Mrs. Peacock")) {
                    token = ImageIO.read(new File(path, "mrs_peacock_token.png"));
                    portrait = ImageIO.read(new File(path, "mrs_peacock.png"));
                    p.setAssets(portrait, token);
                }
            }
            background = ImageIO.read(new File(path, "default.png"));
        } catch (Exception e) {
            System.out.println("Cannot find asset");
        }
        
        try {
            BufferedImage token;
            for (Weapon w : weapons) {
                if(w.getName().equals("Candlestick")) {
                    token = ImageIO.read(new File(path, "candle_stick_token.png"));
                    w.setToken(token);
                }
                if(w.getName().equals("Lead Pipe")) {
                    token = ImageIO.read(new File(path, "lead_pipe_token.png"));
                    w.setToken(token);
                }
                if(w.getName().equals("Dagger")) {
                    token = ImageIO.read(new File(path, "dagger_token.png"));
                    w.setToken(token);
                }
                if(w.getName().equals("Revolver")) {
                    token = ImageIO.read(new File(path, "revolver_token.png"));
                    w.setToken(token);
                }
                if(w.getName().equals("Rope")) {
                    token = ImageIO.read(new File(path, "rope_token.png"));
                    w.setToken(token);
                }
                if(w.getName().equals("Spanner")) {
                    token = ImageIO.read(new File(path, "spanner_token.png"));
                    w.setToken(token);
                }
            }

            background = ImageIO.read(new File(path, "default.png"));
        } catch (Exception e) {
            System.out.println("Cannot find asset");
        }
        update(mainCanvas.getGraphics());
    }

    /**
     * Provides the A* search algorithm with the board tiles
     */
    public void setupSearch(){
        search.setBoard(tiles);
        for(int row = 0; row < 24; row++){
            for(int col = 0; col < 25; col++){
                Tile temp = tiles[col][row];
                temp.setNode(new TileNode(temp));
            }
        }
    }

    //======================
    // UTILITY METHODS
    //======================
    // Getters and Setters as well as other useful methods

    public void setTiles(Tile[][] tiles){
        this.tiles = tiles;
    }

    public void setPlayers(Player[] players){
        this.players = players;
    }

    public void setCurrentPlayer(Player player){
        this.currentPlayer = player;
    }

    public String getAction(){
        return action;
    }

    public void setRooms(List<Room> rooms){
        this.rooms = rooms;
    }

    public void setWeapons(Weapon[] weapons){
        this.weapons = weapons;
    }

    public void setCharacterCards(CharacterCard[] charactersList){
        this.charactersList = charactersList;
    }

    public void setCanvasFocus(){
        mainCanvas.requestFocus();
    }
    public void updateCanvas(){
        update(mainCanvas.getGraphics());
    }

    public void setMovesLeft(int movesLeft){
        this.movesLeft = movesLeft;
    }

    public int getMovesLeft(){
        return movesLeft;
    }

    public void setAction(String action){
        this.action = action;
    }

    private void resetTiles(){
        for(int row = 0; row < 24; row++){
            for(int col = 0; col < 25; col++){
                if(tiles[col][row] instanceof RoomTile) tiles[col][row].setMovable(false);
                else tiles[col][row].setMovable(false);
            }
        }
    }

    public void setText(String text){
        textArea.requestFocus();
        textArea.setText(text);
    }
}
