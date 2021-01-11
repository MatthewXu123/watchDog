<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
	integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
	crossorigin="anonymous">
<link rel="stylesheet"
	href="https://use.fontawesome.com/releases/v5.6.3/css/all.css"
	integrity="sha384-UHRtZLI+pbxtHCWp1t77Bi1L4ZtiqrqD80Kn4Z8NTSRyMA2Fd33n5dQ8lWUE00s/"
	crossorigin="anonymous">
<link rel="stylesheet"
	href="https://unpkg.com/bootstrap-table@1.18.1/dist/bootstrap-table.min.css">
<link rel="stylesheet" href="../css/retail.css">
<link
	href="../js/plugins/bootstrap-datepicker/css/bootstrap-datepicker3.min.css"
	rel="stylesheet">

<script
	src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.5.1/jquery.min.js"
	integrity="sha512-bLT0Qm9VnAYZDflyKcBaQ2gg0hSYNQrJ8RilYldYQ1FxQYoCLtUjuuRuZo+fjqhx/qtq/1itJ0C2ejDxltZVFg=="
	crossorigin="anonymous"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"
	integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1"
	crossorigin="anonymous">
</script>
<script
	src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"
	integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM"
	crossorigin="anonymous">
</script>
<script
	src="https://unpkg.com/bootstrap-table@1.18.1/dist/bootstrap-table.min.js"></script>
<script
	src="https://unpkg.com/bootstrap-table@1.18.1/dist/extensions/filter-control/bootstrap-table-filter-control.min.js">
</script>
<script
	src="../js/plugins/bootstrap-datepicker/js/bootstrap-datepicker.min.js"></script>
<script
	src="../js/plugins/bootstrap-datepicker/locales/bootstrap-datepicker.zh-CN.min.js"></script>
<script src="../js/retailTable.js"></script>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Retail Project</title>
</head>

<body>
	<div class="container-fluid div-date-container">
		<div class="row div-split">
			<div class="col-lg-5 row div-date-fromto div-date-fromto">
				<span>发货时间:</span> <input class="form-control input-date"
					type="text" id="deliveryTimeFrom" placeholder="请选择日期"> <span>-</span>
				<input class="form-control input-date" type="text"
					id="deliveryTimeTo" placeholder="请选择日期">
			</div>
			<div class="col-lg-5 row div-date-fromto">
				<span>计划调试时间:</span> <select id="commissionPlanYear"
					class="form-control select-year select-default">
					<option class="default">-年-</option>
					<option>2019</option>
					<option>2020</option>
					<option>2021</option>
					<option>2022</option>
					<option>2023</option>
				</select> <select id="commissionPlanMonth" class="form-control select-month">
					<option class="default">-月-</option>
					<option>1</option>
					<option>2</option>
					<option>3</option>
					<option>4</option>
					<option>5</option>
					<option>6</option>
					<option>7</option>
					<option>8</option>
					<option>9</option>
					<option>10</option>
					<option>11</option>
					<option>12</option>
				</select>
			</div>
		</div>
		<div class="row div-split">
			<div class="col-lg-5 row div-date-fromto">
				<span>调试开始时间:</span> <input class="form-control input-date"
					type="text" id="commissionStartFrom" placeholder="请选择日期"> <span>-</span>
				<input class="form-control input-date" type="text"
					id="commissionStartTo" placeholder="请选择日期">
			</div>
			<div class="col-lg-5 row div-date-fromto">
				<span>保修到期时间:</span> <select id="warrantyEndFromYear"
					class="form-control select-year select-default">
					<option class="default">-年-</option>
					<option>2019</option>
					<option>2020</option>
					<option>2021</option>
					<option>2022</option>
					<option>2023</option>
				</select> <select id="warrantyEndFromMonth" class="form-control select-month">
					<option class="default">-月-</option>
					<option>1</option>
					<option>2</option>
					<option>3</option>
					<option>4</option>
					<option>5</option>
					<option>6</option>
					<option>7</option>
					<option>8</option>
					<option>9</option>
					<option>10</option>
					<option>11</option>
					<option>12</option>
				</select> <span>-</span> <select id="warrantyEndToYear"
					class="form-control select-year select-default">
					<option class="default">-年-</option>
					<option>2019</option>
					<option>2020</option>
					<option>2021</option>
					<option>2022</option>
					<option>2023</option>
				</select> <select id="warrantyEndToMonth" class="form-control select-month">
					<option class="default">-月-</option>
					<option>1</option>
					<option>2</option>
					<option>3</option>
					<option>4</option>
					<option>5</option>
					<option>6</option>
					<option>7</option>
					<option>8</option>
					<option>9</option>
					<option>10</option>
					<option>11</option>
					<option>12</option>
				</select>
			</div>
		</div>
		<div class="row div-btn div-split">
			<button id="btn_query" class="btn btn-info" onclick="query()">查询</button>
		</div>
		<div class="input-append">
			<form action="upload" method="post" enctype="multipart/form-data">
				<input id="uploadFile" name="uploadFile" type="file" style="display: none" multiple> 
				<input id="fileCover" class="input-large" type="text" style="height: 30px">
				<button type="button" class="btn btn-default" onclick="$('input[id=uploadFile]').click();">浏览</button>
				<button id="submit" type="submit" class="btn btn-default">上传</button>
			</form>
		</div>
	</div>
	<table id="table" data-toggle="table"
		data-url="/watchDog/retail/getData" data-search="true"
		data-show-refresh="true" data-show-columns="true"
		data-toolbar="#toolbar" data-pagination="true"
		data-show-search-clear-button="true" data-filter-control="true">
		<thead>
			<tr>
				<th data-field="customer" data-filter-control="select">客户</th>
				<th data-field="description" data-filter-control="input">项目</th>
				<th data-field="ip" data-visible="false">ip</th>
				<th data-field="province" data-visible="false">省份</th>
				<th data-field="purchaser" data-filter-control="select"
					data-visible="false">采购方</th>
				<th data-field="manufacturer" data-filter-control="select">工程商</th>
				<th data-field="cabinetSupplier" data-visible="false">柜商</th>
				<th data-field="projectType" data-visible="false">项目类型</th>
				<th data-field="contactPerson" data-filter-control="select"
					data-visible="false">项目联系人</th>
				<th data-field="sales" data-filter-control="select">销售</th>
				<th data-field="contactMobile" data-filter-control="select"
					data-formatter="phoneFormatter">联系电话</th>
				<th data-field="deliveryTime" data-formatter="dateFormatter">发货时间</th>
				<th data-field="commissionPlannedTime"
					data-formatter="dateFormatter">计划调试时间</th>
				<th data-field="projectStatus" data-filter-control="select">项目进度</th>
				<th data-field="cstPerson" data-filter-control="select">CST</th>
				<th data-field="commissionStartTime" data-formatter="dateFormatter">调试开始时间</th>
				<th data-field="projectComment" data-visible="false">备注</th>
				<th data-field="projectAddress" data-visible="false">项目地址</th>
				<th data-field="warrantyStartTime" data-formatter="dateFormatter">保修起始</th>
				<th data-field="warrantyPeriod" data-visible="false"
					data-formatter="dateFormatter">保修年限</th>
				<th data-field="warrantyEndTime" data-formatter="dateFormatter">保修到期</th>
			</tr>
		</thead>
	</table>
</body>
<script>
    $(function () {
        $('#table').bootstrapTable();
    });
</script>

</html>