$(function(){
	$("#vpnKickBtn").click(function() {
	var pid = $(this).prev().val();
	if(pid.indexOf("ppp") < 0){
		$("#input_manage_info").text("请输入正确的pid");
		$("#input_manage").val("");
		return;
	}
	$.ajax({
		url : "/watchDog/vpn/manage",
		type : "post",
		data : {
			// kick:1,
			type : 1,
			pid : pid
		},
		success : function(data) {
			if (data = "success") {
				$("#input_manage_info").text("成功了！请刷新页面！");
				$("#input_manage").val("");
			}
		},
		error : function(data) {
			$("#input_manage_info").text("失败了！请检查是否已拨上VPN或者联系管理员");
			$("#input_manage").val("");
		}
	})
})
})