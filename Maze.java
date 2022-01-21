import java.util.HashMap;
import java.util.LinkedList;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import javalib.impworld.World;
import javalib.impworld.WorldScene;
import javalib.worldimages.*;
import tester.Tester; 
import java.util.Collections;
import java.util.Comparator; 
import java.util.Random;
import java.util.Stack;


/* Extra-credit accomplished:
 * Whistles :
 *  - keeping score of wrong moves
 * 
 * Bells : 
 *  - knocking down random walls
 */


//represents this MazeWorld Game
class MazeWorld extends World  {
  int w;
  int h;
  Random rand; 
  ArrayList<Node> board;
  ArrayList<Edge> worklist = new ArrayList<Edge>();
  ArrayList<Edge> edges = new ArrayList<Edge>();
  HashMap<Node,Node> reps = new HashMap<Node,Node>(); 
  boolean breadth;
  boolean depth;
  Node current;
  int search;
  int score;

  //constructor for MazeWorld
  MazeWorld(int x, int y) { 
    this.w = x;
    this.h = y;  
    this.rand = new Random();
    this.board = new ArrayList<Node>(); 
    this.buildNodes();
    this.connectNodes();
    this.reps = this.mapNodes();
    this.createWalls();
    this.createTree();
    this.pathMaker();
    this.breadth = false;
    this.depth = false;
    this.current = this.board.get(0);
    this.search = 0;
    this.score = 0;
  }
  
  //pre-determined MazeWorld constructor
  //to test MazeWorld
  MazeWorld() {
    this.w = 6;
    this.h = 6; 
    this.rand = new Random(5);
    this.board = new ArrayList<Node>();
  } 
  
  //constants for this MazeWorld 
  final int CELL_SIZE = 14;
  
  //creates an initial list of Nodes
  void buildNodes() {
    for (int i = 0; i < this.w; i++) {  
      for (int j = 0; j < this.h; j++) {
        this.board.add(new Node(i * CELL_SIZE, j * CELL_SIZE));
      }
    }  
  } 
  
  //connects the Nodes to each other
  void connectNodes() {
    for (int i = 0; i < this.board.size(); i++) { 
      Node n = this.board.get(i);
      if (n.posn.x > 0) {
        n.setLeft(board.get(i - this.h));
      } 
      if (n.posn.x < this.w * CELL_SIZE - CELL_SIZE) {
        n.setRight(board.get(i + this.h));
      }
      if (n.posn.y > 0) {
        n.setTop(board.get(i - 1));
      }
      if (n.posn.y < this.h * CELL_SIZE - CELL_SIZE) {
        n.setBottom(board.get(i + 1));
      }
    }
  }
  
  //maps every Node to itself
  public HashMap<Node,Node> mapNodes() {
    for (int i = 0; i < this.board.size(); i++) { 
      this.reps.put(this.board.get(i), this.board.get(i));
    }
    return this.reps;
  } 
  
  //creates the edges and sorts them by weight
  //also adds edges to each node's list of edges
  public void createWalls() {
    for (int i = 0; i < this.board.size(); i++) {
      Node n = this.board.get(i);
      if (n.bottom != null && n != null) {
        int r = this.rand.nextInt(20);
        this.worklist.add(new Edge(n, n.bottom, r));
      }
      if (n.right != null && n != null) {
        int r = this.rand.nextInt(20);
        this.worklist.add(new Edge(n, n.right, r));
      }
      if (n.top != null && n != null) {
        int r = this.rand.nextInt(20);
        this.worklist.add(new Edge(n, n.top, r));
      }
      if (n.left != null && n != null) {
        int r = this.rand.nextInt(20);
        this.worklist.add(new Edge(n, n.left, r));
      }
    }
    Collections.sort(this.worklist, new EdgeSorter());  
  } 
  
  //creates a tree from the edges using Kruskal's algorithm
  public void createTree() {  
    ArrayList<Edge> mst = new ArrayList<Edge>();
    int traverser = 0;
    int allNodes = this.h * this.w;
    while (mst.size() < allNodes - 1) {
      Edge current = worklist.get(traverser);
      if (this.find(current.curr) != this.find(current.next)) {
        mst.add(current);
        current.curr.edges.add(current);
        current.next.edges.add(new Edge(current.next, current.curr, current.weight));
        this.union(this.find(current.curr), this.find(current.next));
      }
      traverser++;
    }
    this.edges = mst;
  } 
  
  //sets a union of two Nodes in HashMap
  public void union(Node n1, Node n2) {
    this.reps.put(this.find(n1), this.find(n2));
  } 

  //finds a Node that is the same as the the inputed one
  public Node find(Node n) { 
    if (this.reps.get(n).equals(n)) {
      return n;
    } 
    else {
      return this.find(this.reps.get(n));
    } 
  }
  
  //determines the path the Edges create
  public void pathMaker() {
    for (int i = 0; i < this.edges.size(); i++) {
      Edge edge = this.edges.get(i);
      int xCurrent = edge.curr.posn.x;
      int yCurrent = edge.curr.posn.y;
      int xNext = edge.next.posn.x;
      int yNext = edge.next.posn.y; 
      //when x's are the same
      if (xCurrent == xNext) {
        //which cell is on top
        if (yNext - yCurrent == CELL_SIZE) {
          edge.curr.setBottomPath(true);
          edge.next.setTopPath(true);
        }
        if (yCurrent - yNext == CELL_SIZE) {
          edge.next.setBottomPath(true);
          edge.curr.setTopPath(true);
        }
      }
      //when y's are the same
      if (yCurrent == yNext) {
        //which cell is on the left
        if (xNext - xCurrent == CELL_SIZE) {
          edge.curr.setRightPath(true);
          edge.next.setLeftPath(true);
        }
        if (xCurrent - xNext == CELL_SIZE) {
          edge.curr.setLeftPath(true);
          edge.next.setRightPath(true);
        }
      }
    }
  }
  
  //last in, first out search of this maze
  public ArrayList<Node> dfs() {
    Node start = this.board.get(0);
    Node end = this.board.get((this.w * this.h) - 1);
    Stack<Node> frontier = new Stack<Node>();
    ArrayList<Node> explored = new ArrayList<Node>();

    frontier.push(start);
    while (frontier.size() > 0 && start != end) {
      start = frontier.pop();
      if (start == end) {
        return explored;
      }
      else if (!explored.contains(start)) {
        for (Edge edge : start.edges) {  //for each node in the nodes list of edges
          if (!explored.contains(edge.next)) {
            frontier.push(edge.next);
            edge.next.setParent(start);
          }
        }
      }
      explored.add(start);
    }
    return explored;
  }
  
