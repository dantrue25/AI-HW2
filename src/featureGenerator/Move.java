/*
 * @Authors: Daniel B True      dbtrue@wpi.edu
 * @Authors: Nicholas Muesch    nmmuesch@wpi.edu
 */
package featureGenerator;

/**
 * @author Dan
 *
 */
public class Move {
	public int column;
	public int moveType;
	public int row;
	/**
	 * Constructor
	 * @param column
	 * @param moveType
	 */
	public Move (int column, int moveType) {
		this.column = column;
		this.moveType = moveType;
	}
	
	/*
	 * Returns a clone of the given Move 
	 */
	public Move clone() {
		Move newMove = new Move(this.column, this.moveType);
		newMove.row = this.row;
		return newMove;
	}
	
	
	/**
	 * Used to pass referee our move
	 */
	public String toString () {
		return "" + column + " " + moveType;
	}

}
