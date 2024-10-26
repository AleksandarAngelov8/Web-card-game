package MongoDBConnectionHandler;
import Interface.*;
import MongoDB_Impl.Abgeordneter_MongoDB_Impl;
import MongoDB_Impl.Rede_MongoDB_Impl;
import UserRightsManager.User_Impl;
import com.mongodb.*;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.apache.uima.UIMAException;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import com.mongodb.client.result.UpdateResult;
import xml_Impl.Abgeordneter_xml_Impl;
import xml_Impl.Rede_xml_Impl;

/**
 * erstellt von Feride Yilmaz
 *
 * Klasse gewährleistet Kommunikation
 * mittels MongoDB
 */

public class MongoDBHandler {
    public static MongoCollection<Document> getCollectionSpeech() {
        return collectionSpeech;
    }

    private static MongoCollection<Document> collectionSpeech;

    public static MongoCollection<Document> getCollectionProtocol() {
        return collectionProtocol;
    }

    private static MongoCollection<Document> collectionProtocol;

    public static MongoCollection<Document> getCollectionSpeaker() {
        return collectionSpeaker;
    }

    private static MongoCollection<Document> collectionSpeaker;

    public static MongoCollection<Document> getCollectionCAS() {
        return collectionCAS;
    }

    private static MongoCollection<Document> collectionCAS;
    private static MongoCollection<Document> collectionUsers;

    /**
     * lädt Verbindungsinfromation zur MongoDB
     * @return properties Object mit Verbindungsinformationen
     */
    private static Properties getMongoDBProperties() {
        InputStream inputStream = MongoDBHandler.class.getClassLoader().getResourceAsStream("dbConnection/mongoDBConnectionHandler.properties");
        Properties properties = new Properties();
        try {
            properties.load(inputStream);

        } catch (Exception var) {
            var.printStackTrace();
        }
        return properties;
    }

    /**
     * erstellt von Feride Yilmaz
     *
     * Methode zur Erstellung der MongoDB Verbindung
     * Erstellt Collection, falls noch nicht vorhanden
     */
    public MongoDBHandler() {
        Properties properties = getMongoDBProperties();

        MongoCredential credential = MongoCredential.createCredential(
                properties.getProperty("remote_user"),
                properties.getProperty("remote_database"),
                properties.getProperty("remote_password").toCharArray()
        );


        MongoClientOptions options = MongoClientOptions.builder()
                .serverSelectionTimeout(200000).build();
        MongoClient mongoClient = new MongoClient(
                new ServerAddress(properties.getProperty("remote_host"),
                        Integer.parseInt(properties.getProperty("remote_port"))),
                credential,
                options
        );

        MongoDatabase database = mongoClient.getDatabase(properties.getProperty("remote_database"));
        // Verbindung zur Datenbank
        erstelleCollection(database,"protocol");
        erstelleCollection(database, "speaker");
        erstelleCollection(database, "speech");
        erstelleCollection(database, "cas");
        erstelleCollection(database, "users");

        this.collectionProtocol = database.getCollection("protocol");
        this.collectionSpeaker = database.getCollection("speaker");
        this.collectionSpeech = database.getCollection("speech");
        this.collectionCAS = database.getCollection("cas");
        this.collectionUsers = database.getCollection("users");
    }

    /**
     * erstellt von Feride Yilmaz
     *
     * Methode zur Erstellung von Collection,
     * falls Collection nicht vorhanden
     * @param database
     * @param collection
     */
    public void erstelleCollection(MongoDatabase database, String collection){
        if (!database.listCollectionNames().into(new ArrayList<>()).contains(collection)) {
            database.createCollection(collection);
        }
    }

    /**
     * erstellt von Feride Yilmaz
     *
     * Methode um auf die Collection zuzugreifen
     * @param collectionName
     * @return zugehörige Collection
     */
    public static MongoCollection<Document> getCollectionName(String collectionName) {
        switch (collectionName) {
            case "protocol":
                return collectionProtocol;
            case "speaker":
                return collectionSpeaker;
            case "speech":
                return collectionSpeech;
            case "cas":
                return collectionCAS;
            case "users":
                return collectionUsers;
            default:
                throw new IllegalArgumentException("Ungültige Eingabe");
        }
    }

