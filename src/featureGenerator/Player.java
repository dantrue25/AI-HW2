/*
 * @Authors: Daniel B True      dbtrue@wpi.edu
 * @Authors: Nicholas Muesch    nmmuesch@wpi.edu
 */

package featureGenerator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Player {
	public static Board currentState;
	String playerName = "DanNick";
	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	
	int height;
	int width;
	int connectN;
	int moveTime;
	int firstPlayer;
	public static int ourPlayerNum;
	int otherPlayerNum;
	int turnNum = 1;
	boolean first_move=false;
	ArrayList<Integer> alphaBeta;
	int terminalDepth = 7;
	int MAXVALUE = 1000;
	int MINVALUE = -1000;
	
	/**
	 * 
	 * Handles the input/output from/to the referee
	 * This is where we actually play moves
	 */
	public boolean processInput() throws IOException{	
	
    	String s=input.readLine();	
		List<String> ls=Arrays.asList(s.split(" "));
		
		/*
		 * ls.size() == 5
		 * Gets information about the board, who goes first, and how much time we have per move
		 */
		if(ls.size()==5){
			
			height = Integer.parseInt(ls.get(0));       // (0) Board height
			width =  Integer.parseInt(ls.get(1));       // (1) Board width
			connectN = Integer.parseInt(ls.get(2));     // (2) How many to connect to win
			firstPlayer = Integer.parseInt(ls.get(3));  // (3) Our turn number (1 if going first, 2 if second)
			moveTime = Integer.parseInt(ls.get(4));     // (4) Time allotted for each move
			
			// Create initial board
			currentState = new Board(height, width, connectN);
			
			if(moveTime >= 30)
				terminalDepth = 7;
			else if(moveTime >= 15)
				terminalDepth = 6;
			else if(moveTime >= 4)
				terminalDepth = 5;
			else if(moveTime <= 1)
				terminalDepth = 2;
			else
				terminalDepth = 4;
			
			if(currentState.width >= 8)
				terminalDepth--;
			if(currentState.width >= 25)
				terminalDepth=4;
			if(currentState.width >= 50)
				terminalDepth=2;
			if(terminalDepth <=0)
				terminalDepth = 1;
			
			// If we are going first, make a move, else, do nothing TEST
			if(ourPlayerNum == firstPlayer)
			{
				first_move = true;
				//otherPlayerNum = 2;
				
				//Make a move
				Move firstMove = new Move( (int)(width/2), 1);
				currentState.makeMove(firstMove, ourPlayerNum);
				System.out.println(firstMove.toString());
			}
			else {
				//otherPlayerNum = 1;
				
				//writer.println("This is not our move!!!");
			}

			turnNum++;
			return true;
			//writer.println("This is the end of our turn!");
		}
		
		/* 
		 * ls.size() == 2
		 * Get move from opponent and add opponents move to currentState.
		 * Then find best next move, add it to currentState, and send next move to referee.
		 */
		else if(ls.size()==2){
			Move oppMove = new Move(Integer.parseInt(ls.get(0)), Integer.parseInt(ls.get(1)));
			currentState.makeMove(oppMove, otherPlayerNum);
			
			Node root = new Node(new ArrayList<Move>());
					
			alphaBeta = new ArrayList<Integer>();
			
			for(int i = 0; i < terminalDepth; i++) {
				if( (i % 2) == 0 ) {
					alphaBeta.add(MAXVALUE);
				}
				else
					
					alphaBeta.add(MINVALUE);
					
			}
			
			// Get next move from minimax algorithm
			Move ourNextMove = minimax(root).moves.get(0);
			
			// Update our board with the move we chose
			currentState.makeMove(ourNextMove, ourPlayerNum);
			
			//Send the move to the referee to go to the opponent
			System.out.println(ourNextMove.toString());
			
			
			//currentState.printBoard();
			//Board b = currentState.clone();
			//b.printBoard();
			
			turnNum++;
		}
		
		/* 
		 * ls.size() == 1
		 * Game has ended. Stop looping.
		 */
		else if(ls.size()==1){
			System.out.println("GAME OVER!");
			return false;
		}
		
		/*
		 * ls.size() == 4
		 * Receive information on who goes first and second.
		 */
		else if(ls.size()==4){
			if(ls.get(1).equals(playerName)) {
				ourPlayerNum = 1;
				otherPlayerNum = 2;
			}
			else {
				otherPlayerNum = 1;
				ourPlayerNum  = 2;
			}
			// Take out if unnecessary
		}
		
		/*
		 * ls.size() is not one of the options above
		 */
		else
			System.out.println("Not what I want.");
		
		
		return true; // Always return true unless the game is over
	}
	
	/*
	 * Our minimax implementation.
	 * Takes in a node of the minimax tree and returns with node with the
	 * best minimax value.
	 * Our alphaBeta Pruning implementation
	 * Sets a skipSibling flag in a node if its heuristic value is less 
	 * than the alpha, or greater than the beta, depending on which level
	 * we are at. 
	 * Then calculate the max or min of the child nodes to get its minimax value.
	 */
	public Node minimax (Node currentNode) {
		
		// Copy the current board state
		Board currentBoardCopy = currentState.clone();
		ArrayList<Move> moveList = currentNode.moves;
		int player = ourPlayerNum;
		
		/* Make the move for each move in the node's move list.
		 *  Say we are going 3 levels deep, we will have to make 3 moves
		 *  to get the current board 3 moves ahead (into future moves).
		 *  Each node has a list of moves which will get the current 
		 *  board to the node's prospective state
		 */
		for(Move m: moveList) {
			currentBoardCopy.makeMove(m, player);
			
			if(player == ourPlayerNum)
				player = otherPlayerNum;
			else
				player = ourPlayerNum;
		}
		
		// Get all possible moves for the current player at the given state
		ArrayList<Move> newMoves = currentBoardCopy.getMoves(player);

		/*
		 *  End recursive call if it is at the desired depth or it is a terminal state.
		 *  Calculates the current node's heuristic, and then returns itself.
		 *  Also, if it is a MAX level, check if its heuristic is less than or equal to alpha.
		 *  If it is a MIN level, check if its heuristic is greater than beta.
		 */
		if(moveList.size() >= terminalDepth || newMoves.isEmpty()) {
			currentNode.heuristic = currentBoardCopy.getHeuristic();
			
			if(currentNode.getLevel() % 2 == 0) {
					if(currentNode.getLevel() > 1 && currentNode.heuristic <= alphaBeta.get(currentNode.getLevel() -1)) {
						currentNode.skipSiblings = true;
					}
			}
			else {
					if(currentNode.getLevel() > 1 && currentNode.heuristic > alphaBeta.get(currentNode.getLevel() - 1)) {
						currentNode.skipSiblings = true;
					}
			}
			
			return currentNode;
		}
		
		/*
		 * For each possible move that the current node has, create a new child node
		 * and add it to the current node's list of children.
		 */
		for(Move newM: newMoves) {
			ArrayList<Move> moves = clone(moveList);
			Node n = new Node(moves);
			n.moves.add(newM);
			currentNode.addChild(n);
		}
		
		/*
		 * Recursive step.
		 * Run minimax on each of current node's children.
		 * Add the returned nodes to a list.
		 * Checks after every minimax run to see if further nodes can be pruned.
		 */
		ArrayList<Node> nodes = new ArrayList<Node>();
		for(Node child: currentNode.children) {
			nodes.add(minimax(child));
			if(child.skipSiblings)
				break;
		}
		
		// If MAX level, return the node child node with max heuristic
		if(moveList.size() % 2 == 0) {
			Node max = max(nodes);
			if(currentNode.getLevel() > 1 && currentNode.heuristic <= alphaBeta.get(currentNode.getLevel() -1)) {
				currentNode.skipSiblings = true;
			}
			int maxAlpha = Math.max(alphaBeta.get(currentNode.getLevel()),  (int)(max.heuristic));
			alphaBeta.set(currentNode.getLevel(), maxAlpha);
			return max;
		}
		// else, MIN level, return the node child node with min heuristic
		else {
			Node min = min(nodes);
			if(currentNode.getLevel() > 1 && currentNode.heuristic > alphaBeta.get(currentNode.getLevel() - 1)) {
				currentNode.skipSiblings = true;
			}
			int minBeta = Math.min(alphaBeta.get(currentNode.getLevel()), (int)min.heuristic);
			alphaBeta.set(currentNode.getLevel(), minBeta);
			return min;
		}
	}
	
	// Clone an ArrayList of Moves
	public ArrayList<Move> clone(ArrayList<Move> moves) {
		ArrayList<Move> copyMoves = new ArrayList<Move>();
		for(Move m: moves) {
			copyMoves.add(m.clone());
		}
		return copyMoves;
	}
	

	/*
	 * Returns the node with the highest heuristic out of the given nodes children
	 */
	public Node max (ArrayList<Node> children) {
		
		double highestHeuristic = children.get(0).heuristic;
		Node highestNode = children.get(0);
		
		for(Node n: children) {
			if(n.heuristic > highestHeuristic) {
				highestHeuristic = n.heuristic;
				highestNode = n;
			}
		}
		
		return highestNode;
	}
	
	/*
	 * Returns the node with the lowest heuristic out of the given nodes children
	 */
	public Node min (ArrayList<Node> children) {
		
		double lowestHeuristic = children.get(0).heuristic;
		Node lowestNode = children.get(0);
		
		for(Node n: children) {
			if(n.heuristic < lowestHeuristic) {
				lowestHeuristic = n.heuristic;
				lowestNode = n;
			}
		}
		
		return lowestNode;
	}
	
	/*
	 * Entry point of player.
	 * Keep processing input until player receives information
	 * that game is over.
	 */
//	public static void main(String[] args) throws IOException {
//		Player rp=new Player();
//		System.out.println(rp.playerName);
//
//		
//		boolean continueGame = rp.processInput();
//		
//		while(continueGame) {
//			continueGame = rp.processInput();
//		}
//	}
}
