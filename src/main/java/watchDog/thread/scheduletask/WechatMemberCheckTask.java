
package watchDog.thread.scheduletask;

import java.util.List;
import java.util.Set;
import java.util.TimerTask;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import watchDog.bean.SiteInfo;
import watchDog.bean.constant.CommonConstants;
import watchDog.listener.Dog;
import watchDog.property.template.CommonMsgLogTemplate;
import watchDog.property.template.WechatDeptLogTemplate;
import watchDog.property.template.WechatMemberMsgTemplate;
import watchDog.service.DailyAlarmTestService;
import watchDog.util.DateTool;
import watchDog.wechat.bean.WechatDept;
import watchDog.wechat.bean.WechatMsg;
import watchDog.wechat.bean.WechatResult;
import watchDog.wechat.util.WechatUtil;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date May 9, 2020
 */
public class WechatMemberCheckTask extends TimerTask implements BaseTask {
	public static final WechatMemberCheckTask INSTANCE = new WechatMemberCheckTask();

	private static final Logger logger = Logger.getLogger(WechatMemberCheckTask.class);
	
	public static final long RUNNING_PERIOD = CommonConstants.ONE_DAY;
	
	private static final String SOLDIER_DEPT_SUFFIX = "_士兵";

	private static final String OFFICER_DEPT_SUFFIX = "_军官";
	
	private DailyAlarmTestService dailyAlarmTestService = DailyAlarmTestService.INSTANCE;

	private WechatMemberCheckTask() {
	}

	@Override
	public void run() {
		try {
			if(DateTool.isTodayWorkday()){
				logger.info(propertyConfig.getValue(CommonMsgLogTemplate.CL_START.getKey(), new Object[]{this.getClass().getName()}));
				
				// Daily alarm test
				List<SiteInfo> dailyAlarmNotConfiguredSites = dailyAlarmTestService.getDailyAlarmNotConfiguredSites();
				String dailyAlarmTestMsg = propertyConfig.getValue(WechatMemberMsgTemplate.DAT_NO_CONFIG_TITLE.getKey());
				boolean isAllConfiguerd = true;
				Set<String> msgList = new TreeSet<>();
				for (SiteInfo siteInfo : dailyAlarmNotConfiguredSites) {
						isAllConfiguerd = false;
						if(dailyAlarmTestMsg.getBytes("UTF-8").length > 2000){
							msgList.add(dailyAlarmTestMsg);
							dailyAlarmTestMsg = "";
						}
						dailyAlarmTestMsg += propertyConfig.getValue(WechatMemberMsgTemplate.DAT_NO_CONFIG.getKey(), new Object[]{siteInfo.getDescription(), siteInfo.getIp()});
				}
				
				// Send msg about daily alarms.
				if(isAllConfiguerd)
					dailyAlarmTestMsg += propertyConfig.getValue(WechatMemberMsgTemplate.DAT_ALL_CONFIG.getKey());
				
				if (!msgList.contains(dailyAlarmTestMsg)) {
					msgList.add(dailyAlarmTestMsg);
				}
				for (String msg : msgList) {
					sender.sendIMOfflineMsg(new WechatMsg.Builder(msg).build());
				}
				
				List<SiteInfo> infosWithTags = Dog.getInfosWithTags();
				// Daily update of the site description
				for (SiteInfo siteInfo : infosWithTags) {
					if(siteInfo.getCheckNetwork())
					updateSiteDescription(siteInfo);
				}
				
				logger.info(propertyConfig.getValue(CommonMsgLogTemplate.CL_END.getKey(), new Object[]{this.getClass().getName()}));
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	private boolean getDailyAlarmChecked(SiteInfo siteInfo){
		return true;
	}
	
	private void chechkMembers(){
		// Check members
		/*if (siteInfo.getChannel() != SenderWechat.CHANNEL_TEST
				&& siteInfo.getCheckNetwork() && WechatDeptCheckTask.INSTANCE.isSiteWithDeadline(siteInfo)) {
			List<WechatMsg> wechatMemberCheckedMsgList = CommunityConfig.getWechatMemberChecked(siteInfo);
			if (ObjectUtils.isCollectionNotEmpty(wechatMemberCheckedMsgList)) {
				for (WechatMsg wechatMsg : wechatMemberCheckedMsgList) {
					sender.sendIMOfflineMsg(wechatMsg);
				}
			}
		}*/
	}
	
	private void updateSiteDescription(SiteInfo siteInfo){
		// Update the description of the stores according to the RemotePRO
		String siteDesc = siteInfo.getDescription();
		String tagId = siteInfo.getTagId();
		String tagId2 = siteInfo.getTagId2();
		
		WechatDept soldierDept = WechatUtil.getWechatDeptById(tagId);
		WechatDept officerDept = WechatUtil.getWechatDeptById(tagId2);
		if(soldierDept == null || officerDept == null)
			return;
		
		String previousSoldierGroupDesc = soldierDept.getName();
		String previousOfficerGroupDesc = officerDept.getName();
		
		if(!previousSoldierGroupDesc.contains(siteDesc) || !previousOfficerGroupDesc.contains(siteDesc)){
			String currentSoldierGroupDesc = siteDesc + SOLDIER_DEPT_SUFFIX;
			soldierDept.setName(currentSoldierGroupDesc);
			WechatResult updateSoldierResult = WechatUtil.updateWechatDept(soldierDept);
			if(updateSoldierResult.isOK())
				logger.info(propertyConfig.getValue(WechatDeptLogTemplate.WD_SOLDIER_DESC_UPDATE.getDescripiton()
						, new Object[]{tagId, previousSoldierGroupDesc, currentSoldierGroupDesc}));
			else
				logger.info(propertyConfig.getValue(WechatDeptLogTemplate.WD_SOLDIER_UPDATE_FAILED.getDescripiton()
						, new Object[]{tagId, updateSoldierResult.getErrmsg()}));
			
			String currentOfficerGroupDesc = siteDesc + OFFICER_DEPT_SUFFIX;
			officerDept.setName(currentOfficerGroupDesc);
			WechatResult updateOfficerResult = WechatUtil.updateWechatDept(officerDept);
			if(updateOfficerResult.isOK())
				logger.info(propertyConfig.getValue(WechatDeptLogTemplate.WD_OFFICER_DESC_UPDATE.getDescripiton()
						, new Object[]{tagId, previousOfficerGroupDesc, currentOfficerGroupDesc}));
			else
				logger.info(propertyConfig.getValue(WechatDeptLogTemplate.WD_OFFICER_UPDATE_FAILED.getDescripiton()
						, new Object[]{tagId, updateOfficerResult.getErrmsg()}));
		}
	}

}
