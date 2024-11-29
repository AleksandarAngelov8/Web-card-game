import { ws, sendMessage } from './web_socket';
let gameStarted = "";
let username = document.location.href.substring(document.location.href.indexOf("?")+1);
let leader = "";
let playersTurn = "";
let cards;
let selectedCards = [];
let isCurrentlyPlaying = false;
let numPlayedHands = 0;
let liarsCard;
let canCall = false;
let JAVA_SERVER_HOST;
let alivePlayers;

const divLobby = document.getElementById("lobby");
const divGame =  document.getElementById("game");
const playerList = document.getElementById('player-list');
const chat = document.getElementById('chat');
const chatForm = document.getElementById('chat-form');
const chatInput = document.getElementById('chat-input');
const maxPlayers = 3;
const div_user = document.getElementById("user");
const div_userTopRight = document.getElementById("userTopRight");
const div_userTopLeft = document.getElementById("userTopLeft");
const playHandButton = document.getElementById("playHand");
const callPrevHandButton = document.getElementById("callPrevHand");
const cardsDiv = document.getElementById("cardsDiv");
const liarsCardH3 = document.getElementById("liarsCard");
const playersTurnH3 = document.getElementById("playersTurn");
const alivePlayersH3 = document.getElementById("alivePlayers");
const logDiv = document.getElementById("logDiv");

let players = [
];
function scheduleDataLoad(){
    if (ws.readyState === WebSocket.CONNECTING){
        ws.addEventListener("open", function sendMessageOnOpen() {
            loadGameData();
            ws.removeEventListener("open", sendMessageOnOpen); // Clean up the listener
        });
    }else{
        loadGameData();
    }
}
function loadGameData(){
    fetch('game_data/'+username)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            gameStarted = data.gameStarted;
            username = data.name;
            players = data.users;
            if (gameStarted === "1"){
                loadGameState();
            }
            else{
                divLobby.style.display = "inline";
            }
            playersTurn = data.playersTurn;
            playersTurnH3.innerHTML = `It's ${playersTurn}'s turn`;
            isCurrentlyPlaying = playersTurn === username
            callPrevHandButton.disabled = !isCurrentlyPlaying || !canCall;

            cards = data.cardsInHand;
            if (cards !== undefined) setCardsView();
            liarsCard = data.liarsCard;
            liarsCardH3.innerHTML = `Liars card this round: ${liarsCard}`;
            alivePlayers = data.alivePlayers;
            alivePlayersH3.innerHTML = `Alive players: ${alivePlayers}`;

            const logData = data.moveInfo;

            if (logData !== undefined && logData["moveType"] !== undefined){
                const logEntryDiv = document.createElement("div");
                logEntryDiv.classList.add("logEntry"); // Apply the "logEntry" class to each log entry

                if (logData["moveType"] === "C") {
                    const playerMoving = logData["playerMoving"];
                    const previousPlayer = logData["previousPlayer"];
                    const allegedPlayedHand = JSON.stringify(logData["allegedPlayedHand"]);
                    const playedHand = JSON.stringify(logData["playedHand"]);
                    const wasLie = logData["wasLie"];
                    const shootingSuccess = logData["shootingSuccess"];

                    logEntryDiv.innerHTML = `<span class="playerName">${playerMoving}</span> called out <span class="playerName">${previousPlayer}</span>'s hand:<br>`;
                    logEntryDiv.innerHTML += `Alleged hand: ${allegedPlayedHand}<br>`;
                    logEntryDiv.innerHTML += `Actual hand: ${playedHand}<br>`;

                    if (wasLie) {
                        logEntryDiv.innerHTML += "It was a lie!<br>";
                        logEntryDiv.innerHTML += `<span class="playerName">${previousPlayer}</span> is shooting themselves..<br>`;
                        if (shootingSuccess) logEntryDiv.innerHTML += `${previousPlayer} killed themselves.<br>`;
                        else logEntryDiv.innerHTML += `It was a blank.<br>`;
                    } else {
                        logEntryDiv.innerHTML += "It was NOT a lie!<br>";
                        logEntryDiv.innerHTML += `<span class="playerName">${playerMoving}</span> is shooting themselves..<br>`;
                        if (shootingSuccess) logEntryDiv.innerHTML += `${playerMoving} killed themselves.<br>`;
                        else logEntryDiv.innerHTML += `It was a blank.<br>`;
                    }

                    // Append to the log container
                    document.getElementById("logDiv").appendChild(logEntryDiv);
                }
                else if (logData["moveType"] === "P") {
                    logEntryDiv.innerHTML = "Playing:\n";
                    const playerMoving = logData["playerMoving"];
                    const allegedPlayedHand = JSON.stringify(logData["allegedPlayedHand"]);

                    // Wrap player name in a <span> for styling
                    logEntryDiv.innerHTML += `<span class="playerName">${playerMoving}</span> claims: <span class="hand">${allegedPlayedHand}</span>:\n`;

                    // Append the new log entry to the log container
                    const logContainer = document.getElementById("logDiv");
                    logContainer.appendChild(logEntryDiv);

                    // Scroll to the bottom of the log container
                    logContainer.scrollTop = logContainer.scrollHeight;
                }
                logDiv.className = "logEntry";
                logDiv.appendChild(logEntryDiv);
                logDiv.scrollTop = logDiv.scrollHeight;
            }
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });
}
scheduleDataLoad();

