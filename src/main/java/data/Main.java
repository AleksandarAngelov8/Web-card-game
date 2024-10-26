package data;

import Interface.Abgeordneter;
import Interface.Protokoll;
import Interface.Rede;
import Interface.User;
import MongoDBConnectionHandler.MongoDBHandler;
import MongoDB_Impl.Protokoll_MongoDB_Impl;
import MongoDB_Impl.Rede_MongoDB_Impl;
import UserRightsManager.UserRightsManager;
import com.google.gson.Gson;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.codehaus.jettison.json.JSONException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;


import static data.createSpeaker.createSpeakerWithMeta;
import static data.uploadProtocol.uploadXMLPROTOCOL;
import static spark.Spark.*;

import org.jsoup.Jsoup;
import xml_Impl.Abgeordneter_xml_Impl;
import xml_Impl.Protokoll_xml_Impl;
import xml_Impl.Rede_xml_Impl;

import javax.servlet.MultipartConfigElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, JSONException {
        port(4567);
        staticFileLocation("/public");
        MongoDBHandler connectionHandler = null;
        /*try {
            new Main();
            connectionHandler = new MongoDBHandler();


        } finally {
            if (connectionHandler != null) {
            }
        }*/



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
            return writer.toString(); // Return the updated HTML as a string
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
