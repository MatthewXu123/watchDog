
package watchDog.danfoss.model;

import watchDog.danfoss.enums.DeviceType;

/**
 * Description:
 * @author Matthew Xu
 * @date Apr 19, 2021
 */
public class Device {

	private String name;
	
	// model + version
	// e.g, 080Z0124_012x
	private String id;
	
	private DeviceType type;
	
	// Number of suction groups configured for the pack/rack group(PACK_ONLY or RACK_ONLY devices)
	private int numSuction;
	
	// ID to link sunction group back to the pack/rack group device
	private int rackId;
	
	// Text description of device attribute 'node'.
	// If nodetype <> 16 then description is in format of node-mod.point like '2.101'.
	private String addr;
	
	// 0 = has no active alarms, 1 = has active alarms.
	private boolean hasActiveAlarms;
	
	// Indicates if condenser is associated with the rack device.
	// 0 = no, 1 = yes.
	private boolean isCondenserWithRack;
	
	// setpoint
	private String ctrlVal;
	
	// 0 = Not in defrost, 1 = In defrost.
	private boolean isDefrosting;
	
	// Module address.
	// Will be 0 when nodetype = 16.
	private int moduleAddr;
	
	// Return device model and software version for devices with nodetype = 16.
	private String modelName;
	
	// User's defined name of device.
	private String multicaseName;
	
	// Address of the device on field bus.
	private int node;
	
	private int nodeType;
	
	// Communication status of device on field bus.
	// 0 = Offline, 1 = Online.
	private boolean isOnline;
	
	// Point address.
	// Will be 0 when nodetype = 16.
	private int point;
	
	// Index to link devices to the SC/SM grouping(RACK_ONLY/PACK_ONLY).
	private int rackId2;
	
	// For nodetype = 16, 0 = Case not in refrigeration nor defrost, 1 = runnning or in defrost.
	private boolean isRunningOrDefrosting;
	
	// Current status message of the device.
	private String status;
	
	// Index to link devices to the SC/SM grouping(PACK/RACK).
	private int suctionId;
	
	// current reading of device.
	private String value;
	
	
}
