package watchDog.thread.scheduletask;

import java.util.TimerTask;

import org.apache.log4j.Logger;

import watchDog.service.SimpleCallingService;
import watchDog.thread.AlarmNotificationThread;

public class SimpleCallingTask extends TimerTask {
    public static SimpleCallingTask me = null;
    private static final Logger logger = Logger.getLogger(AlarmNotificationThread.class);
    public static SimpleCallingTask getInstance()
    {
        if(me == null)
            me = new SimpleCallingTask();
        return me;
    }
    @Override
    public void run() {
        logger.info("SimpleCallingTask start");
        // TODO Auto-generated method stub
        try{
            SimpleCallingService.getInstance().start();
        }catch(Exception ex)
        {
            logger.error("",ex);
        }
        logger.info("SimpleCallingTask end");
    }

}
