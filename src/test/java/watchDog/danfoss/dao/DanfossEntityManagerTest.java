
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
		danfossDevice1.setName("test3");
		DanfossDevice danfossDevice2 = new DanfossDevice();
		danfossDevice2.setName("test4");
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
		danfossDevice.setDeviceId("deviceId");
		em.update(danfossDevice);
	}
	
	@Test
	public void testBatchUpdate() {
		DanfossDevice danfossDevice = new DanfossDevice();
		danfossDevice.setId(1);
		danfossDevice.setName("test3333");
		
		DanfossDevice danfossDevice2 = new DanfossDevice();
		danfossDevice2.setId(2);
		danfossDevice2.setName("test34444");
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
		DanfossDevice danfossDevice = em.find(DanfossDevice.class, 1);
		System.out.println(danfossDevice);
	}

}
