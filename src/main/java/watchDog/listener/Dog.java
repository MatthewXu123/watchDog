package watchDog.listener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.Logger;

import me.chanjar.weixin.common.util.StringUtils;
import watchDog.bean.SiteInfo;
import watchDog.dao.SiteInfoDAO;
import watchDog.service.ScheduledTask;
import watchDog.service.SimpleCallingService;
import watchDog.thread.AlarmNotificationThread;
import watchDog.thread.ConnectionThread;
import watchDog.thread.ThreadChecking;
import watchDog.thread.WechatApplicationThread;
import watchDog.wechat.service.WechatService;
import watchDog.wechat.service.WechatService.WxXmlCpInMemoryConfigStorage;
import watchDog.wechat.util.sender.Sender;

@WebListener
public class Dog implements ServletContextListener{
	private static final Logger logger = Logger.getLogger(Dog.class);
	private static Dog me = null;
	private static List<SiteInfo> infos = null;
	private static List<SiteInfo> infosWithTags = null;
	private static List<SiteInfo> infosWithoutTags = null;
	private static List<SiteInfo> networkCheckinList;
	//<ident,SiteInfo>
	Map<String,SiteInfo> siteInfoMap = null;
	AlarmNotificationThread alarm = null;
	ConnectionThread connection = null;
	WechatApplicationThread wechatApplicationThread = null;
	int lastHour = -1;
	
	public Dog(){}
	
	public void contextInitialized(ServletContextEvent arg0)
	{
		logger.info("Dog: wong wong wong");
		Sender.getInstance(Sender.CHANNEL_WECHAT);
		Dog dog = getInstance();
		dog.loadFromDB();
		
		WxXmlCpInMemoryConfigStorage configStorage = WechatService.getInstance().getStorage();
		if(StringUtils.isBlank(configStorage.getDebug()))
		{
    		dog.alarm = dog.getAlarmThread();
    		dog.connection = dog.getConnectionThread();
    		dog.wechatApplicationThread = dog.getWechatApplicationThread();
    		
    		ThreadChecking c1 = new ThreadChecking(1);
    		c1.start();
    		ThreadChecking c2 = new ThreadChecking(2);
    		c2.start();
    		
    		while(true)
    		{
    			if(dog.wechatApplicationThread.isTagCrawled())
    				break;
    			Dog.sleep(1000);
    		}
    		
    		new ScheduledTask();
    		SimpleCallingService.getInstance().start();
		}    
	}
	public AlarmNotificationThread getAlarmThread()
	{
		if(alarm == null)
		{
			alarm = new AlarmNotificationThread();
			alarm.start();
		}
		return alarm;
	}
	public ConnectionThread getConnectionThread()
	{
		if(connection == null)
		{
			connection = new ConnectionThread();
			connection.start();
		}
		return connection;
	}
	public WechatApplicationThread getWechatApplicationThread()
	{
		if(wechatApplicationThread == null)
		{
			wechatApplicationThread = new WechatApplicationThread();
			wechatApplicationThread.start();
		}
		return wechatApplicationThread;
	}
	public void renewAlarmThread()
	{
		logger.error("AlarmThread dead, renew");
		if(alarm != null)
			alarm.forceDead();
		alarm = new AlarmNotificationThread();
		alarm.start();
	}
	public void renewConnectionThread()
	{
		logger.error("ConnectionThread dead, renew");
		if(alarm != null)
		if(connection != null)
			connection.forceDead();
		connection = new ConnectionThread();
		connection.start();
	}
	public void renewWechatAppWlicationThread()
	{
		logger.error("WechatApplicationThread dead, renew");
		if(alarm != null)
		if(wechatApplicationThread != null)
			wechatApplicationThread.forceDead();
		wechatApplicationThread = new WechatApplicationThread();
		wechatApplicationThread.start();
	}
//	private void resetMsgCounter(String ip)
//	{
//		msgCounter.put(ip, null);
//	}
//	private void addMsgCounter(String ip)
//	{
//		int counter = 0;
//		Integer t = msgCounter.get(ip);
//		if(t != null)
//			counter = t;
//		counter++;
//		msgCounter.put(ip, counter);
//	}
//	public void sendIM(String ip,String msg)
//	{
//		Calendar c = Calendar.getInstance();
//		int week = c.get(Calendar.DAY_OF_WEEK);
//		int hour = c.get(Calendar.HOUR_OF_DAY);
//		if(week == Calendar.SATURDAY || week == Calendar.SUNDAY || hour<=8 || hour>=17)
//			return;
//		Sender wx = Sender.getInstance();
//    	wx.sendIM(msg);
//	}
	
	public static Dog getInstance()
	{
		if(me == null)
		{
			me = new Dog();
		}
		return me;
	}
	
