package data;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class dateCheck {
    /**
     * erstellt von Feride Yilmaz
     *
     * konvergiert String in Date Format
     * @param sDatum
     * @return Datum als {@code java.sql.Date} Object
     * falls kein Datum angegeben ist {@code null}
     */
    public static Date dateCheck(String sDatum) {
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy");
        java.util.Date utilDatum = null;
        java.sql.Date sqlDatum = null;

        try {  // falls das Datum angegeben ist wird es hier konvertiert
            utilDatum = date.parse(sDatum);  //  parse ist nur mit util.Date möglich
            sqlDatum = new java.sql.Date(utilDatum.getTime()); // konvergiert util.Date --> sql.Date
        } catch (ParseException e) {
            // falls kein Datum angegeben ist bleibt sqlDatum = null
        }
        return sqlDatum;
    }
}

