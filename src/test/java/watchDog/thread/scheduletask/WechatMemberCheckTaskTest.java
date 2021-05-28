
package watchDog.thread.scheduletask;

import java.util.Timer;

/**
 * Description:
 * @author Matthew Xu
 * @date May 28, 2021
 */
public class WechatMemberCheckTaskTest {

	public static void main(String[] args) {
		Timer wechatMemberCheckTaskTimer = new Timer("WechatMemberCheckTaskTimer");
	    wechatMemberCheckTaskTimer.scheduleAtFixedRate(WechatMemberCheckTask.INSTANCE, 0, WechatMemberCheckTask.RUNNING_PERIOD);
	}

}
