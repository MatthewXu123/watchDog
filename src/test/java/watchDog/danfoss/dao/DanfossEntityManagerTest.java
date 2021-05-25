
package watchDog.danfoss.dao;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import watchDog.danfoss.model.Device;

/**
 * Description:
 * @author Matthew Xu
 * @date May 18, 2021
 */
public class DanfossEntityManagerTest {


	private CustomizedEntityManager em = CustomizedEntityManager.getInstance();
	
	@Test
	public void testSave() {
		Device danfossDevice = new Device();
		danfossDevice.setName("test");
		em.save(danfossDevice);
	}
	
	@Test
	public void testBatchSave() {
		Device danfossDevice1 = new Device();
		danfossDevice1.setName("test5");
		Device danfossDevice2 = new Device();
		danfossDevice2.setName("test666");
		List<Device> list = new ArrayList<>();
		list.add(danfossDevice1);
		list.add(danfossDevice2);
		em.batchSave(list);
	}
	
	@Test
	public void testUpdate() {
		Device danfossDevice = new Device();
		danfossDevice.setName("test3");
		danfossDevice.setDeviceId("deviceId333");
		em.update(danfossDevice);
	}
	
	@Test
	public void testBatchUpdate() {
		Device danfossDevice = new Device();
		danfossDevice.setName("test3333444");
		
		Device danfossDevice2 = new Device();
		danfossDevice2.setName("test34444555");
		danfossDevice2.setDeviceId("deviceId");
		
		List<Object> list = new ArrayList<>();
		list.add(danfossDevice);
		list.add(danfossDevice2);
		em.batchUpdate(list);
	}
	
	@Test
	public void testDelete() {
		Device danfossDevice = em.find(Device.class, 1);
		em.delete(danfossDevice);
	}
	
	@Test
	public void testFind() {
		Device danfossDevice = em.find(Device.class, 2);
		System.out.println(danfossDevice);
	}
	
	@Test
	public void testGetQueryList() {
		List<Device> queryList = em.getQueryList("SELECT d FROM Device d ", Device.class);
		for (Device danfossDevice : queryList) {
			System.out.println(danfossDevice);
		}
	}

}
