package watchDog.bean;

import java.util.Date;

import me.chanjar.weixin.common.util.StringUtils;
import watchDog.config.json.DeviceModelConfig;
import watchDog.database.Record;
import watchDog.util.DateTool;

public class Alarm {
	protected Integer idSite = null;
	private String ktype = null;
	private String ident = null;
	private String site = null;
	protected String ip = null;
	protected Integer idDevice = null;
	private String device = null;
	private String devCode = null;
	private Integer idLine = null;
	protected String devmdlCode = null;
	private Integer idAlarm = null;
	private String var = null;
	private Integer idVariable = null;
	private String code = null;
	private String priority = null;
	private Integer addressIn = null;
	private Date startTime = null;
	private Date endTime = null;
	private String ackUser = null;
	private Date ackTime = null;
	private String delUser = null;
	private Date delTime = null;
	private String resetUser = null;
	private Date resetTime = null;
	private String ackRemoteUser = null;
	private Date ackRemoteTime = null;
	private String useSpare = null;
	private String recallresetuser = null;
	
	public Alarm(Record r)
	{
		idSite = (Integer)r.get("idsite");
		ktype = (String)r.get("ktype");
		ident = (String)r.get("ident");
		site = (String)r.get("site");
		ip = (String)r.get("ip");
		idDevice = (Integer)r.get("iddevice");
		device = (String)r.get("device");
		devCode = (String)r.get("devcode");
		idLine = (Integer)r.get("idline");
		devmdlCode = (String)r.get("devmodcode");
		idAlarm = (Integer)r.get("idalarm");
		var = (String)r.get("var");
		idVariable = (Integer)r.get("idvariable");
		code = (String)r.get("code");
		priority = (String)r.get("priority");
		addressIn = (Integer)r.get("addressin");
		startTime = (Date)r.get("starttime");
		endTime = (Date)r.get("endtime");
		ackUser = (String)r.get("ackuser");
		ackTime = (Date)r.get("acktime");
		delUser = (String)r.get("deluser");
		delTime = (Date)r.get("deltime");
		resetUser = (String)r.get("resetuser");
		resetTime = (Date)r.get("resettime");
		ackRemoteUser = (String)r.get("ackremoteuser");
		ackRemoteTime = (Date)r.get("ackremotetime");
		useSpare = (String)r.get("usespare");
		recallresetuser = (String)r.get("recallresetuser");
	}

	public Integer getIdSite() {
		return idSite;
	}

	public String getIdent() {
		return ident;
	}

	public String getSite() {
		return site;
	}

	public Integer getIdDevice() {
		return idDevice;
	}

	public String getDevice() {
		return device;
	}

	public Integer getIdLine() {
		return idLine;
	}

	public String getDevmdlCode() {
		return devmdlCode;
	}

	public Integer getIdAlarm() {
		return idAlarm;
	}

	public String getVar() {
		return var;
	}

	public Integer getIdVariable() {
		return idVariable;
	}

	public String getCode() {
		return code;
	}

	public String getPriority() {
		if(priority != null)
			return priority.trim();
		return priority;
	}

	public Integer getAddressIn() {
		return addressIn;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public String getAckUser() {
		return ackUser;
	}

	public Date getAckTime() {
		return ackTime;
	}

	public String getDelUser() {
		return delUser;
	}

	public Date getDelTime() {
		return delTime;
	}

	public String getResetUser() {
		return resetUser;
	}

	public Date getResetTime() {
		return resetTime;
	}

	public String getDevCode() {
		return devCode;
	}

	public void setDevCode(String devCode) {
		this.devCode = devCode;
	}

	public String getKtype() {
		return ktype;
	}

	public void setKtype(String ktype) {
		this.ktype = ktype;
	}
	public String getIp()
	{
		return this.ip;
	}
	
	public String getUseSpare() {
		return useSpare;
	}

	public void setUseSpare(String useSpare) {
		this.useSpare = useSpare;
	}

	public String getHumainStartTime()
	{
		return DateTool.msgTime(startTime);
	}
	public String getHumainEndTime()
	{
		return DateTool.msgTime(endTime);
	}
	public String getTimeRange()
	{
		if(DateTool.isSameDay(startTime, endTime))
			return getHumainStartTime()+" ~ "+DateTool.format(endTime, "HH:mm");
		else
			return getHumainStartTime()+" ~ "+DateTool.msgTime(endTime);
	}
	public String getAlarmDuration()
	{
		return DateTool.getAlarmDuration(startTime, endTime);
	}
	public String getIsUnit()
	{
		if(devmdlCode == null)
			return "false";
		return DeviceModelConfig.isDevmdlCode(devmdlCode)?"true":"false";
	}
	public String getActiveAlarmDuration()
	{
		return DateTool.getAlarmDuration(startTime, new Date());
	}
	public String getManageStatus()
	{
		if(StringUtils.isNotBlank(useSpare))
		{
			return "已处理";
		}
		else if(StringUtils.isNotBlank(ackRemoteUser))
		{
			return "正在处理";
		}
		return "未处理";
	}
	public String getManageDetail()
	{
		if(StringUtils.isNotBlank(useSpare))
		{
			String[] strs = useSpare.split(";");
			if(strs.length == 2 && strs[0] != null)
			{
				String result = "";
				if(strs[0].startsWith("1"))
					return result += "无操作  "+strs[1];
				if(strs[0].startsWith("2"))
					return result += " 现场处理 "+strs[1];
				if(strs[0].startsWith("3"))
					return result += " 远程处理 "+strs[1];
				if(strs[0].startsWith("4"))
					return result += " 备件替换 "+strs[1];
			}
		}
		else if(StringUtils.isNotBlank(ackRemoteUser))
		{
			return DateTool.msgTime(ackRemoteTime)+" "+ackRemoteUser;
		}
		return "";
	}
	public String getLed()
	{
		if(StringUtils.isNotBlank(useSpare) || StringUtils.isNotBlank(ackRemoteUser))
			return "led_green";
		else
			return "led_red";
	}

	public void setResetUser(String resetUser) {
		this.resetUser = resetUser;
	}

	public String getRecallresetuser() {
		return recallresetuser;
	}

	public void setRecallresetuser(String recallresetuser) {
		this.recallresetuser = recallresetuser;
	}

	@Override
	public String toString() {
		return "Alarm [idSite=" + idSite + ", idAlarm=" + idAlarm + "]";
	}
	
	
}
