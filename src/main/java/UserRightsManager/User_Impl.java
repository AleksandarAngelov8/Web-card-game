// alles in dieser Klasse ist erstellt von Angelov
package UserRightsManager;

import Interface.User;

public class User_Impl implements User {
    /**
     * @param username neue Nutzername
     */
    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    private String username;

    /**
     * @param password neue Passwort
     */
    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    private String password;

    /**
     * @param rights neue rechte
     */
    @Override
    public void setRights(int rights) {
        this.rights = rights;
    }

    private int rights;// 0-User, 1-Manager, 2-Admin
    private String sessionToken;

    /**
     * erstelle User Objekt
     * @param username
     * @param password
     * @param admin
     */
    public User_Impl(String username, String password, int admin) {
        this.username = username;
        this.password = password;
        this.rights = admin;
    }

    /**
     * überprüfe das Passwort
     * @param passwordAttempt
     * @return
     */
    @Override
    public boolean authenticate(String passwordAttempt) {
        return password.equals(passwordAttempt);
    }

    /**
     * prüfe ob Adminrechte vorhanden sind
     *
     * @return admin
     */
    @Override
    public int hasAdminRights(){
        return rights;
    }

    /**aktiviere eine Session und speichere Sessontoken
     * @param sessionToken eingegebenes sessiontoken
     */
    @Override
    public void activateSession(String sessionToken){
        this.sessionToken = sessionToken;
    }

    /**
     * überprüfe das Sessiontoken
     * @param checkSessionToken eingegebenes sessiontoken
     * @return
     */
    @Override
    public boolean checkSessionToken(String checkSessionToken){
        return sessionToken.equals(checkSessionToken);
    }

    /**
     * @return username
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * @return password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * @return Adminrechte
     */
    @Override
    public int getRights() {
        return rights;
    }
}
