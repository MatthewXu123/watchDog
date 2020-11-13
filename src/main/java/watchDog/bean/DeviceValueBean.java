package watchDog.bean;

import java.util.Date;
import java.util.Map;

public class DeviceValueBean {
	Date date = null;
	Map<String,String> values = null;
	public DeviceValueBean(Date date,Map<String,String> values)
	{
		this.date = date;
		this.values = values;
	}
	public Date getDate() {
		return date;
	}
	public Map<String, String> getValues() {
		return values;
	}
	
}
