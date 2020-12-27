

package watchDog.thread.scheduletask;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import watchDog.service.MailService;
import watchDog.service.SiteInfoService;

/**
 * Description:
 * @author Matthew Xu
 * @date May 29, 2020
 */
public class MailTask extends TimerTask implements BaseTask{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MailTask.class);
	
	public static final MailTask INSTANCE = new MailTask();
	
	private MailTask(){}
	
	private MailService mailService = MailService.INSTANCE;
	
	@Override
	public void run() {
		try {
			BaseTask.getStartLog(LOGGER, this.getClass().getName());
			mailService.sendServiceMails(SiteInfoService.getSitesOutOfService());
		} catch (Exception e) {
			LOGGER.error("",e);
		}finally {
			BaseTask.getEndLog(LOGGER, this.getClass().getName());
		}
	}

}
