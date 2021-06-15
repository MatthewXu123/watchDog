
package watchDog.danfoss.service;

import java.util.List;

import org.junit.Test;

import watchDog.danfoss.model.Device;
import watchDog.danfoss.service.impl.DeviceServiceImpl;

/**
 * Description:
 * @author Matthew Xu
 * @date May 21, 2021
 */
public class DeviceServiceTest {
	
	private DeviceService deviceService = DeviceServiceImpl.getInstance();

	/**
	 * Test method for {@link watchDog.danfoss.service.DeviceService#getDevices(java.lang.String)}.
	 */
	@Test
	public void testGetDevices() {
		List<Device> devices = deviceService.getDevicesFromXML("47.99.193.207");
		for (Device device : devices) {
			System.out.println(device);
		}
	}

	/**
	 * Test method for {@link watchDog.danfoss.service.DeviceService#storeDevices(java.lang.String)}.
	 */
	@Test
	public void testStoreDevices() {
		deviceService.storeDevices("47.99.193.207");
	}
	
	@Test
	public void testFindAllByIp(){
		List<Device> devices = deviceService.findAllByIp("47.99.193.207");
		for (Device device : devices) {
			System.out.println(device);
		}
	}
	

}
