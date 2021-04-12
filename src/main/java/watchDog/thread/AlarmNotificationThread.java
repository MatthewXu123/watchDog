package watchDog.thread;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import watchDog.listener.Dog;
import static watchDog.util.LogUtil.*;
import watchDog.util.MyThread;

public class AlarmNotificationThread extends MyThread {
	
	
	private static final Logger logger = Logger.getLogger(AlarmNotificationThread.class);
	AlarmNotificationMain logicMain = new AlarmNotificationMain();
	
	public AlarmNotificationThread()
	{
		super("AlarmNotificationThread");
	}
	public void run()
	{
		//sleep 5 minutes first
		//Dog.sleep(AlarmNotificationMain.SLEEP_MINUTES*60*1000);
		while(!forceDead)
		{
			logger.info("AlarmNotificationThread start...");
			logicMain.checkAlarms();
			this.lastRunningTime = new Date();
		}
		logger.info("AlarmNotificationThread finished...");
	}
	public List<String> alarmOfSite(int idsite) throws Exception
	{
		return logicMain.alarmOfSite(idsite);
	}
	
}
	//important varaibles:
	//mpxpro
	//高温报警: s_HI
	//低过热度: s_LSH
	//低温报警 : s_LO
	//S1探头故障: s_pre1,s_pre2,s_pre3,s_pre4,s_pre6m,
	
	//机组
	//"pRackCNL1"
	//中高级报警
