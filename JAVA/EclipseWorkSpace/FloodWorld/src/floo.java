import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

//represents Constants of the FloodItWorld Game
interface IConstants {
  public static final ArrayList<Color> COLORS = 
      new ArrayList<Color>(Arrays.asList(Color.red, Color.magenta, 
          Color.blue, Color.green, Color.pink, Color.yellow));
  public static final int CELL_SIZE = 20;
}


//Represents a single square of the game area
class Cell implements IConstants {
  // In logical coordinates, with the origin at the 
  //top-left corner of the screen
  int x;
  int y;
  Color color;
  boolean flooded;
  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom; 

  //Convenience Constructor for initial cells
  Cell(int x, int y, Color color) {
    this.x = x; 
    this.y = y;
    this.color = color;
    this.flooded = false;
    this.left = null;
    this.top = null;
    this.right = null;
    this.bottom = null;
  } 

  //Complete Constructor for a Cell
  Cell(int x, int y, Color color, boolean flooded, 
      Cell left, Cell top, Cell right, Cell bottom) {  
    this.x = x; 
    this.y = y;
    this.color = color;
    this.flooded = flooded;
    this.left = left;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
  } 

  //sets the given as right of this Cell
  void setRight(Cell r) {
    this.right = r;
  } 

  //sets the given as left of this Cell
  void setLeft(Cell l) {
    this.left = l;
  }

  //sets the given as bottom of this Cell
  void setBottom(Cell b) {
    this.bottom = b;  
  }

  //sets the given as top of this Cell
  void setTop(Cell t) {
    this.top = t;
  }

  //draws this Cell
  WorldImage drawCell() {
    return new RectangleImage(CELL_SIZE, CELL_SIZE, 
        OutlineMode.SOLID, this.color);
  }

  //determines if sides of this cell are the same color
  //as the given cell and if they are they flood them
  public void updateSides(Cell cell) {
    if (this.left != null && this.left.color.equals(cell.color)) {
      this.left.flooded = true;
    }
    if (this.bottom != null && this.bottom.color.equals(cell.color)) {
      this.bottom.flooded = true;
    }
    if (this.top != null && this.top.color.equals(cell.color)) {
      this.top.flooded = true;
    }
    if (this.right != null && this.right.color.equals(cell.color)) {
      this.right.flooded = true;
    }
  }
}


//represents a FloodItWorld state
class FloodItWorld extends World implements IConstants {
  ArrayList<Cell> board; 
  int boardSize; 
  Random rand;
  int clicksLeft;
  
  //creates a FloodItWorld for testing with a specified Random object
  FloodItWorld(int boardSize, Random rand) {
    this.board = new ArrayList<Cell>();
    this.boardSize = boardSize; 
    this.rand = rand;
    this.clicksLeft = 0;
    this.buildBoard();
    this.connectBoard();   
  }
  
  //creates a FloodItWorld in the real gamer
  FloodItWorld(int boardSize)  {
    this.board = new ArrayList<Cell>();
    this.boardSize = boardSize;  
    this.rand = new Random();
    this.clicksLeft = 0;
    this.buildBoard();
    this.connectBoard(); 
  } 
  
  //constructor solely to TEST buildBoard 
  FloodItWorld(Random rand) {
    this.board = new ArrayList<Cell>();
    this.boardSize = 6; 
    this.rand = rand;
    this.clicksLeft = 0;
  }

  //constant for game board
  final WorldImage GRID = new RectangleImage((this.boardSize * this.boardSize), 
      (this.boardSize * this.boardSize), OutlineMode.OUTLINE, Color.BLACK);
  
  //creates the initial array of cells for this FloodItWorld
  public void buildBoard() {
    for (int i = 0; i < this.boardSize; i++) {
      for (int j = 0; j < this.boardSize; j++) {
        this.board.add(new Cell(i * CELL_SIZE, 
            j * CELL_SIZE, this.randColor()));
      }
    }
  }
   
  //produces a random color from the Colors list
  Color randColor() {
    return COLORS.get(this.rand.nextInt(COLORS.size()));
  }
  
