import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class AccesPaiement {

    /**
     * Liste de client(s) en attente de Paiement
     */
    private List<Integer> listeAttentePaiement = new ArrayList<>();

    /**
     * EmployeCaisseAFiniDeScannerPourUnClient indique si l'employé de caisse a fini de scanner les articles du
     * client en attente de paiement
     */
    private volatile boolean EmployeCaisseAFiniDeScannerPourUnClient = false;

    /**
     * Temps que met un client pour payer
     */
    private int tps_paiement;

    public AccesPaiement(int tps_paiement){
        this.tps_paiement=tps_paiement;
    }

    /**
     * modifie la valeur du booléen EmployeCaisseAFiniDeScannerPourUnClient
     * @param employeCaisseAFiniDeScannerPourUnClient : indique si l'employé de caisse a fini de scanner les articles
     *                                                  du client en attente de paiement
     */
    public synchronized void setEmployeCaisseAFiniDeScannerPourUnClient(boolean employeCaisseAFiniDeScannerPourUnClient) {
        EmployeCaisseAFiniDeScannerPourUnClient = employeCaisseAFiniDeScannerPourUnClient;
        notifyAll();
    }

    /** Entrée en paiement d'un client lors de son passage en caisse
     * @param client : permet d'obtenir l'index du client qui souhaite payer
     */
    public synchronized void entrePaiement(Client client) {

        // On ajoute le client à la liste d'attente de paiement
        listeAttentePaiement.add(client.getIndex());

        // On a plusieurs processus en concurence donc on utilise un while.
        // Quand ils sont réveillés, ils doivent revérifier cette condition.
        // Mise en attente si l'employé de caisse n'a pas fini de scanner les articles du client ou si le
        // client n'est pas le premier sur la liste d'attente
        while(!EmployeCaisseAFiniDeScannerPourUnClient || listeAttentePaiement.get(0)!=client.getIndex()){
            try {
                System.out.println("Le client n°" + client.getIndex() +" attend de pouvoir payer.");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Le client met un temps `tps_paiement` pour payer
        try {
            sleep(tps_paiement);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /** Paiement d'un client lors de son passage en caisse
     * @param client : permet d'obtenir l'index du client qui souhaite payer
     */
    public synchronized void sortPaiement(Client client) {

        // On réinitialise le booléen EmployeCaisseAFiniDeScannerPourUnClient pour indiquer que l'employé de caisse
        // n'a pas fini de scanner les articles du client suivant (puisqu'il n'a pas commencé)
        setEmployeCaisseAFiniDeScannerPourUnClient(false);

        // On supprime le numéro du client de la liste d'attente de paiement car il a payé
        listeAttentePaiement.remove(0);

        System.out.println("Le client n°" + client.getIndex() +" a payé et quitté la caisse.");

        // On réveille tout le monde car il y a plusieurs processus en concurrence
        notifyAll();

    }

}
