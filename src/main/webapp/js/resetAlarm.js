
var selectedRow = [];
var idsite = "";
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


	$.ajax({
		url : 'servlet/resetalarm',
		type : 'post',
		data : {
			cmd:"detail_list",
			idsite: idsite,
			idvariable:$("#idvariable").val(),
			lastResetId : $("#last_reset_id").val(),
			description:$("#description").val(),
			all:$("#all").val()
			},
		 dataType : 'json', 
		 success: function(data){
			 if(data.length>0)
			 {
				 $("#last_reset_id").val(data[data.length-1].idAlarm);
				 var newRow = "";
				 for(var j = 0;j<data.length;j++){
					// add input checkbox
					var resetBoxClass;
					if(data[j].useSpare != null && data[j].useSpare.length != 0){
						resetBoxClass = "resetbox_green";
					}else{
						resetBoxClass = "resetbox";
					}
					var idAlarm = data[j].idAlarm;
					var input = "<td ida=" + idAlarm + ">"
					+"<input id=" + idAlarm + " class=" + resetBoxClass + " type='checkbox'" + " value=" + data[j].useSpare + ">"
					+"<label for=" + idAlarm + "></label>"
					+"</td>";
					
					newRow += "<tr var="+data[j].idVariable+">";
					if($("#idvariable").val() == "")
						newRow += "<td>"+data[j].var+"</td><td>"+data[j].device+"</td>"+
						"<td>"+data[j].timeRange+"</td><td>"+data[j].alarmDuration+"</td>" + input;
					else
						newRow += "<td>"+data[j].var+"</td><td>"+data[j].humainStartTime+"</td><td>"+data[j].humainEndTime+"</td>"+
						"<td>"+data[j].alarmDuration+"</td>" + input;
					newRow += "</tr>";
				}
				 table.append(newRow);
				 if($("#idvariable").val() == "")
						 $('#reset_table tbody').on('click','tr td:nth-child(1)', function (e) {
							 window.open("alarmStatistics.jsp?idsite="+idsite+"&idvariable="+$(this).parent().attr("var"));
							} );
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
	idsite = $("#idsite").val();
	$(".statistic span").on("click",function(){
		var $container = $(".can_container");
		if($container.is(':hidden'))
			$container.show();
		else
			$container.hide();
	 });
	if($("#reset_table").length>0)
	{
			$('#reset_table tbody').on('click','tr td:nth-child(1)', function (e) {
				//window.open("alarmStatistics.jsp?idsite="+idsite+"&idvariable="+$(this).parent().attr("var"));
				window.location.href = "alarmStatistics.jsp?idsite="+idsite+"&iddevice="+$(this).parent().attr("idDevice");
			} );
	};
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
				/*
				 * if(id == "acknowledge") { $("#myModalLabel").html("确认报警"); }
				 */
				if(id == "manage")
				{
					$("#myModalLabel").html("处理结果反馈");
				}
				$(this).attr("data-target","#mymodel");
			}
			else
			{
				$(this).attr("data-target","");
				alert("您还没有选择要处理的报警哦");
			}
		}
	});
	/*$(".btn.btn-primary").on("click",function(){
		$(this).parents("form").submit();
	});
	$(".detail").on("click",function(){
		$(".row_alarm").toggleClass("short_row");    
	 });*/
});
function hasAlarmRowSelected()
{
	selectedRow = [];
	var hasSelected = false;
	$(".resetbox").each(function(){
	    if(this.checked == true)
	    {
	    	selectedRow.push($(this).parent().attr("ida"));
	    }
	  });
	/*
	 * $(".resetbox_green").each(function(){ if(this.checked == true) {
	 * selectedRow.push($(this).parent().attr("ida")); } });
	 */
};

function submitAjax(){
	var manage_type;
	$(".form-check-input").each(function(){
		if(this.checked == true){
			manage_type = $(this).val();
		}
	});
	$.ajax({
		type: 'get',
		url: 'servlet/managealarm',
		data:
		{
			idsite:idsite,
			cmd:$("#cmd").val(),
			idalarms:$("#idalarms").val(),
			table_type:$("#table_type").val(),
			manage_type:manage_type,
		},
		dataType:'JSON',
		success:function(data){
			$("#mymodel").modal("hide");
			for(var i = 0; i < selectedRow.length; i++){
				var id = selectedRow[i];
				var input = "#" + id +"_input";
				var label = "#" + id + "_label";
				var img = "#" + id + "_img";
				$(input).hide();
				$(label).hide();
				$(img).attr("alt",this.value);
				$(img).attr("src","img/resetalarm/" + data[id].split(";")[0] + ".svg");
				$(img).show();
			}
			$(".resetbox").each(function(){
			    if(this.checked == true)
			    {
			    	$(this).prop("checked",false);
			    }
			  });
		},
		error:function (XMLHttpRequest, textStatus, errorThrown) {
            // 状态码
            console.log(XMLHttpRequest.status);
            // 状态
            console.log(XMLHttpRequest.readyState);
            // 错误信息   
            console.log(textStatus);
        }
	});
};

function clickImg(id){
	var input = "#" + id +"_input";
	var label = "#" + id + "_label";
	var img = "#" + id + "_img";
	$(img).hide();
	$(input).prop("checked",true);
	$(input).show();
	$(label).show();
}
