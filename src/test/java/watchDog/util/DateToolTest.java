
package watchDog.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Description:
 * @author Matthew Xu
 * @date May 19, 2020
 */
public class DateToolTest {

	@Test
	public void testIsNowInPeriods() {
		assertTrue(DateTool.isTodayWorkday());
	}

}
