<!DOCTYPE html>
<%@page import="java.util.Map.Entry"%>
<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<%@page import="watchDog.config.json.DeviceModelConfig"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" 
	import="watchDog.bean.Alarm"
	import="watchDog.bean.AlarmAppend"
	import="watchDog.service.AlarmService"
	import="java.util.List"
	import="java.util.Map"
	import="watchDog.listener.Dog"
	import="watchDog.service.PropertyMgr"
	import="watchDog.service.RequestService"
	import="watchDog.service.DeviceValueMgr"
	import="watchDog.bean.DeviceValueBean"
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
<link rel="stylesheet" href="css/alarm.css?${version }" />
<script src="js/util/swipe.js?${version }"></script>
<script src="js/alarm.js?${version }"></script>
<%
//	import="org.apache.log4j.Logger"
	//Logger logger = Logger.getLogger(Logger.class);
	//logger.info("alarm.jsp");
	String userId = RequestService.validation(request, "wx_userid", "wx_userid");
	if(userId == null || userId.length()==0)
	{
		response.sendRedirect("invalid.html");
		return;
	}
	int kidsupervisor = 185;
	String str = request.getParameter("idsite");
	if(str != null && str.length()>0)
		kidsupervisor= Integer.valueOf(str);
	else
	{
		response.sendRedirect("invalid.html");
		return;
	}
	Map<String,List<AlarmAppend>> result = AlarmService.getActiveAlarm(new int[]{kidsupervisor});
	
	List<AlarmAppend> activeAlarms = result.get("i");
	List<AlarmAppend> unimportantAlarm = result.get("u");
	request.setAttribute("important", activeAlarms);
	request.setAttribute("unimportant", unimportantAlarm);
	
	
	String siteName = "";
	siteName = Dog.getInstance().getSiteName(kidsupervisor);
%>
<html>

<head>
	<title><%=siteName%></title>
</head>

