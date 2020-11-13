
package watchDog.dao;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Description:
 * @author Matthew Xu
 * @date May 18, 2020
 */
public class PropertyDAOTest {

	private PropertyDAO propertyDAO = PropertyDAO.INSTANCE;
	
	/**
	 * Test method for {@link watchDog.dao.PropertyDAO#getValue(java.lang.String)}.
	 */
	@Test
	public void testGetValue() {
		String value = propertyDAO.getOne("resource_version");
		assertTrue(value.equals("3"));
	}

	@Test
	public void testSaveOne(){
		propertyDAO.saveOne("test", "tom");
		String value = propertyDAO.getOne("tom");
		assertTrue(value.equals("tom"));
	}
	
	/**
	 * Test method for {@link watchDog.dao.PropertyDAO#updateValue(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testUpdateValue() {
		propertyDAO.updateOne("tom", "matty");
		String value = propertyDAO.getOne("tom");
		assertTrue(value.equals("matty"));
	}

}
