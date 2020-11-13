
package watchDog.thread.scheduletask;

import watchDog.listener.Dog;
import watchDog.property.template.PropertyConfig;
import watchDog.service.PropertyMgr;
import watchDog.thread.ConnectionThread;
import watchDog.thread.WechatApplicationThread;
import watchDog.wechat.util.sender.Sender;

/**
 * Description:
 * @author Matthew Xu
 * @date May 19, 2020
 */
public interface BaseTask {

	public static final int TASK_BEGIN_HOUR = 8;
	
	public static final int TASK_END_HOUR = 23;
	
	public static final ConnectionThread connectionThread = Dog.getInstance().getConnectionThread();
	
	public static final WechatApplicationThread wechatApplicationThread = Dog.getInstance().getWechatApplicationThread();
	
	public static final PropertyMgr propertyMgr = PropertyMgr.getInstance();
	
	public static final  Sender sender = Sender.getInstance();
	
	PropertyConfig propertyConfig = PropertyConfig.INSTANCE;
}
