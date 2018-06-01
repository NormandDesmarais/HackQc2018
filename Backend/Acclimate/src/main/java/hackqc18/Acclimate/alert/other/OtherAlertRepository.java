package hackqc18.Acclimate.alert.other;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import hackqc18.Acclimate.alert.Alert;
import hackqc18.Acclimate.alert.other.rss.ItemRSS;
import hackqc18.Acclimate.alert.other.rss.Rss;

// TODO: This is a mock repository. Eventually it should
// extends CrudRepository and be declared as an interface... but it will do
// for now...

/**
 * Interface used to define the other alert repository. This interface will
 * automatically be instantiated as a Singleton by Spring at compile time by
 * inserting the following declaration in classes that needs this repository:
 *
 * @Autowired private OtherAlertRepository otherAlertRepository;
 */
// The @Component informs Spring that this class must be instantiated
// as a Singleton so that only one instance will be available at any time.
@Component
public class OtherAlertRepository {

    private static HashMap<String, Alert> alerts = new HashMap<>();
    private final int fetchingDelay = 180; // minutes
    private final String rssURL = "https://geoegl.msp.gouv.qc.ca/avp/rss/";
    private final XmlMapper xmlMapper = new XmlMapper();


    /**
     * Method that mocks the findById method of the CrudRepository interface.
     *
     * @param id the id of the alert of interest
     * @return an optional instance that may contains the alert if it exists.
     */
    public Optional<Alert> findById(String id) {
        return Optional.ofNullable(alerts.get(id));
    }


    /**
     * Method that mocks the findAll method of the CrudRepository interface.
     *
     * @return a iterable list of alerts
     */
    public Iterable<Alert> findAll() {
        return alerts.values();
    }


    /**
     * Utility method used to fetch alerts from the live RSS stream at periodic
     * intervals.
     */
    // The @Scheduled annotation informs Spring to create a
    // task with the annotated method and to run it in a separate
    // thread at the given fixed rate. @Schedule only works if the
    // @EnableScheduling annotation has been set in the application class.
    @Scheduled(fixedRate = 1000 * fetchingDelay)
    private void updateAlertsFromRssFeedTask() {

        String feed = getRssFeed();
        parseFeed(feed);

    }


    /**
     * Utility method that actually fetch the RSS feed.
     *
     * @return the feed in XML format
     */
    private String getRssFeed() {
        try {
            String feed = "";
            URL url = new URL(rssURL);
            URLConnection rssSrc = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    rssSrc.getInputStream(), StandardCharsets.UTF_8));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                feed += inputLine;
            }

            in.close();

            // System.err.println(feed);

            return feed;
        } catch (MalformedURLException ex) {
            Logger.getLogger(OtherAlertRepository.class.getName())
                    .log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OtherAlertRepository.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        return "";
    }


    /**
     * Parse the content of the feed using Jackson XmlMapper to automatically
     * map the feed on the class Rss. The old alerts list is replaced after all
     * alerts has been processed to reduce problems with the findAll or findById
     * methods.
     *
     * @param feed the content of the RSS feed
     */
    private void parseFeed(String feed) {
        try {

            String tmpStrs[];
            String alertId, nom, source, territoire, certitude, severite, type,
                    dateDeMiseAJour, urgence, description;
            double lng, lat;
            HashMap<String, Alert> newAlerts = new HashMap<>();
            Rss rssObject = xmlMapper.readValue(feed, Rss.class);

            for (ItemRSS item : rssObject.getChannel().getItem()) {
                // System.err.println(item.getDescription());

                /**
                 * The name is stored in item.title
                 */
                nom = item.getTitle();

                /**
                 * item.guid contains coordinates and alertId in the form of :
                 * "{url}?...&center={lng},{lat}&...#{alertId}"
                 * ex:"{url}/?context=avp&center=-73.6387202781213,45.6928705203507&zoom=10#MSP.SS.043208"
                 */
                tmpStrs = item.getGuid().split("#");
                alertId = tmpStrs[1].replaceAll("\\.", "-");
                tmpStrs = tmpStrs[0].split("center=")[1].split("&")[0]
                        .split(",");
                lng = Double.parseDouble(tmpStrs[0]);
                lat = Double.parseDouble(tmpStrs[1]);

                /**
                 * descriptions contains all other parameters in the form of key
                 * value pairs: <b>{key}</b> : {value} separated by "<br/>
                 * ".
                 */
                tmpStrs = item.getDescription().split("<br/>");
                source = tmpStrs[0].split(":")[1].trim();
                type = tmpStrs[1].split(":")[1].trim();
                dateDeMiseAJour = tmpStrs[2].split(":")[1].trim();
                description = tmpStrs[3].split(":")[1].trim();
                severite = tmpStrs[4].split(":")[1].trim();
                territoire = tmpStrs[5].split(":")[1].trim();
                certitude = tmpStrs[6].split(":")[1].trim();
                urgence = tmpStrs[7].split(":")[1].trim();

                newAlerts.put(alertId,
                        new Alert(alertId, nom, source, territoire, certitude,
                                severite, type, dateDeMiseAJour, urgence,
                                description, lng, lat));
            }

            alerts = newAlerts;

        } catch (MismatchedInputException ex) {
            Logger.getLogger(OtherAlertRepository.class.getName())
                    .log(Level.WARNING, null, ex);
        } catch (JsonParseException ex) {
            Logger.getLogger(OtherAlertRepository.class.getName())
                    .log(Level.WARNING, null, ex);
        } catch (JsonMappingException ex) {
            Logger.getLogger(OtherAlertRepository.class.getName())
                    .log(Level.WARNING, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OtherAlertRepository.class.getName())
                    .log(Level.WARNING, null, ex);
        }
    }

    // /**
    // * Utility method that creates a unique id from the alert name, longitude
    // * and latitude.
    // *
    // * @param stub
    // * @return
    // */
    // private String createId(String name, double lng, double lat) {
    // return (name.hashCode() + "-" + lng + "-" + lat).replaceAll("\\.", "");
    // }

}
