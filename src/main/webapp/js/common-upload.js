$(function(){
	
	$(".btn-submit").click(function() {
		if ($('input[id=uploadFile]').val() == ""){
			return;
		}
		var formData = new FormData();
		formData.append("file", $('#uploadFile')[0].files[0]);
		$("#span_upload").text("文件上传中");
		$.ajax({
			url : $(this).parent().attr("action"),
			type : "POST",
			data : formData,
			contentType : false,
			processData : false,
			dataType : 'text',
			success : function(data, status) {
				if(status == "success"){
					$("#span_upload").text("上传成功，请刷新页面。");
					$('#fileCover').val("");
				}else{
					$("#span_upload").text("上传失败，请联系管理员。");
					$('#fileCover').val("");
				}
			},
			error:function(data, status){
				
			}
		})
	});
})