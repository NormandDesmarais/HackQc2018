
package hackqc18.Acclimate.deprecated;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@Deprecated
public class GetHistoController {
    @Autowired
    private GetHisto getHisto;

    @RequestMapping(value="/getHisto",produces="application/json;charset=UTF-8")
    public @ResponseBody
    String getHistoric(
            @RequestParam(value="nord", defaultValue="90") double nord,
            @RequestParam(value="sud", defaultValue="-90") double sud,
            @RequestParam(value="est", defaultValue="180") double est,
            @RequestParam(value="ouest", defaultValue="-180") double ouest) {

        return getHisto.alerts(nord, sud, est, ouest);
    }
}
