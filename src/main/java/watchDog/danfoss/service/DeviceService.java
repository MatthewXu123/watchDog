
package watchDog.danfoss.service;

import java.util.List;

import watchDog.danfoss.model.DanfossDevice;

/**
 * Description:
 * @author Matthew Xu
 * @date Apr 19, 2021
 */
public interface DeviceService extends BaseSevice {

	List<DanfossDevice> getDevices(String ip);
}
