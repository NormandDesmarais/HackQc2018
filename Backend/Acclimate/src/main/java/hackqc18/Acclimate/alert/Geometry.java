package hackqc18.Acclimate.alert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import hackqc18.Acclimate.deprecated.Alerte;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Embeddable;

// TODO: add support for polygon or replace with GeoTools classes
@Embeddable
public class Geometry {
    public static final String TYPE_POINT = "Point";
    public static final String TYPE_POLYGON = "Polygon";
    
    private String type;
    private double[] coordinates;

    public Geometry() {
        
    }
    
    /**
     * Constructor for Point type Geometry
     * @param lng longitudinal coordinate
     * @param lat latitudinal coordinate
     */
    public Geometry(double lng, double lat) {
        type = TYPE_POINT;
        coordinates = new double[]{lng, lat};
    }
    

    public String getType() {
        return type;
    }

    public double[] getCoordinates() {
        return coordinates;
    }


    public void setType(String type) {
        this.type = type;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(Alerte.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "{}";
    }
}
