
package watchDog.wechat.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Description:
 * @author Matthew Xu
 * @date Dec 16, 2020
 */
public class WechatTag implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String tagid;
	
	private String tagname;
	
	private List<WechatUser> userlist;
	
	private List<Integer> partylist;

	public String getTagid() {
		return tagid;
	}

	public void setTagid(String tagid) {
		this.tagid = tagid;
	}

	public String getTagname() {
		return tagname;
	}

	public void setTagname(String tagname) {
		this.tagname = tagname;
	}

	public List<WechatUser> getUserlist() {
		return userlist;
	}

	public void setUserlist(List<WechatUser> userlist) {
		this.userlist = userlist;
	}

	public List<Integer> getPartylist() {
		return partylist;
	}

	public void setPartylist(List<Integer> partylist) {
		this.partylist = partylist;
	}

	@Override
	public String toString() {
		return "WechatTag [tagid=" + tagid + ", tagname=" + tagname + ", userlist=" + userlist + ", partylist="
				+ partylist + "]";
	}
	
}
