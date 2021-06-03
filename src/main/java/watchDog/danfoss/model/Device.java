
package watchDog.danfoss.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CreationTimestamp;

import watchDog.danfoss.enums.DeviceType;

/**
 * Description:
 * @author Matthew Xu
 * @date Apr 19, 2021
 */
@Entity
public class Device {

	@Id
	private String id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "supervisor_id", referencedColumnName = "id")
	private Supervisor supervisor;
	
	private String name;
	
	// model + '_' +  version
	// e.g, 080Z0124_012x
	@Column(name = "device_id")
	private String deviceId;
	
	@Enumerated(value = EnumType.STRING)
	private DeviceType type;
	
	// Number of suction groups configured for the pack/rack group(PACK_ONLY or RACK_ONLY devices)
	@Column(name = "suction_num")
	private Integer suctionNum;
	
	// ID to link sunction group back to the pack/rack group device
	@Column(name = "rack_id")
	private Integer rackId;
	
	// Text description of device attribute 'node'.
	// If nodetype <> 16 then description is in format of node-mod.poInteger like '2.101'.
	private String addr;
	
	// 0 = has no active alarms, 1 = has active alarms.
	@Column(name = "has_active_alarms")
	private Boolean hasActiveAlarms;
	
	// Indicates if condenser is associated with the rack device.
	// 0 = no, 1 = yes.
	@Column(name = "is_cond_with_rack")
	private Boolean isCondWithRack;
	
	// setpoInteger
	@Column(name = "ctrl_val")
	private String ctrlVal;
	
	// 0 = Not in defrost, 1 = In defrost.
	@Column(name = "is_defrosting")
	private Boolean isDefrosting;
	
	// Module address.
	// Will be 0 when nodetype = 16.
	@Column(name = "module_addr")
	private Integer moduleAddr;
	
	// Return device model and software version for devices with nodetype = 16.
	@Column(name = "model_name")
	private String modelName;
	
	// User's defined name of device.
	@Column(name = "multicase_name")
	private String multicaseName;
	
	// Address of the device on field bus.
	private Integer node;
	
	@Column(name = "node_type")
	private Integer nodeType;
	
	// Communication status of device on field bus.
	// 0 = Offline, 1 = Online.
	@Column(name = "is_online")
	private Boolean isOnline;
	
	// PoInteger address.
	// Will be 0 when nodetype = 16.
	private Integer point;
	
	// Index to link devices to the SC/SM grouping(RACK_ONLY/PACK_ONLY).
	@Column(name = "rack_id2")
	private Integer rackId2;
	
	// For nodetype = 16, 0 = Case not in refrigeration nor defrost, 1 = runnning or in defrost.
	@Column(name = "is_run_or_defrost")
	private Boolean isRunOrDefrost;
	
	// Current status message of the device.
	private String status;
	
	// Index to link devices to the SC/SM grouping(PACK/RACK).
	@Column(name = "suction_id")
	private Integer suctionId;
	
	// current reading of device.
	private String value;
	
	@OneToMany(mappedBy = "device", fetch = FetchType.LAZY)
	private List<Alarm> alarms;
	
	@Column(name = "create_time", updatable = false)
	@CreationTimestamp
	private Date createTime;
	
	@Column(name = "update_time")
	@CreationTimestamp
	private Date updateTime;

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

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public DeviceType getType() {
		return type;
	}

	public void setType(DeviceType type) {
		this.type = type;
	}

	public Integer getSuctionNum() {
		return suctionNum;
	}

	public void setSuctionNum(Integer suctionNum) {
		this.suctionNum = suctionNum;
	}

	public Integer getRackId() {
		return rackId;
	}

	public void setRackId(Integer rackId) {
		this.rackId = rackId;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public Boolean getHasActiveAlarms() {
		return hasActiveAlarms;
	}

	public void setHasActiveAlarms(Boolean hasActiveAlarms) {
		this.hasActiveAlarms = hasActiveAlarms;
	}

	public String getCtrlVal() {
		return ctrlVal;
	}

	public void setCtrlVal(String ctrlVal) {
		this.ctrlVal = ctrlVal;
	}

	public Boolean getIsDefrosting() {
		return isDefrosting;
	}

	public void setIsDefrosting(Boolean isDefrosting) {
		this.isDefrosting = isDefrosting;
	}

	public Integer getModuleAddr() {
		return moduleAddr;
	}

	public void setModuleAddr(Integer moduleAddr) {
		this.moduleAddr = moduleAddr;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getMulticaseName() {
		return multicaseName;
	}

	public void setMulticaseName(String multicaseName) {
		this.multicaseName = multicaseName;
	}

	public Integer getNode() {
		return node;
	}

	public void setNode(Integer node) {
		this.node = node;
	}

	public Integer getNodeType() {
		return nodeType;
	}

	public void setNodeType(Integer nodeType) {
		this.nodeType = nodeType;
	}

	public Boolean getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(Boolean isOnline) {
		this.isOnline = isOnline;
	}

	public Integer getPoint() {
		return point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

	public Integer getRackId2() {
		return rackId2;
	}

	public void setRackId2(Integer rackId2) {
		this.rackId2 = rackId2;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getSuctionId() {
		return suctionId;
	}

	public void setSuctionId(Integer suctionId) {
		this.suctionId = suctionId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public Supervisor getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(Supervisor supervisor) {
		this.supervisor = supervisor;
	}
	
	public Boolean getIsCondWithRack() {
		return isCondWithRack;
	}

	public void setIsCondWithRack(Boolean isCondWithRack) {
		this.isCondWithRack = isCondWithRack;
	}

	public Boolean getIsRunOrDefrost() {
		return isRunOrDefrost;
	}

	public void setIsRunOrDefrost(Boolean isRunOrDefrost) {
		this.isRunOrDefrost = isRunOrDefrost;
	}

	public List<Alarm> getAlarms() {
		return alarms;
	}

	public void setAlarms(List<Alarm> alarms) {
		this.alarms = alarms;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((moduleAddr == null) ? 0 : moduleAddr.hashCode());
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		result = prime * result + ((nodeType == null) ? 0 : nodeType.hashCode());
		result = prime * result + ((point == null) ? 0 : point.hashCode());
		result = prime * result + ((supervisor == null) ? 0 : supervisor.hashCode());
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
		Device other = (Device) obj;
		if (moduleAddr == null) {
			if (other.moduleAddr != null)
				return false;
		} else if (!moduleAddr.equals(other.moduleAddr))
			return false;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		if (nodeType == null) {
			if (other.nodeType != null)
				return false;
		} else if (!nodeType.equals(other.nodeType))
			return false;
		if (point == null) {
			if (other.point != null)
				return false;
		} else if (!point.equals(other.point))
			return false;
		if (supervisor == null) {
			if (other.supervisor != null)
				return false;
		} else if (!supervisor.equals(other.supervisor))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Device [id=" + id + ", name=" + name + ", deviceId=" + deviceId + ", type=" + type + ", suctionNum="
				+ suctionNum + ", rackId=" + rackId + ", addr=" + addr + ", hasActiveAlarms=" + hasActiveAlarms
				+ ", isCondWithRack=" + isCondWithRack + ", ctrlVal=" + ctrlVal + ", isDefrosting=" + isDefrosting
				+ ", moduleAddr=" + moduleAddr + ", modelName=" + modelName + ", multicaseName=" + multicaseName
				+ ", node=" + node + ", nodeType=" + nodeType + ", isOnline=" + isOnline + ", point=" + point
				+ ", rackId2=" + rackId2 + ", isRunOrDefrost=" + isRunOrDefrost + ", status=" + status + ", suctionId="
				+ suctionId + ", value=" + value + ", alarms=" + alarms + ", createTime=" + createTime + ", updateTime="
				+ updateTime + "]";
	}

}
