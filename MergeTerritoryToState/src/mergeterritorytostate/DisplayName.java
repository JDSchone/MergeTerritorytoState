package mergeterritorytostate;


//This class contains the basic information on any given place's Display Name(s)
public class DisplayName {

	String lang = "";
	String name = "";
	boolean redFlag = false;
	
	public DisplayName(String thisStr) {
		int bar = thisStr.indexOf("|");
		lang = thisStr.substring(0, bar);
		name = thisStr.substring(bar + 1, thisStr.length());
	}

	public boolean sameAs(DisplayName other) {
		String olang = other.getLang();
		String oname = other.getName();
		boolean toRet = lang.equals(olang);
		
		if (!toRet && !redFlag) {
			redFlag = name.equalsIgnoreCase(oname);
		}
		
		return toRet;
	}

	public String getLang() {
		return lang;
	}
	
	public String getName() {
		return name;
	}

	public boolean isFlagged() {
		return redFlag;
	}

}
