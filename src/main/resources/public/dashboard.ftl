<!-- Erstellt von Angelov -->
<html>
<head>
    <link rel="stylesheet" type="text/css" href="/styles.css">
    <script src="ajax_functionality.js"></script>
    <title>Dashboard</title>
</head>
<body>
<h1>Welcome to the Dashboard!</h1>
<form action="/raise_hand" method="post">
    <input type="text" name="name" value="John"><br>
    <input type="submit" value="Login">
</form>

</button>
<div id="dashboard">
    <!-- Dashboard content will be updated here -->
</div>
<table>
<#list users as name,user>
    <td>${name}</td>
</#list>
</table>
<form action="/logout" method="post">
    <button>Logout</button>
</form>
</body>
</html>