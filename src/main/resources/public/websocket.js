// sharedWebSocket.js
const wsURL = window.location.hostname === 'localhost'
    ? "ws://localhost:3000/ws"
    : "ws://91.139.20.23:3000/ws";

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
