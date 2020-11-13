

package watchDog.thread.scheduletask;

import java.util.Date;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;

import watchDog.bean.Property;
import watchDog.listener.Dog;
import watchDog.service.FaxInfoService;
import watchDog.service.PropertyMgr;
import watchDog.util.DateTool;

import static watchDog.util.LogUtil.*;

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

	@Deprecated
	private Date getLastFaxQueryTime(){
		Date lastFaxQueryTime = new Date();
		Property property = propertyMgr.getProperty(PropertyMgr.LAST_FAX_QUERY_TIME);
		if(property == null || StringUtils.isBlank(property.getValue())){
			return DateTool.addMinutes(lastFaxQueryTime, -10);
		}else {
			return DateTool.parse(propertyMgr.getProperty(PropertyMgr.LAST_FAX_QUERY_TIME).getValue(),DateTool.DEFAULT_DATETIME_FORMAT);
		}
			
	}
}
