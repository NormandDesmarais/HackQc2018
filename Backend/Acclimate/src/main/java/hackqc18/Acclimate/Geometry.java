package hackqc18.Acclimate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Geometry {
    public static final String TYPE_POINT = "Point";
    public static final String TYPE_POLYGON = "Polygon";
    
    private String type;
    private final ArrayList<double[]> coordinates;

    /**
     * Constructor for Point type Geometry
     * @param lng longitudinal coordinate
     * @param lat latitudinal coordinate
     */
    public Geometry(double lng, double lat) {
        type = TYPE_POINT;
        coordinates = new ArrayList<>();
        coordinates.add(new double[]{lng, lat});
    }
    

    public String getType() {
        return type;
    }

    public ArrayList<double[]> getCoordinates() {
        return coordinates;
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
