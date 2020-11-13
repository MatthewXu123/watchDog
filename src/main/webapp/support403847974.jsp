<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"
    import="com.carel.supervisor.license.License"
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.8"/>
<title>超级密码计算</title>
</head>
<%
    String mac = request.getParameter("mac");
    String pwd = "";
    try{
	   if(mac != null && mac.length()>0)
	   {
	       mac = mac.toUpperCase().replace("-", "");
	       char[] r = License.generateKEY("A1CAREL33333".toCharArray(), mac.toCharArray());
	       pwd = new String(r);
	   }
	   else
	       mac = "";
    }catch(Exception ex){}
%>
<body>
    <form action="support403847974.jsp">
        <input type="text" name="mac" placeholder="mac地址" value="<%=mac%>"></textarea>
        <br>
        <button type="submit">提交</button>
        <br>
        <%=pwd %>
    </form>
</body>
</html>