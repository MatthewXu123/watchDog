<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"
	import="watchDog.bean.Alarm" import="watchDog.bean.AlarmAppend"
	import="watchDog.service.AlarmService" import="java.util.List"
	import="watchDog.listener.Dog" import="watchDog.service.PropertyMgr"
	import="watchDog.service.RequestService"%>
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
<link rel="stylesheet" href="css/resetalarm.css?${version }" />
<script src="js/util/swipe.js?${version }"></script>
<script src="js/resetAlarm.js?${version }"></script>


<%
	String userId = RequestService.validation(request, "wx_userid", "wx_userid");
	if (userId == null || userId.length() == 0) {
		response.sendRedirect("invalid.html");
		return;
	}
	int kidsupervisor = 140;
	String str = request.getParameter("idsite");
	if (str != null && str.length() > 0)
		kidsupervisor = Integer.valueOf(str);
	else
		return;
	boolean all = false;
	str = request.getParameter("all");
	if("true".equals(str))
		all = true;
	int lastAlarmId = 99999999;
	List<Alarm> resetAlarms = AlarmService.getResetAlarm(kidsupervisor, all,lastAlarmId, AlarmService.NO_VARIABLE,
			AlarmService.NO_VARIABLE, null, null);
	request.setAttribute("reset", resetAlarms);

	String siteName = "";
	boolean showAjax = false;
	if (resetAlarms != null && resetAlarms.size() > 0) {
		lastAlarmId = resetAlarms.get(resetAlarms.size() - 1).getIdAlarm();
		siteName = resetAlarms.get(0).getSite();
		showAjax = resetAlarms.size() > 200;
	} else
		siteName = Dog.getInstance().getSiteName(kidsupervisor);
%>
<html>

<head>
	<title><%=siteName%></title>
</head>

<body>
	<input type="hidden" name="idsite" id="idsite" value="<%=kidsupervisor%>" />
	<input type="hidden" id="all" value="<%=all %>" />
	<input type="hidden" id="last_reset_id" value="<%=lastAlarmId%>" />
	<input type="hidden" id="showAjax" value="<%=showAjax%>" />
	<input type="hidden" id="sitename" value="<%=siteName%>" />
	<!-- menu begin -->
	<div class="more">
		<div class="dropdown">
			<img src="img/menu.png" class="btn btn-secondary1 dropdown-toggle" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
			<ul class="dropdown-menu dropdown-menu-right" data-thumb-target="temp">
				<li><a href="#" class="dropdown-item" data-toggle="modal" data-target="#mymodel" id="btn_manage">我要处理</a></li>
			</ul>
		</div>
	</div>
	<!-- menu end -->
	<!-- table begin -->
	<table id="reset_table" class="table table-hover table-bordered table-striped">
		<thead>
			<tr style="text-align: center;">
				<th>报警说明</th>
				<th>设备</th>
				<th>报警时间</th>
				<th>持续</th>
				<th>处理</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${reset}" var="i" varStatus="vs">
				<tr id="${vs.index}" var="${i.idVariable}" class="show_content" iddevice="${i.idDevice}">
					<td>${i.var }
						<img class="i_history_reset" src="img/history.svg"/>
					</td>
					<td>${i.device }</td>
					<td>${i.timeRange}</td>
					<td>${i.alarmDuration }
						<c:if test="${i.resetUser != null && i.resetUser != ''}"><text>${i.resetUser}</text></c:if>
						<c:if test="${i.recallresetuser != null && i.recallresetuser != ''}"><text>${i.recallresetuser}</text></c:if>
					</td>
					<td ida="${i.idAlarm }">
						<input id="${i.idAlarm }${'_input'}" class="resetbox" type="checkbox" value="${i.useSpare }">
						<label id="${i.idAlarm }${'_label'}" for="${i.idAlarm }${'_input'}"></label>
						<img id="${i.idAlarm }${'_img'}" alt="" src="" class="handleresult" style="display: none;" onclick="clickImg(${i.idAlarm });">
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div id="reset_help" class="reset_help">点击显示更多行</div>
	<!-- table end -->
	<!-- modal#mymodel begin -->
	<div class="modal fade" id="mymodel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title">报警确认</h4>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">  
                    <span aria-hidden="true">&times;</span>  
                	</button>  
				</div>
				<input type="hidden" name="cmd" id="cmd">
				<input type="hidden" name="idalarms" id="idalarms">
				<input type="hidden" name="table_type" id="table_type" value="5">
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
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<input type="button" class="btn btn-primary" onclick="submitAjax()" value="提交">
				</div>
			</div>
		</div>
	</div>
	<!-- modal#mymodel end -->
	<script>
		$(function () {
			$(".reset_help").on("click", f1);
			$(".resetbox").each(function () {
				var id = "#" + $(this).attr("id").split("_")[0];
				var input = id +"_input";
				var label = id + "_label";
				var img = id + "_img";
				if (this.value != null && this.value.length != 0) {
					$(input).hide();
					$(label).hide();
					$(img).attr("alt",this.value);
					$(img).attr("src","img/resetalarm/" + this.value.split(";")[0] + ".svg");
					/* $(img).bind("click",function(){
						$(img).hide();
						$(input).attr("checked","true");
						$(input).show();
						$(label).show();
						}); */
					$(img).show();
				}
			});
		});
	</script>
</body>

</html>