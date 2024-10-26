package data;

import Interface.Abgeordneter;
import Interface.Rede;
import Interface.SozialeMedien;
import MongoDBConnectionHandler.MongoDBHandler;
import xml_Impl.Abgeordneter_xml_Impl;
import static data.xmlAnalyzer.*;
import java.util.*;
import org.bson.Document;
import static MongoDBConnectionHandler.MongoDBHandler.update;

import static data.abgeordneterErstellen.abgeordneterList;
import static data.downloadJSON.abgeordneterMETA;


public class createSpeaker {
    /**
     * erstellt von Feride Yilmaz
     *
     * Methode importiert die Metadaten der Abgeordneten in die DB
     */
    public static void createSpeakerWithMeta() {
        List<Abgeordneter> bereitsAusgegebeneReden = new ArrayList<>();
        List<Document> metadataDocuments = new ArrayList<>();
        for (Abgeordneter abgeordneter : abgeordneterList) {
            String name2;
            if (abgeordneter.getAnrede().equals("")) {
                name2 = abgeordneter.getVorname() + " " + abgeordneter.getName();
            } else {
                name2 = abgeordneter.getAkadTitel() + " " + abgeordneter.getVorname() + " " + abgeordneter.getName();
            }
            for (Abgeordneter abgeordneterMeta : abgeordneterMETA) {
                if (name2.equals(abgeordneterMeta.getVollName())) {
                    System.out.println(abgeordneterMeta.getBuro());
                    Document metadataDocument = new Document("abgeordnetenbüro", abgeordneterMeta.getBuro())
                            .append("wahlkreisbüro", abgeordneterMeta.getWahlkreiburo())
                            .append("homepage", ((SozialeMedien) abgeordneter.getMedien()).getHomepage())
                            .append("soziale_medien", new org.bson.Document("twitter", ((SozialeMedien) abgeordneterMeta.getMedien()).getTwitter())
                                    .append("instagram",((SozialeMedien) abgeordneterMeta.getMedien()).getInstagram())
                                    .append("facebook", ((SozialeMedien) abgeordneterMeta.getMedien()).getFacebook())
                                    .append("youtube", ((SozialeMedien) abgeordneterMeta.getMedien()).getYoutube())
                                    .append("telegram", ((SozialeMedien) abgeordneterMeta.getMedien()).getTelegram()))
                            .append("stand", abgeordneterMeta.getStand());

                    //Abgeordneter ab = new Abgeordneter_xml_Impl(abgeordneter.getID(),abgeordneter.getName(),abgeordneter.getVorname());
                    Document filter = new Document("_id", abgeordneter.getID());

                    update("speaker", filter,metadataDocument);

                    metadataDocuments.add(metadataDocument);
                    //bereitsAusgegebeneReden.add(ab);
                }
            }
        }

    }
}

