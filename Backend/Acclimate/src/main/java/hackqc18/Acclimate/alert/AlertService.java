package hackqc18.Acclimate.alert;

import java.util.ArrayList;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hackqc18.Acclimate.alert.repository.AlertRepository;
import hackqc18.Acclimate.alert.repository.HistoricalAlertRepository;
import hackqc18.Acclimate.alert.repository.OtherAlertRepository;
import hackqc18.Acclimate.alert.repository.UserAlertRepositoryProxy;
import hackqc18.Acclimate.exception.AlertNotFoundException;
import hackqc18.Acclimate.exception.UnsupportedAlertTypeException;

/**
 * Service class for User Alerts. This is the class that implements all the
 * dirty works to answer requests from the controller and connect them with the
 * model (Alert).
 */
// The @Service annotation informs Spring that this class must be treated
// as a Service class. In the background, Spring makes sure that this
// class is a Singleton (only has one single instance) and makes it possible
// to retrieve it with an @Autowired annotation (see the @Autowired in
// the UserAlertController class).
@Service
public class AlertService {

    // These @Autowired creates a singleton instance of the three following
    // repositories classes.

    @Autowired
    private UserAlertRepositoryProxy  userAlertRepository;
    @Autowired
    private OtherAlertRepository      otherAlertRepository;
    @Autowired
    private HistoricalAlertRepository historicalAlertRepository;


    // TODO: call a method that would aggregate alerts depending
    // on the zooming size. It thus imply to update the Alert class
    // to include the number of alerts aggregated under this
    // alert umbrella and to automatically calculate the average
    // coordinate weighted by the alert "certitude", "severity" and
    // "urgence" as well as maybe its "dateDeMiseAJour".
    /**
     * Returns the list of alerts bounded by the north, south, east and west
     * coordinates.
     *
     * @param north the northern latitude (max: 90)
     * @param south the southern latitude (min: -90)
     * @param east the eastern longitude (max: 180)
     * @param west the western longitude (min: -180)
     * @return the list of alerts
     */
    public List<Alert> findAllAlerts(String alertType, double north,
            double south, double east, double west) {

        List<Alert> alerts = new ArrayList<>();
        AlertRepository repository = selectRepository(alertType);
        repository.findAll().forEach(alert -> {
            if (alert.overlapWithBox(north, south, east, west)) {
                alerts.add(alert);
            }
        });

        return alerts;
    }


    /**
     * Returns the alert for the provided id.
     *
     * @param id the id that uniquely identify the alert
     * @return the alert
     * @throws AlertNotFoundException, OperationNotSupportedException
     */
    public Alert findAlertById(String alertType, String id)
            throws AlertNotFoundException, OperationNotSupportedException {

        AlertRepository repository = selectRepository(alertType);
        Alert alert = findAlertOrThrow(repository, id);
        return alert;
    }


    /**
     * Construct a user alert from the AlertStub provided. If the alert already
     * exists, it increases the "count" number of this alert and possibly change
     * is "certitude" status accordingly.
     *
     * @param stub an object containing the alert type, longitude and latitude
     *        coordinates
     * @return the newly created or modified alert
     * @throws OperationNotSupportedException
     */
    public Alert createAlert(String alertType, AlertStub stub)
            throws OperationNotSupportedException {

        AlertRepository repository = selectRepository(alertType);
        repository.saveSupportedOrThrow(null);

        String alertId = stub.createId();
        Alert alert = findAlertOrNull(repository, alertId);
        if (alert == null) {
            alert = new Alert(alertId, stub.getType(), stub.getLng(),
                    stub.getLat());
        } else {
            alert.increaseCount();
        }
        return repository.save(alert);
    }


    // NOTE: the AlertStub is not used so far, but this could change in
    // the future.
    // NOTE: Strictly speaking, this method doesn't respect the HTTP
    // idempotent rule of a PUT method since, when applied more than once,
    // it changes the state of the alert. But this will be addressed
    // when we will manage users by making sure that a user can only
    // confirm once the same alert.
    /**
     * Increases the count of the alert referred by the provided id and adjust
     * the "certitude" status accordingly. It returns null if no alert with this
     * id was found.
     *
     * @param id the id of the alert to be updated
     * @param stub an object containing the alert type, longitude and latitude
     *        coordinates
     * @return the updated alert
     * @throws OperationNotSupportedException
     */
    public Alert updateAlert(String alertType, String id, AlertStub stub)
            throws OperationNotSupportedException {

        AlertRepository repository = selectRepository(alertType);
        repository.updateSupportedOrThrow(null);

        Alert alert = findAlertOrThrow(repository, id);
        alert.increaseCount();
        alert = repository.save(alert);
        return alert;
    }


    /**
     * Deletes the user alert with the providing id from the database. Does
     * nothing if there is no alert with this id.
     *
     * @param id the alert id
     * @return the deleted alert or null if it doesn't exist
     * @throws OperationNotSupportedException
     */
    public Alert deleteAlert(String alertType, String id)
            throws OperationNotSupportedException {

        AlertRepository repository = selectRepository(alertType);
        repository.deleteByIdSupportedOrThrow(id);

        Alert alert = findAlertOrThrow(repository, id);
        userAlertRepository.deleteById(id);
        return alert;
    }


    /**
     * Utility method which selects the right repository based on the alert
     * type.
     *
     * @param alertType the alert type ("user", "other" or "historical")
     * @return the corresponding alert repository
     */
    private AlertRepository selectRepository(String alertType) {

        switch (alertType) {
        case "user":
            return userAlertRepository;
        case "other":
            return otherAlertRepository;
        case "historical":
            return historicalAlertRepository;
        default:
            throw new UnsupportedAlertTypeException(alertType);
        }
    }


    /**
     * Utility method which fetches and returns the alert with the corresponding
     * id, or null if it cannot be found.
     *
     * @param id the alert id
     * @return an alert or null
     */
    private Alert findAlertOrNull(AlertRepository repository, String id) {
        return repository.findById(id).orElse(null);
    }


    /**
     * Utility method which fetches and returns the alert with the corresponding
     * id, or throws an AlertNotFoundException if it cannot be found.
     *
     * @param id the alert id
     * @return an alert or null
     * @throws AlertNotFoundException
     */
    private Alert findAlertOrThrow(AlertRepository repository, String id) {
        return repository.findById(id)
                .orElseThrow(() -> new AlertNotFoundException(id));
    }

}
