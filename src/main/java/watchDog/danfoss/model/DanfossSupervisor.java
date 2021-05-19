
package watchDog.danfoss.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Description:
 * @author Matthew Xu
 * @date May 17, 2021
 */
@Entity
@Table(name = "supervisor")
public class DanfossSupervisor {

	private int id;
	
	private String name;
	
	private String ip;
	
	@OneToMany(mappedBy = "danfossSupervisor")
	private List<DanfossDevice> devices;

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

	public List<DanfossDevice> getDevices() {
		return devices;
	}

	public void setDevices(List<DanfossDevice> devices) {
		this.devices = devices;
	}
	
}
