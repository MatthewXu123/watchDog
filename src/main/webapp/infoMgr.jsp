<!DOCTYPE html>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script src="../js/framework/jquery/3.3.1/jquery.min.js"></script>
<script src="../js/home.js"></script>
<!-- <script src="../js/framework/bootstrap/4.2.1/js/bootstrap.min.js"></script> -->

<link rel="stylesheet" href="../css/home.css?${version }" />
<!-- <link rel="stylesheet" href="../js/framework/bootstrap/4.2.1/css/bootstrap.min.css" /> -->

<html>
<head>
<title>微信配置</title>
</head>
<body>
	<div>
		<P>微信报警信息管理</P>
		<p>通道：1：微信 2：钉钉</p>
	</div>
	<button id="btn_vpn">VPN管理</button>
	<button id="btn_router">路由器管理</button>
	<button id="btn_rinfo">项目注册信息管理</button>
	<button id="btn_retail">零售项目信息查看</button>
	<form action="/watchDog/file/memberExport">
		<button type="submit" class="button-import-member">导出门店成员</button>
	</form>
	<form action="/watchDog/file/logExport">
		<button type="submit" class="button-import-log">导出当天日志</button>
	</form>
	<form action="/watchDog/file/siteExport">
		<button type="submit" class="button-import-csv">导出到csv</button>
	</form>
	<form action="/watchDog/file/trafficExport" method="post" enctype="multipart/form-data">
		<input id="uploadFile" name="uploadFile" type="file" style="display: none" accept=".csv">
		<input id="fileCover" class="form-control" type="text" style="height: 30px" placeholder="请上传流量报表">
		<button type="button" onclick="$('input[id=uploadFile]').click();">浏览</button>
		<button id="submit" type="submit">上传</button>
	</form>
	<form action="/watchDog/site/manage" method="post">
		<input type="submit" class="input-confirm" value="确认修改">
		<table>
			<tr>
				<td width="15%" align="center">店名</td>
				<td width="10%" align="center">厂商</td>
				<td width="5%" align="center">类型</td>
				<td width="15%" align="center">IP</td>
				<td width="10%" align="center">同步日期</td>
				<td width="10%" align="center">结束日期</td>
				<td width="4%" align="center">检查网络</td>
				<td width="4%" align="center">通道</td>
				<td width="5%" align="center">士兵</td>
				<td width="5%" align="center">军官</td>
				<td width="5%" align="center">将军</td>
				<td width="8%" align="center">备注</td>
			</tr>
			<c:forEach items="${siteInfoList}" var="siteInfo" varStatus="status">
				<c:choose>
					<c:when test="${status.index % 2 == 1 }">
						<!-- <tr class="tr-color"> -->
						<tr class="tr-remark">
					</c:when>
					<c:otherwise>
						<tr class="tr-remark">
					</c:otherwise>
				</c:choose>
				<td>
					<input type="text" class="input-readonly" name="description" value="${siteInfo.description}" >
				</td>
				<td>
					<input type="text" class="input-readonly" name="manDescription" value="${empty siteInfo.manDescription ? '未配置' : siteInfo.manDescription}" >
				</td>
				<c:choose>
					<c:when test="${siteInfo.probeissue}">
						<td>
							<input type="text" class="td-online input-readonly" name="ktype" value="${siteInfo.ktype}" >
						</td>
					</c:when>
					<c:otherwise>
						<td>
							<input type="text" class="input-readonly" name="ktype" value="${siteInfo.ktype}" >
						</td>
					</c:otherwise>
				</c:choose>
				<td>
					<input type="text" class="input-readonly" name="ip" value="${siteInfo.ip}" >
				</td>
				<td>
					<input type="text" class="input-readonly" name="lastSynch" value="${siteInfo.lastSynch}" >
				</td>
				<td>
					<input type="text" name="deadline" value="${siteInfo.deadline}" style="width: 90px;">
				</td>
				<td class="td-networkcheck" align="center">
					<c:choose>
						<c:when test="${siteInfo.checkNetwork}">
							<input type="hidden" name="checkNetwork" value="true">
							<input class="input-networkcheck" type="checkbox"  checked="checked">
						</c:when>
						<c:otherwise>
							<input type="hidden" name="checkNetwork" value="false">
							<input class="input-networkcheck" type="checkbox" >
						</c:otherwise>
					</c:choose>
				</td>
				<td>
					<input type="text" name="channel" value="${siteInfo.channel}" style="width: 40px;">
				</td>
				<td>
					<input type="text" class="input-tag-id" name="tag_id" value="${siteInfo.tagId}" style="width: 50px;">
				</td>
				<td>
					<input type="text" class="input-tag-id2" name="tag_id2" value="${siteInfo.tagId2}" style="width: 50px;">
				</td>
				<td>
					<input type="text" name="tag_id3" value="${siteInfo.tagId3}" style="width: 50px;">
				</td>
				<td>
					<input type="text" name="comment" value="${siteInfo.comment}" style="width: 100px;">
				</td>
				<input type="hidden" name="supervisorId" value="${siteInfo.supervisorId}">
				<input type="hidden" name="agentId" value="${siteInfo.agentId}">
				<input type="hidden" name="isRouterOnline" value="${siteInfo.isRouterOnline}">
				<input type="hidden" name="isEngineOnline" value="${siteInfo.isEngineOnline}">
				</tr>
			</c:forEach>
		</table>
	</form>
	<script type="text/javascript">
		$('input[id=uploadFile]').change(function() {
			$('#fileCover').val($(this).val());
		});
	</script>
</body>
</html>
