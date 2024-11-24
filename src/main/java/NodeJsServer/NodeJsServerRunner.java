package NodeJsServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class NodeJsServerRunner {
    public static String communicationToken;
    public static void Run(){
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
