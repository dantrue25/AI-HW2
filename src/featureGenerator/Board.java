/**
 * @Author: Mainly Taken from given referee board class
 * @Author: Nicholas Muesch
 * @Author: Dan B. True
 */

package featureGenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
//import java.util.Random;

public class Board {

	int width;
	int height;
	int board[][];
	int numOfDiscsInColumn[];
	int emptyCell =0;
	int N;
	int PLAYER1 = 1;
	int PLAYER2 = 2;
	int NOCONNECTION=-1;
	int TIE = 0;
	boolean player1Pop = false;
	boolean player2Pop = false;

	Board(int height, int width, int N)
	{
		this.N = N;
		this.width = width;
		this.height = height;
		this.board = new int[height][width];
		numOfDiscsInColumn=new int[this.width];
	}
	
	/*
	 * Takes in a correctly formatted String and returns a Board
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
		//TODO: actually create this
		return "9,9,9,9,9";
	}
	
	//Returns a new board with the same state as the given board
	public Board clone()
	{
		int i = 0;
		int j = 0;
		Board b = new Board(this.height, this.width, this.N);
		for(i = 0; i < b. height;i++)
		{
			for(j = 0; j < b.width; j++)
			{
				b.board[i][j] = this.board[i][j];
			}
		}

		for(int k = 0; k < this.width; k++)
		{
			b.numOfDiscsInColumn[k] = this.numOfDiscsInColumn[k];
		}
		b.player1Pop = this.player1Pop;
		b.player2Pop = this.player2Pop;
		return b;
	}

	//Determine a value for the given board
	public double getHeuristic() {
		double hval = 0;
		for(int i = 2; i <= N; i++) {
			hval += checkV(i) + checkH(i) + checkD1(i) + checkD2(i) + checkHReverse(i) + checkMiddle();
		}
		return hval;
	}

	/*
	 *  Gives a heuristic based on the desity of the chips.
	 *  Returns a higher value if more chips in the center.
	 *  Used as experiment, but decided not to use in heuristic calculation.
	 */
	public double checkMiddle() {
		int sign = -1;
		int sum=0;

		for(int i = 0; i < this.width; i++) {

			if(i >= (int)(width/2) -1 && i <= (int)(width/2) + 1) {
				sign = 1;
			}
			sum += sign * this.numOfDiscsInColumn[i];
		}

		return sum;
	}


	//Given a player, use the current board state to create a list of possible moves
	public ArrayList<Move> getMoves(int player)
	{
		ArrayList<Move> possibleMoves = new ArrayList<Move>();
		if(isConnectN() != NOCONNECTION){ 
			return possibleMoves;
		}

		for(int i = 0; i < this.width; i++)
		{
			Move dropMove = new Move(i, 1);
			Move popMove = new Move(i, 0);


			if(this.canDropADiscFromTop(dropMove.column, player))
			{

				possibleMoves.add(dropMove);
			}

			if(this.canRemoveADiscFromBottom(popMove.column, player))
			{
				if( player == 1 && !player1Pop)
				{
					possibleMoves.add(popMove);
					player1Pop = true;
				}
				else if(player == 2 && !player2Pop)
				{
					possibleMoves.add(popMove);
					player2Pop = true;
				}
			}
		}
		return possibleMoves;
	}


	/* 
	 * Updates the currentState board based on the given move
	 * Dropping is 1
	 * Popping out is 0
	 * Need current player
	 * This function assumes that the move is valid
	 */
	public void makeMove(Move move, int playerNum)
	{
		if(move.moveType == 1)
		{
			this.dropADiscFromTop(move.column, playerNum);
		}
		else if(move.moveType == 0)
		{
			this.removeADiscFromBottom(move.column);
			if(playerNum == 1)
				player1Pop = true;
			else 
				player2Pop = true;
		}
	}

	//Prints the board to file
	public void printBoard(){
		try
		{
			String filename= "DebugBoard.txt";
			FileWriter fw = new FileWriter(filename,true); //the true will append the new data
			fw.write("Board \n");//appends the string to the file
			for(int j = 0; j < this.width; j++)
			{
				fw.write(this.numOfDiscsInColumn[j] + " ");
			}
			for(int i=0;i<height;i++){
				for(int j=0;j<width;j++){
					fw.write(board[i][j]+" ");
				}
				fw.write("\n");
			}
			fw.close();
		}
		catch(IOException ioe)
		{
			System.err.println("IOException: " + ioe.getMessage());
		}

	}
	
