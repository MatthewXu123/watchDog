
package watchDog.danfoss.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import watchDog.danfoss.enums.DeviceType;
import watchDog.danfoss.model.Device;
import watchDog.danfoss.model.Supervisor;
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
	
	private static final Logger logger = Logger.getLogger(DeviceServiceImpl.class);
	
	@Override
	public List<Device> getDevicesFromXML(String ip) {
		List<Device> deviceList = new ArrayList<>();
		Supervisor supervisor = SUPERVISOR_SERVICE.findOneByIp(ip);
		try {
			Document doc = getXMLResult(ip, XML_QUERY_SERVICE.getDevicesCMD());
			if(doc != null){
				Element rootElement = doc.getRootElement();
				String unitName = rootElement.elementText("unit_name");
				String software = rootElement.elementText("software");
				List<Element> deviceElements = rootElement.elements("device");
				for (Element deviceElement : deviceElements) {
					Device device = new Device();
					device.setSupervisor(supervisor);
					device.setType(DeviceType.valueOf(deviceElement.elementText("type")));
					device.setName(deviceElement.elementText("name"));
					device.setRackId(str2Integer(deviceElement.attributeValue("rack_id")));
					if(device.getType().equals(DeviceType.RACK_ONLY)){
						device.setSuctionNum(str2Integer(deviceElement.elementText("num_suction")));
						continue;
					}
					device.setDeviceId(deviceElement.elementText("device_id"));
					device.setAddr(deviceElement.attributeValue("addr"));
					device.setHasActiveAlarms(str2Boolean(deviceElement.attributeValue("alarm")));
					device.setIsCondWithRack(str2Boolean(deviceElement.attributeValue("condenser")));
					device.setCtrlVal(deviceElement.attributeValue("ctrl_val"));
					device.setIsDefrosting(str2Boolean(deviceElement.attributeValue("defrost")));
					device.setModuleAddr(str2Integer(deviceElement.attributeValue("mod")));
					device.setModelName(deviceElement.attributeValue("modelname"));
					device.setMulticaseName(deviceElement.attributeValue("multicasename"));
					device.setNode(str2Integer(deviceElement.attributeValue("node")));
					device.setNodeType(str2Integer(deviceElement.attributeValue("nodetype")));
					device.setIsOnline(str2Boolean(deviceElement.attributeValue("online")));
					device.setPoint(str2Integer(deviceElement.attributeValue("point")));
					device.setIsRunOrDefrost(str2Boolean(deviceElement.attributeValue("state")));
					device.setStatus(deviceElement.attributeValue("status"));
					device.setSuctionId(str2Integer(deviceElement.attributeValue("suction_id")));
					device.setValue(deviceElement.attributeValue("value"));
					// Set the id
					device.setId(getId(supervisor.getId(), device));
					deviceList.add(device);
				}
			}
		} catch (IOException | DocumentException e) {
			logger.error("", e);
		}
		return deviceList;
	}
	
	public static void main(String[] args) {
		DeviceService deviceService = DeviceServiceImpl.getInstance();
		deviceService.storeDevices("47.99.193.207");
	}

	@Override
	public boolean storeDevices(String ip) {
		List<Device> xmlDevices = this.getDevicesFromXML(ip);
		List<Device> dbDevices = DEVICE_SERVICE.findAllByIp(ip);
		List<Device> batchSaveDevices = new ArrayList<>();
		List<Device> batchUpdateDevices = new ArrayList<>();
		
		for (Device device : xmlDevices) {
			if(!dbDevices.contains(device))
				batchSaveDevices.add(device);
			else
				batchUpdateDevices.add(device);
		}
		return CUSTOMIZED_ENTITY_MANAGER.batchSave(batchSaveDevices) & CUSTOMIZED_ENTITY_MANAGER.batchUpdate(batchUpdateDevices);
	}
	
	@Override
	public List<Device> findAllByIp(String ip) {
		List<Device> devices = new ArrayList<>();
		try {
			devices = CUSTOMIZED_ENTITY_MANAGER.getQueryList(PROPERTY_CONFIG.getValue(getQueryPropertiesKey(), new Object[]{ "'" + ip + "'"}), Device.class);
		} catch (Exception e) {
			logger.error("",e);
		}
		return devices;
	}

	@Override
	public String getId(int supervisorId, Device device) {
		return supervisorId + "_" + device.getNodeType() + "_" + device.getNode() + "_" + device.getModuleAddr() + "_" + device.getPoint();
	}

}
