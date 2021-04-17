public class EmployeCaisse extends Thread{

    /**
     * Caisse sur laquelle l'employé de caisse travaille
     */
    private final Caisse caisse;

    public EmployeCaisse(Caisse caisse) {
        this.caisse = caisse;
    }

    /**
     * Fonctionnement de l'employé de caisse : il cherche constamment à scanner des articles
     *  3 phases : avant-consommation (avant de scanner), consommation (scanner), après-production (après avoir scanné)
     */
    public void run() {
        while (true) {
            caisse.avant_cons();
            caisse.cons();
            caisse.apres_cons();
        }
    }

}
