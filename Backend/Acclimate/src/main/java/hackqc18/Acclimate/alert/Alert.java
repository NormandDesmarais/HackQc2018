package hackqc18.Acclimate.alert;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Alert {

    // observed and probable "certitude" count are triggers used
    // to update the "certitude" status accordingly
    private static final int OBSERVED_CERTITUDE_COUNT = 10;
    private static final int PROBABLE_CERTITUDE_COUNT = 5;

    @Id
    private String id;

    private String nom;
    private String source;
    private String territoire;
    private String certitude;
    private String severite;
    // we have to use typeAlert instead of type only, because the database
    // flattens geometry properties which already has a property named type
    private String   typeAlert;
    private String   dateDeMiseAJour;
    private String   urgence;
    private String   description;
    private int      count;
    private Geometry geometry;


    public Alert() {

    }


    public Alert(String id, String nom, String source, String territoire,
            String certitude, String severite, String type,
            String dateDeMiseAJour, String urgence, String description,
            int count, Geometry geometry) {
        super();
        this.id = id;
        this.nom = nom;
        this.source = source;
        this.territoire = territoire;
        this.certitude = certitude;
        this.severite = severite;
        this.typeAlert = type;
        this.dateDeMiseAJour = dateDeMiseAJour;
        this.urgence = urgence;
        this.description = description;
        this.count = count;
        this.geometry = geometry;
    }


    /**
     * Constructor used by other alert from the live RSS stream.
     *
     * @param id
     * @param nom
     * @param source
     * @param territoire
     * @param certitude
     * @param severite
     * @param type
     * @param dateDeMiseAJour
     * @param urgence
     * @param description
     * @param lng
     * @param lat
     */
    public Alert(String id, String nom, String source, String territoire,
            String certitude, String severite, String type,
            String dateDeMiseAJour, String urgence, String description,
            double lng, double lat) {
        super();
        this.id = id;
        this.nom = nom;
        this.source = source;
        this.territoire = territoire;
        this.certitude = certitude;
        this.severite = severite;
        this.typeAlert = type;
        this.dateDeMiseAJour = dateDeMiseAJour;
        this.urgence = urgence;
        this.description = description;
        this.count = 1;
        this.geometry = new Geometry(lng, lat);
    }


    /**
     * Constructor used by UserAlertService
     *
     * @param id
     * @param type
     * @param lng
     * @param lat
     */
    public Alert(String id, String type, double lng, double lat) {
        super();
        this.id = id;
        this.nom = type.substring(0);
        this.source = "usager";
        this.territoire = "inconnu";
        this.certitude = "à déterminer";
        this.severite = "inconnue";
        this.typeAlert = type;
        this.dateDeMiseAJour = LocalDateTime.now().toString();
        this.urgence = "inconnue";
        this.description = "alerte usager";
        this.count = 1;
        this.geometry = new Geometry(lng, lat);
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getNom() {
        return nom;
    }


    public void setNom(String nom) {
        this.nom = nom;
    }


    public String getSource() {
        return source;
    }


    public void setSource(String source) {
        this.source = source;
    }


    public String getTerritoire() {
        return territoire;
    }


    public void setTerritoire(String territoire) {
        this.territoire = territoire;
    }


    public String getCertitude() {
        return certitude;
    }


    public void setCertitude(String certitude) {
        this.certitude = certitude;
    }


    public String getSeverite() {
        return severite;
    }


    public void setSeverite(String severite) {
        this.severite = severite;
    }


    public String getType() {
        return typeAlert;
    }


    public void setType(String type) {
        this.typeAlert = type;
    }


    public String getDateDeMiseAJour() {
        return dateDeMiseAJour;
    }


    public void setDateDeMiseAJour(String dateDeMiseAJour) {
        this.dateDeMiseAJour = dateDeMiseAJour;
    }


    public String getUrgence() {
        return urgence;
    }


    public void setUrgence(String urgence) {
        this.urgence = urgence;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public int getCount() {
        return count;
    }


    public void setCount(int count) {
        this.count = count;
    }


    public Geometry getGeometry() {
        return geometry;
    }


    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }


    /**
     * This method only supports Point type Alert for now.
     *
     * @param nord northern most latitude
     * @param sud southern most latitude
     * @param est eastern most longitude
     * @param ouest western most longitude
     * @return true if the point is within the box defined by the north south,
     *         east and west parameters
     */
    public boolean overlapWithBox(double north, double south, double east,
            double west) {
        double coordLng = geometry.getCoordinates()[0];
        double coordLat = geometry.getCoordinates()[1];

        return (coordLng > west && coordLng < east && coordLat > south
                && coordLat < north);

    }


    /**
     * Increases the count of an alert and changes its "certitude" status
     * accordingly. It also updates the "dateDeMiseAJour" property to the
     * current date and time.
     *
     * @param alert the alert
     */
    public void increaseCount() {
        count++;
        dateDeMiseAJour = LocalDateTime.now().toString();
        if (count == OBSERVED_CERTITUDE_COUNT) {
            certitude = "Observé";
        } else if (count == PROBABLE_CERTITUDE_COUNT) {
            certitude = "Probable";
        }
    }
}
