
package watchDog.bean.config;

/**
 * Description:
 * @author Matthew Xu
 * @date May 21, 2020
 */
public class AlarmFaxRuleDTO{

	private String alarmCode;
	
	private String description;
	
	private int count;
	
	// The delay for calls.
	private int minCallDelay;
	
	// The delay for queries in the day.
	private int dayQueryDelay;
	
	// The delay for queries in the night.
	private int nightQueryDelay;
	
	// Here 0 represents no limit.
	private int maxCallTimes;
	
	public String getAlarmCode() {
		return alarmCode;
	}

	public void setAlarmCode(String alarmCode) {
		this.alarmCode = alarmCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getMinCallDelay() {
		return minCallDelay;
	}

	public void setMinCallDelay(int minCallDelay) {
		this.minCallDelay = minCallDelay;
	}

	public int getMaxCallTimes() {
		return maxCallTimes;
	}

	public void setMaxCallTimes(int maxCallTimes) {
		this.maxCallTimes = maxCallTimes;
	}
	
	public int getDayQueryDelay() {
		return dayQueryDelay;
	}

	public void setDayQueryDelay(int dayQueryDelay) {
		this.dayQueryDelay = dayQueryDelay;
	}

	public int getNightQueryDelay() {
		return nightQueryDelay;
	}

	public void setNightQueryDelay(int nightQueryDelay) {
		this.nightQueryDelay = nightQueryDelay;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alarmCode == null) ? 0 : alarmCode.hashCode());
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
		AlarmFaxRuleDTO other = (AlarmFaxRuleDTO) obj;
		if (alarmCode == null) {
			if (other.alarmCode != null)
				return false;
		} else if (!alarmCode.equals(other.alarmCode))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return alarmCode + "," + maxCallTimes;
	}

}
