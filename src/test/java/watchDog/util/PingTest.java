
package watchDog.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Description:
 * @author Matthew Xu
 * @date Sep 18, 2020
 */
public class PingTest {

	@Test
	public void test() {
		boolean workStatusCheck = Ping.workStatusCheck("BOSS", "192.168.100.24");
	}

}