  //first in, first out search of this maze
  public ArrayList<Node> bfs() {
    Node start = this.board.get(0);
    Node end = this.board.get((this.w * this.h) - 1);
    LinkedList<Node> frontier = new LinkedList<Node>();
    ArrayList<Node> explored = new ArrayList<Node>();
    
    frontier.add(start);
    while (frontier.size() > 0 && start != end) {
      start = frontier.pollFirst();
      if (start == end) {
        break;
      }
      else if (!explored.contains(start)) {
        for (Edge edge : start.edges) {
          if (!explored.contains(edge.next)) {
            frontier.add(edge.next);
            edge.next.setParent(start);
          }
        }
      }
      explored.add(start);
    }
    return explored;
  }
  
  //produces the path from start to end
  public ArrayList<Node> pathToEnd() {
    Node end = this.board.get((this.w * this.h) - 1);
    Node start = this.board.get(0);
    Stack<Node> path = new Stack<Node>();
    ArrayList<Node> orderedPath = new ArrayList<Node>();
    
    //pushes all parents starting from the end onto the stack
    while (end != start) {
      path.push(end);
      end = end.parent;
    }
    
    //pops nodes off the stack which starts
    //with the top left corner node
    //adds it to the list
    while (!path.isEmpty()) {
      Node n = path.pop();
      orderedPath.add(n);
    }
    return orderedPath;
  }
  
  //draws the entire dfs of the maze
  public void drawDFS() {
    ArrayList<Node> dfsPath = this.dfs();
    //draws each node in the dfs path
    if (this.search < dfsPath.size()) {
      dfsPath.get(this.search).setReached();
      this.search = this.search + 1;
    }
    else {
      //draws the dfs path from start to end
      this.drawPathToEnd();
    }
  }
  
  //draws the entire bfs of the maze
  public void drawBFS() {
    ArrayList<Node> bfsPath = this.bfs();
    //draws each node in the bfs path
    if (this.search < bfsPath.size()) {
      bfsPath.get(this.search).setReached();
      this.search = this.search + 1;
    }
    else {
      //draws the dfs path from start to end
      this.drawPathToEnd();
    }
  }
  
  //draws the path from start to end that is found
  public void drawPathToEnd() {
    ArrayList<Node> startToEnd = this.pathToEnd();
    for (Node n : startToEnd) {
      n.setPath();
    }
  }
  
  //keeps track to correct moves in this MazeWorld
  public void wrongScoreCount(Node current) {
    ArrayList<Node> shortest = this.bfs();
    shortest = this.pathToEnd();
    
    if (!shortest.contains(current)) {
      this.score = score - 1;
    }
  }
  
  @Override
  //draws a makeScene
  public WorldScene makeScene() {
    return this.makeSceneHelp(this.getEmptyScene());
  } 
  
  //helps draw a makeScene
  public WorldScene makeSceneHelp(WorldScene scene) {
    //x and y for last cell
    int endX = this.w * CELL_SIZE - CELL_SIZE;
    int endY = this.h * CELL_SIZE - CELL_SIZE;
    this.board.get(0).setReached();
    
    if (this.current.posn.x == endX && this.current.posn.y == endY) {
      return this.makeFinalScene();
    }
    else {
      WorldImage border = new RectangleImage(this.w * CELL_SIZE,     
          this.h * CELL_SIZE, OutlineMode.OUTLINE, Color.BLACK);
      scene.placeImageXY(border, this.w * (CELL_SIZE / 2), this.h * (CELL_SIZE / 2));
      
      WorldImage score = new TextImage("SCORE OF WRONG MOVES: " + this.score,
          CELL_SIZE, Color.black);   
      scene.placeImageXY(score, 105, 
          (this.h * CELL_SIZE) + CELL_SIZE);
      
      
      //all nodes in the board
      for (Node n : board) { 
        Color c;
        //if start node
        if (n.posn.y == 0 && n.posn.x == 0) {
          c = Color.GREEN;
        }
        //if end node
        else if (n.posn.y == endY && n.posn.x == endX) {
          c = Color.BLUE;
        }
        //if node that is being manually traversed
        else if (n == this.current) {
          c = Color.PINK;
        }
        //if node is apart of the final path of dfs or bfs
        else if (n.path) {
          c = Color.PINK;
        }
        //if node has been traversed
        else if (n.reached) {
          c = Color.CYAN;
        }     
        //if node has not been touched
        else {
          c = Color.gray;
        }
        //draw a rectangle for each node
        WorldImage nodeImage = new RectangleImage(CELL_SIZE, 
            CELL_SIZE, OutlineMode.SOLID, c);
        scene.placeImageXY(nodeImage, n.posn.x + (CELL_SIZE / 2), n.posn.y + (CELL_SIZE / 2));
      
        //if there is not a path draw a line
        if (!n.topPath) {
          WorldImage lineHor = new LineImage(new Posn(CELL_SIZE, 0), Color.BLACK);
          scene.placeImageXY(lineHor, n.posn.x + (CELL_SIZE / 2), n.posn.y);
        }
        if (!n.bottomPath) {
          WorldImage lineHor = new LineImage(new Posn(CELL_SIZE, 0), Color.BLACK);
          scene.placeImageXY(lineHor, n.posn.x + (CELL_SIZE / 2), n.posn.y + CELL_SIZE);
        }
        if (!n.leftPath) {
          WorldImage lineVert = new LineImage(new Posn(0, CELL_SIZE), Color.BLACK);
          scene.placeImageXY(lineVert, n.posn.x, n.posn.y + (CELL_SIZE / 2));
        }
        if (!n.rightPath) {
          WorldImage lineVert = new LineImage(new Posn(0, CELL_SIZE), Color.BLACK);
          scene.placeImageXY(lineVert, n.posn.x + CELL_SIZE, n.posn.y + (CELL_SIZE / 2));
        }
      }
      return scene;
    }
  }
  
