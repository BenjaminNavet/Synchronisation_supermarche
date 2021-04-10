import java.util.concurrent.Semaphore;

public class Caisse {

    public Semaphore semaphore = new Semaphore(1);
    public Integer[] tapis;
    public int iprod;
    public int icons;
    public int nbvide;
    public int nbplein;
    public int taille_tapis;
    String[] listeProduits;

    public void entrerEnCaisse(Client client) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("le client " + client.getIndex() +" ("+ client.getNom() + ") entre en caisse");
    }
    public void sortirDeCaisse(Client client) {
            semaphore.release();
            System.out.println("le client " + client.getIndex() +" ("+ client.getNom() + ") sors de caisse");
    }


    public Caisse(int taille_tapis, String[] listeProduits) {
        tapis = new Integer[taille_tapis];
        nbvide = taille_tapis;
        nbplein = 0;
        icons = 0;
        iprod = 0;
        this.taille_tapis = taille_tapis;
        this.listeProduits = listeProduits;

    }

    public synchronized void avant_prod() {
        if(nbvide == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        nbvide --;
    }


    public void prod(int produit) {
        tapis[iprod] = produit;
    }

    public synchronized void apres_prod() {
        nbplein++;
        notify();
        iprod= (iprod+1)%taille_tapis;
    }


    public synchronized void avant_cons() {
        if(nbplein == 0) {
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
            System.out.println("l'employé de caisse scanne 1 " + listeProduits[tapis[icons]]);
        }
        tapis[icons] = null;
    }

    public synchronized void apres_cons() {
        nbvide++;
        notify();
        icons= (icons+1)%taille_tapis;
    }


}
