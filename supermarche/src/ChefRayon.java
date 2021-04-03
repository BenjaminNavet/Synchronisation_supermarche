import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

public class ChefRayon extends Thread{

    Map<String, Integer> chargement = new HashMap();
    List<Rayon> rayons;
    int tpsParcoursRayons, tpsParcoursEntrepot, maxChgtParProduit;

    public ChefRayon(List<Rayon> rayons, int tpsParcoursRayons, int tpsParcoursEntrepot, int maxChgtParProduit) {

        this.rayons = rayons;
        for (Rayon rayon : rayons) {
            chargement.put(rayon.getName(), 0);
        }
        this.tpsParcoursRayons    =  tpsParcoursRayons;
        this.tpsParcoursEntrepot  =  tpsParcoursEntrepot;
        this.maxChgtParProduit    =  maxChgtParProduit;
    }


    public int getStock(String name) {
        return chargement.get(name);
    }

    public void recharge(){
        for (int i = 0; i < rayons.size(); i++) {
            Rayon rayon = rayons.get(i);
            this.chargement.put(rayon.getName(), maxChgtParProduit);
            try {
                sleep(tpsParcoursEntrepot);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void changeDeRayon(){
        try {
            sleep(tpsParcoursRayons);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        while (true) {

            // Le chef de Rayon reprend 5 articles pour chaque type de produit
            recharge();
            // Le chef de Rayon va au premier rayon
            changeDeRayon();

            for (int i = 0; i < rayons.size(); i++) {
                Rayon rayon = rayons.get(i);
                rayon.setChefRayonSurPlace(true);
                int nombreDeProduitsDecharges = rayon.equilibrage(this);
                this.chargement.put(rayon.getName(), chargement.get(rayon.getName())- nombreDeProduitsDecharges);
                changeDeRayon();
            }

        }
    }
}