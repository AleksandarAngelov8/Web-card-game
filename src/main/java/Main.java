import MongoDBConnectionHandler.MongoDBHandler;
import RouteHandler.RouteHandler;

public class Main {
    public static void main(String[] args) {
        MongoDBHandler connectionHandler = new MongoDBHandler();;
        RouteHandler.SetupRoutes();

    }
}
