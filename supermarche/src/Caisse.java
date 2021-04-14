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
     * Le client a terminé de poser ses articles il attend en zone de paiement que le caissier ai fini
     */
    public volatile boolean ClientEnPaiement = false;

    Semaphore RDVpaiement = new Semaphore(0);
    int compteurRDV = 0;



    /**
     * Client pose ses articles sur le tapis
     */
    private volatile boolean ClientPoseArticle = false;

    /**
     * modifie la valeur du booléen ClientPoseArticle
     * @param clientPoseArticle : indique si le client pose ses articles sur le tapis
     */
    public void setClientPoseArticle(boolean clientPoseArticle) {
        this.ClientPoseArticle = clientPoseArticle;
    }

    public void setClientEnPaiement(boolean clientEnPaiement) {
        this.ClientEnPaiement = clientEnPaiement;
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

    public synchronized void entrerEnTapisDeCaisse(Client client) {
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



    public synchronized void entreEnPaiement(Client client) {
        while(ClientEnPaiement){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setClientEnPaiement(true);
        }
        System.out.println("Le client " + client.getIndex() +" ("+ client.getNom() + ") entre en en paiement");
    }

    public void Payer() {
        compteurRDV ++;
        if (compteurRDV == 2) {
            RDVpaiement.release(2);
            compteurRDV = 0;
        }
        try {
            RDVpaiement.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setClientEnPaiement(false);
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
            // pas obligé d'avoir un notify all puisqu'il sera fait dans après prod !!! notifyAll();
        }
    }

    public synchronized void apres_prod() {
        nbplein++;
        notifyAll();
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


    public synchronized void cons() {
        if (!(tapis[icons] == -1)) {
            System.out.println("L'employé de caisse scanne 1 " + listeProduits[tapis[icons]]);
        } else {
            Payer();
            notifyAll();
            System.out.println("L'employé de caisse a fini le passage du client" );
        }
        tapis[icons] = null;
    }

    public synchronized void apres_cons() {
        nbvide++;
        notify();
        icons= (icons+1)%taille_tapis;
    }


}
