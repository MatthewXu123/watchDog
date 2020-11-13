
package watchDog.property.template;

/**
 * Description:
 * @author Matthew Xu
 * @date May 28, 2020
 */
public enum WechatMemberMsgTemplate {

	// member msg
	MM_NO_SOLIDER("mm_no_soldier"),
	MM_NO_OFFICER("mm_no_officer"),
	MM_WRONG_SOLDIER("mm_wrong_soldier"),
	MM_WRONG_OFFICER("mm_wrong_officer"),
	MM_NO_PERSON("mm_no_person"),
	MM_WELCOME_NEW("mm_welcome_new");
	
	private String key;
	
	private WechatMemberMsgTemplate(String key){
		this.key = key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
	
}
