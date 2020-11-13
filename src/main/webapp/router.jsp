<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script src="../js/framework/jquery/3.3.1/jquery.min.js"></script>
<script src="../js/router.js"></script>
<link rel="stylesheet" href="../css/router.css">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>路由器管理</title>
</head>
<body>
	<form action="/watchDog/router/manage">
		<textarea name="routerinfo" rows="30" cols="50" placeholder="格式：mac地址&用户名&密码;" >${routerinfo}</textarea>
		<button type="submit">提交</button>
	</form>
</body>
</html>