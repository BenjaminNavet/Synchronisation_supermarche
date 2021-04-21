import java.util.List;
import java.util.Map;

public class Client extends Thread{

    /**
     * Le numéro du client
     */
    int idxClient;

    /**
     * La liste de courses du client
     */
    Map<Integer, Integer> listeCourses;

    /**
     * Le temps que met un client pour se déplacer entre deux rayons
     */
    int tpsParcoursRayons;


    /**
     * La file de chariots dans laquelle le client va prendre puis rendre un chariot
     */
    Chariot chariot;

    /**
     * La caisse que va utiliser le client pour déposer ses articles sur le tapis puis pour payer
     */
    Caisse caisse;

    /**
     * La liste des rayons du supermarché
     */
    List<Rayon> rayons;

    public Client(int idxClient, Map<Integer, Integer> listeCourses, List<Rayon> rayons, int tpsParcoursRayons, Chariot chariot, Caisse caisse) {
        this.idxClient=idxClient;
        this.listeCourses = listeCourses;
        this.rayons = rayons;
        this.tpsParcoursRayons = tpsParcoursRayons;
        this.chariot = chariot;
        this.caisse = caisse;
    }

    /** Méthode qui retourne le numéro du client
     * @return idxClient : le numéro du client
     */
    public int getIndex() {
        return idxClient;
    }

    /**
     * Méthode permettant au client de changer de rayon
     */
    public void changeDeRayon(){
        try {
            // Le client met un temps `tpsParcoursRayons` pour changer de rayon
            sleep(tpsParcoursRayons);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        // Le client prend un chariot
        chariot.prendreChariot(this);

        // Le client commence ses courses et va au premier rayon
        changeDeRayon();

        // Pour chaque rayon, le client prend le nombre d'articles dont il a besoin puis passe au rayon suivant
        for (Rayon rayon : rayons) {
            // Quantité souhaitée par le client
            int quantiteVoulue = listeCourses.get(rayon.getIndex());
            for (int j = 0; j < quantiteVoulue; j++) {
                // Le client prend un produit dans le rayon
                rayon.takeProduct(this);
            }
            // Le client change de rayon
            changeDeRayon();
        }

        // Le client pose ses articles sur le tapis de caisse
        caisse.tapisCaisse.deposeSurTapisDeCaisse(this);
        for (Rayon rayon : rayons) {
            // Nombre de produits pris dans le rayon d'index rayon.getIndex() à déposer
            int quantiteADeposer = listeCourses.get(rayon.getIndex());
            // Pour chaque produit que le client a dans son chariot, il cherche à le déposer sur le tapis de caisse
            // 3 phases : avant-production (avant de déposer), production (déposer), après-production (après avoir scanné)
            for (int j = 0; j < quantiteADeposer; j++) {
                caisse.tapisCaisse.avant_prod();
                caisse.tapisCaisse.prod(rayon.getIndex(),this);
                caisse.tapisCaisse.apres_prod();
            }
        }

        // Lorsque le client a fini de poser ses produits sur le tapis de caisse, il pose la marque -1 (`client suivant`)
        caisse.tapisCaisse.avant_prod();
        caisse.tapisCaisse.prod(-1,this);
        caisse.tapisCaisse.apres_prod();

        // Le client paye ses courses
        caisse.zonePaiement.paiement(this);

        // Le client rend son chariot
        chariot.rendreChariot(this);
    }

}

