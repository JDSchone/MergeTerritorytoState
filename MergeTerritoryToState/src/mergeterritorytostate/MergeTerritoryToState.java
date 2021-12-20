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
		
		String toOut = "";
		String toUncert = "";
		
		int i = 0;
		int size = commands.size();
		
		LinkedList<Rep> reps = new LinkedList<Rep>();
		
		while (i < size) {
			Command thisCommand = commands.get(i);
			
			Rep repFrom = new Rep(thisCommand.getFrom());
			Rep repTo = new Rep(thisCommand.getTo());
			
			int fromInt = seeIfNew(repFrom, reps);
			int toInt = seeIfNew(repTo, reps);
			//Only add from and to reps to the linked list if they are unique
			
			if (!thisCommand.getMove()) {
				if (reps.get(fromInt).getDeleteId().equals("0")) {
					//Move commands do not need any extra work done,
					//so just put out move command as entered
					//If it's a delete command, other "stuff" needs to happen first
					
					reps.set(toInt, repTo.addVariantNames(repFrom));
					reps.set(toInt, reps.get(toInt).addDispNames(repFrom));
					reps.set(toInt, reps.get(toInt).addAttrs(repFrom));
					reps.set(toInt, reps.get(toInt).addCites(repFrom));
				
					if (repFrom.isCertified()) {
						toUncert += "update_rep_exp\t" + repFrom.getRepId() + "\tvalidate\tfalse\r\n";
					}
				
					if (difDate(reps.get(fromInt), reps.get(toInt))) {
						reps.set(toInt, reps.get(toInt).changeDate(repFrom));
						toUncert += "update_rep_exp\t" + repTo.getRepId() + "\tvalidate\tfalse\r\n";
					}
					else if (betterType(reps.get(fromInt), reps.get(toInt))) {
						reps.set(toInt, reps.get(toInt).changeType(repFrom));
						toUncert += "update_rep_exp\t" + repTo.getRepId() + "\tvalidate\tfalse\r\n";
					}
				
					if (betterType(reps.get(fromInt), reps.get(toInt))) {
						reps.set(toInt, reps.get(toInt).changeType(repFrom));
						toUncert += "update_rep_exp\t" + repTo.getRepId() + "\tvalidate\tfalse\r\n";
					}
				}
				//In the case that one or more attributes, names or citations may
				//possibly be redundant, the system will still include it but will
				//put in a flag to say this place may have unneeded information.
			}
			
			toOut += reps.get(toInt).toOut();
			toOut += thisCommand.getIn() + "\r\n";
			
			i++;
		}
		
		String outfile2 = filepath.substring(0, filepath.lastIndexOf("\\")+1) + "Uncertify.txt";
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(outfile2));
		bw2.write(toUncert);
		bw2.close();
		
		String outfile1 = filepath.substring(0, filepath.lastIndexOf("\\")+1) + "MergeTerritoryToState.txt";
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(outfile1));
		bw1.write(toOut);
		bw1.close();
	}

	private static boolean betterType(Rep rep1, Rep rep2) {
		String type1 = rep1.getType();
		String type2 = rep2.getType();
		
		if (type2.equals("140") || type2.equals("64")) {
			return false;
		}
		
		if (type1.equals("140") || type2.equals("64")) {
			return true;
		}
		
		return false;
	}

	private static boolean difDate(Rep rep1, Rep rep2) {
		String date1 = rep1.getFromDate();
		String date2 = rep2.getFromDate();
		
		return date1.equals(date2);
	}

	//Walks the tree to be sure that the rep being added is unique, then adds it if it is unique
	private static int seeIfNew(Rep repFrom, LinkedList<Rep> reps) {
		int toRet = -1;
		
		String fromId = repFrom.getRepId();
		int j = 0;
		int size = reps.size();
		boolean has = false;
		
		while (j < size && !has) {
			Rep thisRep = reps.get(j);
			String repId = thisRep.getRepId();
			
			if (repId.equals(fromId)) {
				has = true;
				toRet = j;
			}
			else {
				j++;
				//Without this as an "else", the numbers get thrown off
			}
		}
		
		if (!has) {
			reps.add(repFrom);
			toRet = reps.size() - 1;
			//Because reps.size() is a value instead of an index. It counts "1, 2, 3"
			//instead of "0, 1, 2", so it has to be -1
		}
		return toRet;
	}
	
}
