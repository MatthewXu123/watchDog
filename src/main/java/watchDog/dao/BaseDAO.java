
package watchDog.dao;

import watchDog.database.DatabaseMgr;

/**
 * Description:
 * @author Matthew Xu
 * @date May 15, 2020
 */
public abstract class BaseDAO {

	protected DatabaseMgr dataBaseMgr =  DatabaseMgr.getInstance();
	
	protected RegisterationInfoDAO registerationInfoDAO = RegisterationInfoDAO.INSTANCE;
	
	protected SIMCardDAO simCardDAO  = SIMCardDAO.INSTANCE;

	
}
