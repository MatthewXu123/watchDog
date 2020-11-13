<!DOCTYPE html>
<%@page import="java.util.TimerTask"%>
<%@page import="java.net.URLEncoder"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" 
	import="watchDog.bean.Alarm"
	import="watchDog.bean.SLAResult"
	import="watchDog.bean.ACKResult"
	import="watchDog.service.AlarmService"
	import="watchDog.service.AlarmService.*"
	import="watchDog.service.EnergyService"
	import="watchDog.service.TemperatureKPIService"
	import="watchDog.service.TemperatureKPIService.TemperatureKPI"
	import="java.util.List"
	import="java.util.ArrayList"
	import="watchDog.listener.Dog"
	import="watchDog.service.PropertyMgr"
	import="watchDog.service.RequestService"
	import="watchDog.service.AlarmService.AlarmMonth"
	import="java.util.Date"
	import="java.util.Calendar"
	import="java.util.LinkedHashMap"
	import="com.alibaba.fastjson.JSON"
	import="watchDog.util.DateTool"
	import="watchDog.thread.AlarmNotificationMain"
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<link rel="stylesheet" href="js/framework/bootstrap/4.2.1/css/bootstrap.min.css">
<script src="js/framework/jquery/3.3.1/jquery.min.js"></script>
<script src="js/framework/Chartjs/2.7.3/Chart.bundle.js"></script>
<script src="js/framework/popperjs/1.14.6/popper.min.js"></script>
<script src="js/framework/bootstrap/4.2.1/js/bootstrap.min.js"></script>
<%
	String version = PropertyMgr.getInstance().getProperty(PropertyMgr.SCRIPT_VERSION).getValue();
	request.setAttribute("version", version);
