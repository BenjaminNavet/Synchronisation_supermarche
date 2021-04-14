import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

public class Caisse {


    public Integer[] tapis;
    public int iprod;
    public int icons;
    public int nbvide;
    public int nbplein;
    public int taille_tapis;
    String[] listeProduits;

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


    public Caisse(int taille_tapis, String[] listeProduits, int tps_pose_article) {
        tapis = new Integer[taille_tapis];
        nbvide = taille_tapis;
        nbplein = 0;
        icons = 0;
        iprod = 0;
        this.taille_tapis = taille_tapis;
        this.listeProduits = listeProduits;
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


    public synchronized void entrerEnTapisDeCaisse(Client client) {
        while(UnClientUtiliseLeTapis){
            try {
                wait();
                System.out.println("Le client " + client.getIndex() +" ("+ client.getNom() + ") ne peut pas poser" +
                        " ses articles (un autre client utilise actuellement le tapis).");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        setUnClientUtiliseLeTapis(true);
        System.out.println("Le client " + client.getIndex() +" ("+ client.getNom() + ") commence à poser ses articles");
    }

    public synchronized void entrerPaiement(Client client) {
        // Tant qu'un client est déjà en attente de paiement, le client est mis en attente
        while (ClientEnAttenteDePaiement) {
            try {
                wait();
                System.out.println("Le client " + client.getIndex() +" ("+ client.getNom() + ") attend que" +
                        " le client précédent ait fini de payer.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Indique qu'aucun client n'utilise le tapis
        setUnClientUtiliseLeTapis(false);
        // Indique qu'un client attend de payer
        setClientEnAttenteDePaiement(true);
        // On réveille une seule personne : un client
        notify();
    }

    public synchronized void sortirPaiement(Client client) {
        // Tant que l'employé de caisse n'a pas fini de scanner les articles du client, le client est mis en attente
        while(!EmployeCaisseAFiniDeScannerPourUnClient){
            try {
                wait();
                System.out.println("Le client " + client.getIndex() +" ("+ client.getNom() + ") attend que" +
                        " l'employé de caisse ait fini de scanner tous les articles pour pouvoir payer.");
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
        System.out.println("Le client " + client.getIndex() +" ("+ client.getNom() + ") a payé et quitte la caisse.");
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

    public void prod(int produit) {
        if(!(produit==-1)){
            try {
                sleep(tps_pose_article);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("Le client a fini de poser ses articles." );
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
            System.out.println("L'employé de caisse scanne 1 article de " + listeProduits[tapis[icons]]+".");
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
