
package watchDog.danfoss.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
	
}