// Handle incoming messages
const messageHandlers = {
    update_user(messageData) {
        let userDiv = document.getElementById(messageData.userKey);
        if (!userDiv) {
            userDiv = document.createElement("div");
            userDiv.id = messageData.userKey;
            document.getElementById("container").appendChild(userDiv);
        }
        userDiv.textContent = messageData.message;
    },
    join(messageData) {
        addChatMessage(messageData.user + " has been resurrected.");
        updatePlayerList();
        JAVA_SERVER_HOST = messageData.javaHost;
    },
    chatMessage(messageData) {
        const user = messageData.user;
        const you = username === user ? " (you)" : "";
        const message = messageData.text;
        addChatMessage(message, user + you);
    },
    leave(messageData) {
        const user = messageData.user;
        addChatMessage(user + " killed themselves.");
        players.splice(players.indexOf(user), 1);
        updatePlayerList();
    },
    setLeader(messageData) {
        leader = messageData.leader;
        updatePlayerList();
    },
    setUsers(messageData) {
        players = Array.from(messageData.users);
        document.getElementById("startButton").disabled = (leader !== username || players.length !== 3);
        //console.log("Number of players: " + players.length);
        //console.log("Leader: " + leader);
        //console.log("Username: " + username);
    },
    startGame(messageData){
        divLobby.style.display = "none";
        divGame.style.display = "block";
        setPlayerView();
        loadGameData();
    },
    playHand(messageData){
        numPlayedHands++;
        canCall = true;
        loadGameData();
        playHandButton.disabled = true;
    },
    callPrevHand(messageData){
        numPlayedHands++;
        canCall = false;
        loadGameData();
        playHandButton.disabled = true;
    },
};

