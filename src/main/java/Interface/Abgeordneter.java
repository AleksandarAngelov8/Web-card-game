package Interface;

import java.sql.Date;

public interface Abgeordneter {
        Object getID();
        String getName();
        String getVorname();
        String getOrtszusatz();
        String getAdelssuffix();
        String getAnrede();
        String getAkadTitel();
        Date getGeburtsDatum();
        String getGeburtsOrt();
        String getGeschlecht();
        String getReligion();
        String getBeruf();
        String getVita() throws NullPointerException;

        String getPartei() throws NullPointerException;
        String getBuro();
        String getWahlkreiburo();
        SozialeMedien getMedien();
        String getStand();
        String getVollName();
        Rede getRede();

}
