
package watchDog.wechat.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import watchDog.bean.SiteInfo;
import watchDog.bean.config.CommunityDTO;
import watchDog.config.json.BaseJSONConfig;
import watchDog.property.template.WechatMemberMsgTemplate;
import watchDog.util.ObjectUtils;
import watchDog.wechat.bean.WechatMember;
import watchDog.wechat.bean.WechatMsg;
import watchDog.wechat.util.WechatUtil;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date May 9, 2020
 */
public class CommunityConfig extends BaseJSONConfig {

	private static final Logger logger = Logger.getLogger(CommunityConfig.class);
	
	private static final String PATH_COMMUNITY_CONFIG = "community.json";
	
	private static final String MAN_CODE_PREFIX = "MAN";
	
	private static final String CUS_CODE_PREFIX = "CUS";
	
	public static final String DEFAULT_CODE = "Default";
	
	private static List<CommunityDTO> communityDTOList;
	
	private static Map<String, CommunityDTO> codeCommunityMap;
	
	private static List<String> manCodeList;
	
	private static List<String> cusCodeList;
	// The codelist doen't have the default code.
	private static List<String> codeList;
	
	// If isGlobal is true, it means that we adopt the default fax rules for all the communities.
	private static boolean isGlobal = false;
	 
	private static final String SKIP_CHECK = "-1";
	static{
		if(ObjectUtils.isCollectionEmpty(communityDTOList) || ObjectUtils.isMapEmpty(codeCommunityMap)){
			getConfigJSONStr();
			getCustomerCode();
		}
	}
	
	public static void getConfigJSONStr() {
		communityDTOList = JSON.parseArray(readFromPath(basePath + PATH_COMMUNITY_CONFIG), CommunityDTO.class);
		codeCommunityMap = new HashMap<>();
		for (CommunityDTO communityDTO : communityDTOList) {
			if(communityDTO.getCode().equals(DEFAULT_CODE) && communityDTO.getIsGlobal())
				isGlobal = true;
			codeCommunityMap.put(communityDTO.getCode(), communityDTO);
		}
	}

	/**
	 * 
	 * Description:
	 * @param siteInfo
	 * @return
	 * @author Matthew Xu
	 * @date May 12, 2020
	 */
	public static List<WechatMsg> getWechatMemberChecked(SiteInfo siteInfo) {
		List<WechatMsg> wechatMsgList = new ArrayList<>();
		CommunityDTO manCommunity = getValue(siteInfo.getManNode());
		if(manCommunity != null && manCommunity.getIsActive()){
			WechatMsg soldierMsg = getWechatMemberCheckedMsg(siteInfo, true, manCommunity.getSoldierIds());
			if(soldierMsg != null && !wechatMsgList.contains(soldierMsg))
				wechatMsgList.add(soldierMsg);
			
			WechatMsg officerMsg = getWechatMemberCheckedMsg(siteInfo, false, manCommunity.getOfficerIds());
			if(officerMsg != null && !wechatMsgList.contains(officerMsg))
				wechatMsgList.add(officerMsg);
		}
		
		CommunityDTO cusCommunity = getValue(siteInfo.getCusNode());
		if(cusCommunity != null && cusCommunity.getIsActive() ){
			WechatMsg soldierMsg = getWechatMemberCheckedMsg(siteInfo, true, cusCommunity.getSoldierIds());
			if(soldierMsg != null && !wechatMsgList.contains(soldierMsg))
				wechatMsgList.add(soldierMsg);
			
			WechatMsg officerMsg = getWechatMemberCheckedMsg(siteInfo, false, cusCommunity.getOfficerIds());
			if(officerMsg != null && !wechatMsgList.contains(officerMsg))
				wechatMsgList.add(officerMsg);
		}
		
		return wechatMsgList;
	}

