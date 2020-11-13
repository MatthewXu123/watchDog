<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" 
    import="java.util.*" 
    import="watchDog.bean.SiteInfo"
    import="watchDog.service.AlarmService" 
    import="watchDog.bean.AlarmAppend" 
    import="watchDog.listener.Dog" 
    import="watchDog.service.RequestService" 
    import="watchDog.thread.WechatApplicationThread"
    import="com.alibaba.fastjson.JSON" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<link rel="stylesheet" href="js/framework/bootstrap/4.2.1/css/bootstrap.min.css">
<script src="js/framework/jquery/3.3.1/jquery.min.js"></script>
<script src="js/framework/bootstrap/4.2.1/js/bootstrap.min.js"></script>
<%
	String userId = RequestService.validation(request, "wx_userid", "wx_userid");
	if(userId == null || userId.length()==0)
	{
		response.sendRedirect("invalid.html");
		return;
	}
	List<SiteInfo> sites = Dog.getInstance().getWechatApplicationThread().getSitesByUserId(userId,"getActiveNum","desc");
	int[] siteArray = new int[sites.size()];
	int i = 0;
	for(SiteInfo site : sites){
		siteArray[i++] = site.getSupervisorId();
	}
	Map<String,List<AlarmAppend>> result = AlarmService.getActiveAlarm(siteArray);
	List<AlarmAppend> activeAlarms = result.get("i");
	List<Map<String, String>> siteList =  new ArrayList<>();
	for(AlarmAppend aa : activeAlarms){
		Map<String, String> siteMap = new HashMap<>();
		siteMap.put("site", aa.getSite());
		siteMap.put("device",aa.getDevice());
		siteMap.put("alarm",aa.getVar());
		siteList.add(siteMap);
	}
	String sitejson = JSON.toJSONString(siteList);
	request.setAttribute("siteList", siteList);
	request.setAttribute("sitejson", sitejson);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style>
	th,td{font-size:2rem;}
	th{text-align:center;}
</style>
<title>重要报警未处理站点统计</title>
</head>
<body>
	<table class="table table-hover table-bordered table-striped" id="sitelist_table">
		<thead>
			<tr>
				<th>站点</th>
				<th>设备</th>
				<th>报警</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${siteList }" var="item">
				<tr>
					<td>${item.site }</td>
					<td>${item.device }</td>
					<td>${item.alarm }</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>