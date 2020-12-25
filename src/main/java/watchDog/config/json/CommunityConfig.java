
package watchDog.config.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import watchDog.bean.SiteInfo;
import watchDog.bean.config.CommunityDTO;
import watchDog.property.template.WechatMemberMsgTemplate;
import watchDog.util.ObjectUtils;

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
			getConfig();
			getCustomerCode();
		}
	}
	
	public static void getConfig() {
		communityDTOList = JSON.parseArray(readFromPath(basePath + PATH_COMMUNITY_CONFIG), CommunityDTO.class);
		codeCommunityMap = new HashMap<>();
		for (CommunityDTO communityDTO : communityDTOList) {
			if(communityDTO.getCode().equals(DEFAULT_CODE) && communityDTO.getIsGlobal())
				isGlobal = true;
			codeCommunityMap.put(communityDTO.getCode(), communityDTO);
		}
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

	public static List<CommunityDTO> getCommunityDTOList() {
		return communityDTOList;
	}

	public static void setCommunityDTOList(List<CommunityDTO> communityDTOList) {
		CommunityConfig.communityDTOList = communityDTOList;
	}
	
}