    /**
     * erstellt von Feride Yilmaz
     *
     * Methode zum erstellen eines Dokuments in DB, falls es nicht vorhanden ist
     * @param eingabeCollection In diese Collection wird das Dokument eingefügt
     * @param document Das Dokument was eingefügt werden soll
     */
    public void erstellen(String eingabeCollection, Document document) {
        MongoCollection<Document> collection = getCollectionName(eingabeCollection);
        if (!ExistiertBereits(document.get("_id"), collection)) {
            collection.insertOne(document);
        }
    }

    /**
     * erstellt von Feride Yilmaz
     *
     * Methode zum lesen Dokument aus DB mit Filterdokument
     * @param eingabeCollection hier wird gesucht
     * @param document  Filterdokument
     * @return gelesenes Dokument oder {@code null} falls Dokuemnt nicht gefunden wird
     */
    public Document lesen(String eingabeCollection, Document document){
        MongoCollection<Document> collection = getCollectionName(eingabeCollection);
        if (ExistiertBereits(document.get("_id"), collection)) {
            return collection.find(document).first();
        } else {
            System.out.println("Das Dokument wurde nicht in der Datenbank gefunden.");
            return null;
        }
    }


    /**
     * erstellt von Feride Yilmaz
     *
     * Methode aktualisiert Dokument in DB
     * @param eingabeCollection hier wird das Dokument aktualisiert
     * @param filter Filter, um Dokument zu finden
     * @param updateDocument Das aktuelle Dokument
     */
    public static void update(String eingabeCollection, Document filter, Document updateDocument) {
        MongoCollection<Document> collection = getCollectionName(eingabeCollection);
        UpdateResult result = collection.updateOne(filter, new Document("$set", updateDocument));
        if (result.getModifiedCount() == 0) {
            System.out.println("Dokument wurde nicht aktualisiert.");
        } else {
            System.out.println("Dokument wurde erfolgreich aktualisiert.");
        }
    }



    /**
     * erstellt von Feride Yilmaz
     *
     * Methode zum löschen von Dokument in DB,
     * falls Dokument in DB ist
     * @param eingabeCollection
     * @param document
     */
    public void loeschen(String eingabeCollection, Document document){
        MongoCollection<Document> collection = getCollectionName(eingabeCollection);
        if (ExistiertBereits(document.get("_id"), collection)) {
            collection.deleteOne(document);
        }
    }


    /**
     * erstellt von Feride Yilmaz
     *
     * Methode zum aggregieren
     * @param eingabeCollection
     * @param liste
     * @return
     */

    public static AggregateIterable<Document> aggregieren(String eingabeCollection, List<Bson> liste){
        MongoCollection<Document> collection = getCollectionName(eingabeCollection);
        return collection.aggregate(liste);
    }




    /**
     * Methode gibt Anzahl der Dokumente an
     * @param eingabeCollection
     * @param filter Filter Dokument für die Suche
     * @return Anzahl Dokument die edem Filtern entsprechen
     */
    public long zaehlen(String eingabeCollection, Document filter){
        MongoCollection<Document> collection = getCollectionName(eingabeCollection);
        return collection.countDocuments(filter);
    }





    /**
     * erstellt von Angelov
     * fügt casObjekt in DB hinzu
     * @param cas cas-Objekt
     */
    public void insertCAS(CAS cas){
        Object redeId = cas.getRede().getID();
        if (collectionCAS.find(eq("_id",redeId)).first() == null){
            System.out.println("CAS von Rede: " + redeId + ", existiert schon in DB!");
            return;
        }
        Document document = new Document("_id",redeId).append("CAS",cas.getSerializedCas());
        collectionCAS.insertOne(document);
    }

