package watchDog.danfoss.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import watchDog.danfoss.enums.AlarmType;
import watchDog.danfoss.model.Alarm;
import watchDog.danfoss.model.Device;
import watchDog.danfoss.model.Supervisor;
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

	private AlarmServiceImpl() {
	}

	private static final Logger logger = Logger.getLogger(AlarmServiceImpl.class);

	@Override
	public Map<String, List<Alarm>> getAlarmsFromXML(String ip) {
		List<Device> devices = DEVICE_SERVICE.findAllByIp(ip);
		Supervisor supervisor = SUPERVISOR_SERVICE.findOneByIp(ip);
		Map<String, List<Alarm>> alarmMap = new HashMap<>();

		try {
			for (Device device : devices) {
				Document doc = getXMLResult(ip, XML_QUERY_SERVICE.getDeviceAlarmCMD(device.getNodeType(), device.getNode(), device.getModuleAddr(), device.getPoint()));
				if (doc != null) {
					Element rootElement = doc.getRootElement();
					// active,acked,cleared
					for (AlarmType _alarmType : AlarmType.values()) {
						String alarmType = _alarmType.toString();
						List<Alarm> currentAlarms = alarmMap.get(alarmType);
						if (currentAlarms == null)
							currentAlarms = new ArrayList<>();
						currentAlarms.addAll(getAlarmsByAlarmType(rootElement, alarmType, device, supervisor));
						alarmMap.put(alarmType, currentAlarms);
					}
				}

			}
		} catch (Exception e) {
			logger.error("", e);
		}

		return alarmMap;
	}

	/**
	 * 
	 * Description:
	 * @param rootElement
	 * @param alarmType
	 * @param device
	 * @param supervisor
	 * @return
	 * @author Matthew Xu
	 * @date May 24, 2021
	 */
	public List<Alarm> getAlarmsByAlarmType(Element rootElement, String alarmType, Device device,
			Supervisor supervisor) {
		Element activeEle = rootElement.element(alarmType);
		List<Alarm> alarms = new ArrayList<>();
		// e.g.
		// <active>
		//		<ref name = "--- S5 Error">42927</ref>
		//		<total_active>1</total_active>
		// </active>
		int total = str2Integer(activeEle.element("total_" + alarmType).getText());
		if (total > 0) {
			List<Element> elements = activeEle.elements("ref");
			for (Element element : elements) {
				Alarm alarm = new Alarm();
				alarm.setId(str2Integer(element.getText()));
				alarm.setName(element.attributeValue("name"));
				alarm.setDevice(device);
				alarm.setSupervisor(supervisor);

				alarms.add(alarm);
			}
		}

		return alarms;
	}

	public static void main(String[] args) {
		AlarmServiceImpl alarmService = new AlarmServiceImpl();
		alarmService.getAlarmsFromXML("47.99.193.207");
	}

}