  //sets right, left, top, and bottom of each cell in this FloodItWorld
  void connectBoard() {  
    this.board.get(0).flooded = true;
    for (int i = 0; i < (this.boardSize * this.boardSize); i++) {      
      if (board.get(i).x > 0) {
        board.get(i).setLeft(board.get(i - this.boardSize));
      } 
      if (board.get(i).x < this.boardSize * CELL_SIZE - CELL_SIZE) {
        board.get(i).setRight(board.get(i + this.boardSize));
      }
      if (board.get(i).y > 0) {
        board.get(i).setTop(board.get(i - 1));
      }
      if (board.get(i).y < this.boardSize * CELL_SIZE - CELL_SIZE) {
        board.get(i).setBottom(board.get(i + 1));
      }
    }
  }
  
  //creates this FloodItWorld game board
  public WorldScene makeScene() {
    return this.makeSceneHelp(this.getEmptyScene());
  }

  //helps create this FloodItWorld game board
  //and determines if the game should continue
  public WorldScene makeSceneHelp(WorldScene board) {
    int totalClicks = this.boardSize * 2 + IConstants.COLORS.size();
    if (this.clicksLeft <= totalClicks && this.wholeWorldFlooded()) {
      return this.makeAFinalScene("YOU WIN!!!");
    }
    else if (this.clicksLeft > totalClicks && !this.wholeWorldFlooded()) {
      return this.makeAFinalScene("YOU LOSE!!!");
    }
    else {
      
      board.placeImageXY(this.GRID, (this.boardSize * this.boardSize) / 2, 
          (this.boardSize * this.boardSize) / 2);
      for (Cell cell : this.board) {
        board.placeImageXY(cell.drawCell(), cell.x + CELL_SIZE / 2, 
            cell.y  + CELL_SIZE / 2);
      }
      board.placeImageXY(new TextImage(Integer.toString(this.clicksLeft) + "/" +  
          Integer.toString(totalClicks), CELL_SIZE, Color.BLACK), 
          this.boardSize * CELL_SIZE / 2, (this.boardSize * CELL_SIZE) + CELL_SIZE);
      return board;
    }
  }
  
  //creates an end Scene for a win and a lose
  public WorldScene makeAFinalScene(String finalMsg) {
    WorldScene board = this.getEmptyScene();
    WorldImage grid = new RectangleImage(this.boardSize * this.boardSize * CELL_SIZE / 2, 
        this.boardSize * this.boardSize * CELL_SIZE / 2, OutlineMode.OUTLINE, Color.BLUE);
    board.placeImageXY(grid, (this.boardSize * this.boardSize) / 2, 
        (this.boardSize * this.boardSize) / 2);
    board.placeImageXY(new TextImage(finalMsg, CELL_SIZE, Color.BLACK), 
        (this.boardSize * CELL_SIZE) - CELL_SIZE, (this.boardSize * CELL_SIZE)  - (CELL_SIZE * 2));
    board.placeImageXY(new TextImage(Integer.toString(this.finalScore()), CELL_SIZE, Color.BLACK), 
        this.boardSize * CELL_SIZE - CELL_SIZE, this.boardSize *  CELL_SIZE - CELL_SIZE);
    board.placeImageXY(new TextImage("Press 'r' to play again.", CELL_SIZE, Color.BLUE), 
        this.boardSize * CELL_SIZE - CELL_SIZE, this.boardSize *  CELL_SIZE);
    return board;
  }
  
  //calculates the final score of the game
  public int finalScore() {
    int totalClicks = this.boardSize * 2 + IConstants.COLORS.size();
    if (this.clicksLeft <= totalClicks) {
      return (totalClicks - this.clicksLeft) * 20 + 1000; // + this.totalTime();
    }
    return 0;
  }
  
  //updates this FloodItWorld every tick
  public void onTick() {
    //tried iterators, recursion, making worklists,
    //and OH but could not figure out waterfall
  }
  
