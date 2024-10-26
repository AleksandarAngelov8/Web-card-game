package MongoDB_Impl;

import Interface.Abgeordneter;
import Interface.Protokoll;
import Interface.Rede;
import MongoDBConnectionHandler.MongoDBHandler;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpEntity;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.apache.uima.UIMAException;
import org.bson.Document;
import org.bson.conversions.Bson;
import xml_Impl.Rede_xml_Impl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * erstellt von Feride Yilmaz
 */

public class Protokoll_MongoDB_Impl implements Protokoll {
    private MongoCollection<Document> collection;
    private Document pDocument;

    public Protokoll_MongoDB_Impl(MongoCollection<Document> collection, Document pDocument) {
        this.collection = collection;
        this.pDocument = pDocument;
    }

    @Override
    public Object getID() {
        Object id = pDocument.get("_id");
        if (id != null) {
            return id;
        } else {
            return "null";
        }
    }
    public void setID(Object x) {
        collection.updateOne(Filters.eq("_id", pDocument.getObjectId("_id")),
                Updates.set("_id", x));
    }

    @Override
    public Date getDatum() {
        Date datum = new java.sql.Date(pDocument.getDate("Datum").getTime());
        if (datum != null) {
            return datum;
        } else {
            return null;
        }
    }
    public void setDate(Date x) {
        collection.updateOne(Filters.eq("_id", pDocument.getObjectId("_id")),
                Updates.set("Datum", x));
    }

    @Override
    public String getTitel() {
        String titel = pDocument.getString("Titel");
        if (titel != null) {
            return titel;
        } else {
            return null;
        }
    }
    public void setTitel(String x) {
        collection.updateOne(Filters.eq("_id", pDocument.getObjectId("_id")),
                Updates.set("Titel", x));
    }

    @Override
    public String getWahlperiode() {
        String wahlperiode = pDocument.getString("Wahlperiode");
        if (wahlperiode != null) {
            return wahlperiode;
        } else {
            return "null";
        }
    }
    public void setWahlperiode(String x) {
        collection.updateOne(Filters.eq("_id", pDocument.getObjectId("_id")),
                Updates.set("Wahlperiode", x));
    }

    @Override
    public String getOrt() {
        String ort = pDocument.getString("Ort");
        if (ort != null) {
            return ort;
        } else {
            return "null";
        }
    }
    public void setOrt(String x) {
        collection.updateOne(Filters.eq("_id", pDocument.getObjectId("_id")),
                Updates.set("Ort", x));
    }

    /** erstellt von Angelov
     * @param docUse nutzlos
     * @return liste von reden
     */
    public List<Rede> getReden(boolean docUse) {
        List<Document> redenDoc = pDocument.getList("Reden",Document.class);
        List<Object> redenIds = redenDoc.stream().map(doc -> doc.get("RedeID")).collect(Collectors.toList());
        Bson filter = Filters.in("_id",redenIds);
        List<Rede> reden = new ArrayList<>();
        System.out.println(redenDoc.size());
        int i = 0;
        for (Document document : MongoDBHandler.getCollectionSpeech().find(filter)) {
            Rede rede = new Rede_MongoDB_Impl(collection,document,false);
            System.out.println(i++ +"/"+ redenDoc.size());
            reden.add(rede);

        }
        return reden;
    }
    @Override
    public List<Rede> getReden() {
        List<Rede> reden = new ArrayList<>();
        FindIterable<Document> dokumente = collection.find();  // Dokumente aus der Collection
        for (Document document : dokumente) {
            List<Document> redeListe = (List<Document>) document.get("Reden");
            for (Document redeDokumente : redeListe) {
                Object rednerID = redeDokumente.get("RednerID");
                Object redeID = redeDokumente.get("RedeID");
                String text = redeDokumente.getString("Text");


                Rede rede = new Rede_xml_Impl(rednerID, redeID, text);
                reden.add(rede);
            }

        }
        return reden;
    }
    public void setReden(List<Rede> reden) {
        List<Document> documents = new ArrayList<>();
        for (Rede rede : reden) {
            Document redeDokument = new Document("RednerID", rede.getRednerID())
                    .append("RedeID", rede.getID())
                    .append("Text", rede.getText());
            documents.add(redeDokument);

            collection.insertOne(redeDokument);
        }
        collection.updateOne(Filters.eq("_id", pDocument.getObjectId("_id")),
                Updates.addToSet("Reden", documents));
    }

