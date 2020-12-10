<%@page import="watchDog.bean.register.SIMCardStatus"%>
<%@page import="watchDog.dao.SIMCardDAO"%>
<%@page import="watchDog.dao.RegisterationInfoDAO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<!-- <link rel="stylesheet" href="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/css/bootstrap.min.css"> -->
	<!-- <script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script> -->
	<script src="../js/framework/jquery/2.1.1/jquery.min.js"></script>
	<!-- <script src="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/js/bootstrap.min.js"></script> -->
	<script src="../js/framework/bootstrap/3.3.7/bootstrap.min.js"></script>
	
	<link href="../css/bootstrap.min.css" rel="stylesheet">
	<link href="../css/font-awesome.min.css" rel="stylesheet">
	<link href="../css/plugins/bootstrap-table/bootstrap-table.min.css" rel="stylesheet">
	<link href="../css/plugins/bootstrap-table/bootstrap-editable.css" rel="stylesheet">
	<link href="../css/table.css" rel="stylesheet">
	<script src="../js/plugins/bootstrap-table/popper.min.js"></script>
	<script src="../js/table.js"></script>
	<!-- Bootstrap table -->
	<script src="../js/plugins/bootstrap-table/bootstrap-table.min.js"></script>
	<script src="../js/plugins/bootstrap-table/bootstrap-table-mobile.min.js"></script>
	<script src="../js/plugins/bootstrap-table/bootstrap-editable.js?1"></script>
	<script src="../js/plugins/bootstrap-table/bootstrap-table-editable.js"></script>
	<script src="../js/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.18.1/moment.min.js"></script>	
<%
	request.setAttribute("simcardList", SIMCardDAO.INSTANCE.getAllByStatus(SIMCardStatus.UNUSED.getCode()));
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>RPMS</title>
</head>
<body>
	<div class="col-sm-12">
		<!-- Example Events -->
		<div class="example-wrap">
			<h4 class="example-title">项目信息管理</h4>
			<div class="example">
				<div id="toolbar" class="btn-group">
					<button id="btn_add" type="button" class="btn btn-default"  data-toggle="modal" data-target="#addModal">
						<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>新增
					</button>
					<!-- <button id="btn_edit" type="button" class="btn btn-default">
						<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>修改
					</button> -->
					<!-- <button id="btn_delete" type="button" class="btn btn-default">
						<span class="glyphicon glyphicon-remove" aria-hidden="true"></span>删除
					</button> -->
				</div>
				<table id="table"></table>
			</div>
		</div>
		<!-- End Example Events -->
	</div>
	<!-- Modal begin -->
	<div class="modal" id="addModal" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title" id="myModalLabel">新增</h4>
				</div>
				<div class="modal-body">
					<form>
						<div class="form-group">
							<label for="vpnAddressInput" class="col-md-2">VPN地址</label>
							<div class="col-md-4">
								<input type="text" class="form-control" id="vpnAddressInput" name="vpnAddress">
							</div>
							<label for="registerationDateInput" class="col-md-2">注册日期</label>
							<div class="col-md-4">
								<input type="text" class="form-control" id="registerationDateInput" name="registerationDate">
							</div>
						</div>
						<div class="form-group">
							<label for="purchaserInput" class="col-md-2">采购方</label>
							<div class="col-md-4">
								<input type="text" class="form-control" id="purchaserInput" name="purchaser">
							</div>
							<label for="projectInput" class="col-md-2">项目名</label>
							<div class="col-md-4">
								<input type="text" class="form-control" id="projectInput" name="project">
							</div>
						</div>
						<div class="form-group">
							<label for="servicePeroidInput" class="col-md-2">服务年限</label>
							<div class="col-md-4">
								<select class="form-control" name="servicePeriod">
      								<option>1</option>
      								<option>2</option>
      								<option>3</option>
      							</select>
							</div>
							<label for="productCodeInput" class="col-md-2">产品型号</label>
							<div class="col-md-4">
								<input type="text" class="form-control" id="productCodeInput">
							</div>
						</div>
						<div class="form-group">
							<label for="productMacInput" class="col-md-2">产品Mac</label>
							<div class="col-md-4">
								<input type="text" class="form-control" id="productMacInput">
							</div>
							<label for="routerMacInput" class="col-md-2">路由器Mac</label>
							<div class="col-md-4">
								<input type="text" class="form-control" id="routerMacInput">
							</div>
						</div>
						<div class="form-group">
							<label for="routerManufacturerInput" class="col-md-2">路由器厂商</label>
							<div class="col-md-4">
								<input type="text" class="form-control" id="routerManufacturerInput">
							</div>
							<label for="originalVersionInput" class="col-md-2">出厂版本</label>
							<div class="col-md-4">
								<input type="text" class="form-control" id="originalVersionInput">
							</div>
						</div>
						<div class="form-group">
							<label for="isUpdatedInput" class="col-md-2">是否升级</label>
							<div class="col-md-4">
								<input class="form-check-input" type="checkbox" value="" id="isUpdatedInput" name="isUpdated">
							</div>
							<label for="isConnectedInput" class="col-md-2">是否4G连接</label>
							<div class="col-md-4">
								<input class="form-check-input" type="checkbox" value="" id="isConnectedInput" name="isConnected">
							</div>
						</div>
						<div class="form-group">
							<label for="simCardInput" class="col-md-2">sim卡号</label>
							<div class="col-md-10">
								<select class="form-control" name="servicePeriod">
									<c:forEach items="${simcardList}" var="item">
										<option value="${item.id}">${item.cardNumber}</option>
									</c:forEach>
      							</select>
							</div>
						</div>
						<div class="form-group">
							<label for="commentInput" class="col-md-2">备注</label>
							<div class="col-md-10">
								<input type="textarea" class="form-control" id="commentInput" name="comment">
							</div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary">提交</button>
				</div>
			</div>
		</div>
	</div>
	<!-- Modal end -->
</body>
</html>