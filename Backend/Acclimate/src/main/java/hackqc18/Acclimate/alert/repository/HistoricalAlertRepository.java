package hackqc18.Acclimate.alert.repository;

import java.util.Optional;

import org.springframework.stereotype.Component;

import hackqc18.Acclimate.alert.Alert;
import hackqc18.Acclimate.alert.CsvAlertParser;

// TODO: This is a mock repository. Eventually it should
// extends CrudRepository and be declared as an interface... but it will do
// for now...

/**
 * Interface used to define the historical alert repository. This interface will
 * automatically be instantiated as a Singleton by Spring at compile time by
 * inserting the following declaration in classes that needs this repository:
 *
 * @Autowired private HistoricalAlertRepository historicalAlertRepository;
 */
// The @Component informs Spring that this class must be instantiated
// as a Singleton so that only one instance will be available at any time.
@Component
public class HistoricalAlertRepository extends CsvAlertParser
        implements AlertRepository {

    public HistoricalAlertRepository() {
        super("historique_alertes.csv");
        openAndParse();
    }


    /**
     * Method that mocks the findById method of the CrudRepository interface.
     *
     * @param id the id of the alert of interest
     * @return an optional instance that may contains the alert if it exists.
     */
    @Override
    public Optional<Alert> findById(String id) {
        return Optional.ofNullable(alerts.get(id));
    }


    /**
     * Method that mocks the findAll method of the CrudRepository interface.
     *
     * @return an iterable list of alerts
     */
    @Override
    public Iterable<Alert> findAll() {
        return alerts.values();
    }


    @Override
    public boolean existsById(String id) {
        return alerts.containsKey(id);
    }


    @Override
    public long count() {
        return alerts.size();
    }


    @Override
    public void parseContent(String toBeParsed) {
        String[] alertePrg = toBeParsed.split(",");

        // String[] typesAlertes = {"Avalanche", "Feu de brousse", "Feu de
        // forêt",
        // "Géomorphologique (ex. érosion)", "Glace", "Inondation",
        // "Inondation par ruissellement", "Mouvement de terrain", "Onde de
        // tempête",
        // "Orage violent", "Ouragan", "Pluie", "Pluie verglaçante",
        // "Tempête hivernale", "Tornade", "Tremblement de terre",
        // "Vent de tempête"};
        String[] typesAlertes = { "Feu de forêt", "Inondation" };

        String nom = "", territoire = "", certitude = "", severite = "",
                type = "";
        String dateDeMiseAJour = "", urgence = "", description = "",
                alertId = "";
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
                            alertId = createId(type, dateDeMiseAJour, lng, lat);
                            Alert theAlert = new Alert(alertId, nom, source, territoire,
                                    certitude, severite, type, dateDeMiseAJour,
                                    urgence, description, lng, lat);
                            alerts.put(alertId, theAlert);
                            break;
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
