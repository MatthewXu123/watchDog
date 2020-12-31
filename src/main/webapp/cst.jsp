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
                    <td>1. bosss:设置->系统页->系统->RemotePRO连接，“用户认证”要勾选<a href="../img/cst/authentication.png" download="png">如何操作</a></td>
                </tr>
                <tr>
                    <td>2. 是否有“报警测试设备”<a href="../img/cst/testdevice.png" download="png">啥样子?</a><br>
                              如果没有通知iot_support_cn@carel.com进行后续操作<br>
                              如果有“报警测试设备”，但是是灰色的，请在注册页面注册“Logical Device/Logical Variables”，如果license不够，联系iot_support_cn@carel.com进行后续操作
                    </td>
                </tr>
                <tr>
                    <td>3. 用内网网线上网的(奥乐齐和盒马)，请点击下方“公网IP查询”按钮，确认公网IP城市是否是所在地</td>
                </tr>
                <tr>
                    <td>4. 永辉BOSS监控机如果插2跟网线要做路由表，做完拍照发到群里<a href="../img/cst/001.docx" download="doc">手册</a></td>
                </tr>
                <tr>
                    <td>5. 站点备份是否上传到阿里云服务器</td>
                </tr>
				<tr>
                    <td><input type="text" placeholder="请输入vpn内网IP" id="txt_ip"><button id="ip_btn">公网IP查询</button></td>
                </tr>
                <tr>
                    <td id="site_name"></td>
                </tr>
                <tr>
                    <td id="public_ip"></td>
                </tr>
                <tr>
                    <td id="error_info"></td>
                </tr>
				<tr>
                    <td><input type="submit" value="微信报警开始确认" id="submit_btn"></td>
                </tr>
		</tbody>
	</table>
</body>
<script type="text/javascript">
$("#ip_btn").click(function(){
	if($("#txt_ip").val() == "")
    {
        alert("VPN IP地址为空");
        return;
    }
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
                 $("#error_info").html(data.errorInfo);
             }
         }
     });
})

$("#submit_btn").click(function(){
	if($("#error_info").html() != "")
	{
		alert("有错误，无法提交");
		return;
	}
	if($("#site_name").html() == "")
	{
	    alert("请先用IP查询站点信息");
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