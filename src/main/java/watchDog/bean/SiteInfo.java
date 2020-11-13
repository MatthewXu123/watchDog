package watchDog.bean;

import java.util.Arrays;
import java.util.Date;

import org.apache.log4j.Logger;
import org.postgresql.jdbc4.Jdbc4Array;

import watchDog.database.Record;
import watchDog.service.EnergyService;
import watchDog.service.ShortURLMgr;
import watchDog.util.DateTool;
import watchDog.util.RegexUtil;

public class SiteInfo implements Comparable<SiteInfo> {

	private static final Logger logger = Logger.getLogger(SiteInfo.class);
	
	private Integer supervisorId= null;
	private String ident= null;
	private String ip= null;
	private Date deadline = null;
	private boolean checkNetwork;
	private String agentId= null;
	private Integer channel= null;
	private String tagId= null;
	private String tagId2= null;
	private String tagId3= null;
	private Integer sendTag2Delay= null;
	private String ktype= null;
	private String description= null;
	private String lastSynch= null;
	private Date lastSynchDate= null;
	private Integer activeNum = 0;
	private Integer alarm30 = 0;
	private Integer highTemp30 = 0;
	private Integer kwhLastMonth = 0;
	private String comment= null;
	private boolean isRouterOnline = false;
	private boolean isEngineOnline = false;
	private Boolean probeissue = false;
	private String manNode= null;
	private String manDescription= null;
	private String cusNode= null;
	private String cusDescription= null;
	private String[] supervisorTags= null;
	public static final String BOSS = "boss";
	public static final String PVP = "PVP";
	public static final String PWP = "PWP";

	public SiteInfo() {
	}

	public SiteInfo(Record r, boolean loadEnergy){
		try {
			this.supervisorId = (Integer) r.get("idsite");
			this.ident = (String) r.get("ident");
			this.ip = (String) r.get("ipaddress");
			this.deadline = (Date)r.get("deadline");
			this.checkNetwork = r.get("checknetwork") == null ? false : (boolean)r.get("checknetwork");
			this.agentId = (String) (r.get("agent_id") == null ? "" : r.get("agent_id"));
			this.channel = (Integer) r.get("channel");
			this.tagId = (String) r.get("tag_id");
			this.tagId2 = (String) r.get("tag_id2");
			this.tagId3 = (String) r.get("tag_id3");
			this.sendTag2Delay = (Integer) r.get("send_tag2_delay");
			this.ktype = (String) r.get("ktype");
			this.description = (String) r.get("description");
			this.comment = (String) r.get("comment");
			this.manNode = (String) r.get("mannode");
			this.manDescription = (String) r.get("mandescription");
			this.cusNode = (String) r.get("cusnode");
			this.cusDescription = (String) r.get("cusdescription");
			this.probeissue = (Boolean)r.get("probeissue");
			if (r.get("supervisortags") != null) {
				Object array = ((Jdbc4Array) (r.get("supervisortags"))).getArray();
				this.supervisorTags = (String[]) array;
			}
			if (r.hasColumn("lastsynch") && r.get("lastsynch") != null) {
				Date d = (Date) r.get("lastsynch");
				this.lastSynchDate = d;
				this.lastSynch = DateTool.format(d, "yy-MM-dd HH:mm");
			}
			if (lastSynch == null)
				this.lastSynch = "";
			if (r.get("active_cnt") != null)
				this.activeNum = (int) r.get("active_cnt");
			if (r.get("cnt") != null)
				this.alarm30 = (int) r.get("cnt");
			if (r.get("cnt_high") != null)
				this.highTemp30 = (int) r.get("cnt_high");
			if (loadEnergy)
				this.kwhLastMonth = EnergyService.getKWHLastMonth(supervisorId);
		} catch (Exception e) {
			logger.error("",e);
		}
		
	}

	public String getIdent() {
		return ident;
	}

	public String getIp() {
		return ip;
	}

	public String getTagId() {
		return tagId;
	}

