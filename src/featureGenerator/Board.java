/**
 * @Author: Mainly Taken from given referee board class
 * @Author: Nicholas Muesch
 * @Author: Dan B. True
 */

package featureGenerator;

import java.io.FileWriter;
import java.io.IOException;

public class Board {

	int width;
	int height;
	int board[][];
	int N;
	int emptyCell = 0;
	int PLAYER1 = 1;
	int PLAYER2 = 2;

	/*
	 * Constructor
	 */
	Board(int height, int width, int N)
	{
		this.N = N;
		this.width = width;
		this.height = height;
		this.board = new int[height][width];
	}

	/*
	 * Takes in a correctly formatted String and returns a Board.
	 */
	public static Board createBoardFromString(String boardStr) {
		Board newBoard = new Board(6, 7, 4);
		String[] boardArr = boardStr.split(",");

		for(int i = 0; i <= 6; i++) {
			for(int j = 0; j <= 5; j++) {
				newBoard.board[j][i] = Integer.parseInt(boardArr[6*i+5-j]);
			}
		}

		return newBoard;
	}

	/*
	 * Calculate the values for each feature (attribute)
	 */
	public String calculateFeatureValues() {

		int feat1 = checkMiddle();
		int feat2 = checkNInARow(2);
		int feat3 = checkNInARow(3);
		int feat4 = feat2 + 2*feat3;
		int feat5 = 99;

		String featuresStr = feat1 + 
				"," + feat2 + "," + feat3 + 
				"," + feat4 + "," + feat5;

		if(DataReader.debugMode) {
			try {
				FileWriter fw = new FileWriter("debugFile.txt", true);
				fw.write("\nMiddle:  " + feat1 + "\n");
				fw.write("2inARow: " + feat2 + "\n");
				fw.write("3inARow: " + feat3 + "\n");
				fw.write("Total:   " + feat4 + "\n");
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return featuresStr;
	}

	/*
	 * Returns the difference of the number of N in a rows between P1 and P2.
	 */
	public int checkNInARow(int numInARow) {

		int count = checkVReverse(numInARow) + 
				checkH(numInARow) + checkHReverse(numInARow) + 
				checkD1(numInARow) + checkD2(numInARow);

		return count;
	}

	/*
	 *  Gives a heuristic based on the desity of the chips.
	 *  Returns a higher value if more chips in the center.
	 *  Used as experiment, but decided not to use in heuristic calculation.
	 */
	public int checkMiddle() {

		int player1Count = 0;
		int player2Count = 0;
		int[] weights = new int[]{0, 0, 1, 2, 1, 0, 0};

		for(int i = 0; i < this.height; i++) {
			for(int j = 0; j < this.width; j++) {
				if(this.board[i][j] == PLAYER1) {
					player1Count += weights[j];
				}
				else if(this.board[i][j] == PLAYER2) {
					player2Count += (-1) * weights[j];
				}
			}
		}
		return (player1Count + player2Count);
	}

	/*
	 * Returns a String representation of the board.
	 */
	public String printBoard() {
		String boardStr = "";

		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				boardStr += "" + board[i][j]+" ";
			}
			boardStr += "\n";
		}

		return boardStr;
	}

	/*
	 * *********************************************************************************
	 * Our heuristic helper functions.
	 * Count of the number of times there are pieces of the same player in a row
	 * of the given number for both players, and returns the difference.
	 * For example checkH of 3 would return the number of 3 in a rows our player has
	 * minus the number of 3 in a rows the opponent has.
	 * These are used as the heart of our heuristic function.
	 * Each direction (horizontal, vertical, and the two diagonals) have their
	 * seperate functions.
	 */

