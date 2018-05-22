package hackqc18.Acclimate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;
import java.util.ArrayList;

public class GetHisto {

    private static GetHisto theInstance = null;
    private static final ArrayList<HistoryCsvParser> PARSERS = new ArrayList<>();

    private GetHisto() {
        StaticParser parser = new StaticParser("historique_alertes.csv");
        parser.openAndParse();
        PARSERS.add(parser);
        
//        VigilanceParser parser2 = new VigilanceParser("vigilance_alertes.csv");
//        parser2.openAndParse();
//        PARSERS.add(parser2);
    }

    public static GetHisto theInstance() {
        if (theInstance == null) {
            theInstance = new GetHisto();
        }
        return theInstance;
    }

    @JsonRawValue
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String alerts(double nord, double sud, double est, double ouest) {

        Alerts theAlerts = new Alerts();
        for (HistoryCsvParser parser : PARSERS) {
            theAlerts.addAll(nord, sud, est, ouest, parser.getAlertes());
        }

        return theAlerts.toString();
    }

}
