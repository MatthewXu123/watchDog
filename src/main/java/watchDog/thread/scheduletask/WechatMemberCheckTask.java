
package watchDog.thread.scheduletask;

import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import watchDog.bean.SiteInfo;
import watchDog.listener.Dog;
import watchDog.property.template.CommonMsgLogTemplate;
import watchDog.util.DateTool;
import watchDog.util.ObjectUtils;
import watchDog.wechat.bean.WechatMsg;
import watchDog.wechat.config.CommunityConfig;
import watchDog.wechat.util.sender.SenderWechat;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date May 9, 2020
 */
public class WechatMemberCheckTask extends TimerTask implements BaseTask {
	public static final WechatMemberCheckTask INSTANCE = new WechatMemberCheckTask();

	private static final Logger logger = Logger.getLogger(WechatMemberCheckTask.class);

	private WechatMemberCheckTask() {
	}

	@Override
	public void run() {
		try {
			if(DateTool.isTodayWorkday()){
				logger.info(propertyConfig.getValue(CommonMsgLogTemplate.CL_START.getKey(), new Object[]{this.getClass().getName()}));
				List<SiteInfo> infosWithTags = Dog.getInfosWithTags();
				for (SiteInfo siteInfo : infosWithTags) {
					if (siteInfo.getChannel() != SenderWechat.CHANNEL_TEST
							&& siteInfo.getCheckNetwork() && WechatDeptCheckTask.INSTANCE.isSiteWithDeadline(siteInfo)) {
						List<WechatMsg> wechatMemberCheckedMsgList = CommunityConfig.getWechatMemberChecked(siteInfo);
						if (ObjectUtils.isCollectionNotEmpty(wechatMemberCheckedMsgList)) {
							for (WechatMsg wechatMsg : wechatMemberCheckedMsgList) {
								sender.sendIMOfflineMsg(wechatMsg);
							}
						}
					}
				}
				logger.info(propertyConfig.getValue(CommonMsgLogTemplate.CL_END.getKey(), new Object[]{this.getClass().getName()}));
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

}
