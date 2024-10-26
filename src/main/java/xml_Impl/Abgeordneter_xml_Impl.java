package xml_Impl;


import Interface.Abgeordneter;
import Interface.Pictures;
import Interface.Rede;
import Interface.SozialeMedien;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Abgeordneter_xml_Impl implements Abgeordneter {
    private Object oID;
    private String sName;
    private String sVorname;
    private String sOrtszusatz;
    private String sAdelssuffix;
    private String sAnrede;
    private String sAkadTitel;

    private Date dGeburtsDatum;
    private String sGeburtsOrt;
    private Date dSterbeDatum;
    private String sGeschlecht;
    private String sReligion;
    private String sBeruf;
    private String sVita;


    private String sPartei;
    private Integer iRedeLaenge;
    private String sText;
    private String sFraktion;
    private String abgeordnetenburo;
    private String wahlkreisburo;
    private SozialeMedien medien;
    private String stand;
    private String vollstandigerName;
    private Rede reden;
    private List<Pictures> picturesList;

    public Abgeordneter_xml_Impl(Object oID, String sName, String sVorname){
        this.oID = oID;
        this.sName = sName;
        this.sVorname = sVorname;
    }

    public Abgeordneter_xml_Impl(String vollName, String beruf, String abgeordnetenburo,
                                 String wahlkreisburo, SozialeMedien medien, String stand){
        this.vollstandigerName = vollName;
        this.sBeruf = beruf;
        this.abgeordnetenburo = abgeordnetenburo;
        this.wahlkreisburo = wahlkreisburo;
        this.medien = medien;
        this.stand = stand;
    }

    public Abgeordneter_xml_Impl(Object oID, String sName, String sVorname, String sOrtszusatz, String sAdelssuffix,
                                  String sAnrede, String sAkadTitel, Date dGeburtsDatum, String sGeburtsOrt,
                                 String sGeschlecht, String sReligion, String sBeruf, String sVita, String sPartei) {
        this.oID = oID;
        this.sName = sName;
        this.sVorname = sVorname;
        this.sAkadTitel = sAkadTitel;
        this.sPartei = sPartei;
        this.sOrtszusatz=sOrtszusatz;
        this.sAdelssuffix=sAdelssuffix;
        this.sAnrede=sAnrede;
        this.dGeburtsDatum=dGeburtsDatum;
        this.sGeburtsOrt=sGeburtsOrt;
        this.sGeschlecht=sGeschlecht;
        this.sReligion=sReligion;
        this.sBeruf=sBeruf;
        this.sVita=sVita;

    }
    public Abgeordneter_xml_Impl(String abgeordnetenburo, String wahlkreisburo, SozialeMedien medien, String stand) {
        this.oID = oID;
        this.sName = sName;
        this.sVorname = sVorname;
        this.sAkadTitel = sAkadTitel;
        this.sPartei = sPartei;
        this.sOrtszusatz=sOrtszusatz;
        this.sAdelssuffix=sAdelssuffix;
        this.sAnrede=sAnrede;
        this.dGeburtsDatum=dGeburtsDatum;
        this.sGeburtsOrt=sGeburtsOrt;
        this.sGeschlecht=sGeschlecht;
        this.sReligion=sReligion;
        this.sBeruf=sBeruf;
        this.sVita=sVita;
        this.abgeordnetenburo = abgeordnetenburo;
        this.wahlkreisburo = wahlkreisburo;
        this.medien = medien;
        this.stand = stand;
        this.picturesList = new ArrayList<>();

    }
    public Abgeordneter_xml_Impl(Object oID, String sName, String sVorname, String sAkadTitel, String sFraktion){
        this.oID = oID;
        this.sName = sName;
        this.sVorname = sVorname;
        this.sAkadTitel = sAkadTitel;
        this.sFraktion = sFraktion;
    }
    public void addPicture(Pictures picture){
        this.picturesList.add(picture);
    }


    public Abgeordneter_xml_Impl(Object rednerID) {
        this.oID = rednerID;
    }

    @Override
    public Object getID() {
        return this.oID;
    }

    public Abgeordneter_xml_Impl setObject(Object sValue){
        this.oID = sValue;
        return this;
    }
    @Override
    public Rede getRede() {
        return this.reden;
    }

    @Override
    public String getName() {
        return this.sName;
    }


    public Abgeordneter_xml_Impl setName(String sValue){
        this.sName = sValue;
        return this;
    }

    @Override
    public String getVollName() {
        return this.vollstandigerName;
    }




    @Override
    public String getVorname() {
        return this.sVorname;
    }
    public Abgeordneter_xml_Impl setVorame(String sValue){
        this.sVorname = sValue;
        return this;
    }
    @Override
    public String getOrtszusatz() {  // falls kein Eintrag für Ortszusatz --> Ausgabe von null
        if (sOrtszusatz.trim().equals("")){
            String sOrtszusatzNull = "";
            return sOrtszusatzNull;
        }
        else{
            return this.sOrtszusatz;
        }
    }
    public Abgeordneter_xml_Impl setOrtszusatz(String sValue){
        this.sOrtszusatz = sValue;
        return this;
    }

    @Override
    public String getAdelssuffix() {
        if (sAdelssuffix.trim().equals("")){
            String sAdelNull = "";
            return sAdelNull;
        }
        else{
            return this.sAdelssuffix;
        }

    }
    public Abgeordneter_xml_Impl setAdelssuffix(String sValue){
        this.sAdelssuffix = sValue;
        return this;
    }

    @Override
    public String getAnrede() {
        if (sAnrede.trim().equals("")){
            String sAnredeNull = "";
            return sAnredeNull;
        }
        else{
            return this.sAnrede;
        }
    }
    public Abgeordneter_xml_Impl setAnrede(String sValue){
        this.sAnrede = sValue;
        return this;
    }


    @Override
    public String getAkadTitel() {
        return this.sAkadTitel;
    }
    public Abgeordneter_xml_Impl setAkadTitel(String sValue){
        this.sAkadTitel = sValue;
        return this;
    }


    @Override
    public Date getGeburtsDatum() {
        return this.dGeburtsDatum;
    }
    public Abgeordneter_xml_Impl setGeburtsDatum(Date sValue){
        this.dGeburtsDatum = sValue;
        return this;
    }

    @Override
    public String getGeburtsOrt() {
        return this.sGeburtsOrt;
    }
    public Abgeordneter_xml_Impl setGeburtsOrt(String sValue){
        this.sGeburtsOrt = sValue;
        return this;
    }



    @Override
    public String getGeschlecht() {
        return this.sGeschlecht;
    }
    public Abgeordneter_xml_Impl setGeschlecht(String sValue){
        this.sGeschlecht = sValue;
        return this;
    }

    @Override
    public String getReligion() {
        if (sReligion.trim().equals("")){
            String sReligionNull = "";
            return sReligionNull;
        }
        else{
            return this.sReligion;
        }
    }
    public Abgeordneter_xml_Impl setReligio(String sValue){
        this.sReligion = sValue;
        return this;
    }

    @Override
    public String getBeruf() {
        if (sBeruf.trim().equals("")){
            String sBerufNull = "";
            return sBerufNull;
        }
        else{
            return this.sBeruf;
        }
    }
    public Abgeordneter_xml_Impl setBeruf(String sValue){
        this.sBeruf = sValue;
        return this;
    }

    // hier wird überprüft, ob sVita existirt
    @Override
    public String getVita() {
        if (sVita.trim().equals("")){
            String sVitaNull = "";
            return sVitaNull;
        }else{
            return this.sVita;}
    }
    public Abgeordneter_xml_Impl setVita(String sValue){
        this.sVita = sValue;
        return this;
    }
    @Override
    public String getPartei() {
        if (sPartei.trim().equals("")){
            String sParteiNull = "";
            return sParteiNull;
        }
        else{
            return this.sPartei;
        }
    }

    @Override
    public String getBuro() {
        return this.abgeordnetenburo;
    }

    @Override
    public String getWahlkreiburo() {
        return this.wahlkreisburo;
    }

    @Override
    public SozialeMedien getMedien() {
        if (this.medien != null) {
            return this.medien;
        } else {
            return new SozialeMedien_xml_Impl("","","","","","");
        }
    }
    public String getStand(){
        if (this.stand != null) {
            return this.stand;
        } else {
            return "";
        }
    }

    public Abgeordneter_xml_Impl setPartei(String sValue){
        this.sPartei = sValue;
        return this;
    }

}






