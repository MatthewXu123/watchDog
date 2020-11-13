
package watchDog.bean.config;

import java.util.List;

import com.alibaba.fastjson.JSON;

/**
 * Description:
 * @author Matthew Xu
 * @date May 21, 2020
 */
public class FaxRuleDTO {

	private String ruleCode;
	
	private List<AlarmFaxRuleDTO> alarmFaxRuleDTOList;

	/**
	 * @return the ruleCode
	 */
	public String getRuleCode() {
		return ruleCode;
	}

	/**
	 * @param ruleCode the ruleCode to set
	 */
	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}

	/**
	 * @return the alarmFaxRuleDTOList
	 */
	public List<AlarmFaxRuleDTO> getAlarmFaxRuleDTOList() {
		return alarmFaxRuleDTOList;
	}

	/**
	 * @param alarmFaxRuleDTOList the alarmFaxRuleDTOList to set
	 */
	public void setAlarmFaxRuleDTOList(List<AlarmFaxRuleDTO> alarmFaxRuleDTOList) {
		this.alarmFaxRuleDTOList = alarmFaxRuleDTOList;
	}

	@Override
	public String toString() {
		return "FaxRuleDTO [ruleCode=" + ruleCode + ", alarmFaxRuleDTOList="
				+ JSON.toJSONString(alarmFaxRuleDTOList) + "]";
	}
	
}
