import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Supermarche {
    /**
     * nombre d'exemplaires de chaque articles que le chef de rayon peut transporter dans sa tournée de remplissage
     * des rayons
     */
    private static final int NB_ELEMENT_PAR_CHGT = 3;

    /**
     * le stock initial présent dans les rayons à l'ouverture du magasin
     */
    private static final int RAYON_STOCK_INIT = 10;

    /**
     * nombre maximum d'exemplaires d'un produit dans un rayon
     */
    private static final int RAYON_STOCK_MAX = 10;

    /**
     * nombre d'exemplaires d'un produit dans l'entrepot à l'initialisation (-1 correspond à un stock infini)
     */
    private static final int ENTREPOT_STOCK_INIT = -1;

    /**
     * temps en ms pour parcourir le trajet d'un rayon à un autre
     */
    private static final int TPS_PARCOURS_RAYONS = 200;

    /**
     * temps en ms pour parcourir le trajet du rayon à l'entrepot
     */
    private static final int TPS_PARCOURS_ENTREPOT = 500;

    /**
     * temps en ms pour poser un article en caisse
     */
    private static final int TPS_POSER_ARTICLE = 20;

    /**
     * temps en ms pour scanner un article en caisse
     */
    private static final int TPS_SCANNER_ARTICLE = 15;

    /**
     * temps en ms pour payer en caisse
     */
    private static final int TPS_PAIEMENT = 35;

    /**
     * nombre d'objets présents sur le tapis de caisse
     */
    private static final int TAILLE_TAPIS = 10;

    /**
     *Nombre de chariots dans la file à l'ouverture du magasin
     */
    private static final int NB_CHARIOTS = 3;

    /**
     *Nombre de clients du magasin
     */
    private static final int NB_CLIENTS = 5;

    /**
     *Nombre d'aticles maximum par client pour chaque type d'article
     */
    private static final int NB_MAX_ARTICLE_PAR_CLIENT = 3;

    /**
     * liste des produits présents en magasin
     */
    private static final String[] listeProduits = {"Sucre", "Farine", "Beurre", "Lait"};


    public static void main(String[] args) {

        // Création du chariot
        Chariot chariot = new Chariot(NB_CHARIOTS);

        // Création de la caisse
        Caisse caisse = new Caisse(TAILLE_TAPIS,TPS_POSER_ARTICLE,TPS_SCANNER_ARTICLE);

        // Création de l'accès au tapis de caisse
        AccesTapisCaisse accesTapisDeCaisse = new AccesTapisCaisse();

        // Création de l'accès au paiement
        AccesPaiement accesPaiement= new AccesPaiement(TPS_PAIEMENT);

        // Création de l'employé de caisse
        EmployeCaisse employeCaisse = new EmployeCaisse(caisse,accesPaiement);

        // Création des rayons
        List<Rayon> rayons = new ArrayList<>();
        for (int i = 0; i < listeProduits.length ; i ++ ) {
            rayons.add(new Rayon(i,listeProduits[i], RAYON_STOCK_MAX, RAYON_STOCK_INIT));
        }

        // Création de l'entrepot
        HashMap entrepotHmap = new HashMap();
        for (Rayon rayon : rayons) {
            entrepotHmap.put(rayon.getIndex(), ENTREPOT_STOCK_INIT);
        }
        Entrepot entrepot= new Entrepot(entrepotHmap);

        // Création du chef de rayon
        ChefRayon chef_de_rayon = new ChefRayon(rayons, TPS_PARCOURS_RAYONS, TPS_PARCOURS_ENTREPOT,
                NB_ELEMENT_PAR_CHGT,entrepot);

        // Création des clients
        List<Client> listeClients = new ArrayList<>();
        HashMap listeDeCourses;

        for (int i = 0; i < NB_CLIENTS; i++) {
            // Liste de courses du client i
            listeDeCourses = new HashMap();
            for (int j = 0; j < listeProduits.length ; j ++ ) {
                listeDeCourses.put(j, (int) (Math.random() * (NB_MAX_ARTICLE_PAR_CLIENT+1)));
            }

            // Ajout du nouveau client i à la liste des clients
            listeClients.add(new Client(i, listeDeCourses, rayons, TPS_PARCOURS_RAYONS, chariot, caisse,accesTapisDeCaisse,accesPaiement));
        }

        // On créé un deamon pour que le thread `chef_de_rayon` s'arrête une fois que tous les clients ont terminés
        // leur exécution afin qu'il ne bloque pas le programme en tournant indéfiniment
        chef_de_rayon.setDaemon(true);

        // On créé un deamon pour que le thread `employeCaisse` s'arrête une fois que tous les clients ont terminés
        // leur exécution afin qu'il ne bloque pas le programme en tournant indéfiniment
        employeCaisse.setDaemon(true);

        // Le chef de rayon est mis en route
        chef_de_rayon.start();
        // L'employé de caisse est mis en route
        employeCaisse.start();
        // Tous les clients sont mis en route
        for (Client client : listeClients) {
            client.start();
        }

    }
}
