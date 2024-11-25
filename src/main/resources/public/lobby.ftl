<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lobby Waiting Room</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            display: flex;
            flex-direction: column;
            align-items: center;
            background-color: #f4f4f9;
        }
        #lobby {
            max-width: 600px;
            margin-top: 20px;
            background: #fff;
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            width: 90%;
        }
        #player-list {
            list-style: none;
            padding: 0;
        }
        #player-list li {
            padding: 10px;
            border-bottom: 1px solid #ddd;
        }
        #player-list li:last-child {
            border-bottom: none;
        }
        #chat-container {
            margin-top: 20px;
        }
        #chat {
            height: 150px;
            border: 1px solid #ddd;
            padding: 10px;
            overflow-y: scroll;
            border-radius: 5px;
            background: #f9f9f9;
        }
        #chat p {
            margin: 5px 0;
        }
        #chat-form {
            display: flex;
            margin-top: 10px;
        }
        #chat-form input {
            flex: 1;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px 0 0 5px;
        }
        #chat-form button {
            padding: 10px;
            border: none;
            background-color: #007bff;
            color: white;
            cursor: pointer;
            border-radius: 0 5px 5px 0;
        }
        #chat-form button:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>

<div id="lobby">
    <h2>Lobby Waiting Room</h2>
    <p>Waiting for players to join...</p>
    <ul id="player-list">
    </ul>
    <div id="chat-container">
        <h3>Chat</h3>
        <div id="chat"></div>
        <form id="chat-form">
            <input type="text" id="chat-input" placeholder="Type your message..." required>
            <button type="submit">Send</button>
        </form>
    </div>
</div>

<script>
    const username = "${name}";
    const ws = new WebSocket("ws://192.168.0.45:3000/ws");

    ws.onopen = () => {
        const message = JSON.stringify({type: "join", username: username});
        ws.send(message);
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

        else if (messageData.type === "join"){
            const user = messageData.user;
            addChatMessage(user +' is connected.');
        }

        else if (messageData.type === "chatMessage"){
            const user = messageData.user;
            const you = (username === user)?" (you)":"";
            const message = messageData.text;
            addChatMessage(message, user + you);
        }
    };

    const playerList = document.getElementById('player-list');
    const chat = document.getElementById('chat');
    const chatForm = document.getElementById('chat-form');
    const chatInput = document.getElementById('chat-input');

    const players = ['Player 1', 'Player 2']; // Example players
    const maxPlayers = 3;

    // Function to update player list
    function updatePlayerList() {
        playerList.innerHTML = '';
        players.forEach(player => {
            const li = document.createElement('li');
            li.textContent = player;
            playerList.appendChild(li);
        });

        if (players.length < maxPlayers) {
            const waitingLi = document.createElement('li');
            waitingLi.textContent = 'Waiting for more players...';
            playerList.appendChild(waitingLi);
        }
    }

    // Function to add chat message
    function addChatMessage(message, sender = 'System') {
        const p = document.createElement('p');
        p.innerHTML = '<strong>'+sender+':</strong> ' + message;
        chat.appendChild(p);
        chat.scrollTop = chat.scrollHeight; // Scroll to bottom
    }

    // Chat form submission
    chatForm.addEventListener('submit', (event) => {
        event.preventDefault();

        const messageText = chatInput.value.trim();
        if (messageText) {
            //addChatMessage(messageText, '${name} (You)');
            const message = JSON.stringify({type:"chatMessage",username:"${name}",text:messageText});
            ws.send(message);

            chatInput.value = '';
        }
    });



    updatePlayerList();


</script>
</body>
</html>
