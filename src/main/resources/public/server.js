const WebSocket = require('ws');
const wss = new WebSocket.Server({ port: 3000 });

let communicationToken;

function handShakeWithJavaServer(){
    fetch("http://localhost:4567/hand_shake", {
        method: "GET",})
        .then(r => r.text())
        .then(token => {
            communicationToken = token
        })
        .catch(error => console.error('Error during handshake:', error));
}
handShakeWithJavaServer();

wss.on('connection', (ws) => {
    ws.on('message', (message) => {
        const data = JSON.parse(message);

        if (data.type === "raise_hand") {
            const username = data.username;
            const info = data.username + ' raised their hand';
            console.log(info);


            broadcast({
                type: "update_user",
                userKey: username,
                message: info
            });

            fetch("http://localhost:4567/update_info", {
                method: "POST",
                body: JSON.stringify({
                    username: username,
                    token: communicationToken,
                    info: info
                }),
                headers: {
                    "Content-type": "application/json; charset=UTF-8"
                }
            });
        }
    });

    ws.on('close', () => {
        console.log('A client disconnected');
    });
});

function broadcast(data) {
    const message = JSON.stringify(data);
    wss.clients.forEach((client) => {
        if (client.readyState === WebSocket.OPEN) {
            client.send(message);
        }
    });
}

console.log("WebSocket server running on ws://localhost:3000");