	public void loadFromDB()
	{
		logger.info("loadFromDB start...");
		infos = SiteInfoDAO.INSTANCE.getList(null);
		infosWithTags = SiteInfoDAO.INSTANCE.getList(true);
		infosWithoutTags = SiteInfoDAO.INSTANCE.getList(false);
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		if((siteInfoMap != null && infos != null && siteInfoMap.size()<infos.size()) || hour != lastHour)
		{
			networkCheckinList = new ArrayList<>();
			siteInfoMap = new ConcurrentHashMap<String,SiteInfo>();
			for(SiteInfo s:infos)
			{
				siteInfoMap.put(s.getIdent(), s);
				if(s.getCheckNetwork())
					networkCheckinList.add(s);
			}
		}
		this.lastHour = hour;
		logger.info("loadFromDB finished...");
	}
	
	public static void sleep(int ms)
	{
		try {
			Thread.sleep(ms);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		logger.info("destroy watch dog");
	}
	public List<SiteInfo> getNetworkCheckingList()
	{
		return networkCheckinList;
	}
	public SiteInfo getSiteInfo(String ident)
	{
		if(ident == null)
			return null;
		return siteInfoMap.get(ident);
	}
	public String getIdents4AlarmChecking()
	{
		boolean first = true;
		String result = "";
		Iterator it = siteInfoMap.entrySet().iterator();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);
		while(it.hasNext())
		{
			Map.Entry<String,SiteInfo> entry=(Map.Entry<String,SiteInfo>)it.next();
			SiteInfo s = entry.getValue();
			if((s.getDeadline() != null && (c.getTime()).after(s.getDeadline())))
				continue;
			if(!StringUtils.isBlank(s.getAgentId()) && !StringUtils.isBlank(s.getTagId()))
			{
				if(first)
				{
					result += "'"+s.getIdent()+"'";
					first = false;
				}
				else
					result += ","+"'"+s.getIdent()+"'";
			}
		}
		return result;
	}
	//kevin, to do,  add a map by idsite
	public SiteInfo getSiteInfoByIdSite(int idsite)
	{
		Iterator it = siteInfoMap.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<String,SiteInfo> entry=(Map.Entry<String,SiteInfo>)it.next();
			SiteInfo s = entry.getValue();
			if(idsite == s.getSupervisorId())
			{
				return s;
			}
		}
		return null;
	}
	//kevin, to do,  add a map by idsite
    public SiteInfo getSiteInfoByIP(String ip)
    {
        Iterator it = siteInfoMap.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry<String,SiteInfo> entry=(Map.Entry<String,SiteInfo>)it.next();
            SiteInfo s = entry.getValue();
            if(ip.equals(s.getIp()))
            {
                return s;
            }
        }
        return null;
    }
	public String getSiteName(int idsite)
	{
		Iterator it = siteInfoMap.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<String,SiteInfo> entry=(Map.Entry<String,SiteInfo>)it.next();
			SiteInfo s = entry.getValue();
			if(s.getSupervisorId().intValue() == idsite)
			{
				return s.getDescription();
			}
		}
		return "";
	}
	public int canUserAccessSite(String user,int idsite)
	{
		return this.wechatApplicationThread.canUserAccessSite(idsite,user);
	}
	/*
	 * get keySet().iterator()
	 */
	public Iterator getAllSites()
	{
		if(siteInfoMap != null)
			return siteInfoMap.entrySet().iterator();
		return null;
	}
	
	public ConnectionThread getConnection() {
		return connection;
	}
	public void setConnection(ConnectionThread connection) {
		this.connection = connection;
	}
	/**
	 * @return the infos
	 */
	public List<SiteInfo> getInfos() {
		return infos;
	}

	/**
	 * @return the infosWithTags
	 */
	public static List<SiteInfo> getInfosWithTags() {
		return infosWithTags;
	}

	/**
	 * @param infosWithTags the infosWithTags to set
	 */
	public static void setInfosWithTags(List<SiteInfo> infosWithTags) {
		Dog.infosWithTags = infosWithTags;
	}

	/**
	 * @return the infosWithoutTags
	 */
	public static List<SiteInfo> getInfosWithoutTags() {
		return infosWithoutTags;
	}

	/**
	 * @param infosWithoutTags the infosWithoutTags to set
	 */
	public static void setInfosWithoutTags(List<SiteInfo> infosWithoutTags) {
		Dog.infosWithoutTags = infosWithoutTags;
	}

	/**
	 * @return the networkCheckinList
	 */
	public static List<SiteInfo> getNetworkCheckinList() {
		return networkCheckinList;
	}

	/**
	 * @param networkCheckinList the networkCheckinList to set
	 */
	public static void setNetworkCheckinList(List<SiteInfo> networkCheckinList) {
		Dog.networkCheckinList = networkCheckinList;
	}
	
}
