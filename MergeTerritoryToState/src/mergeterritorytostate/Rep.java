package mergeterritorytostate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;

//This class contains the ripped information
//of any rep in any move or delete command
//including lists of their attributes, names
//and citations

public class Rep {

	LinkedList<DisplayName> dnames = new LinkedList<DisplayName>();
	LinkedList<VariantName> vnames = new LinkedList<VariantName>();
	LinkedList<Citation> cites = new LinkedList<Citation>();
	LinkedList<Attr> attrs = new LinkedList<Attr>();
	String repId = "";
	String parentId = "";
	String placeId = "";
	boolean certified = false;
	String deleteId = "0";
	String fromDate = "";
	String toDate = "";
	String type = "140";
	LinkedList<String> changes = new LinkedList<String>();
	boolean written = false;
	
	public Rep(String inId) throws IOException {
		repId = inId;
		URL url = new URL("http://ws.solr-repeater.standards.service.prod.us-east-1.prod.fslocal.org/places/select?wt=json&q=repId:" + repId);

		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
		
		String thisLine = br.readLine();
		String repStr = "";
		
		while (thisLine != null) {
			repStr += thisLine + "\r\n";
			thisLine = br.readLine();
		}
		//Gets the place id, parent id, type id, delete id (as applicable) and
		//certification status
		placeId = getFact(repStr, "ownerId");
		parentId = getFact(repStr, "parentId");
		type = getFact(repStr, "type");
		deleteId = getFact(repStr, "deleteId");
		certified = getBool(repStr, "validated");
		
		//Sets the start and end dates, but "no date" becomes "";
		fromDate = getFact(repStr, "startYear");
		toDate = getFact(repStr, "endYear");
		if (fromDate.equals("-2147483648")) {
			fromDate = "";
		}
		if (toDate.equals("2147483647")) {
			toDate = "";
		}
		
		//Gets the variable sized "sets" of information, namely names, attrs and cites
		dnames = getDNames(getSet(repStr, "displayNames"));
		vnames = getVNames(getSet(repStr, "variantNames"));
		
		if (repStr.contains("attributes")) {
			attrs = getAttrs(getSet(repStr, "attributes"));
		}
		
		if (repStr.contains("citations")) {
			cites = getCites(getSet(repStr, "citations"));
		}
	}

	//Puts all Citations into a list
	public LinkedList<Citation> getCites(String set) {
		LinkedList<Citation> toRet = new LinkedList<Citation>();
		LinkedList<String> extracted = extract(set, 5);
		
		int i = 0;
		int size = extracted.size();
		
		while (i < size) {
			String thisStr = extracted.get(i);
			if (thisStr.contains("|")) { //Does nothing if there are no citations
				Citation toAdd = new Citation(thisStr);
				toRet.add(toAdd);
			}
			i++;
		}
		
		return toRet;
	} 
	
	//Puts all Attributes into a list
	public LinkedList<Attr> getAttrs(String set) {
		LinkedList<Attr> toRet = new LinkedList<Attr>();
		LinkedList<String> extracted = extract(set, 10);
		
		int i = 0;
		int size = extracted.size();
		
		while (i < size) {
			String thisStr = extracted.get(i);
			if (thisStr.contains("|")) { //Does nothing if there are no attributes
				Attr toAdd = new Attr(thisStr);
				toRet.add(toAdd);
			}
			i++;
		}
		
		return toRet;
	}
	
	//Puts all Variant Names into a list
	public LinkedList<VariantName> getVNames(String set) {
		LinkedList<VariantName> toRet = new LinkedList<VariantName>();
		LinkedList<String> extracted = extract(set, 3);
		
		int i = 0;
		int size = extracted.size();
		
		while (i < size) {
			String thisStr = extracted.get(i);
			VariantName toAdd = new VariantName(thisStr);

			toRet.add(toAdd);
			
			i++;
		}
		
		return toRet;
	}
	
	//Puts all Display Names into a list
	public LinkedList<DisplayName> getDNames(String set) {
		LinkedList<DisplayName> toRet = new LinkedList<DisplayName>();
		LinkedList<String> extracted = extract(set, 1);
		
		int i = 0;
		int size = extracted.size();
		
		while (i < size) {
			String thisStr = extracted.get(i);
			DisplayName toAdd = new DisplayName(thisStr);
			
			toRet.add(toAdd);
			
			i++;
		}
		
		return toRet;
	}
	
