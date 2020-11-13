<!DOCTYPE html>
<%@page import="java.util.Map.Entry"%>
<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<%@page import="watchDog.config.json.DeviceModelConfig"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" 
  import="watchDog.service.PropertyMgr"
  import="watchDog.service.RequestService"
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<link rel="stylesheet" href="../js/framework/bootstrap/4.2.1/css/bootstrap.min.css">
<meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.5"/>
<script src="../js/framework/jquery/3.3.1/jquery.min.js"></script>
<script src="../js/framework/bootstrap/4.2.1/js/bootstrap.min.js"></script>
<%
	String version = PropertyMgr.getInstance().getProperty(PropertyMgr.SCRIPT_VERSION).getValue();
	request.setAttribute("version", version);
	String userId = RequestService.validation(request, "wx_userid", "wx_userid");
	if(userId == null || userId.length()==0)
	{
		response.sendRedirect("invalid.html");
		return;
	}
	String p = request.getParameter("type");
	if(p == null)
	    p = "";
%>
<html>

<head>
	<title>反馈</title>
</head>

<body>
    <form id="form" action="../suggestion/submit" method="post">
	<input type="hidden" name="userid" value="<%=userId %>" />
	<textarea name="content" id="content" rows="6" cols="25" placeholder="建议和意见"></textarea>
	<br>
	<input type="button" id="submit_btn" value="提交">
	</form> 
</body>
<script>
var param = '<%=p%>';
if(param == "1")
{
    $("#content").val("麻烦把我取消电话报警功能");
}
else if(param == "created")
{
	alert("提交成功，工作人员会在3个工作日内处理");
}
$("#submit_btn").click(function(){
	var content = $("#content").val();
	if(content == "")
	{
		alert("内容不能为空");
		return;
	}
	$("#form").submit();
})
</script>
</html>