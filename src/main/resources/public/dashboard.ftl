<html>
<head>
    <link rel="stylesheet" type="text/css" href="/styles.css">

    <title>Dashboard</title>
</head>
<body>
<h1>Welcome to the Dashboard, ${name}!</h1>

<!-- Button to raise hand -->
<form onsubmit="raiseHand(event)">
    <button type="submit">Raise Hand</button>
</form>

<div id="container">
    <div id="user" class="user_div"></div>
    <div id="userTopRight" class="user_div"></div>
    <div id="userTopLeft" class="user_div"></div>
<!-- User List
-->
</div>
<script>
    let username = "${name}";
    let users = [
        <#list users?keys as key>
        "${key}",
        </#list>
    ];
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
                document.getElementById("container").body.appendChild(userDiv);
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
    function setPlayerView() {
        const div_user = document.getElementById("user");
        const div_userTopRight = document.getElementById("userTopRight");
        const div_userTopLeft = document.getElementById("userTopLeft");

        div_user.id = username;
        div_userTopRight.id = users[0];
        div_userTopLeft.id = users[1];

        div_user.style.bottom = "0";
        div_user.style.left = "50%";
        div_user.style.transform = "translateX(-50%)";

        div_userTopLeft.style.top = "0";
        div_userTopLeft.style.left = "0";

        div_userTopRight.style.top = "0";
        div_userTopRight.style.right = "0";
    }

    document.addEventListener("DOMContentLoaded", () => {
        setPlayerView();
    });
</script>
</body>
</html>
