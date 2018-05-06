package hackqc18.Acclimate;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

@RestController
@RequestMapping("api")
public class PutAlertController {
    @RequestMapping(value = "/putAlert")
    public @ResponseBody String putAlertController(
            @RequestParam(value = "type") String type,
            @RequestParam(value = "lat") double lat,
            @RequestParam(value = "lng") double lng,
            @RequestParam(value = "date") String date
    ) throws IOException {
        return new PutAlert(type, lat, lng, date).isStatus();
    }
}