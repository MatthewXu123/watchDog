<!DOCTYPE html>
<%@page import="watchDog.util.ObjectUtils"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="watchDog.thread.WechatApplicationThread"%>
<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" 
	import="watchDog.service.RequestService"
	import="watchDog.listener.Dog"
	import="watchDog.bean.SiteInfo"
	import="java.util.*"
	import="watchDog.service.PropertyMgr"
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<link rel="stylesheet" href="js/framework/bootstrap/4.2.1/css/bootstrap.min.css">
<script src="js/framework/jquery/3.3.1/jquery.min.js"></script>
<script src="js/framework/popperjs/1.14.6/popper.min.js"></script>
<script src="js/framework/bootstrap/4.2.1/js/bootstrap.min.js"></script>
<%
    PropertyMgr.getInstance().update(PropertyMgr.SCRIPT_VERSION,"3");
	String version = PropertyMgr.getInstance().getProperty(PropertyMgr.SCRIPT_VERSION).getValue();
	request.setAttribute("version", version);
%>
<link rel="stylesheet" href="css/menu.css?${version }">
<link rel="stylesheet" href="css/site.css?${version }" />
<script src="js/site.js?${version }"></script>
<%
	String userId = RequestService.validation(request, "wx_userid", "wx_userid");
	if(userId == null || userId.length()==0)
	{
		response.sendRedirect("invalid.html");
		return;
	}
	String orderType = request.getParameter("t");
	if(!"a1".equals(orderType) && !"a2".equals(orderType) && !"b1".equals(orderType) &&!"b2".equals(orderType) &&
			!"c1".equals(orderType) &&!"c2".equals(orderType))
		orderType = "a1";
	String method = "getActiveNum";
	String order = "desc";
	boolean shortRow = true;
	if(orderType != null && orderType.startsWith("a"))
	{
		method = "getActiveNum";
	}
	else if(orderType != null && orderType.startsWith("b"))
	{
		method = "getHighTemp30";
		shortRow = false;
	}
	else if(orderType != null && orderType.startsWith("c"))
	{
		method = "getAlarm30";
		shortRow = false;
	}
	if(orderType != null && orderType.endsWith("1"))
	{
		order = "desc";
	}
	else if(orderType != null && orderType.endsWith("2"))
	{
		order = "asc";
	}
	request.setAttribute("shortRow", shortRow);
	Dog dog = Dog.getInstance();
	List<SiteInfo> sites = dog.getWechatApplicationThread().getSitesByUserId(userId,method,order);
	int offlineSiteNum = 0;
	int importantSiteNum = 0;
	if(ObjectUtils.isCollectionNotEmpty(sites)){
		for(SiteInfo site : sites){
			//重要报警未处理门店统计
			if(site.getActiveNum() > 0){
				importantSiteNum ++;
			}
			if(site.getProbeissue() == null || !site.getProbeissue()){
				offlineSiteNum ++ ;
			}
			
		}
		request.setAttribute("siteNum", sites.size());
	}else{
		request.setAttribute("siteNum", 0);
	}
	request.setAttribute("sites", sites);
	//站点总数统计
	request.setAttribute("importantSiteNum", importantSiteNum);
	request.setAttribute("offlineSiteNum", offlineSiteNum);
	String str =  request.getParameter("search_key");
	String search_key= "";
	if(str != null)
	{
		str = java.net.URLDecoder.decode(str,"UTF-8");
		search_key = str;
	}
	request.setAttribute("search_key", search_key);
%>
<html>

<head>
	<title>我的站点</title>
</head>
<div class="more">
	<div class="site_head_div">
		<span class="site_head">${siteNum}&nbsp;&nbsp;站点总数</span>
		<span class="site_head" id="importantSpan">${importantSiteNum }&nbsp;&nbsp;<a href="#">重要报警未处理</a></span>
		<span class="site_head">${offlineSiteNum }&nbsp;&nbsp;&nbsp;离线站点</span>
	</div>
	<input type="search" class="form-control" id="site_input" placeholder="请输入站点名称" oninput="clicksearch()">
	<div class="dropdown">
		<img src="img/menu.png" class="btn btn-secondary1 dropdown-toggle"
			data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
		<ul class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuButton" id="dropdownMenuButton">
			<li><a href="#" class="dropdown-item" id="active_menu">活动报警排序</a></li>
			<li><a href="#" class="dropdown-item" id="high_menu">高温报警排序</a></li>
			<li><a href="#" class="dropdown-item" id="sum_menu">报警总数排序</a></li>
		</ul>
	</div>
	<img src="img/report.svg" class="detail">
	<img src="img/search.svg" class="search">
