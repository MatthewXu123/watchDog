<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" 
    import="java.util.*" 
    %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script src="../js/framework/jquery/3.3.1/jquery.min.js"></script>
<%
String ok = (String)request.getAttribute("ok");
if(ok == null)
    return;
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style>
body{font-size:2rem}
	th,td{font-size:2rem;}
	th{text-align:center;}
	input{font-size:2rem;}
	button{font-size:2rem;}
</style>
<title>调试结束</title>
</head>
<body>
	<table class="table table-hover table-bordered table-striped" id="sitelist_table">
		<tbody>
		        <tr>
                    <td>1. boss:设置，输入输出配置，远程访问服务，测试</td>
                </tr>
                <tr>
                    <td>2. boss:日程，报警和事件管理，包含high和very hight2个规则</td>
                </tr>
                <tr>
                    <td>3. RemotePRO:该站点能成功同步</td>
                </tr>
                <tr>
                    <td>4. 用内网网线上网的(奥乐齐和盒马)，确认公网IP城市是否是所在地</td>
                </tr>
                <tr>
                    <td>5. 永辉监控机如果插2跟网线要做路由表</td>
                </tr>
                <tr>
                    <td>6. 站点备份是否上传到阿里云服务器</td>
                </tr>
				<tr>
					<td><a href="download/commissioning_step.pdf" target="_blank">点击下载调试手册</a></td>
				</tr>
				<tr>
                    <td><input type="text" placeholder="请输入vpn内网IP" id="txt_ip"><button id="ip_btn">查询</button></td>
                </tr>
                <tr>
                    <td id="site_name"></td>
                </tr>
                <tr>
                    <td id="public_ip"></td>
                </tr>
                <tr>
                    <td id="other_info"></td>
                </tr>
				<tr>
                    <td><input type="submit" value="微信报警开始确认" id="submit_btn"></td>
                </tr>
		</tbody>
	</table>
</body>
<script type="text/javascript">
$("#ip_btn").click(function(){
	$.ajax({
        url : '../cst/query',
        type : 'post',
        data : {
            ip: $("#txt_ip").val()
            },
         dataType : 'json', 
         success: function(data){
             if(data != null)
             {
                 console.info(data);
                 $("#site_name").html(data.siteName);
                 $("#public_ip").html(data.info);
                 $("#error_info").html(data.otherInfo);
             }
         }
     });
})

$("#submit_btn").click(function(){
	if($("#site_name").html() == "")
	{
		alert("请先用IP查询站点信息");
	}
	if($("#other_info").html() != "")
	{
		alert("有错误，无法提交");
		return;
	}
	$.ajax({
        url : '../cst/submit',
        type : 'post',
        data : {
            ip: $("#txt_ip").val()
            },
         dataType : 'json', 
         success: function(data){
        	 $("#txt_ip").val("");
        	 $("#error_info").html("确认成功");
         }
     });
})
</script>
</html>