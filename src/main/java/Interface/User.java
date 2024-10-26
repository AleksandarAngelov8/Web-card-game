// alles in dieser Klasse ist erstellt von Angelov

package Interface;

public interface User {
    /**
     * überprüfe das Passwort
     * @param passwordAttempt eingegebenes passwort
     * @return ob sie übereinstimmen
     */
     boolean authenticate(String passwordAttempt);
    /**
     * prüfe ob Adminrechte vorhanden sind
     *
     * @return ob sie übereinstimmen
     */
     int hasAdminRights();
    /**aktiviere eine Session und speichere Sessontoken
     * @param sessionToken eingegebenes sessiontoken
     */
     void activateSession(String sessionToken);
    /**
     * überprüfe das Sessiontoken
     * @param checkSessionToken eingegebenes sessiontoken
     * @return ob sie übereinstimmen
     */
     boolean checkSessionToken(String checkSessionToken);

    /**
     * @return username
     */
     String getUsername();

    /**
     * @return passwort
     */
     String getPassword();

    /**
     * @return admin
     */
     int getRights();
     void setUsername(String username);
    /**
     * @param password neue Passwort
     */
     void setPassword(String password);

    /**
     * @param rights neue rechte
     */
     void setRights(int rights);
}