  //makes a finalScene more this MazeWorld
  public WorldScene makeFinalScene() {
    WorldScene finalScene = this.getEmptyScene();
    WorldImage finish = new RectangleImage(this.w * CELL_SIZE, 
        this.h * CELL_SIZE, OutlineMode.SOLID, Color.PINK);
    finalScene.placeImageXY(finish, this.w * (CELL_SIZE / 2), this.h * (CELL_SIZE / 2));
    WorldImage youWin = new TextImage("You Win!", CELL_SIZE, Color.black);
    WorldImage restart = new TextImage("Press 'r' to restart", CELL_SIZE, Color.black);
    WorldImage rNBreath = new TextImage("Press 'b' after for", CELL_SIZE, Color.black);
    WorldImage breath = new TextImage("Breadth-First Search", CELL_SIZE, Color.black);
    WorldImage rNDepth = new TextImage("OR Press 'd' after for", CELL_SIZE, Color.black);
    WorldImage depth = new TextImage("Depth-First Search", CELL_SIZE, Color.black);
    
    finalScene.placeImageXY(youWin, (this.w * CELL_SIZE / 2) - CELL_SIZE, CELL_SIZE);
    finalScene.placeImageXY(restart, (this.w * CELL_SIZE / 2) - CELL_SIZE, CELL_SIZE * 2);
    finalScene.placeImageXY(rNBreath, (this.w * CELL_SIZE / 2) - CELL_SIZE, CELL_SIZE * 3);
    finalScene.placeImageXY(breath, (this.w * CELL_SIZE / 2) - CELL_SIZE, CELL_SIZE * 4);
    finalScene.placeImageXY(rNDepth, (this.w * CELL_SIZE / 2) - CELL_SIZE, CELL_SIZE * 5);
    finalScene.placeImageXY(depth, (this.w * CELL_SIZE / 2) - CELL_SIZE, CELL_SIZE * 6);
    return finalScene;
  }

  //on-Tick method that either knocks random walls down or 
  //does dfs or bfs
  //EXTRA-CREDIT : KNOCK WALLS DOWN -- remove commented else statement
  public void onTick() {
    if (this.breadth) {
      this.drawBFS();
    }
    else if (this.depth) {
      this.drawDFS();
    }
    /*else {
      Node n = this.board.get(this.rand.nextInt(this.board.size() - 1));
      if (!n.leftPath) {
        n.leftPath = true;
      }
      else if (!n.topPath) {
        n.topPath = true;
      }
    }
    */
  }
  
  //updates this world based on what key has been pressed
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      this.resetWorld();
      this.makeScene();
    }
    else if (key.equals("left")) {
      if (this.current.leftPath) {
        this.wrongScoreCount(current.left);
        this.current = this.current.left;
        this.current.setReached();
      }
    }
    else if (key.equals("right")) {
      if (this.current.rightPath) {
        this.wrongScoreCount(current.right);
        this.current = this.current.right;
        this.current.setReached();
      }
    }
    else if (key.equals("up")) {
      if (this.current.topPath) {
        this.wrongScoreCount(current.top);
        this.current = this.current.top;
        this.current.setReached();
      }
    }
    else if (key.equals("down")) {
      if (this.current.bottomPath) {
        this.wrongScoreCount(current.bottom);
        this.current = this.current.bottom;
        this.current.setReached();
      }
    }
    else if (key.equals("b")) {
      this.breadth = true;
    }
    
    else if (key.equals("d")) {
      this.depth = true;
    }
  }
  
  //constructor that allows a restart of the game
  public void resetWorld() {
    this.worklist = new ArrayList<Edge>();
    this.edges = new ArrayList<Edge>();
    this.reps = new HashMap<Node,Node>(); 
    this.rand = new Random();
    this.board = new ArrayList<Node>(); 
    this.buildNodes();
    this.connectNodes();
    this.reps = this.mapNodes();
    this.createWalls();
    this.createTree();
    this.pathMaker();
    this.breadth = false;
    this.depth = false;
    this.current = this.board.get(0);
    this.search = 0;
    this.score = 0;
  } 
} 


//represents a Node
class Node {
  Posn posn;
  Node top;
  Node bottom; 
  Node right;
  Node left; 
  boolean topPath;
  boolean leftPath;
  boolean bottomPath;
  boolean rightPath;
  boolean reached;
  boolean path;
  ArrayList<Edge> edges;
  Node parent;

  Node(int x, int y) { 
    this.posn = new Posn(x,y);
    this.top = null; 
    this.bottom = null;
    this.right = null;
    this.left = null; 
    this.topPath = false;
    this.leftPath = false;
    this.bottomPath = false;
    this.rightPath = false;
    this.reached = false; 
    this.path = false;
    this.edges = new ArrayList<Edge>();
    this.parent = null;
  }  

  //sets the given as right of this Node
  void setRight(Node r) {
    this.right = r;
  } 

  //sets the given as left of this Node
  void setLeft(Node l) {
    this.left = l;
  }

  //sets the given as bottom of this Node
  void setBottom(Node b) {
    this.bottom = b;  
  }

  //sets the given as top of this Node
  void setTop(Node t) {
    this.top = t;
  }

  //changes path to show if there is one
  void setTopPath(boolean canMove) {
    this.topPath = canMove;
  } 

  //changes path to show if there is one
  void setLeftPath(boolean canMove) {
    this.leftPath = canMove;
  }

  //changes path to show if there is one
  void setRightPath(boolean canMove) {
    this.rightPath = canMove;
  }
  
  //changes path to show if there is one
  void setBottomPath(boolean canMove) {
    this.bottomPath = canMove;
  }
  
  //changes boolean if this Node has been reached
  void setReached() {
    this.reached = true;
  }
  
  //changes boolean if this Node is apart of the path
  void setPath() {
    this.path = true;
  }
  
  //sets this Nodes Parent as the given
  void setParent(Node n) {
    this.parent = n;
  }
}   

//represents an Edge
class Edge {
  Node curr;
  Node next;
  int weight;

  Edge(Node curr, Node next, int weight) {
    this.curr = curr;
    this.next = next;
    this.weight = weight;
  } 
  
  //determine which edge is smaller
  public int smallerEdge(Edge that) {
    return this.weight - that.weight;
  }
} 

//represents a comparator for Edges
class EdgeSorter implements Comparator<Edge> {

  //compare method for Edges
  public int compare(Edge e1, Edge e2) {
    return e1.smallerEdge(e2);       
  }
}
  
//represents examples and tests
class ExamplesMaze {
  MazeWorld maze1; 
  MazeWorld maze2;
  MazeWorld maze3;
  ArrayList<Node> nodes;
  
  WorldScene scene;
  WorldImage grid;
  WorldImage score;
  WorldImage nodeImageGreen;
  WorldImage nodeImageGray;
  WorldImage nodeImageBlue;
  WorldImage lineHor;
  WorldImage lineVer;
  WorldScene finalScene;
  WorldImage finish;
  WorldImage youWin;
  WorldImage restart;
  WorldImage rNBreath;
  WorldImage breath;
  WorldImage rNDepth;
  WorldImage depth;
  
