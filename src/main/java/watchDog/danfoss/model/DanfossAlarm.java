
package watchDog.danfoss.model;

import javax.persistence.Table;

import watchDog.danfoss.enums.AlarmType;

/**
 * Description:
 * @author Matthew Xu
 * @date May 17, 2021
 */
//@Entity
@Table(name = "alarm")
public class DanfossAlarm {

	private int alarmId;
	
	private String name;
	
	private AlarmType alarmType;
	
	
}
