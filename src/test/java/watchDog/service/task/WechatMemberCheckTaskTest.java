
package watchDog.service.task;

import java.util.Timer;

import watchDog.thread.scheduletask.WechatMemberCheckTask;

/**
 * Description:
 * @author Matthew Xu
 * @date Jun 8, 2020
 */
public class WechatMemberCheckTaskTest {

	
	public static void main(String[] args) {
		Timer timer = new Timer();
		timer.schedule(WechatMemberCheckTask.INSTANCE, 0, 1000 * 60 *3);
		//timer.schedule(WechatMemberCheckTask.INSTANCE, 0);
	}

}
