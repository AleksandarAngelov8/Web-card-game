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
        console.log();
        console.log();
    })
    .catch(error => console.error('Error during handshake:', error));
}
handShakeWithJavaServer();

const clients = new Map();
let leader = "";
const messageHandlers = {
    join(ws, data) {
        const username = data.username;
        if (clients.size === 0) {
            leader = username;
        }
        clients.set(ws, username);

        broadcast({ type: "setLeader", leader: leader });
        const users = Array.from(clients.values());
        broadcast({ type: "setUsers", users: users});
        broadcast({ type: "join", user: username, javaHost:JAVA_SERVER_HOST });
    },
    raise_hand(ws, data) {
        const username = data.username;
        const info = `${username} raised their hand`;

        broadcast({
            type: "update_user",
            userKey: username,
            message: info,
        });

        sendToJava("update_info",{ username:username,  info:info });
    },
    chatMessage(ws, data) {
        const username = data.username;
        const message = data.text;
        broadcast({ type: "chatMessage", user: username, text: message });
    },
    async startGame(ws, data) {
        const result= await sendToJava("start_game");
        broadcast({ type: "startGame"});
    },
    async playHand(ws, data){
        const result = await sendToJava("play_hand",data);
        broadcast({type:"playHand"});
    },
    async callPrevHand(ws, data){
        const result = await sendToJava("call_prev_hand",data);
        broadcast({type:"callPrevHand"});
    }
};

wss.on("connection", (ws) => {
    ws.on("message", (message) => {
        let data;
        try {
            data = JSON.parse(message);
        } catch (e) {
            console.error("Invalid JSON:", e);
            return;
        }
        const handler = messageHandlers[data.type];
        if (handler) {
            handler(ws, data);
        } else {
            console.error(`Unknown message type: ${data.type}`);

        }
    });
    ws.on('close', () => {
        const username = clients.get(ws);
        //console.log(username + ' has disconnected');

        broadcast({type: "leave", user: username});
        clients.delete(ws);
        if (username === leader){
            for (const client of clients.keys()){
                leader = clients.get(client);
                break;
            }
            broadcast({type: "setLeader", leader});
        }

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
async function sendToJava(route,data={}){
    data.token = communicationToken;
    try {
        const response = await fetch(`http://${JAVA_SERVER_HOST}:4567/` + route, {
            method: "POST",
            body: JSON.stringify(data),
            headers: {
                "Content-type": "application/json; charset=UTF-8"
            }
        }).catch(error => console.error('Error updating info:', error));
        return await response;
    }catch (error){
        console.error("Error sending data to java: ", error);
        throw error;
    }

}
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
