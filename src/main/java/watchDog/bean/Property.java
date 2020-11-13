package watchDog.bean;

import java.util.Date;

public class Property {
	String key = null;
	String value = null;
	Date time = new Date();
	
	public Property(String key,String value)
	{
		this.key = key;
		this.value = value;
	}
	public Property(String key,String value,Date time)
	{
		this.key = key;
		this.value = value;
		this.time = time;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
	

}