    /**
     * erstellt von Feride Yilmaz
     *
     * füllt Datenbank mit Protokollen,
     * falls Protokoll nicht bereits in MongoDB ist
     * @param protokollListe
     */
    public static void insertProtocol(List<Protokoll> protokollListe) {
        List<Document> documents = new ArrayList<>();
        for (Protokoll protokoll : protokollListe) {
            if (!ExistiertBereits(protokoll.getID(), collectionProtocol)){
                Document document = new Document("_id", protokoll.getID())
                        .append("Sitzungsnummer", protokoll.getSitzungsnummer())
                        .append("Titel", protokoll.getTitel())
                        .append("Ort", protokoll.getOrt())
                        .append("Datum", protokoll.getDatum())
                        .append("Wahlperiode", protokoll.getWahlperiode())
                        .append("Start", protokoll.getStart())
                        .append("Ende", protokoll.getEnde())
                        .append("Reden", protokoll.getReden().stream().map(rede -> new
                                Document("RednerID", rede.getRednerID())
                                .append("RedeID", rede.getID())
                                .append("Text", rede.getText())).collect(Collectors.toList()));
                documents.add(document);
            }
        }
        if (!documents.isEmpty()) {
            collectionProtocol.insertMany(documents);
        }
    }
    /**
     * erstellt von Feride Yilmaz
     *
     * füllt Datenbank mit Reden,
     * falls es nicht bereits in MongoDB ist
     * @param redeListe
     */
    public void insertRede(List<Rede> redeListe) {
        List<Document> documents = new ArrayList<>();
        for (Rede rede : redeListe) {
            if (!ExistiertBereits(rede.getID(), collectionSpeech)){
                Document document = new Document("_id", rede.getID())
                        .append("ID", rede.getRednerID())
                        .append("redner", new Document("id", ((Abgeordneter) rede.getRedner()).getID())
                                .append("name", ((Abgeordneter) rede.getRedner()).getName())
                                .append("vorname", ((Abgeordneter) rede.getRedner()).getVorname()))
                        .append("rede", rede.getText())
                        .append("rede_laenge", rede.getRedelaenge())
                        .append("kommentare", rede.getKommentare())
                        .append("kommentar_laenge", rede.getKommentarLaenge())
                        .append("datum", rede.getDate())
                        .append("fraktion", rede.getFraktion())
                        .append("Sitzung", rede.getSitzung())
                        .append("wahlperiode" , rede.getWahlperiode());
                documents.add(document);
            }
        }
        if (!documents.isEmpty()) {
            collectionSpeech.insertMany(documents);
        }
    }

    /**
     * erstellt von Feride Yilmaz
     *
     * füllt Datenbank mit Rednern,
     * falls Protokoll nicht bereits in MongoDB ist
     * @param abgeordneteListe
     */
    public static void insertAbgeordneter(List<Abgeordneter> abgeordneteListe){
        List<Document> documents = new ArrayList<>();
        for (Abgeordneter abgeordneter : abgeordneteListe){
            if(!ExistiertBereits(abgeordneter.getID(), collectionSpeaker)){
                Document document = new Document("_id", abgeordneter.getID())
                        .append("nachname", abgeordneter.getName())
                        .append("vorname", abgeordneter.getVorname())
                        .append("geschlecht", abgeordneter.getGeschlecht())
                        .append("titel", abgeordneter.getAkadTitel())
                        .append("anrede", abgeordneter.getAnrede())
                        .append("beruf", abgeordneter.getBeruf())
                        .append("partei", abgeordneter.getPartei())
                        .append("vita", abgeordneter.getVita())
                        .append("geburtsdatum", abgeordneter.getGeburtsDatum())
                        .append("geburtsort", abgeordneter.getGeburtsOrt())
                        .append("ortszusatz", abgeordneter.getOrtszusatz())
                        .append("religion", abgeordneter.getReligion())
                        .append("adelssuffix", abgeordneter.getAdelssuffix())

                ;documents.add(document);
            }
        }
        if (!documents.isEmpty()) {
            collectionSpeaker.insertMany(documents);
        }
    }

    /**
     * erstellt von Feride Yilmaz
     *
     * überprüft, ob ein Dokument mit der ID
     * beriets der angegebenen Datenabnk ist
     * @param ID
     * @param collection
     * @return {@code true},falls es in der Datenabnk ist
     * sont {@code false}
     */
    public static boolean ExistiertBereits(Object ID, MongoCollection<Document> collection) {
        String sID = ID.toString();
        Document abgeordneterExistiert = collection.find(eq("_id", sID)).first();
        return abgeordneterExistiert != null;
    }

