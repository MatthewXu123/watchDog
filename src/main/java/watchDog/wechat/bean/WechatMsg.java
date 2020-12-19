
package watchDog.wechat.bean;

import java.io.Serializable;
import java.util.Arrays;

import watchDog.wechat.util.sender.Sender;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date May 12, 2020
 */
public class WechatMsg implements Serializable {

	private static final long serialVersionUID = -7339077313559699171L;

	private String title;

	private String content;

	private String agentId;

	private String[] deptIds;
	
	private String[] tagIds;
	
	private String[] userIds;
	
	/**
	 * 
	 */
	public WechatMsg() {
		super();
	}

	public static class Builder {

		// required fields
		private String content;

		private String agentId;

		private String[] deptIds;
		
		private String[] userIds;
		
		private String[] tagIds;

		// optional fields
		private String title = "";

		public Builder(String content, String agentId, String[] deptIds, String[] tagIds) {
			this.content = content;
			this.agentId = agentId;
			this.deptIds = deptIds;
		}
		
		public Builder(String content, String agentId, String[] deptIds) {
			this.content = content;
			this.agentId = agentId;
			this.deptIds = deptIds;
		}
		
		public Builder(String content, String[] deptIds) {
			this.content = content;
			this.deptIds = deptIds;
		}
		
		public Builder(String content, String agentId) {
			this.content = content;
			this.agentId = agentId;
		}

		/**
		 * @param content
		 */
		public Builder(String content) {
			super();
			this.content = content;
		}

		public Builder content(String val) {
			content = val;
			return this;
		}

		public Builder agentId(String val) {
			agentId = val;
			return this;
		}

		public Builder deptIds(String[] val) {
			deptIds = val;
			return this;
		}
		
		public Builder tagIds(String[] val) {
			tagIds = val;
			return this;
		}
		
		public Builder userIds(String[] val) {
			userIds = val;
			return this;
		}
		
		public Builder title(String val) {
			title = val;
			return this;
		}

		public WechatMsg build() {
			return new WechatMsg(this);
		}

	}

	private WechatMsg(Builder builder) {
		content = builder.content;
		agentId = builder.agentId;
		deptIds = builder.deptIds;
		tagIds = builder.tagIds;
		userIds = builder.userIds;
		title = builder.title;
	}

	
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}



	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}



	/**
	 * @return the agentId
	 */
	public String getAgentId() {
		return agentId;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}



	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}



	/**
	 * @param agentId the agentId to set
	 */
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String[] getDeptIds() {
		return deptIds;
	}

	public void setDeptIds(String[] deptIds) {
		this.deptIds = deptIds;
	}

	public String[] getTagIds() {
		return tagIds;
	}

	public void setTagIds(String[] tagIds) {
		this.tagIds = tagIds;
	}

	public String[] getUserIds() {
		return userIds;
	}

	public void setUserIds(String[] userIds) {
		this.userIds = userIds;
	}
	
	@Override
	public String toString() {
		return "WechatMsg [title=" + title + ", content=" + content + ", agentId=" + agentId + ", deptIds="
				+ Arrays.toString(deptIds) + ", tagIds=" + Arrays.toString(tagIds) + ", userIds="
				+ Arrays.toString(userIds) + "]";
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
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
		WechatMsg other = (WechatMsg) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		return true;
	}
	
	
}
