package hackqc18.Acclimate.alert.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import hackqc18.Acclimate.alert.Alert;
import hackqc18.Acclimate.alert.AlertStub;
import hackqc18.Acclimate.exception.AlertNotFoundException;

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
public class UserAlertService {

    // With this @Autowired annotation, Spring does a little bit of magic
    // by creating on the fly a concrete instance of the UserAlertRepository
    // interface. Springs knows exactly how to do this because:
    // a) database implementations always come to the same four CRUD
    // methods (Create, Retrieve, Update and Delete), and
    // b) we've been careful to respect JPA (Java Persistence API) "rules"
    // when constructing the related Entity class (Alert). Namely,
    // amongst other things:
    // all getters and setters are defined, there is a no-arg
    // constructor, and we used the proper JPA annotations to identify
    // the class and its id.
    @Autowired
    private UserAlertRepository userAlertRepository;

    // Alert Expiration Delay: user alerts with a time stamp difference
    // older than the expiration will be deleted from the database
    private static int alertExpirationDays = 0;
    private static int alertExpirationHrs = 0;
    private static int alertExpirationMin = 10;

    // observed and probable "certitude" count are triggers used
    // to update the "certitude" status accordingly
    private static int observedCertitudeCount = 10;
    private static int probableCertitudeCount = 5;

    // repetition delay of the database cleaning task
    private final static int scheduledDbCleaningMinutes = 10;


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
    public List<Alert> getAllAlerts(double north, double south, double east,
            double west) {
        List<Alert> alerts = new ArrayList<>();
        userAlertRepository.findAll().forEach(alert -> {
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
     */
    public Alert getAlert(String id) throws AlertNotFoundException {
        Alert alert = findAlert(id);
        if (alert == null) {
            throw new AlertNotFoundException(id);
        }
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
     */
    public Alert addAlert(AlertStub stub) {
        String alertId = createId(stub);
        Alert alert = findAlert(alertId);
        if (alert == null) {
            alert = new Alert(alertId, stub.getType(), stub.getLng(),
                    stub.getLat());
        } else {
            increaseAlertCount(alert);
        }
        return userAlertRepository.save(alert);
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
     */
    public Alert updateAlert(String id, AlertStub stub) {
        Alert alert = findAlert(id);
        if (alert == null) {
            throw new AlertNotFoundException(id);
        }
        increaseAlertCount(alert);
        alert = userAlertRepository.save(alert);
        return alert;
    }


    /**
     * Deletes the user alert with the providing id from the database. Does
     * nothing if there is no alert with this id.
     *
     * @param id the alert id
     * @return the deleted alert or null if it doesn't exist
     */
    public Alert removeAlert(String id) {
        Alert alert = findAlert(id);
        if (alert == null) {
            throw new AlertNotFoundException(id);
        }
        userAlertRepository.deleteById(id);
        return alert;
    }


    /**
     * Utility method that fetches and returns the alert with the corresponding
     * id, or null if it cannot be found.
     *
     * @param id the alert id
     * @return an alert or null
     */
    private Alert findAlert(String id) {
        return userAlertRepository.findById(id).orElse(null);
    }


    /**
     * Utility method that creates a unique id from the alert type, longitude
     * and latitude. Alerts of the same type located within a 1 KM square will
     * share the same id and will thus be identified has the same alert.
     *
     * @param stub
     * @return
     */
    private String createId(AlertStub stub) {
        return stub.getType().hashCode() + "-"
                + distanceFromZeroInKM(stub.getLng()) + "-"
                + distanceFromZeroInKM(stub.getLat());
    }


    /**
     * Utility method that computes the distance in KM of a longitude or
     * latitude coordinate from the zero value of this coordinate.
     *
     * @param coordinate the longitude or latitude coordinate
     * @return the distance in km from the zero value of this coordinate
     */
    private int distanceFromZeroInKM(double coordinate) {
        double R = 6378.137; // Radius of earth in KM
        double a = Math.abs(Math.sin(coordinate * Math.PI / 180 / 2));
        double a2 = a * a;
        double d = 2 * R * Math.atan2(a, Math.sqrt(1 - a2));
        return (int) Math.round(d); // KM
    }


    /**
     * Utility method that increases the count of an alert and changes its
     * "certitude" status accordingly. It also updates the "dateDeMiseAJour"
     * property to the current date and time.
     *
     * @param alert the alert
     */
    private void increaseAlertCount(Alert alert) {
        alert.setCount(alert.getCount() + 1);
        alert.setDateDeMiseAJour(LocalDateTime.now().toString());
        if (alert.getCount() == observedCertitudeCount) {
            alert.setCertitude("Observé");
        } else if (alert.getCount() == probableCertitudeCount) {
            alert.setCertitude("Probable");
        }
    }


    /**
     * Utility method that runs in the background (thanks to the
     *
     * @Schedule annotation) to clean the database of expired alerts.
     */
    // The @Scheduled annotation informs Spring to create a
    // task with the annotated method and to run it in a separate
    // thread at the given fixed rate. @Schedule only works if the
    // @EnableScheduling annotation has been set in the application class.
    @Scheduled(fixedRate = 1000 * scheduledDbCleaningMinutes)
    private void scheduledRepositoryCleaningTask() {
        userAlertRepository.findAll().forEach(alert -> {
            if (hasExpired(alert.getDateDeMiseAJour())) {
                userAlertRepository.delete(alert);
            }
        });
    }


    /**
     * Utility method that compares the provided date with the current date and
     * returns true if the difference is greater than the expired alert time
     * delay.
     *
     * @param date the date in the format AAAA-MM-JJTHH:MM:SS
     * @return true if the difference between the provided date and now is
     *         greater than the alert expiration delay
     */
    private boolean hasExpired(String date) {
        LocalDateTime d = LocalDateTime.parse(date);
        LocalDateTime now = LocalDateTime.now();

        int dDays = now.getDayOfYear() - d.getDayOfYear() - alertExpirationDays;
        int dHours = now.getHour() - d.getHour() - alertExpirationHrs;
        int dMin = now.getMinute() - d.getMinute() - alertExpirationMin;
        return (dDays > 0
                || (dDays == 0 && (dHours > 0 || (dHours == 0 && dMin > 0))));
    }
}
