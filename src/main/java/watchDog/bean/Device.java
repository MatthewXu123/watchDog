
package watchDog.bean;

/**
 * Description:
 * @author Matthew Xu
 * @date Jun 11, 2020
 */
public class Device implements Comparable<Device>{

	private int kidsupervisor;
	
	private int iddevice;
	
	private String description;

	public Device() {
		super();
	}
	
	/**
	 * @param kidsupervisor
	 * @param iddevice
	 * @param description
	 */
	public Device(int kidsupervisor, int iddevice, String description) {
		super();
		this.kidsupervisor = kidsupervisor;
		this.iddevice = iddevice;
		this.description = description;
	}

	public int getIddevice() {
		return iddevice;
	}

	public void setIddevice(int iddevice) {
		this.iddevice = iddevice;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getKidsupervisor() {
		return kidsupervisor;
	}
	
	public void setKidsupervisor(int kidsupervisor) {
		this.kidsupervisor = kidsupervisor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + iddevice;
		result = prime * result + kidsupervisor;
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
		if (iddevice != other.iddevice)
			return false;
		if (kidsupervisor != other.kidsupervisor)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return kidsupervisor + "," + iddevice + "," + description;
	}

	@Override
	public int compareTo(Device o) {
		if(this.kidsupervisor == o.kidsupervisor){
			return this.iddevice - o.iddevice;
		}else {
			return this.kidsupervisor - o.kidsupervisor;
		}
	}
	
}
