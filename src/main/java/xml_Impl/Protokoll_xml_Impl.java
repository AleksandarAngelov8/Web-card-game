package xml_Impl;

import Interface.Protokoll;
import Interface.Rede;

import java.sql.Date;
import java.util.List;

public class Protokoll_xml_Impl implements Protokoll {
    private Object oID;
    private Date dDatum;
    private String sTitel;
    private String sWahlperiode;
    private String sOrt;
    private List<Rede> pRede;
    private String pAbgeordneter;
    private String sStart;
    private String sEnde;
    private Object Sitzungsnummer;

    public  Protokoll_xml_Impl(Object oID, Object sitzungsnummer, Date dDatum, String sTitel, String sWahlperiode,
                               String sOrt, List<Rede> pRede, String start, String ende){
        this.oID = oID;
        this.dDatum = dDatum;
        this.sTitel = sTitel;
        this.sWahlperiode = sWahlperiode;
        this.sOrt = sOrt;
        this.pRede = pRede;
        this.sStart = start;
        this.sEnde = ende;
        this.Sitzungsnummer = sitzungsnummer;

    }


    @Override
    public Object getID() {
        return this.oID;
    }
    public Protokoll_xml_Impl setID(Object sValue) {
        this.oID = sValue;
        return this;
    }

    @Override
    public Date getDatum() {
        return this.dDatum;
    }
    public Protokoll_xml_Impl setDate(Date sValue) {
        this.dDatum = sValue;
        return this;
    }

    @Override
    public String getTitel() {
        return this.sTitel;
    }
    public Protokoll_xml_Impl setTitel(String sValue) {
        this.sTitel = sValue;
        return this;
    }
    @Override
    public String getStart() {
        return this.sStart;
    }
    public Protokoll_xml_Impl setStart(String sValue) {
        this.sStart = sValue;
        return this;
    }
    @Override
    public String getEnde() {
        return this.sEnde;
    }

    @Override
    public Object getSitzungsnummer() {
        return this.Sitzungsnummer;
    }

    public Protokoll_xml_Impl setEnde(String sValue) {
        this.sEnde = sValue;
        return this;
    }

    @Override
    public String getWahlperiode() {
        return this.sWahlperiode;
    }
    public Protokoll_xml_Impl setWahlperiode(String sValue) {
        this.sWahlperiode = sValue;
        return this;
    }

    @Override
    public String getOrt() {
        return this.sOrt;
    }
    public Protokoll_xml_Impl setOrt(String sValue) {
        this.sOrt = sValue;
        return this;
    }

    @Override
    public List<Rede> getReden() {
        return this.pRede;
    }
    public Protokoll_xml_Impl setRede(List<Rede> sValue) {
        this.pRede = sValue;
        return this;
    }

    @Override
    public String getRedner() {
        return this.pAbgeordneter;
    }
    public Protokoll_xml_Impl setRedner(String sValue) {
        this.pAbgeordneter = sValue;
        return this;
    }
}
