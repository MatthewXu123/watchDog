
package watchDog.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import watchDog.bean.RetailProject;

/**
 * Description:
 * @author Matthew Xu
 * @date Dec 31, 2020
 */
public class RetailProjectDAO extends BaseDAO{

	public static final RetailProjectDAO INSTANCE = new RetailProjectDAO();
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RetailProjectDAO.class);
	
	private static final String COLUMNS = "customer, description, ip, province, purchaser, manufacturer,"
			+ "cabniet_supplier, contact_person, contact_mobile, sales, cst_person, delivery_time, commission_planned_time,"
			+ "commission_start_time,warranty_start_time, warranty_period,warranty_end_time,project_type,project_status,"
			+ "project_comment,project_address";
	
	private static final String SQL_SAVE = "INSERT INTO wechat.retail_project(" + COLUMNS + ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private RetailProjectDAO(){
		
	}
	
	public void saveOne(RetailProject retailProject){
		try {
			dataBaseMgr.executeUpdate(SQL_SAVE, getParams(retailProject).toArray());
		} catch (Exception e) {
			LOGGER.error("",e);
		}
	}
	
	public void saveAll(Collection<RetailProject> retailProjects){
		try {
			List<Object[]> paramsList = new ArrayList<>();
			for (RetailProject retailProject : retailProjects) {
				paramsList.add(getParams(retailProject).toArray());
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
				, retailProject.getContactPerson()
				, retailProject.getContactMobile()
				, retailProject.getSales()
				, retailProject.getCstPerson()
				, retailProject.getDeliveryTime()
				, retailProject.getCommissionPlannedTime()
				, retailProject.getCommissionStartTime()
				, retailProject.getWarrantyStartTime()
				, retailProject.getWarrantyPeriod()
				, retailProject.getWarrantyEndTime()
				, retailProject.getProjectType()
				, retailProject.getProjectStatus()
				, retailProject.getProjectComment()
				, retailProject.getProjectAddress()
				).collect(Collectors.toList());
	}
}
