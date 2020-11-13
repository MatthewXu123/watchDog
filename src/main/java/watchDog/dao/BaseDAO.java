
package watchDog.dao;

import watchDog.database.DatabaseMgr;

/**
 * Description:
 * @author Matthew Xu
 * @date May 15, 2020
 */
public abstract class BaseDAO {

	protected DatabaseMgr dateBaseMgr =  DatabaseMgr.getInstance();
	
}
