import java.util.List;
import java.util.Map;

public class Client extends Thread{

    int idxClient;
    Map<Integer, Integer> listeCourses;
    int tpsParcoursRayons;
    Chariot chariot;
    Caisse caisse;
    List<Rayon> rayons;



    public Client(int idxClient, Map<Integer, Integer> listeCourses, List<Rayon> rayons, int tpsParcoursRayons, Chariot chariot, Caisse caisse) {
        this.idxClient=idxClient;
        this.listeCourses = listeCourses;
        this.rayons = rayons;
        this.tpsParcoursRayons = tpsParcoursRayons;
        this.chariot = chariot;
        this.caisse = caisse;
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
            int quantiteVoulue = listeCourses.get(rayon.getIndex());
            for (int j = 0; j < quantiteVoulue; j++) {
                rayon.takeProduct(this);
            }

            changeDeRayon();
        }


        // passage en caisse
        caisse.deposeSurTapisDeCaisse(this);

        int a = 0;
        for (Rayon rayon : rayons) {
            int quantiteVoulue = listeCourses.get(rayon.getIndex());
            for (int j = 0; j < quantiteVoulue; j++) {
                caisse.avant_prod();
                caisse.prod(a,this);
                caisse.apres_prod();
            }
            a+=1;
        }


        // client suivant : marqueur -1
        caisse.avant_prod();
        caisse.prod(-1,this);
        caisse.apres_prod();

        caisse.paiement(this);

        //retour chariot
        chariot.rendreChariot(this);


    }

}

