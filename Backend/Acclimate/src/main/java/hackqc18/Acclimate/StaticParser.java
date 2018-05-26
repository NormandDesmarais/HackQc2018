package hackqc18.Acclimate;


public class StaticParser extends HistoryCsvParser {

    public StaticParser(String filename) {
        super(filename);
    }

    public void parseContent(String toBeParsed) {
        String[] alertePrg = toBeParsed.split(",");

//        String[] typesAlertes = {"Avalanche", "Feu de brousse", "Feu de forêt",
//            "Géomorphologique (ex. érosion)", "Glace", "Inondation",
//            "Inondation par ruissellement", "Mouvement de terrain", "Onde de tempête",
//            "Orage violent", "Ouragan", "Pluie", "Pluie verglaçante",
//            "Tempête hivernale", "Tornade", "Tremblement de terre",
//            "Vent de tempête"};
        String[] typesAlertes = {"Feu de forêt", "Inondation"};

        String nom = "", territoire = "", certitude = "", severite = "", type = "";
        String dateDeMiseAJour = "", urgence = "", description = "", IdAlert = "";
        String source = "Ministère de la Sécurité publique du Québec";
        double lng = 0.0, lat = 0.0;

        for (int i = 10; i < alertePrg.length; i++) {
            int j = i % 10;

            switch (j) {
                case 0:
                    String[] temp = alertePrg[i].split("[a-z]+");
                    if (temp.length > 1) {
                        dateDeMiseAJour = temp[1];
                    }
                    break;
                case 1:
                    break;
                case 2:
                    territoire = alertePrg[i];
                    break;
                case 3:
                    lng = Double.parseDouble(alertePrg[i]);
                    break;
                case 4:
                    lat = Double.parseDouble(alertePrg[i]);
                    break;
                case 5:
                    urgence = alertePrg[i];
                    break;
                case 6:
                    certitude = alertePrg[i];
                    break;
                case 7:
                    nom = alertePrg[i];
                    type = getShortType(nom);
                    break;
                case 8:
                    severite = alertePrg[i];
                    break;
                case 9:
                    for (int k = 0; k < typesAlertes.length; k++) {
                        if (nom.equals(typesAlertes[k])) {
                            Alerte theAlert = new Alerte(nom, source, territoire, certitude,
                                    severite, type, dateDeMiseAJour, IdAlert, urgence,
                                    description, lng, lat);
                            alertes.add(theAlert);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private String getShortType(String type) {
        String result;
        switch (type) {
            case "Feu de brousse":
            case "Feu de forêt":
                result = "Feu";
                break;
            case "Inondation":
            case "Inondation par ruissellement":
                result = "Eau";
                break;
            case "Avalanche":
            case "Géomorphologique (ex. érosion)":
            case "Mouvement de terrain":
            case "Tremblement de terre":
                result = "Terrain";
                break;
            case "Glace":
            case "Onde de tempête":
            case "Orage violent":
            case "Ouragan":
            case "Pluie":
            case "Pluie verglaçante":
            case "Tempête hivernale":
            case "Tornade":
            case "Vent de tempête":
                result = "Météo";
                break;
            default:
                result = "Don't care";
        }
        return result;
    }
}
