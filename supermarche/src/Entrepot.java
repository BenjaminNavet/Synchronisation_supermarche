import java.util.Map;

public class Entrepot {
    Map<String, Integer> StockEntrepot;

    public Entrepot(Map<String, Integer> StockEntrepot) {
        this.StockEntrepot=StockEntrepot;
    }

    public int takeProductFromEntrepot(String name, int nbStockDemande){
        if(this.StockEntrepot.get(name)==-1){
            return nbStockDemande;
        }else{
            int nbStockAjout=0;
            while (this.StockEntrepot.get(name) > 0 && nbStockAjout<=nbStockDemande) {
                this.StockEntrepot.put(name, this.StockEntrepot.get(name) - 1);
                nbStockAjout++;
            }
            return nbStockAjout;
        }
    }

}
