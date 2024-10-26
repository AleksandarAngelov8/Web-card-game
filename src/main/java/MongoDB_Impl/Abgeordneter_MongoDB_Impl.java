package MongoDB_Impl;

import Interface.Abgeordneter;
import Interface.Rede;
import Interface.SozialeMedien;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import xml_Impl.SozialeMedien_xml_Impl;;import java.sql.Date;

/**
 * erstellt von Feride Yilmaz
 */

public class Abgeordneter_MongoDB_Impl implements Abgeordneter {
    private MongoCollection<Document> collection;

    public Document getDocument() {
        return pDocument;
    }

    private Document pDocument;

    public Abgeordneter_MongoDB_Impl(MongoCollection<Document> collection, Document pDocument) {
        this.collection = collection;
        this.pDocument = pDocument;
    }


    @Override
    public Object getID() {
        Object id = pDocument.get("_id");
        if(id != null){
            return id;
        }else{
            return "null";
        }
    }
    public void setID(Object x) {
        // ID wird aktualisiert. Dokuemnt wird anhand von ID ausgewählt
        collection.updateOne(Filters.eq("_id", pDocument.getObjectId("_id")),
                Updates.set("ID", x));
    }

    @Override
    public String getName() {
        String name = pDocument.getString("nachname");
        if(name != null){
            return name;
        }else{
            return "null";
        }
    }
    public void setName(String x) {
        // nachname wird aktualisiert. Dokuemnt wird anhand von ID ausgewählt
        collection.updateOne(Filters.eq("_id", pDocument.getObjectId("_id")),
                Updates.set("nachname", x));
    }

    @Override
    public String getVorname() {
        String vorname = pDocument.getString("vorname");
        if(vorname != null){
            return vorname;
        }else{
            return "null";
        }
    }
    public void setVorname(String x) {
        collection.updateOne(Filters.eq("_id", pDocument.getObjectId("_id")),
                Updates.set("vorname", x));
    }

    @Override
    public String getOrtszusatz() {
        String x = pDocument.getString("ortszusatz");
        if (x != null){
            return x;
        }else{
            return "null";
        }
    }

    @Override
    public String getAdelssuffix() {
        String x = pDocument.getString("adelssuffix");
        if (x != null){
            return x;
        }else{
            return "null";
        }
    }

    @Override
    public String getAnrede() {
        String x = pDocument.getString("anrede");
        if (x != null){
            return x;
        }else{
            return "null";
        }
    }



    @Override
    public String getAkadTitel() {
        String titel = pDocument.getString("titel");
        if(titel != null){
            return titel;
        }else{
            return "null";
        }
    }

    @Override
    public Date getGeburtsDatum() {
        Date x = Date.valueOf(pDocument.getString("geburtsdatum"));
        if (x != null){
            return x;
        }else{
            return Date.valueOf("null");
        }
    }

    @Override
    public String getGeburtsOrt() {
        String x = pDocument.getString("geburtsort");
        if (x != null){
            return x;
        }else{
            return "null";
        }
    }

    @Override
    public String getGeschlecht() {
        String x = pDocument.getString("geschlecht");
        if (x != null){
            return x;
        }else{
            return "null";
        }
    }

    @Override
    public String getReligion() {
        String x = pDocument.getString("religion");
        if (x != null){
            return x;
        }else{
            return "null";
        }
    }

    @Override
    public String getBeruf() {
        String x = pDocument.getString("beruf");
        if (x != null){
            return x;
        }else{
            return "null";
        }
    }

    @Override
    public String getVita() throws NullPointerException {
        String x = pDocument.getString("vita");
        if (x != null){
            return x;
        }else{
            return "null";
        }
    }

    public void setAkadTitel(String x) {
        collection.updateOne(Filters.eq("_id", pDocument.getObjectId("_id")),
                Updates.set("titel", x));
    }

    @Override
    public String getPartei(){
        String partei = pDocument.getString("partei");
        if (partei != null){
            return partei;
        }else{
            return "null";
        }
    }

    @Override
    public String getBuro() {
        String x = pDocument.getString("abgeordnetenbüro");
        if (x != null){
            return x;
        }else{
            return "null";
        }
    }

    @Override
    public String getWahlkreiburo() {
        String x = pDocument.getString("wahlkreisbüro");
        if (x != null){
            return x;
        }else{
            return "null";
        }
    }

    @Override
    public SozialeMedien getMedien() {
        String x = pDocument.getString("homepage");
        String home;
        if (x != null){
            home = x;
        }else{
            home ="";
        }
        if (pDocument.containsKey("soziale_medien")) {

            Document sozialeMedien = pDocument.get("soziale_medien", Document.class);

            String facebook = sozialeMedien.getString("facebook");
            String twitter = sozialeMedien.getString("twitter");
            String instagram = sozialeMedien.getString("instagram");
            String telegram = sozialeMedien.getString("telegram");
            String youtube = sozialeMedien.getString("youtube");

            return new SozialeMedien_xml_Impl(home,facebook, twitter, instagram, telegram, youtube);
        } else {
            return new SozialeMedien_xml_Impl(home,"","","","",""); // oder null, je nach Anwendungsfall
        }
    }

    @Override
    public String getStand() {
        String x = pDocument.getString("stand");
        if (x != null){
            return x;
        }else{
            return "null";
        }
    }

    @Override
    public String getVollName() {
        return null;
    }

    @Override
    public Rede getRede() {
        return null;
    }

    public void setPartei(String x) {
        collection.updateOne(Filters.eq("_id", pDocument.getObjectId("_id")),
                Updates.set("fraktion", x));
    }
}
