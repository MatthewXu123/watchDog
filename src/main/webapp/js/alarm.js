
var selectedRow = [];
var idsite = "";
$(document).ready(function(){
	idsite = $("#idsite").val();
	$(".statistic span").on("click",function(){
		var $container = $(".can_container");
		if($container.is(':hidden'))
			$container.show();
		else
			$container.hide();
	 });
	$(".alarm_device").on("click",function(){
		window.open($(this).parent().attr("durl"));
	 });
	$(".alarm_starttime").on("click",function(){
		window.open("alarmStatistics.jsp?idsite="+idsite+"&idvariable="+$(this).parent().attr("var"));
	});	
	$(".circle_span").on("click",function(){
		$(this).toggleClass("circle_selected");
		$(this).parent().toggleClass("row_alarm_selected");
	});	
	
	$(".dropdown-item").on("click",function(){
		var id = $(this).attr("id");
		if(id.indexOf("_")>0)
		{
			id = id.split("_")[1];
			hasAlarmRowSelected();
			var selectedNum = selectedRow.length;
			if(selectedNum>0)
			{
				$(".modal-body").hide();
				$(".body-"+id).show();
				$("#cmd").val(id);
				$("#idalarms").val(selectedRow);
				if(id == "acknowledge")
				{
					$("#myModalLabel").html("确认报警");
				}
				else if(id == "manage")
				{
					$("#myModalLabel").html("处理结果反馈");
				}
				else if(id == "reset")
				{
					$("#myModalLabel").html("强制复位情况说明");
				}
				$(this).attr("data-target","#mymodel");
			}
			else
			{
				$(this).attr("data-target","");
				alert("您还没有选择要处理的报警哦，点击报警行右上角的白色圆圈");
			}
		}
	});
	$(".btn.btn-primary").on("click",function(){
		$(this).parents("form").submit();
	});
	$(".detail").on("click",function(){
		$(".row_alarm").toggleClass("short_row");    
	 });
	/*$.ajax({
		url:'checkSite.do',
		method:'post',
		data:{
			idsite:idsite,
		},
		dataType:'json',
		success:function(data){
			if(data["issiteonline"] == "true"){
				$(".device_status").each(function(){
					$(this).show();
					if(data[($(this).attr("id").split("_"))[0]] == "true")
						$(this).attr("src","img/browse.svg");
					else
						$(this).attr("src","img/offline.svg");
				});
				
			}else if(data["issiteonline"] == "false"){
				$(".device_status").each(function(){
					$(this).show();
					$(this).attr("src","img/offline.svg");
				});
			}
		},
		error: function (XMLHttpRequest, textStatus, errorThrown) {
            // 状态码
            console.log(XMLHttpRequest.status);
            // 状态
            console.log(XMLHttpRequest.readyState);
            // 错误信息   
            console.log(textStatus);
        }
	});*/
});
function hasAlarmRowSelected()
{
	selectedRow = [];
	var hasSelected = false;
	$(".circle_span").each(function(){
		var class_ = $(this).attr("class");
	    if(class_.indexOf("circle_selected")>=0)
	    {
	    	selectedRow.push($(this).parent().attr("ida"));
	    }
	  });
};
function checkDevice(obj){
	var id_on = $(obj).attr("id");
	var idAlarms = id_on.split("_")[0];
	
	var ip_id = "#" + idAlarms + "_ip";
	var ip = $(ip_id).val();
	
	var devCode_id = "#" + idAlarms + "_devCode";
	var devCode = $(devCode_id).val();
	$.ajax({
		url:'checkDevice.do',
		method:'post',
		data:{
			ip:ip,
			devCode:devCode,
		},
		type:'text',
		success:function(isOnline){
			if(isOnline == "false"){
				$(obj).attr("src","img/offline.svg");
			}else if(isOnline == "true"){
				$(obj).attr("src","img/browse.svg");
				var url = 'device.jsp?ip=' + ip + '&devCode=' + devCode;
				window.open(url);
			}
		},
		error: function (XMLHttpRequest, textStatus, errorThrown) {
            // 状态码
            console.log(XMLHttpRequest.status);
            // 状态
            console.log(XMLHttpRequest.readyState);
            // 错误信息   
            console.log(textStatus);
        }
	});
};

