import com.github.javafaker.Faker;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Supermarche {
    /**
     * nombre d'exemplaires de chaque articles que le chef de rayon peut transporter dans sa tournée de remplissage des rayons
     */
    private static final int NB_ELEMENT_PAR_CHGT = 5;

    /**
     * le stock initial présent dans les rayons à l'ouverture du magasin
     */
    private static final int RAYON_STOCK_INIT = 50;

    /**
     * nombre maximum d'exemplaires d'un produit dans un rayon
     */
    private static final int RAYON_STOCK_MAX = 50;

    /**
     * nombre d'exemplaires d'un produit dans l'entrepot à l'initialisation (-1 correspond à un stock infini)
     */
    private static final int ENTREPOT_STOCK_INIT = -1;

    /**
     * temps en ms pour parcourir le trajet d'un rayon à un autre
     */
    private static final int TPS_PARCOURS_RAYONS = 200;

    /**
     * temps en ms pour parcourir le trajet du rayon à l'entrepot
     */
    private static final int TPS_PARCOURS_ENTREPOT = 500;

    /**
     * nombre d'objets présents sur le tapis de caisse
     */
    private static final int TAILLE_TAPIS = 20;

    /**
     *Nombre de chariots dans la file à l'ouverture du magasin
     */
    private static final int NB_CHARIOTS = 15;

    /**
     *Nombre de clients du magasin
     */
    private static final int NB_CLIENTS = 30;

    /**
     *Nombre d'aticles maximum par client pour chaque type d'article
     */
    private static final int NB_MAX_ARTICLE_PAR_CLIENT = 6;

    /**
     * liste des produits présents en magasin
     */
    private static final String[] listeProduits = {"Sucre", "Farine", "Beurre", "Lait"};


    public static void main(String[] args) {



        Map<String, Integer> listeDeCourses = new HashMap();
        List<Client> listeClients = new ArrayList<>();

        //création du chariot
        Chariot chariot = new Chariot(NB_CHARIOTS);

        // création des rayons
        List<Rayon> rayons = new ArrayList<Rayon>();
        for (int i = 0; i < listeProduits.length ; i ++ ) {
            rayons.add(new Rayon(i,listeProduits[i], RAYON_STOCK_MAX, RAYON_STOCK_INIT));
        }

        // création de l'entrepot
        Map<String, Integer> entrepotHmap = new HashMap();
        for (Rayon rayon : rayons) {
            entrepotHmap.put(rayon.getName(), ENTREPOT_STOCK_INIT);
        }
        Entrepot entrepot= new Entrepot(entrepotHmap);

        // création des clients
        for (int i = 0; i < NB_CLIENTS; i++) {
            for (int j = 0; j < listeProduits.length; j++) {
                // @Erwann : ici il faut multiplier le nombre aléatoire par le nombre de produits max par rayons, ainsi
                // un client ne demandera jamais plus que la quantité max d'un rayon ( 1 x Max)
                // erratum : je n'ai pas fait ça au dessus, car le run dure trop longtemps. j'ai mis une petite valeur à la place
                listeDeCourses.put(listeProduits[j], (int)(Math.random() * NB_MAX_ARTICLE_PAR_CLIENT ));
            }

            // génère automatiquement un nom aléatoire. regarde ici : https://stackoverflow.com/questions/5025651/java-randomly-generate-distinct-names
            //du coup j'ai rajouté Maven au projet, regarde le pom.xml
            // la bibliothèque s'appelle Faker
            Faker faker = new Faker();
            String nom = faker.name().fullName();
            listeClients.add(new Client(i,nom, listeDeCourses, rayons, TPS_PARCOURS_RAYONS, chariot));
        }

        //création du chef de rayon
        ChefRayon chef_de_rayon = new ChefRayon(rayons, TPS_PARCOURS_RAYONS, TPS_PARCOURS_ENTREPOT, NB_ELEMENT_PAR_CHGT,entrepot);

        // on créé un deamon pour que le thread chef_de_rayon s'arrête une fois tous les clients passés et qu'il
        //ne bloque pas le programme en tournant indéfiniement
        chef_de_rayon.setDaemon(true);
        chef_de_rayon.start();

        for (Client client : listeClients) {
            client.start();
        }



    }
}
