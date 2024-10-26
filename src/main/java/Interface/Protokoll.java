package Interface;
import java.sql.Date;
import java.util.List;

public interface Protokoll {
    Object getID();
    Date getDatum();
    String getTitel();
    String getWahlperiode();
    String getOrt();

    List<Rede> getReden();
    String getRedner();
    String getStart();
    String getEnde();
    Object getSitzungsnummer();


}
