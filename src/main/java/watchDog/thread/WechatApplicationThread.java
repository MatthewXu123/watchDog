package watchDog.thread;

import static watchDog.util.ObjectUtils.isCollectionEmpty;
import static watchDog.util.ObjectUtils.isCollectionNotEmpty;
import static watchDog.util.ObjectUtils.isMapNotEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import watchDog.bean.SiteInfo;
import watchDog.config.json.BaseJSONConfig;
import watchDog.listener.Dog;
import watchDog.property.template.WechatMemberMsgTemplate;
import watchDog.util.MyThread;
import watchDog.util.ObjectUtils;
import watchDog.util.SortList;
import watchDog.wechat.bean.WechatDept;
import watchDog.wechat.bean.WechatMsg;
import watchDog.wechat.bean.WechatTag;
import watchDog.wechat.bean.WechatUser;
import watchDog.wechat.service.WechatService;
import watchDog.wechat.service.WechatService.WxXmlCpInMemoryConfigStorage;
import watchDog.wechat.util.WechatUtil;
import watchDog.wechat.util.sender.Sender;;

public class WechatApplicationThread extends MyThread {
	private static final Logger logger = Logger.getLogger(WechatApplicationThread.class);

	private static final String GNNERAL_DEPT_ID = "109";

	private static final String SKIP_SUFFIX = "_i";

	private static final String SOLIDER_SUFFIX = "_c";
	
	public static final String SIMPLE_CALLING_SUFFIX_SODIER = "_cs";
	public static final String SIMPLE_CALLING_SUFFIX_OFFICER = "_co";
	public static final String SIMPLE_CALLING_SUFFIX_GENERAL = "_cg";
	public static final String[] SIMPLE_CALLING_SUFFIX_1 = {SIMPLE_CALLING_SUFFIX_SODIER};
	public static final String[] SIMPLE_CALLING_SUFFIX_2 = {SIMPLE_CALLING_SUFFIX_SODIER,SIMPLE_CALLING_SUFFIX_OFFICER};
	public static final String[] SIMPLE_CALLING_SUFFIX_3 = {SIMPLE_CALLING_SUFFIX_SODIER,SIMPLE_CALLING_SUFFIX_OFFICER,SIMPLE_CALLING_SUFFIX_GENERAL};

	private List<WechatUser> allMembers = new ArrayList<>();
	// <deptId, wechatMember>
	private Map<String, List<WechatUser>> deptIdWechatMemberMap = new HashMap<>();
	// <siteInfo, wechatMember>
	private Map<SiteInfo, List<WechatUser>> siteWechatMemberMap = new ConcurrentHashMap<>();
	private Map<SiteInfo, List<WechatUser>> siteWechatMemberMap4Export = new ConcurrentHashMap<>();
	// <siteInfo, wechatMember>
	private Map<SiteInfo, List<WechatUser>> oldSiteWechatMemberMap = new ConcurrentHashMap<>();
	// <userId, WechatMember> all wechatMember
	private Map<String, WechatUser> weChatMemberMap = new HashMap<>();
	// all wechatMember in the general group
	private List<WechatUser> generalWechatMember = new ArrayList<>();
	// <userId, siteId>
	private Map<String, List<Integer>> allWechatMemberSiteMap = new HashMap<>();
	// <tagId, codeId>
	private Map<String, String> tagIdCodeMap = new HashMap<>();
	// <codeId, tagId>
	private Map<String, String> codetagIdMap = new HashMap<>();
	// <userId, siteId>
	private Map<String, List<Integer>> generalWechatMemberSiteMap = new HashMap<>();
	// <siteId, tagId>
	private Map<Integer, String[]> siteIdTagIdMap = new HashMap<>();
	// <WechatTag,userIdList>
	private Map<WechatTag, List<WechatUser>> tagIdUserListMap = new HashMap<>();
	// <TagId, siteList>
	private Map<String, List<SiteInfo>> tagIdSiteListMap = new HashMap<>();
	
	private Sender sender = Sender.getInstance();
	
	public static final int SLEEP_MINUTES = 5;

	private boolean loaded = false;

	private int runTime = 1;

	public WechatApplicationThread() {
		super("WechatApplicationThread");
	}

