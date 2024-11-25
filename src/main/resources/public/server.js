const WebSocket = require('ws');
const os = require('os');

// Configuration
const PORT = 3000;
const JAVA_SERVER_HOST = process.env.JAVA_SERVER_HOST || 'localhost';

// Initialize WebSocket Server
const wss = new WebSocket.Server({ host: '0.0.0.0', port: PORT });

let communicationToken;

// Handshake with Java server
function handShakeWithJavaServer() {
    fetch(`http://${JAVA_SERVER_HOST}:4567/hand_shake`, {
        method: "GET",
    })
    .then(response => response.text())
    .then(token => {
        communicationToken = token;
        console.log('Handshake successful, token:', communicationToken);
    })
    .catch(error => console.error('Error during handshake:', error));
}
handShakeWithJavaServer();

wss.on('connection', (ws) => {
    ws.on('message', (message) => {
        let data;
        try {
            data = JSON.parse(message);
        } catch (e) {
            console.error('Invalid JSON:', e);
            return;
        }

        if (data.type === "raise_hand") {
            const username = data.username;
            const info = `${username} raised their hand`;
            console.log(info);

            broadcast({
                type: "update_user",
                userKey: username,
                message: info
            });

            fetch(`http://${JAVA_SERVER_HOST}:4567/update_info`, {
                method: "POST",
                body: JSON.stringify({
                    username,
                    token: communicationToken,
                    info
                }),
                headers: {
                    "Content-type": "application/json; charset=UTF-8"
                }
            }).catch(error => console.error('Error updating info:', error));
        }
        else if (data.type === "join"){
            const username = data.username;
            broadcast({ type: "join", user: username });
        }
        else if (data.type === "chatMessage"){
            const username = data.username;
            const message = data.text;
            broadcast({ type: "chatMessage", user: username, text: message });
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

// Utility to get local IP addresses
function getNetworkAddresses() {
    const interfaces = os.networkInterfaces();
    const addresses = [];
    for (const iface of Object.values(interfaces)) {
        for (const config of iface) {
            if (config.family === 'IPv4' && !config.internal) {
                addresses.push(config.address);
            }
        }
    }
    return addresses;
}

// Log server addresses
console.log(`WebSocket server running on the following addresses:`);
getNetworkAddresses().forEach(ip => console.log(`ws://${ip}:${PORT}`));
