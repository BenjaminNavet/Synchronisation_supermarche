public class EmployeCaisse extends Thread{

    private Caisse caisse;

    public EmployeCaisse(Caisse caisse) {
        this.caisse = caisse;
    }

    public void run() {

        while (true) {
            caisse.avant_cons();
            caisse.cons();
            caisse.apres_cons();
        }
    }

}
