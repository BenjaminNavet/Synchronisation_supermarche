import java.util.List;
import java.util.Map;

public class Client extends Thread{

    String nom;
    Map<String, Integer> listeCourses;
    List<Rayon> rayons;
    int tpsParcoursRayons;
    Chariot chariot;
    Caisse caisse;
    String[] listeProduits;
    int idxClient;



    public Client(int idxClient,String nom, Map<String, Integer> listeCourses, List<Rayon> rayons, int tpsParcoursRayons, Chariot chariot, Caisse caisse, String[] listeproduits) {
        this.idxClient=idxClient;
        this.nom = nom;
        this.listeCourses = listeCourses;
        this.rayons = rayons;
        this.tpsParcoursRayons = tpsParcoursRayons;
        this.chariot = chariot;
        this.caisse = caisse;
        this.listeProduits = listeproduits;
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


        // passage en caisse
        caisse.entrerEnCaisse(this);

        for (int a = 0; a < listeCourses.size(); a++){
            for (int b = 0; b < listeCourses.get(listeProduits[a]); b ++) {
                caisse.avant_prod();
                caisse.prod(a);
                caisse.apres_prod();
            }
        }
        // client suivant : marqueur -1
        caisse.avant_prod();
        caisse.prod(-1);
        caisse.apres_prod();
        caisse.sortirDeCaisse(this);


        //retour chariot
        chariot.rendreChariot(this);


    }

}

