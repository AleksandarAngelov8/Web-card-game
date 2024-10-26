package data;

import Interface.Protokoll;
import Interface.Rede;
import MongoDBConnectionHandler.MongoDBHandler;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import xml_Impl.Abgeordneter_xml_Impl;
import xml_Impl.Protokoll_xml_Impl;
import xml_Impl.Rede_xml_Impl;
import Interface.Abgeordneter;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

import static MongoDBConnectionHandler.MongoDBHandler.ExistiertBereits;

public class xmlAnalyzer {

    /**
     * erstellt von Feride Yilmaz
     *
     * Methode analysiert die Protokolle
     * @param document Dokument, was die Protokolle enthält
     * @return JA, falls Dokument noch nicht in der Datenbank ist & importiert wird
     * NEIN, falls Dokument schon in der Datenbank existiert
     */

    public static String extractor(org.w3c.dom.Document document) {
        List<Protokoll> protokolle = new ArrayList<>();
        List<Rede> redenListe = new ArrayList<>();

        document.getDocumentElement().normalize();  // entfernt Leerzeichen

        // allgemeine Information zum Protokoll
        String wahlperiode = document.getDocumentElement().getAttribute("wahlperiode");
        String ort = document.getDocumentElement().getAttribute("sitzung-ort");
        String sitzungsNummer = document.getDocumentElement().getAttribute("sitzung-nr");
        String sDatum = document.getDocumentElement().getAttribute("sitzung-datum");
        String start = document.getDocumentElement().getAttribute("sitzung-start-uhrzeit");
        String ende = document.getDocumentElement().getAttribute("sitzung-ende-uhrzeit");
        System.out.println(sitzungsNummer);

        NodeList redeNodes = document.getElementsByTagName("rede");

        String ProtokollTitel = "Plenarprotokoll der " + sitzungsNummer + ". Sitzung";

        Date dDatum = dateCheck.dateCheck(sDatum);

        for (int i = 0; i < redeNodes.getLength(); i++) {
            Element redeElement = (Element) redeNodes.item(i);

            // Überprüfen, ob redeElement null ist
            if (redeElement != null && redeElement.hasAttribute("id")) {
                String redeID = redeElement.getAttribute("id");

                Element rednerElement = (Element) redeElement.getElementsByTagName("redner").item(0);
                // Überprüfen, ob rednerElement null ist
                if (rednerElement != null) {
                    String rednerID = rednerElement.getAttribute("id");

                    Element nameElement = (Element) rednerElement.getElementsByTagName("name").item(0);
                    // Überprüfen, ob nameElement null ist
                    if (nameElement != null) {
                        String vorname = getNodeTextContent(nameElement, "vorname");
                        String nachname = getNodeTextContent(nameElement, "nachname");

                        String titel = getNodeTextContent(nameElement, "titel");

                        String redeText = extractRedeText(redeElement)[0];
                        String kommentare = extractRedeText(redeElement)[1];
                        String fraktion = extractFraktion(redeElement);

                        Abgeordneter abgeordneter = new Abgeordneter_xml_Impl(rednerID, nachname,
                                vorname, titel, fraktion);

                        int redelaenge = redeText.length();
                        int kommentarlaenge = kommentare.length();

                        Rede rede = new Rede_xml_Impl(rednerID, redeID, redeText, redelaenge, kommentare,
                                kommentarlaenge, sitzungsNummer, wahlperiode, dDatum, fraktion, abgeordneter);

                        redenListe.add(rede);
                    }
                }
            }
        }
        Object id = sitzungsNummer+ "+"+wahlperiode;
        Protokoll protokoll = new Protokoll_xml_Impl(id, sitzungsNummer, dDatum, ProtokollTitel,
                wahlperiode, ort, redenListe, start, ende);

        protokolle.add(protokoll);

        MongoCollection<Document> collection = MongoDBHandler.getCollectionName("protocol");
        String erg;
        if (!ExistiertBereits(sitzungsNummer, collection)) {
            erg = "JA";
        } else {
            erg = "NEIN";
        }

        // Daten werden in MongoDB aufgefüllt
        MongoDBHandler connectionHandler = new MongoDBHandler();
        connectionHandler.insertProtocol(protokolle);
        connectionHandler.insertRede(redenListe);

        return erg;
    }

    // Hilfsmethode zur Überprüfung und Extraktion des Textinhalts eines Elements
    private static String getNodeTextContent(Element element, String tagName) {
        String textContent = "";
        Element childElement = (Element) element.getElementsByTagName(tagName).item(0);
        if (childElement != null) {
            textContent = childElement.getTextContent();
        }
        return textContent;
    }




    /**
     * erstellt von Feride Yilmaz
     *
     * Methode extrahiert Rede & Kommentar aus einem Redner-Element
     * @param rednerElement Das Element, aus dem Rede & Kommentar extrahiert werden soll
     * @return String-Array
     * 1. Stelle: redeTex (die Rede ohne Kommentare)
     * 2. Stelle: kommentarText (Kommentare, die während einer Rede gemacht wurden)
     * Credits: Feride Yilmaz, Übungsblatt 3
     */
    private static String[] extractRedeText(Element rednerElement) {
        StringBuilder redeText = new StringBuilder();
        StringBuilder kommentarText = new StringBuilder();
        NodeList pList = rednerElement.getElementsByTagName("p");

        for (int n = 0; n < pList.getLength() - 1; n++) {
            Element pElement = (Element) pList.item(n);
            String klasse = pElement.getAttribute("klasse");
            if (!"redner".equals(klasse)) {
                String inhalt = pElement.getTextContent();
                redeText.append(inhalt);
            }
        }
        NodeList kommentarList = rednerElement.getElementsByTagName("kommentar");
        for (int n = 0; n < kommentarList.getLength(); n++) {
            Element kommentarElement = (Element) kommentarList.item(n);
            String kommentar = kommentarElement.getTextContent();
            kommentarText.append(kommentar);
        }
        String[] result = new String[2];
        result[0] = redeText.toString();
        result[1] = kommentarText.toString();
        return result;
    }

    /**
     * erstellt von Feride Yilmaz
     *
     * Fraktion des Redners wird extrahiert
     * @param rednerElement Element, aus dem Fraktion extrahiert werden soll
     * @return Fraktion
     */
    private static String extractFraktion(Element rednerElement) {
        NodeList fraktionList = rednerElement.getElementsByTagName("fraktion");
        if (fraktionList.getLength() > 0) {
            String fraktion = fraktionList.item(0).getTextContent().trim();
            if (fraktion.equals("null")) {
                return "";
            } else if (fraktion.equals("fraktionslos")) {
                return "Fraktionslos";
            }
            return fraktion;
        } else {
            return "";
        }
    }



}

