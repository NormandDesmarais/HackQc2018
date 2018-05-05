import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.io.*;

/**
 *
 *
 */
public class AlertesFluxRss {
    
    private String contRss;
    private ArrayList<String> alerte = new ArrayList<String>();
    private ArrayList<String> nom = new ArrayList<String>();
    private ArrayList<String> geom = new ArrayList<String>();
    private ArrayList<String> source = new ArrayList<String>();
    private ArrayList<String> type = new ArrayList<String>();
    private ArrayList<String> dateDeMiseAJour = new ArrayList<String>();
    private ArrayList<String> description = new ArrayList<String>();
    private ArrayList<String> severite = new ArrayList<String>();
    private ArrayList<String> territoire = new ArrayList<String>();
    private ArrayList<String> certitude = new ArrayList<String>();
    private ArrayList<String> urgence = new ArrayList<String>();
    
    public AlertesFluxRss() throws Exception{
        this.alerte = alerte;
        this.nom = nom;
        this.geom = geom;
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
    }
    
    public String getContRss() {
        return contRss;
    }

    public void setContRss(String contRss) {
        this.contRss = contRss;
    }

    public ArrayList<String> getAlerte() {
        return alerte;
    }

    public void setAlerte(ArrayList<String> alerte) {
        this.alerte = alerte;
    }

    public ArrayList<String> getNom() {
        return nom;
    }

    public void setNom(ArrayList<String> nom) {
        this.nom = nom;
    }

    public ArrayList<String> getGeom() {
        return geom;
    }

    public void setGeom(ArrayList<String> geom) {
        this.geom = geom;
    }

    public ArrayList<String> getSource() {
        return source;
    }

    public void setSource(ArrayList<String> source) {
        this.source = source;
    }

    public ArrayList<String> getType() {
        return type;
    }

    public void setType(ArrayList<String> type) {
        this.type = type;
    }

    public ArrayList<String> getDateDeMiseAJour() {
        return dateDeMiseAJour;
    }

    public void setDateDeMiseAJour(ArrayList<String> dateDeMiseAJour) {
        this.dateDeMiseAJour = dateDeMiseAJour;
    }

    public ArrayList<String> getDescription() {
        return description;
    }

    public void setDescription(ArrayList<String> description) {
        this.description = description;
    }

    public ArrayList<String> getSeverite() {
        return severite;
    }

    public void setSeverite(ArrayList<String> severite) {
        this.severite = severite;
    }

    public ArrayList<String> getTerritoire() {
        return territoire;
    }

    public void setTerritoire(ArrayList<String> territoire) {
        this.territoire = territoire;
    }

    public ArrayList<String> getCertitude() {
        return certitude;
    }

    public void setCertitude(ArrayList<String> certitude) {
        this.certitude = certitude;
    }

    public ArrayList<String> getUrgence() {
        return urgence;
    }

    public void setUrgence(ArrayList<String> urgence) {
        this.urgence = urgence;
    }

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        AlertesFluxRss fluxRss = new AlertesFluxRss();
    }
    
    
    public void createAlerts() throws Exception {
        String contRss = getRssFeed();
        this.alerte = getInfos("<item>", 5, "</item>", contRss);
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
    
    
    private static String getRssFeed() throws Exception {
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
        
        String rssCleaned = rss.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
        
        return rssCleaned;
    }
    
    
    private static String getInfosStr(String balise, int offset, String fin, String rss) {
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
    
    
    private static ArrayList <String> getInfos(String balise, int offset, String fin, String rss) {
        ArrayList <String> liste = new ArrayList <String>();
        String rssText = rss;
        int ix;
        for(String word : rssText.split(fin)) {
            if (word.contains(balise)) {
                ix = rssText.indexOf(word) + balise.length();
                liste.add(rssText.substring(ix + offset, rssText.indexOf(fin, ix + 1)));
            }
        }
        return liste;
    }
}
