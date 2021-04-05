import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChefRayon extends Thread{

    Map<String, Integer> chargement = new HashMap();
    List<Rayon> rayons;
    int tpsParcoursRayons, tpsParcoursEntrepot, maxChgtParProduit;
    Entrepot entrepot;

    public ChefRayon(List<Rayon> rayons, int tpsParcoursRayons, int tpsParcoursEntrepot, int maxChgtParProduit, Entrepot entrepot) {

        this.rayons = rayons;
        for (Rayon rayon : rayons) {
            chargement.put(rayon.getName(), 0);
        }
        this.tpsParcoursRayons    =  tpsParcoursRayons;
        this.tpsParcoursEntrepot  =  tpsParcoursEntrepot;
        this.maxChgtParProduit    =  maxChgtParProduit;
        this.entrepot=entrepot;
    }

    public int getStock(String name) {
        return chargement.get(name);
    }

    public void recharge(){
        for (Rayon rayon : rayons) {
            int nbArticlesDemande = Math.min(this.maxChgtParProduit - this.chargement.get(rayon.getName()), maxChgtParProduit);
            int addChargement = entrepot.takeProductFromEntrepot(rayon.getName(), nbArticlesDemande);
            //System.out.println(rayon.getName()+"{"+" max :" +this.maxChgtParProduit+ " , ask : "+nbArticlesDemande+" , return :"+addChargement+"}");
            this.chargement.put(rayon.getName(), addChargement);

            //System.out.println("Le chef de rayon prend "+addChargement+" "+rayon.getName()+ " dans l'entrepot. "+"Nouveau chargement de "+rayon.getName()+" : "+this.chargement.get(rayon.getName())+"."+" Il en avait demandé "+nbArticlesDemande);

        }
        try {
            sleep(tpsParcoursEntrepot);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Le chef de rayon sort de l'entrepôt en étant chargé au maximum");

    }

    public void changeDeRayon(){
        try {
            System.out.println("Le chef de rayon change de rayon");
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
                int newStockChargement = this.chargement.get(rayon.getName()) - nombreDeProduitsDecharges;
                this.chargement.put(rayon.getName(), newStockChargement);
                changeDeRayon();
            }

        }
    }
}