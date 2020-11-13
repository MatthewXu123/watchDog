package watchDog.sender;

import org.junit.Test;

import watchDog.wechat.bean.WechatMsg;
import watchDog.wechat.util.sender.Sender;

public class SenderWechatTest {

	@Test
	public void test() {
		Sender.getInstance().sendIMOfflineMsg(new WechatMsg.Builder("1111").title("测试标题")
				.build());
	}

}
