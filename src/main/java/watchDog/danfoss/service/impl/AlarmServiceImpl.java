package watchDog.danfoss.service.impl;

import java.util.ArrayList;
import java.util.List;

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
	public List<Alarm> getActiveAlarmsFromXMLAndStoreAlarms(String ip) {
		List<Device> devices = DEVICE_SERVICE.findAllByIp(ip);
		Supervisor supervisor = SUPERVISOR_SERVICE.findOneByIp(ip);
		List<Alarm> activeAlarms = new ArrayList<>();
		try {
			for (Device device : devices) {
				// If the device doesn't have the attribute 'alarm'
				if (device.getHasActiveAlarms() != null) {
					// nodetype,node,mod,point
					Document doc = getXMLResult(ip, XML_QUERY_SERVICE.getDeviceAlarmCMD(device.getNodeType(),
							device.getNode(), device.getModuleAddr(), device.getPoint()));
					List<Alarm> xmlAlarms = new ArrayList<>();
					List<Alarm> dbAlarms = ALARM_SERVICE.findAlarmsByDeviceId(device.getId());
					if (doc != null) {
						Element rootElement = doc.getRootElement();
						// active,acked,cleared
						for (AlarmType alarmType : AlarmType.values()) {
							List<Alarm> alarms = getAlarmsByAlarmType(rootElement, alarmType.toString(), device, supervisor);
							xmlAlarms.addAll(alarms);
							if(alarmType.equals(AlarmType.active))
								activeAlarms.addAll(alarms);
						}
					}
					List<Alarm> batchSaveAlarms = new ArrayList<>();
					List<Alarm> batchUpdateAlarms = new ArrayList<>();

					for (Alarm alarm : xmlAlarms) {
						if (!dbAlarms.contains(alarm))
							batchSaveAlarms.add(alarm);
						else
							batchUpdateAlarms.add(alarm);
					}
					CUSTOMIZED_ENTITY_MANAGER.batchSave(batchSaveAlarms);
					CUSTOMIZED_ENTITY_MANAGER.batchUpdate(batchUpdateAlarms);
				}

			}
		} catch (Exception e) {
			logger.error("", e);
		}

		return activeAlarms;
	}

	/**
	 * 
	 * Description:
	 * 
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
		// <ref name = "--- S5 Error">42927</ref>
		// <total_active>1</total_active>
		// </active>
		int total = str2Integer(activeEle.element("total_" + alarmType).getText());
		if (total > 0) {
			List<Element> elements = activeEle.elements("ref");
			for (Element element : elements) {
				Alarm alarm = new Alarm();
				alarm.setId(str2Integer(element.getText()));
				alarm.setName(element.attributeValue("name"));
				alarm.setAlarmType(AlarmType.valueOf(alarmType));
				alarm.setDevice(device);
				alarm.setSupervisor(supervisor);

				alarms.add(alarm);
			}
		}

		return alarms;
	}

	public static void main(String[] args) {
		AlarmServiceImpl alarmService = new AlarmServiceImpl();
		alarmService.getActiveAlarmsFromXMLAndStoreAlarms("47.99.193.207");
	}

	@Override
	public List<Alarm> findAlarmsByDeviceId(String deviceId) {
		List<Alarm> alarms = new ArrayList<>();
		try {
			alarms = CUSTOMIZED_ENTITY_MANAGER.getQueryList(
					PROPERTY_CONFIG.getValue(getQueryPropertiesKey(), new Object[] { "'" + deviceId + "'" }), Alarm.class);
		} catch (Exception e) {
			logger.error("", e);
		}
		return alarms;
	}

}