ws.onopen = () => {
    const message = {type: "join", username: username};
    sendMessage(message);
};
ws.onmessage = (event) => {
    const messageData = JSON.parse(event.data);
    const handler = messageHandlers[messageData.type];
    if (handler) {
        handler(messageData);
    } else {
        console.error('Unknown message type:', messageData.type);
    }
};
chatForm.addEventListener('submit', (event) => {
    event.preventDefault();
    const messageText = chatInput.value.trim();
    if (messageText) {
        sendMessage({ type: "chatMessage", username, text: messageText });
        chatInput.value = '';
    }
});
addEventListener("close", (event) => {});
function updatePlayerList() {
    playerList.innerHTML = '';
    players.forEach(player => {
        const li = document.createElement('li');
        li.textContent = ((player===leader?"♕":"") + player);
        playerList.appendChild(li);
    });

    if (players.length < maxPlayers) {
        const waitingLi = document.createElement('li');
        waitingLi.textContent = 'Waiting for more players...';
        playerList.appendChild(waitingLi);
    }
}
function addChatMessage(message, sender = 'System') {
    const p = document.createElement('p');
    p.innerHTML = '<strong>'+sender+':</strong> ' + message;
    chat.appendChild(p);
    chat.scrollTop = chat.scrollHeight; // Scroll to bottom
}
updatePlayerList();
function startGame(){
    const message = {type: "startGame"};//could add some additional settings here, e.g. rounds etc.
    if (ws.readyState === WebSocket.CONNECTING){
        ws.addEventListener("open", function sendMessageOnOpen() {
            sendMessage(message);
            ws.removeEventListener("open", sendMessageOnOpen); // Clean up the listener
        });
    }else{
        sendMessage(message);
    }
}
function sendRaiseHand(){
    sendMessage({ type: "raise_hand", username });
}
function logOut(){
    sendToJava("logout");
    window.location = `http://${JAVA_SERVER_HOST}:4567/login`;
}
window.startGame = startGame;
window.sendRaiseHand = sendRaiseHand;
window.playHand = playHand;
window.callPrevHand = callPrevHand;
window.logOut = logOut;
function playHand (){
    let hand = new Map();

    for (let i = 0; i < selectedCards.length; i++) {
        const type = selectedCards[i][0];
        console.log("Type is: " + type);

        if (hand.has(type)) {
            console.log("Increasing number");
            hand.set(type, hand.get(type) + 1); // Increment the count
        } else {
            console.log("Added new type");
            hand.set(type, 1); // Initialize with 1
        }
    }

    console.log("Hand is:", Object.fromEntries(hand)); // Convert Map to an object for logging

    sendMessage({ type: "playHand", username, hand: Object.fromEntries(hand) });
    selectedCards = [];
}
function callPrevHand(){
    sendMessage({ type: "callPrevHand"});
}
function loadGameState(){
    divLobby.style.display = "none";
    divGame.style.display = "inline";
    setPlayerView();
}
function setPlayerView() {

    if (players.indexOf(username) === 0){
        div_user.id = username;
        div_userTopRight.id = players[1];
        div_userTopLeft.id = players[2];
    }
    else if (players.indexOf(username) === 1){
        div_user.id = username;
        div_userTopRight.id = players[2];
        div_userTopLeft.id = players[0];
    }
    else {
        div_user.id = username;
        div_userTopRight.id = players[0];
        div_userTopLeft.id = players[1];
    }

    div_user.style.bottom = "0";
    div_user.style.left = "50%";
    div_user.style.transform = "translateX(-50%)";

    div_userTopLeft.style.top = "0";
    div_userTopLeft.style.left = "0";

    div_userTopRight.style.top = "0";
    div_userTopRight.style.right = "0";
}
function setCardsView(){
    cardsDiv.replaceChildren();
    //console.log(cards);
    for (const key in cards){
        for (let i = 0; i < cards[key]; i++){
            const cardDiv = document.createElement("div");
            const symbol = key.charAt(0);
            const divText = document.createTextNode(symbol);
            cardDiv.appendChild(divText);
            cardDiv.className = "cardDiv";
            cardDiv.id = symbol+i;
            //console.log(symbol+i);
            cardsDiv.appendChild(cardDiv);
            if (isCurrentlyPlaying){
                cardDiv.addEventListener('click', deselectCardHandler);
            }
        }
    }
}
function selectCard(cardId){
    playHandButton.disabled = false;
    selectedCards.push(cardId);
    console.log("Selected a card, selected cards: " + selectedCards);
    const selectedCardDiv = document.getElementById(cardId);
    selectedCardDiv.style.backgroundColor = "brown";
    selectedCardDiv.removeEventListener('click', deselectCardHandler);
    selectedCardDiv.addEventListener('click', selectCardHandler);
}
function deselectCard(cardId){
    playHandButton.disabled = selectedCards.length === 1;
    selectedCards.splice(selectedCards.indexOf(cardId), 1);
    console.log("Deselected a card, selected cards: " + selectedCards);
    const selectedCardDiv = document.getElementById(cardId);
    selectedCardDiv.style.backgroundColor = "yellow";
    selectedCardDiv.removeEventListener('click', selectCardHandler);
    selectedCardDiv.addEventListener('click', deselectCardHandler);
}
function selectCardHandler(event) {
    const cardId = event.currentTarget.id; // Get the card ID
    deselectCard(cardId);
}
function deselectCardHandler(event) {
    const cardId = event.currentTarget.id; // Get the card ID
    selectCard(cardId);
}
function sendToJava(route,data={}){
    fetch(`http://${JAVA_SERVER_HOST}:4567/` + route, {
        method: "POST",
        headers: {
            "Content-type": "application/json; charset=UTF-8"
        }
    }).catch(error => console.error('Error updating info:', error));
}