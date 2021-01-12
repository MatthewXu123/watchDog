
package watchDog.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import watchDog.bean.RetailProject;
import watchDog.database.Record;
import watchDog.database.RecordSet;

/**
 * Description:
 * @author Matthew Xu
 * @date Dec 31, 2020
 */
public class RetailProjectDAO extends BaseDAO{

	public static final RetailProjectDAO INSTANCE = new RetailProjectDAO();
	
	private static final Logger LOGGER = Logger.getLogger(RetailProjectDAO.class);
	
	private static final String COLUMNS = "customer, description, ip, province, purchaser, manufacturer,"
			+ "cabniet_supplier, contact_person, contact_mobile, sales, cst_person, delivery_time, commission_planned_time,"
			+ "commission_start_time,warranty_start_time, warranty_period,warranty_end_time,project_type,project_status,"
			+ "project_comment,project_address";
	
	private static final String SQL_SAVE = "INSERT INTO wechat.retail_project(" + COLUMNS + ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private static final String SQL_DELETE_ALL = "DELETE FROM wechat.retail_project";
	
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
	
	public void deleteAll(){
		try {
			dataBaseMgr.executeUpdate(SQL_DELETE_ALL);
		} catch (Exception e) {
			LOGGER.error("",e);
		}
	}
	
	public List<RetailProject> getAll(){
		String sql = "SELECT * FROM wechat.retail_project";
		return getAll(sql);
	}
	
	private List<RetailProject> getAll(String sql){
		List<RetailProject> infoList = new ArrayList<>();
		try {
			RecordSet recordSet = dataBaseMgr.executeQuery(sql);
			if(recordSet != null && recordSet.size() > 0){
				for(int i = 0; i < recordSet.size(); i++)
					infoList.add(constructRetailProject(recordSet.get(i)));
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return infoList;
	}
	
	private RetailProject constructRetailProject(Record record){
		RetailProject retailProject = new RetailProject();
		retailProject.setId(record.get(0) != null ? (int)record.get(0) : null);
		retailProject.setCustomer(record.get(1) != null ? (String)record.get(1) : null);
		retailProject.setDescription(record.get(2) != null ? (String)record.get(2) : null);
		retailProject.setIp(record.get(3) != null ? (String)record.get(3) : null);
		retailProject.setProvince(record.get(4) != null ? (String)record.get(4) : null);
		retailProject.setPurchaser(record.get(5) != null ? (String)record.get(5) : null);
		retailProject.setManufacturer(record.get(6) != null ? (String)record.get(6) : null);
		retailProject.setCabnietSupplier(record.get(7) != null ? (String)record.get(7) : null);
		retailProject.setContactPerson(record.get(8) != null ? (String)record.get(8) : null);
		retailProject.setContactMobile(record.get(9) != null ? (String)record.get(9) : null);
		retailProject.setSales(record.get(10) != null ? (String)record.get(10) : null);
		retailProject.setCstPerson(record.get(11) != null ? (String)record.get(11) : null);
		retailProject.setDeliveryTime(record.get(12) != null ? new Date(((java.sql.Date)record.get(12)).getTime()) : null);
		retailProject.setCommissionPlannedTime(record.get(13) != null ? new Date(((java.sql.Date)record.get(13)).getTime()) : null);
		retailProject.setCommissionStartTime(record.get(14) != null ? new Date(((java.sql.Date)record.get(14)).getTime()) : null);
		retailProject.setWarrantyStartTime(record.get(15) != null ? new Date(((java.sql.Date)record.get(15)).getTime()) : null);
		retailProject.setWarrantyPeriod(record.get(16) != null ? new Date(((java.sql.Date)record.get(16)).getTime()) : null);
		retailProject.setWarrantyEndTime(record.get(17) != null ? new Date(((java.sql.Date)record.get(17)).getTime()) : null);
		retailProject.setProjectType(record.get(18) != null ? (String)record.get(18) : null);
		retailProject.setProjectStatus(record.get(19) != null ? (String)record.get(19) : null);
		retailProject.setProjectComment(record.get(20) != null ? (String)record.get(20) : null);
		retailProject.setProjectAddress(record.get(21) != null ? (String)record.get(21) : null);
		return retailProject;
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
