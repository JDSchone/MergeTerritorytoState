package mergeterritorytostate;

//This class contains the basic information on any given place's Variant Name(s)
public class VariantName {

	String index = "";
	String type = "";
	String lang = "";
	String name = "";
	boolean redFlag = false;
	
	public VariantName(String thisStr) {
		int bar0 = thisStr.indexOf("|", 0);
		int bar1 = thisStr.indexOf("|", bar0 + 1);
		int bar2 = thisStr.indexOf("|", bar1 + 1);
		
		index = thisStr.substring(0, bar0);
		type = thisStr.substring(bar0 + 1, bar1);
		lang = thisStr.substring(bar1 + 1, bar2);
		name = thisStr.substring(bar2 + 1, thisStr.length());
	}

	public boolean sameAs(VariantName other) {
		String olang = other.getLang();
		String oname = other.getName();
		
		boolean toRet = lang.equals(olang) && name.equalsIgnoreCase(oname);
		
		if (!toRet && !redFlag) {
			redFlag = !lang.equals(olang) && name.equalsIgnoreCase(oname);
		}
		
		return toRet;
	}

	public String getLang() {
		return lang;
	}
	
	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public boolean isFlagged() {
		return redFlag;
	}

}
