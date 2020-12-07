
package watchDog.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import watchDog.bean.register.RegisterationInfo;
import watchDog.bean.register.SIMCard;

/**
 * Description:
 * @author Matthew Xu
 * @date Nov 30, 2020
 */
public class RegisterationInfoDAOTest {

	private RegisterationInfoDAO registerationInfoDAO = RegisterationInfoDAO.INSTANCE;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterationInfoDAOTest.class);
	
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
		registerationInfo.setProject("盒马生鲜");
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
	
	/**
	 * Test method for {@link watchDog.dao.RegisterationInfoDAO#saveAll(watchDog.bean.register.RegisterationInfo)}.
	 */
	@Test
	public void testSaveAll() {
		SIMCard simCard = new SIMCard();
		simCard.setId(2);
		RegisterationInfo registerationInfo = new RegisterationInfo();
		registerationInfo.setVpnAddress("192.168.100.1");
		registerationInfo.setRegisterationDate(new Date());
		registerationInfo.setPurchaser("开利冷链");
		registerationInfo.setProject("盒马生鲜");
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
		
		RegisterationInfo registerationInfo2 = new RegisterationInfo();
		registerationInfo2.setVpnAddress("192.168.100.12");
		registerationInfo2.setRegisterationDate(new Date());
		registerationInfo2.setPurchaser("开利冷链");
		registerationInfo2.setProject("盒马生鲜");
		registerationInfo2.setServicePeriod(2);
		registerationInfo2.setProductCode("productCode");
		registerationInfo2.setProductMac("productMac");
		registerationInfo2.setRouterMac("routerMac");
		registerationInfo2.setRouterManufacturer("routerMan");
		registerationInfo2.setOriginalVersion("1.2");
		registerationInfo2.setIsUpdated(false);
		registerationInfo2.setIsConnected(false);
		registerationInfo2.setSimCard(simCard);
		registerationInfo2.setComment("comment");
		
		List<RegisterationInfo> list = new ArrayList<>();
		list.add(registerationInfo);
		list.add(registerationInfo2);
		registerationInfoDAO.saveAll(list);
	}
	
	/**
	 * Test method for {@link watchDog.dao.RegisterationInfoDAO#getAll(watchDog.bean.register.RegisterationInfo)}.
	 */
	@Test
	public void testGetAll(){
		List<RegisterationInfo> list = registerationInfoDAO.getAll();
		String string = JSONObject.toJSONString(list, SerializerFeature.WriteMapNullValue);
		System.out.println(string);
	}

}