	public void run() {
		boolean firstTime = true;
		while (!forceDead) {
			long start = System.currentTimeMillis();
			try {
				logger.info("WechatApplicationThread start...");
				allMembers = WechatUtil.getAllMembers();
				
				if(isCollectionEmpty(allMembers)){
					WechatUtil.updateAccessToken();
					allMembers = WechatUtil.getAllMembers();
				}
				
				if(isCollectionNotEmpty(allMembers)){
					
					deptIdWechatMemberMap = initDeptIdWechatMemberMap();
					logger.info("deptIdWechatMemberMap : " + deptIdWechatMemberMap.size());
					
					weChatMemberMap = initWeChatMemberMap();

					initSiteWechatMemberMap();

					allWechatMemberSiteMap = initAllWechatMemberSiteMap();

					generalWechatMemberSiteMap = initGeneralWechatMemberSiteMap();

					generalWechatMember = initGeneralWechatMember();
					
					//Tag
					initTagCodeMap();
					
					siteIdTagIdMap = initSiteIdTagIdMap();
					
					tagIdUserListMap = WechatUtil.getAllTagUserMap();
					
					initTagIdSiteListMap();
					
					welcomeNewWechatMember();
				}else {
					logger.info("deptIdWechatMemberMap : 0");
				}

				BaseJSONConfig.refreshConfig();

				if(firstTime){
					if(deptIdWechatMemberMap.size() > 0)
						loaded = true;
				}else {
					loaded = true;
				}
			} catch (Exception ex) {
				logger.error("",ex);
			} finally {
				// update every 1 hour
				firstTime = false;
				this.lastRunningTime = new Date();
				long end = System.currentTimeMillis();
				logger.info("WechatApplicationThread finished...");
				logger.info("WechatApplicationThread cost " + (end - start) / (1000 * 60) + " minutes at the "
						+ runTime++ + " time...");
				Dog.sleep(SLEEP_MINUTES * 60 * 1000);
			}
		}
	}

	public static final int SITE_ACCESS_OK = 0;
	public static final int SITE_ACCESS_NOSITE = 3;
	public static final int SITE_ACCESS_NOTMEMBER = 5;

	/*
	 * 0: tagMap is null, can access all 3: tag not exist 5: not a member of the
	 * tag 7: OK
	 */
	public int canUserAccessSite(int idsite, String userId) {
		if (deptIdWechatMemberMap == null)
			return SITE_ACCESS_OK;
		if (Dog.getInstance().getSiteInfoByIdSite(idsite) == null)
			return SITE_ACCESS_NOSITE;
		
		//Dept
		List<Integer> sites = allWechatMemberSiteMap.get(userId);
		if(ObjectUtils.isCollectionNotEmpty(sites) && sites.contains(idsite))
			return SITE_ACCESS_OK;
		
		//Tag
		WechatUser wechatUser = new WechatUser();
		wechatUser.setUserid(userId);
		String[] tagIds = siteIdTagIdMap.get(idsite);
		if(ObjectUtils.isArrayNotEmpty(tagIds)){
			for (String tagId : tagIds) {
				List<WechatUser> userList = tagIdUserListMap.get(tagId);
				if(ObjectUtils.isCollectionNotEmpty(userList) && userList.contains(wechatUser))
					return SITE_ACCESS_OK; 
			}
		}
			
		return SITE_ACCESS_NOTMEMBER;
	}

	/**
	 * 
	 * Description:
	 * 
	 * @return
	 * @author Matthew Xu
	 * @date May 13, 2020
	 */
	private Map<String, List<WechatUser>> initDeptIdWechatMemberMap() {
		Map<String, List<WechatUser>> deptIdWechatMemberMap = new HashMap<>();
		List<WechatDept> deptList = WechatUtil.getDeptListByDeptId(sender.getRootDepartmentId());
		List<WechatUser> userList = new ArrayList<>();
		userList.addAll(allMembers);
		
		for (WechatDept wechatDept : deptList) {
			String deptId = wechatDept.getId();
			List<WechatUser> list = new ArrayList<>();
			Iterator<WechatUser> iterator = userList.iterator();
			while(iterator.hasNext()){
				WechatUser wechatUser = iterator.next();
				String[] userDepts = wechatUser.getDepartment();
				if(ObjectUtils.isArrayNotEmpty(userDepts) && Arrays.asList(userDepts).contains(deptId))
					list.add(wechatUser);
				if(!ObjectUtils.isArrayNotEmpty(userDepts) ||(userDepts.length == 1 && userDepts[0].equals(deptId)))
					iterator.remove();
			}
			deptIdWechatMemberMap.put(deptId, list);
		}
		return deptIdWechatMemberMap;
	}

