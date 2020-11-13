package watchDog.wechat.util.sender;

import static org.junit.Assert.*;

import org.junit.Test;

import watchDog.wechat.bean.WechatMsg;

public class SenderWechatTest {

	@Test
	public void test() {
		Sender.getInstance().sendIM(new WechatMsg.Builder("test", "6",new String[] {"15366203524"}).type(Sender.WECHAT_MSG_TYPE_USER).build());
	}

}
