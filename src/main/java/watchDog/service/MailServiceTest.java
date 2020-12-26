
package watchDog.service;

import java.util.List;

import org.junit.Test;

import watchDog.bean.SiteInfo;

/**
 * Description:
 * @author Matthew Xu
 * @date Dec 25, 2020
 */
public class MailServiceTest {

	/**
	 * Test method for {@link watchDog.service.MailService#sendServiceMails(java.util.List)}.
	 */
	@Test
	public void testSendServiceMails() {
		List<SiteInfo> sitesOutOfService = SiteInfoService.getSitesOutOfService();
		MailService.INSTANCE.sendServiceMails(sitesOutOfService);
	}

}