%>
<script src="js/reportManager.js?${version }"></script>
<link rel="stylesheet" href="css/menu.css?${version }">
<link rel="stylesheet" href="css/report.css?${version }"/>
<%
	//params for week: type=w&idsite=140&start=2018-11-05
	//params for month: type=m&idsite=140&start=2018-11
	String userId = RequestService.validation(request, "wx_userid", "wx_userid");
	if(userId == null || userId.length()==0)
	{
		response.sendRedirect("invalid.html");
		return;
	}
	//idsite
	int kidsupervisor = 140;
	String idsiteStr = request.getParameter("idsite");
	if(idsiteStr != null && idsiteStr.length()>0)
		kidsupervisor = Integer.valueOf(idsiteStr);
	else
		return;
	int[] idsite = new int[]{kidsupervisor};
 	//type
	String type = request.getParameter("type");
	boolean isWeekly = true;
	if(type == null || !type.equalsIgnoreCase("w")){
		type = "m";
		isWeekly = false;
	}
	//dateFormat
	String dateFormat = isWeekly ? "yyyy-MM-dd" : "yyyy-MM";
	//start
	Date[] range = DateTool.getLastWeekRange();
	Date lastMonday = range[0];
	String startStr = request.getParameter("start");
	if(startStr != null && startStr.length()>0)
	{
		if(!DateTool.isQualifiedDate(startStr, dateFormat))
			return;
		if(isWeekly){
			range[0] = DateTool.parse(startStr, dateFormat);
			int dayOfWeek = DateTool.getDayOfWeek(range[0]);
			range[0] = DateTool.addDays(range[0], 2 - (dayOfWeek == 1 ? 8 : dayOfWeek));
			range[1] = DateTool.add(range[0], 7, Calendar.DATE);
		}else{
			range = AlarmService.getTimeRangeByMonth(startStr);
		}
	}else{
		if(!isWeekly){
			startStr = DateTool.getMonthFromDay(DateTool.format(DateTool.getFirstDayOfLastMonth(),"yyyy-MM-dd"));
			range = AlarmService.getTimeRangeByMonth(startStr);
		}
	}
	Date startDay = range[0];
	Date endDay = range[1];
	String naviLeft = "";
	String naviRight = "";
	if(isWeekly){
		//type=w 获取指定周的上周和下周
		Date preMonday = DateTool.add(startDay, -7, Calendar.DATE);
		naviLeft = DateTool.format(preMonday);
		
		if(!DateTool.isSameDay(lastMonday, startDay) && startDay.before(lastMonday))
		{
			naviRight = DateTool.format(endDay);
		}
	}
	else{
		//type=m 获取指定月的上月和下个月
		Date preMonth = DateTool.addMonths(DateTool.parse(startStr, dateFormat), -1);
		naviLeft = DateTool.format(preMonth,dateFormat);
		
		Date nextMonth = DateTool.addMonths(DateTool.parse(startStr, dateFormat), 1);
		Date now = DateTool.getFirstDayOfMonth();
		if(nextMonth.before(now) && !DateTool.isSameDay(nextMonth, now)){
			naviRight = DateTool.format(nextMonth,dateFormat);
		}
		
	}
	int lastAlarmId = 99999999;
	
	//energy
	LinkedHashMap<String,List<AlarmMonth>> energyMap = null;
	LinkedHashMap<String,List<AlarmMonth>> globalMeter = null;

	energyMap = EnergyService.getKWHMonthStatistics(kidsupervisor,EnergyService.DAY_PERIOD,AlarmService.NO_VARIABLE,range);
	globalMeter = EnergyService.getTotalMeter(true, energyMap);
	
	String globalData = "";
	if(globalMeter != null)
		globalData = JSON.toJSONString(globalMeter.get("-101_总电量"));
	//week&month
	LinkedHashMap<String,List<AlarmMonth>> energyMapW = null;
	LinkedHashMap<String,List<AlarmMonth>> globalMeterW = null;
	int typePeriodW = isWeekly ? EnergyService.WEEK_PERIOD : EnergyService.MONTH_PERIOD;
	Date[] rangeW = DateTool.getLastWeekRange();
	rangeW[1] = DateTool.add(startDay,7, Calendar.DATE);
	rangeW[0] = DateTool.add(startDay,-7*8, Calendar.DATE);
	if(!isWeekly){
		rangeW[1] = endDay;
		rangeW[0] = DateTool.addMonths(rangeW[1], -6);
	}
	energyMapW = EnergyService.getKWHMonthStatistics(kidsupervisor,typePeriodW,AlarmService.NO_VARIABLE,rangeW);
			
	globalMeterW = EnergyService.getTotalMeter(true, energyMapW);
	
	String globalDataW = "";
	if(globalMeterW != null)
		globalDataW = JSON.toJSONString(globalMeterW.get("-101_总电量"));
	
	//energy
	
	//high temp alarm
	List<AlarmService.AlarmMonth> highAlarm = AlarmService.getResetAlarmDailyStatistic(kidsupervisor, AlarmService.NO_VARIABLE, AlarmService.NO_VARIABLE,AlarmNotificationMain.HIGH_TEMP, range);
	String highAlarmData = "";
	if(highAlarm != null && highAlarm.size()>0)
		highAlarmData = JSON.toJSONString(highAlarm);
	//high temp alarm
	
	//important alarm
	List<AlarmService.AlarmMonth> importantReset = AlarmService.getResetAlarmDailyStatistic(kidsupervisor, AlarmService.NO_VARIABLE, AlarmService.NO_VARIABLE,null, range);
	String importantResetData = "";
	if(importantReset != null && importantReset.size()>0)
		importantResetData = JSON.toJSONString(importantReset);
	
	//kpi
	
	List<TemperatureKPIService.TemperatureKPI> kpi = TemperatureKPIService.getTemperatureKPIStatistic(kidsupervisor,startDay,type);
	
	//important alarm
	SLAResult sla = new SLAResult();
		
	List<SLAResult> list = AlarmService.getSLAResult(idsite, type, startDay);
	if(list.size() > 0 && list != null){
		sla = list.get(0);
	}else{
		sla = null;
	}
	request.setAttribute("sla",sla);
	
	//报警处理结果
	AlarmManageResult ar = new AlarmManageResult();
	List<AlarmManageResult> arList = AlarmService.getAlarmManageResult(idsite, type, startDay);
	if(arList.size() > 0 && arList != null){
		ar = arList.get(0);
	}else{
		ar = null;
	}
	request.setAttribute("ar",ar);
	
	ACKResult ack = AlarmService.getACKResult(kidsupervisor, type, startDay);
	request.setAttribute("ack",ack);
	
	String siteName = Dog.getInstance().getSiteName(kidsupervisor);
	//reportTitle
		String reportTitle = "";
	    String dateRange = "";
		if(isWeekly){
			dateRange = "(" + DateTool.format(startDay, "MM-dd") + "~" + DateTool.format(DateTool.addDays(endDay, -1), "MM-dd") + ")";
			reportTitle = dateRange + "周报表-" + siteName;
		}else{
			dateRange = String.valueOf(DateTool.getAppointedMonth(startDay));
			reportTitle = dateRange + "月报表-" + siteName;
		}
	request.setAttribute("start", DateTool.format(startDay, "MM-dd"));
	request.setAttribute("end", DateTool.format(endDay, "MM-dd"));
