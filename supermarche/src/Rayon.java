public class Rayon {
    /**
     * L'index du rayon (numero du rayon)
     */
    private int index;

    /**
     * Le nombre de produits a l'initialisation
     */
    private int stock;

    /**
     * Le nombre maximum de produits dans le rayon
     */
    private int stockMax;

    /**
     * Le nom du rayon
     */
    private String nom;

    public Rayon(int index, String nom, int stockMax, int stock){
        this.index=index;
        this.nom=nom;
        this.stockMax=stockMax;
        this.stock=stock;
    }

    /**
     * Retourne l'index du rayon
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * Retourne le nom du rayon
     */
    public String getName() {
        return this.nom;
    }
}