	/**
	 * 
	 * Description:
	 * 
	 * @return
	 * @author Matthew Xu
	 * @date May 13, 2020
	 */
	private Map<String, WechatUser> initWeChatMemberMap() {
		return WechatUtil.getMemberMap();
	}

	/**
	 * 
	 * Description:
	 * 
	 * @return
	 * @author Matthew Xu
	 * @date May 10, 2020
	 */
	private Map<String, List<Integer>> initAllWechatMemberSiteMap() {
		return getWechatMemberSiteMap(this.allMembers);
	}

	/**
	 * 
	 * Description:
	 * 
	 * @return
	 * @author Matthew Xu
	 * @date May 10, 2020
	 */
	private Map<String, List<Integer>> initGeneralWechatMemberSiteMap() {
		return getWechatMemberSiteMap(WechatUtil.getMemberByDeptId(GNNERAL_DEPT_ID, WechatUtil.FECTH_CHILD));
	}

	/**
	 * 
	 * Description:
	 * 
	 * @return
	 * @author Matthew Xu
	 * @date May 13, 2020
	 */
	private List<WechatUser> initGeneralWechatMember() {
		return WechatUtil.getMemberByDeptId(GNNERAL_DEPT_ID, WechatUtil.FECTH_CHILD);
	}

	/**
	 * Description:
	 * 
	 * @return
	 * @author Matthew Xu
	 * @date May 13, 2020
	 */
	private void initSiteWechatMemberMap() {
		List<SiteInfo> infos = Dog.getInfosWithTags();
		for (SiteInfo site : infos) {
			List<WechatUser> allWechatMemberList = new ArrayList<>();
			List<WechatUser> soldierWechatMemberList = deptIdWechatMemberMap.get(site.getTagId());
			List<WechatUser> officerWechatMemberList = deptIdWechatMemberMap.get(site.getTagId2());

			allWechatMemberList.addAll(soldierWechatMemberList);
			allWechatMemberList.removeAll(officerWechatMemberList);
			allWechatMemberList.addAll(officerWechatMemberList);
			siteWechatMemberMap4Export.put(site, allWechatMemberList);
			// In case that the wechat API can't get the soldier group or the officer group...
			if (runTime == 1 || (isCollectionNotEmpty(soldierWechatMemberList) && isCollectionNotEmpty(officerWechatMemberList))) {
				siteWechatMemberMap.put(site, allWechatMemberList);
			}
		}
	}

	private void initTagCodeMap(){
		tagIdCodeMap = new HashMap<>();
		codetagIdMap = new HashMap<>();
		List<WechatTag> tagList = WechatUtil.getTagList();
		for (WechatTag wechatTag : tagList) {
			String[] tagNameSplits = wechatTag.getTagname().split("\\|");
			if(ObjectUtils.isArrayNotEmpty(tagNameSplits)){
				String tagid = wechatTag.getTagid();
				String code = tagNameSplits[1];
				tagIdCodeMap.put(tagid, code);
				codetagIdMap.put(code, tagid);
			}
		}
	}
	
	private Map<Integer, String[]> initSiteIdTagIdMap(){
		List<SiteInfo> infos = Dog.getInfosWithTags();
		siteIdTagIdMap = new HashMap<>();
		for (SiteInfo siteInfo : infos) {
			List<String> tagIdList = new ArrayList<>();
			String manTagId = getTagIdByCode(siteInfo.getManNode());
			String cusTagId = getTagIdByCode(siteInfo.getCusNode());
			if(StringUtils.isNotBlank(manTagId))
				tagIdList.add(manTagId);
			if(StringUtils.isNotBlank(cusTagId))
				tagIdList.add(cusTagId);
			siteIdTagIdMap.put(siteInfo.getSupervisorId(), tagIdList.toArray(new String[tagIdList.size()]));
		}
		return siteIdTagIdMap;
	}
	
