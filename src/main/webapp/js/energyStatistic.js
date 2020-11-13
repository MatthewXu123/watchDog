var colors = ["#4876FF","#458B00","#0D0D0D","#AEEEEE","#1C86EE"];
$(document).ready(function(){
	var sitename = $("#sitename").val();
	var idsite = $("#idsite").val();
	var month = $("#month").val();
	var day = $("#day").val();
	var tail  = "";
	if(month != "")
		tail = " "+month+"月份";
	else if(day != "")
		tail = " "+day+"日";
	for(var key in global)
	{
		var id = key.split("_")[0];
		var meterName = key.split("_")[1];
		var data = global[key];
		console.info(id);
		var ids = [];
		var months = [];
		if(data.length>0)
		 {
			var ctx = document.getElementById("global_canvas").getContext('2d');
			Chart.defaults.global.defaultFontSize = 25;
			var nums = [];
			for(var j = 0;j<data.length;j++){
				months.push(data[j].month);
				nums.push(data[j].num);
				ids.push(data[j].id);
			}
			globalChart = new Chart(ctx, {
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
						text: meterName+tail
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
			document.getElementById("global_canvas").onclick = function(evt){
				if(day != "")
					return;
			    var points = globalChart.getElementsAtEvent(evt);
			    // => activePoints is an array of points on the canvas that are at the same position as the click event.
			    if (points.length > 0) {
			        var index = points[0]._index;
			        if(ids.length>0)
			        {
			        	console.info(ids[index]);
			        	var url = "energyStatistics.jsp?idsite="+idsite+"&month="+months[index];
			        	if(month != "")
			        		url = "energyStatistics.jsp?idsite="+idsite+"&day="+month+"-"+months[index];
			        	//window.open(url);
			        	window.location.href = url;
			        }
			    }
			};
		 }
	}
	
	var datasets = [];
	var months = [];
	var jjj= 0;
	for(var key in detail)
	{
		months = [];
		var id = key.split("_")[0];
		var meterName = key.split("_")[1];
		var data = detail[key];
		console.info(id);
		var nums = [];
		if(data.length>0)
		 {
			var ctx = document.getElementById("detail_canvas").getContext('2d');
			Chart.defaults.global.defaultFontSize = 25;
			for(var j = 0;j<data.length;j++){
				months.push(data[j].month);
				nums.push(data[j].num);
			}
		 }
		var s = {
            label: meterName,
            data: nums,
            backgroundColor: colors[jjj],
            borderColor: colors[jjj++],
            borderWidth: 1,
            fill: false,
        }
		datasets.push(s);
	}
	var type = "bar";
	if(month != "" || day != "")
		type = "line";
	detailChart = new Chart(ctx, {
	    type: type,
	    data: {
	        labels: months,
	        datasets: datasets
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
				text: "电表电量"+tail
			},
			hover: {
                animationDuration: 0  
            },
            animation: {           // 这部分是数值显示的功能实现
                onComplete: function () {
                	if(month != "" || day != "")
                		return;
                	if(datasets.length>1)
                		return;
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
});
