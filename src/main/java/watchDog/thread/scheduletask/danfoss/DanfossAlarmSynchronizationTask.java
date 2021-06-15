package watchDog.thread.scheduletask.danfoss;

import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import watchDog.bean.constant.CommonConstants;
import watchDog.danfoss.model.Supervisor;
import watchDog.danfoss.service.NotificationService;
import watchDog.danfoss.service.SupervisorService;
import watchDog.danfoss.service.impl.NotificationSeviceImpl;
import watchDog.danfoss.service.impl.SupervisorServiceImpl;
import watchDog.thread.scheduletask.BaseTask;

/**
 * Description:
 * @author Matthew Xu
 * @date May 29, 2020
 */
public class DanfossAlarmSynchronizationTask extends TimerTask implements BaseTask{
	
	public static final DanfossAlarmSynchronizationTask INSTANCE = new DanfossAlarmSynchronizationTask();
	
	private static final Logger logger = Logger.getLogger(DanfossAlarmSynchronizationTask.class);
	
	private DanfossAlarmSynchronizationTask(){}
	
	public static final long RUNNING_PERIOD = CommonConstants.ONE_MINUTE * 10;
	
	private SupervisorService supervisorService = SupervisorServiceImpl.getInstance();
	
	private NotificationService notificationService = NotificationSeviceImpl.getInstance();
	
	
	@Override
	public void run() {
		try {
			BaseTask.getStartLog(LOGGER, this.getClass().getName());
			List<Supervisor> supervisors = supervisorService.findAll();
			for (Supervisor supervisor : supervisors) {
				notificationService.sendActiveAlarms(supervisor.getIp());
			}
		} catch (Exception e) {
			logger.error("" ,e);
		}finally {
			BaseTask.getEndLog(LOGGER, this.getClass().getName());
		}
	}

}
