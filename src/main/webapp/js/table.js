//(1)初始化
$(function() {
	//$('#table').editable();
	var oTable = new TableInit();
	oTable.Init();
	$("#registerationDateInput").datepicker({
		language:"zh-CN",
	});
	$('#registerationDateInput').datetimepicker('setDate', new Date());
	$("#btn_submit").click(function(){
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
				}
			},
			error : function() {
				alert('编辑失败');
			},
		})
	})
	/*var oButtonInit = new ButtonInit();
	oButtonInit.Init();*/
});
var TableInit = function() {
	var oTableInit = new Object();
	oTableInit.Init = function() {
		$('#table').bootstrapTable({
			url : '/watchDog/rinfo/getData', // 请求后台的URL（*）
			editable: true,
			method : 'get', // 请求方式（*）
			toolbar : '#toolbar', // 工具按钮用哪个容器
			striped : true, // 是否显示行间隔色
			cache : false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
			pagination : true, // 是否显示分页（*）
			sortable : true, // 是否启用排序
			sortOrder : "asc", // 排序方式
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
				field : 'vpnAddress',
				title : 'VPN',
				sortable:true,
				editable : {
					type : 'text',
					title : 'VPN',
					emptytext:'',
					mode: "inline",  
					validate : function(v) {
						if (!v)
							return '不能为空';
					}
				}
			},
			{
				field : 'registerationDate',
				title : '注册日期',
				sortable:true,
				formatter(value,row,index){
					return value == undefined ? null : new Date(value).format("yyyy-MM-dd");
				},
				editable : {
					type : 'combodate',
					//format:'YYYY-MM-DD',
					//viewformat:'DD/MM/YY',
					mode: "inline",  
					emptytext:"暂无",
					validate : function(v) {
						if (!v)
							return '不能为空';
					}
				}
			}, 
			{
				field : 'purchaser',
				title : '采购方',
				sortable:true,
				editable : {
					type : 'text',
					title : '采购方',
					mode: "inline",  
					emptytext:'暂无',
					validate : function(v) {
						if (!v)
							return '不能为空';
					}
				}
			}, 
			{
				field : 'project',
				title : '项目名',
				sortable:true,
				editable : {
					type : 'text',
					title : '项目名',
					mode: "inline",  
					emptytext:'暂无',
					validate : function(v) {
						if (!v)
							return '不能为空';
					}
				}
			}, 
			{
				field : 'servicePeriod',
				title : '服务年限',
				sortable:true,
				editable : {
					type : 'number',
					title : '服务年限',
					mode: "inline",  
					emptytext:'暂无',
					validate: function (v) {
						if (isNaN(v)) return '服务年限必须是数字';
						var age = parseInt(v);
						if (age <= 0) return '服务年限必须是正整数';
					}
				}
			}, 
			{
				field : 'productCode',
				title : '产品型号',
				sortable:true,
				editable : {
					type : 'text',
					title : '产品型号',
					mode: "inline",  
					emptytext:'暂无',
					validate : function(v) {
						if (!v)
							return '不能为空';
					}
				}
			}, 
			{
				field : 'productMac',
				title : '产品Mac',
				sortable:true,
				editable : {
					type : 'text',
					title : '产品Mac',
					mode: "inline",  
					emptytext:'暂无',
					validate : function(v) {
						if (!v)
							return '不能为空';
					}
				}
			}, 
			{
				field : 'routerMac',
				title : '路由器Mac',
				sortable:true,
				editable : {
					type : 'text',
					title : '路由器Mac',
					mode: "inline",  
					emptytext:'暂无',
					validate : function(v) {
						if (!v)
							return '不能为空';
					}
				}
			}, 
			{
				field : 'routerManufacturer',
				title : '路由器厂商',
				sortable:true,
				editable : {
					mode: "inline",  
					type: "select",
					emptytext:'暂无',
					source:[{value:"树米",text:"树米"}
					,{value:"有人",text:"有人"}],
					validate : function(v) {
						if (!v)
							return '不能为空';
					}
				}
			}, 
			{
				field : 'originalVersion',
				title : '出厂版本',
				sortable:true,
				editable : {
					type : 'text',
					title : '出厂设置',
					mode: "inline",  
					emptytext:'暂无',
					validate : function(v) {
						if (!v)
							return '不能为空';
					}
				}
			}, 
			{
				field : 'isUpdated',
				title : '是否升级',
				editable : {
					mode: "inline",  
					type: "select",
					/*formatter(value,row, index){
						return value ? "已升级" : "未升级";
					},*/
					source:[{value:true,text:"已升级"}
					,{value:false,text:"未升级"}],
					validate : function(v) {
						if (!v)
							return '不能为空';
					}
				}
			},
			{
				field : 'isConnected',
				title : '是否4G连接',
				editable : {
					mode: "inline",  
					type:"select",
					/*formatter(value,row,index){
						return value ? "已连接" : "未连接";
					},*/
					source:[{value:true,text:"已连接"}
					,{value:false,text:"未连接"}],
					validate : function(v) {
						if (!v)
							return '不能为空';
					}
				}
			},
			{
				field : 'simCard.id',
				title : 'sim卡号',
				sortable:true,
				 editable: {
					 type: 'select',
					 mode: "inline", 
					 emptytext:"暂无",
					 source: function () {
						 var result = [];
						 $.ajax({
							 url: '/watchDog/simcard/get',
							 async: false,
							 type: "get",
							 success: function (data, status) {
								 var dataJson = JSON.parse(data);
								 for(var p in dataJson){
									var ele = dataJson[p];
									result.push({ value: ele.id, text: ele.cardNumber + getSIMCardStatus(ele.simCardStatus)});
								 }
							 }
						 });
						 return result;
					 },
					 formatter(value,row,index){
							return value;
						},
				 }
			},
			{
				field : 'comment',
				title : '备注',
				editable : {
					type : 'text',
					title : '备注',
					mode: "inline",  
					emptytext:'暂无',
					validate : function(v) {
						if (!v)
							return '不能为空';
					}
				}
			},
			
			],
			// 验证数字
			// editable: {
			// type: 'text',
			// title: '年龄',
			// validate: function (v) {
			// if (isNaN(v)) return '年龄必须是数字';
			// var age = parseInt(v);
			// if (age <= 0) return '年龄必须是正整数';
			// }
			// }
			// 时间框
			// editable: {
			// type: 'datetime',
			// title: '时间'
			// }
			// 选择框
			// editable: {
			// type: 'select',
			// title: '部门',
			// source: [{ value: "1", text: "研发部" }, { value: "2", text:
			// "销售部" }, { value: "3", text: "行政部" }]
			// }
			// 复选框
			// editable: {
			// type: "checklist",
			// separator:",",
			// source: [{ value: 'bsb', text: '篮球' },
			// { value: 'ftb', text: '足球' },
			// { value: 'wsm', text: '游泳' }],
			// }
			// select2
			// 暂未使用到

			// 取后台数据
			// editable: {
			// type: 'select',
			// title: '部门',
			// source: function () {
			// var result = [];
			// $.ajax({
			// url: '/Editable/GetDepartments',
			// async: false,
			// type: "get",
			// data: {},
			// success: function (data, status) {
			// $.each(data, function (key, value) {
			// result.push({ value: value.ID, text: value.Name });
			// });
			// }
			// });
			// return result;
			// }
			// }
			
			// 保存的使用
			onEditableSave:function(field, row, oldValue, $el) {
				// 可进行异步操作
				$.ajax({
					type : "post",
					url : "/watchDog/rinfo/edit",
					data : {
						"row": JSON.stringify(row),
						"simCardId":field == "simCard.id" ? row[field] : null,
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

function getSIMCardStatus(status){
	if(status == "UNUSED")
		return "未使用";
	if(status == "ENABLED")
		return "使用中";
	if(status == "DISABLED")
		return "停用";
	if(status == "DELETED")
		return "销户";
	return "未知";
}
Date.prototype.format = function(fmt) { 
    var o = { 
       "M+" : this.getMonth()+1,                 //月份 
       "d+" : this.getDate(),                    //日 
       "h+" : this.getHours(),                   //小时 
       "m+" : this.getMinutes(),                 //分 
       "s+" : this.getSeconds(),                 //秒 
       "q+" : Math.floor((this.getMonth()+3)/3), //季度 
       "S"  : this.getMilliseconds()             //毫秒 
   }; 
   if(/(y+)/.test(fmt)) {
           fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
   }
    for(var k in o) {
       if(new RegExp("("+ k +")").test(fmt)){
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
        }
    }
   return fmt; 
} 
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