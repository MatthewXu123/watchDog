<!DOCTYPE html>
<%@page import="java.net.URLEncoder"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" 
	import="watchDog.bean.Alarm"
	import="watchDog.service.AlarmService"
	import="java.util.List"
	import="java.util.ArrayList"
	import="watchDog.listener.Dog"
	import="watchDog.service.PropertyMgr"
	import="watchDog.service.RequestService"
	import="watchDog.service.AlarmService.AlarmMonth"
	import="java.util.Date"
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
<link rel="stylesheet" href="css/menu.css?${version }">
<link rel="stylesheet" href="css/alarm.css?${version }"/>
<script src="js/util/swipe.js?${version }"></script>
<script src="js/util/jsUrlHelper.js?${version }"></script>
<script src="js/alarmStatistic.js?${version }"></script>
<%
	String userId = RequestService.validation(request, "wx_userid", "wx_userid");
	if(userId == null || userId.length()==0)
	{
		response.sendRedirect("invalid.html");
		return;
	}
	int kidsupervisor = 140;
	int idvariableInt = AlarmService.NO_VARIABLE;
	int iddeviceInt = AlarmService.NO_VARIABLE;
	
	String str = request.getParameter("idsite");
	if(str != null && str.length()>0)
		kidsupervisor= Integer.valueOf(str);
	else
		return;
	String description = null;
	String descriptionEncoded = "";
	str = request.getParameter("description");
	if(str != null && str.length()>0)
	{
		descriptionEncoded = URLEncoder.encode(str, "UTF-8");
		str = java.net.URLDecoder.decode(str,"UTF-8");
		description = str;
	}
	
	str = request.getParameter("idvariable");
	if(str != null && str.length()>0)
		idvariableInt = Integer.valueOf(str);
	
	str = request.getParameter("iddevice");
	if(str != null && str.length()>0)
		iddeviceInt = Integer.valueOf(str);
	
	str = request.getParameter("month");
	String month = "";
	if(str != null && str.length()>0)
		month = str;
	int defaultMonthNum = 3;
	
	boolean showAjax = false;
	int lastAlarmId = 99999999;
	if(!"".equals(month))
	{
		Date[] timeRange = AlarmService.getTimeRangeByMonth(month);
		List<Alarm> alarmReset = AlarmService.getResetAlarm(kidsupervisor, lastAlarmId, iddeviceInt, idvariableInt,description, timeRange);
		if(alarmReset.size()>0)
			lastAlarmId = alarmReset.get(alarmReset.size()-1).getIdAlarm();
		request.setAttribute("reset",alarmReset);
		showAjax = alarmReset.size()>200;
	}
	String day = "";
	String monthByDay = "";
	str = request.getParameter("day");
	if(str != null && str.length()>0)
	{
		day = str;
		monthByDay = day.substring(0,day.length()-3);
	}
	if(!"".equals(day))
	{
		Date[] timeRange = AlarmService.getTimeRangeByDay(day);
		List<Alarm> alarmReset = AlarmService.getResetAlarm(kidsupervisor, lastAlarmId, iddeviceInt, idvariableInt,description, timeRange);
		if(alarmReset.size()>0)
			lastAlarmId = alarmReset.get(alarmReset.size()-1).getIdAlarm();
		request.setAttribute("reset",alarmReset);
		showAjax = alarmReset.size()>200;
	}
	
	if((description != null||idvariableInt != AlarmService.NO_VARIABLE||iddeviceInt !=AlarmService.NO_VARIABLE) && "".equals(month) && "".equals(day))
	{
		List<Alarm> alarmReset = AlarmService.getResetAlarm(kidsupervisor, lastAlarmId, iddeviceInt, idvariableInt,description, null);
		if(alarmReset.size()>0)
			lastAlarmId = alarmReset.get(alarmReset.size()-1).getIdAlarm();
		request.setAttribute("reset",alarmReset);
		showAjax = alarmReset.size()>200;
	}
	//"".equals(month) &&
	if( "".equals(day) && description == null && idvariableInt == AlarmService.NO_VARIABLE && iddeviceInt == AlarmService.NO_VARIABLE)
	{
		List<AlarmMonth> alarmByDeviceAll = AlarmService.getAlarmDeviceStatistic(kidsupervisor,true,defaultMonthNum,description,null);
		request.setAttribute("alarmByDeviceAll",alarmByDeviceAll);
	}
	
	String siteName = Dog.getInstance().getSiteName(kidsupervisor);
	request.setAttribute("month", month);
	request.setAttribute("day",day);
	String devdescr = "",vardescr = "";
	if(description == null)
		description = "";
	String iddevice = "",idvariable = "";
	if(iddeviceInt != AlarmService.NO_VARIABLE)
	{
		iddevice = ""+iddeviceInt;
		devdescr = AlarmService.getDeviceNameByIddevice(kidsupervisor, iddeviceInt);
	}
	if(idvariableInt != AlarmService.NO_VARIABLE)
	{
		idvariable = ""+idvariableInt;
		String[] strs = AlarmService.getDeviceNameByIdvariable(kidsupervisor, idvariableInt);
		devdescr = strs[0];
		vardescr = strs[1];
	}
	request.setAttribute("description",description);
	request.setAttribute("iddevice", iddevice);
	request.setAttribute("idvariable",idvariable);
