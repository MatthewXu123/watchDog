<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<link rel="stylesheet" href="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/css/bootstrap.min.css">
	<script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>
	<script src="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/js/bootstrap.min.js"></script>
	
	<!-- <link href="../css/bootstrap.min.css" rel="stylesheet"> -->
	<link href="../css/font-awesome.min.css" rel="stylesheet">
	<link href="../css/plugins/bootstrap-table/bootstrap-table.min.css" rel="stylesheet">
	<link href="../css/plugins/bootstrap-table/bootstrap-editable.css" rel="stylesheet">
	<!-- <script src="../js/framework/jquery/3.3.1/jquery.min.js"></script> -->
	<script src="../js/plugins/bootstrap-table/popper.min.js"></script>
	<script src="../js/table.js"></script>
	<!-- <script src="../js/framework/bootstrap/4.2.1/js/bootstrap.min.js"></script> -->
	<!-- Bootstrap table -->
	<script src="../js/plugins/bootstrap-table/bootstrap-table.min.js"></script>
	<script src="../js/plugins/bootstrap-table/bootstrap-table-mobile.min.js"></script>
	<script src="../js/plugins/bootstrap-table/bootstrap-editable.js?1"></script>
	<script src="../js/plugins/bootstrap-table/bootstrap-table-editable.js"></script>
	<script src="../js/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.18.1/moment.min.js"></script>	
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
	<div class="modal" id="addModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title" id="myModalLabel">新增</h4>
				</div>
				<div class="modal-body">在这里添加一些文本</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭
					</button>
					<button type="button" class="btn btn-primary">提交更改</button>
				</div>
			</div>
		</div>
	</div>
	<!-- Modal end -->
</body>
</html>