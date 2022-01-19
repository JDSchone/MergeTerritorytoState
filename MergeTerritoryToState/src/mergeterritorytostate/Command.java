package mergeterritorytostate;

//This class contains three pieces of information: the type
//of command, namely move or delete, the rep to be moved
//or deleted and the rep into which the other will be moved
//or deleted
public class Command {

	boolean move = false;
	String fromRep = "";
	String toRep = "";
	String in = "";
	
	//Reads a line from the delete_tree_out file and transforms it into a Command
	public Command(String line) {
		in = line;
		
		int tab0 = line.indexOf("\t");
		String first = line.substring(0, tab0);
		if (first.equals("move")) {
			move = true;
		}
		
		int tab1 = line.indexOf("\t", tab0 + 1);
		fromRep = line.substring(tab0 + 1, tab1);
		toRep = line.substring(tab1 + 1, line.length());
	}

	//Returns whether there is a move or a delete command
	//For some reason "move" as I typed it and "move" as the delete_tree command output it
	//were different at least once, so even though it looks the same, this makes the code
	//work even if move is the first command given.
	public boolean getMove() {
		String first = in.substring(0, in.indexOf("\t"));
		if (first.equals("﻿move")) {
			return true;
		}
		return move;
	}

	//Returns original input string
	public String getIn() {
		return in;
	}

	public String getFrom() {
		return fromRep;
	}

	public String getTo() {
		return toRep;
	}

}
