package hackqc18.Acclimate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;


public class GetUserAlerts {

    @JsonRawValue
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String alerts(double nord, double sud, double est, double ouest) {
        Alerts theAlerts = new Alerts(nord, sud, est, ouest,
                PutAlert.getUserAlerts());
        
        return theAlerts.toString();
    }

}