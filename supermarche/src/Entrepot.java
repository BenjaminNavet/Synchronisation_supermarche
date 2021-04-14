import java.util.Map;

public class Entrepot {
    Map<Integer, Integer> StockEntrepot;

    public Entrepot(Map<Integer, Integer> StockEntrepot) {
        this.StockEntrepot=StockEntrepot;
    }

    public int takeProductFromEntrepot(Integer index, int nbStockDemande){
        if(this.StockEntrepot.get(index)==-1){
            return nbStockDemande;
        }else{
            int nbStockAjout=0;
            while (this.StockEntrepot.get(index) > 0 && nbStockAjout<=nbStockDemande) {
                this.StockEntrepot.put(index, this.StockEntrepot.get(index) - 1);
                nbStockAjout++;
            }
            return nbStockAjout;
        }
    }

}
