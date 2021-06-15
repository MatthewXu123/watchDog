package watchDog.danfoss.service.impl;

import java.util.List;

import org.apache.log4j.Logger;

import watchDog.danfoss.model.Alarm;
import watchDog.danfoss.model.Supervisor;
import watchDog.danfoss.service.NotificationService;
import watchDog.wechat.bean.WechatMsg;

public class NotificationSeviceImpl implements NotificationService {

	// singleton
	private static NotificationSeviceImpl INSTANCE;

	public static NotificationSeviceImpl getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new NotificationSeviceImpl();
		}
		return INSTANCE;
	}

	private NotificationSeviceImpl() {
	}

	private static final Logger logger = Logger.getLogger(NotificationSeviceImpl.class);

	@Override
	public boolean sendActiveAlarms(String ip) {
		List<Alarm> alarms = ALARM_SERVICE.getActiveAlarmsFromXMLAndStoreAlarms(ip);
		for (Alarm alarm : alarms) {
			Supervisor supervisor = alarm.getSupervisor();
			SENDER_WECHAT.sendIM(new WechatMsg.Builder(
					PROPERTY_CONFIG.getValue(getQueryPropertiesKeyOnlyMethod(), new Object[]{supervisor.getName(), alarm.getName(), alarm.getDevice().getName()})
					, supervisor.getAgentId()
					, new String[]{supervisor.getSoldierDeptId(), supervisor.getOfficerDeptId()}).build());
		}
		return false;
	}
	
	public static void main(String[] args) {
		NotificationSeviceImpl notificationSeviceImpl = new NotificationSeviceImpl();
		notificationSeviceImpl.sendActiveAlarms("47.99.193.207");
	}

}