	//Used for the get heuristic function to evaluate a given board
	public int checkH(int numInARow){
		int max1=0;
		int max2=0;
		int player1Count = 0;
		int player2Count = 0;
		//check each row, horizontally
		for(int i=0;i<this.height;i++){
			max1=0;
			max2=0;
			for(int j=0;j<this.width;j++){
				if(board[i][j]==PLAYER1){
					max1++;
					max2=0;
				}
				else if(board[i][j]==PLAYER2){
					max1=0;
					max2++;
				}
				else{
					if(max1 >= numInARow) {
						player1Count++;
					}
					if(max2 >= numInARow) {
						player2Count++;
					}
					max1=0;
					max2=0;
				}
			}
		}

		if(DataReader.debugMode) {
			try {
				FileWriter fw = new FileWriter("debugFile.txt", true);
				fw.write(numInARow + "     H: P1 = " + player1Count + "\n");
				fw.write(numInARow + "        P2 = " + player2Count + "\n");
				fw.write(numInARow + "        Total = " + (player1Count - player2Count) + "\n");
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return player1Count - player2Count;
	}

	//Used for the get heuristic function to evaluate a given board
	//Checks num in a row from right to left
	public int checkHReverse(int numInARow){
		int max1=0;
		int max2=0;
		int player1Count = 0;
		int player2Count = 0;
		//check each row, horizontally
		for(int i=0;i<this.height;i++){
			max1=0;
			max2=0;
			for(int j=this.width-1; j >= 0;j--){
				if(board[i][j]==PLAYER1){
					max1++;
					max2=0;
				}
				else if(board[i][j]==PLAYER2){
					max1=0;
					max2++;
				}
				else{
					if(max1 >= numInARow) {
						player1Count++;
					}
					if(max2 >= numInARow) {
						player2Count++;
					}
					max1=0;
					max2=0;
				}
			}
		}

		if(DataReader.debugMode) {
			try {
				FileWriter fw = new FileWriter("debugFile.txt", true);
				fw.write(numInARow + "    Hr: P1 = " + player1Count + "\n");
				fw.write(numInARow + "        P2 = " + player2Count + "\n");
				fw.write(numInARow + "        Total = " + (player1Count - player2Count) + "\n");
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return player1Count - player2Count;
	}

	//The heuristic function uses this to determine if this is a good board state
	public int checkV(int numInARow){
		//check each column, vertically
		int max1=0;
		int max2=0;
		int player1Count = 0;
		int player2Count = 0;

		for(int j=0;j<this.width;j++){
			max1=0;
			max2=0;
			for(int i=0;i<this.height;i++){
				if(board[i][j]==PLAYER1){
					max1++;
					max2=0;
				}
				else if(board[i][j]==PLAYER2){
					max1=0;
					max2++;
				}
				else{
					if(max1 >= numInARow)
						player1Count++;
					if(max2 >= numInARow)
						player2Count++;
					max1=0;
					max2=0;
				}
			}
		}

		if(DataReader.debugMode) {	
			try {
				FileWriter fw = new FileWriter("debugFile.txt", true);
				fw.write(numInARow + "     V: P1 = " + player1Count + "\n");
				fw.write(numInARow + "        P2 = " + player2Count + "\n");
				fw.write(numInARow + "        Total = " + (player1Count - player2Count) + "\n");
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return player1Count - player2Count;
	}

	public int checkVReverse(int numInARow){
		//check each column, vertically
		int max1=0;
		int max2=0;
		int player1Count = 0;
		int player2Count = 0;

		for(int j=0;j<this.width;j++){
			max1=0;
			max2=0;
			for(int i=this.height - 1;i>=0;i--){
				if(board[i][j]==PLAYER1){
					max1++;
					max2=0;
				}
				else if(board[i][j]==PLAYER2){
					max1=0;
					max2++;
				}
				else{
					if(max1 >= numInARow)
						player1Count++;
					if(max2 >= numInARow)
						player2Count++;
					max1=0;
					max2=0;
				}
			}
		}

		if(DataReader.debugMode) {	
			try {
				FileWriter fw = new FileWriter("debugFile.txt", true);
				fw.write(numInARow + "    Vr: P1 = " + player1Count + "\n");
				fw.write(numInARow + "        P2 = " + player2Count + "\n");
				fw.write(numInARow + "        Total = " + (player1Count - player2Count) + "\n");
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return player1Count - player2Count;
	}

	//Used by the heuristic function to determine how many diags in a row we have
	public int checkD1(int numInARow){
		//check diagonally y=-x+k
		int max1=0;
		int max2=0;
		int player1Count=0;
		int player2Count=0;
		int upper_bound=height-1+width-1-(numInARow-1);

		for(int k=numInARow-1;k<=upper_bound;k++){			
			max1=0;
			max2=0;
			int x,y;
			if(k<width) 
				x=k;
			else
				x=width-1;
			y=-x+k;

			while(x>=0  && y<height){
				// System.out.println("k: "+k+", x: "+x+", y: "+y);
				if(board[height-1-y][x]==PLAYER1){
					max1++;
					max2=0;
				}
				else if(board[height-1-y][x]==PLAYER2){
					max1=0;
					max2++;
				}
				else{
					if(max1 >= numInARow) {
						player1Count++;
					}
					if(max2 >= numInARow) {
						player2Count++;
					}
					max1=0;
					max2=0;
				}
				x--;
				y++;
			}	  
		}

		if(DataReader.debugMode) {
			try {
				FileWriter fw = new FileWriter("debugFile.txt", true);
				fw.write(numInARow + "    D1: P1 = " + player1Count + "\n");
				fw.write(numInARow + "        P2 = " + player2Count + "\n");
				fw.write(numInARow + "        Total = " + (player1Count - player2Count) + "\n");
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return player1Count - player2Count;
	}

	//Check the board diagonally to see if there is a winner
	public int checkD2(int numInARow){
		//check diagonally y=x-k
		int max1=0;
		int max2=0;
		int player1Count = 0;
		int player2Count = 0;
		int upper_bound=width-1-(numInARow-1);
		int  lower_bound=-(height-1-(numInARow-1));
		// System.out.println("lower: "+lower_bound+", upper_bound: "+upper_bound);
		for(int k=lower_bound;k<=upper_bound;k++){			
			max1=0;
			max2=0;
			int x,y;
			if(k>=0) 
				x=k;
			else
				x=0;
			y=x-k;
			while(x>=0 && x<width && y<height){
				// System.out.println("k: "+k+", x: "+x+", y: "+y);
				if(board[height-1-y][x]==PLAYER1){
					max1++;
					max2=0;
				}
				else if(board[height-1-y][x]==PLAYER2){
					max1=0;
					max2++;
				}
				else{
					if(max1 >= numInARow) 
						player1Count++;
					if(max2 >= numInARow)
						player2Count++;
					max1=0;
					max2=0;
				}
				x++;
				y++;
			}	 
		}

		if(DataReader.debugMode) {
			try {
				FileWriter fw = new FileWriter("debugFile.txt", true);
				fw.write(numInARow + "    D2: P1 = " + player1Count + "\n");
				fw.write(numInARow + "        P2 = " + player2Count + "\n");
				fw.write(numInARow + "        Total = " + (player1Count - player2Count) + "\n");
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return player1Count - player2Count;
	}
}