	private String getTagIdByCode(String code){
		//MAN.XINGX.DINGDONG
		if(StringUtils.isNotBlank(code) && this.codetagIdMap.get(code) == null){
			String[] codeSplits =  code.split("\\.");
			//MAN.XINGX
			code = codeSplits[0] + "." + codeSplits[1];
		}
		return this.codetagIdMap.get(code);
	}
	
	public String[] getTagBySiteId(Integer siteId){
		return this.siteIdTagIdMap.get(siteId);
	}
	
	/**
	 * Description:
	 * 
	 * @return
	 * @author Matthew Xu
	 * @date May 10, 2020
	 */
	private Map<String, List<Integer>> getWechatMemberSiteMap(List<WechatUser> allWeChatMember) {
		// get all members under the department whose id is 1
		Map<String, List<Integer>> wechatMemberSiteMap = new HashMap<>();
		if (isCollectionNotEmpty(allWeChatMember)) {
			for (WechatUser wechatMember : allWeChatMember) {
				String[] allDeptId = wechatMember.getDepartment();
				List<Integer> allDeptSiteInfo = new ArrayList<>();
				for (String deptId : allDeptId) {
					List<Integer> singleDeptSiteInfo = getSiteInfoByTagId(deptId);
					if (isCollectionNotEmpty(singleDeptSiteInfo))
						allDeptSiteInfo.addAll(singleDeptSiteInfo);
				}
				wechatMemberSiteMap.put(wechatMember.getUserid(), allDeptSiteInfo);
			}
		}
		return wechatMemberSiteMap;
	}

	public List<WechatTag> isUserHasRequiredTag(String userId){
		List<WechatTag> list = new ArrayList<>();
		WechatUser wechatUser = new WechatUser();
		wechatUser.setUserid(userId);
		for(Map.Entry<WechatTag, List<WechatUser>> entry : this.tagIdUserListMap.entrySet()){
			if(entry.getValue().contains(wechatUser)){
				list.add(entry.getKey());
			}
		}
		return list;
	}
	
	public List<SiteInfo> getSiteListByTagId(String tagId){
		return this.tagIdSiteListMap.get(tagId);
	}
	
	private void initTagIdSiteListMap(){
		Iterator allSites = Dog.getInstance().getAllSites();
		while(allSites.hasNext()){
			Map.Entry<String, SiteInfo> entry = (Map.Entry<String, SiteInfo>) allSites.next();
			SiteInfo s = entry.getValue();
			String manNode = s.getManNode();
			String cusNode = s.getCusNode();
			String manTag = getTagIdByCode(manNode);
			String cusTag= getTagIdByCode(cusNode);
			
			if(StringUtils.isNotBlank(manTag)){
				List<SiteInfo> list = this.tagIdSiteListMap.get(manTag);
				if(list == null)
					list = new ArrayList<>();
				list.add(s);
				tagIdSiteListMap.put(manTag, list);
			}
			
			if(StringUtils.isNotBlank(cusTag)){
				List<SiteInfo> list = this.tagIdSiteListMap.get(cusTag);
				if(list == null)
					list = new ArrayList<>();
				list.add(s);
				tagIdSiteListMap.put(cusTag, list);
			}
			
		}
	}
	
