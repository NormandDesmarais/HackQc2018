package hackqc18.Acclimate;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AlertesController {
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/alertes")
    public Alertes alertes(
            @RequestParam(value="longitude", defaultValue="0.") double longitude,
            @RequestParam(value="latitude", defaultValue="0.") double latitude) {
        return new Alertes(longitude, latitude);
    }
}

