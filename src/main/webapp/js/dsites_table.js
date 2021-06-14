//(1)初始化
$(function() {
	//$('#table').editable();
	var oTable = new TableInit();
	oTable.Init();
	$("#registerationDateInput").datepicker({
		language:"zh-CN",
	});
	$('#registerationDateInput').datepicker('setDate', new Date());
	$("#btn_submit").click(function(){
		$(".form-notblank").each(function(){
			if($(this).val() == null || $(this).val().length == 0){
				$(this).css("border", "1px red solid");
				return;
			}else{
				$(this).css("border", "1px solid #ccc");
			}
		});
		
		if($("#isUpdatedInput").prop("checked"))
			$("#isUpdatedInput").val(true);
		else
			$("#isUpdatedInput").val(false);
		
		if($("#isConnectedInput").prop("checked"))
			$("#isConnectedInput").val(true);
		else
			$("#isConnectedInput").val(false);
		
		$.ajax({
			method:"post",
			url:"/watchDog/rinfo/save",
			data:{
				"rinfo":JSON.stringify($('#save_form').serializeObject()),
			},
	        //contentType:"application/json",  //缺失会出现URL编码，无法转成json对象
	        dataType : 'JSON',
			success : function(data, status) {
				if (status == "success") {
					alert('提交数据成功');
					$("#addModal").modal('hide');
				}
			},
			error : function() {
				alert('编辑失败');
			},
		})
	});
	$("#btn_save_simcard").click(function(){
		var count = $("#input_cardNumberCount").val();
		if (isNaN(count) || count==""){
			alert("数量必须是数字");
			return;
		}
		
		var countInt = parseInt(count);
		if (countInt <= 0){
			alert("数量必须是正整数");
			return;
		}
		$.ajax({
			method:"POST",
			url:"/watchDog/simcard/create",
			data:{
				"simcard":JSON.stringify($('#form_save_simcard').serializeObject()),
			},
			//contentType:'application/json',
			dataType:'JSON',
			success:function(data,status){
				if (status == "success") {
					var res = data.data;
					var successfulCards = res.successful;
					var registeredCardNumber = "\n成功注册的卡号如下:\n";
					for(var key in successfulCards){
						var simcard = successfulCards[key];
						registeredCardNumber += simcard.cardNumber + ","
					}
					
					var failedCards = res.failed;
					var unregisteredCardNumber = "\n未成功注册的卡号如下(原因是可能已重复)：\n";
					for(var key in failedCards){
						var simcard = failedCards[key];
						unregisteredCardNumber += simcard.cardNumber + ","
					}
					
					alert(registeredCardNumber + unregisteredCardNumber);
				}
			},
			error:function(){
				alert('创建失败');
			}
		})
	})
	/*var oButtonInit = new ButtonInit();
	oButtonInit.Init();*/
});
var TableInit = function() {
	var oTableInit = new Object();
	oTableInit.Init = function() {
		$('#table').bootstrapTable({
			url : '/watchDog/dsites/getData', // 请求后台的URL（*）
			editable: true,
			method : 'get', // 请求方式（*）
			toolbar : '#toolbar', // 工具按钮用哪个容器
			striped : true, // 是否显示行间隔色
			cache : false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
			pagination : true, // 是否显示分页（*）
			//sortable : true, // 是否启用排序
			//sortOrder : "asc", // 排序方式
			//queryParams : oTableInit.queryParams,// 传递参数（*）
			sidePagination : "client", // 分页方式：client客户端分页，server服务端分页（*）
			pageNumber : 1, // 初始化加载第一页，默认第一页
			pageSize : 10, // 每页的记录行数（*）
			pageList : [ 10, 25, 50, 100], // 可供选择的每页的行数（*）
			search : true,// 是否显示表格搜索，此搜索是客户端搜索,也可以是服务端检索
			//strictSearch : true,
			showColumns : true, // 是否显示所有的列
			showRefresh : true, // 是否显示刷新按钮
			//minimumCountColumns : 1, // 最少允许的列数
			clickToSelect : true, // 是否启用点击选中行
			//height : 700, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
			uniqueId : "id", // 每一行的唯一标识，一般为主键列
			showToggle : true, // 是否显示详细视图和列表视图的切换按钮
			cardView : false, // 是否显示详细视图
			detailView : false, // 是否显示父子表
			showExport: true,                     //是否显示导出
            exportDataType: "basic",              //basic', 'all', 'selected'.
			columns : [ 
			/*{
				checkbox : true
			}, */
			{
				field : 'name',
				title : '店名',
				sortable:true,
				editable : {
					type : 'text',
					title : '店名',
					emptytext:'暂无',
					mode: "inline",  
					validate : function(v) {
						if (!v)
							return '不能为空';
					}
				}
			},
			{
				field : 'ip',
				title : 'ip',
				editable : {
					type : 'text',
					title : 'ip',
					mode: "inline",  
					emptytext:'暂无',
					validate : function(v) {
						if (!v)
							return '不能为空';
					}
				}
			}, 
			{
				field : 'soldierDeptId',
				title : '士兵部门id',
				sortable:true,
				editable : {
					type : 'text',
					title : '士兵部门id',
					mode: "inline",  
					emptytext:'暂无',
					validate : function(v) {
						if (!v)
							return '不能为空';
					}
				}
			}, 
			{
				field : 'officerDeptId',
				title : '军官部门id',
				sortable:true,
				editable : {
					type : 'text',
					title : '军官部门id',
					mode: "inline",  
					emptytext:'暂无',
					validate: function (v) {
						if (!v)
							return '不能为空';
					}
				}
			}, 
			{
				field : 'agentId',
				title : 'agentId',
				sortable:true,
				editable : {
					type : 'text',
					title : 'agentId',
					mode: "inline",  
					emptytext:'暂无',
					validate : function(v) {
						if (!v)
							return '不能为空';
					}
				}
			}
			
			],
			onEditableSave:function(field, row, oldValue, $el) {
				// 可进行异步操作
				$.ajax({
					type : "post",
					url : "/watchDog/dsites/edit",
					data : {
						"row": JSON.stringify(row),
					},
					dataType : 'JSON',
					success : function(data, status) {
						if (status == "success") {
							alert('提交数据成功');
						}
					},
					error : function() {
						alert('编辑失败');
					},
					complete : function() {

					}

				});
			}

		});
	};

	// 得到查询的参数
	/*oTableInit.queryParams = function(params) {
		var temp = { // 这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
			limit : params.limit, // 页面大小
			offset : params.offset, // 页码
		};
		return temp;
	};*/
	return oTableInit;
};

