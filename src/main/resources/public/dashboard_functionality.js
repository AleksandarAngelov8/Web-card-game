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
