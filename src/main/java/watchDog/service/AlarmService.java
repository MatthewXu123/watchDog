package watchDog.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import watchDog.bean.ACKResult;
import watchDog.bean.Alarm;
import watchDog.bean.AlarmAppend;
import watchDog.bean.SLAResult;
import watchDog.database.DataBaseException;
import watchDog.database.DatabaseMgr;
import watchDog.database.Record;
import watchDog.database.RecordSet;
import watchDog.thread.AlarmNotificationMain;
import watchDog.util.DateTool;

public class AlarmService {
	private static final Logger logger = Logger.getLogger(AlarmService.class);
	public static final int NO_VARIABLE = -101;
	private static final int MOBILE_LIMITED = 10;
	public static Map<String,List<AlarmAppend>> getActiveAlarm(int[] idsite)
	{
		int siteSize = idsite.length;
		String kidsupervsiorStr = "(kidsupervisor=? ";
		String idsiteStr = "(site.id=? ";
		for(int i = 1; i < siteSize; i ++){
			kidsupervsiorStr += "or kidsupervisor=? ";
			idsiteStr += "or site.id=? ";
		}
		kidsupervsiorStr += " ) ";
		idsiteStr += " ) ";
		Map<String,List<AlarmAppend>> result = new HashMap<String,List<AlarmAppend>>();
		String pattern = "yyyy-MM-dd HH:mm:ss";
		String last24 = DateTool.format(DateTool.addDays(-2),pattern);
		String last7 = DateTool.format(DateTool.addDays(-7),pattern);
		String last30 = DateTool.format(DateTool.addDays(-30),pattern);
		String sql = "select * from"
				+ "(select site.id as idsite,site.ident,site.ktype,site.description as site,site.ipaddress as ip,device.iddevice,device.description as device,device.idline,device.devmodcode,device.code as devcode,var.description as var,var.idvariable,var.code,var.addressin, "+
			"active.idalarm,active.priority,active.starttime,active.endtime, "+
			"active.ackremoteuser,active.ackremotetime, "+
			"active.ackuser,active.acktime, "+
			"active.delactionuser as deluser,active.delactiontime as deltime, "+
			"active.resetuser,active.resettime, "+
			"active.usespare, active.recallresetuser, 1 as important, "+
			"last24.cnt as last24hCNT,last7.cnt as last7CNT,last30.cnt as last30CNT "+
			"from lgalarmactive as active "+
			"inner join cfsupervisors as site on site.id=active.kidsupervisor "+
			"inner join lgdevice as device on device.kidsupervisor=site.id and device.iddevice=active.iddevice "+
			"inner join lgvariable as var on var.kidsupervisor=site.id and var.idvariable=active.idvariable "+
			"left join "+
			"( "+
			"select kidsupervisor,iddevice,idvariable,count(*)::int as cnt from lgalarmrecall as reset "+
			"where endtime>='"+last24+"' and " + kidsupervsiorStr +
			"group by kidsupervisor,iddevice,idvariable "+
			") as last24 on  last24.kidsupervisor=active.kidsupervisor and last24.iddevice=active.iddevice and last24.idvariable=active.idvariable "+
			"left join "+
			"( "+
			"select kidsupervisor,iddevice,idvariable,count(*)::int as cnt from lgalarmrecall as reset "+
			"where endtime>='"+last7+"' and " + kidsupervsiorStr +
			"group by kidsupervisor,iddevice,idvariable "+
			") as last7 on  last7.kidsupervisor=active.kidsupervisor and last7.iddevice=active.iddevice and last7.idvariable=active.idvariable "+
			"left join "+
			"( "+
			"select kidsupervisor,iddevice,idvariable,count(*)::int as cnt from lgalarmrecall as reset "+
			"where endtime>='"+last30+"' and " + kidsupervsiorStr +
			"group by kidsupervisor,iddevice,idvariable "+
			") as last30 on  last30.kidsupervisor=active.kidsupervisor and last30.iddevice=active.iddevice and last30.idvariable=active.idvariable "+
			"where "+importantAlarmSQL("active")+" and " + idsiteStr +
			"order by active.idalarm desc ) as a "+
			"union all "+
			"select * from ("+
			"select site.id as idsite,site.ident,site.ktype,site.description as site,site.ipaddress as ip,device.iddevice,device.description as device,device.idline,device.devmodcode,device.code as devcode,var.description as var,var.idvariable,var.code,var.addressin, "+
			"active.idalarm,active.priority,active.starttime,active.endtime, "+
			"active.ackremoteuser,active.ackremotetime, "+
			"active.ackuser,active.acktime, "+
			"active.delactionuser as deluser,active.delactiontime as deltime, "+
			"active.resetuser,active.resettime, "+
			"active.usespare, active.recallresetuser, 0 as important, "+
			"last24.cnt as last24hCNT,last7.cnt as last7CNT,last30.cnt as last30CNT "+
			"from lgalarmactive as active "+
			"inner join cfsupervisors as site on site.id=active.kidsupervisor "+
			"inner join lgdevice as device on device.kidsupervisor=site.id and device.iddevice=active.iddevice "+
			"inner join lgvariable as var on var.kidsupervisor=site.id and var.idvariable=active.idvariable "+
			"left join "+
			"( "+
			"select kidsupervisor,iddevice,idvariable,count(*)::int as cnt from lgalarmrecall as reset "+
			"where endtime>='"+last24+"' and " + kidsupervsiorStr +
			"group by kidsupervisor,iddevice,idvariable "+
			") as last24 on  last24.kidsupervisor=active.kidsupervisor and last24.iddevice=active.iddevice and last24.idvariable=active.idvariable "+
			"left join "+
			"( "+
			"select kidsupervisor,iddevice,idvariable,count(*)::int as cnt from lgalarmrecall as reset "+
			"where endtime>='"+last7+"' and " + kidsupervsiorStr +
			"group by kidsupervisor,iddevice,idvariable "+
			") as last7 on  last7.kidsupervisor=active.kidsupervisor and last7.iddevice=active.iddevice and last7.idvariable=active.idvariable "+
			"left join "+
			"( "+
			"select kidsupervisor,iddevice,idvariable,count(*)::int as cnt from lgalarmrecall as reset "+
			"where endtime>='"+last30+"' and " + kidsupervsiorStr +
			"group by kidsupervisor,iddevice,idvariable "+
			") as last30 on  last30.kidsupervisor=active.kidsupervisor and last30.iddevice=active.iddevice and last30.idvariable=active.idvariable "+
			"where  " + idsiteStr +
			"and (site.id,active.idalarm) not in "+
			"( "+
			"select site.id as idsite, "+
			"active.idalarm "+
			"from lgalarmactive as active "+
			"inner join cfsupervisors as site on site.id=active.kidsupervisor "+
			"inner join lgdevice as device on device.kidsupervisor=site.id and device.iddevice=active.iddevice "+
			"inner join lgvariable as var on var.kidsupervisor=site.id and var.idvariable=active.idvariable "+
			"where "+importantAlarmSQL("active")+" and " + idsiteStr +
			") and active.idalarm>0 "+
			"order by active.idalarm desc "+
			") as b order by idsite ";
		try {
			Object[] params = new Object[9 * siteSize];
			for(int i = 0; i < 9 * siteSize;){
				for(int j = 0; j < siteSize; j ++){
					params[i++] = idsite[j];
				}
			}
			RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql,params);
			if(rs != null && rs.size()>0)
			{
				List<AlarmAppend> importantList = new ArrayList<AlarmAppend>();
				List<AlarmAppend> unimportantList = new ArrayList<AlarmAppend>();
				result.put("i", importantList);
				result.put("u", unimportantList);
				for(int i=0;i<rs.size();i++)
				{
					Record r = rs.get(i);
					int important = (Integer)r.get("important");
					if(important == 1)
						importantList.add(new AlarmAppend(r));
					else
						unimportantList.add(new AlarmAppend(r));
				}
			}
		} catch (Exception ex) {
			logger.error("error",ex);
		}
		return result;
	}
	
	public static List<Alarm> getResetAlarm(int idsite,int lastAlarmId,int iddevice,int idvariable,String alarmDescription,Date[] timeRange)
	{
		return getResetAlarm(idsite,false,lastAlarmId,iddevice,idvariable,alarmDescription,timeRange);
	}
	public static List<Alarm> getResetAlarm(int idsite,boolean allPriority,int lastAlarmId,int iddevice,int idvariable,String alarmDescription,Date[] timeRange)
	{
		List<Alarm> result = new ArrayList<Alarm>();
		Object[] params = {idsite,lastAlarmId};
		String sql = "select site.id as idsite,site.ident,site.ktype,site.description as site,site.ipaddress as ip,device.iddevice,device.description as device,device.idline,device.devmodcode,device.code as devcode,var.description as var,var.idvariable,var.code,var.addressin, "+
				"reset.idalarm,reset.priority,reset.starttime,reset.endtime, "+
				"reset.ackremoteuser,reset.ackremotetime, "+
				"reset.ackuser,reset.acktime, "+
				"reset.delactionuser as deluser,reset.delactiontime as deltime, "+
				"reset.resetuser,reset.resettime, "+
				"reset.usespare, reset.recallresetuser "+
				"from lgalarmrecall as reset "+
				"inner join cfsupervisors as site on site.id=reset.kidsupervisor "+
				"inner join lgdevice as device on device.kidsupervisor=site.id and device.iddevice=reset.iddevice "+
				"inner join lgvariable as var on var.kidsupervisor=site.id and var.idvariable=reset.idvariable "+
				"where ";
		if(!allPriority)
			sql += importantAlarmSQL("reset") +" and ";
		sql += " site.id = ? and reset.idalarm<? ";
		
				if(idvariable != NO_VARIABLE)
				{
					sql += "and reset.idvariable=? ";
					params = AlarmService.addParams(params, idvariable);
				}
				if(iddevice != NO_VARIABLE)
				{
					sql += "and reset.iddevice=? ";
					params = AlarmService.addParams(params, iddevice);
				}
				if(timeRange != null && timeRange[0] != null && timeRange[1] != null)
				{
					sql += "and reset.starttime>='"+DateTool.format(timeRange[0], "yyyy-MM-dd HH:mm:ss")+"' and reset.starttime<'"+DateTool.format(timeRange[1], "yyyy-MM-dd HH:mm:ss")+"' ";
				}
				if(alarmDescription != null)
				{
					sql += "and var.description=? ";
					params = AlarmService.addParams(params, alarmDescription);
				}
				sql += "order by reset.idalarm desc " +
				"limit 600";
		try {
			
			RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql,params);
			if(rs != null && rs.size()>0)
			{
				for(int i=0;i<rs.size();i++)
				{
					Record r = rs.get(i);
					result.add(new Alarm(r));
				}
			}
		} catch (Exception ex) {
			logger.error("error",ex);
		}
		return result;
	}
	/**
	 * 根据idSite和idAlarms tableType获取userspare,用于resetAlarm.jsp
	 * @param idSite
	 * @param tableType
	 * @param idAlarms
	 * @return
	 * @author MatthewXu
	 * @date Mar 25, 2019
	 */
	public static Map<String, String> getUsespareByIdalarm(int idSite,int tableType,int[] idAlarms) {
		/*SiteInfo site = Dog.getInstance().getSiteInfoByIdSite(idSite);
		if(site == null)
			return null;	*/
		String sql = "select usespare from lgalarmactive where kidsupervisor=? and idalarm=? ";
		if(tableType == AlarmManageService.TABLE_TYPE_RESET)
			sql = "select usespare from lgalarmrecall where kidsupervisor=? and idalarm=?";
		Map<String, String> usespareMap = new HashMap<>();
		try{
			for(int idalarm:idAlarms)
			{
				RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql, new Object[]{idSite,idalarm});
				if(rs != null && rs.size() > 0){
					usespareMap.put(String.valueOf(idalarm), (String)rs.get(0).get(0));
				}
			}
		}catch(DataBaseException ex)
		{
			logger.error("error:",ex);
		}
		return usespareMap;
	}
	//group by month
	public static List<AlarmMonth> getResetAlarmMonthlyStatistic(int idsite,int iddevice,int idvariable,String alarmDescription,Date[] timeRange)
	{
		Map<String,AlarmMonth> tmp = new HashMap<String,AlarmMonth>();
		Date monthBegin = DateTool.getMonthBegin(new Date());
		Date timeStart = DateTool.addMonths(monthBegin,-8);
		Object[] params = {idsite};
		String sql = "select TO_CHAR(reset.starttime, 'YYYY-MM') as month,count(*)::int as cnt from lgalarmrecall as reset "+
				"inner join lgdevice as device on reset.kidsupervisor=device.kidsupervisor and reset.iddevice=device.iddevice ";
		if(alarmDescription != null)
		{
			sql +="inner join lgvariable as var on reset.kidsupervisor=var.kidsupervisor and reset.idvariable=var.idvariable ";
		}
		sql += "where  reset.iddevice>0 and reset.kidsupervisor=? "+
				"and "+importantAlarmSQL("reset")+" ";
		if(alarmDescription != null)
		{
			sql += "and var.description=? ";
			params = addParams(params,alarmDescription);
		}
		if(iddevice != NO_VARIABLE)
		{
			sql += " and reset.iddevice=? ";
			params = addParams(params,iddevice);
		}
		if(idvariable != NO_VARIABLE)
		{
			sql += " and reset.idvariable=? ";
			params = addParams(params,idvariable);
		}
		if(timeRange != null)
		{
			sql += "and reset.starttime>='"+DateTool.format(timeRange[0], "yyyy-MM-dd HH:mm:ss")+"' and reset.starttime<'"+DateTool.format(timeRange[1], "yyyy-MM-dd HH:mm:ss")+"' ";
		}
		else
			sql += " and reset.endtime>='"+DateTool.format(timeStart, "yyyy-MM-dd HH:mm:ss")+"' ";
		
		sql += "group by TO_CHAR(reset.starttime, 'YYYY-MM') "+
				"order by month";
		try{
		RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql,params);
		if(rs != null && rs.size()>0)
		{
			for(int i=0;i<rs.size();i++)
			{
				Record r = rs.get(i);
				String month = (String)r.get("month");
				int cnt = -1;
				if(r.get("cnt") != null)
					cnt = (int)r.get("cnt");
				tmp.put(month,new AlarmMonth(month,cnt));
			}
		}
		} catch (Exception ex) {
			logger.error("error",ex);
		}
		return fixResult(timeStart,tmp);
	}
	//group by daily
	//timeRange must
	public static List<AlarmMonth> getResetAlarmDailyStatistic(int idsite,int iddevice,int idvariable,String alarmDescription,Date[] timeRange)
	{
		Map<String,AlarmMonth> tmp = new HashMap<String,AlarmMonth>();
		Object[] params = {idsite};
		String sql = "select TO_CHAR(reset.starttime, 'YYYY-MM-DD') as month,count(*)::int as cnt from lgalarmrecall as reset "+
				"inner join lgdevice as device on reset.kidsupervisor=device.kidsupervisor and reset.iddevice=device.iddevice ";
		if(alarmDescription != null)
		{
			sql +="inner join lgvariable as var on reset.kidsupervisor=var.kidsupervisor and reset.idvariable=var.idvariable ";
		}
		sql += "where  reset.iddevice>0 and reset.kidsupervisor=? "+
				"and "+importantAlarmSQL("reset")+" ";
		if(alarmDescription != null)
		{
			sql += "and var.description=? ";
			params = addParams(params,alarmDescription);
		}
		if(iddevice != NO_VARIABLE)
		{
			sql += " and reset.iddevice=? ";
			params = addParams(params,iddevice);
		}
		if(idvariable != NO_VARIABLE)
		{
			sql += " and reset.idvariable=? ";
			params = addParams(params,idvariable);
		}
		if(timeRange != null)
		{
			sql += "and reset.starttime>='"+DateTool.format(timeRange[0], "yyyy-MM-dd HH:mm:ss")+"' and reset.starttime<'"+DateTool.format(timeRange[1], "yyyy-MM-dd HH:mm:ss")+"' ";
		}
		else
			return null;
		
		sql += "group by TO_CHAR(reset.starttime, 'YYYY-MM-DD') "+
				"order by month";
		try{
		RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql,params);
		if(rs != null && rs.size()>0)
		{
			for(int i=0;i<rs.size();i++)
			{
				Record r = rs.get(i);
				String month = (String)r.get("month");
				int cnt = -1;
				if(r.get("cnt") != null)
					cnt = (int)r.get("cnt");
				tmp.put(month,new AlarmMonth(month,cnt));
			}
		}
		} catch (Exception ex) {
			logger.error("error",ex);
		}
		return fixResult(timeRange,tmp);
	}
	//group by hour
	//timerange is must
	public static List<AlarmMonth> getResetAlarmHourlyStatistic(int idsite,int iddevice,int idvariable,String alarmDescription,Date[] timeRange)
	{
		Map<String,AlarmMonth> tmp = new HashMap<String,AlarmMonth>();
		Object[] params = {idsite};
		String sql = "select TO_CHAR(reset.starttime, 'YYYY-MM-DD HH24') as month,count(*)::int as cnt from lgalarmrecall as reset "+
				"inner join lgdevice as device on reset.kidsupervisor=device.kidsupervisor and reset.iddevice=device.iddevice ";
		if(alarmDescription != null)
		{
			sql +="inner join lgvariable as var on reset.kidsupervisor=var.kidsupervisor and reset.idvariable=var.idvariable ";
		}
		sql += "where  reset.iddevice>0 and reset.kidsupervisor=? "+
				"and "+importantAlarmSQL("reset")+" ";
		if(alarmDescription != null)
		{
			sql += "and var.description=? ";
			params = addParams(params,alarmDescription);
		}
		if(iddevice != NO_VARIABLE)
		{
			sql += " and reset.iddevice=? ";
			params = addParams(params,iddevice);
		}
		if(idvariable != NO_VARIABLE)
		{
			sql += " and reset.idvariable=? ";
			params = addParams(params,idvariable);
		}
		if(timeRange != null)
		{
			sql += "and reset.starttime>='"+DateTool.format(timeRange[0], "yyyy-MM-dd HH:mm:ss")+"' and reset.starttime<'"+DateTool.format(timeRange[1], "yyyy-MM-dd HH:mm:ss")+"' ";
		}
		else
			return null;
		
		sql += "group by TO_CHAR(reset.starttime, 'YYYY-MM-DD HH24') "+
				"order by month";
		try{
		RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql,params);
		if(rs != null && rs.size()>0)
		{
			for(int i=0;i<rs.size();i++)
			{
				Record r = rs.get(i);
				String month = (String)r.get("month");
				int cnt = -1;
				if(r.get("cnt") != null)
					cnt = (int)r.get("cnt");
				tmp.put(month,new AlarmMonth(month,cnt));
			}
		}
		} catch (Exception ex) {
			logger.error("error",ex);
		}
		return fixResultDaily(timeRange,tmp);
	}
