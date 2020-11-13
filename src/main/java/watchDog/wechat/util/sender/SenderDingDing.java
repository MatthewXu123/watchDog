package watchDog.wechat.util.sender;

import org.apache.log4j.Logger;

import watchDog.wechat.bean.WechatMsg;


public class SenderDingDing extends Sender {
	private static final Logger logger = Logger.getLogger(SenderDingDing.class);
	//private DingtalkChatbotClient client = new DingtalkChatbotClient();
	@Override
	public boolean sendIM(WechatMsg wechatMsg) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean sendIMToSales(WechatMsg wechatMsg) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean sendIMReport(WechatMsg wechatMsg) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean sendIMOfflineMsg(WechatMsg wechatMsg) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public String getDomainName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getURL(String url) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String isDebug() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getRootDepartmentId() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getCorpId() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getCorpSecret() {
		// TODO Auto-generated method stub
		return null;
	}
	
//	public boolean sendIM(int type,String agentId,String robot,String title,String msg)
//	{
//		boolean sendOK = true;
//		msg = msg.replace("\n", "\n\n&nbsp;");
//		try{
//			MarkdownMessage message = new MarkdownMessage();
//			message.setTitle(title);
//			message.add(msg);
//			SendResult result = client.send(robot, message);
//		}
//		catch(Exception ex)
//		{
//			sendOK = false;
//			logger.error("",ex);
//		}
//		return sendOK;
//	}
//	
//	public boolean sendIMReport(int type,String robot,String title,String msg)
//	{
//		boolean sendOK = true;
//		try{
//			FeedCardMessage message = new FeedCardMessage();
//
//	        List<FeedCardMessageItem> items = new ArrayList<FeedCardMessageItem>();
//	        FeedCardMessageItem item1 = new FeedCardMessageItem();
//	        item1.setTitle(title);
//	        item1.setPicURL(msg.split(";")[0]);
//	        item1.setMessageURL(msg.split(";")[1]);
//	        items.add(item1);
//
//	        message.setFeedItems(items);
//
//	        SendResult result = client.send(robot, message);
//		}
//		catch(Exception ex)
//		{
//			sendOK = false;
//			logger.error("",ex);
//		}
//		return sendOK;
//	}
//	
//	
//	FeedCardMessageItem item1 = new FeedCardMessageItem();
//	public static void main(String[] args)
//	{
//		Sender s = Sender.getInstance(Sender.CHANNEL_DINGDING);
//		s.sendIMReport("https://oapi.dingtalk.com/robot/send?access_token=d5202c7ceb3bf6fc29e1f61ea5b17e081c9c414a7f052b27ee6e0714a6a57e40","新报警", AlarmNotificationMain.EMOJI_SUN);
//	}

}
