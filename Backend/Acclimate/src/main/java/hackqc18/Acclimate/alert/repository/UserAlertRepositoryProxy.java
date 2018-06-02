package hackqc18.Acclimate.alert.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.naming.OperationNotSupportedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hackqc18.Acclimate.alert.Alert;

/**
 * User alert repository proxy which forwards requests to the real
 * userAlertRepository instance. This proxy is needed to have a class that
 * implement our AlertRepository interface.
 */
//The @Component informs Spring that this class must be instantiated
//as a Singleton so that only one instance will be available at any time.
@Component
public class UserAlertRepositoryProxy implements AlertRepository {

    // Alert Expiration Delay: user alerts with a time stamp difference
    // greater than the expiration will be deleted from the database
    private static final int ALERT_EXPIRATION_DAYS = 0;
    private static final int ALERT_EXPIRATION_HRS  = 0;
    private static final int ALERT_EXPIRATION_MIN  = 10;

    // repetition delay of the database cleaning task
    private static final int SCHEDULED_DB_CLEANING_DELAY = 10;

    // With this @Autowired annotation, Spring does a little bit of magic
    // by creating on the fly a concrete instance of the UserAlertRepository
    // interface. Springs knows exactly how to do this because:
    //
    // a) database implementations always come to the same four CRUD
    // methods (Create, Retrieve, Update and Delete), and
    //
    // b) we've been careful to respect JPA (Java Persistence API) "rules"
    // when constructing the related Entity class (Alert). Namely,
    // amongst other things:
    //
    // all getters and setters are defined, there is a no-arg
    // constructor, and we used the proper JPA annotations to identify
    // the class and its id.
    @Autowired
    private UserAlertRepository userAlertRepository;


    @Override
    public Optional<Alert> findById(String id) {
        return userAlertRepository.findById(id);
    }


    @Override
    public boolean existsById(String id) {
        return userAlertRepository.existsById(id);
    }


    @Override
    public Iterable<Alert> findAll() {
        return userAlertRepository.findAll();
    }


    @Override
    public long count() {
        return userAlertRepository.count();
    }


    @Override
    public void saveSupportedOrThrow(Alert alert)
            throws OperationNotSupportedException {
        // save supported
    }


    @Override
    public void updateSupportedOrThrow(Alert alert)
            throws OperationNotSupportedException {
        // update supported
    }


    @Override
    public Alert save(Alert alert) throws OperationNotSupportedException {
        return userAlertRepository.save(alert);
    }


    @Override
    public void deleteByIdSupportedOrThrow(String id)
            throws OperationNotSupportedException {
        // deleteById supported
    }


    @Override
    public void deleteById(String id) throws OperationNotSupportedException {
        userAlertRepository.deleteById(id);
    }


    /**
     * Utility method that runs in the background (thanks to the
     * @Schedule annotation) to clean the database of expired alerts.
     *
     * @throws OperationNotSupportedException
     */
    // The @Scheduled annotation informs Spring to create a
    // task with the annotated method and to run it in a separate
    // thread at the given fixed rate. @Schedule only works if the
    //
    // @EnableScheduling annotation has been set in the application class.
    @Scheduled(fixedRate = 1000 * SCHEDULED_DB_CLEANING_DELAY)
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

        int dDays
                = now.getDayOfYear() - d.getDayOfYear() - ALERT_EXPIRATION_DAYS;
        int dHours = now.getHour() - d.getHour() - ALERT_EXPIRATION_HRS;
        int dMin = now.getMinute() - d.getMinute() - ALERT_EXPIRATION_MIN;
        return (dDays > 0
                || (dDays == 0 && (dHours > 0 || (dHours == 0 && dMin > 0))));
    }
}
