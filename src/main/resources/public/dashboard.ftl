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

<#list users?keys as key>
    <div id="${key}">
        ${users[key].storedInfo}
    </div>
</#list>
<form action="/logout" method="post">
    <button>Logout</button>
</form>
</body>
</html>