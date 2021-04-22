public class Chariot {

    /**
     * Nombre de chariots du supermarché disponibles
     */
    private int nbChariots;

    public Chariot(int nbChariots){
        this.nbChariots=nbChariots;
    }

    /** Méthode permettant à un client de prendre un chariot
     * @param client : permet d'obtenir l'index du client qui souhaite prendre un chariot
     * Synchronized : exclusion mutuelle pour la variable partagée nbChariots
     */
    public synchronized void prendreChariot(Client client){
        // On a un seul type de processus donc on utilise un if et on ne réveillera qu'un seul processus lorsqu'un
        // chariot sera rendu à l'aide d'un notify(). Il n'y a donc pas besoin de revérifier cette condition car il y
        // aura forcémment un chariot de disponible si le processus est réveillé.
        // Si il n'y a pas de chariot disponible, le client est mis en attente
        if(nbChariots==0){
            try {
                System.out.println("Le client n°" + client.getIndex()+" ne peut plus prendre de chariot," +
                        " mise en attente sur Chariot." );
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // On actualise le nombre de chariots disponibles : on en retire 1
        nbChariots--;
        System.out.println("Le client n°" + client.getIndex()+" prend un chariot." );
    }

    /** Méthode permettant à un client de rendre un chariot
     * @param client : permet d'obtenir l'index du client qui souhaite rendre un chariot
     * Synchronized : exclusion mutuelle pour la variable partagée nbChariots
     */
    public synchronized void rendreChariot(Client client){

        // On actualise le nombre de chariots disponibles : on en ajoute 1
        nbChariots++;
        System.out.println("Le client n°" + client.getIndex()+" rend un chariot." );

        // On réveille un seul thread car il n'y a qu'un seul type de thread en attente : ceux des clients qui
        // attendent de pouvoir prendre un chariot. On réveille donc un seul client qui va pouvoir prendre son chariot.
        notify();
    }
}
