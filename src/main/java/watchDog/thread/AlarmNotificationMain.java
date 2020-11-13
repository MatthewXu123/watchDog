package watchDog.thread;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.junit.Test;

import me.chanjar.weixin.common.util.StringUtils;
import watchDog.bean.Alarm;
import watchDog.bean.DeviceValueBean;
import watchDog.bean.Property;
import watchDog.bean.SiteInfo;
import watchDog.database.DataBaseException;
import watchDog.database.DatabaseMgr;
import watchDog.database.Record;
import watchDog.database.RecordSet;
import watchDog.listener.Dog;
import watchDog.property.template.CompanyServiceMsgLogTemplate;
import watchDog.property.template.PropertyConfig;
import watchDog.service.AlarmService;
import watchDog.service.DeviceValueMgr;
import watchDog.service.FaxInfoService;
import watchDog.service.PropertyMgr;
import watchDog.util.DateTool;
import watchDog.util.HttpSendUtil;
import watchDog.util.LogUtil;

import static watchDog.util.LogUtil.*;
import watchDog.util.ValueRetrieve;
import watchDog.wechat.bean.WechatMsg;
import watchDog.wechat.util.sender.Sender;
import watchDog.wechat.util.sender.SenderWechat;

public class AlarmNotificationMain {
    private static final String TIMESTR = "TIMESTR";
    private static final String WECHAT_ALARMSEND_HEARTBEAT_REQUEST_URL = "http://dtu.carel-remote.com:8080/callingService/servlet/heartbeat?client=rv_alarm&encrypt=ENCRYPT_CONTENT&key=watchDog_wechatheartbeat&time="+TIMESTR;
    private static final String REMOTE_ALARM_HEARTBEAT_REQUEST_URL = "http://dtu.carel-remote.com:8080/callingService/servlet/heartbeat?client=rv_alarm&encrypt=ENCRYPT_CONTENT&key=remotealarm";
	public static final int ACTIVE = 1;
	public static final int RESET = 5;
	public static final int ALL = 10;

	public static final int MESSAGE_PURPOSE_TYPE_LEVEL1 = 1;
	public static final int MESSAGE_PURPOSE_TYPE_LEVEL2 = 2;
	public static final int MESSAGE_PURPOSE_TYPE_LEVEL1_REPEAT = 3;
	public static final int MESSAGE_PURPOSE_TYPE_DeviceONOFF = 4;

	public static final String EMOJI_PUSHPIN = "📌";
	public static final String EMOJI_BELL = "🔔";
	public static final String EMOJI_SUN = "🔆";
	public static final String EMOJI_REVOLVING_LIGHT = "🚨";
	public static final String EMOJI_NEWMEMBER = "👨";
	public static final String EMOJI_SPOKER = "🗣";

	public static final String HIGH_TEMP = "高温报警";

	Dog dog = Dog.getInstance();
	Date lastSentDate = null;
	int lastRepeatCheckHour = -1;
	int lastRepeatCheckPerHour = -1;

	public static final int SLEEP_MINUTES = 2;

	private static final Logger logger = Logger.getLogger(AlarmNotificationMain.class);
	
	private PropertyConfig propertyConfig = PropertyConfig.INSTANCE;

