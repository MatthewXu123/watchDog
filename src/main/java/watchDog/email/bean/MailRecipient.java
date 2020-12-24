
package watchDog.email.bean;

import java.util.List;

/**
 * Description:
 * @author Matthew Xu
 * @date Dec 24, 2020
 */
public class MailRecipient {

	private List<String> toRecipients;
	
	private List<String> ccRecipient;
	
	private List<String> bccRecipient;

	public List<String> getToRecipients() {
		return toRecipients;
	}

	public void setToRecipients(List<String> toRecipients) {
		this.toRecipients = toRecipients;
	}

	public List<String> getCcRecipient() {
		return ccRecipient;
	}

	public void setCcRecipient(List<String> ccRecipient) {
		this.ccRecipient = ccRecipient;
	}

	public List<String> getBccRecipient() {
		return bccRecipient;
	}

	public void setBccRecipient(List<String> bccRecipient) {
		this.bccRecipient = bccRecipient;
	}
	
}
