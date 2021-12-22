package mergeterritorytostate;

//This class contains the basic information on any given place's Attribute(s)
public class Attr {

	String id = "";
	String type = "";
	String stYear = "";
	String text = "";
	String lang = "";
	String enYear = "";
	String copyright = "";
	String cUrl = "";
	String url = "";
	String urlTitle = "";
	String title = "";
	boolean redFlag = false;
	
	public Attr(String thisStr) {
		int bar0 = thisStr.indexOf("|", 0);
		int bar1 = thisStr.indexOf("|", bar0 + 1);
		int bar2 = thisStr.indexOf("|", bar1 + 1);
		int bar3 = thisStr.indexOf("|", bar2 + 1);
		int bar4 = thisStr.indexOf("|", bar3 + 1);
		int bar5 = thisStr.indexOf("|", bar4 + 1);
		int bar6 = thisStr.indexOf("|", bar5 + 1);
		int bar7 = thisStr.indexOf("|", bar6 + 1);
		int bar8 = thisStr.indexOf("|", bar7 + 1);
		int bar9 = thisStr.indexOf("|", bar8 + 1);
		
		id = thisStr.substring(0, bar0);
		type = thisStr.substring(bar0 + 1, bar1);
		stYear = thisStr.substring(bar1 + 1, bar2);
		text = thisStr.substring(bar2 + 1, bar3);		
		
		if (text.equals("\\\"\\\"")) {
			text = "";
		}
		
		lang = thisStr.substring(bar3 + 1, bar4);
		enYear = thisStr.substring(bar4 + 1, bar5);
		copyright = thisStr.substring(bar5 + 1, bar6);
		cUrl = thisStr.substring(bar6 + 1, bar7);
		url = thisStr.substring(bar7 + 1, bar8);
		urlTitle = thisStr.substring(bar8 + 1, bar9);
		title = thisStr.substring(bar9 + 1, thisStr.length());
	}

	public boolean sameAs(Attr other) {
		String otext = other.getText();
		String ocopy = other.getCopyright();
		String ocurl = other.getCUrl();
		String ourl = other.getUrl();
		String ourlt = other.getUrlTitle();
		String otitle = other.getTitle();
		
		//This looks like nonsense, but basically it means that if the text and url are the same
		//AND either the title is the same or the copyright information is the same, then we will
		//consider the attributes the same. If one of these types of data is the same but both
		//are not, we will allow it but Red Flag it.
		boolean toRet = text.equalsIgnoreCase(otext) && title.equalsIgnoreCase(otitle) && url.equalsIgnoreCase(ourl) && urlTitle.equalsIgnoreCase(ourlt) &&
		cUrl.equalsIgnoreCase(ocurl) && copyright.equalsIgnoreCase(ocopy);
		
		if (!toRet && !redFlag) {
			redFlag = text.equalsIgnoreCase(otext) || title.equalsIgnoreCase(otitle) || url.equalsIgnoreCase(ourl) || urlTitle.equalsIgnoreCase(ourlt) ||
					cUrl.equalsIgnoreCase(ocurl) || copyright.equalsIgnoreCase(ocopy);
		}
		
		return toRet;
	}

	public String getTitle() {
		return title;
	}

	public String getUrlTitle() {
		return urlTitle;
	}

	public String getUrl() {
		return url;
	}

	public String getCUrl() {
		return cUrl;
	}

	public String getCopyright() {
		return copyright;
	}

	public String getEnYear() {
		return enYear;
	}

	public String getLang() {
		return lang;
	}

	public String getText() {
		return text;
	}

	public String getStYear() {
		return stYear;
	}

	public String getType() {
		return type;
	}

	public boolean isFlagged() {
		return redFlag;
	}

}
