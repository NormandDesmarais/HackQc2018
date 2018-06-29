package hackqc18.Acclimate.deprecated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;

@Deprecated
public class GetAlertes {

    @JsonRawValue
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String alerts(double nord, double sud, double est, double ouest) {
        Alerts theAlerts = new Alerts(nord, sud, est, ouest,
                AlertesFluxRss.getAlertes());

        return theAlerts.toString();
    }

}
