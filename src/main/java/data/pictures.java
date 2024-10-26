package data;

import Interface.Pictures;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import xml_Impl.Pictures_xml_Impl;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class pictures {
    public static List<Pictures> pictures = new ArrayList<>();
    public static void extractPictures() {
        String url = "https://bilddatenbank.bundestag.de/search/picture-result?query=&sortVal=3";

        try {
            Document doc = Jsoup.connect(url).get();




            Elements aTags = doc.select("a");
            for (Element aTag : aTags) {
                String href = aTag.attr("href");
                String[] parts = href.split("=");
                if (parts.length > 1) {
                    String bildnummer = parts[1];
                    System.out.println("Bildnummer: " + bildnummer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        /*
        String url = "https://bilddatenbank.bundestag.de/ajax/picture-result?";
        try {

            JSONObject json = readJsonFromUrl(url); // JSON-Objekt von der URL lesen

            JSONArray fotos = json.getJSONArray("fotos");


            for (int i = 0; i < fotos.length(); i++) {
                JSONObject foto = fotos.getJSONObject(i);


                String datum = element(foto,"datum");
                int bildnummer = foto.getInt("bildnummer");
                String hqBild = element(foto,"hqBild");
                String ereignis = element(foto,"ereignis");
                String ort = element(foto,"ort");
                String smallbild = element(foto,"smallBild");
                String rechte = element(foto,"rechtehinweis");
                String beschreibung = element(foto,"objektbeschreibung");
                String name = element(foto,"name");
                String land = element(foto,"land");
                String stadt = element(foto,"stadt");
                String id = element(foto,"id");
                String fotograf = element(foto,"fotograf");
                String caption = element(foto,"captionHtml");
                String title = element(foto,"titleAltTag");

                Pictures pic = new Pictures_xml_Impl(id, datum,bildnummer,hqBild,ereignis,ort, smallbild, rechte,
                        beschreibung, name,land,stadt,fotograf,caption, title);

                pictures.add(pic);



            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }*/
    }


    public static String element(JSONObject foto, String x) throws JSONException {
    if (foto.has(x)) {
        String aus = foto.getString(x);
        return aus;
    } else {
        return "";
    }}

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            String jsonText = sb.toString();
            return new JSONObject(jsonText);
        }
    }
}