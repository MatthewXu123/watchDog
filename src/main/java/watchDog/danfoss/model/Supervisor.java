
package watchDog.danfoss.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CreationTimestamp;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Description:
 * @author Matthew Xu
 * @date May 17, 2021
 */
@Entity
public class Supervisor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String name;
	
	@Column(unique = true)
	private String ip;
	
	@OneToMany(mappedBy = "supervisor", fetch = FetchType.LAZY)
	private List<Device> devices;
	
	@OneToMany(mappedBy = "supervisor", fetch = FetchType.LAZY)
	@JSONField(serialize = false)
	private List<Alarm> alarms;
	
	@Column(name = "soldier_dept_id")
	private String soldierDeptId;
	
	@Column(name = "officer_dept_id")
	private String officerDeptId;
	
	@Column(name = "agent_id")
	private String agentId = "6";
	
	@Column(name = "create_time", updatable = false)
	@CreationTimestamp
	private Date createTime;
	
	@Column(name = "update_time")
	@CreationTimestamp
	private Date updateTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public List<Device> getDevices() {
		return devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}
	
	public List<Alarm> getAlarms() {
		return alarms;
	}

	public void setAlarms(List<Alarm> alarms) {
		this.alarms = alarms;
	}

	public String getSoldierDeptId() {
		return soldierDeptId;
	}

	public void setSoldierDeptId(String soldierDeptId) {
		this.soldierDeptId = soldierDeptId;
	}

	public String getOfficerDeptId() {
		return officerDeptId;
	}

	public void setOfficerDeptId(String officerDeptId) {
		this.officerDeptId = officerDeptId;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "Supervisor [id=" + id + ", name=" + name + ", ip=" + ip + ", devices=" + devices + ", alarms=" + alarms
				+ ", soldierDeptId=" + soldierDeptId + ", officerDeptId=" + officerDeptId + ", agentId=" + agentId
				+ ", createTime=" + createTime + ", updateTime=" + updateTime + "]";
	}

}
