
package watchDog.dao;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import watchDog.bean.register.RegisterationInfo;
import watchDog.bean.register.SIMCard;

/**
 * Description:
 * @author Matthew Xu
 * @date Nov 30, 2020
 */
public class RegisterationInfoDAOTest {

	private RegisterationInfoDAO registerationInfoDAO = RegisterationInfoDAO.INSTANCE;
	
	/**
	 * Test method for {@link watchDog.dao.RegisterationInfoDAO#saveOne(watchDog.bean.register.RegisterationInfo)}.
	 */
	@Test
	public void testSaveOne() {
		SIMCard simCard = new SIMCard();
		simCard.setId(2);
		RegisterationInfo registerationInfo = new RegisterationInfo();
		registerationInfo.setVpnAddress("192.168.100.1");
		registerationInfo.setRegisterationDate(new Date());
		registerationInfo.setPurchaser("开利冷链");
		registerationInfo.setEndUser("盒马生鲜");
		registerationInfo.setServicePeriod(2);
		registerationInfo.setProductCode("productCode");
		registerationInfo.setProductMac("productMac");
		registerationInfo.setRouterMac("routerMac");
		registerationInfo.setRouterManufacturer("routerMan");
		registerationInfo.setOriginalVersion("1.2");
		registerationInfo.setIsUpdated(false);
		registerationInfo.setIsConnected(false);
		registerationInfo.setSimCard(simCard);
		registerationInfo.setComment("comment");
		registerationInfoDAO.saveOne(registerationInfo);
	}

}
