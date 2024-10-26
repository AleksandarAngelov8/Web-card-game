
//erstellt von Angelov
package NLP;

import Interface.CAS;
import Interface.Rede;
import MongoDBConnectionHandler.MongoDBHandler;
import com.mongodb.client.MongoCollection;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.SerialFormat;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.CasIOUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.hucompute.textimager.uima.type.Sentiment;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;
import org.texttechnologylab.DockerUnifiedUIMAInterface.DUUIComposer;
import org.texttechnologylab.DockerUnifiedUIMAInterface.driver.DUUIRemoteDriver;
import org.texttechnologylab.DockerUnifiedUIMAInterface.lua.DUUILuaContext;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;

public class CAS_Impl implements CAS {
    private Rede rede;
    private JCas pCas;
    /**
     * @return serializedCAS
     */
    @Override
    public String getSerializedCas() {
        return serializedCas;
    }
    private String serializedCas;

    /**
     * erstellt CAS_Impl-Objekt
     * @param rede Rede-Objekt
     */
    public CAS_Impl(Rede rede){
        this.rede = rede;
        try {
            pCas = JCasFactory.createText("");
        } catch (UIMAException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Rede getRede() {
        return rede;
    }
    /**
     * /erstellt von Angelov
     *
     * erstellt ein JCas Objekt und analysiert es mit:
     * spaCy
     * GerVader
     * ParlBert
     * HINWEIS: die docker containers müssen auf ports 1001,1002 und 1000 laufen,
     * sodass diese Funktion funktioniert
     * @param text Rede zu analysieren
     * @throws Exception wirft irgendeine Exception, was bedeutet dass etwas schieflief,
     * hoffentlich passiert das nie.
     */
    @Override
    public void setUpJCas(String text) throws Exception{
        pCas = JCasFactory.createText(text, "de");
        DUUILuaContext ctx = new DUUILuaContext().withJsonLibrary();
        DUUIComposer composer = new DUUIComposer()
                .withLuaContext(ctx);
        DUUIRemoteDriver remoteDriver = new DUUIRemoteDriver();
        composer.addDriver(remoteDriver);
        composer.add(new DUUIRemoteDriver.Component("http://localhost:1001").build());
        composer.add(new DUUIRemoteDriver.Component("http://localhost:1002").build());
        composer.add(new DUUIRemoteDriver.Component("http://localhost:1000")
                .withParameter("selection", "text")
                .build());
        composer.run(pCas);
    }

    /**
     * serialisiert das CAS-Objekt
     * @throws IOException darf ignoriert werden
     */
    @Override
    public void serializeJCas() throws java.io.IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CasIOUtils.save(pCas.getCas(), baos, SerialFormat.XCAS);

        serializedCas = baos.toString("UTF-8");
        filterSerializedJCas();
    }

    /**
     * filtriert unnötige Informationen von CAS-Objekt
     */
    private void filterSerializedJCas(){
        String[] lines = serializedCas.split("\n");
        StringBuilder filteredOutput = new StringBuilder();
        for (String line : lines) {
            if (!(line.startsWith("    <org.texttechnologylab.annotation") ||
                    line.startsWith("    <de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma") ||
                    line.startsWith("    <de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.Morphological"))) {
                filteredOutput.append(line).append("\n");
            }
        }
        serializedCas = filteredOutput.toString();//.replace("\r\n", "");
    }

    /**
     * ladet CAS-Objekt vom DB
     * @throws UIMAException
     * @throws IOException
     */
    @Override
    public void loadJCasFromDB() throws UIMAException, IOException {
        //System.out.println("The CAS is loaded just for testing should remove it from loadJCasFromDB in CAS_Impl.java");
        /*try {
            pCas = JCasFactory.createText("");
            String filePath = "reden/cas.txt";

            FileInputStream fileInputStream = new FileInputStream(filePath);

            InputStream inputStream = (InputStream) fileInputStream;
            CasIOUtils.load(inputStream, pCas.getCas());

            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        String casString = getCASStringFromDB();
        if (casString==null){
            System.out.println("THERE IS NO CAS ANALYSIS FOR THIS SPEECH!");
            return;
        }
        InputStream stream = new ByteArrayInputStream(casString.getBytes(StandardCharsets.UTF_8));
        CasIOUtils.load(stream, pCas.getCas());
    }

    /**
     * liefert das CAS-Objekt als Text (xml datei)
     * @return CAS-Objekt
     */
    private String getCASStringFromDB(){
        Bson filter = eq("_id", rede.getID());
        for (Document doc : MongoDBHandler.getCollectionCAS().find(filter)){
            return doc.getString("cas");
        }
        return null;
    }

    /**
     * liefert die Sentimentdateien in einer Liste:
     * erstes Element ist das Sentiment des ganzen Texts
     * zweites ist das Sentiment des ersten Satzes
     * drittes des zweiten Satzes
     * usw.
     * @return
     */
    @Override
    public List<Double> getSentimentData(){
        return JCasUtil.select(pCas,Sentiment.class).
                stream().map(Sentiment::getSentiment).
                collect(Collectors.toList());
    }
    /**
     * liefert die Topicdateien in einem Map:
     * Schluessel -> topic, Wert -> score
     * @return Topicdateien
     */
    @Override
    public Map<String,Double> getTopicData(){
        Map<String, Double> topicData = new HashMap<>();
        for (CategoryCoveredTagged cct: JCasUtil.select(pCas, CategoryCoveredTagged.class)){
            if (!topicData.containsKey(cct.getValue())){
                topicData.put(cct.getValue(),cct.getScore());
            }
            else{
                topicData.put(cct.getValue(),topicData.get(cct.getValue())+cct.getScore());
            }
        }
        return topicData;
    }
    /**
     * liefert die Haefigkeit von NamedEntities in einem Map:
     * Schluessel -> NamedEntity, Wert -> Haefigkeit von dem NamedEntity
     * @return Haefigkeit von dem NamedEntity
     */
    @Override
    public Map<String,Integer> getNamedEntityOccurance(){
        return JCasUtil.select(pCas, NamedEntity.class).stream().
                collect(Collectors.toMap(
                        NamedEntity::getValue,entity->1,
                        Integer::sum));
    }
    /**
     * liefert die Haefigkeit von POS in einem Map:
     * Schluessel -> POS, Wert -> Haefigkeit von dem POS-Objekts
     * @return POS-Objekts
     */
    @Override
    public Map<String,Integer> getPOSOccurance(){
        return JCasUtil.select(pCas, POS.class).stream().
                collect(Collectors.toMap(
                        POS::getPosValue,entity->1,
                        Integer::sum));
    }
    /**
     * extraktiert die NamedEntities Information über der Rede von der CAS Objekt
     * @return NamedEntities Information in JSON Format
     */
    @Override
    public String extractNamedEntities(){
        String jsonResult = "[";
        int indexPOS = 0;
        for (NamedEntity namedEntity : JCasUtil.select(pCas, NamedEntity.class)){
            if (indexPOS == 0){
                indexPOS++;
                continue;
            }
            indexPOS++;
            int begin, end;
            String value;
            begin = namedEntity.getBegin();
            end = namedEntity.getEnd();
            value = namedEntity.getValue();
            String jsonElem = "{";
            jsonElem+="\"begin\":" + begin + ", \"end\": " + end + ", \"value\": \"" + value+"\"";
            jsonElem+="}";
            if (indexPOS < JCasUtil.select(pCas,NamedEntity.class).stream().count()){
                jsonElem+=",";
            }

            jsonResult+=jsonElem;
        }
        jsonResult += "]";
        return jsonResult;
    }
    /**
     * extraktiert die Sentiment Information über der Rede von der CAS Objekt
     * @return Sentiment Information in JSON Format
     */
    @Override
    public String extractSentiment(){
        String jsonResult = "[";
        int indexSentiment = 0;
        for (Sentiment sentiment : JCasUtil.select(pCas, Sentiment.class)){
            if (indexSentiment == 0){
                indexSentiment++;
                continue;
            }
            indexSentiment++;
            int begin, end;
            double sentimentValue;
            begin = sentiment.getBegin();
            end = sentiment.getEnd();
            sentimentValue = sentiment.getSentiment();
            String jsonElem = "{";
            jsonElem+="\"begin\":" + begin + ", \"end\": " + end + ", \"sentiment\": " + sentimentValue;
            jsonElem+="}";
            if (indexSentiment < JCasUtil.select(pCas,Sentiment.class).stream().count()){
                jsonElem+=",";
            }

            jsonResult+=jsonElem;
        }
        jsonResult += "]";
        return jsonResult;
    }
}