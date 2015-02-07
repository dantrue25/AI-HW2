/*
 * @Authors: Daniel B True      dbtrue@wpi.edu
 * @Authors: Nicholas Muesch    nmmuesch@wpi.edu
 */

package featureGenerator;

import java.util.ArrayList;


/**
 * 
 * @author Nick
 * @author Dan
 */

public class Node {
	public double heuristic;
	public ArrayList<Move> moves;
	public ArrayList<Node> children;
	public boolean skipSiblings = false;
	
	/*
	 * Constructor.
	 * Takes in a list of moves which represents the moves needed to get
	 * from the current state of the board to this node's state.
	 * Also creates an empty list for its children.
	 */
	public Node(ArrayList<Move> moves) {
		this.moves = moves;
		this.children = new ArrayList<Node>();
	}
	
	/*
	 * Adds a node to this nodes children
	 */
	public void addChild(Node child){
		children.add(child);
	}
	
	/*
	 * Returns the depth level of this Node by getting the size of its move List
	 */
	public int getLevel() {
		return moves.size();
	}
	
}
