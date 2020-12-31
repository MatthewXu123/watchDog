

package watchDog.thread.scheduletask;

import java.util.Date;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import watchDog.bean.Property;
import watchDog.service.MailService;
import watchDog.service.PropertyMgr;
import watchDog.service.SiteInfoService;
import watchDog.util.DateTool;

/**
 * Description:
 * @author Matthew Xu
 * @date May 29, 2020
 */
public class MailTask extends TimerTask implements BaseTask{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MailTask.class);
	
	public static final MailTask INSTANCE = new MailTask();
	
	private MailService mailService = MailService.INSTANCE;
	
	public static final long RUNNING_PERIOD = ONE_WEEK;
	
	private MailTask(){}
	
	@Override
	public void run() {
		try {
			
			BaseTask.getStartLog(LOGGER, this.getClass().getName());
			if(isNowQualifiedTime())
				mailService.sendServiceMails(SiteInfoService.getSitesOutOfService());
		} catch (Exception e) {
			LOGGER.error("",e);
		}finally {
			BaseTask.getEndLog(LOGGER, this.getClass().getName());
		}
	}
	
	private static boolean isNowQualifiedTime(){
		Date currentMailDate = new Date();
		Property property = propertyMgr.getProperty(PropertyMgr.LAST_MAIL_TIME);
		if(property != null && DateTool.diff(currentMailDate, DateTool.parse(property.getValue())) < RUNNING_PERIOD)
			return false;
		else {
			propertyMgr.update(PropertyMgr.LAST_MAIL_TIME, DateTool.format(currentMailDate));
			return true;
		}
	}

}
