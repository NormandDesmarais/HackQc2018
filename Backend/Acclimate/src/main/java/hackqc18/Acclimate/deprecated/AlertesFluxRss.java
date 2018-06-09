package hackqc18.Acclimate.deprecated;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
//import java.text.SimpleDateFormat;
//import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 *
 */
@Component
@Deprecated
public class AlertesFluxRss {

    private static ArrayList<Alerte> alertes = new ArrayList<>();

//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");


    @Scheduled(fixedRate = 1000 * 180) // scheduled to parse every 3 min
//    @Scheduled(fixedRate = 1000 * 10) // scheduled to parse every 10 sec
    private void parseFeed() {
//        Logger.getLogger(AlertesFluxRss.class.getName()).log(Level.SEVERE, "The time is now {0}", dateFormat.format(new Date()));

        String contRss = getRssFeed();
        ArrayList<String> alertePrg = getInfos("<item>", 5, "</item>", contRss);

        String nom, source, territoire, certitude, severite, type;
        String dateDeMiseAJour, urgence, description, geom;
        ArrayList<Alerte> newAlerts = new ArrayList<>();
        for (int i = 0; i < alertePrg.size(); i++) {
            nom = getInfosStr("<title>", 0, "</title>", alertePrg.get(i));
            String coords = getInfosStr("<b>Urgence</b>", 0, "amp;zoom", alertePrg.get(i));
            geom = getInfosStr("center=", 4, "&", coords);
            String auteur = getInfosStr("Auteur", 150, "br/>", alertePrg.get(i));
            source = getInfosStr(":", 5, "<", auteur);
            type = getInfosStr("<b>Type</b> :", 1, "<br/>", alertePrg.get(i));
            dateDeMiseAJour = getInfosStr("<b>Date de mise à jour</b> :", 1,
                    "<br/>", alertePrg.get(i));
            description = getInfosStr("<b>Description</b> :", 1, "<br/>", alertePrg.get(i));
            severite = getInfosStr("<b>Sévérite</b> :", 1, "<br/>", alertePrg.get(i));
            territoire = getInfosStr("<b>Secteur</b> :", 1, "<br/>", alertePrg.get(i));
            certitude = getInfosStr("<b>Certitude</b> :", 1, "<br/>", alertePrg.get(i));
            urgence = getInfosStr("<b>Urgence</b> :", 1, "<br/>", alertePrg.get(i));
            String coordos = geom + "<>";
            double lng = Double.parseDouble(("-" + getInfosStr("-", 0, ",", coordos)));
            double lat = Double.parseDouble((getInfosStr(",", (lng + "").length(),
                    "<>", coordos)));

            newAlerts.add(new Alerte(nom, source, territoire,
                    certitude, severite, type, dateDeMiseAJour, "00000", urgence,
                    description, lng, lat));
        }
        alertes = newAlerts;
    }

    private String getRssFeed() {
        try {
            String rss = "";
            URL rssSource = new URL("https://geoegl.msp.gouv.qc.ca/avp/rss/");
            URLConnection rssSrc = rssSource.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            rssSrc.getInputStream(), StandardCharsets.UTF_8));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                rss += inputLine;
            }

            in.close();

            String rssCleaned = rss.replaceAll("&lt;", "<").replaceAll("&gt;", ">").substring(564);

            return rssCleaned;
        } catch (MalformedURLException ex) {
            Logger.getLogger(AlertesFluxRss.class.getName()).log(Level.WARNING, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AlertesFluxRss.class.getName()).log(Level.WARNING, null, ex);
        }
        return "";
    }

    private String getInfosStr(String balise, int offset, String fin, String rss) {
        String liste = "";
        String rssText = rss;
        int ix;
        for (String word : rssText.split(fin)) {
            if (word.contains(balise)) {
                ix = rssText.indexOf(word) + balise.length();
                liste += (rssText.substring(ix + offset, rssText.indexOf(fin, ix + 1)));
            }
        }
        return liste;
    }

    private ArrayList<String> getInfos(String balise, int offset, String fin, String rss) {
        ArrayList<String> liste = new ArrayList<>();
        String rssText = rss;
        int ix;
        for (String word : rssText.split(fin)) {
            if (word.contains(balise)) {
                ix = rssText.indexOf(word) + balise.length();
                liste.add(rssText.substring(ix + offset, rssText.indexOf(fin, ix + 1)));
                //System.out.println(rssText.substring(ix + offset, rssText.indexOf(fin, ix + 1)));
            }
        }
        return liste;
    }

    public static ArrayList<Alerte> getAlertes() {
        return alertes;
    }
}
