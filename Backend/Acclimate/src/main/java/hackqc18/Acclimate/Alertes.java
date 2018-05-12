package hackqc18.Acclimate;

import java.util.ArrayList;

public class Alertes {

    private ArrayList<Alerte> alertes;

    public Alertes(double nord, double sud, double est, double ouest) {
        alertes = AlertesFluxRss.theInstance().alertsInBox(nord, sud, est, ouest);
    }

    public Alertes(double nord, double sud, double est, double ouest,
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
            double[] coord = e.getCoord().getData().get(0);
            if (coord[0] > ouest && coord[0] < est && coord[1] > sud && coord[1] < nord) {
                this.alertes.add(e);
            }
                
        }
        
    }

    public ArrayList<Alerte> getAlertes() {
        return alertes;
    }

    @Override
    public String toString() {
        if (alertes.isEmpty()) return "{\"alertes\" : []}";
        String result = "{\n\"alertes\": [";

        for (int i = 0; i < alertes.size() - 1; i++) {
            result += alertes.get(i).toString() + ",";
        }
        result += alertes.get(alertes.size() - 1).toString();
        return result + "]\n}";
    }

}