	public String printBoard2() {
		String boardStr = "";
		
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				boardStr += "" + board[i][j]+" ";
			}
			boardStr += "\n";
		}
		
		return boardStr;
	}

	//Checks to see if the given player can validly remove a piece from bottom
	//Check this function, seems weird
	public boolean canRemoveADiscFromBottom(int col, int currentPlayer){
		if(col<0 || col>=this.width) {
			return false;
		}
		else if(board[height-1][col]!=currentPlayer){
			return false;
		}
		else 
			return true;
	}

	//Actually remove a disc from the bottom
	//This currently just alters this board
	public void removeADiscFromBottom(int col){
		int i;
		for(i=height-1;i>height-this.numOfDiscsInColumn[col];i--){
			board[i][col]=board[i-1][col];
		}
		board[i][col]=this.emptyCell;
		this.numOfDiscsInColumn[col]--;
	}

	//Checks to see if dropping move is valid
	public boolean canDropADiscFromTop(int col, int currentPlayer){
		if(col<0 || col>=this.width) {
			return false;
		}
		else if(this.numOfDiscsInColumn[col]==this.height){
			return false;
		}
		else
			return true;
	}

	//Alters this board to Drop piece
	public void dropADiscFromTop(int col, int currentplayer){
		int firstEmptyCellRow=height-this.numOfDiscsInColumn[col]-1;
		board[firstEmptyCellRow][col]=currentplayer;
		this.numOfDiscsInColumn[col]++;
	}

	//Checks to see if board is already full
	public boolean isFull(){
		for(int i=0;i<height;i++)
			for(int j=0;j<width;j++){
				if(board[i][j]==this.emptyCell)
					return false;
			}
		return true;
	}

	//Update the board with the given row, column and playerNumber
	public void setBoard(int row, int col, int player){
		if(row>=height || col>=width)
			throw new IllegalArgumentException("The row or column number is out of bound!");
		if(player!=this.PLAYER1 && player!=this.PLAYER2)
			throw new IllegalArgumentException("Wrong player!");
		this.board[row][col]=player;
	}
	
	//Check to see if there is a winner
	public int isConnectN(){
		int tmp_winner=checkHorizontally();

		if(tmp_winner!=this.NOCONNECTION)
			return tmp_winner;

		tmp_winner=checkVertically();
		if(tmp_winner!=this.NOCONNECTION)
			return tmp_winner;

		tmp_winner=checkDiagonally1();
		if(tmp_winner!=this.NOCONNECTION)
			return tmp_winner; 
		tmp_winner=checkDiagonally2();
		if(tmp_winner!=this.NOCONNECTION)
			return tmp_winner; 

		return this.NOCONNECTION;
	}

	//Check the board horizontally to see if there is a winner
	public int checkHorizontally(){
		int max1=0;
		int max2=0;
		boolean player1_win=false;
		boolean player2_win=false;
		//check each row, horizontally
		for(int i=0;i<this.height;i++){
			max1=0;
			max2=0;
			for(int j=0;j<this.width;j++){
				if(board[i][j]==PLAYER1){
					max1++;
					max2=0;
					if(max1==N)
						player1_win=true;
				}
				else if(board[i][j]==PLAYER2){
					max1=0;
					max2++;
					if(max2==N)
						player2_win=true;
				}
				else{
					max1=0;
					max2=0;
				}
			}
		} 
		if (player1_win && player2_win)
			return this.TIE;
		if (player1_win)
			return this.PLAYER1;
		if (player2_win)
			return this.PLAYER2;

		return this.NOCONNECTION;
	}


	//Check the board vertically to see if there is a winner
	public int checkVertically(){
		//check each column, vertically
		int max1=0;
		int max2=0;
		boolean player1_win=false;
		boolean player2_win=false;

		for(int j=0;j<this.width;j++){
			max1=0;
			max2=0;
			for(int i=0;i<this.height;i++){
				if(board[i][j]==PLAYER1){
					max1++;
					max2=0;
					if(max1==N)
						player1_win=true;
				}
				else if(board[i][j]==PLAYER2){
					max1=0;
					max2++;
					if(max2==N)
						player2_win=true;
				}
				else{
					max1=0;
					max2=0;
				}
			}
		} 
		if (player1_win && player2_win)
			return this.TIE;
		if (player1_win)
			return this.PLAYER1;
		if (player2_win)
			return this.PLAYER2;

		return this.NOCONNECTION;
	}

	//Check the board diagonally to see if there is a winner
	public int checkDiagonally1(){
		//check diagonally y=-x+k
		int max1=0;
		int max2=0;
		boolean player1_win=false;
		boolean player2_win=false;
		int upper_bound=height-1+width-1-(N-1);

		for(int k=N-1;k<=upper_bound;k++){			
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
					if(max1==N)
						player1_win=true;
				}
				else if(board[height-1-y][x]==PLAYER2){
					max1=0;
					max2++;
					if(max2==N)
						player2_win=true;
				}
				else{
					max1=0;
					max2=0;
				}
				x--;
				y++;
			}	 

		}
		if (player1_win && player2_win)
			return this.TIE;
		if (player1_win)
			return this.PLAYER1;
		if (player2_win)
			return this.PLAYER2;

		return this.NOCONNECTION;
	}

	//Check the board diagonally to see if there is a winner
	public int checkDiagonally2(){
		//check diagonally y=x-k
		int max1=0;
		int max2=0;
		boolean player1_win=false;
		boolean player2_win=false;
		int upper_bound=width-1-(N-1);
		int  lower_bound=-(height-1-(N-1));
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
					if(max1==N)
						player1_win=true;
				}
				else if(board[height-1-y][x]==PLAYER2){
					max1=0;
					max2++;
					if(max2==N)
						player2_win=true;
				}
				else{
					max1=0;
					max2=0;
				}
				x++;
				y++;
			}	 

		}	 //end for y=x-k

		if (player1_win && player2_win)
			return this.TIE;
		if (player1_win)
			return this.PLAYER1;
		if (player2_win)
			return this.PLAYER2;

		return this.NOCONNECTION;
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
					if(max1 == N && Player.ourPlayerNum == 1)
						return 100;
					else if(max1 ==N && Player.ourPlayerNum != 1)
						return -100;
				}
				else if(board[i][j]==PLAYER2){
					max1=0;
					max2++;
					if(max2 == N && Player.ourPlayerNum == 2)
						return 100;
					else if(max2==N && Player.ourPlayerNum != 2)
						return -100;
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
		if(Player.ourPlayerNum == 1) {
			return player1Count - player2Count;
		}
		else
			return player2Count - player1Count;
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
					if(max1 == N && Player.ourPlayerNum == 1)
						return 100;
					else if(max1 ==N && Player.ourPlayerNum != 1)
						return -100;
				}
				else if(board[i][j]==PLAYER2){
					max1=0;
					max2++;
					if(max2 == N && Player.ourPlayerNum == 2)
						return 100;
					else if(max2==N && Player.ourPlayerNum != 2)
						return -100;
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
		if(Player.ourPlayerNum == 1) {
			return player1Count - player2Count;
		}
		else
			return player2Count - player1Count;
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
					if(max1==N && Player.ourPlayerNum == 1)
						return 100;
					else if(max1==N && Player.ourPlayerNum !=1)
						return -100;
				}
				else if(board[i][j]==PLAYER2){
					max1=0;
					max2++;
					if(max2==N && Player.ourPlayerNum == 2)
						return 100;
					else if(max2==N && Player.ourPlayerNum != 2)
						return -100;
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
		if(Player.ourPlayerNum == 1)
			return player1Count - player2Count;
		else
			return player2Count - player1Count;
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
					if(max1 == N && Player.ourPlayerNum == 1)
						return 100;
					else if(max1 == N && Player.ourPlayerNum != 1)
						return -100;
				}
				else if(board[height-1-y][x]==PLAYER2){
					max1=0;
					max2++;
					if(max2 == N && Player.ourPlayerNum == 2)
						return 100;
					else if(max2 == N && Player.ourPlayerNum != 2)
						return -100;
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

		if(Player.ourPlayerNum == 1)
			return player1Count - player2Count;
		else
			return player2Count - player1Count;
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
					if(max1 == N && Player.ourPlayerNum == 1)
						return 100;
					else if(max1 == N && Player.ourPlayerNum != 1)
						return -100;

				}
				else if(board[height-1-y][x]==PLAYER2){
					max1=0;
					max2++;
					if(max2 == N && Player.ourPlayerNum == 2)
						return 100;
					else if(max2 == N && Player.ourPlayerNum != 2)
						return -100;

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

		}	 //end for y=x-k

		if(Player.ourPlayerNum == 1) 
			return player1Count - player2Count;
		else
			return player2Count - player1Count;
	}
}