%>
<html>
<head>
<title><%=reportTitle%></title>
</head>
<input type="hidden" id="sitename" value="<%=siteName %>"/>
<input type="hidden" id="idsite" value="<%=kidsupervisor%>"/>
<input type="hidden" id="naviLeft" value="<%=naviLeft %>"/>
<input type="hidden" id="naviRight" value="<%=naviRight %>"/>
<input type="hidden" id="type" value="<%=type %>"/>
<input type="hidden" id="dateRange" value="<%=dateRange %>"/>
<body>
<div class="navi">
	<img class="navi_left" src="img/arrowleft.svg">
	<img class="navi_right" src="img/arrowright.svg">
</div>
<div class="table_head">报警关闭效率</div>
<table class="table">
	<tr>
		<th scope="col"></th>
		<th>总个数</th>
		<th>逾期未处理</th>
		<th>逾期比例</th>
		<th>强制复位</th>
	</tr>
	<tr>
		<td scope="row">高温报警</td>
		<td>${sla.htTot }</td>
		<td>${sla.htOutSLA }</td>
		<td>${sla.htOutPercent }</td>
		<td>${sla.htReset }</td>
	</tr>
	<tr>
		<td scope="row">最高等级</td>
		<td>${sla.vhTot }</td>
		<td>${sla.vhOutSLA }</td>
		<td>${sla.vhOutPercent }</td>
		<td>${sla.vhReset }</td>
	</tr>
	<tr>
		<td scope="row">高等级</td>
		<td>${sla.hTot }</td>
		<td>${sla.hOutSLA }</td>
		<td>${sla.hOutPercent }</td>
		<td>${sla.hReset }</td>
	</tr>
	<tr>
		<td scope="row">中等级</td>
		<td>${sla.mTot }</td>
		<td>${sla.mOutSLA }</td>
		<td>${sla.mOutPercent }</td>
		<td>${sla.mReset }</td>
	</tr>
	<tr>
		<td scope="row">低等级</td>
		<td>${sla.lTot }</td>
		<td>${sla.lOutSLA }</td>
		<td>${sla.lOutPercent }</td>
		<td>${sla.lReset }</td>
	</tr>
</table>
<div class="table_head">重要报警确认效率</div>
<table class="table">
	<tr>
		<th scope="col"></th>
		<th>持续时间较长报警</th>
		<th>已确认</th>
		<th>确认平均时间</th>
	</tr>
	<tr>
		<td scope="row">上午</td>
		<td>${ack.mTot }</td>
		<td>${ack.mACK }</td>
		<td>${ack.mAvg }</td>
	</tr>
	<tr>
		<td scope="row">下午</td>
		<td>${ack.aTot }</td>
		<td>${ack.aACK }</td>
		<td>${ack.aAvg }</td>
	</tr>
	<tr>
		<td scope="row">晚上</td>
		<td>${ack.eTot }</td>
		<td>${ack.eACK }</td>
		<td>${ack.eAvg }</td>
	</tr>
	<tr>
		<td scope="row">深夜</td>
		<td>${ack.nTot }</td>
		<td>${ack.nACK }</td>
		<td>${ack.nAvg }</td>
	</tr>
