package UserRightsManager;

public class User {

    private String storedInfo;
    public void setStoredInfo(String sI){
        this.storedInfo = sI;
    }
    public String getStoredInfo() {
        return storedInfo;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    private String username;


    public void setPassword(String password) {
        this.password = password;
    }

    private String password;

    public void setRights(int rights) {
        this.rights = rights;
    }

    private int rights;// 0-User, 1-Manager, 2-Admin
    public String sessionToken;

    public User(String username, String password, int admin) {
        this.username = username;
        this.password = password;
        this.rights = admin;
        this.storedInfo = "";
    }

    public boolean authenticate(String passwordAttempt) {
        return password.equals(passwordAttempt);
    }

    public int hasAdminRights(){
        return rights;
    }

    public void activateSession(String sessionToken){
        this.sessionToken = sessionToken;
    }

    public boolean checkSessionToken(String checkSessionToken){
        return sessionToken.equals(checkSessionToken);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getRights() {
        return rights;
    }
    public boolean IsOnline(){
        return sessionToken != null;
    }
}
