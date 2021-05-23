package watchDog.danfoss.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import watchDog.danfoss.model.Alarm;
import watchDog.danfoss.model.Device;
import watchDog.danfoss.service.AlarmService;

public class AlarmServiceImpl implements AlarmService {

	// singleton
	private static AlarmServiceImpl INSTANCE;

	public static AlarmServiceImpl getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new AlarmServiceImpl();
		}
		return INSTANCE;
	}

	private AlarmServiceImpl(){
		}

	private static final Logger logger = Logger.getLogger(AlarmServiceImpl.class);

	@Override
	public List<Alarm> getAlarmsFromXML(String ip) {
		List<Device> devices = DEVICE_SERVICE.findAllByIp(ip);
		try {
			for (Device device : devices) {
				if(device.getHasActiveAlarms()){
					Document doc = getXMLResult(ip, XML_QUERY_SERVICE.getDeviceAlarmCMD(device.getNodeType(), device.getNode(), device.getModuleAddr(), device.getPoint()));
					if(doc != null)
						
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return null;
	}

}
