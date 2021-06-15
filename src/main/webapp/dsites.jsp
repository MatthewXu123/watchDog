<%@page import="watchDog.bean.register.SIMCardStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<script src="../js/framework/jquery/2.1.1/jquery.min.js"></script>
	<script src="../js/framework/bootstrap/3.3.7/bootstrap.min.js"></script>
	
	<link href="../css/bootstrap.min.css" rel="stylesheet">
	<link href="../css/font-awesome.min.css" rel="stylesheet">
	<link href="../js/plugins/bootstrap-datepicker/css/bootstrap-datepicker3.min.css" rel="stylesheet">
	<link href="../css/plugins/bootstrap-table/bootstrap-table.min.css" rel="stylesheet">
	<link href="../css/plugins/bootstrap-table/bootstrap-editable.css" rel="stylesheet">
	<link href="../css/table.css" rel="stylesheet">
	<script src="../js/plugins/bootstrap-table/popper.min.js"></script>
	<script src="../js/dsites_table.js"></script>
	<!-- Bootstrap table -->
	<script src="../js/plugins/bootstrap-datepicker/js/bootstrap-datepicker.min.js"></script>
	<script src="../js/plugins/bootstrap-datepicker/locales/bootstrap-datepicker.zh-CN.min.js"></script>
	<script src="../js/plugins/bootstrap-table/bootstrap-table.min.js"></script>
	<script src="../js/plugins/bootstrap-table/bootstrap-table-mobile.min.js"></script>
	<script src="../js/plugins/bootstrap-table/bootstrap-editable.js?1"></script>
	<script src="../js/plugins/bootstrap-table/bootstrap-table-editable.js"></script>
	<script src="../js/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.18.1/moment.min.js"></script>	
<%
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Danfoss Sites</title>
</head>
<body>
	<div class="col-sm-12">
		<div class="example-wrap">
			<h4 class="example-title">Danfoss Sites</h4>
			<div class="example">
				<div id="toolbar" class="btn-group">
					<button id="btn_add" type="button" class="btn btn-default"  data-toggle="modal" data-target="#addModal">
						<span class="glyphicon glyphicon-plus" aria-hidden="true">新增</span>
					</button>
					<button id="btn_delete" type="button" class="btn btn-default">
						<span class="glyphicon glyphicon-remove" aria-hidden="true">删除</span>
					</button>
				</div>
				<table id="table"></table>
			</div>
		</div>
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
						<form id="save_form">
						<div class="form-group">
							<label for="nameInput" class="col-md-2">店名</label>
							<div class="col-md-4">
								<input type="text" class="form-control" id="nameInput" name="name" >
							</div>
							<label for="ipInput" class="col-md-2">ip</label>
							<div class="col-md-4">
								<input type="text" class="form-control" id="ipInput" name="ip">
							</div>
						</div>
						<div class="form-group">
							<label for="soldierDeptIdInput" class="col-md-2">士兵部门id</label>
							<div class="col-md-4">
								<input type="text" class="form-control" id="soldierDeptIdInput" name="soldierDeptId" >
							</div>
							<label for="officerDeptIdInput" class="col-md-2">军官部门id</label>
							<div class="col-md-4">
								<input type="text" class="form-control" id="officerDeptIdInput" name="officerDeptId">
							</div>
						</div>
						<div class="form-group">
							<label for="agentIdInput" class="col-md-2">agentId</label>
							<div class="col-md-4">
								<input type="text" class="form-control" id="agentIdInput" name="agentId" value="6">
							</div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button id="btn_submit" type="button" class="btn btn-primary">提交</button>
				</div>
			</div>
		</div>
	</div>
	<!-- Modal end -->
</body>
</html>