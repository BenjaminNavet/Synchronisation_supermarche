import java.util.List;
import java.util.Map;

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

        chariot.prendreChariot(this);

        // 'autres temps sont négligés' ??
        // changeDeRayon();

        for (Rayon rayon : rayons) {
            int quantiteVoulue = listeCourses.get(rayon.getName());
            for (int j = 0; j < quantiteVoulue; j++) {
                rayon.takeProduct(this);
            }

            changeDeRayon();
        }


        chariot.rendreChariot(this);


    }

}

