$(function() {
	$(".input-date").datepicker({
		language : "zh-CN",
	});
	$('.input-date').datepicker('setDate', new Date()); 
})

function query() {
	var deliveryTimeFrom = $("#deliveryTimeFrom").val();
	var deliveryTimeTo = $("#deliveryTimeTo").val();
	var commissionPlanYear = $("#commissionPlanYear").val();
	var commissionPlanMonth = $("#commissionPlanMonth").val();
	var commissionStartFrom = $("#commissionStartFrom").val();
	var commissionStartTo = $("#commissionStartTo").val();
	var warrantyEndFromYear = $("#warrantyEndFromYear").val();
	var warrantyEndFromMonth = $("#warrantyEndFromYear").val();
	var warrantyEndToYear = $("#warrantyEndToYear").val();
	var warrantyEndToMonth = $("#warrantyEndToMonth").val();
	var d = new Date(Date.parse(s.replace(/-/g, "/")));
	$('#table').bootstrapTable('filterBy', {
		c0 : [ "盒马" ]
	}, {
		'filterAlgorithm' : function(row, filters) {
			var val = row["c0"];
			if (val == undefined)
				return false;
			if (val.indexOf("盒马") != -1)
				return true;
		}
	});
}