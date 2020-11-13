var alarmMonthChart = null;
var alarmDescriptionChart = null;
var alarmDeviceChart = null;
var f1 = function(){
	var table=$('#reset_table');
	if(table.is(':hidden')){
		return;
	}
	
	var fold = $(window).height() + $(window).scrollTop();
	var $bottom = $("#reset_help");
	
	if( fold < $bottom.offset().top){
		return;
	}
	if($bottom.html().indexOf("底线")>0)
		return;
	$bottom.html("加载中，稍等片刻");


	var idsite= $("#idsite").val();
	$.ajax({
		url : 'servlet/resetalarm',
		type : 'post',
		data : {
			cmd:"detail_list",
			idsite: $("#idsite").val(),
			lastResetId : $("#last_reset_id").val(),
			month:$("#month").val(),
			day:$("#day").val(),
			description:$("#description").val(),
			iddevice:$("#iddevice").val(),
			idvariable:$("#idvariable").val()
			},
		 dataType : 'json', 
		 success: function(data){
			 if(data.length>0)
			 {
				 $("#last_reset_id").val(data[data.length-1].idAlarm);
				 var newRow = "";
				 for(var j = 0;j<data.length;j++){
					newRow += "<tr var="+data[j].idVariable+">";
					if($("#idvariable").val() == "")
						newRow += "<td>"+data[j].var+"<img class='i_history_reset' src='img/history.svg'/></td><td>"+data[j].device+"</td>"+
						"<td>"+data[j].timeRange+"</td><td>"+data[j].alarmDuration+"</td>";
					else
						newRow += "<td>"+data[j].var+"</td><td>"+data[j].humainStartTime+"</td><td>"+data[j].humainEndTime+"</td>"+
						"<td>"+data[j].alarmDuration+"</td>";
					newRow += "</tr>";
				}
				 table.append(newRow);
				 $bottom.html("点击加载更多");
			 }
			 else
			 {
				 $("#reset_help").html("够了，够了，你已经触及我的底线了");
			 }
		 }
		});
}
$(document).ready(function(){
	touches(document.body,"swipetop",f1);
	if($("#showAjax").val() == "false")
		$("#reset_help").hide();
	sitename = $("#sitename").val();
	alarmMonth();
	var monthnum = $("#monthnum").val();
	alarmDevice(monthnum);
	alarmDescription(monthnum);
	idsite = $("#idsite").val();
	var month = $("#month").val();
	var day = $("#day").val();
	if($("#statistic_table").length>0)
	{
		$('#statistic_table tbody').on('click','tr td:nth-child(1)', function (e) {
			//window.open(window.location.href+"&iddevice="+$(this).parent().attr("iddevice"));  
			window.location.href = window.location.href+"&iddevice="+$(this).parent().attr("iddevice");
		} );
	}
	if($("#reset_table").length>0)
	{
		if($("#idvariable").val() == "")
			$('#reset_table tbody').on('click','tr td:nth-child(1)', function (e) {
				//window.open("alarmStatistics.jsp?idsite="+idsite+"&idvariable="+$(this).parent().attr("var"));
				window.location.href = "alarmStatistics.jsp?idsite="+idsite+"&idvariable="+$(this).parent().attr("var");
			} );
	}
	if($("#alarmDescription").length == 0 && $("#alarmDevice").length == 0)
		$(".month_switch").hide();
	$(".month_switch").on("click",function(){
		var monthNum = $("#monthnum").val();
		var newMonth = 3;
		if(monthNum == 3)
			newMonth = 6;
		changeMonth(newMonth);
		$("#monthnum").val(newMonth);
		$(this).html("点击切换至"+monthNum+"个月数据");
		if($("#statistic_table").length>0)
			$(".table_head").html("控制器报警个数排名明细("+newMonth+"个月内)");
	});
	$(".reset_help").on("click",f1);
	var condition = "";
	if($("#month").val() != "")
		condition += $("#month").val()+"<br>";
	if($("#day").val() != "")
		condition += $("#day").val()+"<br>";
	if($("#iddevice").val() != "")
		condition += $("#devdescr").val()+"<br>";
	if($("#idvariable").val() != "")
	{
		condition += $("#devdescr").val()+"<br>";
		condition += $("#vardescr").val()+"<br>";
	}
	if($("#description").val() != "")
		condition += $("#description").val()+"<br>";
	$(".querycondition").html(condition);
});
function changeMonth(monthNum)
{
	alarmDevice(monthNum);
	if($("#description").val() == "")
		alarmDescription(monthNum);
	resetTable(monthNum);
}
function alarmDevice(monthNum)
{
	if($("#alarmDevice").length == 0)
		return;
	var month = $("#month").val();
	var day = $("#day").val();
	var chartTitle = "控制器报警个数排名("+monthNum+"个月内)";
	if(month != "")
		chartTitle = "控制器报警个数排名 "+month;
	else if(day != "")
		chartTitle = "控制器报警个数排名 "+day;
	$.ajax({
		url : 'servlet/resetalarm',
		type : 'post',
		data : {
			cmd:"statistic_alarm_device",
			idsite: $("#idsite").val(),
			month:$("#month").val(),
			monthNum:monthNum,
			day:day,
			description:$("#description").val()
			},
		 dataType : 'json', 
		 success: function(data){
			 if(alarmDeviceChart != null)
				 alarmDeviceChart.destroy();
			 //only one device, will not show chart
			 if(data.length>1)
			 {
				var ctx = document.getElementById("alarmDevice").getContext('2d');
				Chart.defaults.global.defaultFontSize = 25;
				var ids = [];
				var nums = [];
				var months = [];
				for(var j = 0;j<data.length;j++){
					months.push(data[j].month);
					nums.push(data[j].num);
					ids.push(data[j].id);
				}
				alarmDeviceChart = new Chart(ctx, {
				    type: 'horizontalBar',
				    data: {
				        labels: months,
				        datasets: [{
				            label: sitename,
				            data: nums,
				            backgroundColor: "#4876FF",
				            borderWidth: 1
				        }]
				    },
				    options: {
				        scales: {
				            yAxes: [{
				                ticks: {
				                    beginAtZero:true
				                }
				            }]
				        },
				        title: {
							display: true,
							text: chartTitle
						},
						hover: {
			                animationDuration: 0  
			            },
			            animation: {           // 这部分是数值显示的功能实现
			                onComplete: function () {
			                    var chartInstance = this.chart,
			 
			                    ctx = chartInstance.ctx;
			                    ctx.font = Chart.helpers.fontString(Chart.defaults.global.defaultFontSize, Chart.defaults.global.defaultFontStyle, Chart.defaults.global.defaultFontFamily);
			                    ctx.fillStyle = "black";
			                    ctx.textAlign = 'center';
			                    ctx.textBaseline = 'bottom';
			 
			                    this.data.datasets.forEach(function (dataset, i) {
			                        var meta = chartInstance.controller.getDatasetMeta(i);
			                        meta.data.forEach(function (bar, index) {
			                            var data = dataset.data[index];
			                            if(data == 0)
			                            	return true;
			                            ctx.fillText(data, bar._model.x+28, bar._model.y+15);
			                        });
			                    });
			                }
			            }
				    }
				});
				document.getElementById("alarmDevice").onclick = function(evt){
				    var points = alarmDeviceChart.getElementsAtEvent(evt);
				    // => activePoints is an array of points on the canvas that are at the same position as the click event.
				    if (points.length > 0) {
				        var index = points[0]._index;
				        if(ids.length>0)
				        {
				        	console.info(ids[index]);
				        	var url = window.location.href+"&iddevice="+ids[index];
				        	//window.open(url);
				        	window.location.href = url;
				        }
				    }
				};
			 }
		 }
	});	
}
function alarmDescription(monthNum)
{
	if($("#alarmDescription").length == 0)
		return;
	var month = $("#month").val();
	var day = $("#day").val();
	var chartTitle = "报警类型排名("+monthNum+"个月内)";
	if(month != "")
		chartTitle = "报警类型排名 "+month;
	else if(day != "")
		chartTitle = "报警类型排名 "+day;
	$.ajax({
		url : 'servlet/resetalarm',
		type : 'post',
		data : {
			cmd:"statistic_alarm_description",
			idsite: $("#idsite").val(),
			month:$("#month").val(),
			monthNum:monthNum,
			day:day,
			iddevice:$("#iddevice").val()
			},
		 dataType : 'json', 
		 success: function(data){
			 if(alarmDescriptionChart != null)
				 alarmDescriptionChart.destroy();
			 //only one alarm category, will not show chart
			 if(data.length>1)
			 {
				var ctx = document.getElementById("alarmDescription").getContext('2d');
				Chart.defaults.global.defaultFontSize = 25;
				var nums = [];
				var months = [];
				for(var j = 0;j<data.length;j++){
					months.push(data[j].month);
					nums.push(data[j].num);
				}
				alarmDescriptionChart = new Chart(ctx, {
				    type: 'horizontalBar',
				    data: {
				        labels: months,
				        datasets: [{
				            label: sitename,
				            data: nums,
				            backgroundColor: "#4876FF",
				            borderWidth: 1
				        }]
				    },
				    options: {
				        scales: {
				            yAxes: [{
				                ticks: {
				                    beginAtZero:true
				                }
				            }]
				        },
				        title: {
							display: true,
							text: chartTitle
						},
						hover: {
			                animationDuration: 0  
			            },
			            animation: {           // 这部分是数值显示的功能实现
			                onComplete: function () {
			                    var chartInstance = this.chart,
			 
			                    ctx = chartInstance.ctx;
			                    ctx.font = Chart.helpers.fontString(Chart.defaults.global.defaultFontSize, Chart.defaults.global.defaultFontStyle, Chart.defaults.global.defaultFontFamily);
			                    ctx.fillStyle = "black";
			                    ctx.textAlign = 'center';
			                    ctx.textBaseline = 'bottom';
			 
			                    this.data.datasets.forEach(function (dataset, i) {
			                        var meta = chartInstance.controller.getDatasetMeta(i);
			                        meta.data.forEach(function (bar, index) {
			                            var data = dataset.data[index];
			                            if(data == 0)
			                            	return true;
			                            ctx.fillText(data, bar._model.x+28, bar._model.y+15);
			                        });
			                    });
			                }
			            }
				    }
				});
				//if(day == "")
				{
					document.getElementById("alarmDescription").onclick = function(evt){
					    var points = alarmDescriptionChart.getElementsAtEvent(evt);
					    // => activePoints is an array of points on the canvas that are at the same position as the click event.
					    if (points.length > 0) {
					        var index = points[0]._index;
					        if(months.length>0)
					        {
					        	console.info(months[index]);
					        	var encoded = encodeURI(months[index]);
					    		encoded = encodeURI(encoded);
					        	var url = window.location.href+"&description="+encoded;
					        	//window.open(url);
					        	window.location.href = url;
					        }
					    }
					};
				}
			 }
		 }
	});	
}
function alarmMonth()
{
	var month = $("#month").val();
	var day = $("#day").val();
	var queryURL = "";
	var title = "";
	if(month == "" && day == "")
	{
		queryURL = "statistic_month";
		title = "报警数量趋势图";
	}
	else if(month != "")
	{
		queryURL = "statistic_daily";
		title = '报警个数分析 '+month;
	}
	else if(day != "")
	{
		queryURL = "statistic_hourly";
		title = '报警个数分析 '+day;
	}
	var months = [];
	$.ajax({
		url : 'servlet/resetalarm',
		type : 'post',
		data : {
			cmd:queryURL,
			idsite: $("#idsite").val(),
			idvariable:$("#idvariable").val(),
			month:month,
			day:day,
			description:$("#description").val(),
			iddevice:$("#iddevice").val()
			},
		 dataType : 'json', 
		 success: function(data){
			 if(alarmMonthChart != null)
				 alarmMonthChart.destroy();
			 if(data.length>0)
			 {
				var ctx = document.getElementById("alarmMonth").getContext('2d');
				Chart.defaults.global.defaultFontSize = 25;
				var nums = [];
				for(var j = 0;j<data.length;j++){
					months.push(data[j].month);
					nums.push(data[j].num);
				}
				alarmMonthChart = new Chart(ctx, {
				    type: 'bar',
				    data: {
				        labels: months,
				        datasets: [{
				            label: sitename,
				            data: nums,
				            backgroundColor: "#4876FF",
				            borderWidth: 1
				        }]
				    },
				    options: {
				        scales: {
				            yAxes: [{
				                ticks: {
				                    beginAtZero:true
				                }
				            }]
				        },
				        title: {
							display: true,
							text: title
						},
						 hover: {
				                animationDuration: 0  // 防止鼠标移上去，数字闪烁
				            },
				            animation: {           // 这部分是数值显示的功能实现
				                onComplete: function () {
				                    var chartInstance = this.chart,
				 
				                    ctx = chartInstance.ctx;
				                    // 以下属于canvas的属性（font、fillStyle、textAlign...）
				                    ctx.font = Chart.helpers.fontString(Chart.defaults.global.defaultFontSize, Chart.defaults.global.defaultFontStyle, Chart.defaults.global.defaultFontFamily);
				                    ctx.fillStyle = "black";
				                    ctx.textAlign = 'center';
				                    ctx.textBaseline = 'bottom';
				 
				                    this.data.datasets.forEach(function (dataset, i) {
				                        var meta = chartInstance.controller.getDatasetMeta(i);
				                        meta.data.forEach(function (bar, index) {
				                            var data = dataset.data[index];
				                            if(data == 0)
				                            	return true;
				                            ctx.fillText(data, bar._model.x, bar._model.y - 5);
				                        });
				                    });
				                }
				            }
				    }
				});
				if(day == "")
				{
					document.getElementById("alarmMonth").onclick = function(evt){
					    var points = alarmMonthChart.getElementsAtEvent(evt);
					    // => activePoints is an array of points on the canvas that are at the same position as the click event.
					    if (points.length > 0) {
					        var index = points[0]._index;
					        if(months.length>0)
					        {
					        	console.info(months[index]);
					        	var url = window.location.href+"&month="+months[index];
					        	if(month != "")
					        	{
					        		url = jsUrlHelper.delUrlParam(window.location.href,"month");
					        		url = url+"&day="+month+"-"+months[index];
					        	}
					        	//window.open(url);
					        	window.location.href = url;
					        }
					    }
					};
				}
			 }
		 }
	});
}
function resetTable(monthNum)
{
	if($("#reset_table").length>0)
		return;
	$("#reset_table tr:not(:first)").empty(); 
	var table = $("#reset_table");
	$.ajax({
		url : 'servlet/resetalarm',
		type : 'post',
		data : {
			cmd:"statistic_alarm_device",
			idsite: $("#idsite").val(),
			monthNum:monthNum,
			all: "true"
			},
		 dataType : 'json', 
		 success: function(data){
			 if(data.length>0)
			 {
				 var newRow = "";
				 for(var j = 0;j<data.length;j++){
					 newRow += "<tr iddevice="+data[j].id+">"+
							"<td>"+data[j].month+"<img class='i_history_reset' src='img/history.svg'/></td><td>"+data[j].num+"</td>"+
							"</tr>";
				}
				table.append(newRow);
			 }
		 }
		});
}