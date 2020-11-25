
package watchDog.property.template;

/**
 * Description:
 * @author Matthew Xu
 * @date Nov 25, 2020
 */
public enum WechatDeptLogTemplate {

	WD_SOLDIER_UPDATE_FAILED("wd_soldier_update_failed"),
	WD_OFFICER_UPDATE_FAILED("wd_officer_update_failed"),
	WD_SOLDIER_DESC_UPDATE("wd_soldier_desc_update"),
	WD_OFFICER_DESC_UPDATE("wd_officer_desc_update");
	
	private String descripiton;
	
	private WechatDeptLogTemplate(String descripiton){
		this.descripiton = descripiton;
	}

	public String getDescripiton() {
		return descripiton;
	}

	public void setDescripiton(String descripiton) {
		this.descripiton = descripiton;
	}
	
	
}
