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

}
