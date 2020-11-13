<%@page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Login</title>
</head>
<body>
<P>用户登录</P>
<form action="/watchDog/login" method="post">
	<table width="400px">
		<tr>
			<td width="100px">用户名:</td>
			<td ><input type="text" name="userName" id="userName" ></td>
		</tr>
		<tr>
			<td width="100px">密码:</td>
			<td ><input type="password" name="password" id="password" ></td>
		</tr>
		<tr>
			<td width="100px"></td>
			<td ><input type="submit" value="登录"></td>
		</tr>
	</table>
</form>
</body>
</html>