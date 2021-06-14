
package watchDog.danfoss.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import watchDog.danfoss.model.Supervisor;
import watchDog.danfoss.service.SupervisorService;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date May 21, 2021
 */
public class SupervisorServiceImpl implements SupervisorService {

	// singleton
	private static SupervisorServiceImpl INSTANCE;

	public static SupervisorServiceImpl getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SupervisorServiceImpl();
		}
		return INSTANCE;
	}

	private SupervisorServiceImpl(){
		
	}

	private static final Logger logger = Logger.getLogger(SupervisorServiceImpl.class);

	@Override
	public boolean save(Supervisor supervisor) {
		boolean isSaveOk = false;
		try {
			isSaveOk = CUSTOMIZED_ENTITY_MANAGER.save(supervisor);
		} catch (Exception e) {
			logger.error("", e);
		}
		return isSaveOk;
	}

	@Override
	public Supervisor findOneByIp(String ip) {
		Supervisor supervisor = new Supervisor();
		try {
			supervisor = CUSTOMIZED_ENTITY_MANAGER.getQuerySingle(PROPERTY_CONFIG.getValue(getQueryPropertiesKey()
					, new Object[]{"'" + ip + "'"})
					, Supervisor.class);
		} catch (Exception e) {
			logger.error("", e);
		}
		return supervisor;
	}

	@Override
	public List<Supervisor> findAll() {
		List<Supervisor> supervisors = new ArrayList<>();
		try {
			supervisors = CUSTOMIZED_ENTITY_MANAGER.getQueryList(PROPERTY_CONFIG.getValue(getQueryPropertiesKey())
					, Supervisor.class);
		} catch (Exception e) {
			logger.error("", e);
		}
		return supervisors;
	}

	@Override
	public boolean updateOne(Supervisor supervisor) {
		try {
			return CUSTOMIZED_ENTITY_MANAGER.update(supervisor);
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
	}
	
}
