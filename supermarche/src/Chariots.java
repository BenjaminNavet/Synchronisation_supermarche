public class Chariots {

    private int nbChariots;

    public Chariots(int nbChariots){
        this.nbChariots=nbChariots;
    }

    public synchronized void prendreChariot(){
        while(nbChariots==0){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        nbChariots--;
    }

    public synchronized void rendreChariot(){
        nbChariots++;
        notify();
    }
}
