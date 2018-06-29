package hackqc18.Acclimate.deprecated;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Deprecated
public class PutAlert {

    private final static ArrayList<Alerte> USER_ALERTS = new ArrayList<>();

    private String statusMsg;

    public static ArrayList<Alerte> getUserAlerts() {
        return new ArrayList<>(USER_ALERTS);
    }

    public PutAlert(String type, String latValue, String lngValue) {
        double lng = Double.parseDouble(lngValue);
        double lat = Double.parseDouble(latValue);
        String date = LocalDateTime.now().toString();
        /**
         * latitude  :    sud (-90) / nord (90)
         * longitude : ouest (-180) / est (180)
         */

        if (lng > -84 && lng < -58 && lat > 40 && lat < 66) {

            String userAlrId = (int)distanceInMeter(-84, lng, lat, lng) + "-"
                    + (int)distanceInMeter(lat, 66, lat, lng);

            for (Alerte alerte : USER_ALERTS) {
                if (alerte.getId().equals(userAlrId)) {
                    alerte.increment(lat, lng, date);
                    this.statusMsg = "Alerte comfirmée, merci de votre participation!";
                    return;
                }
                // TODO - define default duration by alert type (day, hour, min)
                if (alerte.isOlderThan(1, 0, 0)) {
                    USER_ALERTS.remove(alerte);
                }

            }

            Alerte alerte = new Alerte(type, "usager", "inconnu",
                    "à déterminer", "inconnue", type, date, userAlrId, "inconnue",
                    "alerte usager", lng, lat);

            USER_ALERTS.add(alerte);

            this.statusMsg = "Nouvelle alerte ajoutée. Merci pour votre aide!";
        } else {
            this.statusMsg = "Coordonnées non supportées pour le moment.";
        }

    }

    // generally used geo measurement function
    public double distanceInMeter(double lat1, double lon1, double lat2, double lon2) {
        double R = 6378.137; // Radius of earth in KM
        double dLat = (lat2 - lat1) * Math.PI / 180;
        double dLon = (lon2 - lon1) * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d * 1000; // meters
    }

    public String statusMsg() {
        return this.statusMsg;
    }

}
