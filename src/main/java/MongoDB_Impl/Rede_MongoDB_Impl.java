package MongoDB_Impl;

import Interface.Abgeordneter;
import Interface.CAS;
import Interface.Protokoll;
import Interface.Rede;
import MongoDBConnectionHandler.MongoDBHandler;
import NLP.CAS_Impl;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.apache.uima.UIMAException;
import org.bson.Document;
import xml_Impl.Abgeordneter_xml_Impl;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;


/**
 * erstellt von Feride Yilmaz
 */
public class Rede_MongoDB_Impl implements Rede {
    private MongoCollection<Document> collection;
    private Document pDocument;
    private CAS cas;

    public Rede_MongoDB_Impl(MongoCollection<Document> collection, Document pDocument, boolean cas) {
        this.collection = collection;
        this.pDocument = pDocument;
        if (cas) this.cas = new CAS_Impl(this);
    }

    @Override
    public Object getID() {
        Object redeID = pDocument.get("_id");
        if(redeID != null){
            return redeID;
        }else{
            return "null";
        }
    }
    public void setID(Object x){
        collection.updateOne(Filters.eq("_id", pDocument.getObjectId("_id")),
                Updates.set("_id",x));
    }

    @Override
    public Object getRednerID() {
        Object rednerID = pDocument.get("ID");
        if (rednerID != null){
            return rednerID;
        }else {
            return "null";
        }
    }
    public void setRednerID(Object x){
        collection.updateOne(Filters.eq("_id", pDocument.getObjectId("_id")),
                Updates.set("_id",x));
    }

    // geändert von Angelov
    // das Cast funktionierte nicht und wirft ein Exception
    // hinzu ist es schneller pDocument zu verwenden
    // hinzu return-Typ soll Abgeordneter_MongoDB_Impl sein
    @Override
    public Abgeordneter getRedner() {
        String rednerId  = ((Document) pDocument.get("redner")).getString("id");
        return new Abgeordneter_MongoDB_Impl(MongoDBHandler.getCollectionSpeaker(),MongoDBHandler.getRednerDocument(rednerId));
    }
    public void setRedner(Abgeordneter x){
        Abgeordneter abgeordneterUpdate =
                new Abgeordneter_xml_Impl(x.getID(), x.getName(), x.getVorname());

        collection.updateOne(Filters.eq("_id", pDocument.getObjectId("_id")),
            Updates.set("redner", abgeordneterUpdate));

    }

    @Override
    public String getText() {
        String text =  pDocument.getString("rede");
        if(text != null){
            return text;
        }else{
            return "null";
        }
    }
    public void setText(String x){
        collection.updateOne(Filters.eq("_id", pDocument.getObjectId("_id")),
                Updates.set("rede",x));
    }

    @Override
    public Integer getRedelaenge() {
        Integer redelaenge = Integer.valueOf(pDocument.getString("rede_laenge"));
        if(redelaenge != null){
            return redelaenge;
        }else{
            return null;
        }
    }
    public void setRedeLaenge(Integer x){
        collection.updateOne(Filters.eq("_id", pDocument.getObjectId("_id")),
                Updates.set("rede_laenge",x));
    }

    @Override
    public Integer getKommentarLaenge() {
        Integer kommentarLaenge = Integer.valueOf(pDocument.getString("kommentar_laenge"));
        if(kommentarLaenge != null){
            return kommentarLaenge;
        }else{
            return null;
        }
    }
    public void setKommentarLaenge(Integer x){
        collection.updateOne(Filters.eq("_id", pDocument.getObjectId("_id")),
                Updates.set("kommentar_laenge",x));
    }


    @Override
    public Protokoll getProtokoll() {
        return null;
    }

    @Override
    public String getKommentare() {
        String kommentare = pDocument.getString("kommentare");
        if(kommentare != null){
            return kommentare;
        }else{
            return "null";
        }
    }
    public void setKommentare(String x){
        collection.updateOne(Filters.eq("_id", pDocument.getObjectId("_id")),
                Updates.set("kommentare",x));
    }


    @Override
    public String getSitzung() {
        String sitzung = pDocument.getString("Sitzung");
        if(sitzung != null){
            return sitzung;
        }else{
            return "null";
        }
    }
    public void setSitzung(String x){
        collection.updateOne(Filters.eq("_id", pDocument.getObjectId("_id")),
                Updates.set("Sitzung",x));
    }

    @Override
    public String getWahlperiode() {
        String wahlperiode = pDocument.getString("wahlperiode");
        if(wahlperiode != null){
            return wahlperiode;
        }else{
            return "null";
        }
    }
    public void setWahlperiode(String x){
        collection.updateOne(Filters.eq("_id", pDocument.getObjectId("_id")),
                Updates.set("wahlperiode",x));
    }

    @Override
    public Date getDate() {
        Date datum = (Date) pDocument.getDate("datum");
        if(datum != null){
            return datum;
        }else {
            return null;
        }
    }
    public void setDate(Date x){
        collection.updateOne(Filters.eq("_id", pDocument.getObjectId("_id")),
                Updates.set("datum",x));
    }

    @Override
    public String getFraktion() throws NullPointerException {
        String fraktion = pDocument.getString("fraktion");
        if(fraktion != null){
            return fraktion;
        }else {
            return "null";
        }
    }
    /** erstellt von Angelov
     * erstellt CAS-Objekt
     */
    @Override
    public void createJCas() {
        try {
            cas.setUpJCas(getText());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * @throws UIMAException \
     * @throws IOException
     */
    @Override
    public void loadJCasFromDB() throws UIMAException, IOException {
        cas.loadJCasFromDB();
    }

    /** erstellt von Angelov
     * @return CAS-Objekt
     */
    @Override
    public CAS getCAS() {
        return cas;
    }
    public void setFraktion(String x){
        collection.updateOne(Filters.eq("_id", pDocument.getObjectId("_id")),
                Updates.set("fraktion",x));
    }
}
