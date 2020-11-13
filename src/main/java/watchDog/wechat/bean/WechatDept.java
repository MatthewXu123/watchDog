
package watchDog.wechat.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date May 10, 2020
 */
public class WechatDept implements Serializable{

	private static final long serialVersionUID = 7131657954381559474L;

	private String name;

	private String id;

	private String parentid;
	
	private int order;

	private List<WechatMember> wechatMemberList;

	public WechatDept() {
			super();
		}

	public WechatDept(String id, String name, String parentid) {
			super();
			this.name = name;
			this.id = id;
			this.parentid = parentid;
		}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	/**
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	
	/**
	 * @return the wechatMemberList
	 */
	public List<WechatMember> getWechatMemberList() {
		return wechatMemberList;
	}

	/**
	 * @param wechatMemberList the wechatMemberList to set
	 */
	public void setWechatMemberList(List<WechatMember> wechatMemberList) {
		this.wechatMemberList = wechatMemberList;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WechatDept other = (WechatDept) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}
