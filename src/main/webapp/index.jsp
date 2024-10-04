<!--<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>-->
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hello Page</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
        }
        .button {
            display: inline-block;
            padding: 10px 20px;
            margin: 10px;
            background-color: #4CAF50;
            color: white;
            text-decoration: none;
            border-radius: 4px;
        }
    </style>
</head>
<body>
        <h1>User Management System</h1>
        <div>
            <a href="${pageContext.request.contextPath}/users/new" class="button">Create New User</a>
            <a href="${pageContext.request.contextPath}/users" class="button">View All Users</a>
        </div>
</body>
</html>
