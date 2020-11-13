<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="watchDog.service.DeviceValueMgr"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="watchDog.service.RequestService" 
    import="watchDog.service.DeviceValueMgr"
    import="java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<link rel="stylesheet" href="js/framework/bootstrap/4.2.1/css/bootstrap.min.css">
<link rel="stylesheet" href="css/alarm.css?${version }" />
<script src="js/framework/jquery/3.3.1/jquery.min.js"></script>
<script src="js/framework/popperjs/1.14.6/popper.min.js"></script>
<script src="js/framework/bootstrap/4.2.1/js/bootstrap.min.js"></script>
<%
    String userId = RequestService.validation(request, "wx_userid", "wx_userid");
    if(userId == null || userId.length()==0)
    {
        response.sendRedirect("invalid.html");
        return;
    } 
    int kidsupervisor = 0;
    String str = request.getParameter("idsite");
    if(str != null && str.length()>0)
        kidsupervisor= Integer.valueOf(str);
    else
    {
        response.sendRedirect("invalid.html");
        return;
    }
    List<Map<String,String>> deviceList =  DeviceValueMgr.getSiteDeviceList(kidsupervisor);
    request.setAttribute("deviceList", deviceList);
%>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>设备列表</title>
</head>

<body>
    <div class="devicelist_div">
        <table class="table table-hover table-bordered table-striped">
            <thead>
            </thead>
            <tbody id="devicelist_tbody">
                <c:forEach var="item" items="${deviceList}">
                    <tr>
                        <td ip="${item.ip}" devcode="${item.devcode }" devmdlcode="${item.devmdlcode}">${item.device}
                     	   <img src="img/browse.svg" class="devicelist_status">
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
    <script type="text/javascript">
    	$(function(){
            $(".devicelist_status").each(function(){
                var td = $(this).parent();
                if(td.attr("devmdlcode") == "true"){
                    $(this).click(function(){
                        window.open("device.jsp?ip=" + td.attr("ip") + "&devCode=" + td.attr("devcode"));
                    });
                }else{
                    $(this).hide();
                }
            });
        })
    </script>
</body>

</html>