	//Extracts strings from every set which will then be sent through various processes
	public LinkedList<String> extract(String set, int numbars) {
		int oquo = set.indexOf("\"");
		int skipindex = getSkipIndex(set, oquo + 1, numbars);
		int equo = set.indexOf("\"", skipindex + 1);
		
		LinkedList<String> toRet = new LinkedList<String>();
		
		while (oquo != -1 && equo != -1 && skipindex != -1) {
			String substr = set.substring(oquo + 1, equo);
			toRet.add(substr);
			
			oquo = set.indexOf("\"", equo + 1);
			skipindex = getSkipIndex(set, oquo, numbars);
			equo = set.indexOf("\"", skipindex + 1);
		}
		
		return toRet;
	}

	public int getSkipIndex(String set, int oquo, int numbars) {
		int i = 0;
		int index = oquo;
		
		while (i < numbars) {
			index = set.indexOf("|", index + 1);
			
			if (index == -1) {
				return -1;
			}
			
			i++;
		}
		return index;
	}

	//Gets sets of data from Solr that start with [ and end with ]
	public String getSet(String repStr, String stat) {
		int i1 = repStr.indexOf(stat);
		int i2 = repStr.indexOf(":[", i1 + 1);
		int i3 = repStr.indexOf("],\r\n", i2 + 1);
		
		if (i2 == -1 || i3 == -1) {
			return "";
		}
		
		String toOut = repStr.substring(i2 + 2, i3);
		return toOut;
	}

	//Determines whether boolean is 1 (true) or 0 (false)
	public boolean getBool(String repStr, String stat) {
		String fact = getFact(repStr, stat);
		if (fact.equalsIgnoreCase("1")) {
			return true;
		}
		return false;
	}
	
	//Determines the value of the entry connected to the stat.
	//If the stat is "ownerId", this formula will return
	//the number listed after "ownerId".
	public String getFact(String repStr, String stat) {
		int i1 = repStr.indexOf(stat);
		int i2 = repStr.indexOf(":", i1 + 1);
		int i3 = repStr.indexOf(",", i2 + 1);
		
		if (i2 == -1 || i3 == -1) {
			return "";
		}
		
		String toOut = repStr.substring(i2 + 1, i3);
		return toOut;
	}

	
	public String getRepId() {
		return repId;
	}


	public boolean isCertified() {
		return certified;
	}

	public Rep addVariantNames(Rep fromRep) {
		LinkedList<VariantName> fromNames = fromRep.getVNames();
		
		int i = 0;
		int sizei = fromNames.size();
		
		while (i < sizei) {
			int j = 0;
			int sizej = vnames.size();
			boolean done = false;
			
			VariantName namei = fromNames.get(i);
			
			while (j < sizej && !done) {
				VariantName namej = vnames.get(j);
				done = namei.sameAs(namej);
	
				j++;
			}
			
			if (!done) {
				vnames.add(namei);
				String change = addVarName(namei);
				changes.add(change);
			}
			
			i++;
		}
		
		return this;
	}
	
	public Rep addAttrs(Rep fromRep) {
		LinkedList<Attr> fromAttrs = fromRep.getAttrs();
		
		int i = 0;
		int sizei = fromAttrs.size();
		
		while (i < sizei) {
			int j = 0;
			int sizej = attrs.size();
			boolean done = false;
			
			Attr attri = fromAttrs.get(i);
			
			while (j < sizej && !done) {
				Attr attrj = attrs.get(j);
				done = attri.sameAs(attrj);
				
				j++;
			}
			
			if (!done) {
				attrs.add(attri);
				String change = addAttr(attri);
				changes.add(change);
			}
			
			i++;
		}
		
		return this;
	}

	public String addAttr(Attr attri) {
		String toRet = "add_attr_exp\t" + repId + "\t";
		toRet += attri.getType() + "\t";
		toRet += attri.getStYear() + "\t";
		toRet += attri.getEnYear() + "\t";
		toRet += attri.getText() + "\t";
		toRet += attri.getLang() + "\t";
		toRet += attri.getTitle() + "\t";
		toRet += attri.getCUrl() + "\t";
		toRet += attri.getCopyright() + "\t";
		toRet += attri.getUrl() + "\t";
		toRet += attri.getUrlTitle();
		
		if (attri.isFlagged()) {
			toRet += "\t#Some of this attribute appears redundant with an existing attribute.";
		}
		
		return toRet;
	}