    /**
     * erstellt von Angelov
     * liefert die informationen der Benutzer von DB
     * @return die Benutzer von DB
     */
    public List<User_Impl> getUsers(){
        List<User_Impl> userList = new ArrayList<>();

        for (Document userDocument : collectionUsers.find()) {
            String username = userDocument.getString("_id");
            String password = userDocument.getString("password");
            int admin = userDocument.getInteger("rights");

            userList.add(new User_Impl(username,password,admin));
        }
        return userList;
    }

    /**
     * erstelle neue Benutzer
     * @param user User Objekt
     * @return erfolg
     */
    public boolean addUserToDB(User_Impl user){
        try{
            collectionUsers.insertOne(new Document()
                    .append("_id",user.getUsername())
                    .append("password",user.getPassword())
                    .append("rights",user.getRights()));
        }
        catch (MongoWriteException exception){
            System.out.println("Username already used!");
            return false;
        }
        return true;
    }
    /**
     * erstellt von Angelov
     * ändere die rechte von benutzer
     * @param username username
     * @param admin adminrechte
     * @return erfolg
     */
    public boolean changeRightsOfUser(String username, int admin){
        try {
            Document filter = new Document("_id", username);

            Document update = new Document("$set", new Document("rights", admin));

            collectionUsers.updateOne(filter, update);

            System.out.println("Rights of user " + username + " changed successfully.");
            return true;
        } catch (Exception e) {
            System.err.println("Error changing rights of user " + username + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * erstellt von Angelov
     * ändere das passwort von benutzer
     * @param username username
     * @param newPassword neues passwort
     * @return erfolg
     */
    public boolean changePasswordOfUser(String username, String newPassword){
        try {
            Document filter = new Document("_id", username);

            Document update = new Document("$set", new Document("password", newPassword));

            collectionUsers.updateOne(filter, update);
            System.out.println("Password of user " + username + " changed successfully.");
            return true;
        } catch (Exception e) {
            System.err.println("Error changing password of user " + username + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * erstellt von Angelov
     * ändere das username
     * @param username username
     * @param newUsername neue username
     * @return erfolg
     */
    public boolean changeUsernameOfUser(String username, String newUsername){
        try {
            Document oldDocument = collectionUsers.find(new Document("_id", username)).first();

            if (oldDocument != null) {
                collectionUsers.deleteOne(oldDocument);

                oldDocument.put("_id", newUsername);

                collectionUsers.insertOne(oldDocument);

                System.out.println("Username of user " + username + " changed successfully to " + newUsername);
                return true;
            } else {
                System.out.println("User with username " + username + " not found.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error changing username of user " + username + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * erstellt von Angelov
     * liefert documentobjekt von einer rede
     * @param id
     * @return
     */
    public Document getRedeDocument(String id){
        return collectionSpeech.find(new Document("_id", id)).first();
    }

    /** erstellt von Angelov
     * liefert reden von einem datum bis einem anderen datum
     * @param from Datumanfang
     * @param to Datumende
     * @return reden von from bis to
     */
    public List<Rede> getRedenFromDBWithoutCAS(LocalDate from, LocalDate to){
        MongoCollection<Document> collection = collectionSpeech;
        Bson filter = Filters.and(Filters.gte("datum", from), Filters.lte("datum", to));
        List<Rede> reden = new ArrayList<>();
        int i = 0;
        for (Document doc : collection.find(filter)) {
            System.out.println("Speech number: " + i++ + " - " + doc.getString("_id"));
            String id = doc.getString("_id");
            String rednerId = ((Document)doc.get("redner")).getString("id");
            String text = doc.getString("rede");
            Rede rede = new Rede_xml_Impl(rednerId,id,text);
            reden.add(rede);
        }
        return reden;
    }
    /** erstellt von Angelov
     * liefert reden von einem datum bis einem anderen datum mit geladenen CAS-Objekte
     * @param from Datumanfang
     * @param to Datumende
     * @return reden von from bis to
     */
    public List<Rede> getRedenFromDBWithCAS(LocalDate from, LocalDate to){
        MongoCollection<Document> collection = collectionSpeech;
        Bson filter = Filters.and(Filters.gte("datum", from), Filters.lte("datum", to));
        List<Rede> reden = new ArrayList<>();
        int i = 0;
        long total = collection.countDocuments(filter);
        for (Document doc : collection.find(filter)) {
            System.out.println("Speech number: " + i++ + "/"+ total + " - " + doc.getString("_id"));
            Rede rede = new Rede_MongoDB_Impl(collectionSpeech, doc, true);
            try {
                rede.loadJCasFromDB();
            } catch (UIMAException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            reden.add(rede);
        }
        return reden;
    }

    /** erstellt von Angelov
     * liefert das Dokument Objekt von einer Rede
     * @param id id von Rede
     * @return Dokument Objekt
     */
    public static Document getRednerDocument(String id){
        return collectionSpeaker.find(new Document("_id",id)).first();
    }

    /** erstellt von Angelov
     * HINWEIS: ES WURDE DIE FUNKTION VON MUESTER_LOESUNG 3 VERWENDET FUER DIE VOLLTEXTSUCHE
     * liefert alle Reden die diese zeichenkette enthält
     * @param sValue Suchzeichenkette
     * @return eine liste mit den Ids der Reden
     */
    public static List<String> fullTextSearch(String sValue) {
        List<String> idList = new ArrayList<>();
        MongoCursor<Document> result = collectionSpeech.find(BsonDocument.parse("{ $text: { $search: \"" + sValue + "\" } }"))
                .projection(BsonDocument.parse("{ score: { $meta: \"textScore\" } }"))
                .sort(BsonDocument.parse("{ score: { $meta: \"textScore\" } }"))
                .cursor();

        result.forEachRemaining(d -> {
            idList.add(d.getString("_id"));
        });

        return idList;
    }

    /** erstellt von Angelov
     * liefert eine Liste der Redner
     * @return liste der Redner
     */
    public List<Abgeordneter> getAbgeordnete() {
        List<Abgeordneter> speakers = new ArrayList<>();

        for (Document userDocument : collectionSpeaker.find()) {
            Object oID= userDocument.get("_id");
            String sName= userDocument.getString("nachname");
            String sVorname= userDocument.getString("vorname");
            String sOrtszusatz= userDocument.getString("ortszusatz");
            String sAdelssuffix= userDocument.getString("adelssuffix");
            String sAnrede= userDocument.getString("anrede");
            String sAkadTitel= userDocument.getString("akadTitel");

            Date dGeburtsDatum= new java.sql.Date
                    (userDocument.
                            getDate("geburtsdatum").
                            getTime());
            String sGeburtsOrt= userDocument.getString("geburtsort");
            String sGeschlecht= userDocument.getString("geschlecht");
            String sReligion= userDocument.getString("religion");
            String sBeruf= userDocument.getString("beruf");
            String sVita= userDocument.getString("vita");
            String sPartei= userDocument.getString("partei");
            speakers.add(new Abgeordneter_xml_Impl(oID,sName,sVorname,sOrtszusatz,sAdelssuffix,sAnrede,
                    sAkadTitel,dGeburtsDatum,sGeburtsOrt,sGeschlecht,sReligion,sBeruf,sVita,sPartei));
        }
        return speakers;
    }

    /** erstellt von Angelov
     * entfernt Nutzer vom DB
     * @param username nutzername der nutzer
     * @return erfolg
     */
    public boolean deleteUser(String username) {
        try {
            Document document = collectionUsers.find(new Document("_id", username)).first();

            if (document != null) {
                collectionUsers.deleteOne(document);

                System.out.println("User " + username + " was deleted successfully.");
                return true;
            } else {
                System.out.println("User with username " + username + " not found.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error changing username of user " + username + ": " + e.getMessage());
            return false;
        }
    }

    /** erstellt von Angelov
     * erstellt Redner im DB
     * @param speaker redner
     * @return erfolg
     */
    public boolean createSpeaker(Abgeordneter speaker){
        try{
            collectionSpeaker.insertOne(new Document()
                    .append("_id",speaker.getID())
                    .append("nachname",speaker.getName())
                    .append("vorname",speaker.getVorname())
                    .append("ortszusatz",speaker.getOrtszusatz())
                    .append("adelssuffix",speaker.getAdelssuffix())
                    .append("anrede",speaker.getAnrede())
                    .append("akadTitel",speaker.getAkadTitel())
                    .append("geburtsdatum",speaker.getGeburtsDatum())
                    .append("geburtsort",speaker.getGeburtsOrt())
                    .append("geschlecht",speaker.getGeschlecht())
                    .append("religion",speaker.getReligion())
                    .append("beruf",speaker.getBeruf())
                    .append("vita",speaker.getVita())
                    .append("partei",speaker.getPartei())
            );
        }
        catch (MongoWriteException exception){
            System.out.println("Speaker with id "+speaker.getID()+" already exists!");
            return false;
        }
        System.out.println("Speaker created successfully!");
        return true;
    }

    /** erstellt von Angelov
     * bearbeitet einen Redner
     * @param oID ID
     * @param field field zu bearbeiten
     * @param newValue neues Wert
     * @return erfolg
     */
    public boolean editSpeaker(Object oID, String field, String newValue) {
        try {
            Document filter = new Document("_id", oID);
            if (collectionSpeaker.find(filter).first() == null) {
                System.out.println("Speaker with this ID does NOT exist!");
                return false;
            }

            if (field.equals("_id")){
                Document document = collectionSpeaker.find(filter).first();
                collectionSpeaker.deleteOne(document);

                document.put("_id", newValue);

                collectionSpeaker.insertOne(document);

                System.out.println("Id of speaker " + oID + " changed successfully to " + newValue);
                return true;
            }

            Document update = null;
            if (field.equals("geburtsdatum")) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date date = dateFormat.parse(newValue);
                update = new Document("$set", new Document(field, date));
            }
            else {
                update = new Document("$set", new Document(field, newValue));
            }

            collectionSpeaker.updateOne(filter, update);
            System.out.println(field + " of speaker with ID " + oID + " changed successfully.");
            return true;
        } catch (Exception e) {
            System.err.println("Error changing " + field + " of speaker with ID " + oID + ": " + e.getMessage());
            return false;
        }
    }

    /** erstellt von Angelov
     * entferne Redner vom DB
     * @param oID ID
     * @return erfolg
     */
    public boolean deleteSpeaker(Object oID) {
        try {
            Document document = collectionSpeaker.find(new Document("_id", oID)).first();

            if (document != null) {
                collectionSpeaker.deleteOne(document);

                System.out.println("Speaker " + oID + " was deleted successfully.");
                return true;
            } else {
                System.out.println("Speaker with id " + oID + " not found.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error deleting Speaker " + oID + ": " + e.getMessage());
            return false;
        }
    }

    /** erstellt von Angelov
     * erstelle neue rede im DB
     * @param rede rede objekt
     * @return erfolg
     */
    public boolean createSpeech(Rede rede){
        try{
            Document rednerDoc = getRednerDocument(rede.getRednerID().toString());
            if (rednerDoc == null){
                System.out.println("Speaker with that id does NOT exist!");
                return false;
            }
            collectionSpeech.insertOne(new Document()
                    .append("_id",rede.getID())
                    .append("ID",rede.getRednerID())
                    .append("redner",new Document()
                            .append("id",rednerDoc.getString("_id"))
                            .append("name",rednerDoc.getString("nachname"))
                            .append("vorname",rednerDoc.getString("vorname")))
                    .append("rede",rede.getText())
                    .append("rede_laenge",rede.getRedelaenge())
                    .append("kommentare",rede.getKommentare())
                    .append("kommentar_laenge",rede.getKommentarLaenge())
                    .append("datum",rede.getDate())
                    .append("fraktion",rede.getFraktion())
                    .append("Sitzung",rede.getSitzung())
                    .append("wahlperiode",rede.getWahlperiode())
            );
        }
        catch (MongoWriteException exception){
            System.out.println("Speech with id "+rede.getID()+" already exists!");
            return false;
        }
        System.out.println("Speech created successfully!");
        return true;
    }

    /** erstellt von Angelov
     * bearbeitet rede im DB
     * @param oID id
     * @param field field zu bearbeiten
     * @param newValue neues Wert
     * @return erfolg
     */
    public boolean editSpeech(Object oID, String field, String newValue) {
        try {
            Document filter = new Document("_id", oID);
            if (collectionSpeech.find(filter).first() == null) {
                System.out.println("Speech with this ID does NOT exist!");
                return false;
            }

            if (field.equals("_id")){
                Document document = collectionSpeech.find(filter).first();
                collectionSpeech.deleteOne(document);

                document.put("_id", newValue);

                collectionSpeech.insertOne(document);

                System.out.println("Id of speech " + oID + " changed successfully to " + newValue);
                return true;
            }

            Document update = null;
            Document updateDependant = null;
            if (field.equals("datum")) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date date = dateFormat.parse(newValue);
                update = new Document("$set", new Document(field, date));
            }
            else if (field.equals("rede")){
                update = new Document("$set", new Document(field, newValue));
                updateDependant = new Document("$set", new Document(field+"_laenge", newValue.length()));
            }
            else if (field.equals("kommentare")){
                update = new Document("$set", new Document(field, newValue));
                updateDependant = new Document("$set", new Document(field.substring(0,field.length()-1)+"_laenge", newValue.length()));
            }
            else if (field.equals("ID")){
                update = new Document("$set", new Document(field, newValue));
                Document rednerDoc = getRednerDocument(newValue);
                if (rednerDoc == null){
                    System.out.println("Speaker with that id does NOT exist!");
                    return false;
                }
                System.out.println(rednerDoc.getString("_id") + ": " + rednerDoc.get("vorname") + " " + rednerDoc.get("nachname"));
                updateDependant = new Document("$set", new Document("redner",new Document()
                        .append("id",rednerDoc.getString("_id"))
                        .append("name",rednerDoc.getString("nachname"))
                        .append("vorname",rednerDoc.getString("vorname")))
                );
            }
            else {
                update = new Document("$set", new Document(field, newValue));
            }

            collectionSpeech.updateOne(filter, update);
            if (updateDependant != null){
                collectionSpeech.updateOne(filter,updateDependant);
            }
            System.out.println(field + " of speech with ID " + oID + " changed successfully.");
            return true;
        } catch (Exception e) {
            System.err.println("Error changing " + field + " of speech with ID " + oID + ": " + e.getMessage());
            return false;
        }
    }

    /** erstellt von Angelov
     * entfernt rede vom DB
     * @param oID ID
     * @return erfolg
     */
    public boolean deleteSpeech(Object oID) {
        try {
            Document document = collectionSpeech.find(new Document("_id", oID)).first();

            if (document != null) {
                collectionSpeech.deleteOne(document);

                System.out.println("Speech " + oID + " was deleted successfully.");
                return true;
            } else {
                System.out.println("Speech with id " + oID + " not found.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error deleting Speech " + oID + ": " + e.getMessage());
            return false;
        }
    }

    /** erstellt von Angelov
     * erstelle neuen Protokol
     * @param id ID
     * @param sitzung Sitzungsnummer
     * @param datum Datum
     * @param titel titel
     * @param wahlperiode wahlperiode
     * @param ort ort
     * @param redenS Liste von reden
     * @param start Startuhrzeit
     * @param ende Endeuhrzeit
     * @return erfolg
     */
    public boolean createProtocol(String id, String sitzung, Date datum,String titel,String wahlperiode,
                                  String ort,List<String> redenS,String start,String ende){
        try{
            Document query = new Document("_id", new Document("$in", redenS ));
            Document projection = new Document("ID", 1)
                    .append("_id", 1)
                    .append("rede", 1);
            List<Document> redenDocs = collectionSpeech.find(query).projection(projection).into(new ArrayList<>());
            System.out.println("Adding "+ redenDocs.size() + " found speeches to new protocol..");
            List<Document> transformedDocs = new ArrayList<>();
            for (Document doc : redenDocs) {
                Document transformedDoc = new Document("RednerID", doc.get("ID"))
                        .append("RedeID", doc.get("_id"))
                        .append("Text", doc.get("rede"));
                transformedDocs.add(transformedDoc);
            }

            collectionProtocol.insertOne(new Document()
                    .append("_id",id)
                    .append("Sitzungsnummer",sitzung)
                    .append("Titel",titel)
                    .append("Ort",ort)
                    .append("Datum",datum)
                    .append("Wahlperiode",wahlperiode)
                    .append("Start",start)
                    .append("Ende",ende)
                    .append("Reden",transformedDocs)
            );
        }
        catch (MongoWriteException exception){
            System.out.println("Protocol with id "+id+" already exists!");
            return false;
        }
        System.out.println("Protocol created successfully!");
        return true;
    }

    /** erstellt von Angelov
     * bearbeitet Protokol im DB
     * @param oID id
     * @param field field zu bearbeiten
     * @param newValue neues Wert
     * @return erfolg
     */
    public boolean editProtokol(Object oID, String field, String newValue) {
        try {
            Document filter = new Document("_id", oID);
            if (collectionProtocol.find(filter).first() == null) {
                System.out.println("Protocol with this ID does NOT exist!");
                return false;
            }

            if (field.equals("_id")){
                Document document = collectionProtocol.find(filter).first();
                collectionProtocol.deleteOne(document);

                document.put("_id", newValue);

                collectionProtocol.insertOne(document);

                System.out.println("Id of Protocol " + oID + " changed successfully to " + newValue);
                return true;
            }

            Document update = null;
            if (field.equals("Datum")) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date date = dateFormat.parse(newValue);
                update = new Document("$set", new Document(field, date));
            }
            else if (field.equals("Reden")){
                List<String> redenSList = Arrays.asList(newValue.split(" "));
                Document query = new Document("_id", new Document("$in", redenSList ));
                Document projection = new Document("ID", 1)
                        .append("_id", 1)
                        .append("rede", 1);
                List<Document> redenDocs = collectionSpeech.find(query).projection(projection).into(new ArrayList<>());

                List<Document> transformedDocs = new ArrayList<>();
                for (Document doc : redenDocs) {
                    Document transformedDoc = new Document("RednerID", doc.get("ID"))
                            .append("RedeID", doc.get("_id"))
                            .append("Text", doc.get("rede"));
                    transformedDocs.add(transformedDoc);
                }
                update = new Document("$set",new Document().append("Reden",transformedDocs));
            }
            else {
                update = new Document("$set", new Document(field, newValue));
            }

            collectionProtocol.updateOne(filter, update);
            System.out.println(field + " of Protocol with ID " + oID + " changed successfully.");
            return true;
        } catch (Exception e) {
            System.err.println("Error changing " + field + " of Protocol with ID " + oID + ": " + e.getMessage());
            return false;
        }
    }

    /** erstellt von Angelov
     * entfernt protokol vom DB
     * @param oID ID
     * @return erfolg
     */
    public boolean deleteProtocol(Object oID) {
        try {
            Document document = collectionProtocol.find(new Document("_id", oID)).first();

            if (document != null) {
                collectionProtocol.deleteOne(document);

                System.out.println("Protocol " + oID + " was deleted successfully.");
                return true;
            } else {
                System.out.println("Protocol with id " + oID + " not found.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error deleting Protocol " + oID + ": " + e.getMessage());
            return false;
        }
    }

    /** erstellt von Angelov
     * @param id protokollid
     * @return doc-Objekt
     */
    public static Document getProtocolDocument(String id){
        return collectionProtocol.find(new Document("_id",id)).first();
    }

    /** erstellt von Angelov
     * @return liefert Map mit den Protokollen
     * Schlüssel datum, wert id des protokolls
     */
    public Map<Date, String> getProtocols(){
        Map<Date, String> protocolMap = new HashMap<>();
        int i = 0;
        long total = collectionProtocol.countDocuments();
        for (Document doc: collectionProtocol.find().projection(new Document ("_id", 1)
                .append("Datum", 1))) {
            System.out.println(i++ + "/" + total);
            String id = doc.getString("_id");
            java.util.Date date = doc.getDate("Datum");
            protocolMap.put(new java.sql.Date(date.getTime()),id);
        }
        return protocolMap;
    }
}