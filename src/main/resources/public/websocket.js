// sharedWebSocket.js
const wsURL = "wss://2933-2a02-908-113-9ac0-48a5-3e2d-3caf-c287.ngrok-free.app";

export const ws = new WebSocket(wsURL);

ws.onopen = () => {
    console.log("WebSocket connection established.");
};

ws.onerror = (error) => {
    console.error("WebSocket error:", error);
};

ws.onclose = () => {
    console.log("WebSocket connection closed.");
};

// Optional: Add a function for sending messages to standardize usage
export const sendMessage = (message) => {
    if (ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify(message));
    } else {
        console.warn("WebSocket is not open. Message not sent:", message);
    }
};
