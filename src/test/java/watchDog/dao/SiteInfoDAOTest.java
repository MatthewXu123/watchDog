
package watchDog.dao;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import watchDog.bean.SiteInfo;

/**
 * Description:
 * @author Matthew Xu
 * @date May 14, 2020
 */
public class SiteInfoDAOTest {

	private SiteInfoDAO siteInfoDAO = SiteInfoDAO.INSTANCE;
	
	/**
	 * Test method for {@link watchDog.dao.SiteInfoDAO#isSiteExist(int)}.
	 */
	@Test
	public void testIsSiteExist() {
	}

	/**
	 * Test method for {@link watchDog.dao.SiteInfoDAO#getList(java.lang.Boolean)}.
	 */
	@Test
	public void testGetList() {
		List<SiteInfo> list = siteInfoDAO.getList(false);
		int i = 0;
		for (SiteInfo siteInfo : list) {
			if(siteInfo.getProbeissue() == null){
				i ++;
			}
		}
		assertTrue(i==0);
	}

	/**
	 * Test method for {@link watchDog.dao.SiteInfoDAO#saveOne(int, java.util.Date, boolean, java.lang.String, int, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testSaveOne() {
	}

	/**
	 * Test method for {@link watchDog.dao.SiteInfoDAO#updateOne(int, java.util.Date, boolean, java.lang.String, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testUpdateOne() {
	}
	
}
