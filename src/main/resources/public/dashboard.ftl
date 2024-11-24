<html>
<head>
    <link rel="stylesheet" type="text/css" href="/styles.css">
    <script src="dashboard_functionality.js"></script>
    <title>Dashboard</title>
</head>
<body>
<h1>Welcome to the Dashboard, ${name}!</h1>

<!-- Button to raise hand -->
<form onsubmit="raiseHand(event)">
    <button type="submit">Raise Hand</button>
</form>

<!-- User List -->
<#list users?keys as key>
    <div id="${key}">
        ${users[key].storedInfo}
    </div>
</#list>

<!-- Logout Form -->
<form action="/logout" method="post">
    <button>Logout</button>
</form>
</body>
</html>
