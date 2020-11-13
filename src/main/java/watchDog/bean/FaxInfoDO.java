package watchDog.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import watchDog.bean.config.AlarmFaxRuleDTO;
import watchDog.util.ObjectUtils;
import watchDog.wechat.bean.WechatMember;

public class FaxInfoDO implements Serializable{

	private static final long serialVersionUID = 1L;

	private int idsite;
	
	private String ipaddress;
	
	private String sitename;
	
	private String agentId;
	
	private int repeatedTimes;
	
	private List<String> idalarmList;
	
	private Set<Device> devices;
	
	private List<WechatMember> wechatMemberList;
	
	private Date lastCallTime;
	
	private AlarmFaxRuleDTO alarmFaxRuleDTO;
	
	public FaxInfoDO() {
		super();
	}
	
	public int getIdsite() {
		return idsite;
	}

	public void setIdsite(int idsite) {
		this.idsite = idsite;
	}

	public String getSitename() {
		return sitename;
	}

	public void setSitename(String sitename) {
		this.sitename = sitename;
	}

	public List<WechatMember> getWechatMemberList() {
		return wechatMemberList;
	}

	public void setWechatMemberList(List<WechatMember> wechatMemberList) {
		this.wechatMemberList = wechatMemberList;
	}

	public Date getLastCallTime() {
		return lastCallTime;
	}

	public void setLastCallTime(Date lastCallTime) {
		this.lastCallTime = lastCallTime;
	}

	public AlarmFaxRuleDTO getAlarmFaxRuleDTO() {
		return alarmFaxRuleDTO;
	}

	public void setAlarmFaxRuleDTO(AlarmFaxRuleDTO alarmFaxRuleDTO) {
		this.alarmFaxRuleDTO = alarmFaxRuleDTO;
	}

	public Set<Device> getDevices() {
		return devices;
	}

	public void setDevices(Set<Device> devices) {
		this.devices = devices;
	}

	public int getRepeatedTimes() {
		return repeatedTimes;
	}

	public void setRepeatedTimes(int repeatedTimes) {
		this.repeatedTimes = repeatedTimes;
	}
	
	public void updateRepeatedTimes(){
		this.repeatedTimes ++;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	
	
	public List<String> getIdalarmList() {
		return idalarmList;
	}

	public void setIdalarmList(List<String> idalarmList) {
		this.idalarmList = idalarmList;
	}

	public boolean isWithMobile(){
		return ObjectUtils.isCollectionNotEmpty(this.wechatMemberList);
	}
	
	public boolean isCallTimesNotEnough(){
		int maxCallTimes = this.getAlarmFaxRuleDTO().getMaxCallTimes();
		return maxCallTimes == 0 ? true : (this.repeatedTimes < maxCallTimes ? true : false);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getAlarmFaxRuleDTO() == null) ? 0 : this.getAlarmFaxRuleDTO().hashCode());
		result = prime * result + this.getIdsite();
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
		FaxInfoDO other = (FaxInfoDO) obj;
		if (this.getAlarmFaxRuleDTO() == null) {
			if (other.getAlarmFaxRuleDTO() != null)
				return false;
		} else if (!this.getAlarmFaxRuleDTO().equals(other.getAlarmFaxRuleDTO()))
			return false;
		if (this.getIdsite() != other.getIdsite())
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "FaxInfoDO [sitename=" + sitename + ", repeatedTimes=" + repeatedTimes + ", idalarmList=" + idalarmList
				+ ", devices=" + devices + ", lastCallTime=" + lastCallTime + ", alarmFaxRuleDTO=" + alarmFaxRuleDTO
				+ "]";
	}

}
