package hackqc18.Acclimate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Alerts {

    private ArrayList<Alerte> alertes;
    private ArrayList<AlerteWrapper> alerteWrappers = null;

    /**
     * Constructor for an empty list of alerts
     */
    public Alerts() {
        alertes = new ArrayList<>();
    }

    /**
     * Constructor for an existing list of alerts
     *
     * @param alerts list of existing alerts
     */
    public Alerts(ArrayList<Alerte> alerts) {
        alertes = new ArrayList<>(alerts);
    }

    /**
     * Constructor for an existing list of alerts but that we want to restrict
     * within the given bounding box.
     *
     * @param nord northern most latitude
     * @param sud southern most latitude
     * @param est eastern most longitude
     * @param ouest western most longitude
     * @param userAlerts list of existing alerts
     */
    public Alerts(double nord, double sud, double est, double ouest,
            ArrayList<Alerte> userAlerts) {
        /**
         * latitude : sud (-90) / nord (90) longitude : ouest (-180) / est (180)
         */
        if (ouest <= -84 && est >= -58 && sud <= 40 && nord >= 66) {
            alertes = new ArrayList<>(userAlerts);
        } else {
            this.alertes = new ArrayList<>();
            for (Alerte alert : userAlerts) {
                if (alert.overlapWithBox(nord, sud, est, ouest)) {
                    alertes.add(alert);
                }
            }
        }

    }

    public void add(Alerte alert) {
        alertes.add(alert);
    }

    public void addAll(double nord, double sud, double est, double ouest,
            ArrayList<Alerte> userAlerts) {
        if (ouest <= -84 && est >= -58 && sud <= 40 && nord >= 66) {
            alertes.addAll(userAlerts);
        } else {
            for (Alerte alert : userAlerts) {
                if (alert.overlapWithBox(nord, sud, est, ouest)) {
                    alertes.add(alert);
                }
            }
        }
    }

//    /**
//     * Uncomment this code once clients have removed the string "alerte"
//     * for each alert.
//     * @return 
//     */
//    public ArrayList<Alerte> getAlertes() {
//        return alertes;
//    }
    /**
     * Remove this code once clients have removed the string "alerte" for each
     * alert.
     *
     * @return
     */
    public ArrayList<AlerteWrapper> getAlertes() {
        if (alerteWrappers == null) {
            alerteWrappers = new ArrayList<>();
            for (Alerte e : alertes) {
                alerteWrappers.add(new AlerteWrapper(e));
            }
        }
        return alerteWrappers;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(Alerts.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "[]";
    }
}