<body>
	<input type="hidden" id="sitename" value="<%=siteName %>" />
	<input type="hidden" id="kidsupervisor" value="<%=kidsupervisor %>" />
	<div class="more">
		<div class="dropdown">
			<img src="img/menu.png" class="btn btn-secondary1 dropdown-toggle" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
			<ul class="dropdown-menu dropdown-menu-right" data-thumb-target="temp">
				<li><a href="#" class="dropdown-item" data-toggle="modal" data-target="#mymodel" id="btn_acknowledge">确认报警</a></li>
				<li><a href="#" class="dropdown-item" data-toggle="modal" data-target="#mymodel" id="btn_manage">处理结果</a></li>
				<li><a href="#" class="dropdown-item" data-toggle="modal" data-target="#mymodel" id="btn_reset">强制复位</a></li>
				<li><a href="#" class="dropdown-item" data-toggle="modal" onclick="window.open('reportManager.jsp?idsite=<%=kidsupervisor%>&type=w')" id="">每周报表</a></li>
				<li><a href="#" class="dropdown-item" data-toggle="modal" onclick="window.open('reportManager.jsp?idsite=<%=kidsupervisor%>&type=m')" id="">每月报表</a></li>
				<li><a href="#" class="dropdown-item" data-toggle="modal" onclick="window.open('resetAlarm.jsp?idsite=<%=kidsupervisor%>')" id="">查看复位报警</a></li>
			</ul>
		</div>
		<img src="img/report.svg" class="detail">
    </div>
	<div class="modal fade" id="mymodel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
							aria-hidden="true">&times;</span></button>
					<h4 class="modal-title" id="myModalLabel">报警确认</h4>
				</div>
				<form action="servlet/managealarm" method="post">
					<input type="hidden" name="cmd" id="cmd">
					<input type="hidden" name="idalarms" id="idalarms">
					<input type="hidden" name="idsite" id="idsite" value="<%=kidsupervisor%>" />
					<div class="modal-body body-acknowledge">
						<textarea class="form-control" placeholder="需要跟其他小伙伴们说些什么吗?" id="exampleFormControlTextarea1"
							name="ackComments" rows="3"></textarea>
					</div>
					<div class="modal-body body-manage">
						<div class="form-check">
							<input class="form-check-input" type="radio" name="manage_type" value="1NN" checked>
							<label class="form-check-label" for="exampleRadios1">无操作</label>
							<img src="img/resetalarm/1NN.svg">
						</div>
						<div class="form-check">
							<input class="form-check-input" type="radio" name="manage_type" value="2LC">
							<label class="form-check-label" for="exampleRadios1">现场处理</label>
							<img src="img/resetalarm/2LC.svg">
						</div>
						<div class="form-check">
							<input class="form-check-input" type="radio" name="manage_type" value="3RM">
							<label class="form-check-label" for="exampleRadios1">远程处理</label>
							<img src="img/resetalarm/3RM.svg">
						</div>
						<div class="form-check">
							<input class="form-check-input" type="radio" name="manage_type" value="4RP">
							<label class="form-check-label" for="exampleRadios1">备件替换</label>
							<img src="img/resetalarm/4RP.svg">
						</div>
					</div>
					<div class="modal-body body-reset">
						<div class="form-check">
							<input class="form-check-input" type="radio" name="comments" checked id="resetradio1">
							<label class="form-check-label" for="exampleRadios1">改造、维护、维修更换</label>
						</div>
						<div class="form-check">
							<input class="form-check-input" type="radio" name="comments" id="resetradio2">
							<label class="form-check-label" for="exampleRadios1">停电</label>
						</div>
						<div class="form-check">
							<input class="form-check-input" type="radio" name="comments" id="resetradio3">
							<label class="form-check-label" for="exampleRadios1">等待设备、零配件、材料</label>
						</div>
						<div class="form-check">
							<input class="form-check-input" type="radio" name="comments" id="resetradio4">
							<label class="form-check-label" for="exampleRadios1">其他</label>
						</div>
						<textarea class="form-control" placeholder="需向门店负责人解释具体原因" id="exampleFormControlTextarea2"
							rows="3"></textarea>
						<input type="hidden" name="resetComments" id="resetComments">
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
						<button type="button" class="btn btn-primary" id="submit_acknowledge" onclick="checkInfo()">提交</button>
					</div>
				</form>
			</div>
		</div>
	</div>
	</div>

	<div class="dummy"></div>
	<c:forEach items="${important}" var="i">
		<div class="row_alarm short_row" durl="${i.deviceURL }" var="${i.idVariable }" ida="${i.idAlarm }">
			<input type="hidden" value="${i.ip }" id="${i.idAlarm}${'_ip'}">
			<input type="hidden" value="${i.devCode }" id="${i.idAlarm}${'_devCode'}">
			<span class="circle_span"></span>
			<span class="alarm_description">${i.var}</span>
			<span class="alarm_device">${i.device }</span>
			<c:if test="${i.isUnit=='true' && i.addressIn!=0}"><img src="img/browse.svg" class="device_status"
					onclick="javascript:window.open('device.jsp?ip=${i.ip}&devCode=${i.devCode }')"></c:if>
			<%-- <img id="${i.idAlarm}${'_img'}" class="device_status" style="display:none;" onclick="checkDevice(this)"> --%>
			<span class="alarm_starttime">开始:${i.humainStartTime}</span>
			<span class='alarm_last7 <c:if test="${i.last7CNT>7}">c_red bold</c:if>'>${i.last7CNT }次</span>
			<span class="alarm_last7_">1周:</span>
			<span class="duration">持续:${i.activeAlarmDuration}</span>
			<span class='alarm_last30 <c:if test="${i.last30CNT>30}">c_red bold</c:if>'>${i.last30CNT }次</span>
			<span class="alarm_last30_">30天:</span>
			<span class="alarm_handle ${i.led }">状态:${i.manageStatus}</span> <span
				class="alarm_handle_detail">${i.manageDetail}</span>
		</div>
	</c:forEach>
	<c:if test="${important == null && unimportant == null}">
	<div class="row_spliter">无报警</div>
	</c:if>
	<c:if test="${unimportant != null && unimportant.size()>0}">
	<div class="row_spliter">以下是非紧急报警</div>
	</c:if>
	<c:forEach items="${unimportant}" var="i">
		<div class="row_alarm short_row" durl="${i.deviceURL }" var="${i.idVariable }" ida="${i.idAlarm }">
			<input type="hidden" value="${i.ip }" id="${i.idAlarm}${'_ip'}">
			<input type="hidden" value="${i.devCode }" id="${i.idAlarm}${'_devCode'}">
			<span class="circle_span"></span>
			<span class="alarm_description">${i.var}</span>
			<span class="alarm_device">${i.device }</span>
			<c:if test="${i.isUnit=='true' && i.addressIn!=0}"><img src="img/browse.svg" class="device_status"
					onclick="javascript:window.open('device.jsp?ip=${i.ip}&devCode=${i.devCode }')"></c:if>
			<%-- <img id="${i.idAlarm}${'_img'}" class="device_status" style="display:none;" onclick="checkDevice(this)"> --%>
			<span class="alarm_starttime">开始:${i.humainStartTime}</span>
			<span class='alarm_last7 <c:if test="${i.last7CNT>7}">c_red bold</c:if>'>${i.last7CNT }次</span>
			<span class="alarm_last7_">1周:</span>
			<span class="duration">持续:${i.activeAlarmDuration}</span>
			<span class='alarm_last30 <c:if test="${i.last30CNT>30}">c_red bold</c:if>'>${i.last30CNT }次</span>
			<span class="alarm_last30_">30天:</span>
			<span class="alarm_handle ${i.led }">状态:${i.manageStatus}</span> <span
				class="alarm_handle_detail">${i.manageDetail}</span>
		</div>
	</c:forEach>
	<script type="text/javascript">
		$(function () {
			$("#exampleFormControlTextarea2").hide();
			for (var i = 1; i <= 4; i++) {
				var ids = "#resetradio" + i;
				$(ids).on("click", function () {
					if ($(this).attr("id") == "resetradio4" && $(this).is(":checked")) {
						$("#exampleFormControlTextarea2").show();
					} else {
						$("#exampleFormControlTextarea2").hide();
					}
				})
			}
		});

		function checkInfo() {
			for (var i = 1; i <= 4; i++) {
				var ids = "#resetradio" + i;
				if ($(ids).is(":checked") && ids != "#resetradio4") {
					$("#resetComments").val(" \n" + $(ids).next().text());
				} else if ($(ids).is(":checked") && ids == "#resetradio4") {
					$("#resetComments").val(" \n" + $('#exampleFormControlTextarea2').val());
				}
			}
		}
	</script>
</body>

</html>