	public void checkAlarms() {
		try {
			logger.info("checking alarms");
			// update c:/ip.properties every time
			Sender wechat = Sender.getInstance();
			if (wechat.isDebug() != null) {
				Thread.sleep(SLEEP_MINUTES * 60 * 1000);
				return;
			}
			deadlineNotify();
			String idents = dog.getIdents4AlarmChecking();
			if (!StringUtils.isBlank(idents)) {
				String lastQueryTime = getLastQueryTime();
				Date untilTime = new Date();
				int messagePurposeType = MESSAGE_PURPOSE_TYPE_LEVEL1;
				Map<String, List<String>> active = null;
				Map<String, List<String>> reset = null;
				boolean sendOK = true;

				logger.info("checking alarms: first level");
				// first level
				try {
					messagePurposeType = MESSAGE_PURPOSE_TYPE_LEVEL1;
					active = checkSite(ACTIVE, idents, messagePurposeType, lastQueryTime);
					if (!sendIM(ACTIVE, active, messagePurposeType))
						sendOK = false;
					reset = checkSite(RESET, idents, messagePurposeType, lastQueryTime);
					if (!sendIM(RESET, reset, messagePurposeType))
						sendOK = false;
				} catch (Exception ex) {
					logger.error("", ex);
				}

				logger.info("checking alarms: second level");
				// second level
				try {
					messagePurposeType = MESSAGE_PURPOSE_TYPE_LEVEL2;
					active = checkSite(ACTIVE, idents, messagePurposeType, lastQueryTime);
					if (!sendIM(ACTIVE, active, messagePurposeType))
						sendOK = false;
					reset = checkSite(RESET, idents, messagePurposeType, lastQueryTime);
					if (!sendIM(RESET, reset, messagePurposeType))
						sendOK = false;
				} catch (Exception ex) {
					logger.error("", ex);
				}

				logger.info("checking alarms: repeat, first level");
				// repeat, first level
				try {
					if (canRepeatSend()) {
						messagePurposeType = MESSAGE_PURPOSE_TYPE_LEVEL1_REPEAT;
						active = checkSite(ACTIVE, idents, messagePurposeType, lastQueryTime);
						if (!sendIM(ACTIVE, active, messagePurposeType))
							sendOK = false;
					}
				} catch (Exception ex) {
					logger.error("", ex);
				}

				// if(sendOK)
				PropertyMgr.getInstance().update(PropertyMgr.LAST_QUERY_TIME,
						DateTool.format(untilTime, "yyyy-MM-dd HH:mm:ss"));
				heartbeat(untilTime);
				logger.info("checking alarms: finished");
			} else
				logger.error("no sites");
			// updateAlarmNum();

		} catch (Exception ex) {
			logger.error("", ex);
		} finally {
			try {
				Thread.sleep(SLEEP_MINUTES * 60 * 1000);
				Dog.getInstance().loadFromDB();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public List<String> alarmOfSite(int idsite) throws Exception {
		Dog dog = Dog.getInstance();
		SiteInfo s = dog.getSiteInfoByIdSite(idsite);
		if (s != null) {
			Map<String, List<String>> all = checkSite(ALL, "'" + s.getIdent() + "'", -1, null);
			if (all.size() > 0)
				return all.get(s.getIdent());
			else {
				List<String> temp = new ArrayList<String>();
				temp.add("无报警，干得漂亮！！！");
				return temp;
			}
		} else {
			List<String> temp = new ArrayList<String>();
			temp.add("无此站点哦");
			return temp;
		}
	}

	public Map<String, List<String>> checkSite(int alarmType, String idents, int messagePurposeType,
			String lastQueryTime) throws Exception {
		//TEST
		if(alarmType == RESET)
			lastQueryTime = "2019-11-09 12:55:35";
		if (idents == null || idents.length() == 0)
			return null;
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		String sql = "select site.id as idsite,site.ident,site.ktype,site.description as site,site.ipaddress as ip,device.iddevice,device.description as device,device.idline,device.devmodcode,device.code as devcode,var.description as var,var.idvariable,var.code,var.addressin, "
				+ "active.idalarm,active.priority,active.starttime,active.endtime, "
				+ "active.ackremoteuser,active.ackremotetime, " + "active.ackuser,active.acktime, "
				+ "active.delactionuser as deluser,active.delactiontime as deltime, "
				+ "active.resetuser,active.resettime, " + "active.usespare,active.recallresetuser "
				+ "from lgalarmactive as active " + "inner join cfsupervisors as site on site.id=active.kidsupervisor "
				+ "inner join lgdevice as device on device.kidsupervisor=site.id and device.iddevice=active.iddevice "
				+ "inner join lgvariable as var on var.kidsupervisor=site.id and var.idvariable=active.idvariable ";
		if (messagePurposeType == AlarmNotificationMain.MESSAGE_PURPOSE_TYPE_LEVEL2)
			sql += "inner join private_wechat_receiver as we on we.supervisor_id=site.id and we.tag_id2 is not null ";
		sql += "where site.ident in (" + idents + ") ";
		if (messagePurposeType == AlarmNotificationMain.MESSAGE_PURPOSE_TYPE_LEVEL1)
			sql += "and active.inserttime>='" + lastQueryTime + "' ";
		if (messagePurposeType == AlarmNotificationMain.MESSAGE_PURPOSE_TYPE_LEVEL1)
			sql += "and " + AlarmService.importantAlarmSQL("active") + " ";
		else if (messagePurposeType == AlarmNotificationMain.MESSAGE_PURPOSE_TYPE_LEVEL1_REPEAT) {
			sql += "and " + AlarmService.criticalAlarmSQL("active");
		} else if (messagePurposeType == AlarmNotificationMain.MESSAGE_PURPOSE_TYPE_LEVEL2) {
			sql += "and " + AlarmService.criticalAlarmSQL("active")
					+ "and not exists(select * from private_alarm_important as p where idsite=active.kidsupervisor and idalarm=active.idalarm) ";
		}
		sql += "order by active.kidsupervisor,active.starttime desc";
		if (alarmType == RESET) {
			sql = "select site.id as idsite,site.ident,site.ktype,site.description as site,site.ipaddress as ip,device.iddevice,device.description as device,device.idline,device.devmodcode,device.code as devcode,var.description as var,var.idvariable,var.code,var.addressin, "
					+ "recall.idalarm,recall.priority,recall.starttime,recall.endtime, "
					+ "recall.ackremoteuser,recall.ackremotetime, " + "recall.ackuser,recall.acktime, "
					+ "recall.delactionuser as deluser,recall.delactiontime as deltime, "
					+ "recall.resetuser,recall.resettime, " + "recall.usespare,recall.recallresetuser "
					+ "from lgalarmrecall as recall "
					+ "inner join cfsupervisors as site on site.id=recall.kidsupervisor "
					+ "inner join lgdevice as device on device.kidsupervisor=site.id and device.iddevice=recall.iddevice "
					+ "inner join lgvariable as var on var.kidsupervisor=site.id and var.idvariable=recall.idvariable "
					+ "where site.ident in (" + idents + ") and recall.recalltime>='" + lastQueryTime + "' ";
			if (messagePurposeType == AlarmNotificationMain.MESSAGE_PURPOSE_TYPE_LEVEL1)
				sql += "and " + AlarmService.importantAlarmSQL("recall") + " ";
			// for recalled alarm, no need to send as level 1 repeat
			// else if(messagePurposeType ==
			// AlarmNotificationMain.MESSAGE_PURPOSE_TYPE_LEVEL1_REPEAT)
			// {
			// sql += "and "+AlarmService.criticalAlarmSQL("recall");
			// }
			else if (messagePurposeType == AlarmNotificationMain.MESSAGE_PURPOSE_TYPE_LEVEL2) {
				sql += "and " + AlarmService.criticalAlarmSQL("recall")
						+ "and exists(select * from private_alarm_important as p where idsite=recall.kidsupervisor and idalarm=recall.idalarm) ";
			}
			sql += "order by recall.kidsupervisor,recall.endtime,recall.resettime desc";
		} else if (alarmType == ALL)
			sql = "select site.id as idsite,site.ident,site.ktype,site.description as site,site.ipaddress as ip,device.iddevice,device.description as device,device.idline,device.devmodcode,device.code as devcode,var.description as var,var.idvariable,var.code,var.addressin, "
					+ "active.idalarm,active.priority,active.starttime,active.endtime, "
					+ "active.ackremoteuser,active.ackremotetime, " + "active.ackuser,active.acktime, "
					+ "active.delactionuser as deluser,active.delactiontime as deltime, "
					+ "active.resetuser,active.resettime, " + "active.usespare, active.recallresetuser "
					+ "from lgalarmactive as active "
					+ "inner join cfsupervisors as site on site.id=active.kidsupervisor "
					+ "inner join lgdevice as device on device.kidsupervisor=site.id and device.iddevice=active.iddevice "
					+ "inner join lgvariable as var on var.kidsupervisor=site.id and var.idvariable=active.idvariable "
					+
					// "inner join lgalarmactive as arrive on
					// arrive.kidsupervisor=site.id and
					// arrive.idalarm=active.idalarm "+
					"where " + AlarmService.importantAlarmSQL("active") + " and site.ident in (" + idents + ") "
					+ "order by active.kidsupervisor,active.starttime desc";
		// Object[] obj = new Object[]{new
		// Timestamp(getLastQueryTime().getTime())};
		try {
			RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql);
			if (rs != null && rs.size() > 0) {
				Map<Integer, Map<Integer, Integer>> lineOfflineNum = new HashMap<Integer, Map<Integer, Integer>>();
				Map<Integer, List<Alarm>> siteMap = new HashMap<Integer, List<Alarm>>();
				Integer tempSiteId = null;
				List<Alarm> list = new ArrayList<Alarm>();
				for (int i = 0; i < rs.size(); i++) {
					Record r = rs.get(i);
					Alarm alarm = new Alarm(r);
					if (tempSiteId == null) {
						list.add(alarm);
						siteMap.put(alarm.getIdSite(), list);
					} else if (tempSiteId != null && tempSiteId.intValue() != alarm.getIdSite().intValue()) {
						list = new ArrayList<Alarm>();
						list.add(alarm);
						siteMap.put(alarm.getIdSite(), list);
					} else if (tempSiteId != null && tempSiteId.intValue() == alarm.getIdSite().intValue()) {
						list.add(alarm);
					}
					if (isOfflineAlarm(alarm)) {
						if (lineOfflineNum.containsKey(alarm.getIdSite())) {
							Map<Integer, Integer> m = lineOfflineNum.get(alarm.getIdSite());
							if (m.containsKey(alarm.getIdLine())) {
								Integer num = m.get(alarm.getIdLine());
								m.put(alarm.getIdLine(), ++num);
							} else {
								m.put(alarm.getIdLine(), 1);
							}
						} else {
							Map<Integer, Integer> m = new HashMap<Integer, Integer>();
							m.put(alarm.getIdLine(), 1);
							lineOfflineNum.put(alarm.getIdSite(), m);
						}
					}
					tempSiteId = alarm.getIdSite();
				}
				//TEST
				if(alarmType == RESET){
					List<Alarm> newPlantAlarms = siteMap.get(501);
					testLogger.info("CAREL new plant reset alarm==>" + newPlantAlarms.size());
				}
				
				List<Alarm> secondLevelToSave = new ArrayList<Alarm>();
				Iterator it = siteMap.entrySet().iterator();
				while (it.hasNext()) {
					Entry<Integer, List<Alarm>> entry = (Entry) it.next();
					int idsite = entry.getKey();
					SiteInfo s = Dog.getInstance().getSiteInfoByIdSite(idsite);
					list = entry.getValue();
					list = orderList(list);
					//TEST
					if(alarmType == RESET && idsite == 501){
						testLogger.info("CAREL new plant reset alarm line303==>" + list.size());
					}
					Map<Integer, Integer> offlineMap = lineOfflineNum.get(entry.getKey());
					offlineMap = updateOfflineMap(offlineMap);
					String msg = "";
					String ident = "";
					boolean siteFirst = true;
					Integer tempDevice = null;
					boolean deviceFirst = true;
					int alarmOfDeviceCounter = 1;
					int counter = 0;
					Alarm tmpAlarm = null;
					if (messagePurposeType == AlarmNotificationMain.MESSAGE_PURPOSE_TYPE_LEVEL1_REPEAT && list != null
							&& list.size() > 0) {
						int importantAlarmNum = list.size();
						msg += "\n" + list.get(0).getSite() + "\n";
						msg += "有" + importantAlarmNum + "条重要报警未处理\n";
						Sender wx = Sender.getInstance(s.getChannel());
						if (wx instanceof SenderWechat) {
							msg += alarmSurroundURL(list.get(0).getIdSite(), "查看报警");
						}
						List<String> l = new ArrayList<String>();
						l.add(msg);
						result.put(list.get(0).getIdent(), l);
						continue;
					}
					//TEST
					if(alarmType == RESET && idsite == 501){
						testLogger.info("CAREL new plant reset alarm line331==>" + list.size());
					}
					for (Alarm alarm : list) {
						if (messagePurposeType == AlarmNotificationMain.MESSAGE_PURPOSE_TYPE_LEVEL2
								&& alarmType == ACTIVE) {
							if (importantAlarmTimeCheck(alarm))
								secondLevelToSave.add(alarm);
							else
								continue;
						}
						tmpAlarm = alarm;
						try {
							String msgTmp = msg;
							msg = "";
							if (siteFirst) {
								ident = alarm.getIdent();
								msg += "\n" + alarm.getSite();
								Iterator itOffline = offlineMap.entrySet().iterator();
								while (itOffline.hasNext()) {
									Entry<Integer, Integer> entryOffline = (Entry<Integer, Integer>) itOffline.next();
									msg += "\n线路:" + entryOffline.getKey() + "有" + entryOffline.getValue() + "个设备"
											+ ((alarmType == ACTIVE || alarmType == ALL) ? "离线" : "离线复位");
								}
								siteFirst = false;
							}
							if (isOfflineAlarm(alarm) && offlineMap.containsKey(alarm.getIdLine())) {
								msg = msgTmp + msg;
								counter++;
								continue;
							}
							// offline alarm less than 10 minutes, ignore
							if (isOfflineAlarm(alarm) && alarm.getEndTime() != null) {
								long l = DateTool.diff(alarm.getEndTime(), alarm.getStartTime());
								if (l < 10 * 60 * 1000) {
									msg = msgTmp + msg;
									continue;
								}
							}
							if (tempDevice != null && tempDevice.intValue() != alarm.getIdDevice().intValue()) {
								deviceFirst = true;
								alarmOfDeviceCounter = 1;
							}
							if (deviceFirst) {
								msg += "\n\n";
								msg += alarm.getDevice();
								// if("true".equals(alarm.getIsUnit()))
								// msg +=
								// surroundURL(fast,ShortURLMgr.DEVICE_FAST,alarm,alarm.getDevice());
								// else
								// msg +=
								// surroundURL(fast,ShortURLMgr.DEVICE,alarm,alarm.getDevice());
								deviceFirst = false;
							}
							tempDevice = alarm.getIdDevice();
							msg += "\n报警" + alarmOfDeviceCounter + ":\n" + addEmoji(alarm, alarm.getVar());
							// msg += "\n代码:"+alarm.getCode();
							msg += "\n开始:" + DateTool.msgTime(alarm.getStartTime());
							if (alarm.getEndTime() != null)
								msg += "\n结束:" + DateTool.msgTime(alarm.getEndTime());
							if (alarm.getStartTime() != null && alarm.getEndTime() != null) {
								String tmp = alarm.getAlarmDuration();
								msg += "\n持续:" + tmp;
							}
							if (alarm.getAckTime() != null) {
								msg += "\n确认:" + DateTool.msgTime(alarm.getAckTime()) + "(" + alarm.getAckUser() + ")";
							}
							if (alarm.getDelTime() != null) {
								msg += "\n删除:" + DateTool.msgTime(alarm.getDelTime()) + "(" + alarm.getDelUser() + ")";
							}
							if (alarm.getResetTime() != null) {
								msg += "\n复位:" + DateTool.msgTime(alarm.getAckTime()) + "(" + alarm.getAckUser() + ")";
								msg += "\n" + EMOJI_SPOKER + "报警被强制复位，并未真正消失";
								msg += "\n" + EMOJI_SPOKER + "报警被强制复位，并未真正消失";
								msg += "\n" + EMOJI_SPOKER + "报警被强制复位，并未真正消失";
							}
							// remove || type == ALL
							// when user reply number, message reply shall be
							// fast, could not waiting to get value from local
							// supervisor
							if ((alarmType == ACTIVE) && "boss".equalsIgnoreCase(alarm.getKtype())) {
								if (alarm.getDevmdlCode() != null && alarm.getDevmdlCode().startsWith("mpxpro")) {
									// if(true)
									if (alarm.getCode().equalsIgnoreCase("s_HI")
											|| alarm.getCode().equalsIgnoreCase("s_LSH")) {
										msg += appendValue(ident, alarm.getDevCode(), alarm.getCode(), alarm);
									}
									// else
									// DeviceValueMgr.getInstance().getValueQuick(alarm.getIp(),
									// alarm.getDevCode());
								}
							}
							alarmOfDeviceCounter++;
							counter++;
							int length = 0;
							try {
								length = (msgTmp + msg).getBytes("UTF-8").length;
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (length > 2000) {
								if (result.containsKey(ident)) {
									result.get(ident).add(msgTmp);
								} else {
									List<String> l = new ArrayList<String>();
									l.add(msgTmp);
									result.put(ident, l);
								}
							} else {
								msg = msgTmp + msg;
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					//TEST
					if(alarmType == RESET && idsite == 501){
						testLogger.info("CAREL new plant reset alarm line448==>" + list.size());
						testLogger.info("CAREL new plant reset alarm line449==>" + msg);
						
					}
					if (counter == 0)
						msg = null;
					if (msg != null && msg.length() > 0) {
						if (alarmType == ACTIVE || alarmType == RESET) {
							if (s != null && !StringUtils.isBlank(s.getTagId())) {
								msg += "\n";
								Sender wx = Sender.getInstance(s.getChannel());
								if (wx instanceof SenderWechat
										&& messagePurposeType == AlarmNotificationMain.MESSAGE_PURPOSE_TYPE_LEVEL1) {
									msg += alarmSurroundURL(tmpAlarm.getIdSite(), "查看报警");
									msg += "\n回复数字[" + s.getSupervisorId() + "]查询所有报警";
								}
							}
						}
						if (result.containsKey(ident)) {
							result.get(ident).add(msg);
						} else {
							List<String> l = new ArrayList<String>();
							l.add(msg);
							result.put(ident, l);
						}
					}
					//TEST
					if(alarmType == RESET && idsite == 501){
						testLogger.info("CAREL new plant reset alarm line476==>" + list.size());
						testLogger.info("CAREL new plant reset alarm line477==>" + msg);
						
					}
				}
				if (messagePurposeType == AlarmNotificationMain.MESSAGE_PURPOSE_TYPE_LEVEL2
						&& secondLevelToSave.size() > 0)
					importantAlarmSave(secondLevelToSave);
			}
		} catch (Exception ex) {
			logger.error("error", ex);
			// throw new Exception(ex);
		}
		return result;
	}

	private String appendValue(String ident, String devCode, String code, Alarm alarm) throws Exception {
		String msg = "";
		try {
			String ip = alarm.getIp();

			if (code.equalsIgnoreCase("S_HI")) {
				DeviceValueBean vBean = DeviceValueMgr.getInstance().getValueSlow(ip, devCode);
				if(vBean != null){
					Map<String, String> vMap = vBean.getValues();
					if (vMap != null && vMap.size() > 0) {
						if (vMap.containsKey("s_SetpointWork")) {
							if ("***".equals(vMap.get("s_SetpointWork")))
								return msg;
							msg += "\n温度设定值: " + vMap.get("s_SetpointWork") + " ℃";
						}
						if (vMap.containsKey("airoff")) {
							msg += "\n出风温度: " + vMap.get("airoff") + " ℃";
						}
						if (vMap.containsKey("airon")) {
							msg += "\n回风温度: " + vMap.get("airon") + " ℃";
						}
						if (vMap.containsKey("Po1")) {
							msg += "\n过热度: " + vMap.get("Po1") + " K";
						}
						if (vMap.containsKey("Po2")) {
							msg += "\nEEV开度: " + vMap.get("Po2") + "%";
						}
						if (vMap.containsKey("s_ReleFan")) {
							msg += "\n风机状态: " + (vMap.get("s_ReleFan").startsWith("1") ? "开" : "关");
						}
					}
				}
			} else if (code.equalsIgnoreCase("s_LSH")) {
				// Map<String,String> vMap =
				// ValueRetrieve.getValue(ip,"1.010","Po1","Po2","Po4","s_SuctionProbe","s_ReleFan");
				Map<String, String> vMap = ValueRetrieve.getValue(ip, devCode, "Po1", "Po2", "Po4", "s_SuctionProbe",
						"s_ReleFan");
				if (vMap != null && vMap.size() > 0) {
					if (vMap.containsKey("Po1")) {
						if ("***".equals(vMap.get("Po1")))
							return msg;
						msg += "\n过热度: " + vMap.get("Po1") + " K";
					}
					if (vMap.containsKey("Po2")) {
						msg += "\nEEV开度: " + vMap.get("Po2") + "%";
					}
					if (vMap.containsKey("Po4")) {
						msg += "\n吸气压力饱和温度: " + vMap.get("Po4") + " ℃";
					}
					if (vMap.containsKey("s_SuctionProbe")) {
						msg += "\n吸气温度: " + vMap.get("s_SuctionProbe") + " ℃";
					}
					if (vMap.containsKey("s_ReleFan")) {
						msg += "\n风机状态: " + (vMap.get("s_ReleFan").startsWith("1") ? "开" : "关");
					}
				}
			}
		} catch (Exception ex) {
			logger.error("error:", ex);
		}
		logger.info(msg);
		return msg;
	}

	private List<Alarm> orderList(List<Alarm> list) {
		List<Alarm> result = new ArrayList<Alarm>();
		Map<Integer, String> added = new HashMap<Integer, String>();
		for (int i = 0; i < list.size(); i++) {
			Alarm alarm = list.get(i);
			if (added.containsKey(alarm.getIdAlarm()))
				continue;
			result.add(alarm);
			for (int j = i + 1; j < list.size(); j++) {
				Alarm a2 = list.get(j);
				if (alarm.getIdDevice() == a2.getIdDevice()) {
					result.add(a2);
					added.put(a2.getIdAlarm(), null);
				}
			}
		}
		return result;
	}

	private Map<Integer, Integer> updateOfflineMap(Map<Integer, Integer> offlineMap) {
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		if (offlineMap == null)
			return result;
		Iterator it = offlineMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, Integer> entry = (Entry<Integer, Integer>) it.next();
			Integer key = entry.getKey();
			Integer value = entry.getValue();
			if (value > 10)
				result.put(key, value);
		}
		return result;
	}

	private boolean isOfflineAlarm(Alarm alarm) {
		if ("OFFLINE".equalsIgnoreCase(alarm.getCode()) || alarm.getAddressIn() == 0)
			return true;
		else
			return false;
	}

	public static boolean sendIM(int type, Map<String, List<String>> msgMap, int messagePurposeType) {
		//TEST
		if(type == RESET){
			List<String> list = msgMap.get("501");
			if(list != null && list.size() != 0){
				for (String string : list) {
					testLogger.info("msg:" + string);
				}
			}
				
		}
		Dog dog = Dog.getInstance();
		boolean sendOK = true;
		if (msgMap.size() > 0) {
			Iterator<String> it = msgMap.keySet().iterator();
			while (it.hasNext()) {
				boolean first = true;
				String ident = it.next();
				List<String> msgs = msgMap.get(ident);
				for (String msg : msgs) {
					if (msg != null && msg.length() > 0) {
						String title = "";
						if (first) {
							if (messagePurposeType == AlarmNotificationMain.MESSAGE_PURPOSE_TYPE_LEVEL2) {
								switch (type) {
								case ACTIVE:
									msg = "尊敬的门店领导，您的门店有重要报警未及时解决" + AlarmNotificationMain.EMOJI_REVOLVING_LIGHT + "\n"
											+ msg;
									title = "报警未处理";
									break;
								case RESET:
									msg = "重要报警复位" + "\n" + msg;
									title = "报警复位";
									break;
								}
							} else if (messagePurposeType == AlarmNotificationMain.MESSAGE_PURPOSE_TYPE_LEVEL1_REPEAT) {
								switch (type) {
								case ACTIVE:
									msg = "重要报警未处理" + AlarmNotificationMain.EMOJI_REVOLVING_LIGHT + "\n" + msg;
									title = "重要报警未处理";
									break;
								}
							}
							// LEVEL 1
							else {
								switch (type) {
								case ACTIVE:
									msg = "新报警" + AlarmNotificationMain.EMOJI_REVOLVING_LIGHT + "\n" + msg;
									title = "新报警";
									break;
								case RESET:
									msg = "报警复位" + "\n" + msg;
									title = "报警复位";
									break;
								case ALL:
									msg = "当前所有报警" + "\n" + msg;
									title = "所有报警";
									break;
								}
							}
							first = false;
						}
						if (messagePurposeType == AlarmNotificationMain.MESSAGE_PURPOSE_TYPE_DeviceONOFF) {
							msg = "柜子关闭提示" + "\n" + msg + "\n" + "请检查柜子，以防货损。";
							title = "柜子关闭提示";
						}
						SiteInfo s = dog.getSiteInfo(ident);
						if (s != null) {
							// only ALDI resend every hour
							if (messagePurposeType == AlarmNotificationMain.MESSAGE_PURPOSE_TYPE_LEVEL1_REPEAT
									&& s.getDescription().toUpperCase().indexOf("ALDI") < 0) {
								Calendar c = Calendar.getInstance();
								int hour = c.get(Calendar.HOUR_OF_DAY);
								if (hour % 3 != 0)
									continue;
							}
							String tagId = s.getTagId();
							if (messagePurposeType == AlarmNotificationMain.MESSAGE_PURPOSE_TYPE_LEVEL2)
								tagId = s.getTagId2();
							// by Kevin, if send to solder, but soder is empty,
							// try to send to commander
							else {
								if (StringUtils.isBlank(tagId))
									tagId = s.getTagId2();
							}
							if (StringUtils.isBlank(tagId))
								continue;
							Sender wx = Sender.getInstance(s.getChannel());
							sendOK = wx.sendIM(new WechatMsg.Builder(msg, s.getAgentId(), new String[]{tagId}).title(title).build());
							Dog.sleep(1000);
						}
					}
				}
			}
		}
		return sendOK;
	}

	private String getLastQueryTime() {
		Property p = PropertyMgr.getInstance().getProperty(PropertyMgr.LAST_QUERY_TIME);
		if (p == null || p.getValue() == null || p.getValue().length() == 0) {
			return DateTool.format(DateTool.addDays(-3), "yyyy-MM-dd HH:mm:ss");
		} else {
			Date d = DateTool.parse(p.getValue(), "yyyy-MM-dd HH:mm:ss");
			Date d0 = DateTool.addDays(-3);
			if (d.before(d0))
				return DateTool.format(d0, "yyyy-MM-dd HH:mm:ss");
			else
				return DateTool.format(d, "yyyy-MM-dd HH:mm:ss");
		}
	}

	private String addEmoji(Alarm alarm, String text) {
		if (HIGH_TEMP.equals(text))
			text = text + AlarmNotificationMain.EMOJI_SUN;
		else if (AlarmService.criticalAlarm(alarm))
			text += AlarmNotificationMain.EMOJI_PUSHPIN;
		return text;
	}

	public static String alarmSurroundURL(int idSite, String text) {
		String msg = "";
		String domainName = Sender.getInstance(Sender.CHANNEL_WECHAT).getDomainName();
		String url = "https://" + domainName + "/watchDog/servlet/auth?path=alarm.jsp?idsite=" + idSite;
		url = Sender.getInstance().getURL(url);
		msg = "\n\n<a href=\"" + url + "\">" + text + "</a>";
		return msg;
	}
	public static String cancelCallingSurroundURL() {
        String msg = "";
        String domainName = Sender.getInstance(Sender.CHANNEL_WECHAT).getDomainName();
        String url = "https://" + domainName + "/watchDog/servlet/auth?path=suggestion/create?type=1";
        url = Sender.getInstance().getURL(url);
        msg = "<a href=\"" + url + "\">取消电话报警</a>";
        return msg;
    }

	private void deadlineNotify() {
		Date now = new Date();
		Dog dog = Dog.getInstance();
		Sender sender = Sender.getInstance(Sender.CHANNEL_WECHAT);
		Calendar c = Calendar.getInstance();
		// 10 AM, every wednesday, 60 days before
		if (c.get(Calendar.HOUR_OF_DAY) == 10 && (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
				&& !DateTool.isSameDay(lastSentDate, now)) {
			Iterator it = dog.getAllSites();
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				String ident = (String) entry.getKey();
				SiteInfo site = dog.getSiteInfo(ident);
				if (site != null && site.getDeadline() != null) {
					c.setTime(site.getDeadline());
					c.add(Calendar.DATE, -60);
					Date d_31 = c.getTime();
					if (now.after(d_31) && !now.after(site.getDeadline())) {
						if (!StringUtils.isBlank(site.getAgentId()) && !StringUtils.isBlank(site.getTagId())) {
							sender.sendIM(new WechatMsg.Builder(propertyConfig.getValue(CompanyServiceMsgLogTemplate.CSM_RENEW.getKey(), new Object[]{site.getDescription(), DateTool.format(site.getDeadline())}), site.getAgentId(), new String[]{site.getTagId()})
											.build());
						}
						sender.sendIMToSales(new WechatMsg.Builder(propertyConfig.getValue(CompanyServiceMsgLogTemplate.CSM_INQUIRY.getKey(), new Object[]{site.getDescription(), DateTool.format(site.getDeadline())})).build());
					} else if (now.after(site.getDeadline())) {
						if (!StringUtils.isBlank(site.getAgentId()) && !StringUtils.isBlank(site.getTagId())) {
							sender.sendIM(new WechatMsg.Builder(propertyConfig.getValue(CompanyServiceMsgLogTemplate.CSM_STOP.getKey(), new Object[]{site.getDescription(), DateTool.format(site.getDeadline())}), site.getAgentId(), new String[]{site.getTagId()})
											.build());
						}
					}
				}
			}
			lastSentDate = new Date();
		}
	}

	private static boolean importantAlarmTimeCheck(Alarm alarm) {
		Date now = new Date();
		SiteInfo site = Dog.getInstance().getSiteInfo(alarm.getIdent());
		if (site != null) {
			Integer minutes = site.getSendTag2Delay();
			if (minutes > 0) {
				Date d = DateTool.add(alarm.getStartTime(), minutes, Calendar.MINUTE);
				if (now.after(d))
					return true;
			}
		}
		return false;
	}

	private void importantAlarmSave(List<Alarm> secondLevelToSave) {
		List<Object[]> vals = new ArrayList<Object[]>();
		for (Alarm alarm : secondLevelToSave) {
			Object[] a = new Object[3];
			a[0] = alarm.getIdSite();
			a[1] = alarm.getIdAlarm();
			a[2] = new Timestamp(new Date().getTime());
			vals.add(a);
		}
		try {
			DatabaseMgr.getInstance().executeMulUpdate("insert into private_alarm_important values(?,?,?)", vals);
		} catch (DataBaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean canRepeatSend() {
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		// first hour, do not send repeat
		if (lastRepeatCheckHour == -1) {
			lastRepeatCheckHour = hour;
			return false;
		}
		if (hour == lastRepeatCheckHour)
			return false;
		lastRepeatCheckHour = hour;
		return true;
		// if(hour>=6 && hour<=23 && hour % 3 == 0)
		// {
		// return true;
		// }
		//
		// return false;
	}

	private boolean checkPerHour() {
		boolean flag = false;
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		// first hour, do not check
		if (lastRepeatCheckPerHour == -1) {
			lastRepeatCheckPerHour = hour;
			flag = false;
		} else if (hour == lastRepeatCheckPerHour)
			flag = false;
		else {
			lastRepeatCheckPerHour = hour;
			flag = true;
		}
		return flag;
	}
	@Test
	public void t() throws UnsupportedEncodingException
	{
	    heartbeat(new Date());
	}
	private void heartbeat(Date time)
	{
	    try{
	        wechatSendHeartbeat(time);
	        remoteAlarmHeartbeat();
	    }catch(Exception ex){
	        logger.error("",ex);
	    }
	    
	}
	private void wechatSendHeartbeat(Date time) throws UnsupportedEncodingException
	{
	    String url = WECHAT_ALARMSEND_HEARTBEAT_REQUEST_URL
                .replace(FaxInfoService.ENCRYPT_CONTENT, URLEncoder.encode(FaxInfoService.getEncryptContent(), "utf-8"))
                .replace(TIMESTR, URLEncoder.encode(DateTool.format(time, "yyyy-MM-dd HH:mm:ss"), "utf-8"));
	    logger.info("send hb:");
        String result = HttpSendUtil.INSTANCE.sendGet(new String(url.getBytes("iso-8859-1"), "utf-8"),
                HttpSendUtil.CHAR_ENCODING_UTF8);
	}
	private void remoteAlarmHeartbeat() throws UnsupportedEncodingException
	{
	    String sql = "select count(*)::int from "+
					"(select distinct kidsupervisor from lgalarmactive where idalarm>0 and inserttime>current_timestamp - interval'60 minute')as a ";
	    try {
            RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql);
            if(rs != null)
            {
                int num = (int)rs.get(0).get(0);
                logger.info("alarm sites hb:"+num);
                if(num>5)
                {
                    String url = REMOTE_ALARM_HEARTBEAT_REQUEST_URL
                            .replace(FaxInfoService.ENCRYPT_CONTENT, URLEncoder.encode(FaxInfoService.getEncryptContent(), "utf-8"));
                    String result = HttpSendUtil.INSTANCE.sendGet(new String(url.getBytes("iso-8859-1"), "utf-8"),
                            HttpSendUtil.CHAR_ENCODING_UTF8);
                }
            }
        } catch (DataBaseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error("",e);
        }
	    
	}
}
