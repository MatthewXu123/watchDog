
package watchDog.danfoss.service.impl;

import watchDog.danfoss.service.XMLQueryService;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date Apr 20, 2021
 */
public class XMLQueryServiceImpl implements XMLQueryService {

	// singleton
	private static XMLQueryServiceImpl INSTANCE;

	public static XMLQueryServiceImpl getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new XMLQueryServiceImpl();
		}
		return INSTANCE;
	}

	private XMLQueryServiceImpl() {
		
	}
	
	@Override
	public String getDevicesCMD() {
		return PROPERTY_CONFIG.getValue(getQueryPropertiesKeyOnlyMethod());
	}


	@Override
	public String getDeviceAlarmCMD(int nodeType, int node, int mod, int point) {
		return PROPERTY_CONFIG.getValue(getQueryPropertiesKeyOnlyMethod(), new Object[]{nodeType, node, mod, point});
	}

}
