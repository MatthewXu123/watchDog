package watchDog.service;

import static watchDog.bean.constant.CommonConstants.ONE_HOUR;
import static watchDog.bean.constant.CommonConstants.ONE_MINUTE;
import static watchDog.property.template.WechatReportMsgTemplate.MONTH_REPORT_TITLE;
import static watchDog.property.template.WechatReportMsgTemplate.WEEK_REPORT_TITLE;
import static watchDog.property.template.WechatReportMsgTemplate.getMonthlyReportContent;
import static watchDog.property.template.WechatReportMsgTemplate.getMonthlyReportHQContent;
import static watchDog.property.template.WechatReportMsgTemplate.getMonthlyReportTitle;
import static watchDog.property.template.WechatReportMsgTemplate.getWeeklyReportContent;
import static watchDog.property.template.WechatReportMsgTemplate.getWeeklyReportHQContent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

import me.chanjar.weixin.common.util.StringUtils;
import watchDog.bean.Property;
import watchDog.bean.SiteInfo;
import watchDog.config.json.UnitConfig;
import watchDog.dao.TagDAO;
import watchDog.database.DataBaseException;
import watchDog.database.DatabaseMgr;
import watchDog.database.RecordSet;
import watchDog.listener.Dog;
import watchDog.thread.AlarmNotificationMain;
import watchDog.thread.WechatApplicationThread;
import watchDog.thread.scheduletask.AlarmFaxInfoCheckTask;
import watchDog.thread.scheduletask.MailTask;
import watchDog.thread.scheduletask.MemberExportTask;
import watchDog.thread.scheduletask.SimpleCallingTask;
import watchDog.thread.scheduletask.WechatDeptCheckTask;
import watchDog.thread.scheduletask.WechatMemberCheckTask;
import watchDog.util.DateTool;
import watchDog.wechat.bean.WechatMsg;
import watchDog.wechat.bean.WechatUser;
import watchDog.wechat.util.sender.Sender;

public class ScheduledTask {
  public static final String TAG_ONOFF_IGNORE = "#onoff_ignore";
  private static final Logger logger = Logger.getLogger(ScheduledTask.class);
  
  public static int runTimeForWeek = 0;
  
  public static int runTimeForMonth = 0;
  
  Timer timerDummy = null;
  
  Timer timer = new Timer("ReportTimer");
  
  private static final long PERIOD_MAINTAINER_CHECK_TASK = ONE_HOUR * 24;
  
  private static final long PERIOD_DEPT_CHECK_TASK = ONE_MINUTE * 10;
  
  private static final long DELAY_UNIT_OFF_CHECK = ONE_HOUR * 1;
  
  private static final long PERIOD_UNIT_OFF_CHECK = ONE_HOUR * 1;
  
  private static final long DELAY_ALARM_SYNCHRONIZATION = 0;
  
  private static final long PERIOD_ALARM_SYNCHRONIZATION = ONE_HOUR * 12;
  
  private static final long DELAY_ALARM_FAXINFO_CHECK = 0;
  
  private static final long PERIOD_ALARM_FAXINFO_CHECK = ONE_MINUTE * 1;
  
  private static final Sender sender = Sender.getInstance();
  
  private static final WechatApplicationThread WECHAT_APPLICATION_THREAD = Dog.getInstance().getWechatApplicationThread();
  
