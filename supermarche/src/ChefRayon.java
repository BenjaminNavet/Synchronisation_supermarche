import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChefRayon extends Thread{

    Map<Integer, Integer> chargement = new HashMap();
    List<Rayon> rayons;
    int tpsParcoursRayons, tpsParcoursEntrepot, maxChgtParProduit;
    Entrepot entrepot;

    public ChefRayon(List<Rayon> rayons, int tpsParcoursRayons, int tpsParcoursEntrepot, int maxChgtParProduit, Entrepot entrepot) {

        this.rayons = rayons;
        for (Rayon rayon : rayons) {
            chargement.put(rayon.getIndex(), 0);
        }
        this.tpsParcoursRayons    =  tpsParcoursRayons;
        this.tpsParcoursEntrepot  =  tpsParcoursEntrepot;
        this.maxChgtParProduit    =  maxChgtParProduit;
        this.entrepot=entrepot;
    }

    public int getStock(Integer index) {
        return chargement.get(index);
    }

    public void recharge(){
        for (Rayon rayon : rayons) {
            int nbArticlesDemande = Math.min(this.maxChgtParProduit - this.chargement.get(rayon.getIndex()), maxChgtParProduit);
            int addChargement = entrepot.takeProductFromEntrepot(rayon.getIndex(), nbArticlesDemande);
            this.chargement.put(rayon.getIndex(), addChargement);
        }
        try {
            sleep(tpsParcoursEntrepot);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Le chef de rayon sort de l'entrepôt en étant chargé au maximum.");

    }

    public void changeDeRayon(){
        try {
            System.out.println("Le chef de rayon est en mouvement.");
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

            for (Rayon rayon : rayons) {
                rayon.setChefRayonSurPlace(true);
                int nombreDeProduitsDecharges = rayon.equilibrage(this);
                int newStockChargement = this.chargement.get(rayon.getIndex()) - nombreDeProduitsDecharges;
                this.chargement.put(rayon.getIndex(), newStockChargement);
                changeDeRayon();
            }

        }
    }
}