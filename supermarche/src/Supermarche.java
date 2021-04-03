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
     * liste des produits présents en magasin
     */
    private static final String[] listeProduits = {"Sucre", "Farine", "Beurre", "Lait"};


    public static void main(String[] args) {



        Map<String, Integer> listeDeCourses = new HashMap();
        List<Client> listeClients = new ArrayList<>();

        //création du chariot
        Chariots chariot = new Chariots(NB_CHARIOTS);

        // création des rayons
        List<Rayon> rayons = new ArrayList<Rayon>();
        for (int i = 0; i < listeProduits.length ; i ++ ) {
            rayons.add(new Rayon(listeProduits[i], RAYON_STOCK_MAX, RAYON_STOCK_INIT));
        }

        // création des clients
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < listeProduits.length; j++) {
                // @Erwann : ici il faut multiplier le nombre aléatoire par le nombre de produits max par rayons, ainsi
                // un client ne demandera jamais plus que la quantité max d'un rayon ( 1 x Max)
                // erratum : je n'ai pas fait ça au dessus, car le run dure trop longtemps. j'ai mis une petite valeur à la place
                listeDeCourses.put(listeProduits[j], (int)(Math.random() * NB_ELEMENT_PAR_CHGT ));
            }

            // génère automatiquement un nom aléatoire. regarde ici : https://stackoverflow.com/questions/5025651/java-randomly-generate-distinct-names
            //du coup j'ai rajouté Maven au projet, regarde le pom.xml
            // la bibliothèque s'appelle Faker
            Faker faker = new Faker();
            String nom = faker.name().fullName();
            listeClients.add(new Client(nom, listeDeCourses, rayons, TPS_PARCOURS_RAYONS, chariot));
        }

        //création du chef de rayon
        ChefRayon chef_de_rayon = new ChefRayon(rayons, TPS_PARCOURS_RAYONS, TPS_PARCOURS_ENTREPOT, NB_ELEMENT_PAR_CHGT);

        // on créé un deamon pour que le thread chef_de_rayon s'arrête une fois tous les clients passés et qu'il
        //ne bloque pas le programme en tournant indéfiniement
        chef_de_rayon.setDaemon(true);
        chef_de_rayon.start();

        for (Client client : listeClients) {
            client.start();
        }



    }
}