  public ScheduledTask() {
	logger.info("ScheduledTask start...");
	// timer for report
//    Calendar c = DateTool.getInstanceDate(9, 0, 0);
//    c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
//    c.add(Calendar.DATE, 7);
//    Date nextTime = c.getTime();
//    Property reportStr = PropertyMgr.getInstance().getProperty("is_first_report");
//    boolean isFirstReport = !(reportStr != null && reportStr.getValue() != null && reportStr.getValue().length() != 0 && !"true".equals(reportStr.getValue()));
//    if (isFirstReport) {
//      this.timerDummy = new Timer();
//      this.timerDummy.schedule(new WeeklyReportManager(), 10*60*1000);
//    } 
    //this.timer.schedule(new WeeklyReportManager(), nextTime, 1000*3600*24*7);
    
    Timer timerAlarmSyncTimer = new Timer("AlarmSynchronizeTimer");
    timerAlarmSyncTimer.schedule(new AlarmSynchronizeManager(), DELAY_ALARM_SYNCHRONIZATION, PERIOD_ALARM_SYNCHRONIZATION);
    
    Timer unitOFFTimer = new Timer("unitOFFTimer");
    unitOFFTimer.schedule(new UnitOFFManager(), DELAY_UNIT_OFF_CHECK, PERIOD_UNIT_OFF_CHECK);
    
    Timer wechatDeptCheckTaskTimer = new Timer("WechatDeptCheckTaskTimer");
    wechatDeptCheckTaskTimer.schedule(WechatDeptCheckTask.INSTANCE, 0, PERIOD_DEPT_CHECK_TASK);
    
    Timer alarmFaxInfoCheckTaskTimer = new Timer("AlarmFaxInfoCheckTaskTimer");
    alarmFaxInfoCheckTaskTimer.schedule(AlarmFaxInfoCheckTask.INSTANCE, DELAY_ALARM_FAXINFO_CHECK, PERIOD_ALARM_FAXINFO_CHECK);
    
    Timer simpleCallingTaskTimer = new Timer("simpleCallingTaskTimer");
    simpleCallingTaskTimer.schedule(SimpleCallingTask.getInstance(), 5*60*1000,5*60*1000);
    
    Timer mailTaskTimer = new Timer("MailTaskTimer");
    Calendar mailTaskTime  = Calendar.getInstance();
    mailTaskTime.set(Calendar.DAY_OF_WEEK, 6);
    mailTaskTime.set(Calendar.HOUR_OF_DAY, 14);
    mailTaskTime.set(Calendar.MINUTE, 0);
    mailTaskTime.set(Calendar.SECOND, 0);
    mailTaskTime.set(Calendar.MILLISECOND, 0);
    mailTaskTimer.schedule(MailTask.INSTANCE, mailTaskTime.getTime(), MailTask.RUNNING_PERIOD);
    
    Timer memberExportTimer = new Timer("MemberExportTimer");
    memberExportTimer.schedule(MemberExportTask.INSTANCE, 0, MemberExportTask.RUNNING_PERIOD);
    
    Calendar wechatMemberCheckTaskTime  = Calendar.getInstance();
    wechatMemberCheckTaskTime.set(Calendar.HOUR_OF_DAY, 14);
    Timer wechatMemberCheckTaskTimer = new Timer("WechatMemberCheckTaskTimer");
    wechatMemberCheckTaskTimer.scheduleAtFixedRate(WechatMemberCheckTask.INSTANCE, wechatMemberCheckTaskTime.getTime(), WechatMemberCheckTask.RUNNING_PERIOD);
    
  }
  
