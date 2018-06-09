/*
 * This class should be removed once clients support JSON alerts
 * without the tag "alert"
 */
package hackqc18.Acclimate.deprecated;

@Deprecated
public class AlerteWrapper {
    private final Alerte alerte;

    public AlerteWrapper(Alerte alerte) {
        this.alerte = alerte;
    }

    public Alerte getAlerte() {
        return alerte;
    }


}
