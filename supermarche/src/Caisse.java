import java.util.ArrayList;
import java.util.List;

public class Caisse {

    /**
     * Buffer représentant le tapis de caisse
     */
    public Integer[] tapis;

    /**
     * iprod est l'indice du buffer dans lequel le client va déposer son article suivant
     */
    public int iprod;

    /**
     * icons est l'indice du buffer dans lequel l'employé de caisse va prendre l'article suivant pour le scanner
     */
    public int icons;

    /**
     * Nombre d'espace(s) vide(s) sur le tapis
     */
    public int nbvide;

    /**
     * Nombre d'espace(s) rempli(s) sur le tapis
     */
    public int nbplein;

    /**
     * Nombre d'espace(s) sur le tapis
     */
    public int taille_tapis;

    /**
     * Temps que met un client pour poser un article
     */
    public int tps_pose_article;

    /**
     * Temps que met un employé de caisse pour scanner un article
     */
    public int tps_scanne_article;

    /**
     * Temps que met un client pour payer
     */
    public int tps_paiement;

    /**
     * EmployeCaisseAFiniDeScannerPourUnClient indique si l'employé de caisse a fini de scanner les articles du
     * client en attente de paiement
     */
    private volatile boolean EmployeCaisseAFiniDeScannerPourUnClient = false;

   ZonePaiement zonePaiement;
   TapisCaisse tapisCaisse;

    /**
     * Liste de client(s) en attente de Paiement
     */
    List<Integer> listeAttentePaiement = new ArrayList<>();

    public Caisse(int taille_tapis, int tps_pose_article, int tps_scanne_article, int tps_paiement) {
        this.tapis = new Integer[taille_tapis];
        this.nbvide = taille_tapis;
        this.nbplein = 0;
        this.icons = 0;
        this.iprod = 0;
        this.taille_tapis = taille_tapis;
        this.tps_pose_article = tps_pose_article;
        this.tps_scanne_article = tps_scanne_article;
        this.tps_paiement = tps_paiement;
        this.zonePaiement = new ZonePaiement(this.tps_paiement,
                                             this.EmployeCaisseAFiniDeScannerPourUnClient,
                                             this.listeAttentePaiement);
        this.tapisCaisse = new TapisCaisse(this.taille_tapis,
                                           this.tps_pose_article,
                                           this.tps_scanne_article,
                                           this.EmployeCaisseAFiniDeScannerPourUnClient,
                                           this.listeAttentePaiement);
    }











}