    @Override
    public String getRedner() {
        FindIterable<Document> dokumente = collection.find();  // Dokumente aus der Collection
        for (Document document : dokumente) {
            List<Document> redeListe = (List<Document>) document.get("Reden");
            for (Document redeDokumente : redeListe) {
                Object rednerID = redeDokumente.get("RednerID");
                if (rednerID != null) {
                    return rednerID.toString();
                    // alle Reden im Protokoll werden zu einem String zusammengefasst
                }
            }
        }
        return null; // falls keine Rede im Protokoll steht
    }



    @Override
    public String getStart() {
        String start = pDocument.getString("Start");
        if (start != null) {
            return start;
        } else {
            return "null";
        }
    }
    public void setStart(String x) {
        collection.updateOne(Filters.eq("_id", pDocument.getObjectId("_id")),
                Updates.set("Start", x));
    }
     @Override
    public String getEnde() {
        String ende = pDocument.getString("Ende");
        if (ende != null) {
            return ende;
        } else {
            return "null";
        }
    }

    @Override
    public Object getSitzungsnummer() {
        String x = pDocument.getString("Sitzungsnummer");
        if (x != null){
            return x;
        }else{
            return "null";
        }
    }

    public void setEnde(String x){
        collection.updateOne(Filters.eq("_id", pDocument.getObjectId("_id")),
                Updates.set("Ende",x));
    }

    /** erstellt von Angelov
     * erstellt latex formatierte zeichenkette vom template
     * @return formatierte zeichenkette
     */
    private String getLatex(){
        String template = readTemplateFromFile("src/main/resources/public/LaTeX/template/template.tex");
        Map<Rede, Abgeordneter> redeTexts = getReden(true).stream()
                .collect(Collectors.toMap(speech -> speech, speech -> speech.getRedner()));
        StringBuilder formattedSpeeches = new StringBuilder();
        int i = 1;
        for (Rede rede : redeTexts.keySet()) {
            Abgeordneter_MongoDB_Impl speaker = (Abgeordneter_MongoDB_Impl) redeTexts.get(rede);
            if(speaker.getDocument() == null){
                continue;
            }
            formattedSpeeches.append("\n\n\n\n\n\t" + (i++) + ". " +
                    speaker.getVorname() + " " +
                    speaker.getName() + " (" +
                    speaker.getPartei() + ") " +
                    rede.getText());

        }

        String redeTextsS = formattedSpeeches.toString();

        System.out.println("ID OF THE PROTOCOL IS: " + getID());
        return String.format(template, getID(), getSitzungsnummer(), getTitel(), getOrt(),
                getDatum(), getWahlperiode(), getStart(), getEnde(), redeTextsS);
    }

    /** erstellt von Angelov
     * liest die latexdatei, kompiliert sie und speichert es in directory
     * @param directory eingegebenes directory
     */
    public void exportProtocol(String directory) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/public/LaTeX/protocol.tex"))) {
            writer.write(getLatex());
            System.out.println("LaTeX document exported successfully.");
        } catch (IOException e) {
            System.err.println("Error exporting LaTeX document: " + e.getMessage());
        }

        compileLaTeX(directory);
    }

    /** erstellt von Angelov
     * kompiliert die Latexdatei
     * @param directory eingegebenes directory
     */
    private void compileLaTeX(String directory) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("pdflatex", "protocol.tex");
            processBuilder.directory(new File(directory));

            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("LaTeX compilation successful.");
            } else {
                System.err.println("LaTeX compilation failed.");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /** erstellt von angelov
     * @param fileName dateiname
     * @return string des gelesenen Templates
     */
    public String readTemplateFromFile(String fileName) {
        try {
            Path path = Paths.get(fileName);
            return Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

