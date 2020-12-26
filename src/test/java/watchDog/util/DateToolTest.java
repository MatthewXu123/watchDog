
package watchDog.util;

import static org.junit.Assert.*;

import java.util.Date;

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

	@Test
	public void testDiffMonths(){
		Date day1 = DateTool.parse("2021-3-31");
		Date day2 = DateTool.parse("2020-12-31");
		System.out.println(DateTool.diffMonths(day1, day2));
	}
}