  Node node1;
  Node node2;
  Node node3;
  Node node4;
  Node node5;
  Node node6;
  
  ArrayList<Node> list;
  
  Edge edge1;
  Edge edge2;
  Edge edge3;
  Edge edge4;
  
  EdgeSorter edgeSorter;
  HashMap<Node, Node> reps;
  
  //initializes the data 
  void initData() {
    this.maze1 = new MazeWorld();
    this.maze2 = new MazeWorld(20, 20);
    this.maze3 = new MazeWorld(100,60);
    
    this.nodes = new ArrayList<Node>(Arrays.asList(
        new Node(0,0), new Node(0,14),
        new Node(0,28), new Node(0,42),
        new Node(0,56), new Node(0,70),
        new Node(14,0), new Node(14,14),
        new Node(14,28), new Node(14,42),
        new Node(14,56), new Node(14,70),
        new Node(28,0), new Node(28,14),
        new Node(28,28), new Node(28,42),
        new Node(28,56), new Node(28,70),
        new Node(42,0), new Node(42,14),
        new Node(42,28), new Node(42,42),
        new Node(42,56), new Node(42,70),
        new Node(56,0), new Node(56,14),
        new Node(56,28), new Node(56,42),
        new Node(56,56), new Node(56,70),
        new Node(70,0), new Node(70,14),
        new Node(70,28), new Node(70,42),
        new Node(70,56), new Node(70,70)));
    
    this.scene = this.maze1.getEmptyScene();
    this.grid = new RectangleImage(84, 84, OutlineMode.OUTLINE, Color.BLACK);
    this.score = new TextImage("SCORE OF WRONG MOVES: 0", 14, Color.black);
    this.nodeImageGreen = new RectangleImage(14, 14, OutlineMode.SOLID, Color.GREEN);
    this.nodeImageGray = new RectangleImage(14, 14, OutlineMode.SOLID, Color.gray);
    this.nodeImageBlue = new RectangleImage(14, 14, OutlineMode.SOLID, Color.BLUE);
    this.lineHor = new LineImage(new Posn(14, 0), Color.BLACK);
    this.lineVer = new LineImage(new Posn(0, 14), Color.BLACK);
    this.scene.placeImageXY(this.grid, 42, 42);
    this.scene.placeImageXY(score, 105, 84 + 14);
    this.scene.placeImageXY(nodeImageGreen, 7, 7);
    this.scene.placeImageXY(nodeImageBlue, 63, 63);
    
    for (int i = 1; i < this.nodes.size() - 2; i++) {
      Node n = this.nodes.get(i);
      this.scene.placeImageXY(nodeImageGray, n.posn.x + 7, n.posn.y + 7);
    }    
    
    //horizontal walls
    this.scene.placeImageXY(lineHor, this.nodes.get(1).posn.x + 7, 
        this.nodes.get(1).posn.y + 14);
    this.scene.placeImageXY(lineHor, this.nodes.get(2).posn.x + 7,
        this.nodes.get(2).posn.y + 14);
    this.scene.placeImageXY(lineHor, this.nodes.get(9).posn.x + 7,
        this.nodes.get(9).posn.y + 14);
    this.scene.placeImageXY(lineHor, this.nodes.get(12).posn.x + 7,
        this.nodes.get(12).posn.y + 14);
    this.scene.placeImageXY(lineHor, this.nodes.get(13).posn.x + 7,
        this.nodes.get(13).posn.y + 14);
    this.scene.placeImageXY(lineHor, this.nodes.get(15).posn.x + 7,
        this.nodes.get(15).posn.y + 14);
    this.scene.placeImageXY(lineHor, this.nodes.get(16).posn.x + 7,
        this.nodes.get(16).posn.y + 14);
    this.scene.placeImageXY(lineHor, this.nodes.get(20).posn.x + 7,
        this.nodes.get(20).posn.y + 14);
    this.scene.placeImageXY(lineHor, this.nodes.get(25).posn.x + 7,
        this.nodes.get(25).posn.y + 14);
    this.scene.placeImageXY(lineHor, this.nodes.get(32).posn.x + 7,
        this.nodes.get(32).posn.y + 14);
    this.scene.placeImageXY(lineHor, this.nodes.get(34).posn.x + 7,
        this.nodes.get(34).posn.y + 14);
    //vertical walls
    this.scene.placeImageXY(lineVer, this.nodes.get(0).posn.x + 14,
        this.nodes.get(0).posn.y + 7);
    this.scene.placeImageXY(lineVer, this.nodes.get(4).posn.x + 14,
        this.nodes.get(4).posn.y + 7);
    this.scene.placeImageXY(lineVer, this.nodes.get(9).posn.x + 14,
        this.nodes.get(9).posn.y + 7);
    this.scene.placeImageXY(lineVer, this.nodes.get(10).posn.x + 14,
        this.nodes.get(10).posn.y + 7);
    this.scene.placeImageXY(lineVer, this.nodes.get(11).posn.x + 14,
        this.nodes.get(11).posn.y + 7);
    this.scene.placeImageXY(lineVer, this.nodes.get(13).posn.x + 14,
        this.nodes.get(13).posn.y + 7);
    this.scene.placeImageXY(lineVer, this.nodes.get(14).posn.x + 14,
        this.nodes.get(14).posn.y + 7);
    this.scene.placeImageXY(lineVer, this.nodes.get(18).posn.x + 14,
        this.nodes.get(18).posn.y + 7);
    this.scene.placeImageXY(lineVer, this.nodes.get(19).posn.x + 14,
        this.nodes.get(19).posn.y + 7);
    this.scene.placeImageXY(lineVer, this.nodes.get(21).posn.x + 14,
        this.nodes.get(21).posn.y + 7);
    this.scene.placeImageXY(lineVer, this.nodes.get(22).posn.x + 14,
        this.nodes.get(22).posn.y + 7);
    this.scene.placeImageXY(lineVer, this.nodes.get(23).posn.x + 14,
        this.nodes.get(23).posn.y + 7);
    this.scene.placeImageXY(lineVer, this.nodes.get(25).posn.x + 14,
        this.nodes.get(25).posn.y + 7);
    this.scene.placeImageXY(lineVer, this.nodes.get(28).posn.x + 14,
        this.nodes.get(28).posn.y + 7);
    
    this.node1 = new Node(0,0);
    this.node2 = new Node(0,1);
    this.node3 = new Node(0,2);
    this.node4 = new Node(1,0);
    this.node5 = new Node(1,1);
    this.node6 = new Node(1,2);
    
    
    this.edge1 = new Edge(node1, node2, 34);
    this.edge2 = new Edge(node2, node3, 15);
    this.edge3 = new Edge(node3, node4, 50);
    this.edge4 = new Edge(node4, node1, 0);
    
    this.edgeSorter = new EdgeSorter();
    
    this.finalScene = this.maze1.getEmptyScene();
    this.finish = new RectangleImage(84, 84, OutlineMode.SOLID, Color.PINK);
    finalScene.placeImageXY(finish, 6 * 7, 6 * 7);
    this.youWin = new TextImage("You Win!", 14, Color.black);
    this.restart = new TextImage("Press 'r' to restart", 14, Color.black);
    this.rNBreath = new TextImage("Press 'b' after for", 14, Color.black);
    this.breath = new TextImage("Breadth-First Search", 14, Color.black);
    this.rNDepth = new TextImage("OR Press 'd' after for", 14, Color.black);
    this.depth = new TextImage("Depth-First Search", 14, Color.black);
    
    this.finalScene.placeImageXY(youWin, (6 * 14 / 2) - 14, 14);
    this.finalScene.placeImageXY(restart, (6 * 14 / 2) - 14, 14 * 2);
    this.finalScene.placeImageXY(rNBreath, (6 * 14 / 2) - 14, 14 * 3);
    this.finalScene.placeImageXY(breath, (6 * 14 / 2) - 14, 14 * 4);
    this.finalScene.placeImageXY(rNDepth, (6 * 14 / 2) - 14, 14 * 5);
    this.finalScene.placeImageXY(depth, (6 * 14 / 2) - 14, 14 * 6);
  }
  
