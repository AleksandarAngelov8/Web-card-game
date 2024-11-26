<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lobby Waiting Room</title>

    <link rel="stylesheet" href="styles.css">
</head>
<body>
<p style="display: none" id="name">${name}</p>
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
    <button type= "submit" id="startButton" onclick="startGame()" disabled>Start</button>
</div>
<div id="game" style="display: none">
    <p style="display: none" id="users"><#list users?keys as key>${key},</#list>
    </p>
    <h1>Welcome to the Dashboard, ${name}!</h1>

    <!-- Button to raise hand -->


    <div id="container">
        <div id="user" class="user_div"></div>
        <div id="userTopRight" class="user_div"></div>
        <div id="userTopLeft" class="user_div"></div>
    </div>
    <form id="raiseHandForm">
        <button id="raiseHand" type="submit" >Raise Hand</button>
    </form>
</div>
<script type="module" src="lobby.js"></script>
</body>
</html>