	public LinkedList<Attr> getAttrs() {
		return attrs;
	}

	//Adds a variant name where the language and name were not previously represented
	public String addVarName(VariantName namei) {
		String toRet = "update_place_exp\t" + placeId + "\tvariant\t";
		toRet += namei.getName() + "\t";
		
		toRet += namei.getLang() + "\t";
		toRet += namei.getType();
		
		if (namei.isFlagged()) {
			toRet += "\t#This place has this name already but under a different locale code.";
		}
		
		return toRet;
	}

	public Rep addDispNames(Rep fromRep) {
		LinkedList<DisplayName> fromNames = fromRep.getDNames();
		
		int i = 0;
		int sizei = fromNames.size();
		
		while (i < sizei) {
			int j = 0;
			int sizej = dnames.size();
			boolean done = false;
			
			DisplayName namei = fromNames.get(i);
			
			while (j < sizej && !done) {
				DisplayName namej = dnames.get(j);
				done = namei.sameAs(namej);
				
				j++;
			}
			
			if (!done) {
				dnames.add(namei);
				String change = addDispName(namei);
				changes.add(change);
			}
			
			i++;
		}
		
		return this;
	}

	//Adds a display name where the language of the display name was not previously represented
	public String addDispName(DisplayName namei) {
		String toRet = "update_rep_exp\t" + repId + "\tdisplay\t";
		toRet += namei.getLang() + "\t";
		toRet += namei.getName();
		
		if (namei.isFlagged()) {
			toRet += "\t#This place already has this Display Name but with a different Locale code";
		}
		
		return toRet;
	}

	public LinkedList<DisplayName> getDNames() {
		return dnames;
	}
	
	public LinkedList<VariantName> getVNames() {
		return vnames;
	}

	public Rep addCites(Rep other) {
		LinkedList<Citation> fromCites = other.getCites();
		
		int i = 0;
		int sizei = fromCites.size();
		
		while (i < sizei) {
			int j = 0;
			int sizej = cites.size();
			boolean done = false;
			
			Citation citei = fromCites.get(i);
			
			while (j < sizej && !done) {
				Citation citej = cites.get(j);
				done = citei.sameAs(citej);
				
				j++;
			}
			
			if (!done) {
				cites.add(citei);
				String change = addCite(citei);
				changes.add(change);
			}
			
			i++;
		}
		
		return this;
	}

	public String addCite(Citation citei) {
		String toRet = "add_citation_exp\t" + repId + "\t";
		toRet += citei.getSource() + "\t";
		toRet += citei.getDate() + "\t";
		toRet += citei.getUrl() + "\t";
		toRet += citei.getDescription();
		
		if (citei.isFlagged()) {
			toRet += "\t#Some of this citation appears redundant with an existing citation.";
		}
		
		return toRet;
	}

	public LinkedList<Citation> getCites() {
		return cites;
	}

	public String getFromDate() {
		return fromDate;
	}

	public Rep changeDate(Rep repFrom) {
		String oDate = repFrom.getFromDate();
		fromDate = oDate;
		
		if (oDate.equals("0")) {
			oDate = "-2147483648";
		}
		
		String change = "update_rep_exp\t" + repId + "\tfrom\t" + oDate;
		changes.add(change);
		
		return this;
	}

	public String getType() {
		return type;
	}

	public Rep changeType(Rep repFrom) {
		String oType = repFrom.getType();
		type = oType;
		
		String change = "update_rep_exp\t" + repId + "\ttype\t" + oType;
		changes.add(change);
		
		return this;
	}

	public String toOut() {
		if (written) {
			return "";
		}
		int i = 0;
		int size = changes.size();
		
		String toOut = "";
		
		while (i < size) {
			toOut += changes.get(i) + "\r\n";
			i++;
		}
		written = true;
		return toOut;
	}

	public String getDeleteId() {
		return deleteId;
	}

}
