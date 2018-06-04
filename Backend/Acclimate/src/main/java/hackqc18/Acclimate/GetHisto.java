package hackqc18.Acclimate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;

@Component
@Deprecated
public class GetHisto {

    private CsvAlerteParser[] parsers;

    /**
     * Autowired Constructor that will automatically be instantiated with
     * all "Bean-like" annotated classes of HistoryCsvParser.
     * @param parsers a list of HistoryCsvParser
     */
    @Autowired
    public GetHisto(CsvAlerteParser[] parsers) {
        this.parsers = parsers;
        for (CsvAlerteParser parser : this.parsers) {
            parser.openAndParse();
        }

    }


    @JsonRawValue
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String alerts(double nord, double sud, double est, double ouest) {

        Alerts theAlerts = new Alerts();
        for (CsvAlerteParser parser : parsers) {
            theAlerts.addAll(nord, sud, est, ouest, parser.getAlertes());
        }

        return theAlerts.toString();
    }

}
