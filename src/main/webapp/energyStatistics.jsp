<!DOCTYPE html>
<%@page import="java.net.URLEncoder"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" 
	import="watchDog.bean.Alarm"
	import="watchDog.service.AlarmService"
	import="watchDog.service.EnergyService"
	import="java.util.List"
	import="java.util.ArrayList"
	import="watchDog.listener.Dog"
	import="watchDog.service.PropertyMgr"
	import="watchDog.service.RequestService"
	import="watchDog.service.AlarmService.AlarmMonth"
	import="java.util.Date"
	import="java.util.LinkedHashMap"
	import="com.alibaba.fastjson.JSON"
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<link rel="stylesheet" href="js/framework/bootstrap/4.2.1/css/bootstrap.min.css">
<script src="js/framework/jquery/3.3.1/jquery.min.js"></script>
<script src="js/framework/Chartjs/2.7.3/Chart.bundle.js"></script>
<script src="js/framework/popperjs/1.14.6/popper.min.js"></script>
<script src="js/framework/bootstrap/4.2.1/js/bootstrap.min.js"></script>
<script src="js/energyStatistic.js?${version }"></script>
<%
	String version = PropertyMgr.getInstance().getProperty(PropertyMgr.SCRIPT_VERSION).getValue();
	request.setAttribute("version", version);
%>
<link rel="stylesheet" href="css/menu.css?${version }">
<link rel="stylesheet" href="css/alarm.css?${version }"/>
<script src="js/util/swipe.js?${version }"></script>
<script src="js/util/jsUrlHelper.js?${version }"></script>
<%
	String userId = RequestService.validation(request, "wx_userid", "wx_userid");
	if(userId == null || userId.length()==0)
	{
		response.sendRedirect("invalid.html");
		return;
	}
	int kidsupervisor = 140;
	int idvariableInt = AlarmService.NO_VARIABLE;
	
	String str = request.getParameter("idsite");
	if(str != null && str.length()>0)
		kidsupervisor= Integer.valueOf(str);
	else
		return;
	
	str = request.getParameter("idvariable");
	if(str != null && str.length()>0)
		idvariableInt = Integer.valueOf(str);
	
	str = request.getParameter("month");
	String month = "";
	if(str != null && str.length()>0)
		month = str;
	int defaultMonthNum = 3;
	
	LinkedHashMap<String,List<AlarmMonth>> energyMap = null;
	LinkedHashMap<String,List<AlarmMonth>> globalMeter = null;
	LinkedHashMap<String,List<AlarmMonth>> detailMeter = null;
	Date[] timeRange = null;
	int typePeriod = EnergyService.MONTH_PERIOD;
	if(!"".equals(month))
	{
		timeRange = AlarmService.getTimeRangeByMonth(month);
		typePeriod = EnergyService.DAY_PERIOD;
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
		timeRange = AlarmService.getTimeRangeByDay(day);
		typePeriod = EnergyService.HOUR_PERIOD;
	}
	str = request.getParameter("idvariable");
	if(str != null && str.length()>0)
		idvariableInt = Integer.valueOf(str);
	
	energyMap = EnergyService.getKWHMonthStatistics(kidsupervisor,typePeriod,idvariableInt,timeRange);
	globalMeter = EnergyService.getTotalMeter(true, energyMap);
	detailMeter = EnergyService.getTotalMeter(false, energyMap);
	
	request.setAttribute("globalMeter",globalMeter);
	request.setAttribute("detailMeter",detailMeter);

	String globalData = "",detailData = "";
	if(globalMeter != null)
		globalData = JSON.toJSONString(globalMeter);
	if(detailMeter != null)
		detailData = JSON.toJSONString(detailMeter);
	String siteName = Dog.getInstance().getSiteName(kidsupervisor);
	request.setAttribute("month", month);
	request.setAttribute("day",day);
	String meterStr = "";
	String idvariable = "";
	if(idvariableInt != AlarmService.NO_VARIABLE)
	{
		idvariable = ""+idvariableInt;
	}
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
<input type="hidden" id="iddevice" value="${iddevice }"/>
<input type="hidden" id="idvariable" value="${idvariable }"/>
<body>
<div id="menu_icon"></div>
<div class="more">
	<div class="dropdown">
	  <img src="img/menu.png" class="btn btn-secondary1 dropdown-toggle" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
	  <ul class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenu1">
	    <li><a class="dropdown-item" href="site.jsp">我的站点</a></li>
	    <li><a class="dropdown-item" href="alarmStatistics.jsp?idsite=<%=kidsupervisor%>"><%=siteName %>报警分析</a></li>
	    <c:if test="${day!=''}">
	    <li><a href="alarmStatistics.jsp?idsite=<%=kidsupervisor%>&month=<%=monthByDay %>"><%=monthByDay %></a></li>
	    </c:if>
	  </ul>
	</div>
</div>
<div class="querycondition"></div>
<div class="statistic">
	<div class="can_container">
		<c:if test="${globalMeter!=null}">
			<canvas id="global_canvas" height="20vh" width="30vw"></canvas>
		</c:if>
		<c:if test="${detailMeter!=null}">
			<canvas id="detail_canvas" height="20vh" width="30vw"></canvas>
		</c:if>
	</div>
</div>
</body>
<script type="text/javascript">

var global = [],detail = [];
<c:if test="${globalMeter!=null}">
	global = <%=globalData%>;
</c:if>
<c:if test="${detailMeter!=null}">
	detail = <%=detailData%>;
</c:if>
</script>
</html>