package data;

import Interface.SozialeMedien;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import xml_Impl.SozialeMedien_xml_Impl;

public class sozialMedia {
    /**
     * erstellt von Feride Yilmaz
     * Methode extrahiert Informationen zu den sozialen Medien
     *
     * @param doc
     * @return Object vom Type {@code SozialeMedien}
     * @throws NullPointerException
     */
    public static SozialeMedien extractSozialeMedien(Document doc) throws NullPointerException{
        Elements profil = doc.select("h5:contains(Profile im Internet)");
        if (!profil.isEmpty()) {
            Element profilElement = profil.first();
            Element homepage = profilElement.nextElementSibling().select("a[title=Homepage]").first();
            Element twitter = profilElement.nextElementSibling().select("a[title=Twitter]").first();
            Element telegram = profilElement.nextElementSibling().select("a[title=Telegram]").first();
            Element instaram = profilElement.nextElementSibling().select("a[title=Instagram]").first();
            Element youtube = profilElement.nextElementSibling().select("a[title=Youtube]").first();
            Element facebook = profilElement.nextElementSibling().select("a[title=Facebook]").first();

            String homepageURL = url(homepage);
            String twitterURL = url(twitter);
            String telegramURL = url(telegram);
            String instagramURL = url(instaram);
            String youtubeURL = url(youtube);
            String facebookURL = url(facebook);

            SozialeMedien sozialeMedien = new SozialeMedien_xml_Impl(homepageURL, facebookURL, twitterURL,
                    instagramURL,youtubeURL,telegramURL);

            return sozialeMedien;
        } return null;
    }

    /**
     * erstellt von Feride Yilmaz
     *
     * Methode überprüft, ob das Element existirt
     *
     * @param x Das Element, was überprüft werden soll
     * @return Die URL oder falls das Element nicht existiert ein leerer String
     */

    public static String url(Element x){
        if (x != null) {
            String url = x.attr("href");
            return url;
        } else {
            return "";
        }
    }
}
