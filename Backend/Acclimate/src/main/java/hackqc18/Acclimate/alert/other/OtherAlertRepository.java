package hackqc18.Acclimate.alert.other;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hackqc18.Acclimate.alert.Alert;

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

    /**
     * Method that mocks the findById method of the CrudRepository interface.
     * 
     * @param id
     *            the id of the alert of interest
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
     * Utility method used to fetch alerts from the live stream at periodic
     * intervals.
     */
    // The @Scheduled annotation informs Spring to create a
    // task with the annotated method and to run it in a separate
    // thread at the given fixed rate. @Schedule only works if the
    // @EnableScheduling annotation has been set in the application class.
    @Scheduled(fixedRate = 1000 * fetchingDelay)
    private void fetchRssFeedTask() {
        String contRss = getRssFeed();
        ArrayList<String> alertePrg = getInfos("<item>", 5, "</item>", contRss);

        String nom, source, territoire, certitude, severite, type;
        String dateDeMiseAJour, urgence, description, geom;
        HashMap<String, Alert> newAlerts = new HashMap<>();
        for (int i = 0; i < alertePrg.size(); i++) {
            nom = getInfosStr("<title>", 0, "</title>", alertePrg.get(i));
            String coords = getInfosStr("<b>Urgence</b>", 0, "amp;zoom",
                            alertePrg.get(i));
            geom = getInfosStr("center=", 4, "&", coords);
            String auteur = getInfosStr("Auteur", 150, "br/>",
                            alertePrg.get(i));
            source = getInfosStr(":", 5, "<", auteur);
            type = getInfosStr("<b>Type</b> :", 1, "<br/>", alertePrg.get(i));
            dateDeMiseAJour = getInfosStr("<b>Date de mise à jour</b> :", 1,
                            "<br/>", alertePrg.get(i));
            description = getInfosStr("<b>Description</b> :", 1, "<br/>",
                            alertePrg.get(i));
            severite = getInfosStr("<b>Sévérite</b> :", 1, "<br/>",
                            alertePrg.get(i));
            territoire = getInfosStr("<b>Secteur</b> :", 1, "<br/>",
                            alertePrg.get(i));
            certitude = getInfosStr("<b>Certitude</b> :", 1, "<br/>",
                            alertePrg.get(i));
            urgence = getInfosStr("<b>Urgence</b> :", 1, "<br/>",
                            alertePrg.get(i));
            String coordos = geom + "<>";
            double lng = Double.parseDouble(
                            ("-" + getInfosStr("-", 0, ",", coordos)));
            double lat = Double.parseDouble((getInfosStr(",",
                            (lng + "").length(), "<>", coordos)));

            String alertId = createId(nom, lng, lat);
            newAlerts.put(alertId, new Alert(alertId, nom, source, territoire,
                            certitude, severite, type, dateDeMiseAJour, urgence,
                            description, lng, lat));
        }
        alerts = newAlerts;
    }

    /**
     * Utility method that actually fetch the RSS feed.
     * 
     * @return the feed in XML format
     */
    private String getRssFeed() {
        try {
            String rss = "";
            URL rssSource = new URL(rssURL);
            URLConnection rssSrc = rssSource.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                            rssSrc.getInputStream(), StandardCharsets.UTF_8));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                rss += inputLine;
            }

            in.close();

//            if (!(new File("AlertRSS.xml")).exists()) {
//                FileWriter fw = new FileWriter("AlertRSS.xml");
//                BufferedWriter writer = new BufferedWriter(fw);
//                writer.append(rss);
//                writer.close();
//            }
//            System.err.println(rss);
            String rssCleaned = rss.replaceAll("&lt;", "<")
                            .replaceAll("&gt;", ">").substring(564);

            return rssCleaned;
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
     * Utility method that splits the RSS stream string into alerts.
     * 
     * @param balise
     * @param offset
     * @param fin
     * @param rss
     * @return an list of alerts in XML format
     */
    private ArrayList<String> getInfos(String balise, int offset, String fin,
                    String rss) {
        ArrayList<String> liste = new ArrayList<>();
        String rssText = rss;
        int ix;
        for (String word : rssText.split(fin)) {
            if (word.contains(balise)) {
                ix = rssText.indexOf(word) + balise.length();
                liste.add(rssText.substring(ix + offset,
                                rssText.indexOf(fin, ix + 1)));
            }
        }
        return liste;
    }

    /**
     * Utility method that extract a specific value for a given key.
     * 
     * @param balise
     * @param offset
     * @param fin
     * @param rss
     * @return
     */
    private String getInfosStr(String balise, int offset, String fin,
                    String rss) {
        String liste = "";
        String rssText = rss;
        int ix;
        for (String word : rssText.split(fin)) {
            if (word.contains(balise)) {
                ix = rssText.indexOf(word) + balise.length();
                liste += (rssText.substring(ix + offset,
                                rssText.indexOf(fin, ix + 1)));
            }
        }
        return liste;
    }

    /**
     * Utility method that creates a unique id from the alert name, longitude
     * and latitude.
     * 
     * @param stub
     * @return
     */
    private String createId(String name, double lng, double lat) {
        return (name.hashCode() + "-" + lng + "-" + lat).replaceAll("\\.", "");
    }

}
