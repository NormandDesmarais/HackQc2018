package hackqc18.Acclimate;


import java.io.IOException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@Deprecated
public class PutAlertController {
    @RequestMapping(value = "/putAlert")
    public @ResponseBody String putUserAlert(
            @RequestParam(value = "type") String type,
            @RequestParam(value = "lat") String lat,
            @RequestParam(value = "lng") String lng
    ) throws IOException {
        return new PutAlert(type, lat, lng).statusMsg();
    }
}