package watchDog.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import me.chanjar.weixin.common.util.StringUtils;
import watchDog.bean.Alarm;
import watchDog.bean.SiteInfo;
import watchDog.database.DataBaseException;
import watchDog.database.DatabaseMgr;
import watchDog.listener.Dog;
import watchDog.util.StringTool;
import watchDog.wechat.bean.WechatMsg;
import watchDog.wechat.util.sender.Sender;

public class AlarmManageService {
	private static final Logger logger = Logger.getLogger(AlarmManageService.class);
	public static final String ACKNOWLEDGE = "acknowledge";
	public static final String MANAGE = "manage";
	public static final String RESET = "reset";
	private final static int TYPE_ACK = 0;
	private final static int TYPE_RESET = 5;
	
	public final static int TABLE_TYPE_ACTIVE = 0;
	public final static int TABLE_TYPE_RESET = 5;
	public static void ack(int idSite,int[] idAlarms,String user,String comments)
	{
		SiteInfo site = Dog.getInstance().getSiteInfoByIdSite(idSite);
		if(site == null)
			return;	
		String sql = "update lgalarmactive set ackremoteuser=?,ackremotetime=? where kidsupervisor=? and idalarm=? "+
			"and not exists (select * from lgalarmactive where kidsupervisor=? and idalarm=? and ackremoteuser is not null)";
		List<Object[]> paramList = new ArrayList<Object[]>();
		for(int idalarm:idAlarms)
		{
			Object[] params = new Object[6];
			int i= 0;
			params[i++] = user;
			params[i++] = new Timestamp(new Date().getTime());
			params[i++] = idSite;
			params[i++] = idalarm;
			params[i++] = idSite;
			params[i++] = idalarm;
			paramList.add(params);
		}
		try{
			DatabaseMgr.getInstance().executeMulUpdate(sql, paramList);
			if(StringUtils.isNotBlank(site.getTagId()))
				sendMessage(AlarmManageService.TYPE_ACK,site,StringTool.arrayToString(idAlarms, ","),user,comments);
		}catch(DataBaseException ex)
		{
			logger.error("error:",ex);
		}
	}
	public static void manage(int idSite,int tableType,int[] idAlarms,String user,String manageType)
	{
		SiteInfo site = Dog.getInstance().getSiteInfoByIdSite(idSite);
		if(site == null)
			return;	
		String sql = "update lgalarmactive set usespare=? where kidsupervisor=? and idalarm=?";
		if(tableType == TABLE_TYPE_RESET)
			sql = "update lgalarmrecall set usespare=? where kidsupervisor=? and idalarm=?";
		List<Object[]> paramList = new ArrayList<Object[]>();
		for(int idalarm:idAlarms)
		{
			Object[] params = new Object[3];
			int i= 0;
			params[i++] = manageType+";"+user;
			params[i++] = idSite;
			params[i++] = idalarm;
			paramList.add(params);
		}
		try{
			DatabaseMgr.getInstance().executeMulUpdate(sql, paramList);
		}catch(DataBaseException ex)
		{
			logger.error("error:",ex);
		}
	}
	public static void reset(int idSite,int[] idAlarms,String user,String comments)
	{
		reset(idSite,idAlarms,user,comments,true);
	}
	public static void reset(int idSite,int[] idAlarms,String user,String comments,boolean needNotify)
	{
		SiteInfo site = Dog.getInstance().getSiteInfoByIdSite(idSite);
		if(site == null)
			return;	
		String ids = "";
		for(int idalarm:idAlarms)
		{
			ids += idalarm+",";
		}
		ids = ids.substring(0,ids.length()-1);
		String sql = "insert into lgalarmrecall "+
				"select "+
				"kidsupervisor,idalarm,iddevice,idvariable,priority,islogic,starttime,endtime,ackremoteuser,ackremotetime,ackuser,acktime, "+
				"delactionuser,delactiontime,resetuser,resettime,nocturnal,lastupdate,inserttime,current_timestamp,usespare,?,current_timestamp "+
				"from lgalarmactive where kidsupervisor=? and idalarm in ("+ids+");";
		sql += "delete from lgalarmactive where kidsupervisor=? and idalarm in("+ids+");";
		Object[] params = {user,idSite,idSite};
		if(needNotify)
			sendMessage(AlarmManageService.TYPE_RESET,site,ids,user,comments);
		try{
			DatabaseMgr.getInstance().executeUpdate(sql, params);
			if(needNotify && StringUtils.isNotBlank(site.getTagId2()))
				sendMessage(AlarmManageService.TYPE_RESET,site,ids,user,comments);
		}catch(DataBaseException ex)
		{
			logger.error("error:",ex);
		}
	}
	private static void sendMessage(int type,SiteInfo site,String idAlarms,String user,String comments)
	{
		Sender sender = Sender.getInstance(site.getChannel());
		List<Alarm> alarms = AlarmService.getAlarms(site.getSupervisorId(), idAlarms);
		String msg = "";
		msg += site.getDescription()+"\n\n";
		if(type == AlarmManageService.TYPE_ACK)
		{
			msg += user+"正在处理报警\n";
			for(Alarm a:alarms)
			{
				msg += a.getDevice()+"\n"+
					a.getVar()+"\n"+
					"开始:"+a.getHumainStartTime()+"\n"+
					"持续:"+a.getActiveAlarmDuration()+"\n\n";
			}
			if(StringUtils.isNotBlank(comments))
			{
				msg += "备注:"+comments+"\n";
			}
			msg += "消息收件人:\n"+
					Dog.getInstance().getWechatApplicationThread().getTagUsers(site.getTagId());
			sender.sendIM(new WechatMsg.Builder(msg, site.getAgentId(), new String[]{site.getTagId()}).title("报警处理").build());
		}
		else if(type == AlarmManageService.TYPE_RESET)
		{
			msg += "报警被强制复位:\n\n";
			for(Alarm a:alarms)
			{
				msg += a.getDevice()+"\n"+
					a.getVar()+"\n"+
					"开始:"+a.getHumainStartTime()+"\n"+
					"持续:"+a.getActiveAlarmDuration()+"\n\n";
			}
			msg += "复位原因:"+comments+"\n"+
				"提醒：报警未真正消失";
			//send to tag 1
			
			sender.sendIM(new WechatMsg.Builder(msg, site.getAgentId(), new String[]{site.getTagId(),site.getTagId2()}).title("强制复位报警").build());
		}
	}
}
