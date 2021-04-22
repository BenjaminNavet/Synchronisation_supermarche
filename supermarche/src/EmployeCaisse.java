public class EmployeCaisse extends Thread{

    /**
     * Caisse sur laquelle l'employé de caisse travaille
     */
    private final Caisse caisse;

    /**
     * Acces au paiement lors du passage en caisse
     */
    private AccesPaiement accesPaiement;

    public EmployeCaisse(Caisse caisse, AccesPaiement accesPaiement) {
        this.caisse = caisse;
        this.accesPaiement=accesPaiement;
    }

    /**
     * Fonctionnement de l'employé de caisse : il cherche constamment à scanner des articles
     *  3 phases : avant-consommation (avant de scanner), consommation (scanner), après-consommation (après avoir scanné)
     */
    public void run() {

        while (true) {

            caisse.avant_cons();

            // La consommation d'une case dans le tapis return true seulement si -1 est trouvé (scan fini)
            boolean enMouvement=caisse.cons();

            // L'information "fini de scanner" est transmise à la zone de paiement. Si le scan est fini, le premier
            // client en attente de paiement sera réveillé
            if(enMouvement) {
                accesPaiement.setEmployeCaisseAFiniDeScannerPourUnClient(enMouvement);
            }

            caisse.apres_cons();
        }
    }

}
