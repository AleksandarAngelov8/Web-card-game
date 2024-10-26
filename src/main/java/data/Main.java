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
    static Integer downloadFortschritt = 0;

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, JSONException {
        //pictures.extractPictures();
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


        Hashtable<String, Rede> loadedSpeechesWithCAS = new Hashtable<>();
        Hashtable<String, Rede> loadedSpeechesWithoutCAS = new Hashtable<>();
        UserRightsManager userRightsManager = null;//new UserRightsManager(connectionHandler);

        final Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);
        configuration.setClassForTemplateLoading(org.apache.ivy.Main.class, "/public");

        /**
         * erstellt von Feride Yilmaz
         *
         * Endpunkt, was dem Benutzer die Möglichkeit gibt die Datei stammdaten.zip herunterzuladen
         * und zu entpacken
         */
        post("/reloadStammdaten", (request, respone) -> {
            downloadStammdaten();
            return "stammdaten.zip wurde erfolgreich heruntergeladen und etpackt. " +
                    "Inhalt befindet sich im Ordner src/main/resources/stammdaten/stammdatenZipEntpackt/.";
        });

        /**
         * erstell von Feride Yilmaz
         *
         *Endpunkt, was dem Benutzer die Möglichkeit gibt,
         * die fehlenden Abgeordneten in die DB zu importieren
         * falls stammdaten.zip nicht vorhanden ist oder entpackt
         * muss vorher /reloadStammdaten durchgeführt werden
         */
        post("/reloadSpeaker", (request, response) -> {
            String pfad = "src/main/resources/stammdaten/stammdatenZipEntpackt/MDB_STAMMDATEN.XML";
            abgeordneterErstellen.abgeordneterErstellen(pfad);

            downloadJSON.downloadJSON();
            createSpeakerWithMeta();
            //pictures.extractPictures();
            //importPictures.impPicture();
            return "Erfolg!";
        });


        /**
         * erstell von Feride Yilmaz
         *
         * Endpunkt, was dem Benutzer die Möglichkeit gibt,
         * die fehlenden Protokolle aus der 19. & 20. WP hochzuladen
         * alle Protokolle werden somit in die Datenbank importiert
         *  @return Bestätigung, dass Datenbank aufgefüllt wurde
         */
        post("/reloadProtocol", (request, response) -> {
            download();
            return "Datenbank wurde mit Protokollen und Reden aufgefüllt!";
        });

        /**
         * erstell von Feride Yilmaz
         *
         * Endpunkt, was dem Benutzer ermöglicht Protokoll
         * manuell hochzuladen
         * Terminal Eingabe:
         * curl -X POST http://localhost:4567/uploadProtocol?url=HierKommtDerLinkZumProtokoll
         * @param Download Link zum Protokoll
         * @return Bestätigung, ob es hochgeladen wurde, ob Dokument
         * bereits in der Datenbank existiert oder Fehlermeldung,
         * wenn url nicht gültig ist oder nicht zu einer xml führt
         */
        post("/uploadProtocol", (request, response) -> {
            String erg = uploadXMLPROTOCOL(request.queryParams("url"));
            if (erg.equals("JA")) {
                return "Protokoll wurde erfolgreich nachgeladen!    ";
            } else if (erg.equals("NEIN")) {
                return "Protokoll existiert bereits in der Datenbank!   ";
            } else {
                return erg;
            }
        });

        get("/fortschritt", (request, response) -> {
            Gson gson = new Gson();
            Map<String, Integer> progressData = new HashMap<>();
            progressData.put("progress", downloadFortschritt);
            response.type("application/json");
            return gson.toJson(progressData);
        });
