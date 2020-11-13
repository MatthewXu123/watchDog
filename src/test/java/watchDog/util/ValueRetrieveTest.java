
package watchDog.util;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

/**
 * Description:
 * @author Matthew Xu
 * @date May 28, 2020
 */
public class ValueRetrieveTest {

	/**
	 * Test method for {@link watchDog.util.ValueRetrieve#getValue(java.lang.String, java.lang.String, java.lang.String[])}.
	 */
	@Test
	public void testGetValueStringStringStringArray() {
		Map<String, String> value = ValueRetrieve.getValue("192.168.88.187", "2.01222", "Po4");
		assertTrue(true);
	}

}
