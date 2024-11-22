package MongoDBConnectionHandler;
import UserRightsManager.User;
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.*;
import org.bson.Document;

import java.io.InputStream;
import java.util.*;


public class MongoDBHandler {
    public MongoDatabase database;
    private static MongoCollection<Document> collectionUsers;
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
    public MongoDBHandler() {
        Properties properties = getMongoDBProperties();

        ConnectionString connectionString = new ConnectionString("mongodb://"+
                properties.getProperty("host")+":"+
                properties.getProperty("port"));

        MongoClient mongoClient = MongoClients.create(connectionString);

        database = mongoClient.getDatabase("web_testing");
        collectionUsers = database.getCollection("users");
    }
    /**
     * erstellt von Angelov
     * liefert die informationen der Benutzer von DB
     * @return die Benutzer von DB
     */
    public List<User> getUsers(){
        List<User> userList = new ArrayList<>();

        for (Document userDocument : collectionUsers.find()) {
            String username = userDocument.getString("_id");
            String password = userDocument.getString("password");
            int admin = userDocument.getInteger("rights");

            userList.add(new User(username,password,admin));
        }
        return userList;
    }
    public boolean addUserToDB(User user){
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
}