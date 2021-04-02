public class Rayon {

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

    /**
     * Le chef de rayon
     */
    private ChefRayon chefRayon;

    public Rayon(String nom, int stockMax, int stock){
        this.nom=nom;
        this.stockMax=stockMax;
        this.stock=stock;
    }

    /**
     * Retourne le nom du rayon
     */
    public String getName() {
        return this.nom;
    }


    /**
     * Le client prend un produit dans le rayon
     */
    public synchronized void takeProduct(){
        while(stock<1){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        stock --;
        notifyAll();
    }

    /**
     * Le chef de rayon rajoute des produits en stock si nécessaire
     */
    public synchronized int equilibrage(ChefRayon chefRayon){

        int besoinArticle=this.stockMax-this.stock;
        int nbAddArticle=Math.min(besoinArticle,chefRayon.getStock(getName()));

        for(int i=0;i > nbAddArticle;i++){
            this.stock++;
        }

        notifyAll(); // pour prévenir les clients qui attendent de prendre un article
        System.out.println("Le chef de rayon a équilibré le rayon "+ getName() +".");
        return chefRayon.getStock(getName())-nbAddArticle;
    }

}
