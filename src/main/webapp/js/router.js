$(function(){
	$("#btn_add_line").click(function(){
		addLine();
	});
	$("#btn_update_line").click(function(){
		updateLine();
	});
})

function addLine(){
	var html = $("#tbody_router").html();
	var addedLine =  "<tr>"
		+ "<td><input type='text' name='macAddress'> </td>"
		+ "<td><input type='text' name='username'></td>"
		+ "<td><input type='text' name='password'></td>"
		+ "<td>"
		+ "<button>删除</button>"
		+ "</td>"
	    + "</tr>";
	$("#tbody_router").children().last().after(addedLine);
}

function updateLine(){
	var separator = "&";
	var ele_separator = ";";
	var routerInfoList = "";
	$(".val-macaddress").each(function(){
		var macaddress = $(this).val();
		var ele = $(this).siblings('.val-username');
		var username = $(this).siblings('.val-username').val();
		var password = $(this).siblings('.val-password').val();
		routerInfoList += macaddress + separator + username + separator + password + ele_separator;
	})
}