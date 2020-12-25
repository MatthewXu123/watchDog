
package watchDog.thread.scheduletask;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import watchDog.bean.SiteInfo;
import watchDog.config.json.CommunityConfig;
import watchDog.dao.SiteInfoDAO;
import watchDog.listener.Dog;
import watchDog.property.template.CommonMsgLogTemplate;
import watchDog.property.template.WechatDeptLogTemplate;
import watchDog.util.DateTool;
import watchDog.util.ObjectUtils;
import watchDog.util.RegexUtil;
import watchDog.wechat.bean.WechatDept;
import watchDog.wechat.bean.WechatUser;
import watchDog.wechat.bean.WechatResult;
import watchDog.wechat.util.WechatUtil;
import watchDog.wechat.util.sender.Sender;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date May 13, 2020
 */
public class WechatDeptCheckTask extends TimerTask implements BaseTask{

    private static final String TO_REGEX = "^TO:\\d{4}-\\d{2}-\\d{2}$";
	private static final String TO = "TO:";
    private static final Logger logger = Logger.getLogger(WechatDeptCheckTask.class);

	public static final WechatDeptCheckTask INSTANCE = new WechatDeptCheckTask();

	private static final String SOLDIER_DEPT_SUFFIX = "_士兵";

	private static final String OFFICER_DEPT_SUFFIX = "_军官";

	private SiteInfoDAO siteInfoDAO = SiteInfoDAO.INSTANCE;

	private WechatDeptCheckTask(){}

	@Override
	public void run() {
		try {
			logger.info(propertyConfig.getValue(CommonMsgLogTemplate.CL_START.getKey(), new Object[]{this.getClass().getName()}));
			List<SiteInfo> infos = Dog.getInfosWithoutTags();
			if (ObjectUtils.isCollectionNotEmpty(infos)) {
				Iterator<SiteInfo> iterator = infos.iterator();
				while (iterator.hasNext()) {
					SiteInfo siteInfo = iterator.next();
					if (siteInfo.getChannel() != Sender.CHANNEL_TEST && isSiteWithDeadline(siteInfo)) {
						String tagId = siteInfo.getTagId();
						String tagId2 = siteInfo.getTagId2();
						Date deadLine = getDeadLine(siteInfo);
						// If the tagId is null or the tagId is not null but the
						// dept doesn't exist, we will create the dept.
						if (StringUtils.isBlank(tagId) && StringUtils.isBlank(tagId2)) {
							Map<String, String> createDept = createDept(siteInfo);
							tagId = createDept.get(SOLDIER_DEPT_SUFFIX);
							tagId2 = createDept.get(OFFICER_DEPT_SUFFIX);
							if (!siteInfoDAO.isSiteExist(siteInfo.getSupervisorId()))
								siteInfoDAO.saveOne(siteInfo.getSupervisorId(), deadLine, false, "6", 1, tagId, tagId2,
										null, "created auto");
							else
								siteInfoDAO.updateOne(siteInfo.getSupervisorId(), deadLine, siteInfo.getCheckNetwork(),
										"6", 1, tagId, tagId2, null, "updated auto");
						}

						// add the necessary members from the json
						// NOTICE: Limited by the api, we can't create the member with over 20 departments.
						// Please refer to this 'https://work.weixin.qq.com/api/doc/90000/90135/90197'.
//						List<String> necessarySoldierGroup = CommunityConfig.getNecessarySoldierGroup(siteInfo.getCusNode());
//						List<String> necessaryOfficerGroup = CommunityConfig.getNecessaryOfficerGroup(siteInfo.getManNode());
//						if (ObjectUtils.isCollectionNotEmpty(necessarySoldierGroup))
//							addMembersToDept(tagId, necessarySoldierGroup);
//						if (ObjectUtils.isCollectionNotEmpty(necessaryOfficerGroup))
//							addMembersToDept(tagId2, necessaryOfficerGroup);

					}
				}
			}
			logger.info(propertyConfig.getValue(CommonMsgLogTemplate.CL_END.getKey(), new Object[]{this.getClass().getName()}));
		} catch (Exception e) {
			logger.error("", e);
		}

	}

