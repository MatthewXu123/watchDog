
package watchDog.danfoss.service;

/**
 * Description:
 * @author Matthew Xu
 * @date Apr 20, 2021
 */
public interface XMLQueryService extends BaseService {

	String getDevicesCMD();
	
	String getDeviceAlarmCMD(int nodeType, int node, int mod, int point);
}
