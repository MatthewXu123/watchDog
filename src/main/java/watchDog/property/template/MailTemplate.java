
package watchDog.property.template;

/**
 * Description:
 * @author Matthew Xu
 * @date Dec 25, 2020
 */
public enum MailTemplate {

	MAIL_OOS_TITLE("mail_oos_title"),
	MAIL_OOS_CONTENT("mail_oos_content"),
	MAIL_OOS_BODY("mail_oos_body");
	
	private String key;
	
	private MailTemplate(String key){
		this.key = key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
