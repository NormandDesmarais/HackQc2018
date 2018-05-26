package hackqc18.Acclimate.alert.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import hackqc18.Acclimate.alert.Alert;
import hackqc18.Acclimate.alert.AlertStub;

/**
 * Controller class for user alerts.
 */
// The @RestController annotation informs Spring that this class is
// a REST controller. In the background, Spring does all the magic
// to redirect related HTTP requests to this class.
// The @RequestMapping annotation defines the base URL managed by this
// class. All requests starting with "{site-URL}/api/user/alerts" will be
// redirected to this class.
@RestController
@RequestMapping("api/user/alerts")
public class UserAlertController {

    // The @Autowired annotation informs Spring to instantiate the variable
    // userAlertService with the singleton (unique single instance) instance
    // of the class UserAlertService.
    @Autowired
    private UserAlertService userAlertService;
    
    
    // TODO - add the right response HTTP status
    //
    // The @ResponseBody annotation informs Spring that the object returned
    // by the method must be translated into JSON format. Optionally,
    // we could also support XML format if need be, both at the same time
    // time (the request header "Content-Type" would indicate whether to
    // use "application/json" or "text/xml").
    /**
     * The GET method associated with the URL "api/user/alerts". It
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
    @ResponseBody
    public List<Alert> getAllAlerts(
                    @RequestParam(value="north", defaultValue="90") double north,
                    @RequestParam(value="south", defaultValue="-90") double south,
                    @RequestParam(value="east", defaultValue="180") double east,
                    @RequestParam(value="west", defaultValue="-180") double west) {
        return userAlertService.getAllAlerts(north, south, east, west);
    }
    
    
    // TODO - add the right response HTTP status
    /**
     * The GET method associated with the URL "api/user/alerts/{alertId}",
     * where alertId is variable. It returns the associated alert if it
     * exists or an empty body otherwise.
     * @param alertId the id of the alert of interest
     * @return the alert or empty if not found
     */
    @RequestMapping(method = RequestMethod.GET, value="/{alertId}")
    @ResponseBody
    public Alert getAlert(@PathVariable String alertId) {
        return userAlertService.getAlert(alertId);
    }
    
    
    // TODO - add the right response HTTP status
    /**
     * The POST method associated with the URL "api/user/alerts". It
     * retrieves the alert type, longitude and latitude from the request
     * body and create a new alert. If a similar alert exist within a
     * 1 KM square, then it increases the count of the existing alert
     * and returns it.
     * @param alertStub a simple class containing the alert type,
     * longitude and latitude
     * @return a newly created alert or an existing one
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Alert addAlert(@RequestBody AlertStub alertStub) {
        return userAlertService.addAlert(alertStub);
    }
    
    
    // TODO - add the right response HTTP status
    // NOTE: the AlertStub is not used so far, but this could change in
    // the future.
    // NOTE: Strictly speaking, this method doesn't respect the HTTP
    // idempotent rule of a PUT method since, when applied more than once,
    // it changes the state of the alert. But this will be addressed
    // when we will manage users by making sure that a user can only
    // confirm once the same alert.
    /**
     * The PUT method associated with the URL "api/user/alerts/{alertId}",
     * where alertId is variable. If the alert with the corresponding
     * alertId exists, it increases its count and adjust the "certitude"
     * status accordingly. The modified alert is returned or an empty
     * body if the alert was not found.
     * @param alertId the id of the alert of interest
     * @param alertStub a simple class containing the alert type,
     * longitude and latitude
     * @return the modified alert or an empty body if the alert was
     * not found
     */
    @RequestMapping(method = RequestMethod.PUT, value="/{alertId}")
    @ResponseBody
    public Alert updateAlert(
                    @PathVariable String alertId,
                    @RequestBody AlertStub alertStub) {
        return userAlertService.updateAlert(alertId, alertStub);
    }
    
    
    // TODO - add the right response HTTP status
    // NOTE: we might need to deactivate this request for security.
    // In case a hacker founds it and destroy our database just for fun.
    // Or we should only allow it for users with admin privileges.
    /**
     * The DELETE method associated with the URL "api/user/alerts/{alertId}",
     * where alertId is variable. It deletes the corresponding alert
     * from the database if it is found.
     * @param alertId the id of the alert of interest
     * @return the deleted alert or an empty body if it wasn't found
     */
    @RequestMapping(method = RequestMethod.DELETE, value="/{alertId}")
    @ResponseBody
    public Alert removeAlert(@PathVariable String alertId) {
        return userAlertService.removeAlert(alertId);
    }
}
