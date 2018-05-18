package hackqc18.Acclimate;

//import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetAlertes {

    private ArrayList<Alerte> alertes;
    private ArrayList<AlerteWrapper> alerteWrappers = null;

    public GetAlertes(double nord, double sud, double est, double ouest) {
        alertes = AlertesFluxRss.theInstance().alertsInBox(nord, sud, est, ouest);
    }

    public GetAlertes(double nord, double sud, double est, double ouest,
            ArrayList<Alerte> userAlerts) {
        /**
         * latitude  :    sud (-90) / nord (90)
         * longitude : ouest (-180) / est (180)
         */
        if (ouest <= -84 && est >= -58 && sud <= 40 && nord >= 66) {
            this.alertes =  new ArrayList<>(userAlerts);
        }

        this.alertes =  new ArrayList<>();
        for (Alerte e : userAlerts) {
            double[] coord = e.getGeometry().getCoordinates().get(0);
            if (coord[0] > ouest && coord[0] < est && coord[1] > sud && coord[1] < nord) {
                this.alertes.add(e);
            }
                
        }
        
    }

    public ArrayList<AlerteWrapper> getAlertes() {
//    public ArrayList<Alerte> getAlertes() {
        if (alerteWrappers == null) {
            alerteWrappers = new ArrayList<AlerteWrapper>();
            for (Alerte e : alertes) {
                alerteWrappers.add(new AlerteWrapper(e));
            }
        }
//        return alertes;
        return alerteWrappers;
    }

    public String toJSON() {
        if (alertes.isEmpty()) return "{\"alertes\" : []}";
        String result = "{\n\"alertes\": [";

        for (int i = 0; i < alertes.size() - 1; i++) {
            result += alertes.get(i).toJSON() + ",";
        }
        result += alertes.get(alertes.size() - 1).toJSON();
        return result + "]\n}";
    }

    
    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JtsModule());
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(GetAlertes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "[]";
    }
}
