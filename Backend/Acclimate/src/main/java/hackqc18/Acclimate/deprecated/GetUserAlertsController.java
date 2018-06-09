package hackqc18.Acclimate.deprecated;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api")
@Deprecated
public class GetUserAlertsController {
    @RequestMapping(value="/getUserAlerts",produces="application/json;charset=UTF-8")
    public @ResponseBody
    String getUserAlerts(
            @RequestParam(value="nord", defaultValue="90") double nord,
            @RequestParam(value="sud", defaultValue="-90") double sud,
            @RequestParam(value="est", defaultValue="180") double est,
            @RequestParam(value="ouest", defaultValue="-180") double ouest) {

        return new GetUserAlerts().alerts(nord, sud, est, ouest);
    }
}