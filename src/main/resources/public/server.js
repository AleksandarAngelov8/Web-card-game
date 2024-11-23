const WebSocket = require('ws');
const wss = new WebSocket.Server({ port: 3000 });

wss.on('connection', (ws) => {
    ws.on('message', (message) => {
        const data = JSON.parse(message);

        if (data.type === "raise_hand") {
            const username = data.username;
            console.log(`${username} raised their hand`);

            // Broadcast the update to all connected clients
            broadcast({
                type: "update_user",
                userKey: username, // Use the username as the key
                message: `${username} raised their hand!`
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
