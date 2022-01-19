package mergeterritorytostate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

//This class is meant to take all places currently listed
//under a given Territory and either move them under the
//State or delete them. In the case of deletion, this ensures
//that no citations, attributes or names that provide meaningful,
//useful data are lost.

public class MergeTerritoryToState {

	public static void main(String args[]) throws IOException {
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter full path to delete tree output file:");
		String filepath = scan.nextLine();
		scan.close();
		//Allows user to input any file.
		
		BufferedReader br = new BufferedReader(new FileReader(filepath));
		LinkedList<Command> commands = new LinkedList<Command>();
		String line = br.readLine();
		
		while (line != null) {
			Command newCommand = new Command(line);
			commands.add(newCommand);
			line = br.readLine();
		}
		br.close();
		//Reads through the input file and transforms all delete and move commands into
		//a list of Command objects.
		
		Merger merge = new Merger(commands, filepath);
		String toOut = merge.doIt();
		
		String part = commands.get(commands.size() - 1).getFrom();
		String path = filepath.substring(0, filepath.lastIndexOf("\\")+1);
		String outfile = path + "MergeTerritoryToState_" + part + ".txt";
		
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(outfile));
		bw1.write(toOut);
		bw1.close();
		System.out.println("Done.");
	}
}
