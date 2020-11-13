package watchDog.thread;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import watchDog.bean.SiteInfo;
import watchDog.listener.Dog;
import watchDog.property.template.OfflineMsgLogTemplate;
import watchDog.service.FaxInfoService;
import watchDog.service.PingLogService;
import watchDog.service.VPNService;
import watchDog.util.DateTool;
import watchDog.util.HttpSendUtil;
import watchDog.util.MyThread;
import watchDog.util.Ping;
import watchDog.wechat.bean.WechatMsg;
import watchDog.wechat.util.sender.Sender;

public class ConnectionThread extends MyThread{
    private static final String CONNECTION_HEARTBEAN_REQUEST_URL = "http://dtu.carel-remote.com:8080/callingService/servlet/heartbeat?client=rv_alarm&encrypt=ENCRYPT_CONTENT&key=connection";
	private static final Logger logger = Logger.getLogger(ConnectionThread.class);
	public static final int SLEEP_MINUTES = 10;
	private static final long ONE_HOUR = 1000*60*60;
	private static final long CHECK_PERIOD = 1000*60*30;
	static Map<String,Date> lastOnlineMap = new HashMap<String,Date>();
	static Map<String,Date> lastOfflineMap = new HashMap<String,Date>();
	static Map<String,Date> lastSupervisorOfflineMap = new HashMap<>();
	boolean firstTime = true;
	public ConnectionThread()
	{
		super("ConnectionThread");
	}
	public void run()
	{
		int lastHour = -1;
		Dog.sleep(5*60*1000);
		while(!forceDead)
		{
			try{
				//add this because I find one time the connection thread not running
				logger.info("ConnectionThread start...");
				Date now = new Date();
				int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
				List<SiteInfo> networkCheckingList = dog.getNetworkCheckingList();
				List<String> offlineList= new ArrayList<>();
				for(SiteInfo site: networkCheckingList)
				{
					String ip = site.getIp();
					Integer channel = site.getChannel();
					String ktype = site.getKtype();
					try{
						boolean flag = false;
						Sender wx = Sender.getInstance(channel);
						boolean routerStatus = isIpSkip(ip) || Ping.ping(ip, 1, 2000);
						if(!isIpSkip(ip) && routerStatus)
						    PingLogService.save(ip);
						if(!routerStatus)
						{
						    logger.info("ping failed,"+ip+" "+site.getDescription());
						}
						boolean engineStatus = false;
						if(!routerStatus)
						    offlineList.add(ip);
					    //ok
					    if(routerStatus)
					    {
					    	lastOnlineMap.put(ip, now);
					    	engineStatus = Ping.workStatusCheck(ktype, ip);
					    }
					    //not ok, not online, not offline
					    else if(!lastOnlineMap.containsKey(ip) && !lastOfflineMap.containsKey(ip))
					    {
					    	lastOfflineMap.put(ip, now);
					    }
					    //offline 30 minutes
					    if(!routerStatus)
					    {
					        Date onlineTime = lastOnlineMap.get(ip);
					        Date offlineTime = lastOfflineMap.get(ip);
					        if(onlineTime != null)
					        {
					            
					            Calendar c = Calendar.getInstance();
                                c.setTime(onlineTime);
                                int hour_offline = c.get(Calendar.HOUR_OF_DAY);
                                if(DateTool.isSameDay(now, onlineTime) && hour == hour_offline)
                                {
                                    long diff = DateTool.diff(now, onlineTime);
                                    if(diff>=30*60*1000 && diff<40*60*1000)
                                    {
                                        String routerOfflineMsg = propertyConfig.getValue(OfflineMsgLogTemplate.OM_ROUTER.getKey(), new Object[]{site.getDescription(), ip,site.getManDescription(), diff / ONE_HOUR});
                                        wx.sendIMOfflineMsg(new WechatMsg.Builder(routerOfflineMsg).build());
                                        wx.sendIM(new WechatMsg.Builder(routerOfflineMsg, site.getAgentId(), new String[]{site.getTagId(),site.getTagId2()}).build());
                                    }
                                }
					        }
					        else
					        {
    					        Calendar c = Calendar.getInstance();
    					        c.setTime(offlineTime);
    					        int hour_offline = c.get(Calendar.HOUR_OF_DAY);
    					        if(DateTool.isSameDay(now, offlineTime) && hour == hour_offline)
    					        {
    					            long diff = DateTool.diff(now, offlineTime);
    					            if(diff>=30*60*1000 && diff<40*60*1000)
    					            {
    					                String routerOfflineMsg = propertyConfig.getValue(OfflineMsgLogTemplate.OM_ROUTER.getKey(), new Object[]{site.getDescription(), ip,site.getManDescription(), diff / ONE_HOUR});
                                        wx.sendIMOfflineMsg(new WechatMsg.Builder(routerOfflineMsg).build());
                                        wx.sendIM(new WechatMsg.Builder(routerOfflineMsg, site.getAgentId(), new String[]{site.getTagId(),site.getTagId2()}).build());
    					            }
    					        }
					        }
					    }
					    if(hour != lastHour)
					    {
					    	Date lastOnlineDate = lastOnlineMap.get(ip);
					    	Date lastOfflineDate = lastOfflineMap.get(ip);
					    	
					    	if(!routerStatus)
					    	{
					    		logger.info(ip+" 上次在线时间:"+DateTool.format(lastOnlineDate)+"   上次离线时间:"+DateTool.format(lastOfflineDate));
					    	}
					    	//was online before
					    	if(lastOnlineDate != null)
					    	{
					    		long ms = DateTool.diff(now,lastOnlineDate);
					    		if(ms > CHECK_PERIOD){
					    			// send the msg to offline group
					    			String routerOfflineMsg = propertyConfig.getValue(OfflineMsgLogTemplate.OM_ROUTER.getKey(), new Object[]{site.getDescription(), ip,site.getManDescription(), ms / ONE_HOUR});
					    			wx.sendIMOfflineMsg(new WechatMsg.Builder(routerOfflineMsg).build());
					    			wx.sendIM(new WechatMsg.Builder(routerOfflineMsg, site.getAgentId(), new String[]{site.getTagId(),site.getTagId2()}).build());
					    			flag = true;
					    		}
					    	}
					    	//wasn't online before
					    	else if(lastOfflineDate != null && DateTool.diff(now,lastOfflineDate) > CHECK_PERIOD)
					    	{
					    		long ms = DateTool.diff(now,lastOfflineDate);
				    			// send the msg to offline group
					    		wx.sendIMOfflineMsg(new WechatMsg.Builder(propertyConfig.getValue(OfflineMsgLogTemplate.OM_ROUTER.getKey(), new Object[]{site.getDescription(), ip,site.getManDescription(), ms / ONE_HOUR})).build());
					    		flag = true;
					    	}
					    	if(routerStatus && !engineStatus){
					    		Date lastOffline = lastSupervisorOfflineMap.get(ip);
					    		//check last offline time
					    		if(lastOffline != null)
					    		{
					    			String siteNoAccessMsg = propertyConfig.getValue(OfflineMsgLogTemplate.OM_SITE.getKey(), new Object[]{site.getDescription(), ip});
					    			wx.sendIMOfflineMsg(new WechatMsg.Builder(siteNoAccessMsg).build());
						    		if(DateTool.diff(now,lastOffline) > CHECK_PERIOD)
						    			wx.sendIM(new WechatMsg.Builder(siteNoAccessMsg, site.getAgentId(), new String[]{site.getTagId(),site.getTagId2()}).build());
								    flag = true;
					    		}
					    		//first time offline
					    		else
					    			lastSupervisorOfflineMap.put(ip, now);
					    	}
					    	else if(routerStatus && engineStatus)
					    	{
					    		lastSupervisorOfflineMap.remove(ip);
					    	}
					    }
					}
					catch(Exception ex)
					{
						logger.error("ip",ex);
					}
				}
				//update vpn client's IP city
				if(hour != lastHour)
				{
				    VPNService.INSTANCE.getVPNServerOutPut();
				}
				lastHour = hour;
				this.lastRunningTime = new Date();
				connectionHeartbeat(offlineList.size());
				logger.info("ConnectionThread finished...");
				Dog.sleep(SLEEP_MINUTES*60*1000);
			}catch(Exception ex)
			{
				logger.error("",ex);
				Dog.sleep(SLEEP_MINUTES*60*1000);
			}
		}
	}
	private void connectionHeartbeat(int num) throws UnsupportedEncodingException
    {
	    if(num<=30)
	    {
            String url = CONNECTION_HEARTBEAN_REQUEST_URL
                    .replace(FaxInfoService.ENCRYPT_CONTENT, URLEncoder.encode(FaxInfoService.getEncryptContent(), "utf-8"));
            String result = HttpSendUtil.INSTANCE.sendGet(new String(url.getBytes("iso-8859-1"), "utf-8"),
                    HttpSendUtil.CHAR_ENCODING_UTF8);
	    }
    }
	
	public static boolean isIpSkip(String ip){
		return ip.contains("192.168") && Integer.valueOf((ip.split("\\.")[2])) >= 100;
	}
}
