/**
 * 
 */
package featureGenerator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Dan True, Nick ***
 *
 */
public class DataReader {

	private static String inFilePath;  // Training data (without Features)
	private static String outFilePath; // Training data (with Features)

	private ArrayList<String> boardStrList;         // Board data from file (Strings)
	private ArrayList<Board> boardList;             // Board data from file (Board objects)
	
	public static boolean debugMode = false;

	public DataReader() {
		if(debugMode) {
			try {
				FileWriter fw = new FileWriter("debugFile.txt");
				Date d = new Date();
				fw.write(d.toString() + "\n\n");
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

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
	 * Parses the list of Strings into a list of Board objects.
	 * Also, creates another list of boardStr's without the winner.
	 */
	public void processBoards() {
		boardList = new ArrayList<Board>();
		int count = 0;

		try {

			for(String bStr: boardStrList) {
				boardList.add(Board.createBoardFromString(bStr));

				if(debugMode) {
					FileWriter fw = new FileWriter("debugFile.txt", true);
					fw.write("------------------------------------\n");
					fw.write("Board " + count + "\n\n");				
					fw.write(boardList.get(count).printBoard() + "\n\n");
					fw.close();
				}

				boardList.get(count).calculateFeatureValues();
				count++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	/*
	 * For each of the Boards in boardList, calculate their feature values.
	 * Then output the original board data, but add the feature values.
	 */
	public void getFeatureValues() {

		try {
			PrintWriter writer = new PrintWriter(outFilePath);

			writer.println("f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,"
					+ "f11,f12,f13,f14,f15,f16,f17,f18,f19,f20,"
					+ "f21,f22,f23,f24,f25,f26,f27,f28,f29,f30,"
					+ "f31,f32,f33,f34,f35,f36,f37,f38,f39,f40,"
					+ "f41,f42,Middle,Connect2,Connect3,"
					+ "HHole,M+C2+C3+HH,Winner");

			for(int i = 0; i < boardList.size(); i++) {
				String currentBStr = boardStrList.get(i);
				String featureVals = boardList.get(i).calculateFeatureValues();

				String bStrWithoutWinner = currentBStr.substring(0, currentBStr.length() - 1);
				writer.println(bStrWithoutWinner + featureVals + "," + currentBStr.charAt(currentBStr.length() - 1));
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
		if(args.length != 2 && args.length != 3) {
			System.out.println("Error: Wrong number of parameters.");
			System.exit(1);
		}
		
		if(args.length == 3 && args[2].equals("d"))
			debugMode = true;

		inFilePath = args[0];
		outFilePath = args[1];

		DataReader dReader = new DataReader();

		dReader.readInData();       // Read in data from file
		dReader.processBoards();    // Parse data
		dReader.getFeatureValues(); // Calculate feature values, and output file with new data

		System.exit(0);
	}
}
