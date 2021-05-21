
package watchDog.danfoss.service.impl;

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
		return CUSTOMIZED_ENTITY_MANAGER.save(supervisor);
	}

}
