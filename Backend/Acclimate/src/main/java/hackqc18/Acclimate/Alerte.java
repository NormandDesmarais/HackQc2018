package hackqc18.Acclimate;

import java.time.LocalDateTime;

public class Alerte {

    private final String nom;
    private final String source;
    private final String territoire;
    private String certitude;
    private final String severite;
    private final String type;
    private String dateDeMiseAJour;
    private final String id;
    private final String urgence;
    private final String description;
    private final String geom;
    private int count;
    private Geometry geometry;

    public Alerte(String nom, String source, String territoire,
            String certitude, String severite, String type,
            String dateDeMiseAJour, String id, String urgence,
            String description, String geom, Geometry coord) {

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
        this.geom = geom;
        this.count = 1;
        this.geometry = coord;
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
        this.geom = "";
        this.geometry = new Geometry("Point", lng, lat);
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
     *      AAAA-MM-JJTHH:MM:SS
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
        return (dDays > 0 || (dDays == 0 &&
                (dHours > 0 || (dHours == 0 && dMin > 0))));
    }

    public String toJSON() {
        return "{\"alerte\" : {"
                + "\"id\": \"" + id + "\","
                + "\"count\": \"" + count + "\","
                + "\"nom\": \"" + nom + "\","
                + "\"source\": \"" + source + "\","
                + "\"territoire\": \"" + territoire + "\","
                + "\"certitude\": \"" + certitude + "\","
                + "\"severite\": \"" + severite + "\","
                + "\"type\": \"" + type + "\","
                + "\"dateDeMiseAJour\": \"" + dateDeMiseAJour + "\","
                + "\"urgence\": \"" + urgence + "\","
                + "\"description\": \"" + description + "\","
                + "\"geometry\": " + geom
                + "}}";
    }
    
}