	public String getTagId2() {
		return tagId2;
	}

	public String getAgentId() {
		return agentId;
	}

	public String getDescription() {
		return description;
	}

	public void setIdent(String ident) {
		this.ident = ident;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getLastSynch() {
		return lastSynch;
	}

	public Date getLastSynchDate() {
		return lastSynchDate;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public Integer getChannel() {
		return channel == null ? 1 : channel;
	}

	public String getTagId3() {
		return tagId3;
	}

	public void setTagId3(String tagId3) {
		this.tagId3 = tagId3;
	}

	public Integer getSendTag2Delay() {
		return sendTag2Delay;
	}

	public void setSendTag2Delay(Integer sendTag2Delay) {
		this.sendTag2Delay = sendTag2Delay;
	}

	public void setTagId2(String tagId2) {
		this.tagId2 = tagId2;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public String getKtype() {
		return ktype;
	}

	public void setType(String ktype) {
		this.ktype = ktype;
	}

	public Integer getSupervisorId() {
		return supervisorId;
	}

	public void setSupervisorId(Integer supervisorId) {
		this.supervisorId = supervisorId;
	}

	public Boolean getCheckNetwork() {
		return checkNetwork;
	}

	public void setCheckNetwork(Boolean checkNetwork) {
		this.checkNetwork = checkNetwork;
	}

	public void setKtype(String ktype) {
		this.ktype = ktype;
	}

	public Integer getActiveNum() {
		return activeNum;
	}

	public Integer getAlarm30() {
		return alarm30;
	}

	public Integer getHighTemp30() {
		return highTemp30;
	}

	public Integer getKwhLastMonth() {
		return kwhLastMonth;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getSiteURL()
	{
		return ShortURLMgr.getInstance().getSiteURL(ip, supervisorId);
	}
	
	/**
	 * @return the isRouterOnline
	 */
	public boolean getIsRouterOnline() {
		return isRouterOnline;
	}

	/**
	 * @param isRouterOnline
	 *            the isRouterOnline to set
	 */
	public void setIsRouterOnline(boolean isRouterOnline) {
		this.isRouterOnline = isRouterOnline;
	}

	/**
	 * @return the isEngineOnline
	 */
	public boolean getIsEngineOnline() {
		return isEngineOnline;
	}

	/**
	 * @param isEngineOnline
	 *            the isEngineOnline to set
	 */
	public void setIsEngineOnline(boolean isEngineOnline) {
		this.isEngineOnline = isEngineOnline;
	}

	/**
	 * @return the manNode
	 */
	public String getManNode() {
		return manNode;
	}

	/**
	 * @param manNode
	 *            the manNode to set
	 */
	public void setManNode(String manNode) {
		this.manNode = manNode;
	}

	/**
	 * @return the manDescription
	 */
	public String getManDescription() {
		return manDescription;
	}

	/**
	 * @param manDescription
	 *            the manDescription to set
	 */
	public void setManDescription(String manDescription) {
		this.manDescription = manDescription;
	}

	/**
	 * @return the cusNode
	 */
	public String getCusNode() {
		return cusNode;
	}

	/**
	 * @param cusNode
	 *            the cusNode to set
	 */
	public void setCusNode(String cusNode) {
		this.cusNode = cusNode;
	}

	/**
	 * @return the cusDescription
	 */
	public String getCusDescription() {
		return cusDescription;
	}

	/**
	 * @param cusDescription
	 *            the cusDescription to set
	 */
	public void setCusDescription(String cusDescription) {
		this.cusDescription = cusDescription;
	}

	/**
	 * @return the supervisorTags
	 */
	public String[] getSupervisorTags() {
		return supervisorTags;
	}

	/**
	 * @param supervisorTags
	 *            the supervisorTags to set
	 */
	public void setSupervisorTags(String[] supervisorTags) {
		this.supervisorTags = supervisorTags;
	}
	

	/**
	 * @param checkNetwork the checkNetwork to set
	 */
	public void setCheckNetwork(boolean checkNetwork) {
		this.checkNetwork = checkNetwork;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	/**
	 * @param lastSynch the lastSynch to set
	 */
	public void setLastSynch(String lastSynch) {
		this.lastSynch = lastSynch;
	}

	/**
	 * @param lastSynchDate the lastSynchDate to set
	 */
	public void setLastSynchDate(Date lastSynchDate) {
		this.lastSynchDate = lastSynchDate;
	}

	/**
	 * @param activeNum the activeNum to set
	 */
	public void setActiveNum(Integer activeNum) {
		this.activeNum = activeNum;
	}

	/**
	 * @param alarm30 the alarm30 to set
	 */
	public void setAlarm30(Integer alarm30) {
		this.alarm30 = alarm30;
	}

	/**
	 * @param highTemp30 the highTemp30 to set
	 */
	public void setHighTemp30(Integer highTemp30) {
		this.highTemp30 = highTemp30;
	}

	/**
	 * @param kwhLastMonth the kwhLastMonth to set
	 */
	public void setKwhLastMonth(Integer kwhLastMonth) {
		this.kwhLastMonth = kwhLastMonth;
	}

	/**
	 * @param isRouterOnline the isRouterOnline to set
	 */
	public void setRouterOnline(boolean isRouterOnline) {
		this.isRouterOnline = isRouterOnline;
	}

	/**
	 * @param isEngineOnline the isEngineOnline to set
	 */
	public void setEngineOnline(boolean isEngineOnline) {
		this.isEngineOnline = isEngineOnline;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((supervisorId == null) ? 0 : supervisorId.hashCode());
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
		SiteInfo other = (SiteInfo) obj;
		if (supervisorId == null) {
			if (other.supervisorId != null)
				return false;
		} else if (!supervisorId.equals(other.supervisorId))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "SiteInfo [supervisorId=" + supervisorId + ", ident=" + ident + ", ip=" + ip + ", deadline=" + deadline
				+ ", checkNetwork=" + checkNetwork + ", agentId=" + agentId + ", channel=" + channel + ", tagId="
				+ tagId + ", tagId2=" + tagId2 + ", tagId3=" + tagId3 + ", sendTag2Delay=" + sendTag2Delay + ", ktype="
				+ ktype + ", description=" + description + ", lastSynch=" + lastSynch + ", lastSynchDate="
				+ lastSynchDate + ", activeNum=" + activeNum + ", alarm30=" + alarm30 + ", highTemp30=" + highTemp30
				+ ", kwhLastMonth=" + kwhLastMonth + ", comment=" + comment + ", isRouterOnline=" + isRouterOnline
				+ ", isEngineOnline=" + isEngineOnline + ", manNode=" + manNode + ", manDescription=" + manDescription
				+ ", cusNode=" + cusNode + ", cusDescription=" + cusDescription + ", supervisorTags="
				+ Arrays.toString(supervisorTags) + "]";
	}

	@Override
	public int compareTo(SiteInfo o) {
		String ip1 = this.getIp();
		String ip2 = o.getIp();
		String[] ip1arr = ip1.split("\\.");
		String[] ip2arr = ip2.split("\\.");
		try {
			if (!RegexUtil.matchIP(ip1) || !RegexUtil.matchIP(ip2))
				return 0;
			// 192.168.88.14 192.168.89.14
			else if(Integer.valueOf(ip1arr[2]).equals(Integer.valueOf(ip2arr[2]))){
				return Integer.valueOf(ip1arr[3]) - Integer.valueOf(ip2arr[3]);
			}else {
				return Integer.valueOf(ip1arr[2]) - Integer.valueOf(ip2arr[2]);
			}
		} catch (Exception e) {
			return 1;
		}
	}

	/**
	 * @return the probeissue
	 */
	public Boolean getProbeissue() {
		return probeissue;
	}

	/**
	 * @param probeissue the probeissue to set
	 */
	public void setProbeissue(Boolean probeissue) {
		this.probeissue = probeissue;
	}
	

}
