
package watchDog.danfoss.service;

import java.util.List;

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

	@Test
	public void testFindOneByIp(){
		Supervisor supervisor = supervisorService.findOneByIp("47.99.193.207");
		System.out.println(supervisor.getId());
		System.out.println(supervisor.getName());
		System.out.println(supervisor);
	}
	
	@Test
	public void testFindAll(){
		List<Supervisor> list = supervisorService.findAll();
		for (Supervisor supervisor : list) {
			System.out.println(supervisor);
		}
	}
}
