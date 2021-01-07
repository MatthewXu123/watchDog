<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
	<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.6.3/css/all.css" integrity="sha384-UHRtZLI+pbxtHCWp1t77Bi1L4ZtiqrqD80Kn4Z8NTSRyMA2Fd33n5dQ8lWUE00s/" crossorigin="anonymous">
	<link rel="stylesheet" href="https://unpkg.com/bootstrap-table@1.18.1/dist/bootstrap-table.min.css">
	<script src="../js/retailTable.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.5.1/jquery.min.js" integrity="sha512-bLT0Qm9VnAYZDflyKcBaQ2gg0hSYNQrJ8RilYldYQ1FxQYoCLtUjuuRuZo+fjqhx/qtq/1itJ0C2ejDxltZVFg==" crossorigin="anonymous"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
	<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
	<script src="https://unpkg.com/bootstrap-table@1.18.1/dist/bootstrap-table.min.js"></script>
	<script src="https://unpkg.com/bootstrap-table@1.18.1/dist/extensions/filter-control/bootstrap-table-filter-control.min.js"></script>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Retail Project</title>
</head>
<body>
	<div class="col-sm-12">
	<button class="btn btn-info" onclick="query()">按钮</button>
		<div class="example-wrap">
			<div class="example">
				<table id="table" 
					data-toggle="table"
					data-url="/watchDog/retail/getData"
					data-search="true"
  					data-show-refresh="true"
  					data-show-columns="true"
  					data-toolbar="#toolbar"
  					data-pagination="true"
  					data-show-search-clear-button="true"
  					data-filter-control="true">
					<thead>
   						<tr>
							<th data-field="customer" data-filter-control="select">客户</th>
      						<th data-field="description" data-filter-control="input">项目</th>
      						<th data-field="ip" data-visible="false">ip</th>
      						<th data-field="province" data-visible="false">省份</th>
      						<th data-field="purchaser" data-filter-control="select" data-visible="false">采购方</th>
      						<th data-field="manufacturer" data-filter-control="select">工程商</th>
      						<th data-field="cabinetSupplier" data-visible="false">柜商</th>
      						<th data-field="projectType" data-visible="false">项目类型</th>
      						<th data-field="contactPerson" data-filter-control="select" data-visible="false">项目联系人</th>
      						<th data-field="sales" data-filter-control="select">销售</th>
      						<th data-field="contactMobile" data-filter-control="select" data-formatter="phoneFormatter">联系电话</th>
      						<th data-field="deliveryTime">发货时间</th>
      						<th data-field="commissionPlanndTime">计划调试时间</th>
      						<th data-field="projectStatus" data-filter-control="select">项目进度</th>
      						<th data-field="cstPerson" data-filter-control="select">CST</th>
      						<th data-field="commissionStartTime">调试开始时间</th>
      						<th data-field="projectComment" data-visible="false">备注</th>
      						<th data-field="projectAddress" data-visible="false">项目地址</th>
      						<th data-field="warrantyStartTime">保修起始</th>
      						<th data-field="warrantyPeriod" data-visible="false">保修年限</th>
      						<th data-field="warrantyEndTime">保修到期</th>
      					</tr>
      				</thead>
				</table>
			</div>
		</div>
	</div>
</body>
<script>
$(function() {
    $('#table').bootstrapTable();
  });
function phoneFormatter(value){
	if(value == undefined)
		return value;
	value = value.replace(/\s*/g,"");
	if(value.indexOf("E10") == -1)
		return value;
	var num = new Number(value);
	return num.toLocaleString().replaceAll(",","");
}  

/* function dateFormatter(value){
	if(value != undefined){
		if(value.indexOf("-") != -1){
			var valSplits = value.split("-");
			if(valSplits.length == 3)
				return valSplits[0] + "-" + getMonthNum(valSplits[1]) + "-" + valSplits[2];
		}
	}
	return value;
};
function getMonthNum(month){
	var monthNum;
	switch (month) {
 	case "Jan":
 		monthNum = "01";
 		break;
 	case "Feb":
 		monthNum = "02";
 		break;
 	case "Mar":
 		monthNum = "03";
 		break;
 	case "Apr":
 		monthNum = "04";
 		break;
 	case "May":
 		monthNum = "05";
 		break;
 	case "Jun":
 		monthNum = "06";
 		break;
 	case "Jul":
 		monthNum = "07";
 		break;
 	case "Aug":
 		monthNum = "08";
 		break;
 	case "Sep":
 		monthNum = "09";
 		break;
 	case "Oct":
 		monthNum = "10";
 		break;
 	case "Nov":
 		monthNum = "11";
 		break;
 	case "Dec":
 		monthNum = "12";
 		break;
 	default:
	break;
	}
	return monthNum;
} */
</script>
</html>