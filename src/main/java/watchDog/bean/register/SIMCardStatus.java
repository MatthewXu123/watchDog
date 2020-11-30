
package watchDog.bean.register;

/**
 * Description:
 * @author Matthew Xu
 * @date Nov 26, 2020
 */
public enum SIMCardStatus {

	ENABLED(0, "正常"),
	DISABLED(1, "停用"),
	DELETED(2, "销户");
	
	private int code;
	
	private String description;
	
	private SIMCardStatus(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public static SIMCardStatus getOneByCode(int code){
		switch (code) {
		case 0:
			return ENABLED;
		case 1:
			return DISABLED;
		case 2:
			return DELETED;
		default:
			break;
		}
		return null;
	}
	
	private SIMCardStatus(int code){
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
