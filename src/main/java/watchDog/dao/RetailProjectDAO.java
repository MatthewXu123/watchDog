
package watchDog.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import watchDog.bean.RetailProject;
import watchDog.bean.register.RegisterationInfo;

/**
 * Description:
 * @author Matthew Xu
 * @date Dec 31, 2020
 */
public class RetailProjectDAO {

	public void saveOne(RegisterationInfo registerationInfo){
		try {
			dataBaseMgr.executeUpdate(SQL_SAVE, getParams(registerationInfo).toArray());
		} catch (Exception e) {
			LOGGER.error("",e);
		}
	}
	
	public void saveAll(Collection<RegisterationInfo> registerationInfos){
		try {
			List<Object[]> paramsList = new ArrayList<>();
			for (RegisterationInfo registerationInfo : registerationInfos) {
				paramsList.add(getParams(registerationInfo).toArray());
			}
			dataBaseMgr.executeMulUpdate(SQL_SAVE, paramsList);
		} catch (Exception e) {
			LOGGER.error("",e);
		}
	}
	
	private List<Object> getParams(RetailProject retailProject){
		return Stream.of(retailProject.getCustomer()
				, retailProject.getDescription()
				, retailProject.getIp()
				, retailProject.getProvince()
				, retailProject.getPurchaser()
				, retailProject.getManufacturer()
				, retailProject.getCabnietSupplier()
				, retailProject.getType()
				, retailProject.getContactPerson()
				, retailProject.getSales()
				, retailProject.getContactMobile()
				, retailProject.getDeliveryTime()
				, retailProject.getCommissionPlannedTime()
				, retailProject.getStatus()
				, retailProject.getCstPerson()
				, retailProject.getCommissionStartTime()
				, retailProject.getComment()
				, retailProject.getAddress()
				, retailProject.getWarrantyStartTime()
				, retailProject.getWarrantyPeriod()
				, retailProject.getWarrantyEndTime()).collect(Collectors.toList());
	}
}
