package hackqc18.Acclimate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;

public class GetHisto {
    private static StaticParser parser = new StaticParser("historique_alertes.csv");

    @JsonRawValue
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String alerts(double nord, double sud, double est, double ouest) {
        Alerts theAlerts = new Alerts(nord, sud, est, ouest, parser.getAlertes());

        return theAlerts.toString();
    }

}
