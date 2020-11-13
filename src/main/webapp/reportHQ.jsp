<!DOCTYPE html>
<%@page import="java.net.URLEncoder"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" 
	import="watchDog.bean.SiteInfo"
	import="watchDog.bean.Alarm"
	import="watchDog.bean.SLAResult"
	import="watchDog.bean.ACKResult"
	import="watchDog.service.AlarmService"
	import="watchDog.service.EnergyService"
	import="watchDog.service.TemperatureKPIService"
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
<link rel="stylesheet" href="js/framework/bootstrap/4.2.1/css/bootstrap-table.min.css">
<script src="js/framework/bootstrap/4.2.1/js/bootstrap-table.min.js"></script>
<%
	String version = PropertyMgr.getInstance().getProperty(PropertyMgr.SCRIPT_VERSION).getValue();
	request.setAttribute("version", version);
%>
<script src="js/weeklyReportManager.js?${version }"></script>
<link rel="stylesheet" href="css/menu.css?${version }">
<link rel="stylesheet" href="css/report.css?${version }"/>

<%
String userId = RequestService.validation(request, "wx_userid", "wx_userid");
if(userId == null || userId.length()==0)
{
	response.sendRedirect("invalid.html");
	return;
}
String siteName = "";
String type = request.getParameter("type");
String startday = request.getParameter("start");
boolean isWeekly = true;
//String weekTitle = "("+DateTool.format(DateTool.add(new Date(), -7, Calendar.DATE),"MM-dd")+"~"+DateTool.format(DateTool.add(new Date(), -1, Calendar.DATE),"MM-dd")+")周报表";


if(type == null || !"m".equals(type)){
	type="w";
	siteName = "("+DateTool.format(DateTool.add(new Date(), -7, Calendar.DATE),"MM-dd")+"~"+DateTool.format(DateTool.add(new Date(), -1, Calendar.DATE),"MM-dd")+")周报表";
}else{
	siteName = Calendar.MONTH+1 + "月份报表";
}
	
String energyTitle = "周电量";
if(!"w".equals(type)){
	energyTitle = "月电量";
	isWeekly = false;
}
//startday = "2019-02-25"; //test by glisten
String dateFormat = isWeekly ? "yyyy-MM-dd" : "yyyy-MM";
String mmdd = "MM.dd";
if(startday == null || startday.length() == 0){
	if(isWeekly){
		Date[] range = DateTool.getLastWeekRange();
		startday = DateTool.format(range[0], dateFormat);
	}else{
		startday = DateTool.format(DateTool.getFirstDayOfLastMonth(), dateFormat);
	}
}else{
	if(!DateTool.isQualifiedDate(startday, dateFormat))
		return;
}
%>

<html>
<head>
<meta content="width=device-width, initial-scale=0.7, maximum-scale=1.0, user-scalable=0" name="viewport" />
<title><%=siteName%></title>
</head>
<body>
<div class="table_head">报警数量</div>
<table class="table table-striped table-bordered table-hover"
  id="table"
  data-toggle="table"
  data-height="460"
  data-sort-name="name"
  data-sort-order="desc"
  data-url="servlet/getData?DataType=alarm&type=<%=type%>&startday=<%=startday%>">
  <thead>
    <tr>
      <th data-field="siteName" data-sortable="true">门店名称</th>
      <th data-field="htTot" data-sortable="true">高温报警</th>
      <th data-field="vhTot" data-sortable="true">最高等级</th>
      <th data-field="hTot" data-sortable="true">高等级</th>
      <th data-field="mTot" data-sortable="true">中等级</th>
      <th data-field="lTot" data-sortable="true">低等级</th>
    </tr>
  </thead>
</table>

<div class="table_head">报警关闭效率</div>
<table class="table table-striped table-bordered table-hover"
  id="table2"
  data-toggle="table"
  data-height="460"
  data-sort-name="name"
  data-sort-order="desc"
  data-url="servlet/getData?DataType=alarm&type=<%=type %>&startday=<%=startday%>">
  <thead>
    <tr>
      <th data-field="siteName" data-sortable="true">门店名称</th>
      <th data-field="amountSLA" data-sortable="true">总个数</th>
      <th data-field="amountOutSLA" data-sortable="true">逾期未处理</th>
      <th data-field="persentOutSLA" data-sortable="true">逾期比例</th>
      <th data-field="amountMReset" data-sortable="true">强制复位</th>
    </tr>
  </thead>
