
package watchDog.danfoss.dao;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import watchDog.danfoss.model.DanfossDevice;

/**
 * Description:
 * @author Matthew Xu
 * @date May 18, 2021
 */
public class DanfossEntityManagerTest {


	private DanfossEntityManager em = DanfossEntityManager.getInstance();
	
	@Test
	public void testSave() {
		DanfossDevice danfossDevice = new DanfossDevice();
		danfossDevice.setName("test");
		em.save(danfossDevice);
	}
	
	@Test
	public void testBatchSave() {
		DanfossDevice danfossDevice1 = new DanfossDevice();
		danfossDevice1.setName("test5");
		DanfossDevice danfossDevice2 = new DanfossDevice();
		danfossDevice2.setName("test666");
		List<DanfossDevice> list = new ArrayList<>();
		list.add(danfossDevice1);
		list.add(danfossDevice2);
		em.batchSave(list);
	}
	
	@Test
	public void testUpdate() {
		DanfossDevice danfossDevice = new DanfossDevice();
		danfossDevice.setId(1);
		danfossDevice.setName("test3");
		danfossDevice.setDeviceId("deviceId333");
		em.update(danfossDevice);
	}
	
	@Test
	public void testBatchUpdate() {
		DanfossDevice danfossDevice = new DanfossDevice();
		danfossDevice.setName("test3333444");
		
		DanfossDevice danfossDevice2 = new DanfossDevice();
		danfossDevice2.setName("test34444555");
		danfossDevice2.setDeviceId("deviceId");
		
		List<Object> list = new ArrayList<>();
		list.add(danfossDevice);
		list.add(danfossDevice2);
		em.batchUpdate(list);
	}
	
	@Test
	public void testDelete() {
		DanfossDevice danfossDevice = em.find(DanfossDevice.class, 1);
		em.delete(danfossDevice);
	}
	
	@Test
	public void testFind() {
		DanfossDevice danfossDevice = em.find(DanfossDevice.class, 2);
		System.out.println(danfossDevice);
	}
	
	@Test
	public void testGetQueryList() {
		List<DanfossDevice> queryList = em.getQueryList("SELECT d FROM DanfossDevice d ", DanfossDevice.class);
		for (DanfossDevice danfossDevice : queryList) {
			System.out.println(danfossDevice);
		}
	}

}