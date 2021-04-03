import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

public class ChefRayon {

    Map<String, Integer> chargement = new HashMap();
    List<Rayon> rayons;

    public ChefRayon(List<Rayon> rayons) {

        this.rayons = rayons;
        for (Rayon rayon : rayons) {
            chargement.put(rayon.getName(), 0);
        }
    }


    public int getStock(String name) {
        return chargement.get(name);
    }

    public void recharge(){
        for (int i = 0; i < rayons.size(); i++) {
            Rayon rayon = rayons.get(i);
            this.chargement.put(rayon.getName(), 5);
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void changeDeRayon(){
        try {
            sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        while (true) {

            // Le chef de Rayon reprend 5 articles pour chaque type de produit
            recharge();
            // Le chef de Rayon va au premier rayon
            changeDeRayon();

            for (int i = 0; i < rayons.size(); i++) {
                Rayon rayon = rayons.get(i);
                rayon.setChefRayonSurPlace(true);
                int nombreDeProduitsDecharges = rayon.equilibrage(this);
                this.chargement.put(rayon.getName(), chargement.get(rayon.getName())- nombreDeProduitsDecharges);
                changeDeRayon();
            }

        }
    }
}