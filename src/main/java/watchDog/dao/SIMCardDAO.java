
package watchDog.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;


import watchDog.bean.register.SIMCard;
import watchDog.bean.register.SIMCardStatus;
import watchDog.bean.register.SIMCardType;
import watchDog.database.DataBaseException;
import watchDog.database.Record;
import watchDog.database.RecordSet;

/**
 * Description:
 * @author Matthew Xu
 * @date Nov 30, 2020
 */
public class SIMCardDAO extends BaseDAO{

	public static final SIMCardDAO INSTANCE = new SIMCardDAO();
	
	private static final Logger LOGGER = Logger.getLogger(SIMCardDAO.class);
	
	private SIMCardDAO(){
		
	}
	
	public void saveOne(SIMCard simCard){
		String sql = "INSERT INTO wechat.simcard(card_number, simcard_type, simcard_status) VALUES(?,?,?)";
		try {
			dataBaseMgr.executeUpdate(sql, new Object[]{simCard.getCardNumber()
            		, simCard.getSimCardType().getCode()
            		, simCard.getSimCardStatus().getCode()});
        } catch (DataBaseException e) {
        	LOGGER.error("",e);
        }
	}
	
	public void saveAll(Collection<SIMCard> simCards){
		String sql = "INSERT INTO wechat.simcard(card_number, simcard_type, simcard_status) VALUES(?,?,?)";
		List<Object[]> paramsList = new ArrayList<>();
		try {
			for(SIMCard simCard : simCards){
				paramsList.add(new Object[]{simCard.getCardNumber()
	            		, simCard.getSimCardType().getCode()
	            		, simCard.getSimCardStatus().getCode()});
			}
			
			dataBaseMgr.executeMulUpdate(sql, paramsList);
        } catch (DataBaseException e) {
        	LOGGER.error("",e);
        }
	}
	
	public SIMCard getOneById(Integer id){
		String sql = "SELECT * FROM wechat.simcard WHERE id = ?";
		return getOne(sql, id);
	}
	
	public SIMCard getOneByCardNumber(String cardNumber){
		String sql = "SELECT * FROM wechat.simcard WHERE card_number = ?";
		return getOne(sql, cardNumber);
	}
	
	public List<SIMCard> getAllByStatus(int status){
		String sql = "SELECT * FROM wechat.simcard WHERE simcard_status = ?";
		return getAll(sql, status);
	}
	
	public List<SIMCard> getAllByType(SIMCardType simCardType){
		String sql = "SELECT * FROM wechat.simcard WHERE simcard_type = ?";
		return getAll(sql, simCardType.getCode());
	}
	
	public List<SIMCard> getAll(){
		String sql = "SELECT * FROM wechat.simcard ORDER BY simcard_status";
		return getAll(sql, null);
	}
	
	public void updateStatus(int id, SIMCardStatus simCardStatus){
		String sql = "UPDATE wechat.simcard SET simcard_status = ? WHERE id = ?";
		try {
			dataBaseMgr.executeUpdate(sql, new Object[]{simCardStatus.getCode(), id});
		} catch (Exception e) {
			LOGGER.error("",e);
		}
	}
	
	private List<SIMCard> getAll(String sql, Object object){
		List<SIMCard> list = new ArrayList<>();
		try {
			RecordSet rs = null;
			if(object == null)
				rs = dataBaseMgr.executeQuery(sql);
			else
				rs = dataBaseMgr.executeQuery(sql, new Object[]{object});
			
            if(rs != null && rs.size() > 0){
            	for(int i = 0; i < rs.size(); i++){
            		list.add(getOneSIMCardByRecord(rs.get(i)));
            	}
            }
        } catch (DataBaseException e) {
        	LOGGER.error("",e);
        }
        return list;
	}
	
	private SIMCard getOne(String sql, Object object){
		SIMCard simCard = new SIMCard();
		try {
            RecordSet rs = dataBaseMgr.executeQuery(sql, new Object[]{object});
            if(rs != null && rs.size() > 0){
            	Record record = rs.get(0);
            	simCard = getOneSIMCardByRecord(record);
            }
        } catch (DataBaseException e) {
        	LOGGER.error("",e);
        }
        return simCard;
	}
	
	private SIMCard getOneSIMCardByRecord(Record record){
		SIMCard simCard = new SIMCard();
		simCard.setId((int)record.get(0));
    	simCard.setCardNumber((String)record.get(1));
    	simCard.setSimCardType(SIMCardType.getOneByCode((int)record.get(2)));
    	simCard.setSimCardStatus(SIMCardStatus.getOneByCode((int)record.get(3)));
    	return simCard;
		
	}
}
