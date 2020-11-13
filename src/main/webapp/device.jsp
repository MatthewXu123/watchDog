<!DOCTYPE html>
<%@page import="watchDog.util.ObjectUtils"%>
<%@page import="com.alibaba.fastjson.JSONObject"%>
<%@page import="com.alibaba.fastjson.JSON"%>
<%@page import="java.util.List"%>
<%@page import="watchDog.config.json.DeviceModelConfig"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"
	pageEncoding="UTF-8" import="watchDog.service.DeviceValueMgr"
	import="watchDog.bean.DeviceValueBean" import="watchDog.util.DateTool"
	import="java.util.Map" import="java.util.*" import="com.alibaba.fastjson.*"
	import="watchDog.service.PropertyMgr"
	import="watchDog.service.RequestService"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<link rel="stylesheet"
	href="js/framework/bootstrap/4.2.1/css/bootstrap.min.css">
<script src="js/framework/jquery/3.3.1/jquery.min.js"></script>
<script src="js/framework/popperjs/1.14.6/popper.min.js"></script>
<script src="js/framework/bootstrap/4.2.1/js/bootstrap.min.js"></script>
<script src="js/util/jquery.animate-colors-min.js"></script>
<%
	String version = PropertyMgr.getInstance().getProperty(PropertyMgr.SCRIPT_VERSION).getValue();
	request.setAttribute("version", version);
%>
<link rel="stylesheet" href="css/device.css?${version }" />
<link rel="stylesheet" href="css/menu.css?${version }">
<script src="js/device.js?${version }"></script>
<%
	String userId = RequestService.validation(request, "wx_userid", "wx_userid");
	if (userId == null || userId.length() == 0) {
		response.sendRedirect("invalid.html");
		return;
	}
	String ip = request.getParameter("ip");
	String devCode = request.getParameter("devCode");
	if (ip == null || ip.length() == 0 || devCode == null || devCode.length() == 0) {
		ip = "192.168.88.59";
		devCode = "1.020";
	}
	String lastQueryTime = "";
	Map<String, String> valueMap = new HashMap<String, String>();

	DeviceValueBean valueBean = DeviceValueMgr.getInstance().getValueQuick(ip, devCode);
	if (valueBean != null) {
		lastQueryTime = DateTool.format(valueBean.getDate(), "yyyy-MM-dd HH:mm:ss");
		valueMap = valueBean.getValues();
	}
	String[] info = DeviceValueMgr.getSiteDeviceDescription(ip, devCode);
	List<LinkedHashMap<String, Object>> mapList = DeviceModelConfig
			.getCodeMapList(DeviceModelConfig.getdevmdlCode(ip, devCode));
	LinkedHashMap<String, Object> digital = null;
	LinkedHashMap<String, Object> main = null;
	LinkedHashMap<String, Object> setpoint = null;
	String dJson = new String();
	String mJson = new String();
	String sJson = new String();
	if(ObjectUtils.isCollectionEmpty(mapList)){
		return;
	}else{
		digital = mapList.get(0);
		main = mapList.get(1);
		setpoint = mapList.get(2);
		
		if(digital != null && digital.size() != 0){
			dJson = JSON.toJSONString(digital);
		}else{
			dJson = JSON.toJSONString("");
		}
		
		
		if(main != null && main.size() != 0){
			mJson = JSON.toJSONString(main);
		}else{
			mJson = JSON.toJSONString("");
		}
		
		
		if(setpoint != null && setpoint.size() != 0){
			sJson = JSON.toJSONString(setpoint);
		}else{
			sJson = JSON.toJSONString("");
		}
		 
		request.setAttribute("i", valueMap);
		request.setAttribute("digital", digital);
		request.setAttribute("main", main);
		request.setAttribute("setpoint", setpoint);
	}
%>
<html>
<head>
<title><%=info[1]%> <%=info[0]%></title>
</head>
<body>
	<input type="hidden" id="ip" value="<%=ip%>">
	<input type="hidden" id="devCode" value="<%=devCode%>" />
	<input type="hidden" id="lastQueryTime" value="<%=lastQueryTime%>" />
	<c:if test="${not empty digital}">
		<table class="value_table notop">
		<tr>
			<td colspan="2">状态参数</td>
		</tr>
	</table>
	<c:forEach items="${digital}" var="item">
		<div id="${item.key}" value="${i[item.key]}"
			class="status_row <c:if test="${i[item.key]=='0'}">device_off</c:if><c:if test="${i[item.key]=='1'}">device_on</c:if>">
			<div class="status_description">${item.value}</div>
		</div>
	</c:forEach>
	</c:if>
	
	<c:if test="${not empty main}">
	<table class="value_table">
		<tr>
			<td colspan="2">主要参数</td>
		</tr>
	</table>
	<c:forEach items="${main}" var="item">
		<div class="status_row_new">
			${item.value } <span id="${ item.key}" class="v_span">${i[item.key]}</span>
		</div>
	</c:forEach>
	</c:if>
	
	<c:if test="${not empty setpoint}">
	<table class="value_table notop">
		<tr>
			<td colspan="2">参数设定点</td>
		</tr>
	</table>
	<c:forEach items="${setpoint}" var="item">
		<div class="status_row_new">
			${item.value } <span id="${ item.key}" class="v_span">${i[item.key]}</span>
		</div>
	</c:forEach>
	</c:if>
	<div class="row device_url">
		<a href="<%=info[2]%>">点击查看更多变量</a>
	</div>
	<script type="text/javascript">
		var dJson = <%=dJson%>;
		var mJson = <%=mJson%>;
		var sJson = <%=sJson%>;
		window.onload=f1();
	</script>
</body>
</html>