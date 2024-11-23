import JettyServer.JettyServer;
import MongoDBConnectionHandler.MongoDBHandler;
import NodeJsServer.NodeJsServerRunner;
import RouteHandler.RouteHandler;
import UserRightsManager.UserRightsManager;

public class Main {
    public static void main(String[] args) throws Exception {
        MongoDBHandler connectionHandler = new MongoDBHandler();
        UserRightsManager userRightsManager = new UserRightsManager(connectionHandler);
        RouteHandler.SetupRoutes(userRightsManager);
        //could use this instead of Jetty
        //NodeJsServerRunner.Run();
        //userRightsManager.printUserInfo();

        JettyServer js = new JettyServer();
        js.start();
    }
}
