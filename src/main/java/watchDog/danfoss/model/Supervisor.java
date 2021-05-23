
package watchDog.danfoss.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

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
	private List<Alarm> alarms;

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

	@Override
	public String toString() {
		return "Supervisor [id=" + id + ", name=" + name + ", ip=" + ip + ", devices=" + devices + "]";
	}

	
}
