import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

public class Caisse {

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
     * UnClientUtiliseLeTapis indique si un client pose déjà ses articles sur le tapis
     */
    private volatile boolean UnClientUtiliseLeTapis = false;

    /**
     * EmployeCaisseAFiniDeScannerPourUnClient indique si l'employé de caisse a fini de scanner les articles du
     * client en attente de paiement
     */
    public volatile boolean EmployeCaisseAFiniDeScannerPourUnClient = false;


    /**
     * Liste de client(s) en attente de Paiement
     */
    List<Integer> listeAttentePaiement = new ArrayList<>();

    public Caisse(int taille_tapis, int tps_pose_article) {
        tapis = new Integer[taille_tapis];
        nbvide = taille_tapis;
        nbplein = 0;
        icons = 0;
        iprod = 0;
        this.taille_tapis = taille_tapis;
        this.tps_pose_article=tps_pose_article;
    }

    /**
     * modifie la valeur du booléen UnClientUtiliseLeTapis
     * @param unClientUtiliseLeTapis : indique si un client pose déjà ses articles sur le tapis
     */
    public void setUnClientUtiliseLeTapis(boolean unClientUtiliseLeTapis) {
        UnClientUtiliseLeTapis = unClientUtiliseLeTapis;
    }

    /**
     * modifie la valeur du booléen EmployeCaisseAFiniDeScannerPourUnClient
     * @param employeCaisseAFiniDeScannerPourUnClient : indique si l'employé de caisse a fini de scanner les articles
     *                                                  du client en attente de paiement
     */
    public void setEmployeCaisseAFiniDeScannerPourUnClient(boolean employeCaisseAFiniDeScannerPourUnClient) {
        EmployeCaisseAFiniDeScannerPourUnClient = employeCaisseAFiniDeScannerPourUnClient;
    }


    /** Autorise l'accès à la caisse au client ou mise en attente si elle est déjà occupée
     * @param client : permet d'obtenir l'index du client qui souhaite entrer en caisse
     */
    public synchronized void deposeSurTapisDeCaisse(Client client) {
        // On a plusieurs processus en concurence donc on utilise un while.
        // Quand ils sont réveillés, ils doivent revérifier cette condition.
        // Mise en attente si un client dépose déjà des articles sur le tapis
        while(UnClientUtiliseLeTapis){
            try {
                System.out.println("Le client n°" + client.getIndex() +" ne peut pas poser ses articles " +
                        "(un autre client utilise actuellement le tapis).");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Indique qu'un client utilise le tapis
        setUnClientUtiliseLeTapis(true);
        System.out.println("Le client n°" + client.getIndex() +" commence à poser ses articles");
    }

    public synchronized void paiement(Client client) {
        // Tant que l'employé de caisse n'a pas fini de scanner les articles du client, le client est mis en attente
        while(!EmployeCaisseAFiniDeScannerPourUnClient || listeAttentePaiement.get(0)!=client.getIndex()){
            try {
                System.out.println("Le client n°" + client.getIndex() +" attend de pouvoir payer.");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Indique que l'employé de caisse n'a pas fini de scanner les articles du client (puisqu'il n'a pas commencé)
        setEmployeCaisseAFiniDeScannerPourUnClient(false);

        // On supprime le numéro du client de la liste d'attente de paiement car il a payé
        listeAttentePaiement.remove(0);

        // Réveiller tout le monde y compris l'employé de caisse
        notifyAll();
        System.out.println("Le client n°" + client.getIndex() +" a payé et quitté la caisse.");
    }

    public synchronized void avant_prod() {
        while(nbvide == 0) {
            try {
                System.out.println("Aucune place disponible sur le tapis.");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        nbvide --;
    }

    public void prod(int produit, Client client) {
        try {
            sleep(tps_pose_article);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(!(produit==-1)){
            System.out.println("Le client n°" + client.getIndex() +" pose un article "+produit+ "." );
        }else{
            setUnClientUtiliseLeTapis(false);
            listeAttentePaiement.add(client.getIndex());
            System.out.println("Le client n°" + client.getIndex() +" a fini de poser ses articles." );
        }

        tapis[iprod] = produit;
    }

    public synchronized void apres_prod() {
        nbplein++;
        notifyAll();
        iprod= (iprod+1)%taille_tapis;
    }

    public synchronized void avant_cons() {
        while(nbplein == 0 || EmployeCaisseAFiniDeScannerPourUnClient ) {
            try {
                if(nbplein == 0){
                    System.out.println("Aucun article à scanner.");
                }
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        nbplein --;
    }

    public void cons() {

        try {
            sleep(tps_pose_article*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!(tapis[icons] == -1)) {
            System.out.println("L'employé de caisse scanne un article " + tapis[icons]+".");
        } else {
            setEmployeCaisseAFiniDeScannerPourUnClient(true);
            System.out.println("L'employé de caisse a fini de scanner le(s) article(s) d'un client.");
        }

        tapis[icons] = null;
    }

    public synchronized void apres_cons() {
        nbvide++;
        notifyAll();
        icons= (icons+1)%taille_tapis;
    }


}
