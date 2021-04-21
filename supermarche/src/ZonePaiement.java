import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class ZonePaiement {

    /**
     * Temps que met un client pour payer
     */
    public int tps_paiement;

    /**
     * Liste de client(s) en attente de Paiement
     */
    List<Integer> listeAttentePaiement = new ArrayList<>();


    /**
     * EmployeCaisseAFiniDeScannerPourUnClient indique si l'employé de caisse a fini de scanner les articles du
     * client en attente de paiement
     */
    private volatile boolean EmployeCaisseAFiniDeScannerPourUnClient;

    public ZonePaiement(int tps_paiement, boolean employeCaisseAFiniDeScannerPourUnClient, List<Integer> listeAttentePaiement) {
        this.tps_paiement = tps_paiement;
        this.EmployeCaisseAFiniDeScannerPourUnClient = employeCaisseAFiniDeScannerPourUnClient;
        this.listeAttentePaiement = listeAttentePaiement;
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
     * Paiement d'un client lors de son passage en caisse
     *
     * @param client : permet d'obtenir l'index du client qui souhaite payer
     */
    public synchronized void paiement(Client client) {
        // On a plusieurs processus en concurence donc on utilise un while.
        // une fois réveillé le thread passe en état "prêt", mais l'ordonnanceur peut élire un autre thread
        // il doit donc retester la condition
        // Mise en attente si l'employé de caisse n'a pas fini de scanner les articles du client ou si le
        // client n'est pas le premier sur la liste d'attente
        while (!EmployeCaisseAFiniDeScannerPourUnClient || listeAttentePaiement.get(0) != client.getIndex()) {
            try {
                System.out.println("Le client n°" + client.getIndex() + " attend de pouvoir payer.");
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

        // On réinitialise le booléen EmployeCaisseAFiniDeScannerPourUnClient pour indiquer que l'employé de caisse
        // n'a pas fini de scanner les articles du client suivant (puisqu'il n'a pas commencé)
        setEmployeCaisseAFiniDeScannerPourUnClient(false);

        // On supprime le numéro du client de la liste d'attente de paiement car il a payé
        listeAttentePaiement.remove(0);

        System.out.println("Le client n°" + client.getIndex() + " a payé et quitté la caisse.");

        // il y a plusieurs conditions d'attente : sur les variables UnClientUtiliseLeTapis et EmployeCaisseAFiniDeScannerPourUnClient
        // on ne peut pas prévoir quel processus sera réveillé par notify(), pour être sur de reveiller un thread
        //concerné par la condition d'attente de ce bloc, on est obligé de faire un notifyAll() qui réveille tous les
        //processus bloqués de cette instance
        notifyAll();
    }
}
