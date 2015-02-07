/**
 * 
 */
package featureGenerator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * @author Dan True, Nick ***
 *
 */
public class DataReader {

	private static String inFilePath;  // Training data (without Features)
	private static String outFilePath; // Training data (with Features)
	
	private ArrayList<String> boardStrList; // Board data from file (Strings)
	private ArrayList<Board> boardList;     // Board data from file (Board objects)
	
	/*
	 * Parses the training data file into a list of Strings.
	 * Each String in the list is a different board.
	 * Returns an ArrayList<String> of the boards.
	 */
	public void readInData() {
		boardStrList = new ArrayList<String>();
		String boardStr;
		BufferedReader br;
		
		try {
			br = new BufferedReader(new FileReader(inFilePath));
			
			while((boardStr = br.readLine()) != null) {
				boardStrList.add(boardStr);
			}
			
			br.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Parses the list of Strings into a list of Board objects
	 */
	public void processBoards() {
		boardList = new ArrayList<Board>();
		for(String bStr: boardStrList) {
			boardList.add(Board.createBoardFromString(bStr));
		}
	}
	
	/*
	 * For each of the Boards in boardList, calculate their feature values.
	 * Then output the original board data, but add the feature values.
	 */
	public void getFeatureValues() {
		
		try {
			PrintWriter writer = new PrintWriter(outFilePath);
			
			for(int i = 0; i < boardList.size(); i++) {
				String featureVals = boardList.get(i).calculateFeatureValues();
				writer.println(boardStrList.get(i) + "," + featureVals);
			}
			
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Main entry to program.
	 * Takes in 2 arguments:
	 *      training set (text file)
	 *      name of output file for new data (data with feature values)
	 */
	public static void main(String[] args) {
		
		// Exit if there is an incorrect number of arguments
		if(args.length != 2) {
			System.out.println("Error: Wrong number of parameters.");
			System.exit(1);
		}
		
		inFilePath = args[0];
		outFilePath = args[1];
		
		DataReader dReader = new DataReader();
		
		dReader.readInData();       // Read in data from file
		dReader.processBoards();    // Parse data
		dReader.getFeatureValues(); // Calculate feature values, and output file with new data
		
		System.exit(0);
	}
}