</table>
<div class="table_head">报警处理结果</div>
<table class="table">
	<tr>
		<th scope="col">未管理</th>
		<th>无操作</th>
		<th>现场处理</th>
		<th>远程处理</th>
		<th>备件替换</th>
	</tr>
	<tr>
		<td scope="row">${ar.noManage}</td>
		<td>${ar.noAction}</td>
		<td>${ar.localAction}</td>
		<td>${ar.remAction}</td>
		<td>${ar.repAction}</td>
	</tr>
</table>
<div class="statistic">
	<div class="can_container">
			<canvas id="global_canvas" height="20vh" width="30vw"></canvas>
			<canvas id="globalW_canvas" height="20vh" width="30vw"></canvas>
			<canvas id="highAlarm_canvas" height="20vh" width="30vw"></canvas>
			<canvas id="importantReset_canvas" height="20vh" width="30vw"></canvas>
	</div>
</div>

<div class="table_head">温度KPI</div>
<table class="table table-striped table-bordered table-hover" data-sort-stable="true">
	<tr scope="col" class="textcenter">
		<th>设备</th>
		<th>%</th>
		<th>除霜</th>
		
	</tr>
	<%
	for(int m=0;m<kpi.size();m++) {
		TemperatureKPIService.TemperatureKPI k = kpi.get(m);
		boolean needS= false;
	%>
	<tr>
		<td width="30%" title="<%=k.getDevicedescription() %>"><%=k.getDevicedescription() %></td>
		<td width="55%">
			<%if (k.getPer5() > 0) {%>
				<span class="color_under_critical" style="width:<%=k.getPer5() %>%;height:100%" title="${title}">
					<%=k.getPer5() %>
					<%if (k.getPer5() > 4) {%>
						%
					<%} %>
				</span><!-- 
			<%needS= true;} %>
			<%if (k.getPer4() > 0) {%>
				<%if(needS){%>--><%}%><span class="color_under_setpoint" style="width:<%=k.getPer4() %>%;height:100%" title="${title}">
					<%=k.getPer4() %>
					<%if (k.getPer4() > 4) {%>
						%
					<%} %>
				</span><!-- 
			<%needS= true;} %>
			<%if (k.getPer3() > 0) {%>
				<%if(needS){%>--><%}%><span class="color_over_setpoint" style="width:<%=k.getPer3() %>%;height:100%" title="${title}">
					<%=k.getPer3() %>
					<%if (k.getPer3() > 4) {%>
						%
					<%} %>
				</span><!-- 
			<%needS= true;} %>
			<%if (k.getPer2() > 0) {%>
				<%if(needS){%>--><%} %><span class="color_over_differential" style="width:<%=k.getPer2() %>%;height:100%" title="${title}">
					<%=k.getPer2() %>
					<%if (k.getPer2() > 4) {%>
						%
					<%} %>
				</span><!-- 
			<%needS= true;} %>
			<%if (k.getPer1() > 0) {%>
				<%if(needS){%>--><%} %><span class="color_over_critical" style="width:<%=k.getPer1() %>%;height:100%" title="${title}">
					<%=k.getPer1() %>
					<%if (k.getPer1() > 4) {%>
						%
					<%} %>
				</span><!-- 
			<%needS= true;} %>
			<%if (k.getPer6() > 0) {%>
				<%if(needS){%>--><%} %><span class="color_undefined" style="width:<%=k.getPer6() %>%;height:100%" title="${title}">
					<%=k.getPer6() %>
					<%if (k.getPer6() > 4) {%>
						%
					<%} %>
				</span><!-- 
			<%needS= true;} %>
			<%if(needS){%>--><%} %>
		</td>
		<td width="15%"><%=k.getDeforst() %></td>
	</tr>
	<%} %>
</table>

</body>
<script type="text/javascript">

var global = [],globalW = [],highAlarm=[],importantReset=[];
global = <%=globalData%>;
globalW = <%=globalDataW%>;
highAlarm = <%=highAlarmData%>;
importantReset = <%=importantResetData%>;
</script>
</html>