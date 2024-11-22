package MongoDBConnectionHandler;
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.*;
import org.bson.Document;

import java.io.InputStream;
import java.util.*;


public class MongoDBHandler {
    public MongoDatabase database;
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
    }
}