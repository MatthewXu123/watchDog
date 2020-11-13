
package watchDog.bean;

import java.io.Serializable;

/**
 * Description:
 * @author Matthew Xu
 * @date May 12, 2020
 */
public class VPNInfoDO implements Serializable{

	private static final long serialVersionUID = 6826326226834921697L;

	private String username;
	
	private String pid;
	
	private String ip;
	
	private String lastOnlineDate;
	
	private String statusDescription;

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the pid
	 */
	public String getPid() {
		return pid;
	}

	/**
	 * @param pid the pid to set
	 */
	public void setPid(String pid) {
		this.pid = pid;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the lastOnlineDate
	 */
	public String getLastOnlineDate() {
		return lastOnlineDate;
	}

	/**
	 * @param lastOnlineDate the lastOnlineDate to set
	 */
	public void setLastOnlineDate(String lastOnlineDate) {
		this.lastOnlineDate = lastOnlineDate;
	}

	/**
	 * @return the statusDescription
	 */
	public String getStatusDescription() {
		return statusDescription;
	}

	/**
	 * @param statusDescription the statusDescription to set
	 */
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}

	@Override
	public String toString() {
		return "VPNInfoDO [username=" + username + ", pid=" + pid + ", ip=" + ip + ", lastOnlineDate=" + lastOnlineDate
				+ ", statusDescription=" + statusDescription + "]";
	}
	
}
