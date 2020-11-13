
package watchDog.dao;

import watchDog.database.RecordSet;

/**
 * Description:
 * @author Matthew Xu
 * @date May 18, 2020
 */
public class PropertyDAO extends BaseDAO{
	
	public static final PropertyDAO INSTANCE = new PropertyDAO();

	private PropertyDAO(){}
	
	public String getOne(String key){
		String sql = "select value from private_property where key = ?";
		Object[] params = {key};
        try{
        	RecordSet rs = dateBaseMgr.executeQuery(sql,params);
        	return (String)rs.get(0).get(0);
        } catch (Exception e) {
		}
        return null;
	}
	
	public void saveOne(String key, String value){
		String sql = "insert into private_property values(?,?,now())";
		Object[] params = {key, value};
        try{
        	dateBaseMgr.executeUpdate(sql,params);
        } catch (Exception e) {
		}
	}
	
	public void updateOne(String key, String value){
		String sql = "update private_property set value = ? where key = ?";
		Object[] params = {value, key};
        try{
        	dateBaseMgr.executeUpdate(sql,params);
        } catch (Exception e) {
		}
	}
}
