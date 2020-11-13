package watchDog.service;

import org.junit.Test;

import watchDog.database.DataBaseException;
import watchDog.database.DatabaseMgr;

public class PingLogService {
    public static void save(String IP)
    {
        String sql = "insert into wechat.ping_log(ip) values(?)";
        Object[] params = {IP};
        try {
            DatabaseMgr.getInstance().executeUpdate(sql, params);
        } catch (DataBaseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Test
    public void t()
    {
        save("1");
    }
}
