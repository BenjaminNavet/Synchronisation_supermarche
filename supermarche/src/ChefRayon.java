import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChefRayon extends Thread{

    /**
     * La liste contenant le nombre d'exemplaires pour chaque produit que transporte le chef de rayon
     */
    private Map<Integer, Integer> chargement = new HashMap();

    /**
     * La liste des rayons du supermarché
     */
    private List<Rayon> rayons;

    /**
     * Le temps que met le chef de rayon pour aller d'un rayon à un autre
     */
    private int tpsParcoursRayons;


    /**
     * Le temps que met le chef de rayon pour se réapprovisionner dans l'entrepôt
     */
    private int tpsParcoursEntrepot;


    /**
     * Le nombre maximum d'exemplaires de chaque produit que peut transporter le chef de rayon à la fois
     */
    private int maxChgtParProduit;

    /**
     * L'entrepôt dans lequel le chef de rayon va se réapprovisionner
     */
    private Entrepot entrepot;

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

    /** Méthode permettant de connaître le nombre d'exemplaires d'un produit dont dispose le chef de rayon
     * @param index : l'index du rayon dont on souhaite connaître le stock
     * @return le nombre d'exemplaires d'un produit dont dispose le chef de rayon
     */
    public int getStock(Integer index) {
        return chargement.get(index);
    }

    /**
     * Méthode permettant au chef de rayon de se réapprovisioner à l'entrepôt
     */
    public void recharge(){
        for (Rayon rayon : rayons) {
            // Le nombre maximum d'exemplaires que le chef de rayon peut transporter en plus de ceux qu'il a déjà
            int nbArticlesDemande = this.maxChgtParProduit - this.chargement.get(rayon.getIndex());
            // Le chef de rayon prend le maximum d'exemplaires disponibles (addChargement) à l'entrepôt (inférieur ou
            // égal au nombre d'articles qu'il a demandé)
            int addChargement = entrepot.takeProductFromEntrepot(rayon.getIndex(), nbArticlesDemande);
            // Mise à jour du nombre d'exemplaires que le chef de rayon transporte
            this.chargement.put(rayon.getIndex(), addChargement);
        }

        try {
            // Le chef de rayon met un temps `tpsParcoursEntrepot` pour se réapprovisioner à l'entrepôt
            sleep(tpsParcoursEntrepot);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Le chef de rayon sort de l'entrepôt en étant chargé au maximum.");

    }

    /**
     * Méthode permettant au chef de rayon de changer de rayon
     */
    public void changeDeRayon(){
        try {
            System.out.println("Le chef de rayon est en mouvement.");
            // Le chef de rayon met un temps `tpsParcoursRayons` pour changer de rayon
            sleep(tpsParcoursRayons);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        while (true) {
            // Le chef de rayon va se réapprovisionner à l'entrepôt
            recharge();

            // Le chef de rayon va au premier rayon
            changeDeRayon();

            for (Rayon rayon : rayons) {
                // Le chef de rayon réapprovisionne le rayon
                rayon.setChefRayonSurPlace(true);
                // Nombre d'article(s) que le chef de rayon a mis en rayon après avoir réapprovisionné le rayon
                // (ou tenté de le faire)
                int nombreDeProduitsDecharges = rayon.equilibrage(this);
                // Nombre d'exemplaire(s) du produit dont dispose le chef de rayon après avoir réapprovisionné le rayon
                int newStockChargement = this.chargement.get(rayon.getIndex()) - nombreDeProduitsDecharges;
                // Mise à jour du nombre d'exemplaire(s) du produit dont dispose le chef de rayon
                this.chargement.put(rayon.getIndex(), newStockChargement);
                // Le chef de raron passe au rayon suivant
                changeDeRayon();
            }
        }
    }
}