//--------------------------------------------------------------------------------------------------------------------
        MongoDBHandler finalConnectionHandler = connectionHandler;
        // erstellt von Angelov
        // Route für die Anmeldung, die aufgerufen wird, wenn "/login" angefordert wird
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
        // erstellt von Angelov
        // Bearbeiten des Anmeldeformulars
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
        post("/logout", (request, response) -> {
            request.session().attribute("sessionToken", "");
            response.redirect("/login");
            return null;
        });
        get("/change_password", (request, response) -> {
            String sessionToken = userRightsManager.getCurrentSessionToken();
            System.out.println(sessionToken);
            if (sessionToken == null || !isValidSessionToken(sessionToken, request)) {
                response.redirect("/dashboard");
                return null;
            }

            Map<String, Object> attributes = new HashMap<>();
            StringWriter writer = new StringWriter();
            try {
                Template representativesTemplate = configuration.getTemplate("change_password.html");
                representativesTemplate.process(attributes, writer);
            } catch (Exception e) {
                halt(500);
            }
            return writer;
        });
        post("/change_password", (request, response) -> {
            String username = request.session().attribute("username");
            String password = request.queryParams("newPassword");
            userRightsManager.changePasswordOfUser(username, password, finalConnectionHandler);

            response.redirect("/dashboard");
            return null;
        });
        post("/register", (request, response) -> {
            String username = request.queryParams("username");
            String password = request.queryParams("password");

            String sessionToken = UUID.randomUUID().toString();
            userRightsManager.addNewUserToDB(username, password, 0, finalConnectionHandler);
            if (userRightsManager.authenticate(username, password, sessionToken)) {
                request.session().attribute("sessionToken", sessionToken);
                request.session().attribute("username", username);

                response.redirect("/dashboard");
            } else {
                response.redirect("/login?error=true");
            }
            return null;
        });

        // erstellt von Angelov
        // Placeholder webseite, soll was anderes sein
        get("/dashboard", (request, response) -> {
            String sessionToken = userRightsManager.getCurrentSessionToken();

            String username = request.session().attribute("username");

            Map<String, Object> attributes = new HashMap<>();
//asdasdasdas
            boolean admin = isValidSessionToken(sessionToken, request)
                    && userRightsManager.sessionHasAdminRights(username, sessionToken);
            boolean manager = isValidSessionToken(sessionToken, request)
                    && userRightsManager.sessionHasManagerRights(username, sessionToken);
            boolean loggedIn = isValidSessionToken(sessionToken, request);

            attributes.put("admin", admin);
            attributes.put("manager", manager || admin);
            attributes.put("loggedIn", loggedIn || admin);

            attributes.put("username", username);

            StringWriter writer = new StringWriter();
            try {
                Template dashboardTemplate = configuration.getTemplate("dashboard.ftl");
                dashboardTemplate.process(attributes, writer);
            } catch (Exception e) {
                halt(500);
            }
            return writer;
        });

        // erstellt von Angelov
        // sunburst diagramm
        get("/sunburst", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            //Bring this back
            Map<String, Double> topicInfo = new HashMap<>();
            for (String redeId : loadedSpeechesWithCAS.keySet()) {
                Map<String, Double> topicdata = loadedSpeechesWithCAS.get(redeId).getCAS().getTopicData();
                for (String ne : topicdata.keySet()) {
                    if (!topicInfo.containsKey(ne)) {
                        topicInfo.put(ne, topicdata.get(ne));
                    } else {
                        topicInfo.put(ne, topicInfo.get(ne) + topicdata.get(ne));
                    }
                }
            }
            StringWriter writer = new StringWriter();
            attributes.put("data", topicInfo);
            try {
                Template infoTemplate = configuration.getTemplate("topic_sunburst.ftl");
                infoTemplate.process(attributes, writer);
            } catch (Exception e) {
                halt(1000);
            }
            return writer;
        });

        // erstellt von Angelov
        // barchart
        get("/barchart_pos", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            //Bring this back
            Map<String, Integer> topicInfo = new HashMap<>();
            for (String redeId : loadedSpeechesWithCAS.keySet()) {
                Map<String, Integer> topicdata = loadedSpeechesWithCAS.get(redeId).getCAS().getPOSOccurance();
                for (String ne : topicdata.keySet()) {
                    if (!topicInfo.containsKey(ne)) {
                        topicInfo.put(ne, topicdata.get(ne));
                    } else {
                        topicInfo.put(ne, topicInfo.get(ne) + topicdata.get(ne));
                    }
                }
            }
            System.out.println(topicInfo.size());
            attributes.put("data", topicInfo);
            StringWriter writer = new StringWriter();
            try {
                Template infoTemplate = configuration.getTemplate("pos_barchart.ftl");
                infoTemplate.process(attributes, writer);
            } catch (Exception e) {
                halt(1000);
            }
            return writer;
        });

        // erstellt von Angelov
        // radarchart
        get("/radarchart", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            //Bring this back
            Map<String, Double> topicInfo = new HashMap<>();
            topicInfo.put("1", 0.0);
            topicInfo.put("0", 0.0);
            topicInfo.put("-1", 0.0);
            for (String redeId : loadedSpeechesWithCAS.keySet()) {
                boolean first = true;
                List<Double> sentimentData = loadedSpeechesWithCAS.get(redeId).getCAS().getSentimentData();
                for (Double ne : sentimentData) {
                    if (first) {
                        first = false;
                        continue;
                    }
                    if (ne > 0.3) {
                        topicInfo.put("1", topicInfo.get("1") + 1);
                        System.out.println(ne + " POSITIVE");
                    } else if (ne < -0.3) {
                        topicInfo.put("-1", topicInfo.get("-1") + 1);
                        System.out.println(ne + " NEGATIVE");
                    } else {
                        topicInfo.put("0", topicInfo.get("0") + 1);
                    }
                }
            }
            double max = Math.max(Math.max(topicInfo.get("-1"), topicInfo.get("0")), topicInfo.get("1"));
            topicInfo.put("1", topicInfo.get("1") / max);
            topicInfo.put("-1", topicInfo.get("-1") / max);
            topicInfo.put("0", topicInfo.get("0") / max);
            for (String key : topicInfo.keySet()) {
                System.out.println(key + " - " + topicInfo.get(key));
            }
            attributes.put("data", topicInfo);
            StringWriter writer = new StringWriter();
            try {
                Template infoTemplate = configuration.getTemplate("sentiment_radarchart.ftl");
                infoTemplate.process(attributes, writer);
            } catch (Exception e) {
                halt(1000);
            }
            return writer;
        });

        // erstellt von Angelov
        // radarchart
        get("/bubblechart", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            //Bring this back
            Map<String, Integer> topicInfo = new HashMap<>();
            for (String redeId : loadedSpeechesWithCAS.keySet()) {
                Map<String, Integer> namedEntityOccurance = loadedSpeechesWithCAS.get(redeId).getCAS().getNamedEntityOccurance();
                for (String ne : namedEntityOccurance.keySet()) {
                    if (!topicInfo.containsKey(ne)) {
                        topicInfo.put(ne, namedEntityOccurance.get(ne));
                    } else {
                        topicInfo.put(ne, topicInfo.get(ne) + namedEntityOccurance.get(ne));
                    }
                }
            }
            /*CAS testcas = new CAS_Impl(null);
            testcas.loadJCasFromDB();*/
            //System.out.println(topicInfo.size());
            /*for (String key: topicInfo.keySet()){
                System.out.println(key + " - " + topicInfo.get(key));
            }*/
            attributes.put("data", topicInfo);
            StringWriter writer = new StringWriter();
            try {
                Template infoTemplate = configuration.getTemplate("named_entity_bubble.ftl");
                infoTemplate.process(attributes, writer);
            } catch (Exception e) {
                halt(1000);
            }
            return writer;
        });
        get("/date_search_cas/search", (request, response) -> {
            StringWriter writer = new StringWriter();
            try {
                Template infoTemplate = configuration.getTemplate("date_search_cas.html");
                infoTemplate.process(null, writer);
            } catch (Exception e) {
                halt(1000);
            }
            return writer;
        });
        // erstellt von Angelov
        // suche nach datum
        // speichert die geladene reden in loadedSpeechesWithCAS
        post("/date_search_cas/submit_date", (request, response) -> {
            String startDateString = request.queryParams("start-date");
            String endDateString = request.queryParams("end-date");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDate = LocalDate.parse(startDateString, formatter);
            LocalDate endDate = LocalDate.parse(endDateString, formatter);

            Map<String, Object> attributes = new HashMap<>();

            List<Rede> reden = finalConnectionHandler.getRedenFromDBWithCAS(startDate, endDate);

            for (Rede rede : reden) {
                loadedSpeechesWithCAS.put(rede.getID().toString(), rede);
            }

            attributes.put("speeches", reden);


            response.redirect("/generic_search/search");

            return "";
        });
        // erstellt von Angelov
        get("/date_search_speeches/search", (request, response) -> {
            StringWriter writer = new StringWriter();
            try {
                Template infoTemplate = configuration.getTemplate("date_search_speeches.html");
                infoTemplate.process(null, writer);
            } catch (Exception e) {
                halt(1000);
            }
            return writer;
        });
        // erstellt von Angelov
        // suche nach datum
        // speichert die geladene reden in loadedSpeechesWithoutCAS
        post("/date_search_speeches/submit_date", (request, response) -> {
            String startDateString = request.queryParams("start-date");
            String endDateString = request.queryParams("end-date");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDate = LocalDate.parse(startDateString, formatter);
            LocalDate endDate = LocalDate.parse(endDateString, formatter);

            Map<String, Object> attributes = new HashMap<>();

            List<Rede> reden = finalConnectionHandler.getRedenFromDBWithoutCAS(startDate, endDate);

            for (Rede rede : reden) {
                loadedSpeechesWithoutCAS.put(rede.getID().toString(), rede);
            }

            attributes.put("speeches", reden);

            StringWriter writer = new StringWriter();
            try {
                Template infoTemplate = configuration.getTemplate("date_search_speeches_result.ftl");
                infoTemplate.process(attributes, writer);
            } catch (Exception e) {
                halt(1000);
            }
            return writer;
        });
        // erstellt von Angelov
        // such nach diagramme (sentiment,pos,speakers,named entities and topics)
        // zeigt die entsprechende diagramme in iframe
        get("/generic_search/search", (req, res) -> {
            Map<String, Object> attributes = new HashMap<>();

            StringWriter writer = new StringWriter();
            Map<String, String> links = new HashMap<>();
            links.put("sentiments", "http://localhost:4567/radarchart");
            links.put("pos", "http://localhost:4567/barchart_pos");
            links.put("speakers", "http://localhost:4567/barchart_redner");
            links.put("named entities", "http://localhost:4567/bubblechart");
            links.put("topics", "http://localhost:4567/sunburst");

            attributes.put("data", links);
            try {
                Template infoTemplate = configuration.getTemplate("generic_search.ftl");
                infoTemplate.process(attributes, writer);
            } catch (Exception e) {
                halt(1000);
            }
            return writer;
        });
        // erstellt von Angelov
        // barchart der redner
        get("/barchart_redner", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            //Bring this back
            Map<String, Integer> topicInfo = new HashMap<>();
            for (String redeId : loadedSpeechesWithCAS.keySet()) {
                Abgeordneter redner = loadedSpeechesWithCAS.get(redeId).getRedner();
                String name = redner.getVorname() + " " + redner.getName();
                if (!topicInfo.containsKey(name)) {
                    topicInfo.put(name, 1);
                } else {
                    topicInfo.put(name, topicInfo.get(name) + 1);
                }
            }
            attributes.put("data", topicInfo);
            StringWriter writer = new StringWriter();
            try {
                Template infoTemplate = configuration.getTemplate("pos_barchart.ftl");
                infoTemplate.process(attributes, writer);
            } catch (Exception e) {
                halt(1000);
            }
            return writer;
        });
        // erstellt von Angelov
        // Informationen über der Redner und die Rede
        // Sentiment und named entities
        // zweites button führt zu generic_search
        get("/rede/:rede", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            String redeS = request.params(":rede");
            Rede rede = null;
            loadedSpeechesWithCAS.clear();
            rede = new Rede_MongoDB_Impl(MongoDBHandler.getCollectionSpeech(), finalConnectionHandler.getRedeDocument(redeS), true);
            rede.loadJCasFromDB();
            loadedSpeechesWithCAS.put(rede.getID().toString(), rede);
            rede.getCAS().extractNamedEntities();
            rede.getCAS().extractSentiment();
            attributes.put("text", rede.getText());
            attributes.put("speech_id", rede.getID());
            attributes.put("comments", rede.getKommentare());
            attributes.put("fraction", rede.getFraktion());
            Abgeordneter redner = rede.getRedner();
            attributes.put("speaker_id", redner.getID());
            attributes.put("speaker_name", redner.getVorname() + " " + redner.getName());
            attributes.put("party", redner.getPartei());

            StringWriter writer = new StringWriter();
            try {
                Template representativesTemplate = configuration.getTemplate("rede.ftl");
                representativesTemplate.process(attributes, writer);
            } catch (Exception e) {
                halt(500);
            }
            return writer;

        });
        // erstellt von Angelov
        // JSON Informationen des sentiments
        get("/api/sentiment/:rede", (request, response) -> {
            response.type("application/json");
            String redeS = request.params(":rede");
            Rede rede = loadedSpeechesWithCAS.get(redeS);
            if (rede == null) {
                rede = new Rede_MongoDB_Impl(MongoDBHandler.getCollectionSpeech(), finalConnectionHandler.getRedeDocument(redeS), true);
                rede.loadJCasFromDB();
                loadedSpeechesWithCAS.put(rede.getID().toString(), rede);
            }
            String jsonS = rede.getCAS().extractSentiment();
            return jsonS;
        });
        // erstellt von Angelov
        // JSON Informationen der namedentities
        get("/api/namedentities/:rede", (request, response) -> {
            response.type("application/json");
            String redeS = request.params(":rede");
            Rede rede = loadedSpeechesWithCAS.get(redeS);
            if (rede == null) {
                rede = new Rede_MongoDB_Impl(MongoDBHandler.getCollectionSpeech(), finalConnectionHandler.getRedeDocument(redeS), true);
                rede.loadJCasFromDB();
                loadedSpeechesWithCAS.put(rede.getID().toString(), rede);
            }
            String jsonS = rede.getCAS().extractNamedEntities();
            return jsonS;
        });
        // erstellt von Angelov
        // volltextsuche
        // man kann auf ergebnisse klicken und die Informationen der rede öffnen
        get("/full_text_search/search", (request, response) -> {
            StringWriter writer = new StringWriter();
            try {
                Template representativesTemplate = configuration.getTemplate("full_text_search.html");
                representativesTemplate.process(null, writer);
            } catch (Exception e) {
                halt(500);
            }
            return writer;
        });
        // erstellt von Angelov
        // speichert die Ids der reden die keyWord enthalten
        get("/api/search/:keyWord", (request, response) -> {
            String keyWord = request.params(":keyWord");
            return MongoDBHandler.fullTextSearch(keyWord);
        });
        // erstellt von Angelov
        // Informationen über die Abgeordneter
        get("/info/speakers", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            List<Abgeordneter> representatives = finalConnectionHandler.getAbgeordnete();
            attributes.put("representatives", representatives);
            StringWriter writer = new StringWriter();
            try {
                Template representativesTemplate = configuration.getTemplate("representatives.ftl");
                representativesTemplate.process(attributes, writer);
            } catch (Exception e) {
                halt(500);
            }
            return writer;
        });
        // erstellt von Angelov
        get("/admin/users", (request, response) -> {
            if (!checkRights(request, userRightsManager, true)) {
                response.redirect("/dashboard");
                return null;
            }
            Map<String, Object> attributes = new HashMap<>();
            List<User> users = new ArrayList(userRightsManager.getUsers().values());
            attributes.put("users", users);
            StringWriter writer = new StringWriter();
            try {
                Template representativesTemplate = configuration.getTemplate("manage_users.ftl");
                representativesTemplate.process(attributes, writer);
            } catch (Exception e) {
                halt(500);
            }
            return writer;
        });
        // erstellt von Angelov
        post("/admin/create_user", (request, response) -> {
            if (!checkRights(request, userRightsManager, true)) {
                response.redirect("/dashboard");
                return null;
            }
            String username = request.queryParams("username");
            String password = request.queryParams("create_password");
            String rightsS = request.queryParams("rights");
            int rights = 0;
            switch (rightsS) {
                case "Normal user":
                    rights = 0;
                    break;
                case "Manager":
                    rights = 1;
                    break;
                case "Admin":
                    rights = 2;
                    break;
            }

            userRightsManager.addNewUserToDB(username, password, rights, finalConnectionHandler);

            response.redirect("/admin/users");
            return null;
        });
        // erstellt von Angelov
        post("/admin/edit_user", (request, response) -> {
            if (!checkRights(request, userRightsManager, true)) {
                response.redirect("/dashboard");
                return null;
            }
            String username = request.queryParams("edit_user");
            String newUsername = request.queryParams("new_username");
            String password = request.queryParams("edit_password");
            String rightsS = request.queryParams("rights");
            int rights = 0;
            switch (rightsS) {
                case "Normal user":
                    rights = 0;
                    break;
                case "Manager":
                    rights = 1;
                    break;
                case "Admin":
                    rights = 2;
                    break;
                default:
                    response.redirect("/admin/users?error=true");
                    return null;
            }
            User user = userRightsManager.getUsers().get(username);
            if (user == null) {
                response.redirect("/admin/users?user_does_not_exist_error=true");
                return null;
            }
            if (!user.getUsername().equals(newUsername)) {
                userRightsManager.changeUsernameOfUser(username, newUsername, finalConnectionHandler);
            }
            if (!user.getPassword().equals(password)) {
                userRightsManager.changePasswordOfUser(username, password, finalConnectionHandler);
            }
            if (user.getRights() != rights) {
                userRightsManager.changeRightsOfUser(username, rights, finalConnectionHandler);
            }
            userRightsManager.printUserInfo();
            response.redirect("/admin/users");
            return null;
        });
        // erstellt von Angelov
        post("/admin/delete_user", (request, response) -> {
            if (!checkRights(request, userRightsManager, true)) {
                response.redirect("/dashboard");
                return null;
            }
            String username = request.queryParams("delete_user");
            if (userRightsManager.getUsers().get(username) == null) {
                response.redirect("/admin/users?user_already_exists_error=true");
                return null;
            }
            userRightsManager.deleteUser(username, finalConnectionHandler);

            response.redirect("/admin/users");
            return null;
        });
        // erstellt von Angelov
        get("/manage/speakers", (request, response) -> {
            if (!checkRights(request, userRightsManager, false)) {
                response.redirect("/dashboard");
                return null;
            }
            Map<String, Object> attributes = new HashMap<>();
            StringWriter writer = new StringWriter();
            try {
                Template representativesTemplate = configuration.getTemplate("manage_speakers.ftl");
                representativesTemplate.process(attributes, writer);
            } catch (Exception e) {
                halt(500);
            }
            return writer;
        });
        // erstellt von Angelov
        post("/manage/create_speaker", (request, response) -> {
            if (!checkRights(request, userRightsManager, false)) {
                response.redirect("/dashboard");
                return null;
            }
            Object oID = request.queryParams("ID");
            String sName = request.queryParams("Name");
            String sVorname = request.queryParams("Vorname");
            String sOrtszusatz = request.queryParams("Ortszusatz");
            String sAdelssuffix = request.queryParams("Adelssuffix");
            String sAnrede = request.queryParams("Anrede");
            String sAkadTitel = request.queryParams("Akadtitel");


            String dGeburtsDatumS = request.queryParams("Geburtsdatum");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dGeburtsDatum = new Date(dateFormat.parse(dGeburtsDatumS).getTime());


            String sGeburtsOrt = request.queryParams("Geburtsort");
            String sGeschlecht = request.queryParams("Geschlecht");
            String sReligion = request.queryParams("Religion");
            String sBeruf = request.queryParams("Beruf");
            String sVita = request.queryParams("Vita");
            String sPartei = request.queryParams("Partei");

            Abgeordneter speaker = new Abgeordneter_xml_Impl(oID, sName, sVorname, sOrtszusatz, sAdelssuffix, sAnrede,
                    sAkadTitel, dGeburtsDatum, sGeburtsOrt, sGeschlecht, sReligion, sBeruf, sVita, sPartei);
            if (finalConnectionHandler.createSpeaker(speaker)) {
                response.redirect("/manage/speakers");
            } else {
                response.redirect("/manage/speakers?create_error=true");
            }
            return null;
        });
        // erstellt von Angelov
        post("/manage/edit_speaker", (request, response) -> {
            if (!checkRights(request, userRightsManager, false)) {
                response.redirect("/dashboard");
                return null;
            }
            Object oID = request.queryParams("id_edit");
            System.out.println("Editing: " + oID);
            String field = request.queryParams("field");
            String changeTo = request.queryParams("change_to");
            System.out.println("Field: " + field + " -> " + changeTo);

            if (finalConnectionHandler.editSpeaker(oID, field, changeTo)) {
                response.redirect("/manage/speakers");
            } else {
                response.redirect("/manage/speakers?error_editing=true");
            }
            return null;
        });
        // erstellt von Angelov
        post("/manage/delete_speaker", (request, response) -> {
            if (!checkRights(request, userRightsManager, false)) {
                response.redirect("/dashboard");
                return null;
            }
            Object oID = request.queryParams("id_delete");

            if (finalConnectionHandler.deleteSpeaker(oID)) {
                response.redirect("/manage/speakers");
            } else {
                response.redirect("/manage/speakers?create_error=true");
            }
            return null;
        });
        // erstellt von Angelov
        get("/manage/speeches", (request, response) -> {
            if (!checkRights(request, userRightsManager, false)) {
                response.redirect("/dashboard");
                return null;
            }
            Map<String, Object> attributes = new HashMap<>();
            StringWriter writer = new StringWriter();
            try {
                Template representativesTemplate = configuration.getTemplate("manage_speeches.ftl");
                representativesTemplate.process(attributes, writer);
            } catch (Exception e) {
                halt(500);
            }
            return writer;
        });
        // erstellt von Angelov
        post("/manage/create_speech", (request, response) -> {
            if (!checkRights(request, userRightsManager, false)) {
                response.redirect("/dashboard");
                return null;
            }

            String id = request.queryParams("ID");
            String redner = request.queryParams("Redner");
            String text = request.queryParams("Text");
            String kommentare = request.queryParams("Kommentare");
            String datumS = request.queryParams("Datum");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date datum = new Date(dateFormat.parse(datumS).getTime());
            String fraktion = request.queryParams("Fraktion");
            String sitzung = request.queryParams("Sitzung");
            String wahlperiode = request.queryParams("Wahlperiode");

            Rede rede = new Rede_xml_Impl(redner, id, text, text.length(), kommentare,
                    kommentare.length(), sitzung, wahlperiode, datum,
                    fraktion);


            if (finalConnectionHandler.createSpeech(rede)) {
                response.redirect("/manage/speeches");
            } else {
                response.redirect("/manage/speeches?create_error=true");
            }
            return null;
        });
        // erstellt von Angelov
        post("/manage/edit_speech", (request, response) -> {
            if (!checkRights(request, userRightsManager, false)) {
                response.redirect("/dashboard");
                return null;
            }
            Object oID = request.queryParams("id_edit");
            System.out.println("Editing: " + oID);
            String field = request.queryParams("field");
            String changeTo = request.queryParams("change_to");
            System.out.println("Field: " + field + " -> " + changeTo);

            if (finalConnectionHandler.editSpeech(oID, field, changeTo)) {
                response.redirect("/manage/speeches");
            } else {
                response.redirect("/manage/speeches?error_editing=true");
            }
            return null;
        });
        // erstellt von Angelov
        post("/manage/delete_speech", (request, response) -> {
            if (!checkRights(request, userRightsManager, false)) {
                response.redirect("/dashboard");
                return null;
            }
            Object oID = request.queryParams("id_delete");

            if (finalConnectionHandler.deleteSpeech(oID)) {
                response.redirect("/manage/speeches");
            } else {
                response.redirect("/manage/speeches?create_error=true");
            }
            return null;
        });
        // erstellt von Angelov
        get("/manage/protocols", (request, response) -> {
            if (!checkRights(request, userRightsManager, false)) {
                response.redirect("/dashboard");
                return null;
            }
            Map<String, Object> attributes = new HashMap<>();
            StringWriter writer = new StringWriter();
            try {
                Template representativesTemplate = configuration.getTemplate("manage_protocols.ftl");
                representativesTemplate.process(attributes, writer);
            } catch (Exception e) {
                halt(500);
            }
            return writer;
        });
        // erstellt von Angelov
        post("/manage/create_protocol", (request, response) -> {
            if (!checkRights(request, userRightsManager, false)) {
                response.redirect("/dashboard");
                return null;
            }

            String id = request.queryParams("ID");
            String sitzung = request.queryParams("Sitzungsnummer");
            String titel = request.queryParams("Titel");
            String ort = request.queryParams("Ort");

            String datumS = request.queryParams("Datum");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date datum = new Date(dateFormat.parse(datumS).getTime());

            String wahlperiode = request.queryParams("Wahlperiode");
            String start = request.queryParams("Start");
            String ende = request.queryParams("Ende");
            String redenS = request.queryParams("Reden");
            List<String> redenSList = Arrays.asList(redenS.split(" "));

            if (finalConnectionHandler.createProtocol(id, sitzung, datum, titel, wahlperiode,
                    ort, redenSList, start, ende)) {
                response.redirect("/manage/protocols");
            } else {
                response.redirect("/manage/protocols?create_error=true");
            }
            return null;
        });
        // erstellt von Angelov
        post("/manage/edit_protocol", (request, response) -> {
            if (!checkRights(request, userRightsManager, false)) {
                response.redirect("/dashboard");
                return null;
            }
            Object oID = request.queryParams("id_edit");
            System.out.println("Editing: " + oID);
            String field = request.queryParams("field");
            String changeTo = request.queryParams("change_to");
            System.out.println("Field: " + field + " -> " + changeTo);

            if (finalConnectionHandler.editProtokol(oID, field, changeTo)) {
                response.redirect("/manage/protocols");
            } else {
                response.redirect("/manage/protocols?error_editing=true");
            }
            return null;
        });
        // erstellt von Angelov
        post("/manage/delete_protocol", (request, response) -> {
            if (!checkRights(request, userRightsManager, false)) {
                response.redirect("/dashboard");
                return null;
            }
            Object oID = request.queryParams("id_delete");

            if (finalConnectionHandler.deleteProtocol(oID)) {
                response.redirect("/manage/protocols");
            } else {
                response.redirect("/manage/protocols?create_error=true");
            }
            return null;
        });
        // erstellt von Angelov
        get("/protocols", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            Map<Date, String> info = finalConnectionHandler.getProtocols();

            List<Map.Entry<Date, String>> entryList = new ArrayList<>(info.entrySet());

            Comparator<Map.Entry<Date, String>> dateComparator = Map.Entry.comparingByKey();

            entryList.sort(dateComparator);

            Map<Date, String> sortedInfo = new LinkedHashMap<>();
            for (Map.Entry<Date, String> entry : entryList) {
                sortedInfo.put(entry.getKey(), entry.getValue());
            }

            attributes.put("protocols", sortedInfo);


            StringWriter writer = new StringWriter();
            try {
                Template representativesTemplate = configuration.getTemplate("protocols.ftl");
                representativesTemplate.process(attributes, writer);
            } catch (Exception e) {
                halt(500);
            }
            return writer;
        });
        // erstellt von Angelov
        get("/export_protocol/:protocol", (request, response) -> {
            String id = request.params("protocol");
            Protokoll_MongoDB_Impl protocol = new Protokoll_MongoDB_Impl(
                    MongoDBHandler.getCollectionProtocol()
                    , MongoDBHandler.getProtocolDocument(id));
            protocol.exportProtocol("src/main/resources/public/LaTeX/");
            StringWriter writer = new StringWriter();
            try {
                Template representativesTemplate = configuration.getTemplate("protocol.ftl");
                representativesTemplate.process(null, writer);
            } catch (Exception e) {
                halt(500);
            }
            return writer;
        });
        // erstellt von Angelov
        get("/preview/pdf", (request, response) -> {
            response.type("application/pdf");

            String filePath = "src/main/resources/public/LaTeX/protocol.pdf";

            return new java.io.FileInputStream(filePath);
        });
        // erstellt von Angelov
        get("/download/pdf", (request, response) -> {
            response.type("application/pdf");

            response.header("Content-Disposition", "attachment; filename=protocol.pdf");

            String filePath = "src/main/resources/public/LaTeX/protocol.pdf";

            return new java.io.FileInputStream(filePath);
        });
    }

    /**
     * erstellt von Angelov
     * überprüft die gültigkeit des Sessiontokens
     * @param sessionToken
     * @param request
     * @return
     */
    private static boolean isValidSessionToken(String sessionToken, spark.Request request) {
        return sessionToken.equals(request.session().attribute("sessionToken"));
    }

    /** erstellt von Angelov
     * überprüft die Rechte von einem Session
     * falls admin = false überprüft nach Managerrechte
     * falls admin = true überprüft nach Adminrechte
     * @param request
     * @param userRightsManager
     * @param adminReq
     * @return
     */
    private static boolean checkRights(spark.Request request, UserRightsManager userRightsManager, boolean adminReq){
        String adminUsername = request.session().attribute("username");
        String sessionToken = request.session().attribute("sessionToken");
        return (sessionToken != null
                && isValidSessionToken(sessionToken,request)
                && ((userRightsManager.sessionHasAdminRights(adminUsername,sessionToken))
                || (userRightsManager.sessionHasManagerRights(adminUsername,sessionToken)) && !adminReq));
    }
    /**
     * erstell von Feride Yilmaz
     *
     * Methode lädt Protokolle direkt von Bundestag herunter und verarbeitet
     * URL zu den Protokollen aus 19. & 20. WP wurden mit DevTools ermittelt
     */
    private static void download() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            // Deaktiviert DTD-Validierung
            factory.setFeature("http://xml.org/sax/features/validation", false);
            // Deaktiviert XML-Validierung
            factory.setValidating(false);

            DocumentBuilder builder= factory.newDocumentBuilder();

            String url19Wahlperiode = "https://www.bundestag.de/ajax/filterlist/de/services/opendata/543410-543410?limit=10&noFilterSet=true&offset=";
            String url20Wahlperiode = "https://www.bundestag.de/ajax/filterlist/de/services/opendata/866354-866354?limit=10&noFilterSet=true&offset=";

            downloadXML(builder,url19Wahlperiode);  // lade xml ab 19. Legislaturperiode
            downloadXML(builder,url20Wahlperiode);  // lade xml ab 20. Legislaturperiode
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * erstell von Feride Yilmaz
     *
     * URLs werden generiert, die zu Protokollen führen
     * Verbindung zur URL wird erstellt und der Inhalt der Seite analysiert
     * alle Links die zu xml- Dateien führen werden heruntergeladen & Inhalt wird analysiert
     * @param builder
     * @param urlLink
     */
    private static void downloadXML(DocumentBuilder builder, String urlLink) {
        try {
            for (int offset = 0; offset <= 250; offset += 10) {
                String urlMitOffset = urlLink + offset;  // URLs werden generiert
                Document protokolle = Jsoup.connect(urlMitOffset).get();
                // Verbindung erstellen &  Document enthält HTML- Inhalt der Seiten
                Elements urlXML = protokolle.select("a[href$=.xml]");
                //  Sammlung von allen links die auf .xml enden
                for (Element url : urlXML) {
                    String href = url.attr("abs:href");  // Link der XML-Datei
                    try (InputStream inputStream = new URL(href).openStream()) {

                        // Verbindung zur URL und öffne Inputstream, um Inhalt lesen zu können
                        org.w3c.dom.Document document = builder.parse(inputStream);
                        // DOM Parser, um XML Datei weiterzuverarbeiten

                        downloadFortschritt ++;
                        xmlAnalyzer.extractor(document);
                    }
                }
            }
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    /**
     * erstellt von Feride Yilmaz
     *
     * Methode lädt Stammdaten.zip aus der Webseite und speichert es im Ordner
     * src/main/resources/stammdaten/
     */
    private static void downloadStammdaten(){
        String linkURL = "https://www.bundestag.de/services/opendata";
        try {
            Document document = Jsoup.connect(linkURL).get();
            Elements zipLinks = document.select("a[href$=MdB-Stammdaten.zip]");
            for(Element link : zipLinks){
                String href = link.attr("abs:href");
                URL url = new URL(href);
                String zipDatei = "MdB-Stammdaten.zip";
                try (InputStream inputStream = url.openStream()){
                    Files.copy(inputStream, Paths.get("src/main/resources/stammdaten/"+zipDatei),
                            StandardCopyOption.REPLACE_EXISTING);
                    extractZip.entpackeZip();  // entpacke zip Ordner
                }
            }


        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