  //tests the buildNodes method for this MazeWorld
  void testBuildNodes(Tester t) {
    this.initData();
    this.maze1.buildNodes();
    t.checkExpect(this.maze1.board, nodes);
  }
  
  //tests the connectNodes method for this MazeWorld
  void testConnectNodes(Tester t) {
    this.initData();
    this.maze1.buildNodes();
    t.checkExpect(this.maze1.board.get(15).bottom, null);
    t.checkExpect(this.maze1.board.get(15).left, null);
    t.checkExpect(this.maze1.board.get(15).right, null);
    t.checkExpect(this.maze1.board.get(0).left, null);
    t.checkExpect(this.maze1.board.get(0).top, null);
    t.checkExpect(this.maze1.board.get(0).bottom, null);
    t.checkExpect(this.maze1.board.get(0).right, null);
    t.checkExpect(this.maze1.board.get(5).bottom, null);
    t.checkExpect(this.maze1.board.get(5).left, null);
    t.checkExpect(this.maze1.board.get(5).top, null);
    t.checkExpect(this.maze1.board.get(5).right, null);
    t.checkExpect(this.maze1.board.get(30).right, null);
    t.checkExpect(this.maze1.board.get(30).top, null);
    t.checkExpect(this.maze1.board.get(30).bottom, null);
    t.checkExpect(this.maze1.board.get(30).left, null);
    t.checkExpect(this.maze1.board.get(35).bottom, null);
    t.checkExpect(this.maze1.board.get(35).right, null);
    t.checkExpect(this.maze1.board.get(35).top, null);
    t.checkExpect(this.maze1.board.get(35).left, null);
    this.maze1.connectNodes();
    t.checkExpect(this.maze1.board.get(15).bottom, this.maze1.board.get(16));
    t.checkExpect(this.maze1.board.get(15).left, this.maze1.board.get(9));
    t.checkExpect(this.maze1.board.get(15).right, this.maze1.board.get(21));
    t.checkExpect(this.maze1.board.get(0).left, null);
    t.checkExpect(this.maze1.board.get(0).top, null);
    t.checkExpect(this.maze1.board.get(0).bottom, this.maze1.board.get(1));
    t.checkExpect(this.maze1.board.get(0).right, this.maze1.board.get(6));
    t.checkExpect(this.maze1.board.get(5).bottom, null);
    t.checkExpect(this.maze1.board.get(5).left, null);
    t.checkExpect(this.maze1.board.get(5).top, this.maze1.board.get(4));
    t.checkExpect(this.maze1.board.get(5).right, this.maze1.board.get(11));
    t.checkExpect(this.maze1.board.get(30).right, null);
    t.checkExpect(this.maze1.board.get(30).top, null);
    t.checkExpect(this.maze1.board.get(30).bottom, this.maze1.board.get(31));
    t.checkExpect(this.maze1.board.get(30).left, this.maze1.board.get(24));
    t.checkExpect(this.maze1.board.get(35).bottom, null);
    t.checkExpect(this.maze1.board.get(35).right, null);
    t.checkExpect(this.maze1.board.get(35).top, this.maze1.board.get(34));
    t.checkExpect(this.maze1.board.get(35).left, this.maze1.board.get(29));  
  }
  
  
  //tests the mapNodes method for this MazeWorld
  void testMapNodes(Tester t) {
    this.initData();
    this.maze1.buildNodes();
    t.checkExpect(this.maze1.reps.size(), 0);
    this.maze1.mapNodes();
    t.checkExpect(this.maze1.reps.size(), 36);
    t.checkExpect(this.maze1.reps.get(this.maze1.board.get(14)), this.maze1.board.get(14));
    t.checkExpect(this.maze1.reps.get(this.maze1.board.get(0)), this.maze1.board.get(0));
    t.checkExpect(this.maze1.reps.get(this.maze1.board.get(35)), this.maze1.board.get(35)); 
  }
  
  //tests for createWalls method for this MazeWorld
  void testCreateWalls(Tester t) {
    this.initData();
    this.maze1.buildNodes();
    this.maze1.connectNodes();
    t.checkExpect(this.maze1.worklist.size(), 0);
    this.maze1.createWalls();
    t.checkExpect(this.maze1.worklist.size(), 120);
  }
 
  //tests for createTree method for this MazeWorld
  void testCreateTree(Tester t) {
    this.initData();
    this.maze1.buildNodes();
    this.maze1.connectNodes();
    this.maze1.mapNodes();
    this.maze1.createWalls(); 
    t.checkExpect(this.maze1.board.get(0).edges.size(), 0);
    t.checkExpect(this.maze1.edges.size(), 0);
    this.maze1.createTree();
    t.checkExpect(this.maze1.edges.size(), 35);
    t.checkExpect(this.maze1.board.get(0).edges.size(), 1);
  }
  
