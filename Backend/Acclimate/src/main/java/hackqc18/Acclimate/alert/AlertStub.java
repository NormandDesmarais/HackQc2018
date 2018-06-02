package hackqc18.Acclimate.alert;

// NOTE: This class only has the type, longitude and latitude for now.
//       this may change in the future.
/**
 * Class used to receive user alerts.
 */
public class AlertStub {
    private String type;
    private double lng;
    private double lat;

    /**
     * No-arg constructor, mandatory for automatic JSON conversion.
     */
    public AlertStub() {

    }


    /**
     * Full fleshed constructor with all properties.
     * @param type le type d'alertes levées
     * @param lng longitude
     * @param lat latitude
     */
    public AlertStub(String type, int lng, int lat) {
        super();
        this.type = type;
        this.lng = lng;
        this.lat = lat;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(int lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }



    /**
     * Utility method that creates a unique id from the alert type, longitude
     * and latitude. Alerts of the same type located within a 1 KM square will
     * share the same id and will thus be identified has the same alert.
     *
     * @return the id
     */
    public String createId() {
        return type.hashCode() + "-"
                + distanceFromZeroInKM(lng) + "-"
                + distanceFromZeroInKM(lat);
    }


    /**
     * Utility method that computes the distance in KM of a longitude or
     * latitude coordinate from the zero value of this coordinate.
     *
     * @param coordinate the longitude or latitude coordinate
     * @return the distance in KM from the zero value of this coordinate
     */
    private int distanceFromZeroInKM(double coordinate) {
        double R = 6378.137; // Radius of earth in KM
        double a = Math.abs(Math.sin(coordinate * Math.PI / 180 / 2));
        double a2 = a * a;
        double d = 2 * R * Math.atan2(a, Math.sqrt(1 - a2));
        return (int) Math.round(d); // KM
    }
}
