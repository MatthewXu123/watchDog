<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<link href="../css/bootstrap.min.css" rel="stylesheet">
<link href="../css/plugins/bootstrap-table/bootstrap-table.min.css" rel="stylesheet">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>RPMS</title>
</head>
<body>
	<div class="col-sm-12">
		<!-- Example Events -->
		<div class="example-wrap">
			<h4 class="example-title">销售人员</h4>
			<div class="example">
				<div id="toolbar" class="btn-group">
					<button id="btn_add" type="button" class="btn btn-default">
						<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>新增
					</button>
					<button id="btn_edit" type="button" class="btn btn-default">
						<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>修改
					</button>
					<button id="btn_delete" type="button" class="btn btn-default">
						<span class="glyphicon glyphicon-remove" aria-hidden="true"></span>删除
					</button>
				</div>
				<table id="table"></table>
			</div>
		</div>
		<!-- End Example Events -->
	</div>

	<script src="../js/framework/jquery/3.3.1/jquery.min.js"></script>
	<script src="../js/framework/bootstrap/4.2.1/js/bootstrap.min.js"></script>
	<!-- Bootstrap table -->
	<script src="../js/plugins/bootstrap-table/bootstrap-table.min.js"></script>
	<script
		src="../js/plugins/bootstrap-table/bootstrap-table-mobile.min.js"></script>
	<script
		src="../js/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
</body>
</html>