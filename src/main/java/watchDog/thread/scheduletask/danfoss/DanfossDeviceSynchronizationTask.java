package watchDog.thread.scheduletask.danfoss;

import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import watchDog.bean.constant.CommonConstants;
import watchDog.danfoss.model.Supervisor;
import watchDog.danfoss.service.DeviceService;
import watchDog.danfoss.service.SupervisorService;
import watchDog.danfoss.service.impl.DeviceServiceImpl;
import watchDog.danfoss.service.impl.SupervisorServiceImpl;
import watchDog.thread.scheduletask.BaseTask;

/**
 * Description:
 * @author Matthew Xu
 * @date May 29, 2020
 */
public class DanfossDeviceSynchronizationTask extends TimerTask implements BaseTask{
	
	public static final DanfossDeviceSynchronizationTask INSTANCE = new DanfossDeviceSynchronizationTask();
	
	private static final Logger logger = Logger.getLogger(DanfossDeviceSynchronizationTask.class);
	
	private DanfossDeviceSynchronizationTask(){}
	
	public static final long RUNNING_PERIOD = CommonConstants.ONE_HOUR;
	
	private SupervisorService supervisorService = SupervisorServiceImpl.getInstance();
	
	private DeviceService deviceService = DeviceServiceImpl.getInstance();
	
	@Override
	public void run() {
		try {
			BaseTask.getStartLog(LOGGER, this.getClass().getName());
			List<Supervisor> supervisors = supervisorService.findAll();
			for (Supervisor supervisor : supervisors) {
				deviceService.storeDevices(supervisor.getIp());
			}
		} catch (Exception e) {
			logger.error("" ,e);
		}finally {
			BaseTask.getEndLog(LOGGER, this.getClass().getName());
		}
	}

}
