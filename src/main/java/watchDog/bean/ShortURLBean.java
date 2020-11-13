package watchDog.bean;

import watchDog.database.Record;

public class ShortURLBean {
	String longURL = null;
	String url = null;
	public ShortURLBean(Record r)
	{
		longURL = (String)r.get("long_url");
		url = (String)r.get("url");
	}
	public String getLongURL() {
		return longURL;
	}
	public void setLongURL(String longURL) {
		this.longURL = longURL;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
