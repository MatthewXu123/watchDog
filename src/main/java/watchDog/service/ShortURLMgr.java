package watchDog.service;

import watchDog.bean.ShortURLBean;
import watchDog.bean.SiteInfo;
import watchDog.database.DatabaseMgr;
import watchDog.database.RecordSet;
import watchDog.listener.Dog;
import watchDog.util.MyHashMap;
import watchDog.util.ShortURL;
import watchDog.wechat.util.sender.Sender;

public class ShortURLMgr {
	public static final int DEVICE = 0;
	public static final int DEVICE_FAST = 1;
	public static final int ALARM_DETAIL = 30;
	public static final int SITE = 10;
	public static final int SITE_ALARM = 20;
	MyHashMap<String,String> deviceURL = new MyHashMap<String,String>(100);
	MyHashMap<String,String> siteURL = new MyHashMap<String,String>(100);
	public static ShortURLMgr instance = null;
	
	public static final String AND = "ricarelsuzhou";
	
	public static ShortURLMgr getInstance()
	{
		if(instance == null)
			instance = new ShortURLMgr();
		return instance;
	}
	
	private ShortURLMgr(){}
	
	public String getShortURL(int type,String longURL)
	{
		MyHashMap<String,String> map = null;
		if(type == DEVICE || type == DEVICE_FAST)
			map = deviceURL;
		else if(type == SITE || type == SITE_ALARM)
			map = siteURL;
		String shortURL = null;
		if(map != null)
			shortURL = map.get(longURL);
		if(shortURL != null)
			return shortURL;
		else
		{
			ShortURLBean ub = getFromDB(longURL);
			if(ub != null && map != null)
			{
				map.put(ub.getLongURL(), ub.getUrl());
				return ub.getUrl();
			}
			else
			{
				String encoded = ShortURL.getURLEncoderString(longURL);
				shortURL = ShortURL.shorten(encoded);
				if(type != ALARM_DETAIL)
					insert2DB(longURL,shortURL);
				if(map != null)
					map.put(longURL,shortURL);
				return shortURL;
			}
		}
	}
	
	
	private ShortURLBean getFromDB(String longURL)
	{
		String sql = "select * from private_shortURL where long_url=?";
		Object[] params = new Object[]{longURL};
		try
		{
			RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql,params);
			if(rs != null && rs.size()>0)
			{
				return new ShortURLBean(rs.get(0));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	private void insert2DB(String longURL,String url)
	{
		String sql = "insert into private_shortURL values(?,?)";
		Object[] params = new Object[]{longURL,url};
		try
		{
			DatabaseMgr.getInstance().executeUpdate(sql,params);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	//page,without shortURL
	public String getSiteURL(String ip,int id)
	{
		String domainName = Sender.getInstance(Sender.CHANNEL_WECHAT).getDomainName();
		String  type = "";
		SiteInfo site = Dog.getInstance().getSiteInfoByIdSite(id);
		if(site != null)
		{
			if("PVP".equals(site.getKtype()))
				type = "PlantVisorPRO";
			else if("boss".equals(site.getKtype()))
				type = "boss";
		}
		return "https://"+domainName+"/RVRP@"+new Integer(ip.hashCode()).toString()+new Integer(domainName.hashCode()).toString()+id+"@/"+type+"/";
	}
//	public String getSiteURLShort(String ip,int id)
//	{
//		String url = getSiteURL(ip,id)+"?";
//		return ShortURLMgr.getInstance().getShortURL(ShortURLMgr.SITE,url);
//	}
//	public String getSiteAlarmURL(int idsite)
//	{
//		String domainName = Sender.getInstance(Sender.CHANNEL_WECHAT).getDomainName();
//		String url = "https://"+domainName+"/watchDog/servlet/auth?path=alarm.jsp?idsite="+idsite;
//		url = Sender.getInstance().getURL(url);
//		return url;
//	}
//	public String getAlarmDetailURLShort(int idsite,int idvariable)
//	{
//		String domainName = Sender.getInstance(Sender.CHANNEL_WECHAT).getDomainName(); 
//		String url = "https://"+domainName+"/watchDog/alarmStatistics.jsp?idsite="+idsite+"&idvariable="+idvariable;
//		return ShortURLMgr.getInstance().getShortURL(ShortURLMgr.ALARM_DETAIL,url);
//	}
	//page, without short URL
	public String getDeviceURL(String ip,int idsite,int iddevice)
	{
		String url = getSiteURL(ip,idsite)+"?folder=dtlview&bo=BDtlView&curTab=tab1name&iddev="+iddevice;
		return url;
	}
//	public String getDeviceURLShort(String ip,int idsite,int iddevice)
//	{
//		String url = getSiteURL(ip,idsite)+"?folder=dtlview&bo=BDtlView&curTab=tab1name&iddev="+iddevice;
//		return ShortURLMgr.getInstance().getShortURL(ShortURLMgr.DEVICE,url);
//	}
//	public String getDeviceFastURLShort(String ip,String devCode)
//	{
//		String domainName = Sender.getInstance(Sender.CHANNEL_WECHAT).getDomainName(); 
//		String url = "https://"+domainName+"/watchDog/device.jsp?ip="+ip+"&devCode="+devCode;
//		return ShortURLMgr.getInstance().getShortURL(ShortURLMgr.DEVICE_FAST,url);
//	}
	public String getReportMgr(String type,Integer idsite)
	{
		String domainName = Sender.getInstance(Sender.CHANNEL_WECHAT).getDomainName(); 
		String url = "https://"+domainName+"/watchDog/servlet/auth?path=reportManager.jsp?type="+type+AND+"idsite="+idsite;
		url = Sender.getInstance().getURL(url);
		return url;
	}
	public String getReportHQ(String type)
	{
		String domainName = Sender.getInstance(Sender.CHANNEL_WECHAT).getDomainName(); 
		String url = "https://"+domainName+"/watchDog/servlet/auth?path=reportHQ.jsp?type="+type;
		url = Sender.getInstance().getURL(url);
		return url;
	}
	public String getReportImg(Integer channel)
	{
		String domainName = Sender.getInstance(Sender.CHANNEL_WECHAT).getDomainName(); 
		String png="reportMW.png";
		if(channel==2)
			png="reportMD.png";
		String url = "https://"+domainName+"/watchDog/img/"+png;
		return url;
	}
	
}
