package mergeterritorytostate;

//This class contains the basic information on any given place's Citation(s)
public class Citation {

	String id = "";
	String source = "";
	String type = "";
	String date = "";
	String description = "";
	String url = "";
	boolean redFlag = false;
	
	public Citation(String thisStr) {
		int bar0 = thisStr.indexOf("|", 0);
		int bar1 = thisStr.indexOf("|", bar0 + 1);
		int bar2 = thisStr.indexOf("|", bar1 + 1);
		int bar3 = thisStr.indexOf("|", bar2 + 1);
		int bar4 = thisStr.indexOf("|", bar3 + 1);
		
		id = thisStr.substring(0, bar0);
		source = thisStr.substring(bar0 + 1, bar1);
		type = thisStr.substring(bar1 + 1, bar2);
		date = thisStr.substring(bar2 + 1, bar3);		
		description = thisStr.substring(bar3 + 1, bar4);
		url = thisStr.substring(bar4 + 1, thisStr.length());
	}

	public boolean sameAs(Citation other) {
		String ourl = other.getUrl();
		String odes = other.getDescription();
		String osource = other.getSource();
		
		boolean toRet = (url.equalsIgnoreCase(ourl) && description.equalsIgnoreCase(odes)) ||
				ourl.contains("NGA") || odes.contains("NGA") || ourl.contains("ODM") ||
				odes.contains("ODM") || osource.contains("NGA") || osource.contains("ODM") ||
				url.contains("NGA") || description.contains("NGA") || url.contains("ODM") ||
				description.contains("ODM") || source.contains("NGA") || source.contains("ODM")
				|| url.contains("ahcbp");
		
		if (!toRet && !redFlag) {
			redFlag = (url.equalsIgnoreCase(ourl) && !description.equalsIgnoreCase(odes)) ||
					(!url.equalsIgnoreCase(ourl) && description.equalsIgnoreCase(odes)) || 
					(url.equalsIgnoreCase(ourl) && !description.equalsIgnoreCase(odes)) ||
					(!url.equalsIgnoreCase(ourl) && description.equalsIgnoreCase(odes)) ||
					(!url.equalsIgnoreCase(ourl) && !description.equalsIgnoreCase(odes));
		}
		
		return toRet;
	}

	public String getDescription() {
		return description;
	}

	public String getUrl() {
		return url;
	}

	public String getSource() {
		if (source.equals("1476") || source.equals("1477") || source.equals("1478")) {
			return "1476";
		}
		return "1483";
	}

	public String getDate() {
		return date;
	}

	public boolean isFlagged() {
		return redFlag;
	}

}
