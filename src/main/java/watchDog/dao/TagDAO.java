package watchDog.dao;

import watchDog.database.DataBaseException;
import watchDog.database.DatabaseMgr;
import watchDog.database.RecordSet;

public class TagDAO extends BaseDAO {
    public static final TagDAO INSTANCE = new TagDAO();
    
    private TagDAO(){}
    
    public boolean isTagIgnore(int supervisorId,String tag)
    {
        String sql = "select count(*)::int from tags.supervisortags where '"+tag+"'=any(tags) and kidsupervisor=?";
        try {
            RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql, new Object[]{supervisorId});
            if(rs != null && rs.size()>0)
            {
                if((int)rs.get(0).get(0)>0)
                    return true;
            }
        } catch (DataBaseException e) {
            e.printStackTrace();
        }
        return false;
    }
    
}
