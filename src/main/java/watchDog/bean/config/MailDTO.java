
package watchDog.bean.config;

import java.util.Arrays;

/**
 * Description:
 * @author Matthew Xu
 * @date Dec 25, 2020
 */
public class MailDTO {
	
	private String identifier;
	
	private String fromAddress;
	
	private String authCode;
	
	private String[] toAddresses;
	
	private String mailSmtpHost;
	
	private String mailSmtpPort;

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String[] getToAddresses() {
		return toAddresses;
	}

	public void setToAddresses(String[] toAddresses) {
		this.toAddresses = toAddresses;
	}

	public String getMailSmtpHost() {
		return mailSmtpHost;
	}

	public void setMailSmtpHost(String mailSmtpHost) {
		this.mailSmtpHost = mailSmtpHost;
	}

	public String getMailSmtpPort() {
		return mailSmtpPort;
	}

	public void setMailSmtpPort(String mailSmtpPort) {
		this.mailSmtpPort = mailSmtpPort;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public String toString() {
		return "MailDTO [identifier=" + identifier + ", fromAddress=" + fromAddress + ", authCode=" + authCode
				+ ", toAddresses=" + Arrays.toString(toAddresses) + ", mailSmtpHost=" + mailSmtpHost + ", mailSmtpPort="
				+ mailSmtpPort + "]";
	}
	
}
