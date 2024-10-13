<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Error</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f2f2f2;
            color: #333;
            text-align: center;
            padding: 50px;
        }
        h1 {
            color: #d9534f;
        }
    </style>
</head>
<body>
<h1>Oops!</h1>
<p>Something went wrong.</p>
<p>Error Code: <%= request.getAttribute("javax.servlet.error.status_code") != null ? request.getAttribute("javax.servlet.error.status_code") : "Unknown" %></p>
</body>
</html>
