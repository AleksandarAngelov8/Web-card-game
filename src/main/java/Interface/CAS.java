//erstellt von Angelov
package Interface;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.util.CasIOUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.hucompute.textimager.uima.type.Sentiment;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;

public interface CAS {
    /**
     * @return Rede Objekt des CAS-Objekts
     */
    Rede getRede();

    /**
     * @return serializedCAS
     */
    String getSerializedCas();
    /**
     * erstellt ein JCas Objekt und analysiert es mit:
     * spaCy
     * GerVader
     * ParlBert
     * @param text Rede zu analysieren
     * @throws Exception wirft irgendeine Exception, was bedeutet dass etwas schiefliefft,
     * hoffentlich passiert das nie.
     */
    void setUpJCas(String text) throws Exception;
    /**
     * serialisiert das CAS-Objekt
     * @throws IOException darf ignoriert werden
     */
    void serializeJCas() throws java.io.IOException;
    void loadJCasFromDB() throws UIMAException, IOException;

    /**
     * liefert die Sentimentdateien in einer Liste:
     * erstes Element ist das Sentiment des ganzen Texts
     * zweites ist das Sentiment des ersten Satzes
     * drittes des zweiten Satzes
     * usw.
     * @return
     */
     List<Double> getSentimentData();
    /**
     * liefert die Topicdateien in einem Map:
     * Schluessel -> topic, Wert -> score
     * @return Topicdateien
     */
     Map<String,Double> getTopicData();
    /**
     * liefert die Haefigkeit von NamedEntities in einem Map:
     * Schluessel -> NamedEntity, Wert -> Haefigkeit von dem NamedEntity
     * @return Haefigkeit von dem NamedEntity
     */
     Map<String,Integer> getNamedEntityOccurance();
    /**
     * liefert die Haefigkeit von POS in einem Map:
     * Schluessel -> POS, Wert -> Haefigkeit von dem POS-Objekts
     * @return POS-Objekts
     */
     Map<String,Integer> getPOSOccurance();
    /**
     * extraktiert die NamedEntities Information über der Rede von der CAS Objekt
     * @return NamedEntities Information in JSON Format
     */
    String extractNamedEntities();
    /**
     * extraktiert die Sentiment Information über der Rede von der CAS Objekt
     * @return Sentiment Information in JSON Format
     */
    String extractSentiment();
}