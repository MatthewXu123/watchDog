
package watchDog.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import watchDog.bean.register.RegisterationInfo;

/**
 * Description:
 * @author Matthew Xu
 * @date Nov 30, 2020
 */
public class RegisterationInfoDAO extends BaseDAO{

	public static final RegisterationInfoDAO INSTANCE = new RegisterationInfoDAO();
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterationInfoDAO.class);
	
	private static final String SQL_SAVE = "INSERT INTO private_registeration_info(vpn_address, registeration_date, purchaser"
				+ ", project,service_period,product_code,product_mac,router_mac,router_manufacturer,original_version,is_updated"
				+ ",is_connected, simcard_id, comment) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private RegisterationInfoDAO(){
		
	}
	
	public void saveOne(RegisterationInfo registerationInfo){
		try {
			dataBaseMgr.executeUpdate(SQL_SAVE, getParams(registerationInfo));
		} catch (Exception e) {
			LOGGER.error("",e);
		}
	}
	
	public void saveAll(Collection<RegisterationInfo> registerationInfos){
		try {
			List<Object[]> paramsList = new ArrayList<>();
			for (RegisterationInfo registerationInfo : registerationInfos) {
				paramsList.add(getParams(registerationInfo));
			}
			dataBaseMgr.executeMulUpdate(SQL_SAVE, paramsList);
		} catch (Exception e) {
			LOGGER.error("",e);
		}
	}
	
	private Object[] getParams(RegisterationInfo registerationInfo){
		return new Object[]{
				registerationInfo.getVpnAddress()
				, registerationInfo.getRegisterationDate()
				, registerationInfo.getPurchaser()
				, registerationInfo.getProject()
				, registerationInfo.getServicePeriod()
				, registerationInfo.getProductCode()
				, registerationInfo.getProductMac()
				, registerationInfo.getRouterMac()
				, registerationInfo.getRouterManufacturer()
				, registerationInfo.getOriginalVersion()
				, registerationInfo.getIsUpdated()
				, registerationInfo.getIsConnected()
				, registerationInfo.getSimCard().getId()
				, registerationInfo.getComment()
		};
	}
}
