package watchDog.danfoss.service;

public interface NotificationService extends BaseService{

	boolean sendActiveAlarms(String ip);
}