  //updates this world based on what key has been pressed
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      this.resetWorld();
      this.makeScene();
    }
  }
  
  //constructor that allows a restart of the game
  public void resetWorld() {
    this.board = new ArrayList<Cell>();
    this.clicksLeft = 0;
    this.buildBoard();
    this.connectBoard();
  }
  
  //updates this world based on what has been clicked
  public void onMouseClicked(Posn p) {
    if (this.clickedCell(p) != null) {
      this.floodCells(this.clickedCell(p)); 
      this.clicksLeft = this.clicksLeft + 1;
    }
  }
  
  //updates this world by changing cell color if cell 
  //is flooded and updates sides of each cell 
  //if they are the same color as the given cell
  public void floodCells(Cell cell) {
    for (int i = 0; i < this.boardSize * this.boardSize; i++) {
      Cell c = this.board.get(i);
      if (c != null && c.flooded) {
        c.color = cell.color;
        c.updateSides(cell);
      }
    }
  }
  
  //determines the cell that has been clicked
  public Cell clickedCell(Posn p) {
    Cell c = null;
    for (int i = 0; i < this.boardSize * this.boardSize; i++) {
      int xPosition = this.board.get(i).x;
      int yPosition = this.board.get(i).y;
      if (p.x >= xPosition && p.x < xPosition + IConstants.CELL_SIZE 
          && p.y >= yPosition && p.y < yPosition + IConstants.CELL_SIZE) {
        c = this.board.get(i);
      }
    }
    return c;
  }
  
  //determines if all cells are flooded
  public boolean wholeWorldFlooded() {
    boolean result = true;
    for (int i = 0; i < this.board.size(); i++) {
      if (!(this.board.get(i).flooded)) {
        result = false;
        break;
      }
    }
    return result;
  }
  
} 

//represents examples and tests
class ExamplesFloodIt {
  FloodItWorld g0;
  FloodItWorld g1;
  FloodItWorld g2;
  FloodItWorld g3;
  FloodItWorld g4;

  Cell exampleCell1;
  Cell exampleCell2;
  Cell exampleCell3; 
  Cell exampleCell4;
  Cell exampleCell5;
  Cell exampleCell6;
  WorldScene board;
  WorldImage grid;
  
  WorldScene winScene;
  WorldScene loseScene;
  WorldImage grids;
  WorldImage text1;
  WorldImage text2;
  WorldImage score;
  WorldImage restart;

