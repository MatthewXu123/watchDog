$(function() {
	$("#btn_vpn").click(function() {
		window.open("/watchDog/vpn/view");
	});
	$("#btn_router").click(function() {
		window.open("/watchDog/router/view");
	});
	$("#btn_rinfo").click(function() {
		window.open("/watchDog/rinfo/view");
	});
	$(".input-readonly").each(function() {
		$(this).attr("readonly", "readonly");
		$(this).css("border", "none");
	});
	$(".input-networkcheck").click(function() {
		if ($(this).prop("checked")) {
			$(this).prev().val("true");
		} else {
			$(this).prev().val("false");
		}
	});
	$(".tr-remark").each(function() {
		$(this).mouseover(function() {
			$(this).css("background-color", "grey");
		});
		$(this).mouseout(function() {
			$(this).css("background-color", "white");
		});
	})

})