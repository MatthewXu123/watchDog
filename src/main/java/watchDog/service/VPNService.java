
package watchDog.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.junit.Test;

import ch.ethz.ssh2.Connection;
import me.chanjar.weixin.common.util.StringUtils;
import watchDog.bean.PageVPNInfo;
import watchDog.bean.SiteInfo;
import watchDog.listener.Dog;
import watchDog.thread.AlarmNotificationMain;
import watchDog.util.HttpSendUtil;
import watchDog.util.Ping;
import watchDog.util.RemoteCommandUtil;

/**
 * Description:
 * @author Matthew Xu
 * @date May 12, 2020
 */
public class VPNService {
    private static final Logger logger = Logger.getLogger(VPNService.class);
	public static final VPNService INSTANCE = new VPNService();
	private Map<String,String> IPCity = new HashMap<>();
	
	public VPNService(){}
	
	//@Test
	public void test()
	{
	    //INSTANCE.getVPNInfo("pvp89");
	}
	//@Test
	public void t()
	{
	    String ip = "192.168.8.1";
	    Connection login = RemoteCommandUtil.login(ip,22, "root", "adminCarel");
        if(login.isAuthenticationComplete())
        {
            String output = RemoteCommandUtil.execute(login, "passwd");
            if(output.indexOf("HWaddr")>0)
            {
                String[] s = output.split("HWaddr");
                output = s[1].replace(";", "").trim();
                logger.info(ip+"="+output);
            }
        }
	}
	@Test
	public void testt() throws InterruptedException
	{
	    for(int j=68;j<69;j++)
	    {
    	    for(int i=2;i<255;i++)
    	    {
        	    String ip = "192.168."+j+"."+i;
        	    System.out.println("checking "+ip);
        	    boolean pingOK = Ping.ping(ip, 1, 5000);
        	    System.out.println("after ping");
        	    if(pingOK)
        	    {
        	        RemoteThread r = new RemoteThread(ip);
        	        Thread t = new Thread(r,ip);
        	        t.start();
            	    Thread.sleep(5000);
        	    }
    	    }
	    }
	}
	class RemoteThread implements Runnable
	{
	    String ip = null;
	    RemoteThread(String ip)
	    {
	        this.ip = ip;
	    }
        @Override
        public void run() {
            // TODO Auto-generated method stub
            Connection login = RemoteCommandUtil.login(ip,22, "root", "adminCarel");
            if(login.isAuthenticationComplete())
            {
                String output = RemoteCommandUtil.execute(login, "ifconfig|grep eth0");
                if(output.indexOf("HWaddr")>0)
                {
                    String[] s = output.split("HWaddr");
                    output = s[1].replace(";", "").trim();
                    logger.info(ip+"="+output);
                }
            }
            else
            {
                logger.info(ip+" can not login");
            }
        }
	    
	}
	public PageVPNInfo getVPNInfo(String account)
	{
	    account += " ";
	    Connection login = RemoteCommandUtil.login("192.168.88.1",null, "root", "carel@suzhou2020CARELIOT");
        String output = RemoteCommandUtil.execute(login, "last|grep gone|grep '"+account+"'|awk '{for(i=1;i<8;i=i+1){printf $i\",\"}; print \"\"}'");
        String[] lines = output.split(";");
        return buildInfo(lines,0);
	}
	public List<PageVPNInfo> getVPNServerOutPut(){
	    List<PageVPNInfo> result = new ArrayList<>();
	    try{
    		// The default port is 22.
    		Connection login = RemoteCommandUtil.login("192.168.88.1",null, "root", "carel@suzhou2020CARELIOT");
    		String output = RemoteCommandUtil.execute(login, "last|grep gone|sort|awk '{for(i=1;i<8;i=i+1){printf $i\",\"}; print \"\"}'");
    		String[] lines = output.split(";");
    		Dog dog = Dog.getInstance();
    		//only for debug
    		//dog.loadFromDB();
    		for (int i = 0; i < lines.length; i++) {
    		    PageVPNInfo v = buildInfo(lines,i);
    		    if(v != null)
    		        result.add(v);
    		}
	    }
		catch(Exception ex)
	    {
		    logger.error("",ex);
	    }
		return result;
	}
	private PageVPNInfo buildInfo(String[] lines,int i)
	{
	    try{
    	    PageVPNInfo v = new PageVPNInfo();
            String[] columns = lines[i].split(",");
            if(columns.length<4)
                return null;
            columns[0] = columns[0] != null?columns[0].trim():"";
            String str = columns[0];
            if(columns[0].startsWith("pvp"))
            {
                String ip = columns[0].replace("pvp", "");
                if(ip.length()>=4)
                {
                    ip = "192.168."+ip.substring(0,2)+"."+ip.substring(2,ip.length());
                }
                else
                    ip = "192.168.88."+ip;
                SiteInfo s = Dog.getInstance().getSiteInfoByIP(ip);
                if(s != null)
                {
                    str += "("+s.getDescription()+")";
                }
            }
            v.setIp(str);
            v.setPid(columns[1] != null?columns[1].trim():"");
            str = columns[2] != null?columns[2].trim():"";
            if(IPCity.containsKey(str))
                v.setPublicIP(str+"("+IPCity.get(str)+")");
            else
            {
                String city = getCity(str);
                if(StringUtils.isNotBlank(city))
                {
                    IPCity.put(str, city);
                    v.setPublicIP(str+"("+IPCity.get(str)+")");
                }
                else
                    v.setPublicIP(str);
            }
            v.setConnectTime(join(columns,4));
            return v;
	    }
	    catch(Exception ex)
	    {
	        System.out.println(ex.toString());
	    }
	    return null;
	}
	public String getPIDKicked(String pid){
		Connection login = RemoteCommandUtil.login("192.168.88.1",null, "root", "carel@suzhou2020CARELIOT");
		String output = RemoteCommandUtil.execute(login, "kill -TERM `cat /var/run/XXX.pid`".replace("XXX", pid));
		return output;
	}
	//@Test
	public void test2()
	{
	    INSTANCE.getCity("117.132.198.158");
	}
	public String getCity(String ip)
	{
	    if(IPCity.containsKey(ip))
	        return IPCity.get(ip);
	    String url = "http://ip.ws.126.net/ipquery?ip="+ip;
	    String result = HttpSendUtil.INSTANCE.sendGet(url, "gbk");
	    Pattern p1=Pattern.compile("\"(.*?)\"");
        Matcher m = p1.matcher(result);
        ArrayList<String> list = new ArrayList<String>();
        while (m.find()) {
            list.add(m.group().trim().replace("\"","")+" ");
        }
	    if(list != null && list.size()>=4)
	    {
	        return list.get(0)+list.get(1);
	    }
	    return "";
	}
	public String join(String[] str,int startIndex)
	{
	    if(str == null || str.length<startIndex)
	        return "";
	    String result = "";
	    for(int i=startIndex;i<str.length;i++)
	        result += str[i]+" ";
	    return result;
	}

}
