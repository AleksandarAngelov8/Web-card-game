package data;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import java.io.File;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.net.URL;

public class extractZip {
    /**
     * erstellt von Feride Yilmaz
     *
     * entpackt zip Ordner und speichert inhalt in src/main/resources/stammdaten/stammdatenZipEntpackt
     * @throws IOException
     */
    public static void entpackeZip() throws IOException {
        String zipOrdner = "src/main/resources/stammdaten/MdB-Stammdaten.zip";
        String zielOrdner = "src/main/resources/stammdaten/stammdatenZipEntpackt";
        File ordner = new File(zielOrdner);

        File zielOrdnerFile = new File(zielOrdner);
        byte[] by = new byte[1024];
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipOrdner))){
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null){
                String name = zipEntry.getName();
                File file = new File(zielOrdner + File.separator + name);
                new File(file.getParent()).mkdirs();
                try (FileOutputStream fileOutputStream = new FileOutputStream(file)){
                    int zahl;
                    while ((zahl = zipInputStream.read(by))>0){
                        fileOutputStream.write(by, 0,zahl);
                    }

                }zipEntry = zipInputStream.getNextEntry();
            }zipInputStream.closeEntry();
        }
    }
}
