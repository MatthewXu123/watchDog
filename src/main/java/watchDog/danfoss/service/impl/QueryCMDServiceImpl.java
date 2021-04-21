
package watchDog.danfoss.service.impl;

import watchDog.danfoss.service.QueryCMDService;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date Apr 20, 2021
 */
public class QueryCMDServiceImpl implements QueryCMDService {

	// singleton
	private static QueryCMDServiceImpl INSTANCE;

	public static QueryCMDServiceImpl getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new QueryCMDServiceImpl();
		}
		return INSTANCE;
	}

	// <cmd action='read_devices' lang='c'/>
	// <cmd action='read_val'><val nodetype='16' node='5' cid='0' vid='2553' /></cmd>
	private static final String CMD_QUERY_DEVICES = "<cmd action='read_devices' lang='c'/>";

	@Override
	public String getDevicesCMD() {
		return CMD_QUERY_DEVICES;
	}

}
