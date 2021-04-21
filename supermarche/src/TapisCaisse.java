import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class TapisCaisse {

    /**
     * Buffer représentant le tapis de caisse
     */
    public Integer[] tapis;

    /**
     * iprod est l'indice du buffer dans lequel le client va déposer son article suivant
     */
    public int iprod;

    /**
     * icons est l'indice du buffer dans lequel l'employé de caisse va prendre l'article suivant pour le scanner
     */
    public int icons;

    /**
     * Nombre d'espace(s) vide(s) sur le tapis
     */
    public int nbvide;

    /**
     * Nombre d'espace(s) rempli(s) sur le tapis
     */
    public int nbplein;

    /**
     * Nombre d'espace(s) sur le tapis
     */
    public int taille_tapis;

    /**
     * Temps que met un client pour poser un article
     */
    public int tps_pose_article;

    /**
     * Temps que met un employé de caisse pour scanner un article
     */
    public int tps_scanne_article;


    /**
     * UnClientUtiliseLeTapis indique si un client pose déjà ses articles sur le tapis
     */
    private volatile boolean UnClientUtiliseLeTapis = false;

    /**
     * EmployeCaisseAFiniDeScannerPourUnClient indique si l'employé de caisse a fini de scanner les articles du
     * client en attente de paiement
     */
    private volatile boolean EmployeCaisseAFiniDeScannerPourUnClient;

    /**
     * Liste de client(s) en attente de Paiement
     */
    List<Integer> listeAttentePaiement = new ArrayList<>();

    public TapisCaisse(int taille_tapis, int tps_pose_article, int tps_scanne_article, boolean employeCaisseAFiniDeScannerPourUnClient, List<Integer> listeAttentePaiement) {
        this.tapis = new Integer[taille_tapis];
        this.nbvide = taille_tapis;
        this.nbplein = 0;
        this.icons = 0;
        this.iprod = 0;
        this.taille_tapis = taille_tapis;
        this.tps_pose_article = tps_pose_article;
        this.tps_scanne_article = tps_scanne_article;
        this.EmployeCaisseAFiniDeScannerPourUnClient = employeCaisseAFiniDeScannerPourUnClient;
        this.listeAttentePaiement = listeAttentePaiement;
    }


    /**
     * modifie la valeur du booléen UnClientUtiliseLeTapis     *
     * @param unClientUtiliseLeTapis : indique si un client pose déjà ses articles sur le tapis
     */
    public void setUnClientUtiliseLeTapis(boolean unClientUtiliseLeTapis) {
        UnClientUtiliseLeTapis = unClientUtiliseLeTapis;
    }

    /**
     * modifie la valeur du booléen EmployeCaisseAFiniDeScannerPourUnClient     *
     * @param employeCaisseAFiniDeScannerPourUnClient : indique si l'employé de caisse a fini de scanner les articles
     * du client en attente de paiement
     */
    public synchronized void setEmployeCaisseAFiniDeScannerPourUnClient(boolean employeCaisseAFiniDeScannerPourUnClient) {
        EmployeCaisseAFiniDeScannerPourUnClient = employeCaisseAFiniDeScannerPourUnClient;
    }



    /**
     * Un client dépose ses articles sur le tapis de caisse lors de son passage en caisse
     *
     * @param client : permet d'obtenir l'index du client qui souhaite entrer en caisse
     */
    public synchronized void deposeSurTapisDeCaisse(Client client) {
        // On a plusieurs processus en concurence donc on utilise un while.
        // une fois réveillé le thread passe en état "prêt", mais un autre thread peut prendre sa place,
        // il doit donc retester la condition
        // Mise en attente si un client dépose déjà des articles sur le tapis
        while (UnClientUtiliseLeTapis) {
            try {
                System.out.println("Le client n°" + client.getIndex() + " ne peut pas poser ses articles " +
                        "(un autre client utilise actuellement le tapis).");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Indique qu'un client utilise le tapis
        setUnClientUtiliseLeTapis(true);
        System.out.println("Le client n°" + client.getIndex() + " commence à poser ses articles");
    }



    /**
     * Méthode d'avant production permettant de vérifier s'il est possible de déposer un article sur le tapis
     */
    public synchronized void avant_prod() {
        // On a plusieurs processus en concurence donc on utilise un while.
        // une fois réveillé le thread passe en état "prêt", mais l'ordonnanceur peut élire un autre thread
        // il doit donc retester la condition
        // Tant qu'il n'y a aucune place disponible sur le tapis, le client est mis en attente
        while (nbvide == 0) {
            try {
                System.out.println("Aucune place disponible sur le tapis.");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // On décrémente nbvide de 1 : un article va être placé sur le tapis
        nbvide--;
    }

    /**
     * Méthode de production permettant de déposer un article sur le tapis
     *
     * @param produit : indique l'index du produit qui va être placé
     * @param client  : permet d'obtenir l'index du client qui dépose un article
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
        if (!(produit == -1)) {
            System.out.println("Le client n°" + client.getIndex() + " dépose un article " + produit + ".");
        } else {
            // On indique que personne n'utilise le tapis car le client a terminé de déposer ses articles
            setUnClientUtiliseLeTapis(false);
            // On ajoute le client à la liste d'attente de paiement
            listeAttentePaiement.add(client.getIndex());
            System.out.println("Le client n°" + client.getIndex() + " a fini de déposer ses articles.");
        }
        // On ajoute le produit déposé dans le buffer `tapis` à l'indice iprod
        tapis[iprod] = produit;
    }

    /**
     * Méthode d'après production permettant d'actualiser le nombre de places occupées sur le tapis, de réveiller
     * tous les threads en attente et d'actualiser la place sur laquelle l'article suivant va être déposé
     */
    public synchronized void apres_prod() {
        // On actualise la valeur de nbplein en l'incrémentant de 1 pour indiquer à l'employé de caisse que le tapis
        // contient un article de plus
        nbplein++;
        // il y a plusieurs conditions d'attente : sur les variables UnClientUtiliseLeTapis et EmployeCaisseAFiniDeScannerPourUnClient
        // on ne peut pas prévoir quel processus sera réveillé par notify(), pour être sur de reveiller un thread
        //concerné par la condition d'attente de ce bloc, on est obligé de faire un notifyAll() qui réveille tous les
        //processus bloqués de cette instance
        notifyAll();
        // Le tapis est circulaire donc l'indice de production suivant est égal à l'indice actuel + 1, modulo la
        // taille du tapis
        iprod = (iprod + 1) % taille_tapis;
    }

    /**
     * Méthode d'avant consommation permettant de vérifier s'il est possible de prendre un article sur le tapis
     */
    public synchronized void avant_cons() {
        // On a plusieurs processus en concurence donc on utilise un while.
        // une fois réveillé le thread passe en état "prêt", mais l'ordonnanceur peut élire un autre thread
        // il doit donc retester la condition
        // Tant qu'il n'y a aucune article sur le tapis ou qu'un client est entrain de payer,
        // l'employé de caisse est mis en attente
        while (nbplein == 0 || EmployeCaisseAFiniDeScannerPourUnClient) {
            try {
                if (nbplein == 0) {
                    System.out.println("Aucun article à scanner.");
                }
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // On décrémente nbplein de 1 : un article va être enlevé du tapis
        nbplein--;
    }

    /**
     * Méthode de consommation permettant de d'enlever un article du tapis
     */
    public void cons() {

        // L'employé de caisse met un temps `tps_scanne_article` pour scanner un article
        try {
            sleep(tps_scanne_article);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Si ce qui est scanné est différent de -1, l'employé de caisse scanne l'article sinon, il indique qu'il a
        // fini de scanner, permettant au client de payer
        if (!(tapis[icons] == -1)) {
            System.out.println("L'employé de caisse scanne un article " + tapis[icons] + ".");
        } else {
            // On indique que l'employé de caisse a fini de scanner les articles du client
            setEmployeCaisseAFiniDeScannerPourUnClient(true);
            System.out.println("L'employé de caisse a fini de scanner le(s) article(s) d'un client.");
        }

        // On retire le produit scanné du buffer `tapis` : indice icons de tapis est réinitialisé à null
        tapis[icons] = null;
    }

    public synchronized void apres_cons() {
        // On actualise la valeur de nbvide en l'incrémentant de 1 pour indiquer au client (qui souhaite déposer un
        // article) qu'une place vient d'être libérée sur le tapis
        nbvide++;
        // il y a plusieurs conditions d'attente : sur les variables UnClientUtiliseLeTapis et EmployeCaisseAFiniDeScannerPourUnClient
        // on ne peut pas prévoir quel processus sera réveillé par notify(), pour être sur de reveiller un thread
        //concerné par la condition d'attente de ce bloc, on est obligé de faire un notifyAll() qui réveille tous les
        //processus bloqués de cette instance
        notifyAll();
        // Le tapis est circulaire donc l'indice de consommation suivant est égal à l'indice actuel + 1, modulo la
        // taille du tapis
        icons = (icons + 1) % taille_tapis;
    }


}
