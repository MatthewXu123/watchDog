$(function () {
	$(".input-date").datepicker({
		language: "zh-CN",
	});
	$("button[name='clearSearch']").click(function () {
		$('.input-date').datepicker('setDate', "");
		$(".default").attr("selected", true);
		$('#table').bootstrapTable('filterBy', {},
			{
				'filterAlgorithm': function (row, filters) {
					return true;
				}
			});
	});
	//$('.input-date').datepicker('setDate', new Date()); 
})

function query() {
	var flags = [];
	//发货时间
	var deliveryTimeFromStr = $("#deliveryTimeFrom").val();
	var deliveryTimeToStr = $("#deliveryTimeTo").val();

	//调试计划时间
	var commissionPlanYearStr = $("#commissionPlanYear").val();
	var commissionPlanMonthStr = $("#commissionPlanMonth").val();

	//调试开始时间
	var commissionStartFromStr = $("#commissionStartFrom").val();
	var commissionStartToStr = $("#commissionStartTo").val();

	//保修到期时间
	var warrantyEndFromYearStr = $("#warrantyEndFromYear").val();
	var warrantyEndFromMonthStr = $("#warrantyEndFromMonth").val();
	var warrantyEndToYearStr = $("#warrantyEndToYear").val();
	var warrantyEndToMonthStr = $("#warrantyEndToMonth").val();

	$('#table').bootstrapTable('filterBy',
		{
			deliveryTime: [deliveryTimeFromStr, deliveryTimeToStr],
			commissionPlanTime: [commissionPlanYearStr, commissionPlanMonthStr],
			commissionStartTime: [commissionStartFromStr, commissionStartToStr],
			warrantyEndTime: [warrantyEndFromYearStr, warrantyEndFromMonthStr, warrantyEndToYearStr, warrantyEndToMonthStr]
		}
		, {
			'filterAlgorithm': function (row, filters) {
				if (row == undefined || row == null)
					return false;
				// 发货时间过滤
				var rowDeliveryTimeStamp = row["deliveryTime"];
				if (isStrValid(filters.deliveryTime[0])
					&& !(rowDeliveryTimeStamp != undefined && rowDeliveryTimeStamp >= strToTimeStamp(filters.deliveryTime[0]))) {
					return false;
				}
				if (isStrValid(filters.deliveryTime[1])
					&& !(rowDeliveryTimeStamp != undefined && rowDeliveryTimeStamp <= strToTimeStamp(filters.deliveryTime[1]))) {
					return false;
				}
				//调试计划时间过滤
				var rowCommissionPlanndTimeStamp = row["commissionPlannedTime"];
				var rowCommissionPlanndTime = new Date(rowCommissionPlanndTimeStamp);
				if (isNumber(filters.commissionPlanTime[0])
					&& !(rowCommissionPlanndTime != undefined && rowCommissionPlanndTime.getFullYear() == Number(filters.commissionPlanTime[0]))) {
					return false;
				}
				if (isNumber(filters.commissionPlanTime[1])
					&& !(rowCommissionPlanndTime != undefined && (rowCommissionPlanndTime.getMonth() + 1) == parseInt(filters.commissionPlanTime[1]))) {
					return false;
				}

				//调试开始时间过滤
				var rowCommissionStartTimeStamp = row["commissionStartTime"];
				if (isStrValid(filters.commissionStartTime[0])
					&& !(rowCommissionStartTimeStamp != undefined && rowCommissionStartTimeStamp >= strToTimeStamp(filters.commissionStartTime[0]))) {
					return false;
				}
				if (isStrValid(filters.commissionStartTime[1])
					&& !(rowCommissionStartTimeStamp != undefined && rowCommissionStartTimeStamp <= strToTimeStamp(filters.commissionStartTime[1]))) {
					return false;
				}

				//保修到期时间
				var rowWarrantyEndTimeStamp = row["warrantyEndTime"];
				var rowWarrantyEndTime = new Date(rowWarrantyEndTimeStamp);
				if (isNumber(warrantyEndFromYearStr) && isNumber(warrantyEndFromMonthStr)
					&& !(rowWarrantyEndTime != undefined
						&& rowWarrantyEndTimeStamp >= strToTimeStamp(filters.warrantyEndTime[0] + "-" + filters.warrantyEndTime[1] + "-01"))) {
					return false;
				}
				if (isNumber(warrantyEndToYearStr) && isNumber(warrantyEndToMonthStr)
					&& !(rowWarrantyEndTime != undefined
						&& rowWarrantyEndTimeStamp <= strToTimeStamp(filters.warrantyEndTime[2] + "-" + (parseInt(filters.warrantyEndTime[3])) + "-01"))) {
					return false;
				}
				return true;

			}
		});
}

function isStrValid(str) {
	return str != undefined && str != null && str.length != 0;
}
function isNumber(str) {
	return !isNaN(Number(str));
}
function strToTimeStamp(str) {
	return new Date(Date.parse(str.replace(/-/g, "/"))).getTime();
}
function phoneFormatter(value) {
	if (value == undefined)
		return value;
	value = value.replace(/\s*/g, "");
	if (value.indexOf("E10") == -1)
		return value;
	var num = new Number(value);
	return num.toLocaleString().replaceAll(",", "");
}

function dateFormatter(value) {
	if (value != undefined) {
		var date = new Date(value); //时间戳为10位需*1000，时间戳为13位的话不需乘1000
		var Y = date.getFullYear() + '-';
		var M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
		var D = date.getDate() < 10 ? '0' + date.getDate() : date.getDate();
		return Y + M + D;
	} else
		return "";
}