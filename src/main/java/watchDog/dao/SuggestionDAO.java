package watchDog.dao;

import watchDog.database.DataBaseException;
import watchDog.database.DatabaseMgr;

public class SuggestionDAO {
    public static void insert(String userId,String suggestion)
    {
        String sql = "insert into wechat.suggestion(user_id,suggestion) values(?,?)";
        Object[] params = {userId,suggestion};
        try {
            DatabaseMgr.getInstance().executeUpdate(sql, params);
        } catch (DataBaseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
