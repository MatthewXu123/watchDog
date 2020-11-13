
package watchDog.property.template;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date Jun 10, 2020
 */
public enum CommonMsgLogTemplate {

	// common log
	CL_START("cl_start"), 
	CL_END("cl_end");
	
	private String key;

	private CommonMsgLogTemplate(String key) {
		this.key = key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
