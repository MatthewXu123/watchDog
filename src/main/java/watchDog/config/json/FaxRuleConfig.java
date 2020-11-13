
package watchDog.config.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import watchDog.bean.FaxInfoDO;
import watchDog.bean.config.AlarmFaxRuleDTO;
import watchDog.bean.config.CommunityDTO;
import watchDog.bean.config.FaxRuleDTO;
import watchDog.util.ObjectUtils;
import watchDog.wechat.config.CommunityConfig;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date May 21, 2020
 */
public class FaxRuleConfig extends BaseJSONConfig {

	private static final String DEPT_OFFICER_PATH = "faxrule.json";

	// The list of the fax rule. 
	private static List<FaxRuleDTO> faxRuleDTOList;
	
	// The list of the alarmFaxRuleDTO.
	private static List<AlarmFaxRuleDTO> alarmFaxRuleDTOs;
	
	// The map of the faxrule code and fax rules.
	private static Map<String, FaxRuleDTO> faxCodeRuleMap;
	
	// The map of the faxrule code and alarmFaxRuleDTO list.
	private static Map<String, List<AlarmFaxRuleDTO>> faxCodeAlarmFaxRuleMap;
	
	// The map of the faxrule code and alarmcode list.
	private static Map<String, List<String>> faxCodeAlarmCodeMap;
	
	public static final String DEFAULT_HIGH_TEMP_CODE = "s_HI";
	public static final String CODE_HIGH_TEMP = "High_Temp";
	public static final String CODE_LT_HIGH_TEMP = "LT_High_Temp";
	public static final String CODE_MT_HIGH_TEMP = "MT_High_Temp";
	public static final String CODE_UNIT_OFFLINE = "OFFLINE";
	public static final String CODE_TAG_HIGH_TEMP = "Tag_High_Temp";
	
	static {
		if (ObjectUtils.isCollectionEmpty(faxRuleDTOList))
			getConfigJSONStr();
	}

	/**
	 * 
	 * Description: The initialization process of the faxrule.json
	 * @author Matthew Xu
	 * @date Jun 16, 2020
	 */
	public static void getConfigJSONStr() {
		// Get the fax rules from the json
		faxRuleDTOList = JSON.parseArray(readFromPath(basePath + DEPT_OFFICER_PATH), FaxRuleDTO.class);
		
		alarmFaxRuleDTOs = new ArrayList<>();
		faxCodeRuleMap = new HashMap<>();
		faxCodeAlarmFaxRuleMap = new HashMap<>();
		faxCodeAlarmCodeMap = new HashMap<>();
		
		for (FaxRuleDTO faxRuleDTO : faxRuleDTOList) {
			String faxRuleCode = faxRuleDTO.getRuleCode();
			faxCodeRuleMap.put(faxRuleCode, faxRuleDTO);
			List<AlarmFaxRuleDTO> alarmFaxRuleDTOList = new ArrayList<>();
			List<String> alarmCodes = new ArrayList<>();
			for (AlarmFaxRuleDTO alarmFaxRuleDTO : faxRuleDTO.getAlarmFaxRuleDTOList()) {
				if (!alarmFaxRuleDTOs.contains(alarmFaxRuleDTO)) {
					alarmFaxRuleDTOs.add(alarmFaxRuleDTO);
				}
				alarmFaxRuleDTOList.add(alarmFaxRuleDTO);
				alarmCodes.add(alarmFaxRuleDTO.getAlarmCode());
			}
			faxCodeAlarmFaxRuleMap.put(faxRuleCode, alarmFaxRuleDTOList);
			faxCodeAlarmCodeMap.put(faxRuleCode, alarmCodes);
		}
	}
	
