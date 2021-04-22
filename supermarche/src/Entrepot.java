import java.util.Map;

public class Entrepot {

    /**
     * Liste des stocks dans l'entrepôt pour chaque rayon
     */
    private Map<Integer, Integer> StockEntrepot;

    public Entrepot(Map<Integer, Integer> StockEntrepot) {
        this.StockEntrepot=StockEntrepot;
    }

    /**
     * @param index : index du rayon
     * @param nbStockDemande : nombre de produits désirés
     * @return nbStockAjout : le nombre de produits qu'a pu réellement prendre le chef de Rayon
     * Pas besoin de synchronized car un seul thread manipule le stock de l'entrepôt, attention si ajout de thread
     * en interaction avec le stock de l'entrepôt, il faudra protéger en exclusion mutuelle
     */
    public int takeProductFromEntrepot(Integer index, int nbStockDemande){

        // Si le stock dans l'entrepôt pour le rayon est de -1, on considère que le stock est illimité donc
        // nbStockAjout=nbStockDemande. Sinon, on détermine le nombre de produits que peut réellement prendre
        // le chef de rayon et on les retire du stock de l'entrepôt
        if(this.StockEntrepot.get(index)==-1){
            return nbStockDemande;
        }

        else{
            // Nombre de produits que va pouvoir prendre le chef de rayon initialisé à 0
            int nbStockAjout=0;

            // Tant qu'il reste des produits en stock et qu'on n'a pu satisfaire totalement la demande du chef de
            // rayon, on incrémente de 1 le nombre de produits que peut prendre le chef de rayon
            while (this.StockEntrepot.get(index) > 0 && nbStockAjout<=nbStockDemande) {
                // On décrémente le stock de l'entrepôt de 1
                this.StockEntrepot.put(index, this.StockEntrepot.get(index) - 1);
                // On incrémente de 1 le nombre de produits que peut prendre le chef de rayon
                nbStockAjout++;
            }
            return nbStockAjout;
        }
    }

}
