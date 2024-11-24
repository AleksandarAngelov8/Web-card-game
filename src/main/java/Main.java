import MongoDBConnectionHandler.MongoDBHandler;
import NodeJsServer.NodeJsServerRunner;
import RouteHandler.RouteHandler;
import UserRightsManager.UserRightsManager;

public class Main {
    public static void main(String[] args) throws Exception {
        MongoDBHandler connectionHandler = new MongoDBHandler();
        UserRightsManager userRightsManager = new UserRightsManager(connectionHandler);
        RouteHandler.SetupRoutes(userRightsManager);
        NodeJsServerRunner.Run();
        //userRightsManager.printUserInfo();

    }
}
