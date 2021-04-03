import java.util.List;
import java.util.ArrayList;

public class Supermarche {
    /**
     * nombre d'exemplaires de chaque articles que le chef de rayon peut transporter dans sa tournée de remplissage des rayons
     */
    private static final int NB_ELEMENT_PAR_CHGT = 5;

    /**
     * le stock initial présent dans les rayons à l'ouverture du magasin
     */
    private static final int RAYON_STOCK_INIT = 50;

    /**
     * nombre maximum d'exemplaires d'un produit dans un rayon
     */
    private static final int RAYON_STOCK_MAX = 50;

    /**
     * temps en ms pour parcourir le trajet d'un rayon à un autre
     */
    private static final int TPS_PARCOURS_RAYONS = 200;

    /**
     * temps en ms pour parcourir le trajet du rayon à l'entrepot
     */
    private static final int TPS_PARCOURS_ENTREPOT = 500;

    /**
     * nombre d'objets présents sur le tapis de caisse
     */
    private static final int TAILLE_TAPIS = 20;

    /**
     *Nombre de chariots dans la file à l'ouverture du magasin
     */
    private static final int NB_CHARIOTS = 15;

    /**
     * liste des produits présents en magasin
     */
    private static final String[] listeProduits = {"Sucre", "Farine", "Beurre", "Lait"};


    public static void main(String[] args) {

        List<Rayon> rayons = new ArrayList<Rayon>();
        for (int i = 0; i < listeProduits.length ; i ++ ) {
            rayons.add(new Rayon(listeProduits[i], RAYON_STOCK_MAX, RAYON_STOCK_INIT));
        }

        ChefRayon chef_de_rayon = new ChefRayon(rayons);

    }
}
