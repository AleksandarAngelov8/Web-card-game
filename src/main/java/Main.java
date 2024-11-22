import MongoDBConnectionHandler.MongoDBHandler;
import RouteHandler.RouteHandler;
import UserRightsManager.UserRightsManager;

public class Main {
    public static void main(String[] args) {
        MongoDBHandler connectionHandler = new MongoDBHandler();;
        UserRightsManager userRightsManager = new UserRightsManager(connectionHandler);
        RouteHandler.SetupRoutes(userRightsManager);
        userRightsManager.printUserInfo();
    }
}