</table>

<div class="table_head">报警处理结果</div>
<table class="table table-striped table-bordered table-hover"
  id="table2"
  data-toggle="table"
  data-height="460"
  data-sort-name="name"
  data-sort-order="desc"
  data-url="servlet/getData?DataType=alarmMange&type=<%=type %>&startday=<%=startday%>">
  <thead>
    <tr>
      <th data-field="siteName" data-sortable="true">门店名称</th>
      <th data-field="noManage" data-sortable="true">未管理</th>
      <th data-field="noAction" data-sortable="true">无操作</th>
      <th data-field="localAction" data-sortable="true">现场处理</th>
      <th data-field="remAction" data-sortable="true">远程处理</th>
      <th data-field="repAction" data-sortable="true">备件替换</th>
    </tr>
  </thead>
</table>

<%
	
	String strDate11="";
	String strDate21="";
	String strDate31="";
	String strDate41="";

	if(isWeekly){
//		startday = "2018-12-31";
		if(startday == null || startday.length() == 0){
			Date[] range = DateTool.getLastWeekRange();
			startday = DateTool.format(range[0], dateFormat);
		}else if(!DateTool.isQualifiedDate(startday, dateFormat))
			return;
	
		Date str10 = EnergyService.getMonday(DateTool.parse(startday, dateFormat));
		strDate11 = DateTool.format(EnergyService.getMonday(str10),mmdd);
		Date str11 = DateTool.add(str10, 6, Calendar.DATE);
		strDate11 +=("-"+DateTool.format(str11,mmdd));	
		
		Date str20 = DateTool.add(str10, -7, Calendar.DATE);
		strDate21 = DateTool.format(EnergyService.getMonday(str20),mmdd);
		Date str21 = DateTool.add(str20, 6, Calendar.DATE);
		strDate21 +=("-"+DateTool.format(str21,mmdd));	
		
		Date str30 = DateTool.add(str20, -7, Calendar.DATE);
		strDate31 = DateTool.format(EnergyService.getMonday(str30),mmdd);
		Date str31 = DateTool.add(str30, 6, Calendar.DATE);
		strDate31 +=("-"+DateTool.format(str31,mmdd));
		
		Date str40 = DateTool.add(str30, -7, Calendar.DATE);
		strDate41 = DateTool.format(EnergyService.getMonday(str40),mmdd);
		Date str41 = DateTool.add(str40, 6, Calendar.DATE);
		strDate41 +=("-"+DateTool.format(str41,mmdd));	
	}else{
	//	startday = "2018-12";
		if(startday == null || startday.length() == 0){
			startday = DateTool.format(DateTool.getFirstDayOfLastMonth(), dateFormat);
		}else if(!DateTool.isQualifiedDate(startday, dateFormat))
			return;
		strDate11 = startday;
		strDate21 = EnergyService.getMonth(startday,-1);
		strDate31 = EnergyService.getMonth(startday,-2);
		strDate41 = EnergyService.getMonth(startday,-3);
		
	}
	
	
	
%>
<div class="table_head"><%=energyTitle %></div>
<table class="table table-striped table-bordered table-hover"
  id="table2"
  data-toggle="table"
  data-height="460"
  data-sort-name="name"
  data-sort-order="desc"
  data-url="servlet/getData?DataType=energy&type=<%=type %>&startday=<%=startday%>">
  <thead>
    <tr >
      <th data-field="siteName" data-sortable="true">门店名称</th>
      <th data-field="energyw4" data-sortable="true"><%=strDate41%></th>
      <th data-field="energyw3" data-sortable="true"><%=strDate31%></th>
      <th data-field="energyw2" data-sortable="true"><%=strDate21%></th>
      <th data-field="energyw1" data-sortable="true"><%=strDate11%></th>
    </tr>
  </thead>
</table>
</body>
</html>