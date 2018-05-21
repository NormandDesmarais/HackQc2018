package hackqc18.Acclimate;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This API should be the one delete (old API).
 * GetAlertsController (with alerts in English) should be the one we keep
 * once all clients have migrated to the new API.
 */
@RestController
@RequestMapping("api")
public class GetAlertesController {
    @RequestMapping(value="/alertes",produces="application/json;charset=UTF-8")
    public String getAlerts(
            @RequestParam(value="nord", defaultValue="90") double nord,
            @RequestParam(value="sud", defaultValue="-90") double sud,
            @RequestParam(value="est", defaultValue="180") double est,
            @RequestParam(value="ouest", defaultValue="-180") double ouest) {
        return new GetAlertes().alerts(nord, sud, est, ouest);
    }
}

