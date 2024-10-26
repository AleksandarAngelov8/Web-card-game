package Interface;

import org.apache.uima.UIMAException;

import java.io.IOException;
import java.sql.Date;

public interface Rede{
    Object getID();

    Object getRednerID();
    Abgeordneter getRedner();
    String getText();
    Integer getRedelaenge();
    Integer getKommentarLaenge();

    Protokoll getProtokoll();
    String getKommentare();
    String getSitzung();
    String getWahlperiode();

    Date getDate();
    String getFraktion()  throws  NullPointerException;
    /**
     * erstellt CAS-Objekt
     */
    void createJCas();
    void loadJCasFromDB() throws UIMAException, IOException;
    CAS getCAS();
}

