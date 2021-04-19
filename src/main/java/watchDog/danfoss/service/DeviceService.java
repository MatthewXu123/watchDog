
package watchDog.danfoss.service;

import watchDog.bean.Device;

/**
 * Description:
 * @author Matthew Xu
 * @date Apr 19, 2021
 */
public interface DeviceService extends BaseSevice {

	Device getDevices(String ip);
}
