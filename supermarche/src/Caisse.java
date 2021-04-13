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

    /**
     * Temps que met un client pour poser un article
     */
    public int tps_poser_article;

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

    public void setClientEnPaiement(boolean clientEnPaiement) {
        ClientEnPaiement = clientEnPaiement;
    }

    public Caisse(int taille_tapis, String[] listeProduits, int tps_poser_article) {
        tapis = new Integer[taille_tapis];
        nbvide = taille_tapis;
        nbplein = 0;
        icons = 0;
        iprod = 0;
        this.taille_tapis = taille_tapis;
        this.listeProduits = listeProduits;
        this.tps_poser_article=tps_poser_article;
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

    public void sortirDuTapisDeCaisse(Client client) {
        System.out.println("le client " + client.getIndex() +" ("+ client.getNom() + ") sors de caisse");
    }

    public synchronized void entreEnPaiement(Client client) {
        while(ClientEnPaiement){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        setClientEnPaiement(true);
        System.out.println("Le client " + client.getIndex() +" ("+ client.getNom() + ") entre en en paiement");
    }

    public void sortirDuPaiement(Client client) {
        System.out.println("le client " + client.getIndex() +" ("+ client.getNom() + ") sors de paiement");
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
            sleep(tps_poser_article);
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
