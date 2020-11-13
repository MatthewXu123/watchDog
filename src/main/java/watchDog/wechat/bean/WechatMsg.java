
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

	// userId or deptId or tagId
	private String[] targetIds;

	private int type;

	
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

		private String[] targetIds;

		// optional fields
		private String title = "";

		private int type = Sender.WECHAT_MSG_TYPE_DEPT;

		/**
		 * @param content
		 * @param agentId
		 * @param targetIds
		 */
		public Builder(String content, String agentId, String[] targetIds) {
			this.content = content;
			this.agentId = agentId;
			this.targetIds = targetIds;
		}
		
		public Builder(String content, String[] targetIds) {
			this.content = content;
			this.targetIds = targetIds;
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

		public Builder targetIds(String[] val) {
			targetIds = val;
			return this;
		}

		public Builder title(String val) {
			title = val;
			return this;
		}

		public Builder type(int val) {
			type = val;
			return this;
		}

		public WechatMsg build() {
			return new WechatMsg(this);
		}

	}

	private WechatMsg(Builder builder) {
		content = builder.content;
		agentId = builder.agentId;
		targetIds = builder.targetIds;
		title = builder.title;
		type = builder.type;
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
	 * @return the targetIds
	 */
	public String[] getTargetIds() {
		return targetIds;
	}



	/**
	 * @return the type
	 */
	public int getType() {
		return type;
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



	/**
	 * @param targetIds the targetIds to set
	 */
	public void setTargetIds(String[] targetIds) {
		this.targetIds = targetIds;
	}



	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}



	@Override
	public String toString() {
		return "WechatMsg [title=" + title + ", content=" + content + ", agentId=" + agentId + ", targetIds="
				+ Arrays.toString(targetIds) + ", type=" + type + "]";
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
