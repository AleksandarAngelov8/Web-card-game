package xml_Impl;

import Interface.SozialeMedien;

public class SozialeMedien_xml_Impl implements SozialeMedien {
    private String homepage;
    private String facebook;
    private String twitter;
    private String instagram;
    private String youtube;
    private String telegram;

    public SozialeMedien_xml_Impl(String homepage, String facebook,String twitter, String instagram,
                                  String youtube, String telegram){
        this.homepage = homepage;
        this.facebook = facebook;
        this.twitter = twitter;
        this.instagram = instagram;
        this.youtube = youtube;
        this.telegram = telegram;
    }
    @Override
    public String getInstagram() throws NullPointerException {
        if (this.instagram != null) {
            return this.instagram;
        } else {
            return "";
        }

    }

    @Override
    public String getTwitter() throws NullPointerException {
        if (this.twitter!= null) {
            return this.twitter;
        } else {
            return "";
        }
    }

    @Override
    public String getFacebook() throws NullPointerException {
        if (this.facebook != null) {
            return this.facebook;
        } else {
            return "";
        }

    }

    @Override
    public String getHomepage() throws NullPointerException {
        if (this.homepage != null) {
            return this.homepage;
        } else {
            return "";
        }
    }

    @Override
    public String getYoutube() throws NullPointerException {
        if (this.youtube != null) {
            return this.youtube;
        } else {
            return "";
        }

    }

    @Override
    public String getTelegram() throws NullPointerException {
        if (this.telegram != null) {
            return this.telegram;
        } else {
            return "";
        }

    }
}
