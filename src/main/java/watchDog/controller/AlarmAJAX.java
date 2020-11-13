package watchDog.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import watchDog.bean.Alarm;
import watchDog.service.AlarmService;

import com.alibaba.fastjson.JSON;

public class AlarmAJAX extends HttpServlet{

	private static final long serialVersionUID = 318130974894549262L;
	private static final String DETAIL_LIST = "detail_list";
	private static final String STATISTIC_MONTH = "statistic_month";
	private static final String STATISTIC_DAILY = "statistic_daily";
	private static final String STATISTIC_HOURLY = "statistic_hourly";
	private static final String STATISTIC_ALARM_DEVICE = "statistic_alarm_device";
	private static final String ALARM_DESCRIPTION = "statistic_alarm_description";
	private static final String DEVICE_ALARM_MONTH = "statistic_device_alarm_month";
	protected void doPost(HttpServletRequest request, HttpServletResponse response)  
            throws ServletException, IOException {  
		Date d1 = new Date();
        request.setCharacterEncoding("UTF-8");  
        response.setCharacterEncoding("UTF-8");
        String cmd = (String)request.getParameter("cmd");
        String json = "";
        if(DETAIL_LIST.equals(cmd))
        {
        	Date[] timeRange = null;
	        String str=request.getParameter("idsite");
	        int idsite = Integer.valueOf(str);
	        str = request.getParameter("lastResetId");
	        int lastResetId = Integer.valueOf(str);
	        List<Alarm> alarmList = new ArrayList<Alarm>();
	        str = request.getParameter("idvariable");
	        int idvariable = AlarmService.NO_VARIABLE;
	        if(str != null && str.length() > 0)
	        	idvariable = Integer.valueOf(str);
	        str = request.getParameter("iddevice");
	        int iddevice = AlarmService.NO_VARIABLE;
	        if(str != null && str.length() > 0)
	        	iddevice = Integer.valueOf(str);
	        str = request.getParameter("month");
	        if(str != null && str.length() > 0)
	        	timeRange = AlarmService.getTimeRangeByMonth(str);
	        str = request.getParameter("day");
	        if(str != null && str.length() > 0)
	        	timeRange = AlarmService.getTimeRangeByDay(str);
	        String description = null;
	        str = request.getParameter("description");
	        if(str != null && str.length()>0)
	        {
	        	str = java.net.URLDecoder.decode(str,"UTF-8");
	        	description = str;
	        }
	        boolean all = false;
	        str = request.getParameter("all");
	        if("true".equals(str))
	        	all = true;
	        alarmList = AlarmService.getResetAlarm(idsite,all, lastResetId,iddevice,idvariable,description,timeRange);
	        json = JSON.toJSONString(alarmList);
        }
        else if(STATISTIC_MONTH.equals(cmd))
        {
        	String str=request.getParameter("idsite");
	        int idsite = Integer.valueOf(str);
	        List<AlarmService.AlarmMonth> alarmList = new ArrayList<AlarmService.AlarmMonth>();
	        str = request.getParameter("idvariable");
	        int idvariable = AlarmService.NO_VARIABLE;
	        if(str != null && str.length() > 0)
	        	idvariable = Integer.valueOf(str);
	        int iddevice = AlarmService.NO_VARIABLE;
	        str = request.getParameter("iddevice");
	        if(str != null && str.length() > 0)
	        	iddevice = Integer.valueOf(str);
	        String alarmDescription = null;
	        str = request.getParameter("description");
	        if(str != null && str.length()>0)
	        	alarmDescription = str;
	        alarmList = AlarmService.getResetAlarmMonthlyStatistic(idsite,iddevice,idvariable,alarmDescription,null);
	        json = JSON.toJSONString(alarmList);
        }
        else if(STATISTIC_DAILY.equals(cmd))
        {
        	String str=request.getParameter("idsite");
	        int idsite = Integer.valueOf(str);
	        List<AlarmService.AlarmMonth> alarmList = new ArrayList<AlarmService.AlarmMonth>();
	        str = request.getParameter("idvariable");
	        int idvariable = AlarmService.NO_VARIABLE;
	        if(str != null && str.length() > 0)
	        	idvariable = Integer.valueOf(str);
	        int iddevice = AlarmService.NO_VARIABLE;
	        str = request.getParameter("iddevice");
	        if(str != null && str.length() > 0)
	        	iddevice = Integer.valueOf(str);
	        Date[] timeRange = null;
	        str = request.getParameter("month");
	        if(str != null && str.length()>0)
	        	timeRange = AlarmService.getTimeRangeByMonth(str);
	        String alarmDescription = null;
	        str = request.getParameter("description");
	        if(str != null && str.length()>0)
	        	alarmDescription = str;
	        alarmList = AlarmService.getResetAlarmDailyStatistic(idsite,iddevice,idvariable,alarmDescription,timeRange);
	        json = JSON.toJSONString(alarmList);
        }
        else if(STATISTIC_HOURLY.equals(cmd))
        {
        	String str=request.getParameter("idsite");
	        int idsite = Integer.valueOf(str);
	        List<AlarmService.AlarmMonth> alarmList = new ArrayList<AlarmService.AlarmMonth>();
	        str = request.getParameter("idvariable");
	        int idvariable = AlarmService.NO_VARIABLE;
	        if(str != null && str.length() > 0)
	        	idvariable = Integer.valueOf(str);
	        int iddevice = AlarmService.NO_VARIABLE;
	        str = request.getParameter("iddevice");
	        if(str != null && str.length() > 0)
	        	iddevice = Integer.valueOf(str);
	        Date[] timeRange = null;
	        str = request.getParameter("day");
	        if(str != null && str.length()>0)
	        	timeRange = AlarmService.getTimeRangeByDay(str);
	        String alarmDescription = null;
	        str = request.getParameter("description");
	        if(str != null && str.length()>0)
	        	alarmDescription = str;
	        alarmList = AlarmService.getResetAlarmHourlyStatistic(idsite,iddevice,idvariable,alarmDescription,timeRange);
	        json = JSON.toJSONString(alarmList);
        }
        else if(DEVICE_ALARM_MONTH.equals(cmd))
        {
	        String str=request.getParameter("idsite");
	        int idsite = Integer.valueOf(str);
	        List<AlarmService.AlarmMonth> alarmList = new ArrayList<AlarmService.AlarmMonth>();
	        str = request.getParameter("iddevice");
	        int iddevice = Integer.valueOf(str);
	        String alarmDescription = null;
	        str = request.getParameter("description");
	        if(str != null && str.length()>0)
	        	alarmDescription = str;
	        alarmList = AlarmService.getResetAlarmMonthlyStatistic(idsite,iddevice,AlarmService.NO_VARIABLE,alarmDescription,null);
	        json = JSON.toJSONString(alarmList);
        }
        else if(STATISTIC_ALARM_DEVICE.equals(cmd))
        {
        	String str=request.getParameter("idsite");
	        int idsite = Integer.valueOf(str);
	        List<AlarmService.AlarmMonth> alarmList = new ArrayList<AlarmService.AlarmMonth>();
	        str = request.getParameter("monthNum");
	        int monthNum = Integer.valueOf(str);
	        str = request.getParameter("all");
	        boolean all = false;
	        if("true".equals(str))
	        	all = true;
	        str = request.getParameter("month");
	        Date[] timeRange = null;
	        if(str != null && str.length()>0)
	        	timeRange = AlarmService.getTimeRangeByMonth(str);
	        str = request.getParameter("day");
	        if(str != null && str.length()>0)
	        	timeRange = AlarmService.getTimeRangeByDay(str);
	        String alarmDescription = null;
	        str = request.getParameter("description");
	        if(str != null && str.length()>0)
	        	alarmDescription = str;
	        alarmList = AlarmService.getAlarmDeviceStatistic(idsite,all,monthNum,alarmDescription,timeRange);
	        json = JSON.toJSONString(alarmList);
        }
        else if(ALARM_DESCRIPTION.equals(cmd))
        {
        	String str=request.getParameter("idsite");
	        int idsite = Integer.valueOf(str);
	        List<AlarmService.AlarmMonth> alarmList = new ArrayList<AlarmService.AlarmMonth>();
	        str = request.getParameter("monthNum");
	        int monthNum = Integer.valueOf(str);
	        str = request.getParameter("month");
	        Date[] timeRange = null;
	        if(str != null && str.length()>0)
	        	timeRange = AlarmService.getTimeRangeByMonth(str);
	        str = request.getParameter("day");
	        if(str != null && str.length()>0)
	        	timeRange = AlarmService.getTimeRangeByDay(str);
	        int iddevice = AlarmService.NO_VARIABLE;
	        str = request.getParameter("iddevice");
	        if(str != null && str.length()>0)
	        	iddevice = Integer.valueOf(str);
	        alarmList = AlarmService.getAlarmDescriptionStatistic(idsite,iddevice,monthNum,timeRange);
	        json = JSON.toJSONString(alarmList);
        }
        response.setHeader("Cache-Control", "no-cache");
        OutputStream outputStream = response.getOutputStream();
        Writer outputStreamWriter = new OutputStreamWriter(outputStream, "UTF8");
        Writer printWriter = new PrintWriter(outputStreamWriter);
        printWriter.write(json);
        printWriter.flush();
        
    }
	@Override 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException 
	{
		doPost(request, response);
	}
}
