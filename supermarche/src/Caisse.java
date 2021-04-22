import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class Caisse {

    /**
     * Buffer représentant le tapis de caisse
     */
    private Integer[] tapis;

    /**
     * iprod est l'indice du buffer dans lequel le client va déposer son article suivant
     */
    private int iprod;

    /**
     * icons est l'indice du buffer dans lequel l'employé de caisse va prendre l'article suivant pour le scanner
     */
    private int icons;

    /**
     * Nombre d'espace(s) vide(s) sur le tapis
     */
    private int nbvide;

    /**
     * Nombre d'espace(s) rempli(s) sur le tapis
     */
    private int nbplein;

    /**
     * Nombre d'espace(s) sur le tapis
     */
    private int taille_tapis;

    /**
     * Temps que met un client pour poser un article
     */
    private int tps_pose_article;

    /**
     * Temps que met un employé de caisse pour scanner un article
     */
    private int tps_scanne_article;

    /**
     * EmployeCaisseAFiniDeScannerPourUnClient indique si l'employé de caisse a fini de scanner les articles du
     * client en attente de paiement
     */
    private volatile boolean EmployeCaisseAFiniDeScannerPourUnClient = false;


    public Caisse(int taille_tapis, int tps_pose_article, int tps_scanne_article) {
        this.tapis = new Integer[taille_tapis];
        this.nbvide = taille_tapis;
        this.nbplein = 0;
        this.icons = 0;
        this.iprod = 0;
        this.taille_tapis = taille_tapis;
        this.tps_pose_article=tps_pose_article;
        this.tps_scanne_article=tps_scanne_article;
    }

    /**
     * modifie la valeur du booléen EmployeCaisseAFiniDeScannerPourUnClient
     * @param employeCaisseAFiniDeScannerPourUnClient : indique si l'employé de caisse a fini de scanner les articles
     *                                                  du client en attente de paiement
     * Synchronized : exclusion mutuelle pour la variable partagée employeCaisseAFiniDeScannerPourUnClient
     */
    public synchronized void setEmployeCaisseAFiniDeScannerPourUnClient(boolean employeCaisseAFiniDeScannerPourUnClient) {
        EmployeCaisseAFiniDeScannerPourUnClient = employeCaisseAFiniDeScannerPourUnClient;
        // Si on set EmployeCaisseAFiniDeScannerPourUnClient à true, cela veut dire que l'employé de caisse a terminé de
        // scanner les articles du client et restera ensuite en attente dans avant_prod() tant que le client n'a pas
        // payé donc il n'y a pas besoin de réveiller un thread
        // Sinon :
        if(!employeCaisseAFiniDeScannerPourUnClient) {
            // Lorsque le client a terminé de payer, l'employé de caisse recommence à scanner. On a potentiellement
            // plusieurs processus en attente mais on souhaite surtout réveiller le thread de l'employé (il recommencer
            // à scanner). On doit donc réveiller tous les threads pour être sûr que celui de l'employé de caisse se
            // réveille sinon on pourrait se retrouver en situtation d'interblocage
            notifyAll();
        }
    }

    /**
     * Méthode d'avant production permettant de vérifier s'il est possible de déposer un article sur le tapis
     * Synchronized : exclusion mutuelle pour la variable partagée nbvide
     */
    public synchronized void avant_prod() {
        // Il y a 1 seul thread producteur sur cette instance cependant, la possibilité existe de se faire réveiller
        // par le notifyAll() de setEmployeCaisseAFiniDeScannerPourUnClient.
        // Donc dans l'hypothèse ou le consommateur était également bloqué et qu'il n'avait
        // pas encore consommé (tapis toujours plein), il faut revérifier la condition : utilisation de while nécessaire
        while(nbvide == 0) {
            try {
                System.out.println("Aucune place disponible sur le tapis.");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // On décrémente nbvide de 1 : un article va être placé sur le tapis
        nbvide --;
    }

    /** Méthode de production permettant de déposer un article sur le tapis
     * @param produit : indique l'index du produit qui va être placé
     * @param client : permet d'obtenir l'index du client qui dépose un article
     */
    public void prod(int produit, Client client) {

        // Le client met un temps `tps_pose_article` pour poser un article sur le tapis
        try {
            sleep(tps_pose_article);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Si l'index du produit est différent de -1, le client dépose un article sinon, il dépose la marque
        // `client suivant` (-1)
        if(!(produit==-1)){
            System.out.println("Le client n°" + client.getIndex() +" dépose un article "+produit+ " sur le tapis." );
        }

        // On ajoute le produit déposé dans le buffer `tapis` à l'indice iprod
        tapis[iprod] = produit;
    }

    /**
     * Méthode d'après production permettant d'actualiser le nombre de places occupées sur le tapis,
     * et d'actualiser la place sur laquelle l'article suivant va être déposé
     * Synchronized : exclusion mutuelle pour la variable partagée nbplein
     */
    public synchronized void apres_prod() {

        // On actualise la valeur de nbplein en l'incrémentant de 1 pour indiquer à l'employé de caisse que le tapis
        // contient un article de plus
        nbplein++;

        // le thread qui produit à terminé sa production, il réveille le thread consommateur si bloqué.
        // ils ne sont que deux threads sur cette instance Caisse, donc notify réveillera nécessairement
        // le consommateur (caissier)
        notify();

        // Le tapis est circulaire donc l'indice de production suivant est égal à l'indice actuel + 1, modulo la
        // taille du tapis
        iprod= (iprod+1)%taille_tapis;
    }

    /**
     * Méthode d'avant consommation permettant de vérifier s'il est possible de prendre un article sur le tapis
     * Synchronized : entrée en exclusion mutuelle pour la variable nbplein et EmployeCaisseAFiniDeScannerPourUnClient
     */
    public synchronized void avant_cons() {
        // Il y a 1 seul thread consommateur sur cette instance cependant, la possibilité existe de se faire réveiller
        // par le notifyAll() de setEmployeCaisseAFiniDeScannerPourUnClient.
        // Donc dans l'hypothèse ou le producteur était également bloqué et qu'il n'avait
        // pas encore produit (tapis toujours vide), il faut revérifier la condition : utilisation de while nécessaire
        // Tant qu'il n'y a aucun article sur le tapis ou qu'un client est en train de payer,
        // l'employé de caisse est mis en attente
        while(nbplein == 0 || EmployeCaisseAFiniDeScannerPourUnClient) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // On décrémente nbplein de 1 : un article va être enlevé du tapis
        nbplein --;
    }

    /**
     * Méthode de consommation permettant de d'enlever un article du tapis
     */
    public boolean cons() {

        // L'employé de caisse a fini de scanner les articles du client ? Après traitement ci-dessous
        // cette information sera retournée à l'appellant de cons()
        boolean finScan;

        // L'employé de caisse met un temps `tps_scanne_article` pour scanner un article
        try {
            sleep(tps_scanne_article);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Si ce qui est scanné est différent de -1, l'employé de caisse scanne l'article sinon, il indique qu'il a
        // fini de scanner, permettant au client de payer
        if (!(tapis[icons] == -1)) {
            System.out.println("L'employé de caisse scanne un article " + tapis[icons]+".");
            finScan= false;
        } else {
            // On indique que l'employé de caisse a fini de scanner les articles du client
            finScan= true;
            setEmployeCaisseAFiniDeScannerPourUnClient(true);
            System.out.println("L'employé de caisse a fini de scanner le(s) article(s) d'un client.");
        }

        // On retire le produit scanné du buffer `tapis` : indice icons de tapis est réinitialisé à null
        tapis[icons] = null;

        // Indique si l'employé de caisse a fini de scanner les articles ou pas
        return finScan;
    }

    /**
     * Synchronized : entrée en exclusion mutuelle pour la variable nbvide
     */
    public synchronized void apres_cons() {

        // On actualise la valeur de nbvide en l'incrémentant de 1 pour indiquer au client (qui souhaite déposer un
        // article) qu'une place vient d'être libérée sur le tapis
        nbvide++;

        // le thread qui consomme à terminé sa consommation, il réveille le thread producteur si bloqué.
        // ils ne sont que deux threads sur cette instance Caisse, donc notify réveillera nécessairement
        // le producteur (client)
        notify();

        // Le tapis est circulaire donc l'indice de consommation suivant est égal à l'indice actuel + 1, modulo la
        // taille du tapis
        icons= (icons+1)%taille_tapis;
    }


}
