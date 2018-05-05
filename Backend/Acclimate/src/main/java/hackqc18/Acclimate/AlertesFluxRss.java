import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.io.*;

/**
 *
 *
 */
public class AlertesFluxRss {
    private ArrayList<String> alerte = new ArrayList<String>();
    private ArrayList<String> nom = new ArrayList<String>();
    private ArrayList<String> geom = new ArrayList<String>();
    private ArrayList<Double> coordX = new ArrayList<Double>();
    private ArrayList<Double> coordY = new ArrayList<Double>();
    private ArrayList<String> source = new ArrayList<String>();
    private ArrayList<String> type = new ArrayList<String>();
    private ArrayList<String> dateDeMiseAJour = new ArrayList<String>();
    private ArrayList<String> description = new ArrayList<String>();
    private ArrayList<String> severite = new ArrayList<String>();
    private ArrayList<String> territoire = new ArrayList<String>();
    private ArrayList<String> certitude = new ArrayList<String>();
    private ArrayList<String> urgence = new ArrayList<String>();
    private ArrayList<Integer> indexes;
    
    public AlertesFluxRss() throws Exception{
        this.alerte = alerte;
        this.nom = nom;
        this.geom = geom;
        this.coordX = coordX;
        this.coordY = coordY;
        this.source = source;
        this.type = type;
        this.dateDeMiseAJour = dateDeMiseAJour;
        this.description = description;
        this.severite = severite;
        this.territoire = territoire;
        this.certitude = certitude;
        this.urgence = urgence;
        
        this.createAlerts();
        this.getAlertInfos();
        this.getCoords();
        this.alertsInBox(-70, -60, 40, 58);
        
    }
    
    
    public void createAlerts() throws Exception {
        String contRss = getRssFeed();
        this.alerte = getInfos("<item>", 5, "</item>", contRss);
    }
    
    
    public void getCoords() {
        for(int i = 0; i < geom.size(); i++) {
            String coordos = geom.get(i) + "<>";
            this.coordX.add(Double.parseDouble(("-" + getInfosStr("-", 0, ",", coordos))));
            this.coordY.add(Double.parseDouble((getInfosStr(",", coordX.get(i).toString().length(), "<>", coordos))));
        }
    }
    
    
    public void getAlertInfos() {
        for (int i = 0; i < alerte.size(); i++) {
            this.nom.add(getInfosStr("<title>", 0, "</title>", alerte.get(i)));
            String coords = getInfosStr("<b>Urgence</b>", 0, "amp;zoom", alerte.get(i));
            this.geom.add(getInfosStr("center=", 4, "&", coords));
            String auteur = getInfosStr("Auteur", 150, "br/>", alerte.get(i));
            this.source.add(getInfosStr(":", 5, "<", auteur));
            this.type.add(getInfosStr("<b>Type</b> :", 1, "<br/>", alerte.get(i)));
            this.dateDeMiseAJour.add(getInfosStr("<b>Date de mise à jour</b> :", 1, "<br/>", alerte.get(i)));
            this.description.add(getInfosStr("<b>Description</b> :", 1, "<br/>", alerte.get(i)));
            this.severite.add(getInfosStr("<b>Sévérite</b> :", 1, "<br/>", alerte.get(i)));
            this.territoire.add(getInfosStr("<b>Secteur</b> :", 1, "<br/>", alerte.get(i)));
            this.certitude.add(getInfosStr("<b>Certitude</b> :", 1, "<br/>", alerte.get(i)));
            this.urgence.add(getInfosStr("<b>Urgence</b> :", 1, "<br/>", alerte.get(i)));
        }
    }
    
    
    public static String getRssFeed() throws Exception {
        String rss = "";
        URL rssSource = new URL("https://geoegl.msp.gouv.qc.ca/avp/rss/");
        URLConnection rssSrc = rssSource.openConnection();
        BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                        rssSrc.getInputStream(), StandardCharsets.UTF_8));
        String inputLine;
        
        while((inputLine = in.readLine()) != null)
            rss += inputLine;
            
        in.close();
        
        String rssCleaned = rss.replaceAll("&lt;", "<").replaceAll("&gt;", ">").substring(564);

        return rssCleaned;
    }
    
    
    public static String getInfosStr(String balise, int offset, String fin, String rss) {
        String liste = "";
        String rssText = rss;
        int ix;
        for(String word : rssText.split(fin)) {
            if (word.contains(balise)) {
                ix = rssText.indexOf(word) + balise.length();
                liste += (rssText.substring(ix + offset, rssText.indexOf(fin, ix + 1)));
            }
        }
        return liste;
    }
    
    
    public static ArrayList <String> getInfos(String balise, int offset, String fin, String rss) {
        ArrayList <String> liste = new ArrayList <String>();
        String rssText = rss;
        int ix;
        for(String word : rssText.split(fin)) {
            if (word.contains(balise)) {
                ix = rssText.indexOf(word) + balise.length();
                liste.add(rssText.substring(ix + offset, rssText.indexOf(fin, ix + 1)));
                //System.out.println(rssText.substring(ix + offset, rssText.indexOf(fin, ix + 1)));
            }
        }
        return liste;
    }
    
    public ArrayList<Integer> alertsInBox(double minX, double maxX, double minY, double maxY) {
        indexes = new ArrayList<Integer>();
        
        if (minX <= -84 && maxX >= -58 && minY > 40 && maxY < 63) {
            // RENVOYER TOUT
        }
        
        for (int i = 0; i < alerte.size(); i++) {
            System.out.println(coordX.get(i) > minX && coordX.get(i) < maxX);
            if (coordX.get(i) > minX && coordX.get(i) < maxX) {
                System.out.println(coordX.get(i) > minX && coordX.get(i) < maxX);
                if (coordY.get(i) > minY && coordY.get(i) < maxY) {
                    System.out.println("CoordX" + coordX.get(i));
                    System.out.println("CoordY" + coordY.get(i));
                    indexes.add(i);
                    System.out.println(i);
                }
            }
        }
        return indexes;
    }
}
