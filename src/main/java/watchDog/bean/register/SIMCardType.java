
package watchDog.bean.register;

/**
 * Description:
 * @author Matthew Xu
 * @date Nov 26, 2020
 */
public enum SIMCardType {

	CHINA_MOBILE(0, "中国移动"),
	CHINA_TELECOM(1, "中国电信");
	
	private int code;
	
	private String description;
	
	private SIMCardType(int code, String description) {
		this.code = code;
		this.description = description;
	}

	private SIMCardType(int code){
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
