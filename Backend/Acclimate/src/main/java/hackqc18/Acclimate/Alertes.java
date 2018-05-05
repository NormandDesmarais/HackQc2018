package hackqc18.Acclimate;

import java.util.ArrayList;

public class Alertes {

    private final ArrayList<Alerte> alertes = new ArrayList<>();

    public Alertes(double longitude, double latitude) {
        PointJSON point = new PointJSON(longitude + 2, latitude + 3);
        
        
        
//        Polygon polygon = geoFact.createPolygon(new Coordinate[]{
//            new Coordinate(longitude + 4, latitude + 4),
//            new Coordinate(longitude - 4, latitude + 4),
//            new Coordinate(longitude - 4, latitude - 4),
//            new Coordinate(longitude + 4, latitude - 4)});

        alertes.add(new Alerte("innondation", "le gouvernement", "Montréal",
                "certain", "grave", "innondation", "1978-05-04", "00001", "urgent",
                "on coule!", point.toString()));
//        alertes.add(new Alerte("innondation", "le gouvernement", "Montréal",
//                "certain", "grave", "innondation", "1978-05-04", "00001", "urgent",
//                "on coule!", point2));
//        alertes.add(new Alerte("innondation", "le gouvernement", "Montréal",
//                "certain", "grave", "innondation", "1978-05-04", "00001", "urgent",
//                "on coule!", point3));
//        alertes.add(new Alerte("innondation", "le gouvernement", "Montréal",
//                "certain", "grave", "innondation", "1978-05-04", "00001", "urgent",
//                "on coule!", point4));
        
//        alertes.add(new Alerte("vent", "Monsieur Dupont", "Montréal",
//                "mettons", "hey", "attache ta tuque", "1979-05-04", "00021",
//                "immédiat","on coule!", polygon));

    }

    public ArrayList<Alerte> getAlertes() {
        return alertes;
    }

    @Override
    public String toString() {
        String result = "{";

        for (Alerte alerte : alertes) {
            result += alerte.toString();
        }
        return result + "}";
    }

}