	private List<Integer> getSiteInfoByTagId(String tagId) {
		List<Integer> result = new ArrayList<>();
		Iterator it = Dog.getInstance().getAllSites();
		while (it.hasNext()) {
			Map.Entry<String, SiteInfo> entry = (Map.Entry<String, SiteInfo>) it.next();
			SiteInfo s = entry.getValue();
			if (!StringUtils.isBlank(s.getTagId()) && tagId.equals(s.getTagId()) && !result.contains(s)) {
				result.add(s.getSupervisorId());
			}
			if (!StringUtils.isBlank(s.getTagId2()) && tagId.equals(s.getTagId2()) && !result.contains(s)) {
				result.add(s.getSupervisorId());
			}
			if (!StringUtils.isBlank(s.getTagId3()) && !result.contains(s)) {
				String[] tag3 = s.getTagId3().split(";");
				for (String t : tag3) {
					if (tagId.equals(t)) {
						result.add(s.getSupervisorId());
						break;
					}
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * Description:
	 * 
	 * @param deptId
	 * @return
	 * @author Matthew Xu
	 * @date May 10, 2020
	 */
	public String getTagUsers(String deptId) {

		List<WechatUser> wechatMemberList = deptIdWechatMemberMap.get(deptId);
		String deptMemberNameStr = "";
		if (isCollectionNotEmpty(wechatMemberList)) {
			for (WechatUser wechatMember : wechatMemberList) {
				String name = wechatMember.getName();
				if (name.indexOf(SKIP_SUFFIX) >= 0)
					continue;
				deptMemberNameStr += name + "，";
			}
			if(deptMemberNameStr.length() > 0)
				return deptMemberNameStr.substring(0, deptMemberNameStr.length() - 1);
		}

		return deptMemberNameStr;
	}

	/**
	 * Description:
	 * 
	 * @param deptId
	 * @return
	 * @author Matthew Xu
	 * @date May 10, 2020
	 */
	public String getWechatMemberStrByDeptId(String deptId) {
		List<WechatUser> wechatMemberList = deptIdWechatMemberMap.get(deptId);
		List<WechatUser> allWechatMemberList = new ArrayList<>();
		if (isCollectionNotEmpty(wechatMemberList))
			allWechatMemberList.addAll(wechatMemberList);
		Iterator<WechatUser> iterator = allWechatMemberList.iterator();
		while (iterator.hasNext()) {
			WechatUser wechatMember = iterator.next();
			if (wechatMember.getName().indexOf(SKIP_SUFFIX) >= 0)
				iterator.remove();
		}
		return JSON.toJSONString(allWechatMemberList);
	}

	private void welcomeNewWechatMember() {
		try {
			if(isMapNotEmpty(siteWechatMemberMap)){
				if(!isMapNotEmpty(oldSiteWechatMemberMap)){
					oldSiteWechatMemberMap = new ConcurrentHashMap<>();
					oldSiteWechatMemberMap.putAll(siteWechatMemberMap);
				}else{
					for (Entry<SiteInfo, List<WechatUser>> entry : siteWechatMemberMap.entrySet()) {
						SiteInfo siteInfo = entry.getKey();
						List<WechatUser> newWechatMemberList = entry.getValue();
						List<WechatUser> oldWeChatMemberList = oldSiteWechatMemberMap.get(siteInfo);
						
						if(isCollectionEmpty(newWechatMemberList) || isCollectionEmpty(oldWeChatMemberList))
							continue;
						
						String newWechatMemberStr = "";
						for (WechatUser newWechatMember : newWechatMemberList) {
							if (!oldWeChatMemberList.contains(newWechatMember) && !newWechatMember.getName().contains(SKIP_SUFFIX)) {
								newWechatMemberStr += newWechatMember.getName() + ",";
							}
						}
						
						if (newWechatMemberStr.length() > 0) {
							
							newWechatMemberStr = newWechatMemberStr.substring(0, newWechatMemberStr.length() - 1);
							Sender sender = Sender.getInstance(siteInfo.getChannel());
							sender.sendIM(new WechatMsg.Builder(getNewWechatMemberMsg(siteInfo, newWechatMemberStr),
									siteInfo.getAgentId(), new String[] { siteInfo.getTagId(), siteInfo.getTagId2() }).build());
						}
					}
					
					oldSiteWechatMemberMap = new ConcurrentHashMap<>();
					oldSiteWechatMemberMap.putAll(siteWechatMemberMap);
				}
			}
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	/**
	 * 
	 * Description:
	 * @param tagId
	 * @return
	 * @author Matthew Xu
	 * @date May 22, 2020
	 */
	public List<WechatUser> getAlarmFaxCallUsers(String deptId) {
		if(isMapNotEmpty(deptIdWechatMemberMap) ){
			List<WechatUser> wechatMembers = deptIdWechatMemberMap.get(deptId);
			List<WechatUser> wechatMemberList = new ArrayList<>();
			if (isCollectionNotEmpty(wechatMembers)) {
				for (WechatUser wechatMember : wechatMembers) {
					if (wechatMember.getName().contains(SKIP_SUFFIX) || !wechatMember.getName().endsWith(SOLIDER_SUFFIX))
						continue;

					wechatMemberList.add(wechatMember);
				}
			}
			return wechatMemberList;
		}
		return null;
	}
	public List<WechatUser> getMessageReceiver(String deptId,String[] types)
	{
	    return getMessageReceiver(null,deptId,types);
	}
	public List<WechatUser> getMessageReceiver(Integer idSite,String[] types)
    {
        return getMessageReceiver(idSite,null,types);
    }
	public List<WechatUser> getMessageReceiver(Integer idSite,String deptId,String[] types)
	{
	    if(deptIdWechatMemberMap != null)
	    {
	        List<WechatUser> wechatMembers = new ArrayList<>();
	        if(idSite == null)
	        {
	            wechatMembers = deptIdWechatMemberMap.get(deptId);
	        }
	        else
	        {
	            String[] tagIds = siteIdTagIdMap.get(idSite);
	            if(ObjectUtils.isArrayNotEmpty(tagIds)){
	                for (String tagId : tagIds) {
	                    List<WechatUser> list = tagIdUserListMap.get(tagId);
	                    if(list != null)
	                        wechatMembers.addAll(list);
	                }
	            }
	        }
            List<WechatUser> wechatMemberList = new ArrayList<>();
            WxXmlCpInMemoryConfigStorage configStorage = WechatService.getInstance().getStorage();
            if(StringUtils.isNotBlank(configStorage.getDebug()))
            {
                WechatUser m = new WechatUser("葛为卫", "13358000723");
                m.setUserid("nemoge");
                wechatMemberList.add(m);
            }
            if (isCollectionNotEmpty(wechatMembers)) 
            {
                for (WechatUser wechatMember : wechatMembers) 
                {
                    if (wechatMember.getName().endsWith(SKIP_SUFFIX))
                        continue;
                    for(String t:types)
                    {
                        if(wechatMember.getName().endsWith(t))
                            wechatMemberList.add(wechatMember);
                    }
                }
            }
            return wechatMemberList;
	    }
	    return null;
	}

	private String getNewWechatMemberMsg(SiteInfo siteInfo, String newWechatMemberStr){
		String soldiers = this.getTagUsers(siteInfo.getTagId());
		String soldierMemberStr = StringUtils.isBlank(soldiers) ? "无" : soldiers;
		
		String officers = this.getTagUsers(siteInfo.getTagId2());
		String officerMemberStr = StringUtils.isBlank(officers) ? "无" : officers;
		return propertyConfig.getValue(WechatMemberMsgTemplate.MM_WELCOME_NEW.getKey(), 
				new Object[]{siteInfo.getDescription(), 
						newWechatMemberStr, 
						soldierMemberStr, 
						officerMemberStr});
	}

	
	public WechatUser getWechatMemberByUserId(String userId) {
		return weChatMemberMap.get(userId);
	}

	public boolean isDeptNotEmptyOfMembers(String deptId){
		return isMapNotEmpty(deptIdWechatMemberMap) 
				&& isCollectionNotEmpty(deptIdWechatMemberMap.get(deptId));
	}
	
	public boolean isUserExist(String userId) {
		WechatUser wechatUser = new WechatUser();
		wechatUser.setUserid(userId);
		return allMembers.contains(wechatUser);
	}
	
	public List<SiteInfo> getSitesByUserId(String userId, String method, String order) {
		List<Integer> siteIdList = allWechatMemberSiteMap.get(userId);
		List<SiteInfo> siteList = new ArrayList<>();
		if (isCollectionNotEmpty(siteIdList)) {
			for (Integer siteId : siteIdList) {
				siteList.add(Dog.getInstance().getSiteInfoByIdSite(siteId.intValue()));
			}
			SortList<SiteInfo> sort = new SortList<SiteInfo>();
			sort.Sort(siteList, method, order, true);
			return siteList;
		}
		return null;
	}

	public boolean isTagCrawled() {
		return loaded;
	}

	public List<WechatUser> getGeneralWechatMember() {
		return generalWechatMember;
	}

	/**
	 * @return the generalWechatMemberSiteMap
	 */
	public Map<String, List<Integer>> getGeneralWechatMemberSiteMap() {
		return generalWechatMemberSiteMap;
	}

	/**
	 * @return the loaded
	 */
	public boolean isLoaded() {
		return loaded;
	}

	public Map<SiteInfo, List<WechatUser>> getSiteWechatMemberMap() {
		return siteWechatMemberMap;
	}

	public Map<SiteInfo, List<WechatUser>> getSiteWechatMemberMap4Export() {
		return siteWechatMemberMap4Export;
	}

}
