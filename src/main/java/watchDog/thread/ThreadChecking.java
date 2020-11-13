package watchDog.thread;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import watchDog.listener.Dog;
import watchDog.util.DateTool;

public class ThreadChecking extends Thread {
	private static final Logger logger = Logger.getLogger(ThreadChecking.class);
	Dog dog = Dog.getInstance();
	int lastHour = -1;
	String name = null;
	private static Integer FREQUENCY = 10;
	private static Date lastRunTime = null;

	public ThreadChecking(int i) {
		super("ThreadChecking" + i);
		name = "ThreadChecking" + i;
	}

	public void run() {
		while (true) {
			Dog.sleep(FREQUENCY * 60 * 1000);
			int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
			if (lastHour == -1 || (hour % 3 == 0 && lastHour != hour))
				logger.info(name + " running");
			lastHour = hour;
			synchronized (FREQUENCY) {
				if (lastRunTime != null && DateTool.diff(new Date(), lastRunTime) <= (FREQUENCY - 1) * 60 * 1000)
					continue;
				try {
					if (dog.getAlarmThread().isDead(12 * AlarmNotificationMain.SLEEP_MINUTES)) {
						logger.error("AlarmThread is dead...");
						dog.renewAlarmThread();
					}
					if (dog.getConnectionThread().isDead(4 * ConnectionThread.SLEEP_MINUTES)) {
						logger.error("ConnectionThread is dead...");
						dog.renewConnectionThread();
					}
					if (dog.getWechatApplicationThread().isDead(4 * WechatApplicationThread.SLEEP_MINUTES)) {
						logger.error("WechatApplicationThread is dead...");
						dog.renewWechatAppWlicationThread();
					}
				} catch (Exception ex) {
					logger.error("", ex);
				} finally {
					lastRunTime = new Date();
				}
			}
		}
	}
}
