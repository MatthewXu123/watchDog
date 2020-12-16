
package watchDog.dao;

import java.util.ArrayList;

import org.junit.Test;

import watchDog.BaseTest;
import watchDog.wechat.bean.WechatUser;

/**
 * Description:
 * @author Matthew Xu
 * @date May 22, 2020
 */
public class FaxInfoDAOTest implements BaseTest{

	private FaxInfoDAO faxInfoDAO = FaxInfoDAO.INSTANCE;
	
	/**
	 * Test method for {@link watchDog.dao.FaxInfoDAO#getList(java.util.Date, java.lang.Object[])}.
	 */
	@Test
	public void testGetList() {
		long start = System.currentTimeMillis();
		faxInfoDAO.getFaxInfoList();
		logger.info("共耗时："  + ( System.currentTimeMillis() - start) / 1000);
		
	}
	
}
