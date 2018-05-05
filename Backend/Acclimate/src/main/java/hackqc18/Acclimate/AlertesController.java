package hackqc18.Acclimate;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AlertesController {
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping(value="/alertes",produces="application/json")
    public String alertes(
            @RequestParam(value="lat", defaultValue="0.") double latitude,
            @RequestParam(value="lng", defaultValue="0.") double longitude) {
        return new Alertes(latitude, longitude).toString();
    }
}

