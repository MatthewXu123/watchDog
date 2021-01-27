

package watchDog.thread.scheduletask;

import java.util.Arrays;
import java.util.Date;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import watchDog.bean.constant.CommonConstants;
import watchDog.controller.FileController;
import watchDog.service.FileSevice;
import watchDog.util.CSVUtils;
import watchDog.util.DateTool;

/**
 * Description:
 * @author Matthew Xu
 * @date May 29, 2020
 */
public class MemberExportTask extends TimerTask implements BaseTask{
	
	private static final Logger LOGGER = Logger.getLogger(MemberExportTask.class);
	
	public static final MemberExportTask INSTANCE = new MemberExportTask();
	
	public static final long RUNNING_PERIOD = CommonConstants.ONE_DAY;
	
	private static final String EXPORT_PATH = "C:\\watchDog\\files\\members";
	
	private MemberExportTask(){}
	
	@Override
	public void run() {
		try {
			
			BaseTask.getStartLog(LOGGER, this.getClass().getName());
			CSVUtils.createCSVFile(Arrays.asList(FileController.MEMBER_HEADERS), FileSevice.INSTANCE.getSiteMemberList(),
					EXPORT_PATH, DateTool.format(new Date(), "yyyyMMddmm") + "members");
		} catch (Exception e) {
			LOGGER.error("",e);
		}finally {
			BaseTask.getEndLog(LOGGER, this.getClass().getName());
		}
	}
	
}
