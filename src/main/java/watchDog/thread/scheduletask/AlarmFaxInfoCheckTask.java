

package watchDog.thread.scheduletask;

import static watchDog.util.LogUtil.faxInfoLogger;

import java.util.TimerTask;

import watchDog.service.FaxInfoService;

/**
 * Description:
 * @author Matthew Xu
 * @date May 29, 2020
 */
public class AlarmFaxInfoCheckTask extends TimerTask implements BaseTask{
	
	public static final AlarmFaxInfoCheckTask INSTANCE = new AlarmFaxInfoCheckTask();
	
	private AlarmFaxInfoCheckTask(){}
	
	private FaxInfoService faxInfoService = FaxInfoService.INSTANCE;
	
	@Override
	public void run() {
		try {
			faxInfoLogger.info("AlarmFaxInfoCheckTask start...");
			faxInfoService.sendFaxInfo();
		} catch (Exception e) {
			faxInfoLogger.error("",e);
		}finally {
			faxInfoLogger.info("AlarmFaxInfoCheckTask finished...");
		}
	}

}
