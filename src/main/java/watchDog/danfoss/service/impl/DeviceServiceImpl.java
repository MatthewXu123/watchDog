
package watchDog.danfoss.service.impl;

import java.io.IOException;

import watchDog.bean.Device;
import watchDog.danfoss.service.DeviceService;

/**
 * Description:
 * @author Matthew Xu
 * @date Apr 19, 2021
 */
public class DeviceServiceImpl implements DeviceService{
	// singleton
	private static DeviceServiceImpl INSTANCE;
	
	public static DeviceServiceImpl getInstance(){
		if (INSTANCE == null) {
			INSTANCE = new DeviceServiceImpl();
		}
		return INSTANCE;
	}
	
	private DeviceServiceImpl(){
	}
	
	// cmd
	private static final String CMD_QUERY_DEVICES = "<cmd action='read_devices' lang='c'/>";
	
	@Override
	public Device getDevices(String ip) {
		try {
			String result = sendQuery(ip, CMD_QUERY_DEVICES);
			System.out.println(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		DeviceService deviceService = DeviceServiceImpl.getInstance();
		deviceService.getDevices("47.99.193.207");
	}

}
