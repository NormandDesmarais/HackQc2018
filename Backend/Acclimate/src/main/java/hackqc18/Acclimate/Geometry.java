package hackqc18.Acclimate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Geometry {

    private String type;
    private final ArrayList<double[]> coordinates = new ArrayList<>();

    public Geometry(String type) {
        this.type = type;
    }
    
    public Geometry(String type, double x, double y) {
        this.type = type;
        coordinates.add(new double[]{x, y});
    }

    public void add(double x, double y) {
        coordinates.add(new double[]{x, y});
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
