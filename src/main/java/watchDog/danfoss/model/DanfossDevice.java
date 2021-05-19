
package watchDog.danfoss.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import watchDog.danfoss.enums.DeviceType;

/**
 * Description:
 * @author Matthew Xu
 * @date Apr 19, 2021
 */
@Entity
@Table(name = "device")
public class DanfossDevice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "supervisorId", insertable = false, updatable = false)
	private DanfossSupervisor danfossSupervisor;
	
	private String name;
	
	// model + '_' +  version
	// e.g, 080Z0124_012x
	private String deviceId;
	
	private DeviceType type;
	
	// Number of suction groups configured for the pack/rack group(PACK_ONLY or RACK_ONLY devices)
	private Integer suctionNum;
	
	// ID to link sunction group back to the pack/rack group device
	private Integer rackId;
	
	// Text description of device attribute 'node'.
	// If nodetype <> 16 then description is in format of node-mod.poInteger like '2.101'.
	private String addr;
	
	// 0 = has no active alarms, 1 = has active alarms.
	private Boolean hasActiveAlarms;
	
	// Indicates if condenser is associated with the rack device.
	// 0 = no, 1 = yes.
	private Boolean isCondenserWithRack;
	
	// setpoInteger
	private String ctrlVal;
	
	// 0 = Not in defrost, 1 = In defrost.
	private Boolean isDefrosting;
	
	// Module address.
	// Will be 0 when nodetype = 16.
	private Integer moduleAddr;
	
	// Return device model and software version for devices with nodetype = 16.
	private String modelName;
	
	// User's defined name of device.
	private String multicaseName;
	
	// Address of the device on field bus.
	private Integer node;
	
	private Integer nodeType;
	
	// Communication status of device on field bus.
	// 0 = Offline, 1 = Online.
	private Boolean isOnline;
	
	// PoInteger address.
	// Will be 0 when nodetype = 16.
	private Integer point;
	
	// Index to link devices to the SC/SM grouping(RACK_ONLY/PACK_ONLY).
	private Integer rackId2;
	
	// For nodetype = 16, 0 = Case not in refrigeration nor defrost, 1 = runnning or in defrost.
	private Boolean isRunningOrDefrosting;
	
	// Current status message of the device.
	private String status;
	
	// Index to link devices to the SC/SM grouping(PACK/RACK).
	private Integer suctionId;
	
	// current reading of device.
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	public Boolean getIsCondenserWithRack() {
		return isCondenserWithRack;
	}

	public void setIsCondenserWithRack(Boolean isCondenserWithRack) {
		this.isCondenserWithRack = isCondenserWithRack;
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

	public Boolean getIsRunningOrDefrosting() {
		return isRunningOrDefrosting;
	}

	public void setIsRunningOrDefrosting(Boolean isRunningOrDefrosting) {
		this.isRunningOrDefrosting = isRunningOrDefrosting;
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

	@Override
	public String toString() {
		return "DanfossDevice [id=" + id + ", name=" + name + ", deviceId=" + deviceId + ", type=" + type
				+ ", suctionNum=" + suctionNum + ", rackId=" + rackId + ", addr=" + addr + ", hasActiveAlarms="
				+ hasActiveAlarms + ", isCondenserWithRack=" + isCondenserWithRack + ", ctrlVal=" + ctrlVal
				+ ", isDefrosting=" + isDefrosting + ", moduleAddr=" + moduleAddr + ", modelName=" + modelName
				+ ", multicaseName=" + multicaseName + ", node=" + node + ", nodeType=" + nodeType + ", isOnline="
				+ isOnline + ", point=" + point + ", rackId2=" + rackId2 + ", isRunningOrDefrosting="
				+ isRunningOrDefrosting + ", status=" + status + ", suctionId=" + suctionId + ", value=" + value + "]";
	}
	
}
