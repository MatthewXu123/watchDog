
package watchDog.thread.scheduletask;

import static org.junit.Assert.*;

import java.util.Timer;

import org.junit.Test;

/**
 * Description:
 * @author Matthew Xu
 * @date Jun 18, 2020
 */
public class AlarmFaxInfoCheckTaskTest {

	public static void main(String[] args) {
		Timer alarmFaxInfoCheckTaskTimer = new Timer("AlarmFaxInfoCheckTaskTimer");
	    alarmFaxInfoCheckTaskTimer.schedule(AlarmFaxInfoCheckTask.INSTANCE, 0, 1000 * 60 *2);
	}
	
}
