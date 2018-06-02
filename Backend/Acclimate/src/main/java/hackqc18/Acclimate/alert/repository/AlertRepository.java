package hackqc18.Acclimate.alert.repository;

import java.util.Optional;

import javax.naming.OperationNotSupportedException;

import hackqc18.Acclimate.alert.Alert;

public interface AlertRepository {

    /**
     * Throw an exception if updating an alert is not supported. This method is
     * useful to avoid doing preparatory works in view of a save operation for
     * an update. Default implementation is to throw an exception. Classes that
     * implements this interface and supports updates should override this
     * method with an empty body.
     *
     * @param alert this field could be null, it is only there to clearly
     *        identify the signature of the save method to be called.
     * @throws OperationNotSupportedException
     */
    default public void updateSupportedOrThrow(Alert alert)
            throws OperationNotSupportedException {
        throw new OperationNotSupportedException(
                "Mise à jour non supportée pour ce type d'alerte.");
    }


    /**
     * Throw an exception if saving an alert is not supported. This method is
     * useful to avoid doing preparatory works in view of a save operation.
     * Default implementation is to throw an exception. Classes that implements
     * this interface and supports save should override this method with an
     * empty body.
     *
     * @param alert this field could be null, it is only there to clearly
     *        identify the signature of the save method to be called.
     * @throws OperationNotSupportedException
     */
    default public void saveSupportedOrThrow(Alert alert)
            throws OperationNotSupportedException {
        throw new OperationNotSupportedException(
                "Sauvegarde non supportée pour ce type d'alerte.");
    }


    /**
     * Save the alert in the database or throw an OperationNotSupportedException
     * if this action is not supported. Default implementation is to throw an
     * exception.
     *
     * @param alert the alert to save
     * @return the saved alert
     * @throws OperationNotSupportedException
     */
    default public Alert save(Alert alert)
            throws OperationNotSupportedException {
        throw new OperationNotSupportedException(
                "Sauvegarde non supportée pour ce type d'alerte.");
    }


    /**
     * Throw an exception if saving a collection of alerts is not supported.
     * This method is useful to avoid doing preparatory works in view of a save
     * operation. Default implementation is to throw an exception. Classes that
     * implements this interface and supports saveAll should override this
     * method with an empty body.
     *
     * @param alerts this field could be null, it is only there to clearly
     *        identify the signature of the saveAll method to be called.
     * @throws OperationNotSupportedException
     */
    default public void saveAllSupportedOrThrow(Iterable<Alert> alerts)
            throws OperationNotSupportedException {
        throw new OperationNotSupportedException(
                "Sauvegarde d'une collection non supportée pour ce type d'alerte.");
    }


    /**
     * Save all alerts passed in parameters. Deafult implementation is to throw
     * an OperationNotSupportedException.
     *
     * @param alerts an iterable list of alerts
     * @return the alerts saved
     * @throws OperationNotSupportedException
     */
    default public Iterable<Alert> saveAll(Iterable<Alert> alerts)
            throws OperationNotSupportedException {
        throw new OperationNotSupportedException(
                "Sauvegarde d'une collection non supportée pour ce type d'alerte.");
    }


    public Optional<Alert> findById(String id);


    public boolean existsById(String id);


    public Iterable<Alert> findAll();


    default public void findAllByIdSupportedOrThrow(Iterable<String> ids)
            throws OperationNotSupportedException {
        throw new OperationNotSupportedException(
                "Recherche d'un sous ensemble non supportée pour ce type d'alerte.");
    }


    default public Iterable<Alert> findAllById(Iterable<String> ids)
            throws OperationNotSupportedException {
        throw new OperationNotSupportedException(
                "Recherche d'un sous ensemble non supportée pour ce type d'alerte.");
    }


    public long count();


    default public void deleteByIdSupportedOrThrow(String id)
            throws OperationNotSupportedException {
        throw new OperationNotSupportedException(
                "Destruction par id non supportée pour ce type d'alerte (id : '"
                        + id + "')");
    }


    default public void deleteById(String id)
            throws OperationNotSupportedException {
        throw new OperationNotSupportedException(
                "Destruction par id non supportée pour ce type d'alerte (id : '"
                        + id + "')");
    }


    default public void deleteSupportedOrThrow(Alert alert)
            throws OperationNotSupportedException {
        throw new OperationNotSupportedException(
                "Destruction non supportée pour ce type d'alerte.");
    }


    default public void delete(Alert alert)
            throws OperationNotSupportedException {
        throw new OperationNotSupportedException(
                "Destruction non supportée pour ce type d'alerte.");
    }


    default public void deleteAllSupportedOrThrow(Iterable<Alert> alerts)
            throws OperationNotSupportedException {
        throw new OperationNotSupportedException(
                "Destruction d'un sous-ensemble non supportée pour ce type d'alerte.");
    }


    default public void deleteAll(Iterable<Alert> alerts)
            throws OperationNotSupportedException {
        throw new OperationNotSupportedException(
                "Destruction d'un sous-ensemble non supportée pour ce type d'alerte.");
    }


    default public void deleteAllSupportedOrThrow()
            throws OperationNotSupportedException {
        throw new OperationNotSupportedException(
                "Destruction complète non supportée pour ce type d'alerte.");
    }


    default public void deleteAll() throws OperationNotSupportedException {
        throw new OperationNotSupportedException(
                "Destruction complète non supportée pour ce type d'alerte.");
    }

}
