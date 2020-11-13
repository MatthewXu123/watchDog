package watchDog.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import watchDog.bean.SiteInfo;

public class Ping {
	private static final Logger logger = Logger.getLogger(Ping.class);
	
	public static boolean ping(String ipAddress, int pingTimes, int timeOut)
	{
		return ping(ipAddress,pingTimes,timeOut,true);
	}
    public static boolean ping(String ipAddress, int pingTimes, int timeOut,boolean checkResult) {  
        BufferedReader in = null;  
        Runtime r = Runtime.getRuntime();  // 将要执行的ping命令,此命令是windows格式的命令  
        String pingCommand = "ping " + ipAddress + " -n " + pingTimes    + " -w " + timeOut;  
        Process p = null;
        try {   // 执行命令并获取输出  
            p = r.exec(pingCommand);
            if (p == null) {    
                return false;   
            }
            if(checkResult)
            {
	            in = new BufferedReader(new InputStreamReader(p.getInputStream()));   // 逐行检查输出,计算类似出现=23ms TTL=62字样的次数  
	            String line = null;   
	            while ((line = in.readLine()) != null) {
	            	if(getCheckResult(line)  == 1)
	            		return true;
	            }   // 如果出现类似=23ms TTL=62这样的字样,出现的次数=测试次数则返回真  
	            return false;
            }
            else
            	return true;
        } catch (Exception ex) {   
            ex.printStackTrace();   // 出现异常则返回假  
            return false;  
        } finally {   
            try {   
            	if(in != null)
            		in.close();
                p.destroy();
            } catch (IOException e) {    
                e.printStackTrace();   
            }  
        }
    }
    
    public static void alwaysPing(String ipAddress)
    {
        Runtime r = Runtime.getRuntime();  // 将要执行的ping命令,此命令是windows格式的命令  
        String pingCommand = "ping " + ipAddress + " -l 1 -t";  
        try {   // 执行命令并获取输出  
            r.exec(pingCommand);
        } catch (Exception ex) {   
            ex.printStackTrace();   // 出现异常则返回假  
        }
    }
    public static void killPing()
    {
    	Runtime r = Runtime.getRuntime();  // 将要执行的ping命令,此命令是windows格式的命令  
        String pingCommand = "taskkill /f /IM ping.exe";  
        try {   // 执行命令并获取输出  
            r.exec(pingCommand);
        } catch (Exception ex) {   
            ex.printStackTrace();   // 出现异常则返回假  
        }
    }
    //若line含有=18ms TTL=16字样,说明已经ping通,返回1,否則返回0.
    private static int getCheckResult(String line) {    
        Pattern pattern = Pattern.compile("(\\d+ms)(\\s+)(TTL=\\d+)",    Pattern.CASE_INSENSITIVE);  
        Matcher matcher = pattern.matcher(line);  
        while (matcher.find()) {
            return 1;
        }
        return 0; 
    }
    
    public static boolean workStatusCheck(String ktype, String ip)
	{
		if(!SiteInfo.BOSS.equals(ktype))
			return true;
		String url = "https://"+ip+"/";
		if(SiteInfo.BOSS.equals(ktype))
			url += "boss/";
//		else if(SiteInfo.PVP.equals(s.getKtype()))
//			url += "PlantVisorPRO/";
		url += "probe/PVProStatus.jsp";
		String result = ValueRetrieve.httpGet(url);
		if("0".equals(result))
			return true;
		else
		{
			logger.info(ip +" engine down");
			return false;
		}
	}
}
