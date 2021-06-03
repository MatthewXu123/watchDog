
package watchDog.danfoss.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;

import watchDog.danfoss.enums.AlarmType;

/**
 * Description:
 * @author Matthew Xu
 * @date May 17, 2021
 */
@Entity
public class Alarm {
	
	@Id
	private int id;
	
	private String name;
	
	@Enumerated(value = EnumType.STRING)
	@Column(name = "alarm_type")
	private AlarmType alarmType;
	
	@ManyToOne
	@JoinColumn(name = "device_id", referencedColumnName = "id")
	private Device device;
	
	@ManyToOne
	@JoinColumn(name = "supervisor_id", referencedColumnName = "id")
	private Supervisor supervisor;
	
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

	public AlarmType getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(AlarmType alarmType) {
		this.alarmType = alarmType;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Supervisor getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(Supervisor supervisor) {
		this.supervisor = supervisor;
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
		result = prime * result + id;
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
		Alarm other = (Alarm) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Alarm [id=" + id + ", name=" + name + ", alarmType=" + alarmType + ", createTime=" + createTime
				+ ", updateTime=" + updateTime + "]";
	}
	
}
