public class AccesTapisCaisse {

    /**
     * UnClientUtiliseLeTapis indique si un client pose déjà ses articles sur le tapis
     */
    private volatile boolean UnClientUtiliseLeTapis = false;

    public AccesTapisCaisse(){
    }

    /**
     * modifie la valeur du booléen UnClientUtiliseLeTapis
     * @param unClientUtiliseLeTapis : indique si un client pose déjà ses articles sur le tapis
     */
    public void setUnClientUtiliseLeTapis(boolean unClientUtiliseLeTapis) {
        UnClientUtiliseLeTapis = unClientUtiliseLeTapis;
    }

    /** Un client souhaite déposer ses articles sur le tapis de caisse lors de son passage en caisse
     * @param client : permet d'obtenir l'index du client qui souhaite entrer en caisse
     * Synchronized : entrée en exclusion mutuelle pour la variable UnClientUtiliseLeTapis
     */
    public synchronized void deposeSurTapisDeCaisse(Client client) {
        // On a un seul type de processus en concurence donc on utilise un if.
        // Quand ils sont réveillés, ils ne doivent donc pas revérifier cette condition.
        // Mise en attente si un client dépose déjà des articles sur le tapis
        if(UnClientUtiliseLeTapis){
            try {
                System.out.println("Le client n°" + client.getIndex() +" ne peut pas poser ses articles " +
                        "(un autre client utilise actuellement le tapis).");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Indique qu'un client utilise le tapis
        setUnClientUtiliseLeTapis(true);
        System.out.println("Le client n°" + client.getIndex() +" commence à poser ses articles");
    }

    /** Un client a fini de déposer ses articles sur le tapis de caisse lors de son passage en caisse
     * @param client : permet d'obtenir l'index du client qui souhaite entrer en caisse
     * Synchronized : entrée en exclusion mutuelle pour le réveil des processus en attente
     */
    public synchronized void aFiniDeDeposeSurTapisDeCaisse(Client client) {
        // Indique qu'aucun client n'utilise le tapis
        setUnClientUtiliseLeTapis(false);
        System.out.println("Le client n°" + client.getIndex() +" a fini de poser ses articles");
        // On libère une seule place car seulement une personne peut accèder au tapis donc un seul processus en attente
        // est réveillé
        notify();
    }

}