%>
<html>
<head>
<title><%=siteName%></title>
</head>
<input type="hidden" id="sitename" value="<%=siteName %>"/>
<input type="hidden" id="idsite" value="<%=kidsupervisor%>"/>
<input type="hidden" id="monthnum" value="<%=defaultMonthNum%>"/>
<input type="hidden" id="month" value="<%=month %>"/>
<input type="hidden" id="day" value="<%=day%>"/>
<input type="hidden" id="showAjax" value="<%=showAjax %>"/>
<input type="hidden" id="last_reset_id" value="<%=lastAlarmId%>"/>
<input type="hidden" id="iddevice" value="${iddevice }"/>
<input type="hidden" id="devdescr" value="<%=devdescr %>"/>
<input type="hidden" id="vardescr" value="<%=vardescr %>"/>
<input type="hidden" id="idvariable" value="${idvariable }"/>
<input type="hidden" id="description" value="<%=description%>"/>
<body>
<div class="more">
	<div class="dropdown">
	  <img src="img/menu.png" class="btn btn-secondary1 dropdown-toggle" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
	  <ul class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuButton">
	     <li><a class="dropdown-item" href="site.jsp">我的站点</a></li>
	     <li><a class="dropdown-item" href="alarm.jsp?idsite=<%=kidsupervisor%>">活动报警</a></li>
	     <li><a class="dropdown-item" href="resetAlarm.jsp?idsite=<%=kidsupervisor%>">复位报警</a></li>
	  </ul>
	</div>
</div>
<div class="querycondition"></div>
<div class="statistic">
	<div class="can_container">
		<canvas id="alarmMonth" height="13vh" width="36vw"></canvas>
		<c:if test="${month =='' && day ==''}">
		<div class="month_switch">点击切换至6个月数据</div>
		</c:if>
		<c:if test="${!(description !='' || idvariable!='')}">
		<canvas id="alarmDescription" height="20vh" width="30vw"></canvas>
		</c:if>
		<c:if test="${!(idvariable!=''||iddevice!='')}">
		<canvas id="alarmDevice" height="20vh" width="30vw"></canvas>
		</c:if>
	</div>
</div>
<c:if test="${month==''&&day==''&&description==''&&idvariable==''&&iddevice==''}">
<div class="table_head">3个月内控制器报警个数排名明细</div>
<table id="statistic_table" class="statistic_table">
<tr>
	<th>设备</th>
	<th>报警数量</th>
</tr>
<c:forEach items="${alarmByDeviceAll}" var="i">
	<tr iddevice="${i.id }">
		<td>${i.month }<img class="i_history_reset" src="img/history.svg"/></td>
		<td>${i.num }</td>
	</tr>
</c:forEach>
</table>
</c:if>
<c:if test="${month!=''||day!='' ||description!=''||idvariable!=''||iddevice!=''}">
<div class="table_head">历史报警明细</div>
<table id="reset_table" class="reset_table">
	<tr>
		<th>报警说明</th>
		<th>设备</th>
		<th>报警时间</th>
		<th>持续</th>
	</tr>
	<c:forEach items="${reset}" var="i">
		<tr <c:if test="${idvariable==''}">var="${i.idVariable}"</c:if>>
			<td>${i.var }<c:if test="${idvariable==''}"><img class="i_history_reset" src="img/history.svg"/></c:if></td>
			<td>${i.device }</td>
			<td>${i.timeRange}</td>
			<td>${i.alarmDuration }</td>
		</tr>
	</c:forEach>
</table>
<div id="reset_help" class="reset_help">点击显示更多行</div>
</c:if>
</body>
</html>