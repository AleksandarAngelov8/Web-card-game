package NodeJsServer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NodeJsServerRunner {
    public static String communicationToken;
    private static Process ngrokProcess;
    private static void RunNGrok(){
        try {
            // Start the ngrok process
            ProcessBuilder pb = new ProcessBuilder("ngrok", "start", "--all");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down Ngrok process...");
                try {
                    // Kill all running ngrok.exe processes
                    new ProcessBuilder("taskkill", "/f", "/im", "ngrok.exe").start();
                } catch (IOException e) {
                    System.err.println("Failed to terminate ngrok process: " + e.getMessage());
                }
            }));

            // Capture the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            String wsURL = null;

            // Regex to match public ngrok WebSocket URL
            Pattern wsUrlPattern = Pattern.compile("https://[a-zA-Z0-9\\-]+\\.ngrok-free\\.app");

            System.out.println(reader.readLine());
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Optional: log the ngrok output
                Matcher matcher = wsUrlPattern.matcher(line);
                if (matcher.find()) {
                    wsURL = matcher.group().replace("https://", "wss://");
                    break;
                }
            }
            System.out.println("hello");

            if (wsURL != null) {
                System.out.println("WebSocket URL: " + wsURL);

                // Update sharedWebSocket.js
                updateJavaScriptFile(wsURL);
            } else {
                System.err.println("Failed to extract WebSocket URL from ngrok output.");
            }

            // Optionally wait for the process to end (or handle separately)
            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void updateJavaScriptFile(String wsURL) throws IOException {
        // Path to the JavaScript file
        File jsFile = new File("src/main/resources/public/server.js");

        // Read the current content of the file
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(jsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Replace the WebSocket URL line dynamically
                if (line.trim().startsWith("const wsURL")) {
                    content.append("const wsURL = \"").append(wsURL).append("\";\n");
                } else {
                    content.append(line).append("\n");
                }
            }
        }

        // Write the updated content back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(jsFile))) {
            writer.write(content.toString());
        }

        System.out.println("Updated sharedWebSocket.js with WebSocket URL: " + wsURL);
    }
    public static void RunServer(){
        //RunNGrok();
        Process process = null;
        try {
            String serverScript = "src/main/resources/public/server.js";
            ProcessBuilder processBuilder = new ProcessBuilder("node",serverScript);
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();

            Process finalProcess = process;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down Node.js process...");
                finalProcess.destroy();
            }));

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            System.out.println("Node.js process exited with code: " + exitCode);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null && process.isAlive()) {
                process.destroy();
            }
        }
    }
}
