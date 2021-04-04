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
     * L'index du rayon
     */
    private int index;

    /**
     * chef de rayon sur place
     */
    private volatile boolean ChefRayonSurPlace = false;



    public Rayon(int index, String nom, int stockMax, int stock){
        this.index=index;
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
     * Retourne l'index du rayon
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * modifie la valeur du booléen chefRayonSurPlace
     * @param chefRayonSurPlace
     */
    public void setChefRayonSurPlace(boolean chefRayonSurPlace) {
        ChefRayonSurPlace = chefRayonSurPlace;
    }


    /**
     * Le client prend un produit dans le rayon
     */
    //ici paramètres pour voir l'execution : on laisse ou pas dans le rendu prof?
    public synchronized void takeProduct(Client client){
        while(stock==0 || ChefRayonSurPlace){
            try {
                System.out.println("Le client n°" + client.getIndex() + " (" + client.getNom() + ") ne peut plus " +
                        "prendre de " + getName() + ", mise en attente sur Rayon " + getIndex() + "." );
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        stock --;
        System.out.println("Le client n°"+client.getIndex()+" ("+client.getNom()+") prend 1 article de "+getName()+".");
        notify();
    }

    /**
     * Le chef de rayon rajoute des produits en stock si nécessaire
     */
    public synchronized int equilibrage(ChefRayon chefRayon){
        int besoinArticle=this.stockMax-this.stock;
        int nbAddArticle=Math.min(besoinArticle,chefRayon.getStock(getName()));

        for(int i=0;i < nbAddArticle;i++){
            stock ++;
        }
        System.out.println("Le chef de rayon remet "+nbAddArticle+" "+getName()+"(s) dans le rayon "+getIndex()+".");

        setChefRayonSurPlace(false);
        notify(); // pour prévenir les clients qui attendent de prendre un article

        return chefRayon.getStock(getName())-nbAddArticle;
    }

}