  //tests for union method for this MazeWorld
  void testUnion(Tester t) {
    this.initData();
    t.checkExpect(this.maze1.reps.size(), 0);
    this.maze1.buildNodes();
    this.maze1.connectNodes();
    this.maze1.mapNodes();
    t.checkExpect(this.maze1.reps.get(this.maze1.board.get(0)), this.maze1.board.get(0));
    t.checkExpect(this.maze1.reps.get(this.maze1.board.get(1)), this.maze1.board.get(1));
    this.maze1.union(this.maze1.board.get(0), this.maze1.board.get(1));
    t.checkExpect(this.maze1.reps.get(this.maze1.board.get(0)), this.maze1.board.get(1));
    t.checkExpect(this.maze1.reps.get(this.maze1.board.get(1)), this.maze1.board.get(1));
    t.checkExpect(this.maze1.reps.get(this.maze1.board.get(6)), this.maze1.board.get(6));
    this.maze1.union(this.maze1.board.get(0), this.maze1.board.get(6));
    t.checkExpect(this.maze1.reps.get(this.maze1.board.get(0)), this.maze1.board.get(1));
    t.checkExpect(this.maze1.reps.get(this.maze1.board.get(1)), this.maze1.board.get(6));
    t.checkExpect(this.maze1.reps.get(this.maze1.board.get(6)), this.maze1.board.get(6));
  }
  
  //tests for find method for this MazeWorld
  void testFind(Tester t) {
    this.initData();
    t.checkExpect(this.maze1.reps.size(), 0);
    this.maze1.buildNodes();
    this.maze1.connectNodes();
    this.maze1.mapNodes();
    this.maze1.union(this.maze1.board.get(0), this.maze1.board.get(1));
    t.checkExpect(this.maze1.find(this.maze1.board.get(0)), this.maze1.board.get(1));
    t.checkExpect(this.maze1.find(this.maze1.board.get(1)), this.maze1.board.get(1));
    t.checkExpect(this.maze1.find(this.maze1.board.get(6)), this.maze1.board.get(6));
    this.maze1.union(this.maze1.board.get(0), this.maze1.board.get(6));
    t.checkExpect(this.maze1.find(this.maze1.board.get(0)), this.maze1.board.get(6));
    t.checkExpect(this.maze1.find(this.maze1.board.get(1)), this.maze1.board.get(6));
    t.checkExpect(this.maze1.find(this.maze1.board.get(6)), this.maze1.board.get(6));
  }
  
  //tests for pathMaker method for this MazeWorld
  void testPathMaker(Tester t) {
    this.initData();
    this.maze1.buildNodes();
    this.maze1.connectNodes();
    this.maze1.reps = this.maze1.mapNodes();
    this.maze1.createWalls();
    this.maze1.createTree(); 
    
    //this will become true, because there is no wall underneath
    t.checkExpect(this.maze1.board.get(0).bottomPath, false );
    //this will stay false, because there is a wall to the right
    t.checkExpect(this.maze1.board.get(0).rightPath, false );
    //this will become true, because there is a wall to the rigth
    t.checkExpect(this.maze1.board.get(12).rightPath, false);
    this.maze1.pathMaker();
    t.checkExpect(this.maze1.board.get(0).bottomPath, true );
    t.checkExpect(this.maze1.board.get(0).rightPath, false );
    t.checkExpect(this.maze1.board.get(12).rightPath, true);
  }  
  
  //tests for dfs method for this MazeWorld
  void testDfs(Tester t) {
    this.initData(); 
    this.maze1.buildNodes();
    this.maze1.connectNodes(); 
    this.maze1.reps = this.maze1.mapNodes(); 
    this.maze1.createWalls();
    this.maze1.createTree();
    ArrayList<Node> explored = this.maze1.dfs();
    //when run on bigBang, this starts at 0,0
    t.checkExpect(explored.contains(this.maze1.board.get(0)), true);
    //the traversed path does not contain cell 5
    t.checkExpect(explored.contains(this.maze1.board.get(5)), false); 
    //the traversed path does contain cell 29
    t.checkExpect(explored.contains(this.maze1.board.get(29)), true); 
  }
  
  //tests for bfs method for this MazeWorld
  void testBfs(Tester t) {
    this.initData(); 
    this.maze1.buildNodes();
    this.maze1.connectNodes(); 
    this.maze1.reps = this.maze1.mapNodes();  
    this.maze1.createWalls();
    this.maze1.createTree();
    ArrayList<Node> explored = this.maze1.bfs();
    //when run on bigBang, this should start at 0,0
    //traverse through all nodes except node 25
    t.checkExpect(explored.contains(this.maze1.board.get(0)), true);
    t.checkExpect(explored.contains(this.maze1.board.get(1)), true); 
    t.checkExpect(explored.contains(this.maze1.board.get(25)), false); 
  }  
  
  //test for pathToEnd method for this MazeWorld
  void testPathToEnd(Tester t) {
    this.initData(); 
    this.maze1.buildNodes();
    this.maze1.connectNodes(); 
    this.maze1.reps = this.maze1.mapNodes(); 
    this.maze1.createWalls();
    this.maze1.createTree();
    this.maze1.dfs(); 

    //creates dfs final path from start to finish
    ArrayList<Node> pathToEnd = this.maze1.pathToEnd();
    //path is size 12
    t.checkExpect(pathToEnd.size(), 12);
    //path does not include 0,0 so starts at 0,14 and ends at 70,70
    t.checkExpect(pathToEnd.get(0).posn, new Posn(0, 14));
    t.checkExpect(pathToEnd.get(11).posn, new Posn(70, 70)); 
    t.checkExpect(pathToEnd.get(5).posn, new Posn(42, 14));

    
  } 
  
  //tests for drawDFS method for this MazeWorld
  void testDrawDFS(Tester t) {
    this.initData(); 
    this.maze1.buildNodes();
    this.maze1.connectNodes(); 
    this.maze1.reps = this.maze1.mapNodes(); 
    this.maze1.createWalls();
    this.maze1.createTree(); 
    this.maze1.pathMaker();
    
    this.maze1.search = 0;
    this.maze1.drawDFS();
    t.checkExpect(this.maze1.board.get(0).reached, true);
    t.checkExpect(this.maze1.board.get(1).reached, false); 
    // search now equals 1
    this.maze1.drawDFS();
    t.checkExpect(this.maze1.board.get(1).reached, true); 
    t.checkExpect(this.maze1.board.get(7).reached, false);
    // search now equals 2
    this.maze1.drawDFS(); 
    t.checkExpect(this.maze1.board.get(7).reached, true);
    
  }
  
