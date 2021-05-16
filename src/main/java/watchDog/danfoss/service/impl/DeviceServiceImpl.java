
package watchDog.danfoss.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.impl.xb.xsdschema.DerivationControl;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import watchDog.danfoss.enums.DeviceType;
import watchDog.danfoss.model.DanfossDevice;
import watchDog.danfoss.service.DeviceService;
import watchDog.service.EnergyService;

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
	public List<DanfossDevice> getDevices(String ip) {
		List<DanfossDevice> deviceList = new ArrayList<>();
		try {
			Document doc = getXMLResult(ip, QUERY_CMD_SERVICE.getDevicesCMD());
			if(doc != null){
				Element rootElement = doc.getRootElement();
				String unitName = rootElement.elementText("unit_name");
				String software = rootElement.elementText("software");
				List<Element> deviceElements = rootElement.elements("device");
				for (Element deviceElement : deviceElements) {
					DanfossDevice device = new DanfossDevice();
					device.setType(DeviceType.valueOf(deviceElement.elementText("type")));
					device.setName(deviceElement.elementText("name"));
					device.setRackId(str2Integer(deviceElement.attributeValue("rack_id")));
					if(device.getType().equals(DeviceType.RACK_ONLY)){
						device.setSuctionNum(str2Integer(deviceElement.elementText("num_suction")));
						continue;
					}
					device.setId(deviceElement.elementText("device_id"));
					device.setAddr(deviceElement.attributeValue("addr"));
					device.setHasActiveAlarms(str2Boolean(deviceElement.attributeValue("alarm")));
					device.setIsCondenserWithRack(str2Boolean(deviceElement.attributeValue("condenser")));
					device.setCtrlVal(deviceElement.attributeValue("ctrl_val"));
					device.setIsDefrosting(str2Boolean(deviceElement.attributeValue("defrost")));
					device.setModuleAddr(str2Integer(deviceElement.attributeValue("mod")));
					device.setModelName(deviceElement.attributeValue("modelname"));
					device.setMulticaseName(deviceElement.attributeValue("multicasename"));
					device.setNode(str2Integer(deviceElement.attributeValue("node")));
					device.setNodeType(str2Integer(deviceElement.attributeValue("nodetype")));
					device.setIsOnline(str2Boolean(deviceElement.attributeValue("online")));
					device.setPoint(str2Integer(deviceElement.attributeValue("point")));
					device.setIsRunningOrDefrosting(str2Boolean(deviceElement.attributeValue("state")));
					device.setStatus(deviceElement.attributeValue("status"));
					device.setSuctionId(str2Integer(deviceElement.attributeValue("suction_id")));
					device.setValue(deviceElement.attributeValue("value"));
					
				}
			}
		} catch (IOException | DocumentException e) {
			logger.error("", e);
		}
		return null;
	}
	
	private Integer str2Integer(String str){
		Integer result = null;
		try {
			result = StringUtils.isBlank(str) ? null : Integer.valueOf(str);
		} catch (Exception e) {
			return result;
		}
		return result;
	}
	
	private Boolean str2Boolean(String str){
		Boolean result = null;
		try {
			result = StringUtils.isBlank(str) ? null : (str.equals("1") ? true : false);
		} catch (Exception e) {
			return result;
		}
		return result;
	}
	
	
	public static void main(String[] args) {
		DeviceService deviceService = DeviceServiceImpl.getInstance();
		deviceService.getDevices("47.99.193.207");
	}

}