//	public static List<AlarmMonth> getResetAlarmMonthlyByDeiviceStatistic(int idsite,int iddevice)
//	{
//		Map<String,AlarmMonth> tmp = new HashMap<String,AlarmMonth>();
//		Date monthBegin = DateTool.getMonthBegin(new Date());
//		Date timeStart = DateTool.addMonths(monthBegin,-8);
//		String sql = "";
//		sql = "select TO_CHAR(reset.endtime, 'YYYY-MM') as month,count(*)::int as cnt from lgalarmrecall as reset "+
//			"inner join lgdevice as device on reset.kidsupervisor=device.kidsupervisor and reset.iddevice=device.iddevice "+
//			"where  reset.iddevice>0 and reset.starttime>='"+DateTool.format(timeStart, "yyyy-MM-dd HH:mm:ss")+"' and reset.kidsupervisor=? and reset.iddevice=? "+
//			"and ((device.devmodcode like 'mpxpro%' and reset.priority = '1') or (device.devmodcode not like 'mpxpro%' and reset.priority in ('1','2'))) "+
//			"group by TO_CHAR(reset.endtime, 'YYYY-MM') "+
//			"order by month";
//		Object[] params = new Object[]{idsite,iddevice};
//		try{
//			RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql,params);
//			if(rs != null && rs.size()>0)
//			{
//				for(int i=0;i<rs.size();i++)
//				{
//					Record r = rs.get(i);
//					String month = (String)r.get("month");
//					int cnt = -1;
//					if(r.get("cnt") != null)
//						cnt = (int)r.get("cnt");
//					tmp.put(month,new AlarmMonth(month,cnt));
//				}
//			}
//		} catch (Exception ex) {
//			logger.error("error",ex);
//		}
//		return fixResult(timeStart,tmp);
//	}
	//group by alarm name
	public static List<AlarmMonth> getAlarmDescriptionStatistic(int idsite,int iddevice,int monthNum,Date[] timeRange)
	{
		Object[] params = new Object[]{idsite};
		List<AlarmMonth> tmp = new ArrayList<AlarmMonth>();
		Date timeStart = DateTool.addMonths(new Date(),-monthNum);
		String sql = "select v.description as month,count(*)::int as cnt from lgalarmrecall as reset "+
				"inner join lgvariable as v on reset.idvariable=v.idvariable and reset.kidsupervisor=v.kidsupervisor "+
				"inner join lgdevice as d on reset.iddevice=d.iddevice and reset.kidsupervisor=d.kidsupervisor " +
				"where reset.iddevice>0 and reset.kidsupervisor=? ";
		if(timeRange == null)
			sql += "and reset.starttime>='"+DateTool.format(timeStart, "yyyy-MM-dd HH:mm:ss")+"' ";
		else
			sql += "and reset.starttime>='"+DateTool.format(timeRange[0], "yyyy-MM-dd HH:mm:ss")+"' and reset.starttime<'"+DateTool.format(timeRange[1], "yyyy-MM-dd HH:mm:ss")+"' ";
				sql += "and "+importantAlarmSQL("d","reset")+" ";
		if(iddevice != NO_VARIABLE)
		{
			sql += " and reset.iddevice=? ";
			params = addParams(params,iddevice);
		}
		sql += "group by v.description "+
				"order by cnt desc " +
				"limit "+MOBILE_LIMITED;

		
		try{
			RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql,params);
			if(rs != null && rs.size()>0)
			{
				for(int i=0;i<rs.size();i++)
				{
					Record r = rs.get(i);
					String month = (String)r.get("month");
					int cnt = -1;
					if(r.get("cnt") != null)
						cnt = (int)r.get("cnt");
					tmp.add(new AlarmMonth(month,cnt));
				}
			}
		} catch (Exception ex) {
			logger.error("error",ex);
		}
		return tmp;
	}
	//group by device name
	public static List<AlarmMonth> getAlarmDeviceStatistic(int idsite,boolean all,int monthNum,String alarmDescription,Date[] timeRange)
	{
		Object[] params = new Object[]{idsite};
		List<AlarmMonth> tmp = new ArrayList<AlarmMonth>();
		Date timeStart = DateTool.addMonths(new Date(),-monthNum);
		String sql = "select d.iddevice,d.description as month,count(*)::int as cnt from lgalarmrecall as reset "+
				"inner join lgdevice as d on reset.iddevice=d.iddevice and reset.kidsupervisor=d.kidsupervisor "+
				"inner join cfsupervisors as s on reset.kidsupervisor=s.id ";
		if(alarmDescription != null)
		{
			sql +="inner join lgvariable as var on reset.kidsupervisor=var.kidsupervisor and reset.idvariable=var.idvariable ";
		}
		sql += "where reset.iddevice>0 and reset.kidsupervisor=? ";
		if(alarmDescription != null)
		{
			sql += "and var.description=? ";
			params = addParams(params,alarmDescription);
		}
		if(timeRange == null)
			sql += "and reset.starttime>='"+DateTool.format(timeStart, "yyyy-MM-dd HH:mm:ss")+"' ";
		else
			sql += "and reset.starttime>='"+DateTool.format(timeRange[0], "yyyy-MM-dd HH:mm:ss")+"' and reset.starttime<'"+DateTool.format(timeRange[1], "yyyy-MM-dd HH:mm:ss")+"' ";
			sql += "and "+importantAlarmSQL("d","reset")+" "+
				"group by d.iddevice,d.description "+
				"order by cnt desc ";
		if(!all)
			sql += "limit "+MOBILE_LIMITED;

		try{
			RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql,params);
			if(rs != null && rs.size()>0)
			{
				for(int i=0;i<rs.size();i++)
				{
					Record r = rs.get(i);
					int iddevice = (int)r.get("iddevice");
					String month = (String)r.get("month");
					int cnt = -1;
					if(r.get("cnt") != null)
						cnt = (int)r.get("cnt");
					tmp.add(new AlarmMonth(iddevice,month,cnt));
				}
			}
		} catch (Exception ex) {
			logger.error("error",ex);
		}
		return tmp;
	}
	//fix month
	public static List<AlarmMonth> fixResult(Date timeStart,Map<String,AlarmMonth> tmp)
	{
		List<AlarmMonth> result = new ArrayList<AlarmMonth>();
		//mother fucker Kevin changed his idea
		Date now = new Date();//DateTool.getFirstDayOfMonth();
		if(timeStart.after(now))
			return null;
		while(true)
		{
			if(!timeStart.before(now) || DateTool.isSameDay(timeStart, now))
				return result;
			String key = DateTool.format(timeStart,"yyyy-MM");
			AlarmMonth v = tmp.get(key);
			if(v == null)
				result.add(new AlarmMonth(key,null));
			else
				result.add(v);
			timeStart = DateTool.addMonths(timeStart, 1);
		}
	}
	//fix week
	public static List<AlarmMonth> fixResultW(Date[] timeRange,Map<String,AlarmMonth> tmp)
	{
		List<AlarmMonth> result = new ArrayList<AlarmMonth>();
		if(timeRange == null)
			return result;
		if(timeRange[0].after(timeRange[1]))
			return result;
		long dayDiff = DateTool.diffDays(timeRange[1], timeRange[0]);
//		String formatStr = "dd";
//		if(dayDiff<10)
//			formatStr = "MM-dd";
		String formatStr = "MM-dd";
		Date timeStart = timeRange[0];
		while(true)
		{
			if(!timeStart.before(timeRange[1]))
				return result;
			String key = DateTool.format(timeStart,"yyyy-MM-dd");
			String keyDay = DateTool.format(timeStart,formatStr);
			AlarmMonth v = tmp.get(key);
			if(v == null)
				result.add(new AlarmMonth(keyDay,null));
			else
				result.add(new AlarmMonth(keyDay,v.getNum()));
			timeStart = DateTool.addDays(timeStart, 7);
		}
	}
	//fix day
	public static List<AlarmMonth> fixResult(Date[] timeRange,Map<String,AlarmMonth> tmp)
	{
		List<AlarmMonth> result = new ArrayList<AlarmMonth>();
		if(timeRange == null)
			return result;
		if(timeRange[0].after(timeRange[1]))
			return result;
		long dayDiff = DateTool.diffDays(timeRange[1], timeRange[0]);
		String formatStr = "dd";
		if(dayDiff<10)
			formatStr = "MM-dd";
		Date timeStart = timeRange[0];
		while(true)
		{
			if(!timeStart.before(timeRange[1]))
				return result;
			String key = DateTool.format(timeStart,"yyyy-MM-dd");
			String keyDay = DateTool.format(timeStart,formatStr);
			AlarmMonth v = tmp.get(key);
			if(v == null)
				result.add(new AlarmMonth(keyDay,null));
			else
				result.add(new AlarmMonth(keyDay,v.getNum()));
			timeStart = DateTool.addDays(timeStart, 1);
		}
	}
	//fix hour
	public static List<AlarmMonth> fixResultDaily(Date[] timeRange,Map<String,AlarmMonth> tmp)
	{
		List<AlarmMonth> result = new ArrayList<AlarmMonth>();
		if(timeRange == null)
			return result;
		if(timeRange[0].after(timeRange[1]))
			return result;
		Date timeStart = timeRange[0];
		while(true)
		{
			if(!timeStart.before(timeRange[1]))
				return result;
			String key = DateTool.format(timeStart,"yyyy-MM-dd HH");
			String keyDay = DateTool.format(timeStart,"HH");
			AlarmMonth v = tmp.get(key);
			if(v == null)
				result.add(new AlarmMonth(keyDay,null));
			else
				result.add(new AlarmMonth(keyDay,v.getNum()));
			timeStart = DateTool.add(timeStart, 1,Calendar.HOUR_OF_DAY);
		}
	}
	public static String[] getDeviceNameByIdvariable(int idsite,int idvariable)
	{
		String[] result = new String[2];
		result[0] = "";
		result[1] = "";
		String sql = "select d.description as d,v.description as v from lgvariable as v "+
					"inner join lgdevice as d on v.kidsupervisor=d.kidsupervisor and v.iddevice=d.iddevice "+
					"where v.kidsupervisor=? and v.idvariable=? ";
		Object[] params = {idsite,idvariable};
		RecordSet rs;
		try {
			rs = DatabaseMgr.getInstance().executeQuery(sql,params);
			if(rs != null && rs.size()>0)
			{
				result[0] = (String)rs.get(0).get("d");
				result[1] = (String)rs.get(0).get("v");
			}
		} catch (DataBaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	public static String getDeviceNameByIddevice(int idsite,int iddevice)
	{
		String sql = "select d.description from lgdevice as d where d.kidsupervisor=? and d.iddevice=? ";
		Object[] params = {idsite,iddevice};
		RecordSet rs;
		try {
			rs = DatabaseMgr.getInstance().executeQuery(sql,params);
			if(rs != null && rs.size()>0)
			{
				return (String)rs.get(0).get(0);
			}
		} catch (DataBaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	private static Object[] addParams(Object[] oldParams,Object... more)
	{
		Object[] newParams = new Object[oldParams.length+more.length];
		System.arraycopy(oldParams,0,newParams,0,oldParams.length);
		for(int i=0;i<more.length;i++)
		{
			newParams[oldParams.length+i] = more[i];
		}
		return newParams;
	}
	public static Date[] getTimeRangeByMonth(String month)
	{
		Date start = DateTool.parse(month+"-1", "yyyy-MM-dd");
		Date end = DateTool.addMonths(start, 1);
		Date[] result = new Date[]{start,end};
		return result;
	}
	public static Date[] getTimeRangeByDay(String day)
	{
		Date start = DateTool.parse(day, "yyyy-MM-dd");
		Date end = DateTool.addDays(start, 1);
		Date[] result = new Date[]{start,end};
		return result;
	}
	public static class energyWeek{
		String siteName;
		int energyw1;
		int energyw2;
		int energyw3;
		int energyw4;
	
		public energyWeek(String siteNamew,int energyw11,int energyw22,int energyw33,int energyw44){
			siteName = siteNamew;
			energyw1 = energyw11;
			energyw2 = energyw22;
			energyw3 = energyw33;
			energyw4 = energyw44;
		}

		public String getSiteName() {
			return siteName;
		}

		public int getEnergyw1() {
			return energyw1;
		}

		public int getEnergyw2() {
			return energyw2;
		}

		public int getEnergyw3() {
			return energyw3;
		}

		public int getEnergyw4() {
			return energyw4;
		}

	}
	
	public static class AlarmMonth{
		int id = 0;
		String month = null;
		Integer num = null;
		float numFloat = 0;
		public AlarmMonth(String month,Integer num)
		{
			this.month = month;
			this.num = num;
		}
		public AlarmMonth(String month,float numFloat)
		{
			this.month = month;
			this.numFloat = numFloat;
			this.num = (int)this.numFloat;
		}
		public AlarmMonth(int id,String month,Integer num)
		{
			this.id = id;
			this.month = month;
			this.num = num;
		}
		public String getMonth() {
			return month;
		}
		public Integer getNum() {
			return num;
		}
		public int getId() {
			return id;
		}
		public void setNumFloat(float numFloat) {
			this.numFloat = numFloat;
			this.num = (int)numFloat;
		}
		public float getNumFloat()
		{
			return this.numFloat;
		}
		
	}
	public static List<Alarm> getAlarms(int idsite,String ids)
	{
		List<Alarm> result = new ArrayList<Alarm>();
		Object[] params = {idsite};
		String sql = "select site.id as idsite,site.ident,site.ktype,site.description as site,site.ipaddress as ip,device.iddevice,device.description as device,device.idline,device.devmodcode,device.code as devcode,var.description as var,var.idvariable,var.code,var.addressin, "+
				"active.idalarm,active.priority,active.starttime,active.endtime, "+
				"active.ackremoteuser,active.ackremotetime, "+
				"active.ackuser,active.acktime, "+
				"active.delactionuser as deluser,active.delactiontime as deltime, "+
				"active.resetuser,active.resettime, "+
				"active.usespare, active.recallresetuser "+
				"from lgalarmactive as active "+
				"inner join cfsupervisors as site on site.id=active.kidsupervisor "+
				"inner join lgdevice as device on device.kidsupervisor=site.id and device.iddevice=active.iddevice "+
				"inner join lgvariable as var on var.kidsupervisor=site.id and var.idvariable=active.idvariable "+
				"where active.kidsupervisor=? and active.idalarm in ("+ids+") "+
				"order by active.iddevice ";
		try {
			
			RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql,params);
			if(rs != null && rs.size()>0)
			{
				for(int i=0;i<rs.size();i++)
				{
					Record r = rs.get(i);
					result.add(new Alarm(r));
				}
			}
		} catch (Exception ex) {
			logger.error("error",ex);
		}
		return result;
	}
	public static List<SLAResult> getSLAResult(int[] idsite, String type,Date startDay)
	{
		List<SLAResult> result = new ArrayList<SLAResult>();
		if(idsite.length==0)
			return result;
		String sites="(";
		for(int i=0;i<idsite.length;i++){
			sites +=(idsite[i]+",");
		}
		if(sites.endsWith(","))
			sites=sites.substring(0, sites.length()-1);
		sites +=")";
		String sql = "select s.description as sitename,r.* from private_sla_site_result as r "
				+ "inner join cfsupervisors as s on r.idsite=s.id "
				+ " where r.idsite in "+sites+" and r.type=? and r.startday = '" + DateTool.format(startDay, "yyyy-MM-dd") + "'"
				+ " order by h_tot desc";
		Object[] params = {type};
		try {
			
			RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql,params);
			if(rs != null && rs.size()>0)
			{
				for(int i=0;i<rs.size();i++){
					result.add(new SLAResult(rs.get(i)));
				}
			}
		} catch (Exception ex) {
			logger.error("error",ex);
		}
		return result;
	}
	
	public static List<AlarmManageResult> getAlarmManageResult(int[] idsite, String type,Date startDay)
	{
		List<AlarmManageResult> result = new ArrayList<AlarmManageResult>();
		if(idsite.length==0)
			return result;
		String sites="(";
		for(int i=0;i<idsite.length;i++){
			sites +=(idsite[i]+",");
		}
		if(sites.endsWith(","))
			sites=sites.substring(0, sites.length()-1);
		sites +=")";
		Date endDate = new Date();
		if("w".equals(type)){
			endDate = DateTool.addDays(startDay,7);
		}
		String addDate = "";
		if("w".equals(type)){
			addDate =" and (starttime between '"+DateTool.format(startDay,"YYYY-MM-dd")+"' and '"+DateTool.format(endDate,"YYYY-MM-dd")+"') ";
		}else{
			addDate =" and TO_CHAR(starttime,'YYYY-MM')  = '"+DateTool.format(startDay,"YYYY-MM") +"'";
		}
		
		String sql = "select cfsupervisors.description,count(kidsupervisor),substring(usespare,0,position(';' in usespare)) from ("
				+" select kidsupervisor,usespare,idalarm from lgalarmactive where kidsupervisor in "+sites + addDate 
				+" UNION "
				+" select kidsupervisor,usespare,idalarm from lgalarmrecall where kidsupervisor in "+sites + addDate 
				+ ") alarm inner join cfsupervisors on alarm.kidsupervisor=cfsupervisors.id  "
				+ " where  (usespare is null or usespare like '%1NN%' or usespare like '%2LC%' or usespare like '%3RM%' or usespare like '%4RP%') ";
		sql += " group by description,usespare";
		try {
			
			RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql,null);
			if(rs != null && rs.size()>0)
			{
				String siteName="";
				Integer noManage = 0;
				Integer noAction =0;
				Integer localAction =0;
				Integer remAction =0;
				Integer repAction =0;
				
				for(int i=0;i<rs.size();i++){
					String temp = (String) rs.get(i).get(0);
					if(!siteName.equals(temp)){
						if(!"".equals(siteName)){
							result.add(new AlarmManageResult(siteName,noManage,noAction,localAction,remAction,repAction));
						}
						siteName = temp;
						noManage = 0;
						noAction =0;
						localAction =0;
						remAction =0;
						repAction =0;
					}
					Integer val = ((Long) rs.get(i).get(1)).intValue();
					temp = (String) rs.get(i).get(2);
					if(temp==null){
						noManage = val;
					}
					else if("1NN".equals(temp)){
						noAction = val;
					}
					else if("2LC".equals(temp)){
						localAction = val;
					}
					else if("3RM".equals(temp)){
						remAction = val;
					}
					else if("4RP".equals(temp)){
						repAction = val;
					}
				}
				result.add(new AlarmManageResult(siteName,noManage,noAction,localAction,remAction,repAction));
			}
		} catch (Exception ex) {
			logger.error("error",ex);
		}
		return result;
	}
	
	public static ACKResult getACKResult(int idsite, String type,Date startDay)
	{
		ACKResult result = null;
		String sql = "select s.description as sitename,r.* from private_ack_site_result as r "
				+ "inner join cfsupervisors as s on r.idsite=s.id "
				+ " where r.idsite=? and r.type=? and r.startday='" + DateTool.format(startDay, "yyyy-MM-dd") + "'";
		Object[] params = {idsite,type};
		try {
			
			RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql,params);
			if(rs != null && rs.size()>0)
			{
				result = new ACKResult(rs.get(0));
			}
		} catch (Exception ex) {
			logger.error("error",ex);
		}
		return result;
	}
	
	public static void main(String[] args)
	{
		Date[] range = {DateTool.parse("2018-9-15"),DateTool.parse("2018-10-15")};
		createSiteACK("w",range);
//		SLAResult r = getSLAResult(140,"w","2018-10-29");
	}
	public static void createSiteSLA(String type,Date[] timeRange)
	{
		String sql = "delete from private_sla_site_result where type=? and startday>=?;"+
				"insert into private_sla_site_result "+
				"select ? as type,? as startday,site.id, "+
				"COALESCE(high_temp.ht_tot,0) as ht_tot,COALESCE(high_temp_out.ht_outSla,0) as ht_outSla,COALESCE(high_temp_reset.ht_reset,0) as ht_reset, "+
				"COALESCE(vh.vh_tot,0) as vh_tot,COALESCE(vh_out.vh_outSla,0) as vh_outSla,COALESCE(vh_reset.vh_reset,0) as vh_reset, "+
				"COALESCE(h.h_tot,0) as h_tot,COALESCE(h_out.h_outSla,0) as h_outSla,COALESCE(h_reset.h_reset,0) as h_reset, "+
				"COALESCE(m.m_tot,0) as m_tot,COALESCE(m_out.m_outSla,0) as m_outSla,COALESCE(m_reset.m_reset,0) as m_reset, "+
				"COALESCE(l.l_tot,0) as l_tot,COALESCE(l_out.l_outSla,0) as l_outSla,COALESCE(l_reset.l_reset,0) as l_reset "+
				"from cfsupervisors as site "+
				"left join "+
				"( "+
				"select lga.kidsupervisor,count(*) as ht_tot from lgalarmrecall lga  "+
				"inner join lgvariable as v on v.kidsupervisor=lga.kidsupervisor and v.idvariable=lga.idvariable "+
				"inner join cfsupervisors cfs on lga.kidsupervisor = cfs.id  "+
				"INNER JOIN lgdevice d ON lga.kidsupervisor=d.kidsupervisor and lga.iddevice=d.iddevice and d.kpi  "+
				"where lga.iddevice > 0 and lga.priority = '1' and starttime >= ? and starttime < ? "+
				"and v.description='高温报警' "+
				"group by lga.kidsupervisor "+
				")as high_temp on site.id=high_temp.kidsupervisor "+
				"left join "+
				"( "+
				"select lga.kidsupervisor,count(*) as ht_outSla "+
				"from lgalarmrecall lga  "+
				"inner join lgvariable as v on v.kidsupervisor=lga.kidsupervisor and v.idvariable=lga.idvariable "+
				"inner join cfsupervisors cfs on lga.kidsupervisor = cfs.id  "+
				"INNER JOIN lgdevice d ON lga.kidsupervisor=d.kidsupervisor and lga.iddevice=d.iddevice and d.kpi  "+
				"inner join cfplantdata pd on  cfs.ksite = pd.code  "+
				"where lga.iddevice > 0 and lga.priority = '1' and pd.veryhighsla > 0 and (pd.veryhighsla < EXTRACT(epoch from  age(lga.endtime, lga.starttime)/3600 )) is true  "+
				"and starttime >= ? and starttime < ? "+
				"and v.description='高温报警' "+
				"group by lga.kidsupervisor "+
				") as high_temp_out on high_temp_out.kidsupervisor=site.id "+
				"left join "+
				"( "+
				"select lga.kidsupervisor,count(*) as ht_reset  "+
				"from lgalarmrecall lga  "+
				"inner join lgvariable as v on v.kidsupervisor=lga.kidsupervisor and v.idvariable=lga.idvariable "+
				"inner join cfsupervisors cfs on lga.kidsupervisor = cfs.id  "+
				"INNER JOIN lgdevice d ON lga.kidsupervisor=d.kidsupervisor and lga.iddevice=d.iddevice and d.kpi  "+
				"inner join cfplantdata pd on cfs.ksite = pd.code  "+
				"where lga.iddevice > 0 and lga.priority = '1' and pd.veryhighsla > 0 and  EXTRACT(epoch from  age(lga.endtime, lga.starttime)/3600 ) is null and starttime >= ? and starttime < ? "+
				"and v.description='高温报警' "+
				"group by lga.kidsupervisor "+
				")as high_temp_reset on high_temp_reset.kidsupervisor=site.id "+
				"left join ( "+
				"select lga.kidsupervisor,count(*) as vh_tot from lgalarmrecall lga  "+
				"inner join cfsupervisors cfs on lga.kidsupervisor = cfs.id  "+
				"INNER JOIN lgdevice d ON lga.kidsupervisor=d.kidsupervisor and lga.iddevice=d.iddevice and d.kpi  "+
				"where lga.iddevice > 0 and priority = '1' and starttime >= ? and starttime < ? "+
				"group by lga.kidsupervisor "+
				")as vh on vh.kidsupervisor=site.id "+
				"left join( "+
				"select lga.kidsupervisor,count(*) as vh_outSla "+
				"from lgalarmrecall lga  "+
				"inner join cfsupervisors cfs on lga.kidsupervisor = cfs.id  "+
				"INNER JOIN lgdevice d ON lga.kidsupervisor=d.kidsupervisor and lga.iddevice=d.iddevice and d.kpi  "+
				"inner join cfplantdata pd on cfs.ksite = pd.code  "+
				"where lga.iddevice > 0 and priority = '1' and pd.veryhighsla > 0 and (pd.veryhighsla < EXTRACT(epoch from  age(lga.endtime, lga.starttime)/3600 )) is true and starttime >= ? and starttime < ? "+
				"group by lga.kidsupervisor "+
				")as vh_out on vh_out.kidsupervisor=site.id "+
				"left join ( "+
				"select lga.kidsupervisor,count(*) as vh_reset "+
				"from lgalarmrecall lga  "+
				"inner join cfsupervisors cfs on lga.kidsupervisor = cfs.id  "+
				"INNER JOIN lgdevice d ON lga.kidsupervisor=d.kidsupervisor and lga.iddevice=d.iddevice and d.kpi  "+
				"inner join cfplantdata pd on cfs.ksite = pd.code  "+
				"where lga.iddevice > 0 and lga.priority = '1' and pd.veryhighsla > 0 and  EXTRACT(epoch from  age(lga.endtime, lga.starttime)/3600 ) is null and starttime >= ? and starttime < ? "+
				"group by lga.kidsupervisor "+
				")as vh_reset on vh_reset.kidsupervisor=site.id "+
				"left join ( "+
				"select lga.kidsupervisor,count(*) as h_tot from lgalarmrecall lga  "+
				"inner join cfsupervisors cfs on lga.kidsupervisor = cfs.id  "+
				"INNER JOIN lgdevice d ON lga.kidsupervisor=d.kidsupervisor and lga.iddevice=d.iddevice and d.kpi  "+
				"where lga.iddevice > 0 and priority = '2' and starttime >= ? and starttime < ? "+
				"group by lga.kidsupervisor "+
				") as h on h.kidsupervisor=site.id "+
				"left join ( "+
				"select lga.kidsupervisor,count(*) as h_outSla "+
				"from lgalarmrecall lga  "+
				"inner join cfsupervisors cfs on lga.kidsupervisor = cfs.id  "+
				"INNER JOIN lgdevice d ON lga.kidsupervisor=d.kidsupervisor and lga.iddevice=d.iddevice and d.kpi  "+
				"inner join cfplantdata pd on cfs.ksite = pd.code  "+
				"where lga.iddevice > 0 and priority = '2' and pd.veryhighsla > 0 and (pd.veryhighsla < EXTRACT(epoch from  age(lga.endtime, lga.starttime)/3600 )) is true and starttime >= ? and starttime < ? "+
				"group by lga.kidsupervisor "+
				") as h_out on h_out.kidsupervisor=site.id "+
				"left join ( "+
				"select lga.kidsupervisor,count(*) as h_reset "+
				"from lgalarmrecall lga  "+
				"inner join cfsupervisors cfs on lga.kidsupervisor = cfs.id  "+
				"INNER JOIN lgdevice d ON lga.kidsupervisor=d.kidsupervisor and lga.iddevice=d.iddevice and d.kpi  "+
				"inner join cfplantdata pd on cfs.ksite = pd.code  "+
				"where lga.iddevice > 0 and lga.priority = '2' and pd.veryhighsla > 0 and  EXTRACT(epoch from  age(lga.endtime, lga.starttime)/3600 ) is null and starttime >= ? and starttime < ? "+
				"group by lga.kidsupervisor "+
				") as h_reset on h_reset.kidsupervisor=site.id "+
				"left join ( "+
				"select lga.kidsupervisor,count(*) as m_tot from lgalarmrecall lga  "+
				"inner join cfsupervisors cfs on lga.kidsupervisor = cfs.id  "+
				"INNER JOIN lgdevice d ON lga.kidsupervisor=d.kidsupervisor and lga.iddevice=d.iddevice and d.kpi  "+
				"where lga.iddevice > 0 and priority = '3' and starttime >= ? and starttime < ? "+
				"group by lga.kidsupervisor "+
				") as m on m.kidsupervisor=site.id "+
				"left join ( "+
				"select lga.kidsupervisor,count(*) as m_outSla "+
				"from lgalarmrecall lga  "+
				"inner join cfsupervisors cfs on lga.kidsupervisor = cfs.id  "+
				"INNER JOIN lgdevice d ON lga.kidsupervisor=d.kidsupervisor and lga.iddevice=d.iddevice and d.kpi  "+
				"inner join cfplantdata pd on cfs.ksite = pd.code  "+
				"where lga.iddevice > 0 and priority = '3' and pd.veryhighsla > 0 and (pd.veryhighsla < EXTRACT(epoch from  age(lga.endtime, lga.starttime)/3600 )) is true and starttime >= ? and starttime < ? "+
				"group by lga.kidsupervisor "+
				") as m_out on m_out.kidsupervisor=site.id "+
				"left join ( "+
				"select lga.kidsupervisor,count(*) as m_reset  "+
				"from lgalarmrecall lga  "+
				"inner join cfsupervisors cfs on lga.kidsupervisor = cfs.id  "+
				"INNER JOIN lgdevice d ON lga.kidsupervisor=d.kidsupervisor and lga.iddevice=d.iddevice and d.kpi  "+
				"inner join cfplantdata pd on cfs.ksite = pd.code  "+
				"where lga.iddevice > 0 and lga.priority = '3' and pd.veryhighsla > 0 and  EXTRACT(epoch from  age(lga.endtime, lga.starttime)/3600 ) is null and starttime >= ? and starttime < ? "+
				"group by lga.kidsupervisor "+
				") as m_reset on m_reset.kidsupervisor=site.id "+
				"left join ( "+
				"select lga.kidsupervisor,count(*) as l_tot from lgalarmrecall lga  "+
				"inner join cfsupervisors cfs on lga.kidsupervisor = cfs.id  "+
				"INNER JOIN lgdevice d ON lga.kidsupervisor=d.kidsupervisor and lga.iddevice=d.iddevice and d.kpi  "+
				"where lga.iddevice > 0 and priority = '4' and starttime >= ? and starttime < ? "+
				"group by lga.kidsupervisor "+
				") as l on l.kidsupervisor=site.id "+
				"left join ( "+
				"select lga.kidsupervisor,count(*) as l_outSla "+
				"from lgalarmrecall lga  "+
				"inner join cfsupervisors cfs on lga.kidsupervisor = cfs.id  "+
				"INNER JOIN lgdevice d ON lga.kidsupervisor=d.kidsupervisor and lga.iddevice=d.iddevice and d.kpi  "+
				"inner join cfplantdata pd on cfs.ksite = pd.code  "+
				"where lga.iddevice > 0 and priority = '4' and pd.veryhighsla > 0 and (pd.veryhighsla < EXTRACT(epoch from  age(lga.endtime, lga.starttime)/3600 )) is true and starttime >= ? and starttime < ? "+
				"group by lga.kidsupervisor "+
				") as l_out on l_out.kidsupervisor=site.id "+
				"left join ( "+
				"select lga.kidsupervisor,count(*) as l_reset "+
				"from lgalarmrecall lga  "+
				"inner join cfsupervisors cfs on lga.kidsupervisor = cfs.id  "+
				"INNER JOIN lgdevice d ON lga.kidsupervisor=d.kidsupervisor and lga.iddevice=d.iddevice and d.kpi  "+
				"inner join cfplantdata pd on cfs.ksite = pd.code  "+
				"where lga.iddevice > 0 and lga.priority = '4' and pd.veryhighsla > 0 and  EXTRACT(epoch from  age(lga.endtime, lga.starttime)/3600 ) is null and starttime >= ? and starttime < ? "+
				"group by lga.kidsupervisor "+
				") as l_reset on l_reset.kidsupervisor=site.id ";
		Object[] params = {type,timeRange[0],type,timeRange[0],
				timeRange[0],timeRange[1],timeRange[0],timeRange[1],timeRange[0],timeRange[1],
				timeRange[0],timeRange[1],timeRange[0],timeRange[1],timeRange[0],timeRange[1],
				timeRange[0],timeRange[1],timeRange[0],timeRange[1],timeRange[0],timeRange[1],
				timeRange[0],timeRange[1],timeRange[0],timeRange[1],timeRange[0],timeRange[1],
				timeRange[0],timeRange[1],timeRange[0],timeRange[1],timeRange[0],timeRange[1]};
		try {
			
			DatabaseMgr.getInstance().executeUpdate(sql,params);
		} catch (Exception ex) {
			logger.error("error",ex);
		}
	}
	public static void createSiteACK(String type,Date[] timeRange)
	{
		String sql = 
			"delete from private_ack_site_result where type=? and startday>=?;"+
			"insert into private_ack_site_result "+
			"select ?,?,site.id, "+
			"COALESCE(m_tot.m_tot,0) as m_tot,COALESCE(m_ack.m_ack,0) as m_ack,COALESCE(m_avg.m_avg,0) as m_avg, "+
			"COALESCE(a_tot.a_tot,0) as a_tot,COALESCE(a_ack.a_ack,0) as a_ack,COALESCE(a_avg.a_avg,0) as a_avg, "+
			"COALESCE(e_tot.e_tot,0) as e_tot,COALESCE(e_ack.e_ack,0) as e_ack,COALESCE(e_avg.e_avg,0) as e_avg, "+
			"COALESCE(n_tot.n_tot,0) as n_tot,COALESCE(n_ack.n_ack,0) as n_ack,COALESCE(n_avg.n_avg,0) as n_avg "+
			"from "+
			"cfsupervisors as site "+
			"left join "+
			"( "+
			"select recall.kidsupervisor,count(*) as m_tot from lgalarmrecall as recall "+
			"inner join lgdevice as device on recall.kidsupervisor=device.kidsupervisor and recall.iddevice=device.iddevice "+
			"where  starttime>=? and starttime<? "+
			"and extract(hour from starttime)>=6 and extract(hour from starttime)<12 "+
			"and EXTRACT(epoch from  age(endtime, starttime)/60 )>30 "+
			"and "+importantAlarmSQL("recall")+" "+
			"group by recall.kidsupervisor "+
			") as m_tot on m_tot.kidsupervisor=site.id "+
			"left join ( "+
			"select recall.kidsupervisor,count(*) as m_ack from lgalarmrecall as recall "+
			"inner join lgdevice as device on recall.kidsupervisor=device.kidsupervisor and recall.iddevice=device.iddevice "+
			"where starttime>=? and starttime<? "+
			"and ackremotetime is not null "+
			"and extract(hour from starttime)>=6 and extract(hour from starttime)<12 "+
			"and EXTRACT(epoch from  age(endtime, starttime)/60 )>30 "+
			"and "+importantAlarmSQL("recall")+" "+
			"group by recall.kidsupervisor "+
			") as m_ack on site.id=m_ack.kidsupervisor "+
			"left join ( "+
			"select recall.kidsupervisor,avg(EXTRACT(epoch from  age(recall.ackremotetime,recall.starttime)/60)) as m_avg from lgalarmrecall as recall "+
			"inner join lgdevice as device on recall.kidsupervisor=device.kidsupervisor and recall.iddevice=device.iddevice "+
			"where starttime>=? and starttime<? "+
			"and ackremotetime is not null "+
			"and extract(hour from starttime)>=6 and extract(hour from starttime)<12 "+
			"and EXTRACT(epoch from  age(endtime, starttime)/60 )>30 "+
			"and "+importantAlarmSQL("recall")+" "+
			"group by recall.kidsupervisor "+
			") as m_avg on site.id=m_avg.kidsupervisor "+
			"left join ( "+
			"select recall.kidsupervisor,count(*) as a_tot from lgalarmrecall as recall "+
			"inner join lgdevice as device on recall.kidsupervisor=device.kidsupervisor and recall.iddevice=device.iddevice "+
			"where starttime>=? and starttime<? "+
			"and extract(hour from starttime)>=12 and extract(hour from starttime)<18 "+
			"and EXTRACT(epoch from  age(endtime, starttime)/60 )>30 "+
			"and "+importantAlarmSQL("recall")+" "+
			"group by recall.kidsupervisor "+
			") as a_tot on site.id=a_tot.kidsupervisor "+
			"left join ( "+
			"select recall.kidsupervisor,count(*) as a_ack from lgalarmrecall as recall "+
			"inner join lgdevice as device on recall.kidsupervisor=device.kidsupervisor and recall.iddevice=device.iddevice "+
			"where starttime>=? and starttime<? "+
			"and ackremotetime is not null "+
			"and extract(hour from starttime)>=12 and extract(hour from starttime)<18 "+
			"and EXTRACT(epoch from  age(endtime, starttime)/60 )>30 "+
			"and "+importantAlarmSQL("recall")+" "+
			"group by recall.kidsupervisor "+
			") as a_ack on site.id=a_ack.kidsupervisor "+
			"left join ( "+
			"select recall.kidsupervisor,avg(EXTRACT(epoch from  age(recall.ackremotetime,recall.starttime)/60)) as a_avg from lgalarmrecall as recall "+
			"inner join lgdevice as device on recall.kidsupervisor=device.kidsupervisor and recall.iddevice=device.iddevice "+
			"where starttime>=? and starttime<? "+
			"and ackremotetime is not null "+
			"and extract(hour from starttime)>=12 and extract(hour from starttime)<18 "+
			"and EXTRACT(epoch from  age(endtime, starttime)/60 )>30 "+
			"and "+importantAlarmSQL("recall")+" "+
			"group by recall.kidsupervisor "+
			") as a_avg on site.id=a_avg.kidsupervisor "+
			"left join ( "+
			"select recall.kidsupervisor,count(*) as e_tot from lgalarmrecall as recall "+
			"inner join lgdevice as device on recall.kidsupervisor=device.kidsupervisor and recall.iddevice=device.iddevice "+
			"where starttime>=? and starttime<? "+
			"and extract(hour from starttime)>=18 and extract(hour from starttime)<24 "+
			"and EXTRACT(epoch from  age(endtime, starttime)/60 )>30 "+
			"and "+importantAlarmSQL("recall")+" "+
			"group by recall.kidsupervisor "+
			") as e_tot on site.id=e_tot.kidsupervisor "+
			"left join ( "+
			"select recall.kidsupervisor,count(*) as e_ack from lgalarmrecall as recall "+
			"inner join lgdevice as device on recall.kidsupervisor=device.kidsupervisor and recall.iddevice=device.iddevice "+
			"where starttime>=? and starttime<? "+
			"and ackremotetime is not null "+
			"and extract(hour from starttime)>=18 and extract(hour from starttime)<24 "+
			"and EXTRACT(epoch from  age(endtime, starttime)/60 )>30 "+
			"and "+importantAlarmSQL("recall")+" "+
			"group by recall.kidsupervisor "+
			") as e_ack on site.id=e_ack.kidsupervisor "+
			"left join ( "+
			"select recall.kidsupervisor,avg(EXTRACT(epoch from  age(recall.ackremotetime,recall.starttime)/60)) as e_avg from lgalarmrecall as recall "+
			"inner join lgdevice as device on recall.kidsupervisor=device.kidsupervisor and recall.iddevice=device.iddevice "+
			"where starttime>=? and starttime<? "+
			"and ackremotetime is not null "+
			"and extract(hour from starttime)>=18 and extract(hour from starttime)<24 "+
			"and EXTRACT(epoch from  age(endtime, starttime)/60 )>30 "+
			"and "+importantAlarmSQL("recall")+" "+
			"group by recall.kidsupervisor "+
			") as e_avg on site.id=e_avg.kidsupervisor "+
			"left join ( "+
			"select recall.kidsupervisor,count(*) as n_tot from lgalarmrecall as recall "+
			"inner join lgdevice as device on recall.kidsupervisor=device.kidsupervisor and recall.iddevice=device.iddevice "+
			"where starttime>=? and starttime<? "+
			"and extract(hour from starttime)>=0 and extract(hour from starttime)<6 "+
			"and EXTRACT(epoch from  age(endtime, starttime)/60 )>30 "+
			"and "+importantAlarmSQL("recall")+" "+
			"group by recall.kidsupervisor "+
			") as n_tot on site.id=n_tot.kidsupervisor "+
			"left join ( "+
			"select recall.kidsupervisor,count(*) as n_ack from lgalarmrecall as recall "+
			"inner join lgdevice as device on recall.kidsupervisor=device.kidsupervisor and recall.iddevice=device.iddevice "+
			"where starttime>=? and starttime<? "+
			"and ackremotetime is not null "+
			"and extract(hour from starttime)>=0 and extract(hour from starttime)<6 "+
			"and EXTRACT(epoch from  age(endtime, starttime)/60 )>30 "+
			"and "+importantAlarmSQL("recall")+" "+
			"group by recall.kidsupervisor "+
			") as n_ack on site.id=n_ack.kidsupervisor "+
			"left join ( "+
			"select recall.kidsupervisor,avg(EXTRACT(epoch from  age(recall.ackremotetime,recall.starttime)/60)) as n_avg from lgalarmrecall as recall "+
			"inner join lgdevice as device on recall.kidsupervisor=device.kidsupervisor and recall.iddevice=device.iddevice "+
			"where starttime>=? and starttime<? "+
			"and ackremotetime is not null "+
			"and extract(hour from starttime)>=0 and extract(hour from starttime)<6 "+
			"and EXTRACT(epoch from  age(endtime, starttime)/60 )>30 "+
			"and "+importantAlarmSQL("recall")+" "+
			"group by recall.kidsupervisor "+
			") as n_avg on site.id=n_avg.kidsupervisor ";
		Object[] params = {type,timeRange[0],type,timeRange[0],
				timeRange[0],timeRange[1],timeRange[0],timeRange[1],timeRange[0],timeRange[1],
				timeRange[0],timeRange[1],timeRange[0],timeRange[1],timeRange[0],timeRange[1],
				timeRange[0],timeRange[1],timeRange[0],timeRange[1],timeRange[0],timeRange[1],
				timeRange[0],timeRange[1],timeRange[0],timeRange[1],timeRange[0],timeRange[1]};
		try {
			
			DatabaseMgr.getInstance().executeUpdate(sql,params);
		} catch (Exception ex) {
			logger.error("error",ex);
		}
	}
	
	public static String criticalAlarmSQL(String alarmTable)
	{
		return "((device.devmodcode like 'mpxpro%' and var.code in('s_HI','s_LSH','s_LO','s_pre1','s_pre2','s_pre3','s_pre4','s_pre6')) "
				+ "or (device.devmodcode ='pRackCNL1' and "+alarmTable+".priority in ('1','2')) "
		       + "or (device.devmodcode like 'IR33%' and var.code in('ALR_HIGH_TEMP','ALR_LOW_TEMP','S_ALM_ALTA_TEMP_COND','S_ALM_SONDA_1','S_ALM_SONDA_2')) "
		       + "or (lower(device.devmodcode) like 'mpxone%' and var.code in('BAS_HI','BAS_E1','BAS_E2', 'BAS_LO', 'BAS dor')) "
		       + "or (lower(device.devmodcode) like 'heos%' and var.code in('Al_Supply_Probe',"+
                "'Al_Defrost_Probe',"+
                "'Al_Return_Probe',"+
                "'Al_Low_Temp',"+
                "'Al_Low_Temp2',"+
                "'Al_High_Temp',"+
                "'Al_High_Temp2',"+
                "'MOP_Delay_Al_Drv',"+
                "'Low_Suction_Al_Man',"+
                "'Al_Offline_Inverter',"+
                "'General_Inverter_Alarm',"+
                "'Al_PowerPlus_Check_Fail',"+
                "'Al_LP_Tran',"+
                "'HP_Alarm_Transducer',"+
                "'Alarm_S1',"+
                "'Alarm_S2',"+
                "'Alarm_S3',"+
                "'Alarm_S4',"+
                "'Al_LiqdTempPrb',"+
                "'Al_Start_Failure_msk_blocker',"+
                "'Al_Envelop_blocker',"+
                "'General_Inverter_Alarm_blocker',"+
                "'Al_CompPrbNotCfg')) "
		       + "or var.description like '%"+AlarmNotificationMain.HIGH_TEMP+"%') ";
	}
	public static boolean criticalAlarm(Alarm alarm)
	{
		if(alarm.getAddressIn() == 0 || 
			(alarm.getDevmdlCode().startsWith("mpxpro") && (alarm.getCode().equals("s_HI")||alarm.getCode().equals("s_LSH")||alarm.getCode().equals("s_LO")||alarm.getCode().equals("s_pre1")||alarm.getCode().equals("s_pre2")||alarm.getCode().equals("s_pre3")||alarm.getCode().equals("s_pre4")||alarm.getCode().equals("s_pre6"))) ||
			(alarm.getDevmdlCode().equals("pRackCNL1") &&(alarm.getPriority().equals("1")||alarm.getPriority().equals("2")))
		)
			return true;
		return false;
	}
	public static String importantAlarmSQL(String alarmTable)
	{
//		return "((device.devmodcode like 'mpxpro%' and "+alarmTable+".priority = '1') or (device.devmodcode not like 'mpxpro%' and "+alarmTable+".priority in ('1','2'))) ";
		return importantAlarmSQL("device",alarmTable);
	}
	public static String importantAlarmSQL(String deviceTable,String alarmTable)
	{
		return "(("+deviceTable+".devmodcode like 'mpxpro%' and "+alarmTable+".priority = '1') or ("+deviceTable+".devmodcode not like 'mpxpro%' and "+alarmTable+".priority in ('1','2'))) ";
	}
	public static String getInDeviceCode(){
		return "'mpxprov4','mpxprostep2'";
	}
	
	public static class AlarmManageResult{
		String siteName;
		Integer noManage;
		Integer noAction;
		Integer localAction;
		Integer remAction;
		Integer repAction;
		
		public AlarmManageResult() {
			super();
		}
		
		public AlarmManageResult(String siteName1,Integer noManage1,Integer noAction1,Integer localAction1,Integer remAction1,Integer repAction1)
		{
			siteName = siteName1;
			noManage = noManage1;
			noAction = noAction1;
			localAction = localAction1;
			remAction = remAction1;
			repAction = repAction1;
		}
		
		public String getSiteName() {
			return siteName;
		}
		public void setSiteName(String siteName) {
			this.siteName = siteName;
		}
		
		public Integer getNoManage() {
			return noManage;
		}

		public void setNoManage(Integer noManage) {
			this.noManage = noManage;
		}

		public Integer getNoAction() {
			return noAction;
		}
		public void setNoAction(Integer noAction) {
			this.noAction = noAction;
		}
		public Integer getLocalAction() {
			return localAction;
		}
		public void setLocalAction(Integer localAction) {
			this.localAction = localAction;
		}
		public Integer getRemAction() {
			return remAction;
		}
		public void setRemAction(Integer remAction) {
			this.remAction = remAction;
		}
		public Integer getRepAction() {
			return repAction;
		}
		public void setRepAction(Integer repAction) {
			this.repAction = repAction;
		}
		
	}
}
	