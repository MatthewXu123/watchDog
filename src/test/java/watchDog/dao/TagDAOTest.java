
package watchDog.dao;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Description:
 * @author Matthew Xu
 * @date Jul 3, 2020
 */
public class TagDAOTest {

	/**
	 * Test method for {@link watchDog.dao.TagDAO#isTagIgnore(int, java.lang.String)}.
	 */
	@Test
	public void testIsTagIgnore() {
		System.out.println(TagDAO.INSTANCE.isTagIgnore(245, "#c_ignore"));
	}

}
