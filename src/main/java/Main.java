import MongoDBConnectionHandler.MongoDBHandler;
import com.google.gson.Gson;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.codehaus.jettison.json.JSONException;
import org.xml.sax.SAXException;


import static spark.Spark.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, JSONException {
        port(4567);
        staticFileLocation("/public");
        MongoDBHandler connectionHandler = new MongoDBHandler();;

        connectionHandler.Test();
        final Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);
        configuration.setClassForTemplateLoading(org.apache.ivy.Main.class, "/public");

        get("/dashboard", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            String name = request.session().attribute("name"); // Retrieve the stored name
            if (name != null) {
                attributes.put("name", name);
            } else {
                attributes.put("name", "Guest"); // Default value if name isn't set
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
    }
}
