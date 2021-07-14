

package watchDog.thread.scheduletask;

import java.util.TimerTask;

import org.apache.log4j.Logger;

import watchDog.bean.constant.CommonConstants;
import watchDog.service.SiteService;

/**
 * Description:
 * @author Matthew Xu
 * @date May 29, 2020
 */
public class MailTask extends TimerTask implements BaseTask{
	
	private static final Logger LOGGER = Logger.getLogger(MailTask.class);
	
	public static final MailTask INSTANCE = new MailTask();
	
	private SiteService mailService = SiteService.INSTANCE;
	
	public static final long RUNNING_PERIOD = CommonConstants.ONE_WEEK;
	
	private MailTask(){}
	
	@Override
	public void run() {
		try {
			BaseTask.getStartLog(LOGGER, this.getClass().getName());

			if(propertyMgr.checkQueryFrequency(propertyMgr.LAST_MAIL_QUERY_TIME, RUNNING_PERIOD))
				mailService.sendServiceMails();
		} catch (Exception e) {
			LOGGER.error("",e);
		}finally {
			BaseTask.getEndLog(LOGGER, this.getClass().getName());
		}
	}
	
}
