
package watchDog.service;

import java.util.List;

import org.apache.log4j.Logger;

import watchDog.bean.SiteInfo;
import watchDog.dao.SiteInfoDAO;

/**
 * Description:
 * @author Matthew Xu
 * @date Feb 20, 2021
 */
public class DailyAlarmTestService {

	private static final Logger LOGGER = Logger.getLogger(DailyAlarmTestService.class);

	public static final DailyAlarmTestService INSTANCE = new DailyAlarmTestService();

	private DailyAlarmTestService(){
		
	}
	
	public List<SiteInfo> getDailyAlarmConfiguredSites(){
		return SiteInfoDAO.INSTANCE.getDailyAlarmConfiguredSites();
	}
}
