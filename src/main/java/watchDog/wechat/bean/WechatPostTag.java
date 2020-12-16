
package watchDog.wechat.bean;

import java.util.List;

/**
 * Description:
 * @author Matthew Xu
 * @date Dec 16, 2020
 */
public class WechatPostTag {

	private String tagid;
	
	private String tagname;
	
	private List<String> userlist;
	
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

	public List<String> getUserlist() {
		return userlist;
	}

	public void setUserlist(List<String> userlist) {
		this.userlist = userlist;
	}

	public List<Integer> getPartylist() {
		return partylist;
	}

	public void setPartylist(List<Integer> partylist) {
		this.partylist = partylist;
	}
	
}
