
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
	
	private List<String> officerIds;
	
	private List<String> soldierIds;
	
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

	

	/**
	 * @return the officerIds
	 */
	public List<String> getOfficerIds() {
		return officerIds;
	}

	/**
	 * @param officerIds the officerIds to set
	 */
	public void setOfficerIds(List<String> officerIds) {
		this.officerIds = officerIds;
	}

	/**
	 * @return the soldierIds
	 */
	public List<String> getSoldierIds() {
		return soldierIds;
	}

	/**
	 * @param soldierIds the soldierIds to set
	 */
	public void setSoldierIds(List<String> soldierIds) {
		this.soldierIds = soldierIds;
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
		return "CommunityDTO [code=" + code + ", isActive=" + isActive + ", deptId=" + deptId + ", officerIds="
				+ officerIds + ", soldierIds=" + soldierIds + ", faxRuleCodes=" + faxRuleCodes + "]";
	}


}
