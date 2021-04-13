import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

public class Caisse {

    public Semaphore semaphore = new Semaphore(1);
    public Integer[] tapis;
    public int iprod;
    public int icons;
    public int nbvide;
    public int nbplein;
    public int taille_tapis;
    String[] listeProduits;
    /**
     * Client pose ses articles sur le tapis
     */
    private volatile boolean ClientPoseArticle = false;

    /**
     * modifie la valeur du booléen ClientPoseArticle
     * @param clientPoseArticle : indique si le client pose ses articles sur le tapis
     */
    public void setClientPoseArticle(boolean clientPoseArticle) {
        ClientPoseArticle = clientPoseArticle;
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

    public synchronized void entrerEnCaisse(Client client) {
        while(ClientPoseArticle){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        setClientPoseArticle(true);
        System.out.println("Le client " + client.getIndex() +" ("+ client.getNom() + ") entre en caisse");
    }

    public void sortirDeCaisse(Client client) {
        //Système d'attente
        System.out.println("le client " + client.getIndex() +" ("+ client.getNom() + ") sors de caisse");
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

    public synchronized void prod(int produit) {
        try {
            sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tapis[iprod] = produit;
        if (produit == -1) {
            setClientPoseArticle(false);
            notifyAll();
        }
    }

    public synchronized void apres_prod() {
        nbplein++;
        notifyAll();
        iprod= (iprod+1)%taille_tapis;
    }


    public void avant_cons() {
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
            System.out.println("L'employé de caisse scanne 1 " + listeProduits[tapis[icons]]);
        } else {
            System.out.println("L'employé de caisse a fini le passage du client" );
        }
        tapis[icons] = null;
    }

    public void apres_cons() {
        nbvide++;
        notify();
        icons= (icons+1)%taille_tapis;
    }


}
