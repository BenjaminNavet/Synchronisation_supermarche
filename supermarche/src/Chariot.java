public class Chariot {

    private int nbChariots;

    public Chariot(int nbChariots){
        this.nbChariots=nbChariots;
    }

    public synchronized void prendreChariot(Client client){
        if(nbChariots==0){
            try {
                System.out.println("Le client n°" + client.getIndex()+" ne peut plus prendre de chariot," +
                        " mise en attente sur Chariot." );
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        nbChariots--;
        System.out.println("Le client n°" + client.getIndex()+" prend un chariot." );
    }

    public synchronized void rendreChariot(Client client){
        nbChariots++;
        System.out.println("Le client n°" + client.getIndex()+" rend un chariot." );
        notify();
    }
}
