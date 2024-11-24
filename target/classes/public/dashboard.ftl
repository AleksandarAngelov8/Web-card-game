<html>
<head>
    <link rel="stylesheet" type="text/css" href="/styles.css">
    <script>
        let username = "${name}";

        // Establish WebSocket connection
        const ws = new WebSocket("ws://localhost:3000/ws");

        ws.onopen = () => {
            console.log(`${name} is connected.`);
        };

        // Handle incoming messages
        ws.onmessage = (event) => {
            const messageData = JSON.parse(event.data);

            // Dynamically update user info
            if (messageData.type === "update_user") {
                var userDiv = document.getElementById(messageData.userKey);
                if (!userDiv){
                    userDiv = document.createElement("div");
                    userDiv.id = messageData.userKey;
                    document.body.appendChild(userDiv);
                }
                userDiv.textContent = messageData.message;
            }
        };

        // Raise hand functionality
        function raiseHand(event) {
            event.preventDefault();

            // Send WebSocket message with the logged-in username
            const message = JSON.stringify({
                type: "raise_hand",
                username: username
            });
            ws.send(message);
        }
    </script>
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
