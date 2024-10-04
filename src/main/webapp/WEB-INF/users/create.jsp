<%--
  Created by IntelliJ IDEA.
  User: soulaiman
  Date: 10/1/24
  Time: 10:30 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Create Users</title>
</head>
<body>
<h1>Create New User</h1>
<form method="post" action="${pageContext.request.contextPath}/users">
    Username: <input type="text" name="username" required><br>
    First Name: <input type="text" name="firstname" required><br>
    Last Name: <input type="text" name="lastname" required><br>
    Email: <input type="email" name="email" required><br>
    Password: <input type="password" name="password" required><br>
    <button type="submit">Create User</button>
</form>
<a href="${pageContext.request.contextPath}/users">Back to List</a>
</body>
</html>