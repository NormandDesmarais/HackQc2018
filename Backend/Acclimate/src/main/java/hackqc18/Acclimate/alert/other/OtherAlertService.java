package hackqc18.Acclimate.alert.other;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hackqc18.Acclimate.alert.Alert;
import hackqc18.Acclimate.exception.AlertNotFoundException;

@Service
public class OtherAlertService {

    // The @Autowired annotation informs Spring to instantiate the variable
    // userAlertService with the singleton (unique single instance) instance
    // of the class UserAlertService.
    @Autowired
    private OtherAlertRepository otherAlertRepository;


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
        otherAlertRepository.findAll().forEach(alert -> {
            if (alert.overlapWithBox(north, south, east, west)) {
                alerts.add(alert);
            }
        });

        return alerts;
    }


    /**
     * Returns the alert for the provided id, null if no alert with this id was
     * found.
     *
     * @param id the id that uniquely identify the alert
     * @return the alert
     */
    public Alert getAlert(String id) {
        Alert alert = findAlert(id);
        if (alert == null) {
            throw new AlertNotFoundException(id);
        }
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
        return otherAlertRepository.findById(id).orElse(null);
    }

}
