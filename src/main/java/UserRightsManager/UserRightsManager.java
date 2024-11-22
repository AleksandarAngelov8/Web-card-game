// alles in dieser Klasse ist erstellt von Angelov

package UserRightsManager;

import MongoDBConnectionHandler.MongoDBHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRightsManager {
    public Map<String, User> getUsers() {
        return users;
    }
    public Map<String, User> getOnlineUsers() {
        Map<String,User> onlineUsers = new HashMap<String, User>();
        for (String user: users.keySet()){
            if(users.get(user).IsOnline()) onlineUsers.put(user,users.get(user));
        }
        return onlineUsers;
    }
    private Map<String,User> users = new HashMap<>();

    public String getCurrentSessionToken() {
        return currentSessionToken;
    }

    public void setCurrentSessionToken(String currentSessionToken) {
        this.currentSessionToken = currentSessionToken;
    }

    private String currentSessionToken;
    /**
     * lade die Benutzer von DB
     * @param connectionHandler connection handler
     */
    public UserRightsManager(MongoDBHandler connectionHandler) {
        System.out.println("User list:");
        List<User> userList = connectionHandler.getUsers();
        for (User user: userList){
            users.put(user.getUsername(),user);
        }
        currentSessionToken = "";
        printUserInfo();
    }

    /**
     * führe authentikation aus
     * @param username username
     * @param password passwort
     * @param sessionToken sessiontoken
     * @return ob sie übereinstimmen
     */
    public boolean authenticate(String username, String password, String sessionToken) {
        User user = users.get(username);
        if (user != null && user.authenticate(password)){
            user.activateSession(sessionToken);
            return true;
        }
        return false;
    }

    /**
     * @param username username
     * @param sessionToken sessiontoken
     * @return ob adminrechte vorhanden sind
     */
    public boolean sessionHasAdminRights(String username, String sessionToken) {
        User user = users.get(username);
        return (user.hasAdminRights() == 2)
                && user.checkSessionToken(sessionToken);
    }
    public boolean sessionHasManagerRights(String username, String sessionToken) {
        User user = users.get(username);
        return (user.hasAdminRights() == 1)
                && user.checkSessionToken(sessionToken);
    }

    /**
     * erstelle neue Benutzer
     * @param username username
     * @param password passwort
     * @param admin adminrechte
     * @param mongoDBHandler mongodb handler
     * @return erfolg
     */
    public boolean addNewUserToDB(String username, String password, int admin, MongoDBHandler mongoDBHandler){
        if (users.containsKey(username)){
            System.out.println("User with this name already exists!");
            return false;
        }
        User newUser = new User(username,password,admin);
        users.put(username,newUser);
        return mongoDBHandler.addUserToDB(newUser);
    }

    /**
     * ändere die rechte von benutzer
     * @param username username
     * @param admin adminrechte
     * @param mongoDBHandler mongodb handler
     * @return erfolg
     */
    public boolean changeRightsOfUser(String username, int admin, MongoDBHandler mongoDBHandler){
        if (!users.containsKey(username)){
            System.out.println("User with this name does NOT exists!");
            return false;
        }

        if (mongoDBHandler.changeRightsOfUser(username, admin)){
            users.get(username).setRights(admin);
            return true;
        }
        return false;
    }

    /**
     * ändere das passwort von benutzer
     * @param username username
     * @param newPassword neues passwort
     * @param mongoDBHandler mongodb handler
     * @return erfolg
     */
    public boolean changePasswordOfUser(String username, String newPassword, MongoDBHandler mongoDBHandler){
        if (!users.containsKey(username)){
            System.out.println("User with this name does NOT exists!");
            return false;
        }
        if (mongoDBHandler.changePasswordOfUser(username, newPassword)){
            users.get(username).setPassword(newPassword);
            return true;
        }
        return false;
    }

    /**ändere das username
     * @param username username
     * @param newUsername neue username
     * @param mongoDBHandler mongodb handler
     * @return erfolg
     */
    public boolean changeUsernameOfUser(String username, String newUsername, MongoDBHandler mongoDBHandler){
        if (!users.containsKey(username)){
            System.out.println("User with the name "+username+" does NOT exists!");
            return false;
        }
        if (users.containsKey(newUsername)){
            System.out.println("User with the name "+newUsername+" already exists!");
            return false;
        }
        if (mongoDBHandler.changeUsernameOfUser(username, newUsername)){
            users.get(username).setUsername(username);
            User newUser = users.get(username);
            newUser.setUsername(newUsername);
            users.remove(username);
            users.put(newUsername,newUser);
            return true;
        }
        return false;
    }

    /**
     * entfernt Benutzer
     * @param username nutzername
     * @param mongoDBHandler mongodbhandler Objekt
     * @return erfolg
     */
    public boolean deleteUser(String username, MongoDBHandler mongoDBHandler){
        if (!users.containsKey(username)){
            System.out.println("User with the name "+username+" does NOT exists!");
            return false;
        }
        if (mongoDBHandler.deleteUser(username)){
            users.get(username).setUsername(username);
            users.remove(username);
            return true;
        }
        return false;
    }

    /**
     * ausgibt Benutzerinformationen
     */
    public void printUserInfo(){
        for (String user: users.keySet()){
            System.out.print("Username: " + user);
            System.out.print("\tPassword: " + users.get(user).getPassword());
            System.out.println("\tRights: " + users.get(user).getRights());
        }
    }

}
