import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void run() {

        while (true) {

            for (int i = 0; i < rayons.size(); i++) {
                Rayon rayon = rayons.get(i);
                rayon.setChefRayonSurPlace(true);
                int nombreDeProduitsDecharges = rayon.equilibrage(this);
                this.chargement.put(rayon.getName(), chargement.get(rayon.getName())- nombreDeProduitsDecharges);
            }

        }
    }
}