package xml_Impl;

import Interface.Abgeordneter;
import Interface.CAS;
import Interface.Protokoll;
import Interface.Rede;
import NLP.CAS_Impl;
import org.apache.uima.UIMAException;

import java.io.IOException;
import java.sql.Date;

public class Rede_xml_Impl implements Rede {
    private Object oID;
    private Object rednerID;
    private String sText;
    private Integer iLaenge;
    private Protokoll pProtokoll;
    private String sKommentar;
    private String iSitzung;
    private String sWahlperiode;
    private Date dDate;
    private String sFraktion;
    private Integer kommentarLaenge;
    private Abgeordneter redner;
    private CAS cas;

    public Rede_xml_Impl(Object rednerID, Object oID, String text){
        this.rednerID = rednerID;
        this.oID = oID;
        this.sText = text;
    }

    public Rede_xml_Impl(Object rednerID, Object oID, String sText,Integer iLaenge,String sKommentare,
                         Integer kommentarLaenge, String iSitzung, String sWahlperiode, Date dDate,
                         String sFraktion, Abgeordneter redner) {
        this.oID = oID;
        this.rednerID = rednerID;
        this.sText = sText;
        this.iLaenge = iLaenge;
        this.sKommentar = sKommentare;
        this.kommentarLaenge = kommentarLaenge;
        this.iSitzung = iSitzung;
        this.sWahlperiode = sWahlperiode;
        this.dDate = dDate;
        this.sFraktion = sFraktion;
        this.redner = redner;
    }
    public Rede_xml_Impl(Object rednerID, Object oID, String sText,Integer iLaenge,String sKommentare,
                         Integer kommentarLaenge, String iSitzung, String sWahlperiode, Date dDate,
                         String sFraktion) {
        this.oID = oID;
        this.rednerID = rednerID;
        this.sText = sText;
        this.iLaenge = iLaenge;
        this.sKommentar = sKommentare;
        this.kommentarLaenge = kommentarLaenge;
        this.iSitzung = iSitzung;
        this.sWahlperiode = sWahlperiode;
        this.dDate = dDate;
        this.sFraktion = sFraktion;
    }

    @Override
    public Abgeordneter getRedner(){
        return this.redner;}

    public Rede_xml_Impl setRedner(Abgeordneter sValue) {
        this.redner = sValue;
        return this;
    }
    @Override
    public String getFraktion(){
        return this.sFraktion;}

    public Rede_xml_Impl setFraktion(String sValue) {
        this.sFraktion = sValue;
        return this;
    }

    @Override
    public Object getID() {
        return this.oID;
    }

    public Rede_xml_Impl setID(Object sValue) {
        this.oID = sValue;
        return this;
    }

    @Override
    public Object getRednerID() {
        return this.rednerID;
    }
    public Rede_xml_Impl setRednerID(Object sValue) {
        this.rednerID= sValue;
        return this;
    }

    @Override
    public String getText() {
        return this.sText;
    }
    public Rede_xml_Impl setText(String sValue) {
        this.sText = sValue;
        return this;
    }

    @Override
    public Integer getRedelaenge() {
        return this.iLaenge;
    }
    public Rede_xml_Impl setRedeLaenge(Integer sValue) {
        this.iLaenge = sValue;
        return this;
    }
    public Integer getKommentarLaenge(){
        return this.kommentarLaenge;
    }
    public Rede_xml_Impl setKommentarLaenge(Integer sValue){
        this.kommentarLaenge = sValue;
        return this;
    }

    @Override
    public Protokoll getProtokoll() {
        return this.pProtokoll;
    }
    public Rede_xml_Impl setProtokoll(Protokoll sValue) {
        this.pProtokoll = sValue;
        return this;
    }

    @Override
    public String getKommentare() {
        return this.sKommentar;
    }
    public Rede_xml_Impl setKommentare(String sValue) {
        this.sKommentar = sValue;
        return this;
    }

    @Override
    public String getSitzung() {
        return this.iSitzung;
    }
    public Rede_xml_Impl setSitzung(String sValue) {
        this.iSitzung = sValue;
        return this;
    }

    @Override
    public String getWahlperiode() {
        return this.sWahlperiode;
    }
    public Rede_xml_Impl setWahlperiode(String sValue) {
        this.sWahlperiode = sValue;
        return this;
    }

    @Override
    public Date getDate() {
        return this.dDate;
    }
    public Rede_xml_Impl setDate(Date sValue) {
        this.dDate = sValue;
        return this;
    }
    @Override
    public void createJCas() {
        try {
            cas.setUpJCas(getText());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @throws UIMAException
     * @throws IOException
     */
    @Override
    public void loadJCasFromDB() throws UIMAException, IOException {
        cas.loadJCasFromDB();
    }
    /**
     * @return
     */
    @Override
    public CAS getCAS() {
        return cas;
    }


}


