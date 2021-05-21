
package watchDog.danfoss.service;

import org.junit.Test;

import watchDog.danfoss.model.Supervisor;
import watchDog.danfoss.service.impl.SupervisorServiceImpl;

/**
 * Description:
 * @author Matthew Xu
 * @date May 21, 2021
 */
public class SupervisorServiceTest {

	private SupervisorService supervisorService = SupervisorServiceImpl.getInstance();
	
	/**
	 * Test method for {@link watchDog.danfoss.service.SupervisorService#save(watchDog.danfoss.model.Supervisor)}.
	 */
	@Test
	public void testSave() {
		Supervisor supervisor = new Supervisor();
		supervisor.setIp("47.99.193.207");
		supervisor.setName("store1");
		supervisorService.save(supervisor);
	}

}