	/**
	 * 
	 * Description:
	 * 
	 * @param siteInfo
	 * @return
	 * @author Matthew Xu
	 * @date May 14, 2020
	 */
	protected boolean isSiteWithDeadline(SiteInfo siteInfo) {
		return getDeadLine(siteInfo) != null ? true : false;
	}

	/**
	 * 
	 * Description:
	 * 
	 * @param siteInfo
	 * @return
	 * @author Matthew Xu
	 * @date May 14, 2020
	 */
	private Date getDeadLine(SiteInfo siteInfo) {
		String[] supervisorTags = siteInfo.getSupervisorTags();
		if (siteInfo.getDeadline() != null)
			return siteInfo.getDeadline();
		if (ObjectUtils.isArrayNotEmpty(supervisorTags)) {
			for (String supervisorTag : supervisorTags) {
				if (RegexUtil.match(TO_REGEX,supervisorTag.toUpperCase()))
					return DateTool.parse(supervisorTag.replace(TO, ""), DateTool.DEFAULT_DATE_FORMAT);
				else if (RegexUtil.matchDateString(supervisorTag))
                    return DateTool.parse(supervisorTag, DateTool.DEFAULT_DATE_FORMAT);
			}
		}
		return null;
	}

	/**
	 * 
	 * Description:
	 * 
	 * @param siteInfo
	 * @author Matthew Xu
	 * @date May 14, 2020
	 */
	private Map<String, String> createDept(SiteInfo siteInfo) {
		Map<String, String> map = new HashMap<>();
		String parentId = CommunityConfig.getDeptIdByCode(siteInfo.getCusNode());
		// Example: create the department 'xxx' here.
		String baseId = createDept2(siteInfo, parentId, null);
		// Example: create the department 'xxx_士兵' here.
		String tagId = createDept2(siteInfo, baseId, true);
		// Example: create the department 'xxx_军官' here.
		String tagId2 = createDept2(siteInfo, baseId, false);
		map.put(SOLDIER_DEPT_SUFFIX, tagId);
		map.put(OFFICER_DEPT_SUFFIX, tagId2);
		return map;
	}

	/**
	 * 
	 * Description:
	 * 
	 * @param siteInfo
	 * @param parentId
	 * @param isSoldierGroup
	 * @author Matthew Xu
	 * @date May 14, 2020
	 */
	private String createDept2(SiteInfo siteInfo, String parentId, Boolean isSoldierGroup) {
		String tagId = "";
		if (isSoldierGroup != null)
			tagId = isSoldierGroup ? siteInfo.getTagId() : siteInfo.getTagId2();
		String siteDesc = siteInfo.getDescription();
		if (isSoldierGroup != null)
			siteDesc += isSoldierGroup ? SOLDIER_DEPT_SUFFIX : OFFICER_DEPT_SUFFIX;
		String createdDeptId = "";

		String deptId = WechatUtil.isDeptExistByName(parentId, siteDesc);
		if(StringUtils.isNotBlank(deptId)){
			logger.info("该门店已存在。" + deptId + "," + siteDesc);
			return deptId;
		}else{
			WechatDept wechatDept = new WechatDept();
			wechatDept.setName(siteDesc);
			if (isSoldierGroup != null)
				wechatDept.setOrder(isSoldierGroup ? 1 : 0);
			wechatDept.setParentid(parentId);
			if (StringUtils.isNotBlank(tagId))
				wechatDept.setId(tagId);

			return WechatUtil.createDept(wechatDept);
		}
		
		
	}
	
	/**
	 * 
	 * Description:
	 * @param deptId
	 * @param memberGroup
	 * @author Matthew Xu
	 * @date May 15, 2020
	 */
	private void addMembersToDept(String deptId, List<String> memberGroup){
		for (String userId : memberGroup) {
			WechatUser wechatMember = WechatUtil.getMemberByUserId(userId);
			String[] deptIds = wechatMember.getDepartment();
			boolean flag = ObjectUtils.isArrayNotEmpty(deptIds) ? true : false;
			int length = 1;
			if(flag)
				length = deptIds.length + 1;
			String[] newDeptIds =  new String[length];
			int i = 0;
			if(flag){
				for (String id : deptIds) {
					newDeptIds[i++] = id;
				}
			}
			newDeptIds[i] = deptId;
			wechatMember.setDepartment(newDeptIds);
			WechatUtil.updateMember(wechatMember);
		}
	}
	
}
