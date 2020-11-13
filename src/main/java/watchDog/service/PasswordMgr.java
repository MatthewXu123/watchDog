package watchDog.service;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

public class PasswordMgr {
    private Properties properties = new Properties();
    private static PasswordMgr instance = null;
    public synchronized static PasswordMgr getInstance()
    {
        if(instance == null)
            instance = new PasswordMgr();
        return instance;
    }
    
    private PasswordMgr()
    {
        try{
            FileInputStream in = new FileInputStream("/watchDog/conf/client_password.properties");
            properties.load(in);
            in.close();
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    public String getPassword(String client)
    {
        if(StringUtils.isBlank(client))
            return null;
        if(properties != null)
        {
            return (String)properties.get(client);
        }
        return null;
    }
}
