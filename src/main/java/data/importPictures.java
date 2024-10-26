package data;

import Interface.Abgeordneter;
import Interface.Pictures;

import static data.abgeordneterErstellen.abgeordneterList;
import static data.pictures.pictures;
public class importPictures {
    /**
     * erstellt von Feride Yilmaz
     *
     * Methode importiert fotos der Abgeordneten in die DB
     */
    public static void impPicture(){

        for (Abgeordneter abgeordneter : abgeordneterList){
            String name2;
            if (abgeordneter.getAnrede().equals("")) {
                name2 = abgeordneter.getVorname() + " " + abgeordneter.getName();
            } else {
                name2 = abgeordneter.getAkadTitel() + " " + abgeordneter.getVorname() + " " + abgeordneter.getName();
            }
            for (Pictures pictures1 : pictures){

                if (name2.equals(pictures1.getFullName())){
                    System.out.println("hat geklappt");
                }
            }
        }

    }
}
