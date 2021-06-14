
package watchDog.danfoss.service;

import java.util.List;

import watchDog.danfoss.model.Supervisor;

/**
 * Description:
 * @author Matthew Xu
 * @date May 21, 2021
 */
public interface SupervisorService extends BaseService{

	boolean save(Supervisor supervisor);
	
	Supervisor findOneByIp(String ip);
	
	List<Supervisor> findAll();
	
	boolean updateOne(Supervisor supervisor);
}