  //tests for drawBFS method for this MazeWorld
  void testDrawBFS(Tester t) {
    this.initData(); 
    this.maze1.buildNodes();
    this.maze1.connectNodes(); 
    this.maze1.reps = this.maze1.mapNodes(); 
    this.maze1.createWalls();
    this.maze1.createTree(); 
    this.maze1.pathMaker();
    
    this.maze1.search = 0;
    this.maze1.drawBFS();
    t.checkExpect(this.maze1.board.get(0).reached, true);
    t.checkExpect(this.maze1.board.get(1).reached, false); 
    // search is equals 1
    this.maze1.drawBFS();
    t.checkExpect(this.maze1.board.get(1).reached, true); 
    t.checkExpect(this.maze1.board.get(7).reached, false);
    // search now equals 2
    this.maze1.drawBFS(); 
    t.checkExpect(this.maze1.board.get(7).reached, true);
    
  }
  
  //tests for drawPathToEnd method for this MazeWorld
  void testDrawPathToEnd(Tester t) { 
    this.initData(); 
    this.maze1.buildNodes();
    this.maze1.connectNodes(); 
    this.maze1.reps = this.maze1.mapNodes(); 
    this.maze1.createWalls();
    this.maze1.createTree(); 
    this.maze1.pathMaker();
    this.maze1.search = 0;
    t.checkExpect(this.maze1.board.get(1).path, false); 
    t.checkExpect(this.maze1.board.get(7).path, false);
    t.checkExpect(this.maze1.board.get(5).path, false);
    t.checkExpect(this.maze1.board.get(6).path, false);
    this.maze1.dfs();
    this.maze1.drawPathToEnd();
    t.checkExpect(this.maze1.board.get(1).path, true); 
    t.checkExpect(this.maze1.board.get(7).path, true);
    t.checkExpect(this.maze1.board.get(5).path, false);
    t.checkExpect(this.maze1.board.get(6).path, true);
  }
  
  //tests the wrongScoreCount method for this MazeWoeld
  void testWrongScoreCount(Tester t) {
    this.initData();
    this.maze1.buildNodes();
    this.maze1.connectNodes();
    this.maze1.reps = this.maze1.mapNodes();
    this.maze1.createWalls();
    this.maze1.createTree();
    this.maze1.pathMaker();
    this.maze1.breadth = false;
    this.maze1.depth = false;
    this.maze1.current = this.maze1.board.get(0);
    this.maze1.search = 0;
    this.maze1.score = 0;
    
    //this cell is in the bfs shortest path
    this.maze1.current = this.maze1.board.get(1);
    this.maze1.wrongScoreCount(this.maze1.current);
    t.checkExpect(this.maze1.score, 0);
    
    //this cell is not in the bfs shortest path
    this.maze1.current = this.maze1.board.get(8);
    this.maze1.wrongScoreCount(this.maze1.current);
    t.checkExpect(this.maze1.score, -1);
  }
  
  
  //tests the makeSceneHelp method for this MazeWorld
  void testMakeSceneHelp(Tester t) {
    this.initData();
    this.maze1.buildNodes();
    this.maze1.connectNodes();
    this.maze1.reps = this.maze1.mapNodes();
    this.maze1.createWalls();
    this.maze1.createTree();
    this.maze1.current = this.maze1.board.get(0);
    
    t.checkExpect(this.maze1.makeSceneHelp(
        this.maze1.getEmptyScene()), this.scene);
  }
  
  //tests the makeFinalScene method for this MazeWorld
  void testMakeFinalScene(Tester t) {
    this.initData();
    t.checkExpect(this.maze1.makeFinalScene(), this.finalScene);
  }
  
  //test for onTick method for this MazeWorld
  void testOnTick(Tester t) {
    this.maze1.buildNodes();
    this.maze1.connectNodes();
    this.maze1.reps = this.maze1.mapNodes();
    this.maze1.createWalls();
    this.maze1.createTree();
    this.maze1.pathMaker();
    this.maze1.breadth = false;
    this.maze1.depth = false;
    this.maze1.current = this.maze1.board.get(0);
    this.maze1.search = 0;
    this.maze1.score = 0;
    
    t.checkExpect(this.maze1.search, 0);
    t.checkExpect(this.maze1.board.get(0).reached, false);
    this.maze1.depth = true;
    this.maze1.onTick();
    t.checkExpect(this.maze1.search, 1);
    t.checkExpect(this.maze1.board.get(0).reached, true);
    
    this.maze1.resetWorld();
    t.checkExpect(this.maze1.search, 0);
    t.checkExpect(this.maze1.board.get(0).reached, false);
    this.maze1.breadth = true;
    this.maze1.onTick();
    t.checkExpect(this.maze1.search, 1);
    t.checkExpect(this.maze1.board.get(0).reached, true);
  }
  
  //test for onKeyEvent method for this MazeWorld
  void testOnKeyEvent(Tester t) {
    this.initData();
    this.maze1.buildNodes();
    this.maze1.connectNodes();
    this.maze1.reps = this.maze1.mapNodes();
    this.maze1.createWalls();
    this.maze1.createTree();
    this.maze1.pathMaker();
    this.maze1.breadth = false;
    this.maze1.depth = false;
    this.maze1.current = this.maze1.board.get(0);
    this.maze1.search = 0;
    this.maze1.score = 0;
    //down key
    t.checkExpect(this.maze1.current, this.maze1.board.get(0));
    this.maze1.onKeyEvent("down");
    t.checkExpect(this.maze1.current, this.maze1.board.get(1));
    //right key
    this.maze1.onKeyEvent("right");
    t.checkExpect(this.maze1.current, this.maze1.board.get(7));
    //left key
    this.maze1.onKeyEvent("left");
    t.checkExpect(this.maze1.current, this.maze1.board.get(1));
    //up key
    this.maze1.onKeyEvent("up");
    t.checkExpect(this.maze1.current, this.maze1.board.get(0));
    
    //breadth-first search key
    t.checkExpect(this.maze1.breadth, false);
    this.maze1.onKeyEvent("b");
    t.checkExpect(this.maze1.breadth, true);
    //depth-first search key
    this.maze1.onKeyEvent("r");
    t.checkExpect(this.maze1.depth, false);
    this.maze1.onKeyEvent("d");
    t.checkExpect(this.maze1.depth, true);
    //restart key
    t.checkExpect(this.maze1.makeScene(), this.scene);
    this.maze1.onKeyEvent("r"); 
    t.checkExpect(this.maze1.makeScene().equals(this.scene), true);
  } 
  
