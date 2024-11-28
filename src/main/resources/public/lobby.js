import { ws, sendMessage } from './web_socket';
let gameStarted = "";
let username = document.location.href.substring(document.location.href.indexOf("?")+1);
let leader = "";
let playersTurn = "";
let cards;
let selectedCards = [];

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
const cardsDiv = document.getElementById("cardsDiv");

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
            playHandButton.disabled = playersTurn !== username;

            cards = data.cardsInHand;
            if (cards !== undefined) setCardsView();
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
    iterateTurn(messageData){
        loadGameData();
    }
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
window.startGame = startGame;
window.sendRaiseHand = sendRaiseHand;

document.getElementById("playHandForm").addEventListener("submit", function(event) {
    event.preventDefault();
    let hand = {};
    for (let i = 0; i < selectedCards.length; i++){
        const type = selectedCards[i][0];
        if (type in hand) hand[type]++;
        else hand[type] = 1;
    }
    console.log("Hand is: " + hand);
    sendMessage({ type: "playHand", username, hand:hand});
});
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
            console.log(symbol+i);
            cardsDiv.appendChild(cardDiv);
            cardDiv.addEventListener('click', deselectCardHandler);
        }
    }
}
function selectCard(cardId){
    selectedCards.push(cardId);
    console.log("Selected a card, selected cards: " + selectedCards);
    const selectedCardDiv = document.getElementById(cardId);
    selectedCardDiv.style.backgroundColor = "brown";
    selectedCardDiv.removeEventListener('click', deselectCardHandler);
    selectedCardDiv.addEventListener('click', selectCardHandler);
}
function deselectCard(cardId){
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