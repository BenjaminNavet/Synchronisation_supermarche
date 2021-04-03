import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

public class Client extends Thread{

    String nom;
    Map<String, Integer> listeCourses;
    List<Rayon> rayons;
    int tpsParcoursRayons;
    Chariot chariot;
    int idxClient;



    public Client(int idxClient,String nom, Map<String, Integer> listeCourses, List<Rayon> rayons, int tpsParcoursRayons, Chariot chariot) {
        this.idxClient=idxClient;
        this.nom = nom;
        this.listeCourses = listeCourses;
        this.rayons = rayons;
        this.tpsParcoursRayons = tpsParcoursRayons;
        this.chariot = chariot;
    }

    public String getNom() {
        return nom;
    }

    public int getIndex() {
        return idxClient;
    }

    public void changeDeRayon(){
        try {
            sleep(tpsParcoursRayons);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        chariot.prendreChariot();

        // 'autres temps sont négligés' ??
        // changeDeRayon();

        for (int i = 0; i < rayons.size(); i++) {
            Rayon rayon = rayons.get(i);

            int quantiteVoulue = listeCourses.get(rayon.getName());
            for (int j = 0; j < quantiteVoulue; j++) {
                rayon.takeProduct(this, rayon);
            }

            changeDeRayon();
        }


        chariot.rendreChariot();


    }

}