  class WeeklyReportManager extends TimerTask {
    public void run() {
      try {
        logger.info("WeeklyReportManager running at " + DateTool.format(new Date()));
        Property reportStr = PropertyMgr.getInstance().getProperty("is_first_report");
        boolean isFirstReport = !(reportStr != null && reportStr.getValue() != null && reportStr.getValue().length() != 0 && !"true".equals(reportStr.getValue()));
        ScheduledTask.runTimeForWeek = isFirstReport ? 24 : 4;
        ScheduledTask.runTimeForMonth = isFirstReport ? 6 : 1;
        Calendar cm = Calendar.getInstance();
        boolean isFirstTimeOfWeek = false;
        if(cm.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
    		isFirstTimeOfWeek = true;
    	cm.set(Calendar.DAY_OF_MONTH, 1);
        int i = 1;
        while(cm.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
        	cm.set(Calendar.DAY_OF_MONTH, i++);
        Date firstOfMonth = cm.getTime();
        String str = DateTool.format(firstOfMonth, "yyyy-MM-dd");
        boolean isFirstOfMonth = DateTool.format(new Date(), "yyyy-MM-dd").equals(str);
        Property p = PropertyMgr.getInstance().getProperty("last_report_time");
        if (p == null || p.getValue() == null || p.getValue().length() == 0) {
          PropertyMgr.getInstance().update("last_report_time", DateTool.format(new Date(), "yyyy-MM-dd"));
        } else {
          if (isFirstTimeOfWeek)
            isFirstTimeOfWeek = !p.getValue().equals(DateTool.format(new Date(), "yyyy-MM-dd")); 
          if (isFirstOfMonth)
            isFirstOfMonth = !p.getValue().equals(str); 
        } 
        if (isFirstReport) {
          isFirstTimeOfWeek = true;
          isFirstOfMonth = true;
        } 
        Calendar c = DateTool.getInstance0();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.add(Calendar.DATE, -7);
        Date lastMonday = c.getTime();
        lastMonday = DateTool.add(lastMonday, 1, Calendar.DATE);
        c.add(Calendar.DATE, -7*runTimeForWeek);
        Date date = c.getTime();
        if (isFirstTimeOfWeek) {
          ScheduledTask.this.cleanOldData();
          logger.info("WeeklyReportManager first time week " + DateTool.format(new Date()));
          do {
        	Date dateTo = DateTool.add(date, 7, Calendar.DATE);
          	Date[] timeRange = {date,dateTo};
          	AlarmService.createSiteSLA("w", timeRange);
          	AlarmService.createSiteACK("w", timeRange);
          	TemperatureKPIService.createTempreatureACK("w", timeRange);
          	date = DateTool.add(date, 7, Calendar.DATE);
          } while (!date.after(lastMonday));
        } 
        if (isFirstOfMonth) {
          logger.info("WeeklyReportManager first time month " + DateTool.format(new Date()));
          cm = Calendar.getInstance();
          cm = Calendar.getInstance();
          cm.set(Calendar.DATE, 1);
          cm.set(Calendar.HOUR_OF_DAY, 0);
          cm.set(Calendar.MINUTE, 0);
          cm.set(Calendar.SECOND, 0);
          Date lastMonth = cm.getTime();
          lastMonth = DateTool.add(lastMonth, -1, Calendar.DATE);
          cm.add(Calendar.MONTH, -runTimeForMonth);
          date = cm.getTime();
          do {
        	cm.set(Calendar.DAY_OF_MONTH, cm.getActualMaximum(Calendar.DAY_OF_MONTH)+1);
          	Date dateTo = cm.getTime();
          	Date[] timeRange = {date,dateTo};
          	AlarmService.createSiteSLA("m", timeRange);
          	AlarmService.createSiteACK("m", timeRange);
            TemperatureKPIService.createTempreatureACK("m", timeRange);
            date = cm.getTime();
          } while (!date.after(lastMonth));
        } 
        if (isFirstReport) {
          PropertyMgr.getInstance().update("is_first_report", "false");
        } else {
          ScheduledTask.this.sendReportMonth(isFirstTimeOfWeek, isFirstOfMonth, cm);
        } 
        if (ScheduledTask.this.timerDummy != null) {
          ScheduledTask.this.timerDummy.cancel();
          ScheduledTask.this.timerDummy = null;
        } 
      } catch (Exception ex) {
        logger.error("", ex);
      } 
    }
  }
  
  private void sendReportMonth(boolean isFirstTimeOfWeek, boolean isFirstOfMonth, Calendar cm) {
    Iterator<Map.Entry> it = Dog.getInstance().getAllSites();
    cm.set(Calendar.WEEK_OF_MONTH, -1);
    logger.info("sending weekly/monthly manager " + DateTool.format(new Date()));
    while (it.hasNext()) {
      Map.Entry entry = it.next();
      String ident = (String)entry.getKey();
      SiteInfo info = Dog.getInstance().getSiteInfo(ident);
      if (info.getChannel() == 1) {
    	String tagId = info.getTagId();
    	String tagId2 = info.getTagId2();
    	String tagId3 = info.getTagId3();
        if (isFirstOfMonth) 
        	sender.sendIMReport(new WechatMsg.Builder(getMonthlyReportContent(info), info.getAgentId(), new String[]{tagId,tagId2})
        			  .title(getMonthlyReportTitle(info))
        			  .build());
        
        if (isFirstTimeOfWeek) 
        	sender.sendIMReport(new WechatMsg.Builder(getWeeklyReportContent(info), info.getAgentId(), new String[]{tagId,tagId2})
        			  .title(getWeeklyReportContent(info))
        			  .build());
        
      } 
    } 
    logger.info("sending weekly/monthly general " + DateTool.format(new Date()));
    List<WechatUser> generalWechatMember = Dog.getInstance().getWechatApplicationThread().getGeneralWechatMember();
    //String msgURLW = ShortURLMgr.getInstance().getReportHQ("w");
    //String msgURLM = ShortURLMgr.getInstance().getReportHQ("m");
    for (WechatUser wechatMember : generalWechatMember) {
    	if (isFirstOfMonth)
    		sender.sendIMReport(new WechatMsg.Builder(getMonthlyReportHQContent())
    				.userIds(new String[]{wechatMember.getUserid()})
    				.title(MONTH_REPORT_TITLE)
    				.build());
      	  //sender.sendIMReport(3, wechatMember.getUserid(), monthTitle, String.valueOf(msgPictureWX) + ";" + msgURLM); 
        if (isFirstTimeOfWeek)
      	  //sender.sendIMReport(3, wechatMember.getUserid(), weekTitle, String.valueOf(msgPictureWX) + ";" + msgURLW); 
        	sender.sendIMReport(new WechatMsg.Builder(getWeeklyReportHQContent())
        			.userIds(new String[]{wechatMember.getUserid()})
        			.title(WEEK_REPORT_TITLE)
        			.build());
	}
    PropertyMgr.getInstance().update("last_report_time", DateTool.format(new Date(), "yyyy-MM-dd"));
  }
  
  private void cleanOldData() {
    cleanOldDataPrivateAlarmImporant();
  }
  
  private void cleanOldDataPrivateAlarmImporant() {
    Date d = DateTool.addDays(-30);
    String sql = "delete from private_alarm_important where insert_time<='" + DateTool.format(d) + "'";
    try {
      DatabaseMgr.getInstance().executeUpdate(sql);
    } catch (DataBaseException e) {
      e.printStackTrace();
    } 
  }
  
	class AlarmSynchronizeManager extends TimerTask {
		public void run() {
			try {
				logger.info("AlarmSynchronizeManager running at " + DateTool.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
				Iterator<Map.Entry> it = Dog.getInstance().getAllSites();
				while (it.hasNext()) {
					Map.Entry en = it.next();
					SiteInfo site = (SiteInfo) en.getValue();
					if (!"boss".equals(site.getKtype())
							|| (StringUtils.isBlank(site.getTagId()) && StringUtils.isBlank(site.getTagId2())
							|| !site.getCheckNetwork()
							|| !site.getProbeissue()))
						continue;
					String ip = site.getIp();
					Integer supervisorId = site.getSupervisorId();
					AlarmSynchronizeService.doit(ip, supervisorId);
				}
			} catch (Exception ex) {
				logger.error("", ex);
			}finally{
				logger.info("AlarmSynchronizeManager finished at " + DateTool.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
			}
		}
	}

  class UnitOFFManager extends TimerTask {
    Dog dog;
    
    UnitOFFManager() {
      this.dog = Dog.getInstance();
    }
    
    public void run() {
      logger.info("UnitOFF running");
      try {
        String idents = this.dog.getIdents4AlarmChecking();
        Map<String, List<String>> active = checkSiteForDeviceONOFF(idents);
        AlarmNotificationMain.sendIM(-1, active, 4);
      } catch (Exception ex) {
        logger.error(ex.getMessage());
      } 
    }
    
    public Map<String, List<String>> checkSiteForDeviceONOFF(String idents) throws Exception {
      if (idents == null || idents.length() == 0)
        return null; 
      Map<String, List<String>> result = new HashMap<>();
      Iterator<Map.Entry<String, SiteInfo>> it = this.dog.getAllSites();
      while (it.hasNext()) {
        Map.Entry<String, SiteInfo> entry = it.next();
        SiteInfo s = entry.getValue();
        if (StringUtils.isBlank(s.getTagId()) && StringUtils.isBlank(s.getTagId2()))
          continue; 
        if(TagDAO.INSTANCE.isTagIgnore(s.getSupervisorId(), TAG_ONOFF_IGNORE))
        {
            logger.info(s.getDescription()+" ONOFF ignored");
            continue;
        }
        String ip = s.getIp();
        String[] devcodes = UnitConfig.getUnitOFFDevmdlCode();
        byte b;
        int i;
        String[] arrayOfString1;
        for (i = (arrayOfString1 = devcodes).length, b = 0; b < i; ) {
          String devcode = arrayOfString1[b];
          String OFFVarCode = UnitConfig.getUnitOFFVar(devcode);
          String sql = "select lgdevice.code,lgdevice.description from lgdevice inner join cfsupervisors on lgdevice.kidsupervisor = cfsupervisors.id   where lgdevice.devmodcode in (" + 
            devcode + ") and lgdevice.kidsupervisor=? and lgdevice.iscancelled=? and cfsupervisors.ktype=?";
          Object[] params = { s.getSupervisorId(), "FALSE", "boss" };
          try {
            RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql, params);
            if (rs != null && rs.size() > 0) {
              String[] devCodes = new String[rs.size() + 1];
              Map<String, String> deviceInfo = new HashMap<>();
              for (int j = 0; j < rs.size(); j++) {
                String devCode = (String)rs.get(j).get(0);
                String devDescritption = (String)rs.get(j).get(1);
                devCodes[j] = devCode;
                deviceInfo.put(devCode, devDescritption);
              } 
              String r = DeviceValueMgr.getInstance().getUnitOffDesc(ip, devCodes, OFFVarCode, deviceInfo);
              if (!"".equals(r)) {
                String msg = "\n";
                if (!"".equals(r)) {
                  msg = String.valueOf(msg) + s.getDescription() + r;
                  try {
                    List<String> l = new ArrayList<>();
                    l.add(msg);
                    result.put(s.getIdent(), l);
                  } catch (Exception ex) {
                	  logger.error(ex.getMessage());
                  } 
                } 
              } 
            } 
          } catch (DataBaseException e) {
        	  logger.error(e.getMessage());
          } 
          b++;
        } 
      } 
      return result;
    }
  }
  
  class AlarmSynchChecking extends TimerTask {
    public void run() {
      logger.info("AlarmSynchChecking running");
    }
    
    public Map<String, List<String>> lastAlarmSynchCheck() {
      Map<String, List<String>> result = new HashMap<>();
      SiteInfo s = new SiteInfo();
      List<SiteInfo> rs = Dog.getInstance().getInfosWithTags();
      Date today = new Date();
      for (SiteInfo site : rs) {
        String msg = "站点"+ site.getDescription();
        if (site.getLastSynchDate() == null || !DateUtils.isSameDay(site.getLastSynchDate(), today)) {
          msg = String.valueOf(msg) + "今日报警未成功同步" ;
        } else {
          msg = String.valueOf(msg) + "今日报警成功同步" ;
        } 
        Sender wx = Sender.getInstance(site.getChannel().intValue());
        wx.sendIM(new WechatMsg.Builder(msg,site.getAgentId(), new String[]{site.getTagId(),site.getTagId2()})
        		.tagIds(WECHAT_APPLICATION_THREAD.getTagBySiteId(site.getSupervisorId())).build());
        Dog.sleep(1000);
      } 
      return result;
    }
  }
}
