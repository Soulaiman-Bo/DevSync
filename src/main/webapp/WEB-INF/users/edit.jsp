<%--
  Created by IntelliJ IDEA.
  User: soulaiman
  Date: 10/1/24
  Time: 10:31 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Edit User</title>
</head>
<body>
<h1>Edit User</h1>
<form method="post" action="${pageContext.request.contextPath}/users/${user.id}">
    <input type="hidden" name="_method" value="PUT"/>
    Username: <input type="text" name="username" value="${user.username}" required><br>
    First Name: <input type="text" name="firstname" value="${user.firstname}" required><br>
    Last Name: <input type="text" name="lastname" value="${user.lastname}" required><br>
    Email: <input type="email" name="email" value="${user.email}" required><br>
    Password: <input type="password" name="password" value="${user.password}" required><br>
    <button type="submit">Update User</button>
</form>
<a href="${pageContext.request.contextPath}/users">Back to List</a>
</body>
</html>