// (2)关键字检索
/*$("#btn_query").click(function() {
	// 点击查询是 使用刷新 处理刷新参数
	var opt = {
		url : "/index/GetDepartment",
		silent : true,
		query : {
			text1 : $("#txt_search_departmentname").val(), // 条件1
			text2 : $("#txt_search_statu").val()
		// 条件2 ....
		}
	};
	$('#tb_departments').bootstrapTable('refresh', opt);

});

// (3)修改一、获取编号进入下一页
$("#btn_edit").click(function() {
	var i = 0;
	var id;
	$("input[name='btSelectItem']:checked").each(function() {
		i++;
		id = $(this).parents("tr").attr("data-uniqueid");
	})
	if (i > 1) {
		alert("编辑只支持单一操作")
	} else {
		if (i != 0) {
			alert("获取选中的id为" + id);
			window.location.href = "/aftersales/sales/manage";
		} else {
			alert("请选中要编辑的数据");
		}

	}

})

// (4)删除及批量删除

$("#btn_delete").click(function() {
	if (confirm("确认要删除吗？")) {
		var idlist = "";
		$("input[name='btSelectItem']:checked").each(function() {
			idlist += $(this).parents("tr").attr("data-uniqueid") + ",";
		})
		alert("删除的列表为" + idlist);

	}
});*/
var ButtonInit = function () {
    var oInit = new Object();
    var postdata = {};

    oInit.Init = function () {
        /*$("#btn_add").click(function () {
            $("#addModal").text("新增");
            $("#myModal").find(".form-control").val("");
            $('#myModal').modal();

            postdata.DEPARTMENT_ID = "";
        	*//**
        	 * 新增一行数据
        	 *//*
        	var count = $('#table').bootstrapTable('getData').length;
    	    // newFlag == 1的数据为新规的数据
    	    $('#table').bootstrapTable('insertRow',{index:count,row:{}});
        });*/

        //$("#btn_edit").click(function () {
        //    var arrselections = $("#tb_departments").bootstrapTable('getSelections');
        //    if (arrselections.length > 1) {
        //        toastr.warning('只能选择一行进行编辑');

        //        return;
        //    }
        //    if (arrselections.length <= 0) {
        //        toastr.warning('请选择有效数据');

        //        return;
        //    }
        //    $("#myModalLabel").text("编辑");
        //    $("#txt_departmentname").val(arrselections[0].DEPARTMENT_NAME);
        //    $("#txt_parentdepartment").val(arrselections[0].PARENT_ID);
        //    $("#txt_departmentlevel").val(arrselections[0].DEPARTMENT_LEVEL);
        //    $("#txt_statu").val(arrselections[0].STATUS);

        //    postdata.DEPARTMENT_ID = arrselections[0].DEPARTMENT_ID;
        //    $('#myModal').modal();
        //});

         $("#btn_delete").click(function () {
             var arrselections = $("#table").bootstrapTable('getSelections');
             if (arrselections.length <= 0) {
                 toastr.warning('请选择有效数据');
                 return;
             }

             Ewin.confirm({ message: "确认要删除选择的数据吗？" }).on(function (e) {
                 if (!e) {
                     return;
                 }
                 $.ajax({
                     type: "post",
                     url: "/Home/Delete",
                     data: { "": JSON.stringify(arrselections) },
                     success: function (data, status) {
                         if (status == "success") {
                             toastr.success('提交数据成功');
                             $("#tb_departments").bootstrapTable('refresh');
                         }
                     },
                     error: function () {
                         toastr.error('Error');
                     },
                     complete: function () {

                     }

                 });
             });
         });

        //$("#btn_submit").click(function () {
        //    postdata.DEPARTMENT_NAME = $("#txt_departmentname").val();
        //    postdata.PARENT_ID = $("#txt_parentdepartment").val();
        //    postdata.DEPARTMENT_LEVEL = $("#txt_departmentlevel").val();
        //    postdata.STATUS = $("#txt_statu").val();
        //    $.ajax({
        //        type: "post",
        //        url: "/Home/GetEdit",
        //        data: { "": JSON.stringify(postdata) },
        //        success: function (data, status) {
        //            if (status == "success") {
        //                toastr.success('提交数据成功');
        //                $("#tb_departments").bootstrapTable('refresh');
        //            }
        //        },
        //        error: function () {
        //            toastr.error('Error');
        //        },
        //        complete: function () {

        //        }

        //    });
        //});

        //$("#btn_query").click(function () {
        //    $("#tb_departments").bootstrapTable('refresh');
        //});
    };

    return oInit;
};

$.fn.serializeObject = function() {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name]) {
            if (!o[this.name].push) {
                o[this.name] = [ o[this.name] ];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};