<!DOCTYPE html>
<%@page import="watchDog.util.ObjectUtils"
    import="javax.servlet.http.HttpSession"
    import="java.util.ArrayList"
    import="watchDog.bean.PageVPNInfo"
%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script src="../js/framework/jquery/3.3.1/jquery.min.js"></script>
<script src="../js/vpn.js"></script>
<%
    boolean login = false;
    if("success".equals(session.getAttribute("LoginStatus")))
        login = true;
    ArrayList<PageVPNInfo> lines =  (ArrayList<PageVPNInfo>)request.getAttribute("lines");
    if(lines == null || lines.size() == 0){
        out.print("未能抓取VPN信息，请检查网络信息或者联系管理员。");
    }
%>
<html>
<head>
<meta>
<title>VPN管理页面</title>
</head>
<body>
<%  if(login){ %>
    <input id="input_manage" type="text" placeholder="输入你想要踢掉的ip,例如：ppp209" style="width: 15%;">
    <button id="vpnKickBtn">确定</button>
<% } %>
    <div id="input_manage_info"></div>
    <table>
        <tr>
            <tr>
                <td>账户<td>
                <td>PID<td>
                <td>公网IP<td>
                <td>上线时间<td>
            </tr>
        </tr>
        <c:forEach items="${lines}" var="line">
            <tr>
                <td>${line.ip}<td>
                <td>${line.pid}<td>
                <td>${line.publicIP}<td>
                <td>${line.connectTime}<td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>