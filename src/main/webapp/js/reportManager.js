var alarmMonthChart = null;
var alarmDescriptionChart = null;
var alarmDeviceChart = null;

$(document).ready(function(){
	var type = $("#type").val();
	if($("#naviRight").val() != "")
	{
		$(".navi_right").show();
	}
	$(".navi_left").on("click",function(){
		window.location.href = "reportManager.jsp?idsite="+$("#idsite").val()+"&start="+$("#naviLeft").val()+"&type="+type;
	 });
	$(".navi_right").on("click",function(){
		window.location.href = "reportManager.jsp?idsite="+$("#idsite").val()+"&start="+$("#naviRight").val()+"&type="+type;
	 });
	var dateRange = $("#dateRange").val();
	var sitename = $("#sitename").val();
	var str1 = dateRange+ "总能耗";
	var str2 = "周电量趋势";
	var str3 = dateRange + "高温报警";
	var str4 = dateRange + "重要报警";
	if(type == "m" || type == "M"){
		str1 = dateRange + "月总能耗";
		str2 = "月电量趋势";
		str3 = dateRange + "月高温报警";
		str4 = dateRange +"月重要报警";
	}
	
	drawChart(global,"global_canvas","电量",str1);
	drawChart(globalW,"globalW_canvas","电量",str2);
	drawChart(highAlarm,"highAlarm_canvas","数量",str3);
	drawChart(importantReset,"importantReset_canvas","数量",str4);
	
});

function drawChart(chartData,chartName,label,title){
	if(chartData.length>0)
	 {
		var data = chartData;
		var ctx = document.getElementById(chartName).getContext('2d');
		Chart.defaults.global.defaultFontSize = 25;
		var nums = [];
		var months = [];
		for(var j = 0;j<data.length;j++){
			months.push(data[j].month);
			nums.push(data[j].num);
		}
		globalChart = new Chart(ctx, {
		    type: 'bar',
		    data: {
		        labels: months,
		        datasets: [{
		            label: label,
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
	                            ctx.fillText(data, bar._model.x, bar._model.y-5);
	                        });
	                    });
	                }
	            }
		    }
		});
	 }
}

