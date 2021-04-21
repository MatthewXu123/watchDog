
package watchDog.danfoss.service.impl;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import watchDog.bean.Device;
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
	public Device getDevices(String ip) {
		try {
			Document doc = getXMLResult(ip, QUERY_CMD_SERVICE.getDevicesCMD());
			if(doc != null){
				Element rootElement = doc.getRootElement();
				String unitName = rootElement.elementText("unit_name");
				List<Element> deviceElements = rootElement.elements("device");
				for (Element deviceElement : deviceElements) {
					String nodetype = deviceElement.attributeValue("nodetype");
					String name = deviceElement.elementText("name");
				}
			}
		} catch (IOException | DocumentException e) {
			logger.error("", e);
		}
		return null;
	}
	
	public static void main(String[] args) {
		DeviceService deviceService = DeviceServiceImpl.getInstance();
		deviceService.getDevices("47.99.193.207");
	}

}
