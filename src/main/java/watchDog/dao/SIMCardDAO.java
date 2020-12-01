
package watchDog.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SIMCardDAO.class);
	
	private SIMCardDAO(){
		
	}
	
	public void saveOne(SIMCard simCard){
		String sql = "INSERT INTO private_simcard(card_number, simcard_type, simcard_status) VALUES(?,?,?)";
		try {
			dataBaseMgr.executeUpdate(sql, new Object[]{simCard.getCardNumber()
            		, simCard.getSimCardType().getCode()
            		, simCard.getSimCardStatus().getCode()});
        } catch (DataBaseException e) {
        	LOGGER.error("",e);
        }
	}
	
	public void saveAll(Collection<SIMCard> simCards){
		String sql = "INSERT INTO private_simcard(card_number, simcard_type, simcard_status) VALUES(?,?,?)";
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
	
	public SIMCard getOneById(int id){
		String sql = "SELECT * FROM private_simcard WHERE id = ?";
		return getOne(sql, id);
	}
	
	public SIMCard getOneByCardNumber(String cardNumber){
		String sql = "SELECT * FROM private_simcard WHERE card_number = ?";
		return getOne(sql, cardNumber);
	}
	
	private SIMCard getOne(String sql, Object object){
		SIMCard simCard = new SIMCard();
		try {
            RecordSet rs = dataBaseMgr.executeQuery(sql, new Object[]{object});
            if(rs != null && rs.size() > 0){
            	Record record = rs.get(0);
            	
            	simCard.setId((int)record.get(0));
            	simCard.setCardNumber((String)record.get(1));
            	simCard.setSimCardType(SIMCardType.getOneByCode((int)record.get(2)));
            	simCard.setSimCardStatus(SIMCardStatus.getOneByCode((int)record.get(3)));
            }
        } catch (DataBaseException e) {
        	LOGGER.error("",e);
        }
        return simCard;
	}
}
