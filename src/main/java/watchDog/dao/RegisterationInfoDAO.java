
package watchDog.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;


import watchDog.bean.register.RegisterationInfo;
import watchDog.database.Record;
import watchDog.database.RecordSet;

/**
 * Description:
 * @author Matthew Xu
 * @date Nov 30, 2020
 */
public class RegisterationInfoDAO extends BaseDAO{

	public static final RegisterationInfoDAO INSTANCE = new RegisterationInfoDAO();
	
	private static final Logger LOGGER = Logger.getLogger(RegisterationInfoDAO.class);
	
	private static final String COLUMNS = "vpn_address, registeration_date, purchaser, project"
			+ ",service_period,product_code,product_mac,router_mac"
			+ ",router_manufacturer,original_version,is_updated,is_connected, simcard_id, comment,is_deleted";
	
	private static final String SQL_SAVE = "INSERT INTO wechat.registeration_info(" + COLUMNS + ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private RegisterationInfoDAO(){
		
	}
	
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
	
	public RegisterationInfo getOneById(int id){
		String sql = "SELECT * FROM wechat.registeration_info WHERE id = ?";
		RegisterationInfo info = new RegisterationInfo();
		try {
			RecordSet recordSet = dataBaseMgr.executeQuery(sql, new Object[]{id});
			if(recordSet != null && recordSet.size() > 0){
				info = constructRegisterationInfo(recordSet.get(0));
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return info;
	}
	
	public List<RegisterationInfo> getAll(){
		String sql = "SELECT * FROM wechat.registeration_info WHERE is_deleted = false";
		return getAll(sql);
	}
	
	public List<RegisterationInfo> getAllOrderByRegisterationDate(){
		String sql = "(SELECT * FROM wechat.registeration_info WHERE is_deleted = FALSE and registeration_date IS NOT NULL ORDER BY registeration_date DESC)"
				+ "UNION ALL "
				+ "SELECT * FROM wechat.registeration_info WHERE is_deleted = FALSE and registeration_date IS NULL";
		return getAll(sql);
	}
	
	
	public List<String> getAllPurchaser(){
		String sql = "SELECT DISTINCT purchaser FROM wechat.registeration_info"
				+ " WHERE purchaser IS NOT NULL AND purchaser <> ''";
		List<String> purchaserList = new ArrayList<>();
		try {
			RecordSet recordSet = dataBaseMgr.executeQuery(sql);
			if(recordSet != null && recordSet.size() > 0){
				for(int i = 0; i < recordSet.size(); i++)
					purchaserList.add((String)recordSet.get(i).get(0));
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return purchaserList;
	}
	
	public void updateOne(RegisterationInfo registerationInfo){
		String updateSql = "UPDATE wechat.registeration_info SET ";
		for(String column : COLUMNS.split(",")){
			updateSql += column.trim() + " = ?,";
		}
		updateSql = updateSql.substring(0, updateSql.length() - 1) + " WHERE id = ?";
		try {
			dataBaseMgr.executeUpdate(updateSql, getUpdateParams(registerationInfo));
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}
	
	private List<RegisterationInfo> getAll(String sql){
		List<RegisterationInfo> infoList = new ArrayList<>();
		try {
			RecordSet recordSet = dataBaseMgr.executeQuery(sql);
			if(recordSet != null && recordSet.size() > 0){
				for(int i = 0; i < recordSet.size(); i++)
					infoList.add(constructRegisterationInfo(recordSet.get(i)));
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return infoList;
	}
	
	private RegisterationInfo constructRegisterationInfo(Record record){
		RegisterationInfo info = new RegisterationInfo();
		info.setId(record.get(0) != null ? (int)record.get(0) : null);
		info.setVpnAddress(record.get(1) != null ? (String)record.get(1) : null);
		info.setRegisterationDate(record.get(2) != null ? new Date(((java.sql.Date)record.get(2)).getTime()) : null);
		info.setPurchaser(record.get(3) != null ? (String)record.get(3) : null);
		info.setProject(record.get(4) != null ? (String)record.get(4) : null);
		info.setServicePeriod(record.get(5) != null ? (int)record.get(5) : null);
		info.setProductCode(record.get(6) != null ? (String)record.get(6) : null);
		info.setProductMac(record.get(7) != null ? (String)record.get(7) : null);
		info.setOriginalVersion(record.get(8) != null ? (String)record.get(8) : null);
		info.setIsConnected((boolean)record.get(9));
		info.setIsUpdated((boolean)record.get(10));
		info.setRouterMac(record.get(11) != null ? (String)record.get(11) : null);
		info.setRouterManufacturer(record.get(12) != null ? (String)record.get(12) : null);
		// Find the SIM card.
		Integer simCardId = record.get(13) != null ? (int)record.get(13) : null;
		info.setSimCard(simCardDAO.getOneById(simCardId));
		
		info.setComment(record.get(14) != null ? (String)record.get(14) : null);
		info.setIsDeleted((boolean)record.get(15));
		info.setInsertTime(record.get(16) != null ? new Date(((Timestamp)record.get(16)).getTime()) : null);
		return info;
	}
	
	private List<Object> getParams(RegisterationInfo registerationInfo){
		return Stream.of(registerationInfo.getVpnAddress()
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
				, registerationInfo.getSimCard() == null ? null : registerationInfo.getSimCard().getId()
				, registerationInfo.getComment()
				, registerationInfo.getIsDeleted()).collect(Collectors.toList());
	}
	
	private Object[] getUpdateParams(RegisterationInfo registerationInfo){
		List<Object> params = getParams(registerationInfo);
		params.add(registerationInfo.getId());
		return params.toArray();
	}
}
