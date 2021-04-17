public class Rayon {

    /**
     * Le nombre de produits a l'initialisation
     */
    private int stock;

    /**
     * Le nombre maximum de produits dans le rayon
     */
    private final int stockMax;

    /**
     * Le nom du rayon
     */
    private final String nom;

    /**
     * L'index du rayon
     */
    private final int index;

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
     * Modifie la valeur du booléen chefRayonSurPlace
     * @param chefRayonSurPlace : indique si le chef de rayon est sur place
     */
    public void setChefRayonSurPlace(boolean chefRayonSurPlace) {
        ChefRayonSurPlace = chefRayonSurPlace;
    }


    /** Méthode permettant à un client de prendre un produit dans le rayon
     * @param client : permet d'obtenir l'index du client qui souhaite prendre un article
     */
    public synchronized void takeProduct(Client client){
        // On a plusieurs processus en concurence donc on utilise un while.
        // Quand ils sont réveillés, ils doivent revérifier cette condition.
        // Tant qu'il n'y a plus de stock ou que le chef de rayon remet des articles dans le rayon, le client est
        // mis en attente
        while(stock==0 || ChefRayonSurPlace){
            try {
                System.out.println("Le client n°" + client.getIndex() + " ne peut plus prendre de " + getName() +
                        ", mise en attente sur Rayon " + getIndex() + "." );
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Modification du stock : décrémentation d'1 produit
        stock --;
        System.out.println("Le client n°"+client.getIndex()+" prend 1 article de "+getName()+".");
        // Un notify() suffit car les seuls threads en attente sont ceux des clients et un seul client à la fois peut
        // prendre un produit dans le rayon donc il suffit de réveiller un seul client
        notify();
    }


    /** Méthode permettant au chef de rayon de remplir le rayon
     * @param chefRayon : le chef de rayon qui vient remplir le rayon
     * @return nbAddArticle : le nombre d'article ajouté au rayon par le chef de rayon
     */
    public synchronized int equilibrage(ChefRayon chefRayon){
        // Le nombre d'articles nécessaires pour remplir intégralement le rayon
        int besoinArticle=this.stockMax-this.stock;

        // Le nombre d'articles ajoutés est le minimum entre le nombre d'articles nécessaires pour remplir
        // intégralement le rayon et le nombre de produits dont dispose le chef de rayon
        int nbAddArticle=Math.min(besoinArticle,chefRayon.getStock(getIndex()));

        // Ajout de produit(s) dans le rayon en incrémentant le stock
        for(int i=0;i < nbAddArticle;i++){
            stock ++;
        }
        System.out.println("Le chef de rayon remet "+nbAddArticle+" "+getName()+"(s) dans le rayon "+getIndex()+".");

        // On indique que le chef de rayon n'est plus sur place, les clients pourront donc de nouveau accèder au rayon
        setChefRayonSurPlace(false);

        // Un notify() suffit car les seuls threads en attente sont ceux des clients et un seul client à la fois peut
        // prendre un produit dans le rayon donc il suffit de réveiller un seul client
        notify();

        return nbAddArticle;
    }

}
