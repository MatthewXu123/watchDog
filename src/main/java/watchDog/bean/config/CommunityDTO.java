
package watchDog.bean.config;

import java.io.Serializable;
import java.util.List;

/**
 * Description:
 * @author Matthew Xu
 * @date May 9, 2020
 */
public class CommunityDTO implements Serializable{

	private static final long serialVersionUID = -2672754196093138717L;

	// manNode or cusNode
	private String code;
	
	private boolean isActive;
	
	private boolean isGlobal;
	
	private String deptId;
	
	private String requiredUserTag;
	
	private List<String> faxRuleCodes;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the deptId
	 */
	public String getDeptId() {
		return deptId;
	}

	/**
	 * @param deptId the deptId to set
	 */
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getRequiredUserTag() {
		return requiredUserTag;
	}

	public void setRequiredUserTag(String requiredUserTag) {
		this.requiredUserTag = requiredUserTag;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return the isActive
	 */
	public boolean getIsActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return the faxRuleCodes
	 */
	public List<String> getFaxRuleCodes() {
		return faxRuleCodes;
	}

	/**
	 * @param faxRuleCodes the faxRuleCodes to set
	 */
	public void setFaxRuleCodes(List<String> faxRuleCodes) {
		this.faxRuleCodes = faxRuleCodes;
	}

	/**
	 * @return the isGlobal
	 */
	public boolean getIsGlobal() {
		return isGlobal;
	}

	/**
	 * @param isGlobal the isGlobal to set
	 */
	public void setIsGlobal(boolean isGlobal) {
		this.isGlobal = isGlobal;
	}

	@Override
	public String toString() {
		return "CommunityDTO [code=" + code + ", isActive=" + isActive + ", isGlobal=" + isGlobal + ", deptId=" + deptId
				+ ", requiredUserTag=" + requiredUserTag + ", faxRuleCodes=" + faxRuleCodes + "]";
	}

}
