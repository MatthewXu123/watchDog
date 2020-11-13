package watchDog.wechat.util.sender;

import org.apache.log4j.Logger;

import watchDog.wechat.bean.WechatMsg;

public abstract class Sender {
	private static final Logger logger = Logger.getLogger(Sender.class);
	private static Sender me = null;
	
	public static final int WECHAT_MSG_TYPE_USER = 0;
	public static final int WECHAT_MSG_TYPE_TAG = 1;
	public static final int WECHAT_MSG_TYPE_DEPT = 2; 
	public static final int CHANNEL_WECHAT = 1;
	public static final int CHANNEL_DINGDING = 2;
	public static final int CHANNEL_TEST = -1;
	
	public static Sender getInstance(int channel)
	{
		if(me == null)
		{
			if(channel == CHANNEL_DINGDING)
				me = new SenderDingDing();
			else
				me = new SenderWechat();
		}
		return me;
	}
	
	public static Sender getInstance()
	{
		return getInstance(CHANNEL_WECHAT);
	}
	
	protected Sender(){}
	
	public abstract boolean sendIM(WechatMsg wechatMsg);
	
	public abstract boolean sendIMToSales(WechatMsg wechatMsg);
	
	public abstract boolean sendIMReport(WechatMsg wechatMsg);
	
	public abstract boolean sendIMOfflineMsg(WechatMsg wechatMsg);
	
	public abstract String getDomainName();

	public abstract String getURL(String url);
	
	public abstract String isDebug();

	public abstract String getRootDepartmentId();

	public abstract String getCorpId();

	public abstract String getCorpSecret();
	
}
