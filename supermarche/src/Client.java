import java.util.List;
import java.util.Map;

public class Client extends Thread{

    /**
     * Le numéro du client
     */
    private int idxClient;

    /**
     * La liste de courses du client
     */
    private Map<Integer, Integer> listeCourses;

    /**
     * Le temps que met un client pour se déplacer entre deux rayons
     */
    private int tpsParcoursRayons;


    /**
     * La file de chariots dans laquelle le client va prendre puis rendre un chariot
     */
    private Chariot chariot;

    /**
     * La caisse que va utiliser le client pour déposer ses articles sur le tapis puis pour payer
     */
    private Caisse caisse;

    /**
     * Le tapis de caisse de la caisse
     */
    private AccesTapisCaisse accesTapisDeCaisse;

    /**
     * Acces au paiement lors du passage en caisse
     */
    private AccesPaiement accesPaiement;

    /**
     * La liste des rayons du supermarché
     */
    private List<Rayon> rayons;

    public Client(int idxClient, Map<Integer, Integer> listeCourses, List<Rayon> rayons, int tpsParcoursRayons, Chariot chariot, Caisse caisse, AccesTapisCaisse accesTapisDeCaisse, AccesPaiement accesPaiement) {
        this.idxClient=idxClient;
        this.listeCourses = listeCourses;
        this.rayons = rayons;
        this.tpsParcoursRayons = tpsParcoursRayons;
        this.chariot = chariot;
        this.caisse = caisse;
        this.accesTapisDeCaisse=accesTapisDeCaisse;
        this.accesPaiement=accesPaiement;
    }

    /** Méthode qui retourne le numéro du client
     * @return idxClient : le numéro du client
     */
    public int getIndex() {
        return idxClient;
    }

    /**
     * Méthode permettant au client de changer de rayon
     */
    public void changeDeRayon(){
        try {
            // Le client met un temps `tpsParcoursRayons` pour changer de rayon
            sleep(tpsParcoursRayons);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        // Le client prend un chariot
        chariot.prendreChariot(this);

        // Le client commence ses courses et va au premier rayon
        changeDeRayon();

        // Pour chaque rayon, le client prend le nombre d'articles dont il a besoin puis passe au rayon suivant
        for (Rayon rayon : rayons) {

            // Quantité souhaitée par le client
            int quantiteVoulue = listeCourses.get(rayon.getIndex());

            // Le client prend les produits un par un dans le rayon judqu'à atteindre la quatité voulue
            for (int j = 0; j < quantiteVoulue; j++) {
                rayon.takeProduct(this);
            }

            // Le client change de rayon
            changeDeRayon();
        }

        // Le client pose ses articles sur le tapis de caisse, entrée en exclusion mutuelle
        accesTapisDeCaisse.deposeSurTapisDeCaisse(this);

        //Le client commence à déposer ses articles sur le tapis
        for (Rayon rayon : rayons) {

            // Nombre de produits à déposer sur le tapis = nombre pris dans le rayon d'index rayon.getIndex()
            int quantiteADeposer = listeCourses.get(rayon.getIndex());

            // Le client cherche à déposer sur le tapis de caisse chaque produit qu'il a dans son chariot,
            // 3 phases : avant-production (avant de déposer), production (déposer), après-production (après avoir déposé)
            for (int j = 0; j < quantiteADeposer; j++) {
                caisse.avant_prod();
                caisse.prod(rayon.getIndex(),this);
                caisse.apres_prod();
            }
        }

        // Lorsque le client a fini de poser ses articles sur le tapis de caisse, il pose le marqueur -1 (`client suivant`)
        caisse.avant_prod();
        caisse.prod(-1,this);
        caisse.apres_prod();

        //fin de zone d'exclusion mutuelle du dépôt sur tapis de caisse
        accesTapisDeCaisse.aFiniDeDeposeSurTapisDeCaisse(this);

        // Le client paye ses courses, entrée en exclusion mutuelle
        accesPaiement.entrePaiement(this);

        // Le client a fini de payer, sortie d'exclusion mutuelle
        accesPaiement.sortPaiement(this);

        // Le client a fini de payer, l'employé de caisse recommence donc à scanner
        caisse.setEmployeCaisseAFiniDeScannerPourUnClient(false);

        // Le client rend son chariot
        chariot.rendreChariot(this);
    }

}

