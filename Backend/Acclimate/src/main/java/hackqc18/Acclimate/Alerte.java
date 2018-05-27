package hackqc18.Acclimate;

import java.time.LocalDateTime;

public class Alerte {

    private String id;
    private String nom;
    private String source;
    private String territoire;
    private String certitude;
    private String severite;
    private String type;
    private String dateDeMiseAJour;
    private String urgence;
    private String description;
    private int count;
    private Geometry geometry;


    public Alerte() {
        
    }
    

    public Alerte(String nom, String source, String territoire,
            String certitude, String severite, String type,
            String dateDeMiseAJour, String id, String urgence,
            String description, double lng, double lat) {

        this.nom = nom;
        this.source = source;
        this.territoire = territoire;
        this.certitude = certitude;
        this.severite = severite;
        this.type = type;
        this.dateDeMiseAJour = dateDeMiseAJour;
        this.id = id;
        this.urgence = urgence;
        this.description = description;
        this.count = 1;
        this.geometry = new Geometry(lng, lat);
    }

    public String getNom() {
        return nom;
    }

    public String getSource() {
        return source;
    }

    public String getTerritoire() {
        return territoire;
    }

    public String getCertitude() {
        return certitude;
    }

    public String getSeverite() {
        return severite;
    }

    public String getType() {
        return type;
    }

    public String getDateDeMiseAJour() {
        return dateDeMiseAJour;
    }

    public String getUrgence() {
        return urgence;
    }

    public String getDescription() {
        return description;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public int getCount() {
        return count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setTerritoire(String territoire) {
        this.territoire = territoire;
    }

    public void setCertitude(String certitude) {
        this.certitude = certitude;
    }

    public void setSeverite(String severite) {
        this.severite = severite;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDateDeMiseAJour(String dateDeMiseAJour) {
        this.dateDeMiseAJour = dateDeMiseAJour;
    }

    public void setUrgence(String urgence) {
        this.urgence = urgence;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public void increment(double lat, double lng, String date) {
        // TODO - renormalisé la posiiton du point
        //      ((x*count)+lat)/(count+1)
        //      ((y*count)+lng)/(count+1)
        count++;
        dateDeMiseAJour = date;
        if (count == 10) {
            certitude = "Observé";
        } else if (count == 5) {
            certitude = "Probable";
        }
    }

    /**
     * This method assume that the alert date is in the following format:
     * AAAA-MM-JJTHH:MM:SS
     *
     * @param days number of days
     * @param hours number of hours
     * @param minutes number of minutes
     * @return true if the alert date is older than the one given
     */
    public boolean isOlderThan(int days, int hours, int minutes) {
        LocalDateTime alrTime = LocalDateTime.parse(dateDeMiseAJour);
        LocalDateTime now = LocalDateTime.now();

        int dDays = now.getDayOfYear() - alrTime.getDayOfYear() - days;
        int dHours = now.getHour() - alrTime.getHour() - hours;
        int dMin = now.getMinute() - alrTime.getMinute() - minutes;
        return (dDays > 0 || (dDays == 0
                && (dHours > 0 || (dHours == 0 && dMin > 0))));
    }

    /**
     * This method only supports Point type Alert for now.
     *
     * @param nord northernmost latitude
     * @param sud southernmost latitude
     * @param est easternmost longitude
     * @param ouest westernmost longitude
     * @return true if the point is within the box defined by nord, sud, est et
     * ouest.
     */
    public boolean overlapWithBox(
            double nord, double sud, double est, double ouest) {
        double coordLng = geometry.getCoordinates()[0];
        double coordLat = geometry.getCoordinates()[1];

        return (coordLng > ouest && coordLng < est
                && coordLat > sud && coordLat < nord);

    }

}
