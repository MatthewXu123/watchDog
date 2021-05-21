
package watchDog.danfoss.service;

import java.util.List;

import watchDog.danfoss.model.Device;

/**
 * Description:
 * @author Matthew Xu
 * @date Apr 19, 2021
 */
public interface DeviceService extends BaseService {

	List<Device> getDevices(String ip);
	
	boolean storeDevices(String ip);
}
