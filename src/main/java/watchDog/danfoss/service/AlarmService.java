
package watchDog.danfoss.service;

import java.util.List;

import watchDog.danfoss.model.Alarm;

/**
 * Description:
 * @author Matthew Xu
 * @date Apr 19, 2021
 */
public interface AlarmService extends BaseService {

	List<Alarm> getAlarmsFromXML(String ip);
	
	//List<Alarm> getAlarmsFromXML(String ip, )
}