	/**
	 * 
	 * Description:
	 * @param tagId
	 * @param siteInfo
	 * @param isSoldier
	 * @param requiredMembers The members configured in the community.json
	 * @return
	 * @author Matthew Xu
	 * @date Jun 8, 2020
	 */
	private static WechatMsg getWechatMemberCheckedMsg(SiteInfo siteInfo, boolean isSoldier, List<String> requiredMembers){
		WechatMsg msg = null;
		String tagId = isSoldier ? siteInfo.getTagId() : siteInfo.getTagId2();
		// If the tagId is blank, we don't need to check it.
		if(StringUtils.isNotBlank(tagId) && !tagId.equals(SKIP_CHECK)){
			List<WechatMember> members = WechatUtil.getMemberByDeptId(tagId, WechatUtil.FECTH_CHILD);
			
			if(ObjectUtils.isCollectionEmpty(members)){
				msg = new WechatMsg.Builder(getPropertyValue(isSoldier ? WechatMemberMsgTemplate.MM_NO_SOLIDER : WechatMemberMsgTemplate.MM_NO_OFFICER, 
						siteInfo)).build();
			}else {
				if(ObjectUtils.isCollectionNotEmpty(requiredMembers)){
					int count = 0;
					for (WechatMember member : members) {
						if(requiredMembers.contains(member.getUserid()))
							count ++ ;
					}
					if (count < requiredMembers.size()){
						msg = new WechatMsg.Builder(getPropertyValue(isSoldier ? WechatMemberMsgTemplate.MM_WRONG_SOLDIER : WechatMemberMsgTemplate.MM_WRONG_OFFICER, 
								siteInfo)).build();
					}
				}
			}
		}
		return msg;
	}
	
	
	private static String getPropertyValue(WechatMemberMsgTemplate wechatMemberMsgTemplate, SiteInfo siteInfo){
		return propertyConfig.getValue(wechatMemberMsgTemplate.getKey(), 
						new Object[]{siteInfo.getIp(),siteInfo.getDescription(),(StringUtils.isBlank(siteInfo.getManDescription()) ? "未配置" : siteInfo.getManDescription())});
	}
	
	public static CommunityDTO getValue(String code){
		return codeCommunityMap.get(code);
	}
	
	/**
	 * 
	 * Description:
	 * @param code
	 * @return
	 * @author Matthew Xu
	 * @date May 14, 2020
	 */
	public static String getDeptIdByCode(String code){
		CommunityDTO communityDTO = codeCommunityMap.get(code);
		String defaultDeptId = getDefaultDeptId();
		if(communityDTO == null)
			return defaultDeptId;
		String deptId = communityDTO.getDeptId();
		if(!StringUtils.isBlank(deptId))
			return deptId;
		return defaultDeptId;
	}
	
	/**
	 * 
	 * Description:
	 * @author Matthew Xu
	 * @date May 25, 2020
	 */
	private static void getCustomerCode(){
		manCodeList = new ArrayList<>();
		cusCodeList = new ArrayList<>();
		codeList = new ArrayList<>();
		for (CommunityDTO communityDTO : communityDTOList) {
			String code = communityDTO.getCode();
			if(code.startsWith(MAN_CODE_PREFIX))
				manCodeList.add(code);
			if(code.startsWith(CUS_CODE_PREFIX))
				cusCodeList.add(code);
		}
		codeList.addAll(manCodeList);
		codeList.addAll(cusCodeList);
	}
	
	public static List<String> getNecessarySoldierGroup(String code){
		CommunityDTO communityDTO = getValue(code);
		return communityDTO == null ? null : communityDTO.getSoldierIds();
	}
	
	public static List<String> getNecessaryOfficerGroup(String code){
		CommunityDTO communityDTO = getValue(code);
		return communityDTO == null ? null : communityDTO.getOfficerIds();
	}
	
	private static String getDefaultDeptId(){
		return codeCommunityMap.get(DEFAULT_CODE).getDeptId();
	}

	/**
	 * @return the deptMemberDTOList
	 */
	public static List<CommunityDTO> getDeptMemberDTOList() {
		return communityDTOList;
	}

	/**
	 * @return the manCodeList
	 */
	public static List<String> getManCodeList() {
		return manCodeList;
	}

	/**
	 * @return the cusCodeList
	 */
	public static List<String> getCusCodeList() {
		return cusCodeList;
	}

	/**
	 * @return the codeList
	 */
	public static List<String> getCodeList() {
		return codeList;
	}

	/**
	 * @return the isGlobal
	 */
	public static boolean getIsFaxRuleGlobal() {
		return isGlobal;
	}
	
}