  //test for resetWorld method for a MazeWorld
  void testResetWorld(Tester t) {
    this.initData();
    this.maze1.buildNodes();
    t.checkExpect(this.maze1.board.get(3).bottom, null);
    t.checkExpect(this.maze1.board.get(3).top, null);
    this.maze1.resetWorld(); 
    t.checkExpect(this.maze1.board.get(3).bottom, this.maze1.board.get(4));
    t.checkExpect(this.maze1.board.get(3).top, this.maze1.board.get(2));
  }
  
  //tests the setTop method for this Node
  void testSetTop(Tester t) { 
    this.initData(); 
    t.checkExpect(this.node3.top, null); 
    t.checkExpect(this.node2.top, null); 
    t.checkExpect(this.node5.top, null);
  
    this.node3.setTop(node1); 
    this.node2.setTop(node4); 
    this.node5.setTop(node3); 
 
    t.checkExpect(this.node3.top, this.node1); 
    t.checkExpect(this.node2.top, this.node4); 
    t.checkExpect(this.node5.top, this.node3);
  } 

  //tests the setBottom method for this Node
  void testSetBottom(Tester t) {
    this.initData(); 
    
    t.checkExpect(this.node1.bottom, null); 
    t.checkExpect(this.node4.bottom, null); 
    t.checkExpect(this.node3.bottom, null);
  
    this.node1.setBottom(this.node3); 
    this.node4.setBottom(this.node2); 
    this.node3.setBottom(this.node5); 
  
    t.checkExpect(this.node1.bottom, this.node3); 
    t.checkExpect(this.node4.bottom, this.node2); 
    t.checkExpect(this.node3.bottom, this.node5);
  
  } 

  //tests the setRight method for this Node
  void testSetRight(Tester t) { 
    this.initData();
  
    t.checkExpect(this.node2.right, null); 
    t.checkExpect(this.node5.right, null); 
    t.checkExpect(this.node3.right, null);
  
    this.node2.setRight(this.node1); 
    this.node5.setRight(this.node4);
    this.node3.setRight(this.node2);
  
    t.checkExpect(this.node2.right, this.node1);
    t.checkExpect(this.node5.right, this.node4);
    t.checkExpect(this.node3.right, this.node2);
  } 

  //tests the setLeft method for this Node
  void testSetLeft(Tester t) {
    this.initData();
  
    t.checkExpect(this.node1.left, null); 
    t.checkExpect(this.node4.left, null); 
    t.checkExpect(this.node2.left, null);
  
    this.node1.setLeft(this.node2); 
    this.node4.setLeft(this.node5);
    this.node2.setLeft(this.node3);
  
    t.checkExpect(node1.left, node2); 
    t.checkExpect(node4.left, node5); 
    t.checkExpect(node2.left, node3);
  }
  
  //tests for setTopPath method for this Node
  void testSetTopPath(Tester t) {
    this.initData();
    t.checkExpect(this.node1.topPath, false);
    t.checkExpect(this.node2.topPath, false);
    this.node1.setTopPath(false);
    this.node2.setTopPath(true);
    t.checkExpect(this.node1.topPath, false);
    t.checkExpect(this.node2.topPath, true);
  }
  
  //tests for setLeftPath method for this Node
  void testSetLeftPath(Tester t) {
    this.initData();
    t.checkExpect(this.node1.leftPath, false);
    t.checkExpect(this.node2.leftPath, false);
    this.node1.setLeftPath(false);
    this.node2.setLeftPath(true);
    t.checkExpect(this.node1.leftPath, false);
    t.checkExpect(this.node2.leftPath, true);
  }
  
  //tests for setRightPath method for this Node
  void testSetRightPath(Tester t) {
    this.initData();
    t.checkExpect(this.node1.bottomPath, false);
    t.checkExpect(this.node2.bottomPath, false);
    this.node1.setBottomPath(false);
    this.node2.setBottomPath(true);
    t.checkExpect(this.node1.bottomPath, false);
    t.checkExpect(this.node2.bottomPath, true);
  }
  
  //tests for setBottomPath method for this Node
  void testSetBottomPath(Tester t) {
    this.initData();
    t.checkExpect(this.node1.rightPath, false);
    t.checkExpect(this.node2.rightPath, false);
    this.node1.setRightPath(false);
    this.node2.setRightPath(true);
    t.checkExpect(this.node1.rightPath, false);
    t.checkExpect(this.node2.rightPath, true);
  }
  
  //tests for setReached method for this Node
  void testSetReached(Tester t) {
    this.initData();
    t.checkExpect(this.node1.reached, false);
    this.node1.setReached();
    t.checkExpect(this.node1.reached, true);
  }
  
  //tests for setPath method for this Node
  void testSetPath(Tester t) {
    this.initData();
    t.checkExpect(this.node1.path, false);
    this.node1.setPath();
    t.checkExpect(this.node1.path, true);
  }
  
  //tests for setParent method for this Node
  void testSetParent(Tester t) {
    this.initData();
    t.checkExpect(this.node1.parent, null);
    this.node1.setParent(this.node2);
    t.checkExpect(this.node1.parent, this.node2);
  }
  
  //tests for smallerEdge method for this Edge
  void testSmallerEdge(Tester t) {
    this.initData();
    t.checkExpect(this.edge1.smallerEdge(this.edge3), 34 - 50);
    t.checkExpect(this.edge3.smallerEdge(this.edge2), 50 - 15);
    t.checkExpect(this.edge2.smallerEdge(this.edge4), 15 - 0);
  }
  
  //tests for EdgeSorter comparator for Edges
  void testCompare(Tester t) {
    this.initData();
    t.checkExpect(this.edgeSorter.compare(this.edge1, this.edge2), 34 - 15);
    t.checkExpect(this.edgeSorter.compare(this.edge2, this.edge3), 15 - 50);
    t.checkExpect(this.edgeSorter.compare(this.edge3, this.edge4), 50 - 0);
  }
  
  //tests bigbang method on this MazeWorld
  void testBigBang(Tester t) {
    this.initData();
    this.maze2.bigBang(500, 500, 0.1); //20x20
    //this.maze3.bigBang(2000, 1000, 0.1);  //100x60 maze
  }
}