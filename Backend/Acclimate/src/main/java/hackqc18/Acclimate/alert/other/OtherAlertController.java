package hackqc18.Acclimate.alert.other;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hackqc18.Acclimate.alert.Alert;

/**
 * Controller class for "official"/other alerts.
 */
// The @RestController annotation informs Spring that this class is
// a REST controller. In the background, Spring does all the magic
// to redirect related HTTP requests to this class.
//
// @RestController also invokes @ResponseBody for the class.
// The @ResponseBody annotation informs Spring that the object returned
// by methods must be translated into JSON format. Optionally,
// we could also support XML format if need be, both at the same time
// (the request header "Content-Type" would indicate whether to
// use "application/json" or "text/xml").
//
// The @RequestMapping annotation defines the base URL managed by this
// class. All requests starting with "{site-URL}/api/other/alerts" will be
// redirected to this class.
@RestController
@RequestMapping("api/other/alerts")
public class OtherAlertController {

    // The @Autowired annotation informs Spring to instantiate the variable
    // userAlertService with the singleton (unique single instance) instance
    // of the class UserAlertService.
    @Autowired
    private OtherAlertService otherAlertService;
    
    
    // TODO - add the right response HTTP status
    //
    /**
     * The GET method associated with the URL "api/other/alerts". It
     * retrieves and returns the collection of user alerts. Optional filter
     * parameters could be provided to limit the number of alerts to a 
     * given region.
     * @param north the northern latitude (default: 90)
     * @param south the southern latitude (default: -90)
     * @param east the eastern longitude (default: 180)
     * @param west the western longitude (default: -180)
     * @return a list of alerts in JSON format
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Alert> getAllAlerts(
                    @RequestParam(value="north", defaultValue="90") double north,
                    @RequestParam(value="south", defaultValue="-90") double south,
                    @RequestParam(value="east", defaultValue="180") double east,
                    @RequestParam(value="west", defaultValue="-180") double west) {
        return otherAlertService.getAllAlerts(north, south, east, west);
    }
    
    
    // TODO - add the right response HTTP status
    /**
     * The GET method associated with the URL "api/other/alerts/{alertId}",
     * where alertId is variable. It returns the associated alert if it
     * exists or an empty body otherwise.
     * @param alertId the id of the alert of interest
     * @return the alert or empty if not found
     */
    @RequestMapping(method = RequestMethod.GET, value="/{alertId}")
    public Alert getAlert(@PathVariable String alertId) {
        return otherAlertService.getAlert(alertId);
    }
    
}