	/**
	 * 
	 * Description: If the returned result is null, the alarm type is not included in the right alarm faxrules.
	 * @param alarmCount
	 * @param manNode
	 * @param cusNode
	 * @param alarmCode
	 * @return
	 * @author Matthew Xu
	 * @date Jun 16, 2020
	 */
	public static boolean isAlarmCountEnough(int alarmCount, String manNode, String cusNode, String alarmCode) {
		List<AlarmFaxRuleDTO> rightAlarmFaxRuleDTOs = getRightAlarmFaxRuleDTOs(manNode, cusNode);
		if(ObjectUtils.isCollectionNotEmpty(rightAlarmFaxRuleDTOs)){
			for (AlarmFaxRuleDTO alarmFaxRuleDTO : rightAlarmFaxRuleDTOs) {
				if(alarmFaxRuleDTO.getAlarmCode().equals(alarmCode)){
					return alarmFaxRuleDTO.getCount() == 0 ? true : (alarmFaxRuleDTO.getCount() <= alarmCount ? true : false);
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * Description: We get the right alarm codes according to the community config.
	 * The cusNode has the highest the priority and then the manNode is next.
	 * If these two nodes don't have the faxrule configuration, we return the default faxrules.
	 * @param manNode
	 * @param cusNode
	 * @return
	 * @author Matthew Xu
	 * @date Jun 16, 2020
	 */
	private static List<AlarmFaxRuleDTO> getRightAlarmFaxRuleDTOs(String manNode, String cusNode){
		List<AlarmFaxRuleDTO> cusAlarmCodes = getAlarmFaxRuleDTOByCommunityCode(cusNode);
		List<AlarmFaxRuleDTO> manAlarmCodes = getAlarmFaxRuleDTOByCommunityCode(manNode);
		
		return ObjectUtils.isCollectionNotEmpty(cusAlarmCodes) ? cusAlarmCodes : 
			(ObjectUtils.isCollectionNotEmpty(manAlarmCodes) ? manAlarmCodes : getAlarmFaxRuleDTOByCommunityCode(CommunityConfig.DEFAULT_CODE));
	}
	
	/**
	 * Description:
	 * @param communityCode
	 * @return
	 * @author Matthew Xu
	 * @date Jun 1, 2020
	 */
	public static List<AlarmFaxRuleDTO> getAlarmFaxRuleDTOByCommunityCode(String communityCode) {
		List<AlarmFaxRuleDTO> alarmFaxRuleDTOs = new ArrayList<>();
		CommunityDTO communityDTO = CommunityConfig.getValue(communityCode);
		if(communityDTO == null)
			return null;
			
		List<String> faxRuleCodes = communityDTO.getFaxRuleCodes();
		if(ObjectUtils.isCollectionNotEmpty(faxRuleCodes)){
			for (String faxRuleCode : faxRuleCodes) {
				List<AlarmFaxRuleDTO> list = faxCodeAlarmFaxRuleMap.get(faxRuleCode);
				alarmFaxRuleDTOs.addAll(list);
			}
		}
		return alarmFaxRuleDTOs;
	}
	
	/**
	 * 
	 * Description: Designed for {@link watchDog.dao.FaxInfoDAO#getFaxInfoList()}
	 * @param alarmCode
	 * @param alarmFaxRuleDTOList
	 * @return
	 * @author Matthew Xu
	 * @date Jul 2, 2020
	 */
	public static boolean isAlarmCodeExist(String alarmCode, List<AlarmFaxRuleDTO> alarmFaxRuleDTOList){
		for (AlarmFaxRuleDTO alarmFaxRuleDTO : alarmFaxRuleDTOList) {
			if(alarmFaxRuleDTO.getAlarmCode().equals(alarmCode)){
				alarmFaxRuleDTOList.remove(alarmFaxRuleDTO);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * Description:
	 * @param alarmCode
	 * @param alarmFaxRuleDTOList
	 * @return
	 * @author Matthew Xu
	 * @date Jul 21, 2020
	 */
	public static AlarmFaxRuleDTO removeAlarmFaxRuleDTOSimpleByAlarmCode (String alarmCode, List<AlarmFaxRuleDTO> alarmFaxRuleDTOList){
		for (AlarmFaxRuleDTO alarmFaxRuleDTO : alarmFaxRuleDTOList) {
			if(alarmFaxRuleDTO.getAlarmCode().equals(alarmCode)){
				alarmFaxRuleDTOList.remove(alarmFaxRuleDTO);
				return alarmFaxRuleDTO;
			}
		}
		return null;
	}
	
	public static AlarmFaxRuleDTO getAlarmFaxRuleDTOByAlarmCode(String alarmCode, String manNode, String cusNode){
		List<AlarmFaxRuleDTO> rightAlarmFaxRuleDTOs = getRightAlarmFaxRuleDTOs(manNode, cusNode);
		for (AlarmFaxRuleDTO alarmFaxRuleDTO : rightAlarmFaxRuleDTOs) {
			if(alarmFaxRuleDTO.getAlarmCode().equals(alarmCode)){
				return alarmFaxRuleDTO;
			}
		}
		return null;
	}
	
	public static List<AlarmFaxRuleDTO> getAlarmFaxRuleDTOs() {
		return alarmFaxRuleDTOs;
	}

}
