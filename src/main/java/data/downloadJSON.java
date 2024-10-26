package data;

import Interface.Abgeordneter;
import Interface.SozialeMedien;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import xml_Impl.Abgeordneter_xml_Impl;

import static data.createSpeaker.*;

public class downloadJSON {
    public static List<Abgeordneter> abgeordneterMETA = new ArrayList<>();

    /**
     * erstellt von Feride Yilmaz
     *
     * Methode lädt JSON-Daten von der URL herunter & extrahiert Informationen aus den jeweiligen Links.
     * informationen werden in AbgeordneterMETA gespeichert
     * @throws IOException
     * @throws NullPointerException
     */
    public static void downloadJSON() throws IOException, NullPointerException {

        String urlLink = "https://www.bundestag.de/static/appdata/sitzplan/data.json";  // mit Devtools
        // enthält alle links zu den Abgeordneten Webseiten
        try {
            URL u = new URL(urlLink);
            URLConnection urlConnection = u.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String zeile;
            while ((zeile = bufferedReader.readLine()) != null) {
                stringBuilder.append(zeile);
            }
            bufferedReader.close();

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            Integer count = 0;
            String h = "https://www.bundestag.de";
            for (String id : jsonObject.keySet()) { // alle Abgeordneten werden durchlaufe
                JSONObject abgeordneter = jsonObject.getJSONObject(id);
                if (abgeordneter.has("href")) {  // falls ein Link zur webseite existiert
                String hrefAb = abgeordneter.getString("href");
                String href = h + hrefAb;
                try {// wird es bearbeitet

                    Document doc = Jsoup.connect(href).get();

                    if (doc != null) {
                        // Informationen werden extrahiert
                        String stand = doc.select("meta[name=date]").attr("content");

                        String name = doc.select("meta[name=keywords]").attr("content");
                        String nameOhneKomma = extractName(name);

                        String abgeordneterBuro = extractSpeaker(doc, "h5:contains(Abgeordnetenbüro)");
                        String beruf = extractSpeaker2(doc, ".bt-biografie-beruf p");
                        String wahlkreisburo = extractSpeaker(doc, "h5:contains(Wahlkreisbüro)");


                        SozialeMedien medien = sozialMedia.extractSozialeMedien(doc);

                        Abgeordneter abgeordneter1 = new Abgeordneter_xml_Impl(nameOhneKomma, beruf,
                                abgeordneterBuro, wahlkreisburo, medien, stand);
                        abgeordneterMETA.add(abgeordneter1);

                    }/*

                    Map<String, List<String>> h5ToLiMap = new HashMap<>();

                    Elements h5Elements = doc.select("h5");

                    Map<String, List<String>> map = new LinkedHashMap<>();

                    for (Element h5Element : h5Elements) {
                        String h5Text = h5Element.text();
                        List<String> liTexts = new ArrayList<>();
                        Element nextElement = h5Element.nextElementSibling();
                        while (nextElement != null && !nextElement.tagName().equals("h5")) {
                            if (nextElement.tagName().equals("ul")) {
                                Elements liElements = nextElement.select("li");
                                for (Element liElement : liElements) {
                                    liTexts.add(liElement.text());
                                }
                            }
                            nextElement = nextElement.nextElementSibling();
                        }
                        map.put(h5Text, liTexts);
                    }
                    for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                        //System.out.println(entry.getKey() + ":");
                        for (String liText : entry.getValue()) {
                            //System.out.println("- " + liText);
                        }
                    }





                    for (Map.Entry<String, List<String>> entry : h5ToLiMap.entrySet()) {
                        System.out.println(entry.getKey() + ": " + entry.getValue());
                    }


                    Elements paragraphs = doc.select("p");
                    // System.out.println("Alle Absätze:");

                    for (Element paragraph : paragraphs) {
                        //System.out.println(paragraph.tagName() +" "   + "  " +paragraph.text());
                    } */


                } finally {

                }

            }}
        } finally {

        }


    }

    /**
     * erstellt von Feride YIlmaz
     *
     * Methode extrahiert Namen der Abgeordenten
     * @param name
     * @return name ohne das Komma am Ende
     */

    public static String extractName(String name){
        String nameOhneKomma;
        if (name.endsWith(",")) {
            nameOhneKomma = name.substring(0, name.length() - 1);
            return nameOhneKomma;

        } else {
            nameOhneKomma = name;
            return nameOhneKomma;
        }
    }

    /**
     * erstellt von Feride Yilmaz
     *
     * Methode extrahiert den Text basierend auf dem nächsten Geschwister Element von elementNme
     * @param doc html Dokument
     * @param elementName
     * @return text, falls element exisiert, sonst leere Zeichenfolge
     */

    public static String extractSpeaker(Document doc, String elementName) {
            Elements elements = doc.select(elementName);
            if (!elements.isEmpty()) {
                Element speakerElement = elements.first();
                String x = speakerElement.nextElementSibling().text();
                return x;
            } else {
                return "";
            }
        }

    /**
     * erstellt von Feride Yilmaz
     *
     * Methode extrahiert den Text basierend direkt aufelementName
     * @param doc html Dokument
     * @param elementName
     * @return text, falls element exisiert, sonst leere Zeichenfolge
     */
    public static String extractSpeaker2(Document doc, String elementName) {
    Elements elements = doc.select(elementName);
    if (!elements.isEmpty()) {
        Element speakerElement = elements.first();
        String x = speakerElement.text();
        return x;
    } else {
        return "";
    }
}
}