</div>
	<div id="container">
		<c:forEach items="${sites}" var="item">
			<div class="row_site <c:if test="${shortRow==true}">short_row</c:if>" idsite="${item.supervisorId }">
				<div class="dropdownList">
					<img src="img/dropdown.svg" class="site_menu btn btn-secondary1 dropdown-toggle" id="ids_${item.supervisorId}"
						data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
						<ul class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuButton" energy="${item.kwhLastMonth}">
						</ul>
				</div>
				<span class="site_name" url="${item.siteURL}?">${item.description}</span>
				<img src="img/alarm/members.svg" class="site_person" ida="${item.ident }"></img>
				<c:if test="${item.activeNum>0}">
					<img src="img/alarm.png" class="alarm_img"></img>
					<span class="<c:if test="${item.activeNum<10}">a_num1</c:if>
				<c:if test="${item.activeNum>=10}">a_num11</c:if>">${item.activeNum}</span>
				</c:if>
				<!-- <img src="img/calculate.svg" class="statistics"></img> -->
				<span class="project_company">${item.manDescription }</span>
				<c:if test="${item.kwhLastMonth>0}">
					<span class="energy">上月电量${item.kwhLastMonth}</span>
				</c:if>
				<span class="alarm_high30">高温报警${item.highTemp30}</span>
				<span class="alarm_all30">报警总数${item.alarm30}</span>
				<span id="${item.ident }_commander"
					class="hiddenSpan">${Dog.getInstance().getWechatApplicationThread().getWechatMemberStrByDeptId(item.tagId2)}</span>
				<span id="${item.ident }_soldier"
					class="hiddenSpan">${Dog.getInstance().getWechatApplicationThread().getWechatMemberStrByDeptId(item.tagId)}</span>
			</div>
		</c:forEach>
	</div>
	<div class="modal fade" id="site_person" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="table_head"></div>
				<table class="table table-striped table-bordered table-hover" id="user_table">
					<thead>
						<tr>
							<td>人员</td>
							<td>联系方式</td>
						</tr>
					</thead>
					<tbody id="user_table_body"></tbody>
				</table>
			</div>
		</div>
	</div>

	<body>
		<script>
			String.prototype.startWith = function (str) {
				var reg = new RegExp("^" + str);
				return reg.test(this);
			}
			//测试ok，直接使用str.endWith("abc")方式调用即可
			String.prototype.endWith = function (str) {
				var reg = new RegExp(str + "$");
				return reg.test(this);
			}

			function getMenuDefault(type) {
				if (type == "active_menu") {
					$("#active_menu").attr("href", "?t=a1");
					$("#active_menu").html("活动报警排序");
				} else if (type == "high_menu") {
					$("#high_menu").attr("href", "?t=b1");
					$("#high_menu").html("30天高温报警排序");
				} else if (type == "sum_menu") {
					$("#sum_menu").attr("href", "?t=c1");
					$("#sum_menu").html("30天报警总数排序");
				}
			}
			var orderType = "<%=orderType%>";
			if (orderType.startWith("a")) {
				if (orderType == "a1") {
					$("#active_menu").attr("href", "?t=a2");
					$("#active_menu").html("活动报警升序");
				} else {
					$("#active_menu").attr("href", "?t=a1");
					$("#active_menu").html("活动报警降序");
				}
				getMenuDefault("high_menu");
				getMenuDefault("sum_menu");
			}
			if (orderType.startWith("b")) {
				if (orderType == "b1") {
					$("#high_menu").attr("href", "?t=b2");
					$("#high_menu").html("30天高温报警升序");
				} else {
					$("#high_menu").attr("href", "?t=b1");
					$("#high_menu").html("30天高温报警降序");
				}
				getMenuDefault("active_menu");
				getMenuDefault("sum_menu");
			}
			if (orderType.startWith("c")) {
				if (orderType == "c1") {
					$("#sum_menu").attr("href", "?t=c2");
					$("#sum_menu").html("30天报警总数升序");
				} else {
					$("#sum_menu").attr("href", "?t=c1");
					$("#sum_menu").html("30天报警总数降序");
				}
				getMenuDefault("high_menu");
				getMenuDefault("active_menu");
			}


			$(document).ready(function () {
				bindOnLoad();
				//var key = "${search_key}";
				var key = sessionStorage.getItem("search_key");
				if (key != null && key != "") {
					$(".more .search").trigger("click");
					$("#site_input").val(key);
					$(".more .search").trigger("click");
				}
			});
			/* $("#site_input").keyup(function (event) {
				if (event.keyCode == 13 || event.keyCode == 8) {
					if (event.keyCode == 8 && $.trim($(this).val()).length > 0)
						return;
					$(".more .search").trigger("click");
				}
			}); */

			function bindOnLoad() {
				$(".site_name").on("click", function () {
					window.open($(this).attr("url"));
				});
				$(".alarm_img").on("click", function () {
					window.open("alarm.jsp?idsite=" + $(this).parent().attr("idsite"));
				});
				$(".reset_alarm_img").on("click", function () {
					window.open("alarm.jsp?idsite=" + $(this).parent().attr("idsite") + "&reset=true");
				});
				$(".site_menu").on("click", function () {
					setSiteMenu(this);
				});
				$(".a_num1").on("click", function () {
					window.open("alarm.jsp?idsite=" + $(this).parent().attr("idsite"));
				});
				$(".a_num11").on("click", function () {
					window.open("alarm.jsp?idsite=" + $(this).parent().attr("idsite"));
				});
				/* $(".statistics").on("click", function () {
					window.open("alarmStatistics.jsp?idsite=" + $(this).parent().attr("idsite"));
				});
				$(".project_company").on("click", function () {
					window.open("alarmStatistics.jsp?idsite=" + $(this).parent().attr("idsite"));
				}); */
//				$(".energy").on("click", function () {
//					window.open("energyStatistics.jsp?idsite=" + $(this).parent().attr("idsite"));
//				});
				$(".detail").on("click", function () {
					$(".row_site").toggleClass("short_row");
				});
				$(".site_person").on("click", function () {
					var id = $(this).attr("ida");
					var commanderText = $("#" + id + "_commander").text();
					var soldierText = $("#" + id + "_soldier").text();
					$("#user_table_body").html(getPersonTable(commanderText,"c") + getPersonTable(soldierText,"s"));
					$(".modal-dialog").css("max-width", "100%");
					$('#site_person').modal('show');
				});
			};
			function clicksearch(){
				$(".more .search").click();
			};
			$(".more .search").on("click", function () {
				sessionStorage.setItem("search_key",$("#site_input").val());
				var $input = $("#site_input");
				if ($input.is(':hidden')){
					$input.show();
					$(this).hide();
				}
				else {
					if ($.trim($input.val()).length == 0)
						$(".row_site").each(function () {
							$(this).show();
						});
					else
						$(".site_name").each(function () {
							if ($(this).html().indexOf($input.val()) >= 0)
								$(this).parent().show();
							else
								$(this).parent().hide();
						});
				}
			});
			$(".dropdown-item").on("click", function () {
				var searchStr = $("#site_input").val();
				if (searchStr != "") {
					var encoded = encodeURI(searchStr);
					encoded = encodeURI(encoded);
					$(this).attr("href", $(this).attr("href") + "&search_key=" + encoded);
				}
			});
//			$(".alarm_high30").on("click", function () {
//				var encoded = encodeURI("高温报警");
//				encoded = encodeURI(encoded);
//				window.open("alarmStatistics.jsp?idsite=" + $(this).parent().attr("idsite") + "&description=" + encoded);
//			});
			$("#importantSpan").on("click",function(){
				if(<%=importantSiteNum%> > 0)
					window.open("siteAlarms.jsp");
			});
		</script>
	</body>

</html>