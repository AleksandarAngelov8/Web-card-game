package RouteHandler;

import Game.Game;
import UserRightsManager.UserRightsManager;
import UserRightsManager.User;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import NodeJsServer.NodeJsServerRunner;
import org.apache.ivy.Main;

import static spark.Spark.*;

public class RouteHandler {
    static Game game;
    static private UserRightsManager userRightsManagerLocal;
    static public void SetupRoutes(UserRightsManager userRightsManager){

        userRightsManagerLocal = userRightsManager;
        ipAddress("0.0.0.0");
        port(4567);
        staticFileLocation("/public");
        final Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);
        configuration.setClassForTemplateLoading(Main.class, "/public");

        get("/login", (request, response) -> {
            if (isValidSessionToken(request)) {
                response.redirect("/lobby");
                return null;
            }
            Map<String, Object> attributes = new HashMap<>();
            return renderTemplate(configuration, "login.html", attributes);
        });

        get("/lobby", (request, response) -> {
            if (!isValidSessionToken(request)) {
                response.redirect("/login");
                return null;
            }

            Map<String, Object> attributes = new HashMap<>();
            String name = request.session().attribute("username");
            Map<String, User> otherUsers = userRightsManager.getOnlineUsers();
            otherUsers.remove(name);
            attributes.put("users", otherUsers);
            attributes.put("name", name != null ? name : "Guest");

            return renderTemplate(configuration, "lobby.ftl", attributes);
        });

        get("/web_socket", (request, response) -> {
            response.type("application/javascript");
            Map<String, Object> attributes = new HashMap<>();
            return renderTemplate(configuration, "websocket.js", attributes);
        });

        get("/hand_shake", (request, response) -> {
            if (NodeJsServerRunner.communicationToken == null){
                NodeJsServerRunner.communicationToken = UUID.randomUUID().toString();
                request.session().attribute("communicationToken",NodeJsServerRunner.communicationToken);

                System.out.println("Communication token has been set!");
            }
            return NodeJsServerRunner.communicationToken;
        });

        post("/raise_hand", (request, response) -> {
            if (!isValidSessionToken(request)) return null;
            String name = request.queryParams("name");
            String username = request.session().attribute("username");
            Set<String> names = new HashSet<>(){
                {
                    add("John");
                    add("Nuts");
                    add("Nigelf");
                }
            };
            Map<String, Object> jsonResponse = new HashMap<>();
            if (names.contains(name)) {
                request.session().attribute("name", name);
                jsonResponse.put("success", true);
                jsonResponse.put("username", username);
                jsonResponse.put("message", "Name received: " + name);

                String info = userRightsManager.getUsers().get(username).getStoredInfo();
                userRightsManager.getUsers().get(username).setStoredInfo(info+name);
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Name not allowed.");
            }

            response.type("application/json");
            return new Gson().toJson(jsonResponse);
        });
        post("/login", (request, response) -> {
            String username = request.queryParams("username");
            String password = request.queryParams("password");

            // Überprüfen von Benutzername und Passwort
            String sessionToken = UUID.randomUUID().toString();
            if (userRightsManager.authenticate(username, password, sessionToken)) {
                request.session().attribute("sessionToken", sessionToken);
                request.session().attribute("username", username);

                response.redirect("/lobby");
            } else {
                response.redirect("/login?error=true");
            }
            return null;
        });
        post("/logout", (request, response) -> {
            request.session().attribute("sessionToken", "");
            String username = request.session().attribute("username");
            userRightsManager.getUsers().get(username).activateSession(null);
            response.redirect("/login");
            return null;
        });
        post("/update_info", (request, response) -> {
            String body = request.body();
            try {
                Map bodyAttributes = new Gson().fromJson(body, Map.class);
                if (!isValidCommunicationToken(bodyAttributes.get("token").toString())) return null;

                String name = bodyAttributes.get("username").toString();
                userRightsManager.getUsers().get(name).setStoredInfo(bodyAttributes.get("info").toString());

            } catch (JsonSyntaxException e) {
                System.err.println("Error parsing JSON: " + e.getMessage());
                halt(400, "Invalid JSON format");
            }
            return null;
        });
        post("/start_game", (request, response) -> {
            String body = request.body();
            try {
                Map bodyAttributes = new Gson().fromJson(body, Map.class);
                if (!isValidCommunicationToken(bodyAttributes.get("token").toString())) return null;


                game = new Game(List.copyOf(userRightsManager.getOnlineUsers().keySet()), 1);
                game.PrintPlayers();
            } catch (JsonSyntaxException e) {
                System.err.println("Error parsing JSON: " + e.getMessage());
                halt(400, "Invalid JSON format");
            }
            return null;

        });
    }
    private static boolean isValidSessionToken(spark.Request request) {
        String username = request.session().attribute("username");
        User user = userRightsManagerLocal.getUsers().get(username);
        return user != null &&
                user.sessionToken != null &&
                user.sessionToken.equals(request.session().attribute("sessionToken")) &&
                !user.sessionToken.isEmpty();
    }
    private static boolean isValidCommunicationToken(String jsCommunicationToken){
        if (!jsCommunicationToken.equals(NodeJsServerRunner.communicationToken)){
            System.out.println("Incorrect communication token.");
        }
        return jsCommunicationToken.equals(NodeJsServerRunner.communicationToken);
    }
    private static String renderTemplate(Configuration configuration, String templateName, Map<String, Object> attributes) {
        StringWriter writer = new StringWriter();
        try {
            Template template = configuration.getTemplate(templateName);
            template.process(attributes, writer);
        } catch (Exception e) {
            halt(500, "Template rendering failed: " + e.getMessage());
        }
        return writer.toString();
    }

}
