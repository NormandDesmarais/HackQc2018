package hackqc18.Acclimate.alert;

import java.net.URI;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import hackqc18.Acclimate.exception.AlertNotFoundException;

// IMPORTANT !!!
// TODO: Make sure that all input validations are done on the server side because
// hacking clients is a peace of cake compare to hacking the server!!!

/**
 * Controller class for user alerts.
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
// class. All requests starting with "{site-URL}/api/user/alerts" will be
// redirected to this class.
@RestController
@RequestMapping("api/{alertType}/alerts")
public class AlertController {

    // The @Autowired annotation informs Spring to instantiate the variable
    // userAlertService with the singleton (unique single instance) instance
    // of the class UserAlertService.
    @Autowired
    private AlertService alertService;


    /**
     * The GET method associated with the URL "api/{alertType}/alerts". It
     * retrieves and returns the collection of user alerts. Optional filter
     * parameters could be provided to limit the number of alerts to a given
     * region.
     *
     * @param north the northern latitude (default: 90)
     * @param south the southern latitude (default: -90)
     * @param east the eastern longitude (default: 180)
     * @param west the western longitude (default: -180)
     * @return a list of alerts in JSON format
     * @throws OperationNotSupportedException
     */
    @GetMapping
    public List<Alert> getAllAlerts(
            @PathVariable String alertType,
            @RequestParam(defaultValue = "90") double north,
            @RequestParam(defaultValue = "-90") double south,
            @RequestParam(defaultValue = "180") double east,
            @RequestParam(defaultValue = "-180") double west) throws OperationNotSupportedException {
        return alertService.findAllAlerts(alertType, north, south, east, west);
    }


    /**
     * The GET method associated with the URL
     * "api/{alertType}/alerts/{alertId}", where alertId is variable. It returns
     * the associated alert if it exists or an empty body otherwise.
     *
     * @param alertId the id of the alert of interest
     * @return the alert or empty if not found
     * @throws OperationNotSupportedException
     * @throws AlertNotFoundException
     */
    @GetMapping("/{alertId}")
    public Alert getAlert(
            @PathVariable String alertType,
            @PathVariable String alertId) throws AlertNotFoundException, OperationNotSupportedException {
        return alertService.findAlertById(alertType, alertId);
    }


    // TODO: add a specific message to distinguish new alerts from confirmation
    // of a previous one. For example:
    //     new alert    : "Nouvelle alerte ajoutée. Merci pour votre aide!"
    //     confirmation : "Alerte comfirmée, merci de votre participation!"
    // This implies that either the Alert class must be updated to add a
    // status message or it is wrapped inside another class that contains
    // the message. This last implementation would be better and could be
    // used/extended to provide better status messages for all requests.
    /**
     * The POST method associated with the URL "api/{alertType}/alerts". It
     * retrieves the alert type, longitude and latitude from the request body
     * and create a new alert. If a similar alert exist within a 1 KM square,
     * then it increases the count of the existing alert and returns it.
     *
     * @param alertStub a simple class containing the alert type, longitude and
     *        latitude
     * @return a newly created alert or an existing one
     * @throws OperationNotSupportedException
     */
    @PostMapping
    public ResponseEntity<Alert> addAlert(
            @PathVariable String alertType,
            @RequestBody AlertStub alertStub) throws OperationNotSupportedException {
        Alert alert = alertService.createAlert(alertType, alertStub);
        if (alert.getCount() == 1) { // new alert
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{alertId}").buildAndExpand(alert.getId()).toUri();
            return ResponseEntity.created(location).body(alert);
        } else { // added +1 to the count of an existing alert
            return ResponseEntity.ok(alert);
        }
    }


    // NOTE: the AlertStub is not used so far, but this could change in
    // the future.
    //
    // NOTE: Strictly speaking, this method doesn't respect the HTTP
    // idempotent rule of a PUT method since, when applied more than once,
    // it changes the state of the alert. But this will be addressed
    // when we will manage users by making sure that a user can only
    // confirm once the same alert.
    //
    // NOTE: We use the PATCH method instead of the PUT because PATCH
    // is when a subset of the resource is changed (e.g. count and
    // possibly "certitude" in our case). While PUT is when the existing
    // resource is replaced by the one submitted. Since the client only
    // sends AlertStubs, they can't replace an existing, thus we default
    // to PATCH in order to respect RESTfull API.
    /**
     * The PATCH method associated with the URL
     * "api/{alertType}/alerts/{alertId}", where alertId is variable. If the
     * alert with the corresponding alertId exists, it increases its count and
     * adjust the "certitude" status accordingly. The modified alert is returned
     * or an empty body if the alert was not found.
     *
     * @param alertId the id of the alert of interest
     * @param alertStub a simple class containing the alert type, longitude and
     *        latitude
     * @return the modified alert or an empty body if the alert was not found
     * @throws OperationNotSupportedException
     */
    @PatchMapping("/{alertId}")
    public Alert updateAlert(
            @PathVariable String alertType,
            @PathVariable String alertId,
            @RequestBody AlertStub alertStub) throws OperationNotSupportedException {
        return alertService.updateAlert(alertType, alertId, alertStub);
    }


    // NOTE: we might need to deactivate this request for security.
    // In case a hacker founds it and destroy our database just for fun.
    // Or we should only allow it for users with admin privileges.
    /**
     * The DELETE method associated with the URL
     * "api/{alertType}/alerts/{alertId}", where alertId is variable. It deletes
     * the corresponding alert from the database if it is found.
     *
     * @param alertId the id of the alert of interest
     * @return the deleted alert or an empty body if it wasn't found
     * @throws OperationNotSupportedException
     */
    @DeleteMapping("/{alertId}")
    public Alert removeAlert(
            @PathVariable String alertType,
            @PathVariable String alertId) throws OperationNotSupportedException {
        return alertService.deleteAlert(alertType, alertId);
    }
}
