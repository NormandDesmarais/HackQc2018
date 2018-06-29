package hackqc18.Acclimate.deprecated;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * This API should be the one we keep (new API).
 * GetAlertesController (with alertes in French) should be deleted
 * once all clients have migrated to the new API.
 */
@RestController
@RequestMapping("api")
@Deprecated
public class GetAlertsController {
    @RequestMapping(value="/getAlerts",produces="application/json;charset=UTF-8")
    @ResponseBody
    public String getAlerts(
            @RequestParam(value="nord", defaultValue="90") double nord,
            @RequestParam(value="sud", defaultValue="-90") double sud,
            @RequestParam(value="est", defaultValue="180") double est,
            @RequestParam(value="ouest", defaultValue="-180") double ouest) {
        return new GetAlertes().alerts(nord, sud, est, ouest);
    }
}
