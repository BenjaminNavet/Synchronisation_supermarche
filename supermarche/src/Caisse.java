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
     * ClientEnAttenteDePaiement indique si un client attend déjà de payer
     */
    public volatile boolean ClientEnAttenteDePaiement = false;

    /**
     * EmployeCaisseAFiniDeScannerPourUnClient indique si l'employé de caisse a fini de scanner les articles du
     * client en attente de paiement
     */
    public volatile boolean EmployeCaisseAFiniDeScannerPourUnClient = false;


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
     * modifie la valeur du booléen ClientEnAttenteDePaiement
     * @param clientEnAttenteDePaiement : indique si un client est déjà en attente de paiement
     */
    public void setClientEnAttenteDePaiement(boolean clientEnAttenteDePaiement) {
        ClientEnAttenteDePaiement = clientEnAttenteDePaiement;
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
    public synchronized void entrerEnTapisDeCaisse(Client client) {
        // On a plusieurs processus en concurence donc on utilise un while.
        // Quand ils sont réveillés, ils doivent revérifier cette condition.
        // Mise en attente si un client dépose déjà des articles sur le tapis
        while(UnClientUtiliseLeTapis){
            try {
                wait();
                System.out.println("Le client n°" + client.getIndex() +" ne peut pas poser ses articles " +
                        "(un autre client utilise actuellement le tapis).");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Indique qu'un client utilise le tapis
        setUnClientUtiliseLeTapis(true);
        System.out.println("Le client n°" + client.getIndex() +" commence à poser ses articles");
    }

    /** Autorise la mise en attente de paiement d'un client (mise en attente si un client est déjà en attente
     *  de paiement), et réveille tous les processus pour permettre à un client d'entrer en caisse à la place
     *  de ce client
     * @param client : permet d'obtenir l'index du client qui souhaite entrer en paiement
     */
    public synchronized void entrerPaiement(Client client) {
        // On a plusieurs processus en concurence donc on utilise un while.
        // Quand ils sont réveillés, ils doivent revérifier cette condition.
        // Mise en attente si un client est déjà en attente de paiement
        while (ClientEnAttenteDePaiement) {
            try {
                wait();
                System.out.println("Le client n°" + client.getIndex() +" attend que le client précédent ait" +
                        " fini de payer.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Indique qu'aucun client n'utilise le tapis
        setUnClientUtiliseLeTapis(false);
        // Indique qu'un client attend de payer
        setClientEnAttenteDePaiement(true);
        // Pas besoin de notify car si la caissière continue de scanner, elle notifyAll et si elle est à l'arret ????????????????????
        // notifyAll();
    }

    public synchronized void sortirPaiement(Client client) {
        // Tant que l'employé de caisse n'a pas fini de scanner les articles du client, le client est mis en attente
        while(!EmployeCaisseAFiniDeScannerPourUnClient){
            try {
                wait();
                System.out.println("Le client n°" + client.getIndex() +" attend que l'employé de caisse ait fini" +
                        " de scanner tous les articles pour pouvoir payer.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Indique qu'aucun client n'attend de payer
        setClientEnAttenteDePaiement(false);

        // Indique que l'employé de caisse n'a pas fini de scanner les articles du client (puisqu'il n'a pas commencé)
        setEmployeCaisseAFiniDeScannerPourUnClient(false);

        // Réveiller tout le monde y compris l'employé de caisse
        notifyAll();
        System.out.println("Le client n°" + client.getIndex() +" a payé et quitté la caisse.");
    }

    public synchronized void avant_prod() {
        while(nbvide == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        nbvide --;
    }

    public void prod(int produit, Client client) {
        if(!(produit==-1)){
            try {
                sleep(tps_pose_article);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Le client n°" + client.getIndex() +" pose un article d'index "+produit+ "." );
        }else{
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
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        nbplein --;
    }

    public void cons() {
        if (!(tapis[icons] == -1)) {
            System.out.println("L'employé de caisse scanne un article d'index " + tapis[icons]+".");
        } else {
            setEmployeCaisseAFiniDeScannerPourUnClient(true);
        }

        tapis[icons] = null;
    }

    public synchronized void apres_cons() {
        nbvide++;
        notifyAll();
        icons= (icons+1)%taille_tapis;
    }


}
