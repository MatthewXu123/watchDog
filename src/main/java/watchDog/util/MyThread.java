package watchDog.util;

import java.util.Calendar;
import java.util.Date;

import watchDog.listener.Dog;
import watchDog.property.template.PropertyConfig;

public class MyThread extends Thread {
	
	protected Dog dog = Dog.getInstance();
	
	protected PropertyConfig propertyConfig = PropertyConfig.INSTANCE;
	
	public MyThread(String str)
	{
		super(str);
	}
	protected Date lastRunningTime = new Date();
	protected boolean forceDead = false;
	public boolean isDead(int maxDeadMinutes)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(lastRunningTime);
		c.add(Calendar.MINUTE, maxDeadMinutes);
		Date t = c.getTime();
		if(t.before(new Date()))
			return true;
		else
			return false;
	}
	public void forceDead()
	{
		this.forceDead = true;
	}
}