  //initialization of variables method for tests
  void initData() { 
    exampleCell1 = new Cell(1, 1, Color.red);
    exampleCell2 = new Cell(0, 1, Color.yellow); 
    exampleCell3 = new Cell(2, 1, Color.magenta);
    exampleCell4 = new Cell(0, 0, Color.green); 
    exampleCell5 = new Cell(2, 2, Color.red);
    exampleCell6 = new Cell(1, 1, Color.pink, 
        false, null, null, null, null);
  
    g0 = new FloodItWorld(6, new Random(7));
    g1 = new FloodItWorld(6, new Random(5));
    g2 = new FloodItWorld(6, new Random(6));
    g3 = new FloodItWorld(new Random(8));
    g4 = new FloodItWorld(12, new Random(5));
    
    board = new WorldScene(1000,1000);
    grid = new RectangleImage(0, 0, OutlineMode.OUTLINE, Color.BLACK);
    board.placeImageXY(grid, 18, 18);
    //places each cell on the scene
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.yellow), 10, 10);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.pink), 10, 30);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.blue), 10, 50);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.blue), 10, 70);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.red), 10, 90);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.yellow), 10, 110);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.pink), 30, 10);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.yellow), 30, 30);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.blue), 30, 50);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.green), 30, 70);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.yellow), 30, 90);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.green), 30, 110);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.magenta), 50, 10);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.red), 50, 30);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.blue), 50, 50);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.pink), 50, 70);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.red), 50, 90);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.magenta), 50, 110);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.yellow), 70, 10);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.green), 70, 30);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.red), 70, 50);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.yellow), 70, 70);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.red), 70, 90);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.blue), 70, 110);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.magenta), 90, 10);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.pink), 90, 30);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.blue), 90, 50);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.green), 90, 70);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.green), 90, 90);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.blue), 90, 110);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.magenta), 110, 10);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.blue), 110, 30);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.magenta), 110, 50);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.magenta), 110, 70);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.magenta), 110, 90);
    board.placeImageXY(new RectangleImage(20, 20, "solid", Color.blue), 110, 110);
    board.placeImageXY(new TextImage("0/18", 20, Color.black), 60, 140);
    
    
    winScene = this.g1.getEmptyScene();
    grids = new RectangleImage(1000, 1000, OutlineMode.OUTLINE, Color.BLACK);
    text1 = new TextImage("YOU WIN!!!", 20, Color.BLACK);
    score = new TextImage(Integer.toString(this.g0.finalScore()), 20, Color.BLACK);
    restart = new TextImage("Press 'r' to play again.", 20, Color.BLACK);
    winScene.placeImageXY(grids, 18, 18);
    winScene.placeImageXY(text1, 18, 18);
    winScene.placeImageXY(score, 18, 38);
    winScene.placeImageXY(restart, 18, 38);
    
    loseScene = this.g0.getEmptyScene();
    text2 = new TextImage("YOU LOSE!!!", 20, Color.BLACK);
    loseScene.placeImageXY(grids, 18, 18);
    loseScene.placeImageXY(text2, 18, 18);
    loseScene.placeImageXY(score, 18, 38);
    loseScene.placeImageXY(restart, 18, 38);
  } 


  //tests the setTop method for this Cell
  void testSetTop(Tester t) { 
    this.initData(); 
  
    t.checkExpect(exampleCell3.top, null); 
    t.checkExpect(exampleCell2.top, null); 
    t.checkExpect(exampleCell5.top, null);
  
    exampleCell3.setTop(exampleCell1); 
    exampleCell2.setTop(exampleCell4); 
    exampleCell5.setTop(exampleCell3); 
 
    t.checkExpect(exampleCell3.top, exampleCell1);
    t.checkExpect(exampleCell2.top, exampleCell4);
    t.checkExpect(exampleCell5.top, exampleCell3); 
  } 

  //tests the setBottom method for this Cell
  void testSetBottom(Tester t) {
    this.initData(); 
    
    t.checkExpect(exampleCell1.bottom, null); 
    t.checkExpect(exampleCell4.bottom, null); 
    t.checkExpect(exampleCell3.bottom, null);
  
    exampleCell1.setBottom(exampleCell3); 
    exampleCell4.setBottom(exampleCell2); 
    exampleCell3.setBottom(exampleCell5); 
  
    t.checkExpect(exampleCell1.bottom, exampleCell3);
    t.checkExpect(exampleCell4.bottom, exampleCell2);
    t.checkExpect(exampleCell3.bottom, exampleCell5);
  
  } 

  //tests the setRight method for this Cell
  void testSetRight(Tester t) { 
    this.initData();
  
    t.checkExpect(exampleCell2.right, null);
    t.checkExpect(exampleCell5.right, null); 
    t.checkExpect(exampleCell4.right, null); 
  
    exampleCell2.setRight(exampleCell1); 
    exampleCell5.setRight(exampleCell4);
    exampleCell3.setRight(exampleCell2);
  
    t.checkExpect(exampleCell2.right, exampleCell1);
    t.checkExpect(exampleCell5.right, exampleCell4);
    t.checkExpect(exampleCell3.right, exampleCell2);
  } 

  //tests the setLeft method for this Cell
  void testSetLeft(Tester t) {
    this.initData();
  
    t.checkExpect(exampleCell1.right, null);
    t.checkExpect(exampleCell4.right, null); 
    t.checkExpect(exampleCell2.right, null); 
  
    exampleCell1.setRight(exampleCell2); 
    exampleCell4.setRight(exampleCell5);
    exampleCell2.setRight(exampleCell3);
  
    t.checkExpect(exampleCell1.right, exampleCell2);
    t.checkExpect(exampleCell4.right, exampleCell5);
    t.checkExpect(exampleCell2.right, exampleCell3);
  }

  //tests the drawCell method for this Cell
  boolean testDrawCell(Tester t) {
    this.initData();
    return t.checkExpect(this.exampleCell6.drawCell(), new RectangleImage(20, 20, 
        OutlineMode.SOLID, Color.pink))
        && t.checkExpect(this.exampleCell2.drawCell(), new RectangleImage(20, 20, 
            OutlineMode.SOLID, Color.yellow));
  } 
  
  //tests the updateSides method for this Cell
  void testUpdateSides(Tester t) {
    this.initData();
    t.checkExpect(this.g1.board.get(0).flooded, true);
    t.checkExpect(this.g1.board.get(1).flooded, false);
    t.checkExpect(this.g1.board.get(6).flooded, false);
    t.checkExpect(this.g1.board.get(7).flooded, false);
    this.g1.board.get(0).updateSides(this.exampleCell6);
    t.checkExpect(this.g1.board.get(0).flooded, true);
    t.checkExpect(this.g1.board.get(1).flooded, true);
    t.checkExpect(this.g1.board.get(6).flooded, true);
    t.checkExpect(this.g1.board.get(7).flooded, false);
    this.g1.board.get(6).updateSides(this.exampleCell2);
    t.checkExpect(this.g1.board.get(0).flooded, true);
    t.checkExpect(this.g1.board.get(1).flooded, true);
    t.checkExpect(this.g1.board.get(6).flooded, true);
    t.checkExpect(this.g1.board.get(7).flooded, true);
  }
  
  //tests the buildBoard method for this FloodItWorld
  void testBuildBoard(Tester t) {
    this.initData();
    g3.buildBoard();
    ArrayList<Cell> cells4g3 = new ArrayList<Cell>(Arrays.asList(
        new Cell(0,0,Color.pink), new Cell(0,20,Color.pink),
        new Cell(0,40,Color.pink), new Cell(0,60,Color.magenta),
        new Cell(0,80,Color.red), new Cell(0,100,Color.blue),
        new Cell(20,0,Color.green), new Cell(20,20,Color.yellow),
        new Cell(20,40,Color.yellow), new Cell(20,60,Color.red),
        new Cell(20,80,Color.red), new Cell(20,100,Color.blue),
        new Cell(40,0,Color.blue),new Cell(40,20,Color.yellow),
        new Cell(40,40,Color.pink), new Cell(40,60,Color.red),
        new Cell(40,80,Color.red), new Cell(40,100,Color.magenta),
        new Cell(60,0,Color.green), new Cell(60,20,Color.green),
        new Cell(60,40,Color.red), new Cell(60,60,Color.magenta),
        new Cell(60,80,Color.blue), new Cell(60,100,Color.pink),
        new Cell(80,0,Color.yellow), new Cell(80,20,Color.green),
        new Cell(80,40,Color.magenta), new Cell(80,60,Color.green),
        new Cell(80,80,Color.green), new Cell(80,100,Color.yellow),
        new Cell(100,0,Color.yellow), new Cell(100,20,Color.magenta),
        new Cell(100,40,Color.magenta), new Cell(100,60,Color.yellow),
        new Cell(100,80,Color.pink), new Cell(100,100,Color.magenta)));
    t.checkExpect(g3.board, cells4g3);
    t.checkExpect(g3.board.size(), 36);
    t.checkExpect(g1.board.size(), 36);
  }

  //tests the connectBoard method for this FloodItWorld
  void testConnectBoard(Tester t) {
    this.initData();
    t.checkExpect(g1.board.get(15).bottom, g1.board.get(16));
    t.checkExpect(g1.board.get(15).left, g1.board.get(9));
    t.checkExpect(g1.board.get(15).right, g1.board.get(21));
  
    t.checkExpect(g1.board.get(0).left, null);
    t.checkExpect(g1.board.get(0).top, null);
    t.checkExpect(g1.board.get(0).bottom, g1.board.get(1));
    t.checkExpect(g1.board.get(0).right, g1.board.get(6));
    t.checkExpect(g1.board.get(5).bottom, null);
    t.checkExpect(g1.board.get(5).left, null);
    t.checkExpect(g1.board.get(5).top, g1.board.get(4));
    t.checkExpect(g1.board.get(5).right, g1.board.get(11));
    t.checkExpect(g1.board.get(30).right, null);
    t.checkExpect(g1.board.get(30).top, null);
    t.checkExpect(g1.board.get(30).bottom, g1.board.get(31));
    t.checkExpect(g1.board.get(30).left, g1.board.get(24));
    t.checkExpect(g1.board.get(35).bottom, null);
    t.checkExpect(g1.board.get(35).right, null);
    t.checkExpect(g1.board.get(35).top, g1.board.get(34));
    t.checkExpect(g1.board.get(35).left, g1.board.get(29));
    
  }

  // tests makeSceneHelp method for this FloodItBoard
  void testMakeSceneHelp(Tester t) {
    this.initData();
    //runs the bigbang for this scene so we can test makeSceneHelp
    this.g1.bigBang(1000, 1000);
    //checks the drawing of g1 with the drawing in initData
    t.checkExpect(this.g1.makeSceneHelp(g1.getEmptyScene()), this.board);
    t.checkExpect(this.g0.makeSceneHelp(this.g0.getEmptyScene()), this.loseScene);
    this.g0.wholeWorldFlooded();
    t.checkExpect(this.g0.makeSceneHelp(this.g0.getEmptyScene()), this.winScene);
  } 

  // tests the randColor method for a FloodItWorld to see
  // if a permissible color is being generated
  boolean testRandColor(Tester t) { 
    this.initData();
    ArrayList<Color> testColors = new ArrayList<Color>(
        Arrays.asList(Color.red, Color.magenta, Color.blue, 
            Color.green, Color.pink, Color.yellow));
    return t.checkExpect(testColors.contains(g0.randColor()), true)
        && t.checkExpect(testColors.contains(g1.randColor()), true)
        && t.checkExpect(testColors.contains(g2.randColor()), true);
  }
  
  //test for onKeyEvent method for a FloodItWorld
  void testOnKeyEvent(Tester t) {
    this.initData();
    t.checkExpect(this.g0.board.get(3).color, Color.pink);
    this.g0.resetWorld();
    t.checkExpect(this.g0.board.get(3).color, Color.blue);
  }
  
  //test for resetWorld method for a FloodItWorld
  void testResetWorld(Tester t) {
    this.initData();
    t.checkExpect(this.g1.makeSceneHelp(new WorldScene(1000,1000)), this.board);
    this.g1.onKeyEvent("r");
    t.checkExpect(this.g1.makeSceneHelp(new WorldScene(1000,1000)).equals(this.board), false);
  }

  //test for onMouseClicked method for a FloodItWorld
  void testOnMouseClicked(Tester t) {
    this.initData();
    t.checkExpect(this.g1.board.get(0).color, Color.yellow);
    t.checkExpect(this.g1.board.get(1).color, Color.pink);
    t.checkExpect(this.g1.board.get(6).color, Color.pink);
    this.g1.onMouseClicked(new Posn(35,45));
    t.checkExpect(this.g1.board.get(0).color, Color.blue);
    t.checkExpect(this.g1.board.get(1).color, Color.pink);
    t.checkExpect(this.g1.board.get(6).color, Color.pink);
  }
  
  //test for floodCells method for a FloodItWorld
  void testFloodCells(Tester t) {
    this.initData();
    t.checkExpect(this.g1.board.get(0).color, Color.yellow);
    t.checkExpect(this.g1.board.get(1).color, Color.pink);
    t.checkExpect(this.g1.board.get(6).color, Color.pink);
    this.g1.floodCells(new Cell(35, 45, Color.pink));
    t.checkExpect(this.g1.board.get(0).color, Color.pink);
    t.checkExpect(this.g1.board.get(1).color, Color.pink);
    t.checkExpect(this.g1.board.get(6).color, Color.pink);
    this.g1.floodCells(new Cell(25,35, Color.blue));
    t.checkExpect(this.g1.board.get(0).color, Color.blue);
    t.checkExpect(this.g1.board.get(1).color, Color.blue);
    t.checkExpect(this.g1.board.get(6).color, Color.blue);
  }
  
  //test for clickedCell method for a FloodItWorld
  void testClickedCell(Tester t) {
    this.initData();
    t.checkExpect(this.g1.clickedCell(new Posn(45, 83)),
        this.g1.board.get(16));
    t.checkExpect(this.g1.clickedCell(new Posn(113, 112)),
        this.g1.board.get(35));
  }
  
  //test for makeAFinalScene for FloodItWorld
  void testMakeAFinalScene(Tester t) {
    this.initData();
    t.checkExpect(this.g0.makeAFinalScene("YOU WIN!!!"), this.winScene);
    this.g0.clicksLeft = 18;
    t.checkExpect(this.g0.makeAFinalScene("YOU LOSE!!!"), this.loseScene);
  }
  
  //test for finalScore method for this FloodItWorld
  void testFinalScore(Tester t) {
    this.g1.clicksLeft = 0;
    t.checkExpect(this.g1.finalScore(), 1360);
    this.g1.clicksLeft = 18;
    t.checkExpect(this.g1.finalScore(), 1000);
  }
  
  //test for wholeWorldFlooded to see if the board is flooded
  void testWholeWorldFlooded(Tester t) {
    this.initData();
    t.checkExpect(this.g0.wholeWorldFlooded(), false); 
    for (int i = 0; i < this.g0.boardSize * this.g0.boardSize; i++) { 
      this.g0.board.get(i).flooded = true;
    }
    t.checkExpect(this.g0.wholeWorldFlooded(), true);  
    t.checkExpect(this.g1.wholeWorldFlooded(), false); 
    for (int j = 0; j < this.g1.boardSize * this.g1.boardSize; j++) { 
      this.g1.board.get(j).flooded = true;
    }  
    t.checkExpect(this.g1.wholeWorldFlooded(), true);  
    t.checkExpect(this.g2.wholeWorldFlooded(), false); 
  }

  //tests this FloodItWorld game
  void testFloodItWorld(Tester t) {
    this.initData();
    this.g4.bigBang(1000,1000, 0.05);
  }
}
