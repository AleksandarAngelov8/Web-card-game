package data;

import Interface.Abgeordneter;
import MongoDBConnectionHandler.MongoDBHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import xml_Impl.Abgeordneter_xml_Impl;


import java.util.HashMap;

import static data.downloadJSON.abgeordneterMETA;


//import static projekt.classes.Factory.abgeordneterList;


public class abgeordneterErstellen {

    public static List<Abgeordneter> abgeordneterList = new ArrayList<>();

    /**
     * erstellt von Feride Yilmaz
     *
     * Methode extrahiert Stammdaten.xml & importiert die Abgeordneten mit Stammdaten in die DB
     * @param xmlPath  in diesem Ordner befindet sich die Stammdaten.xml datei
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static void abgeordneterErstellen(String xmlPath) throws ParserConfigurationException, IOException, SAXException {
        try {
            // Erstellen Sie einen DocumentBuilder, um die XML-Datei zu analysieren
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(xmlPath));

            // Extrahieren Sie die Abgeordneten aus der XML-Datei und erstellen Sie sie
            Element tags = document.getDocumentElement();

            NodeList list = tags.getElementsByTagName("MDB");  // alle Elemenete mit Tag "MDB" werden in eine NodeList geladen


            for (int i = 0; i < list.getLength(); i++) {  // NodeList wird durchlaufen
                Node node = list.item(i);
                Element eElement = (Element) node;
                if (node.getNodeType() == Node.ELEMENT_NODE) {  // falls aktueller Knoten vom Typ Element ist
                    // Stammdaten werden hier erfasst
                    Object id = eElement.getElementsByTagName("ID").item(0).getTextContent();
                    String sLabel = "Abgeordneter";
                    String Nachname = eElement.getElementsByTagName("NACHNAME").item(0).getTextContent();
                    String Vorname = eElement.getElementsByTagName("VORNAME").item(0).getTextContent();
                    String Ortszusatz = eElement.getElementsByTagName("ORTSZUSATZ").item(0).getTextContent();
                    String Adelssuffix = eElement.getElementsByTagName("ADEL").item(0).getTextContent();
                    String Anrede = eElement.getElementsByTagName("ANREDE_TITEL").item(0).getTextContent();
                    String AkadTitel = eElement.getElementsByTagName("AKAD_TITEL").item(0).getTextContent();
                    String GeburtsDatum = eElement.getElementsByTagName("GEBURTSDATUM").item(0).getTextContent();
                    Date dGeburtsDatum = dateCheck.dateCheck(GeburtsDatum);
                    String GeburtsOrt = eElement.getElementsByTagName("GEBURTSORT").item(0).getTextContent();
                    String SterbeDatum = eElement.getElementsByTagName("STERBEDATUM").item(0).getTextContent();
                    Date dSterbedatum = dateCheck.dateCheck(SterbeDatum);
                    String sGeschlecht = eElement.getElementsByTagName("GESCHLECHT").item(0).getTextContent();
                    String Religion = eElement.getElementsByTagName("RELIGION").item(0).getTextContent();
                    String Beruf = eElement.getElementsByTagName("BERUF").item(0).getTextContent();
                    String Vita = eElement.getElementsByTagName("VITA_KURZ").item(0).getTextContent().trim();
                    String sPartei = eElement.getElementsByTagName("PARTEI_KURZ").item(0).getTextContent();


                    Abgeordneter abgeordneter = new Abgeordneter_xml_Impl(id, Nachname, Vorname, Ortszusatz, Adelssuffix, Anrede, AkadTitel,
                            dGeburtsDatum, GeburtsOrt, sGeschlecht, Religion, Beruf, Vita, sPartei);
                    abgeordneterList.add(abgeordneter);


                    //connectionHandler.insertAbgeordneter(abgeordneter);


                }
            }MongoDBHandler connectionHandler = new MongoDBHandler();
            connectionHandler.insertAbgeordneter(abgeordneterList);


        } finally {

        }

}
}

