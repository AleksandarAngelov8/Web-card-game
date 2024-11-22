package RouteHandler;

import UserRightsManager.UserRightsManager;
import com.google.gson.Gson;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.*;

import static spark.Spark.*;

public class RouteHandler {
    static public void SetupRoutes(UserRightsManager userRightsManager){
        ipAddress("0.0.0.0");
        port(4567);
        staticFileLocation("/public");
        final Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);
        configuration.setClassForTemplateLoading(org.apache.ivy.Main.class, "/public");

        get("/dashboard", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("users",userRightsManager.getOnlineUsers());
            String name = request.session().attribute("name");
            if (name != null) {
                attributes.put("name", name);
            } else {
                attributes.put("name", "Guest");
            }

            StringWriter writer = new StringWriter();
            try {
                Template dashboardTemplate = configuration.getTemplate("dashboard.ftl");
                dashboardTemplate.process(attributes, writer);
            } catch (Exception e) {
                halt(500);
            }
            return writer.toString();
        });
        post("/raise_hand", (request, response) -> {
            String name = request.queryParams("name");
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
                jsonResponse.put("message", "Name received: " + name);
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Name not allowed.");
            }

            response.type("application/json");
            return new Gson().toJson(jsonResponse);
        });
        get("/login", (request, response) -> {
            String sessionToken = userRightsManager.getCurrentSessionToken();
            if (sessionToken != null && isValidSessionToken(sessionToken, request)) {
                response.redirect("/dashboard");
                return null;
            }
            Map<String, Object> attributes = new HashMap<>();
            StringWriter writer = new StringWriter();
            try {
                Template representativesTemplate = configuration.getTemplate("login.html");
                representativesTemplate.process(attributes, writer);
            } catch (Exception e) {
                halt(500);
            }
            return writer;
        });
        post("/login", (request, response) -> {
            String username = request.queryParams("username");
            String password = request.queryParams("password");

            // Überprüfen von Benutzername und Passwort
            String sessionToken = UUID.randomUUID().toString();
            if (userRightsManager.authenticate(username, password, sessionToken)) {
                request.session().attribute("sessionToken", sessionToken);
                request.session().attribute("username", username);
                userRightsManager.setCurrentSessionToken(sessionToken);

                response.redirect("/dashboard");
            } else {
                response.redirect("/login?error=true");
            }
            return null;
        });
    }
    private static boolean isValidSessionToken(String sessionToken, spark.Request request) {
        return sessionToken.equals(request.session().attribute("sessionToken"));
    }
}