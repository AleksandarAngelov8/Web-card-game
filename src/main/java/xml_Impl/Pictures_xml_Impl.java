package xml_Impl;

import Interface.Pictures;
import java.util.List;

public class Pictures_xml_Impl implements Pictures {
        private String id;
        private String datum;
        private int bildnummer;
        private String hqBild;
        private String ereignis;
        private String ort;
        private String smallBild;
        private String rechtehinweis;
        private  String objektbeschreibung;
        private  String name;
        private String land;
        private String stadt;
        private String fotograf;
        private String captionHtml;
        private String titleAltTag;
        public Pictures_xml_Impl(String id, String datum, int bildnummer, String hqBild, String ereignis,
                       String ort, String smallBild, String rechtehinweis,  String objektbeschreibung,
                                 String name, String land, String stadt, String fotograf, String captionHtml,
                       String titleAltTag) {
            this.id = id;
            this.datum = datum;
            this.bildnummer = bildnummer;
            this.hqBild = hqBild;
            this.ereignis = ereignis;
            this.ort = ort;
            this.smallBild = smallBild;
            this.rechtehinweis = rechtehinweis;
            this.objektbeschreibung = objektbeschreibung;
            this.name = name;
            this.land = land;
            this.stadt = stadt;
            this.fotograf = fotograf;
            this.captionHtml = captionHtml;
            this.titleAltTag = titleAltTag;
        }

    public String getFullName() {
        String[] nameParts = this.getName().split(","); // Trennen des Namens am Komma
        if (nameParts.length > 1) {
            String lastName = nameParts[0].trim().replaceAll("[^a-zA-Z]", ""); // Entfernen von Sonderzeichen im Nachnamen
            String firstName = nameParts[1].trim().replaceAll("[^a-zA-Z]", "");
            return firstName + " " + lastName; // Vorname zuerst, dann Nachname
        } else {
            // Rückgabe des gesamten Namens, falls keine Trennung gefunden wurde
            return nameParts[0].trim();
        }
    }






    @Override
    public Object getId() {
        return this.id;
    }

    @Override
    public String getDatum() {
        return this.datum;
    }

    @Override
    public int getBildnummer() {
        return this.bildnummer;
    }

    @Override
    public String gethqBild() {
        return this.hqBild;
    }

    @Override
    public String getEreignis() {
        return this.ereignis;
    }

    @Override
    public String getOrt() {
        return this.ort;
    }

    @Override
    public String sgetSmallBild() {
        return this.smallBild;
    }

    @Override
    public String getRechtehinweis() {
        return this.rechtehinweis;
    }

    @Override
    public  String getObjektbeschreibung() {
        return this.objektbeschreibung;
    }

    @Override
    public  String getName() {
        return this.name;
    }

    @Override
    public String getLand() {
        return this.land;
    }

    @Override
    public String getStadt() {
        return this.stadt;
    }

    @Override
    public String getFotograf() {
        return this.fotograf;
    }

    @Override
    public String getCaptionHtml() {
        return this.captionHtml;
    }

    @Override
    public String getTtitleAltTag() {
        return this.titleAltTag;
    }
}
