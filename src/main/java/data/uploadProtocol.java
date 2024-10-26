package data;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class uploadProtocol {

    /**
     * erstellt von Feride Yilmaz
     *
     * Methode überprüft, ob die angegebene URL gültig ist und zu einer xml-Datei führt
     * @param x Das zu überprüfende URL
     * @return Bestätigung, ob URL gültig ist oder nicht
     */
    public static String xmlFormat(String x) {
        try {
            URL url = new URL(x);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();  // verbindung zur URL
            connection.setRequestMethod("GET");  // Anfrage ist von Typ GET
            connection.connect();


            int code = connection.getResponseCode(); // Antwortcode vom Server
            if (code == 200) {
                String format = connection.getContentType(); // Inhaltstyp
                if (format.toLowerCase().contains("xml")) {
                    return "XML";
                }
            }
            return "URL IST UNGÜLTIG!   ";

        } catch (MalformedURLException e){
            return "FEHLER BEI URL ERSTELLUNG!  ";
        } catch (IOException e) {
            return "VERBINDUNGSFEHLER!  ";  // Fehler bei der Verbindung
        }
    }

    /**
     * erstellt von Feride Yilmaz
     *
     * Methode lädt Protokoll hoch, wenn URL gültig ist
     * @param xmlURL URL, die das Protokoll enthält
     * @return Bestätigung, ob das Protokoll hochgeladen wurde oder bereits existiert
     * bei Fehlermeldung {@code null}
     */
    public static String uploadXMLPROTOCOL(String xmlURL) {
        String format = xmlFormat(xmlURL);
        if (format.equals("XML")) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            DocumentBuilder builder = null;
            try {
                factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                // Deaktiviere DTD-Validierung
                factory.setFeature("http://xml.org/sax/features/validation", false);
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                throw new RuntimeException(e);
            }
            factory.setValidating(false);


            try (InputStream inputStream = new URL(xmlURL).openStream()) {
                Document document = builder.parse(inputStream);
                String erg = xmlAnalyzer.extractor(document);
                return erg;
            } catch (SAXException | IOException e) {
                return "null";
            }

        }return format;
    }
}

