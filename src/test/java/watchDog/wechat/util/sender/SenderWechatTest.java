package watchDog.wechat.util.sender;

import org.junit.Test;

import watchDog.wechat.bean.WechatMsg;

public class SenderWechatTest {

	@Test
	public void test() {
		Sender instance = Sender.getInstance();
		String msg1 = "这条消息给dept";
		WechatMsg wechatMsg1 = new WechatMsg.Builder(msg1,"6", new String[]{"741"}).build();
		instance.sendIM(wechatMsg1);
		
		String msg2 = "这条消息给dept、tag";
		WechatMsg wechatMsg2 = new WechatMsg.Builder(msg2,"6", new String[]{"741"}).tagIds(new String[]{"146"}).build();
		instance.sendIM(wechatMsg2);
		
		String msg3 = "这条消息给dept、tag、user";
		WechatMsg wechatMsg3 = new WechatMsg.Builder(msg3,"6", new String[]{"741"}).tagIds(new String[]{"146"}).userIds(new String[] {"matthewxu123"}).build();
		instance.sendIM(wechatMsg3);
		
		String msg4 = "这条消息给tag";
		WechatMsg wechatMsg4 = new WechatMsg.Builder(msg4,"6").tagIds(new String[]{"146"}).build();
		instance.sendIM(wechatMsg4);
		
		String msg5 = "这条消息给user";
		WechatMsg wechatMsg5 = new WechatMsg.Builder(msg5,"6").userIds(new String[] {"matthewxu123"}).build();
		instance.sendIM(wechatMsg5);
	}

}
